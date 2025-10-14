package com.sparta.sparta_eats.ai.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ReviewSummaryEvent extends ApplicationEvent {
    private final List<String> reviewList;

    public ReviewSummaryEvent(Object source, List<String> reviewList) {
        super(source);
        this.reviewList = reviewList;
    }
}
