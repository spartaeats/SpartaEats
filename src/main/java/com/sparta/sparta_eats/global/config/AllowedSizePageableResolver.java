package com.sparta.sparta_eats.global.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class AllowedSizePageableResolver extends PageableHandlerMethodArgumentResolver {

    private final int defaultSize;
    private final List<Integer> allowedSizes;
    private final boolean oneIndexed;

    public AllowedSizePageableResolver(PaginationProperties props) {
        this.defaultSize = props.defaultSize();
        this.allowedSizes = props.allowedSizes();
        this.oneIndexed = props.oneIndexed();
        setOneIndexedParameters(oneIndexed); // 페이지 번호 1부터 시작할지 여부
    }

    @Override
    public Pageable resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        int size = pageable.getPageSize();
        if (!allowedSizes.contains(size)) {
            size = defaultSize;
        }

        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
