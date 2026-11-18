package com.finapp.card.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "cards") // This tells Spring Boot to automatically look in your configuration files (like application.properties or Config Server)
// It binds those external properties prefixed with "cards" directly to the fields in this class.
public record CardsContactInfoDto(String message, Map<String, String> contactDetails, List<String> onCallSupport) {
    // A record is a compact way to create immutable data classes
    // Because it is a record, the fields (message, contactDetails, onCallSupport) are final.
    // They are set once when Spring creates the bean and cannot be changed during runtime, which is a best practice for configuration properties.
}