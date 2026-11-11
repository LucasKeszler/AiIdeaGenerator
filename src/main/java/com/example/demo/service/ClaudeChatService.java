package com.example.demo.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.errors.AnthropicServiceException;
import com.anthropic.errors.AnthropicIoException;
import com.anthropic.errors.RateLimitException;
import com.anthropic.errors.UnauthorizedException;
import com.anthropic.models.beta.AnthropicBeta;
import com.anthropic.models.beta.messages.BetaCacheControlEphemeral;
import com.anthropic.models.beta.messages.BetaMessage;
import com.anthropic.models.beta.messages.BetaOutputConfig;
import com.anthropic.models.beta.messages.BetaStopReason;
import com.anthropic.models.beta.messages.BetaTextBlockParam;
import com.anthropic.models.beta.messages.MessageCreateParams;
import com.example.demo.dto.ChatMessageDto;
import com.example.demo.dto.FreeApiDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaudeChatService {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeChatService.class);

    private static final String INSTRUCTIONS = """
            Du er en kreativ idé-assistent for udviklere og studerende.
            Brugeren vil have idéer til projekter, apps eller features, som bygger på
            gratis, offentlige API'er. Du har en liste over tilgængelige API'er nedenfor.

            Når du kommer med idéer:
            - Foreslå 3 konkrete idéer, medmindre brugeren beder om noget andet.
            - Hver idé er præcis to linjer:
              linje 1: **Idéens navn** - de API'er fra listen den bruger (brug API'ens titel)
              linje 2: én sætning om hvad appen gør.
            - Kombinér gerne flere API'er i samme idé.
            - Hold dig til API'erne på listen. Hvis ingen passer, så sig det i én sætning.
            - Svar på det sprog brugeren skriver på (typisk dansk).
            - Hele svaret skal være under 120 ord. Ingen indledning, ingen overskrifter,
              ingen opsummering. Du må slutte med ét kort opfølgende spørgsmål.
            - Uddyb kun en idé, hvis brugeren beder om det - og hold det også kort.
            """;

    private final AnthropicClient client;
    private final FreePublicApisService freePublicApisService;
    private final String model;

    // Listen af API'er gemmes, så system-prompten er den samme hver gang (godt for prompt caching)
    private volatile String cachedApiCatalog;

    public ClaudeChatService(AnthropicClient client,
                             FreePublicApisService freePublicApisService,
                             @Value("${anthropic.model:claude-opus-5-5}") String model) {
        this.client = client;
        this.freePublicApisService = freePublicApisService;
        this.model = model;
    }

    public String chat(List<ChatMessageDto> history) {
        if (history == null || history.isEmpty()) {
            throw new IllegalArgumentException("Samtalen er tom");
        }

        MessageCreateParams.Builder params = MessageCreateParams.builder()
                .model(model)
                .maxTokens(16000L)
                // Low effort er nok til korte idé-svar - hurtigere og billigere (mindre tænkning)
                .outputConfig(BetaOutputConfig.builder().effort(BetaOutputConfig.Effort.LOW).build())
                // Hvis modellen afviser en forespørgsel, prøver API'en automatisk en anden model
                .addBeta(AnthropicBeta.of("server-side-fallback-2026-07-01"))
                .fallbacksDefault()
                .systemOfBetaTextBlockParams(List.of(
                        BetaTextBlockParam.builder()
                                .text(INSTRUCTIONS + "\n\nTilgængelige offentlige API'er:\n" + getApiCatalog())
                                .cacheControl(BetaCacheControlEphemeral.builder().build())
                                .build()));

        for (ChatMessageDto msg : history) {
            if (msg.content() == null || msg.content().isBlank()) continue;
            if ("assistant".equals(msg.role())) {
                params.addAssistantMessage(msg.content());
            } else {
                params.addUserMessage(msg.content());
            }
        }

        try {
            BetaMessage response = client.beta().messages().create(params.build());

            if (response.stopReason().map(r -> r.equals(BetaStopReason.REFUSAL)).orElse(false)) {
                return "Beklager, det spørgsmål kan jeg ikke hjælpe med. Prøv at formulere det anderledes.";
            }

            String text = response.content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(t -> t.text())
                    .collect(Collectors.joining("\n"));

            if (response.stopReason().map(r -> r.equals(BetaStopReason.MAX_TOKENS)).orElse(false)) {
                text += "\n\n(Svaret blev afkortet.)";
            }
            return text;
        } catch (UnauthorizedException e) {
            logger.error("Ugyldig Anthropic API-nøgle", e);
            throw new ClaudeException("API-nøglen er ugyldig. Tjek ANTHROPIC_API_KEY.", e);
        } catch (RateLimitException e) {
            logger.warn("Rate limit ramt", e);
            throw new ClaudeException("Claude har for travlt lige nu. Prøv igen om lidt.", e);
        } catch (AnthropicServiceException e) {
            logger.error("Fejl fra Claude API (status {})", e.statusCode(), e);
            throw new ClaudeException("Claude API svarede med en fejl (" + e.statusCode() + ").", e);
        } catch (AnthropicIoException e) {
            logger.error("Kunne ikke forbinde til Claude API", e);
            throw new ClaudeException("Kunne ikke forbinde til Claude API.", e);
        }
    }

    private String getApiCatalog() {
        String catalog = cachedApiCatalog;
        if (catalog != null) return catalog;

        List<FreeApiDto> apis = freePublicApisService.getFreeApis();
        if (apis.isEmpty()) {
            // Gem ikke en tom liste - prøv igen næste gang
            return "(Listen kunne ikke hentes lige nu. Kom med generelle idéer til gratis offentlige API'er.)";
        }

        catalog = apis.stream()
                .map(api -> "- %s %s: %s (dokumentation: %s)".formatted(
                        nullToEmpty(api.emoji()), api.title(), nullToEmpty(api.description()),
                        nullToEmpty(api.documentation())))
                .collect(Collectors.joining("\n"));
        cachedApiCatalog = catalog;
        return catalog;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public static class ClaudeException extends RuntimeException {
        public ClaudeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
