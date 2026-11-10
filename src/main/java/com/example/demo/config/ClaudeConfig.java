package com.example.demo.config;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaudeConfig {

    /**
     * Opretter én Anthropic-klient til hele applikationen.
     * API-nøglen hentes fra property'en anthropic.api-key (som igen læser
     * miljøvariablen ANTHROPIC_API_KEY). Lav nøglen i Claude Console:
     * https://platform.claude.com/settings/keys
     */
    @Bean
    public AnthropicClient anthropicClient(@Value("${anthropic.api-key:}") String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            // Falder tilbage på SDK'ens egen opslag (ANTHROPIC_API_KEY, ant auth login profil osv.)
            return AnthropicOkHttpClient.fromEnv();
        }
        return AnthropicOkHttpClient.builder().apiKey(apiKey).build();
    }
}
