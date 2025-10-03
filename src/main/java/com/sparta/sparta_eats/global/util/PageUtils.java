package com.sparta.sparta_eats.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtils {

  private static final int MAX_SIZE = 100;

  private PageUtils() {
    // 유틸리티 클래스는 인스턴스 생성 방지
  }

  public static Pageable of(int page, int size, Sort sort) {
    int p = Math.max(page, 1) - 1;  // 1부터 시작하는 페이지를 0부터 시작으로 변환
    int s = Math.min(Math.max(size, 1), MAX_SIZE);  // 최소 1, 최대 100으로 제한
    return PageRequest.of(p, s, sort);
  }
}