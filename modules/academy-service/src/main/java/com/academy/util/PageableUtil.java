package com.academy.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableUtil {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    private PageableUtil() {}

    public static Pageable createPageable(Integer page, Integer size, String sort) {
        int pageIndex = page != null ? page : DEFAULT_PAGE;
        int pageSize  = size != null ? size : DEFAULT_SIZE;

        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            Sort.Direction dir = "desc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
            return PageRequest.of(pageIndex, pageSize, Sort.by(dir, parts[0].trim()));
        }

        return PageRequest.of(pageIndex, pageSize);
    }

    public static Pageable createPageable(Integer page, Integer size) {
        return createPageable(page, size, null);
    }
}
