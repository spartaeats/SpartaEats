package com.sparta.sparta_eats.ai.presentation.controller;

import com.sparta.sparta_eats.ai.application.service.AiServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiServiceV1 aiService;

}
