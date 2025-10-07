package com.sparta.sparta_eats.ai.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    @Value("${google.api.key}")
    private String apiKey;
}
