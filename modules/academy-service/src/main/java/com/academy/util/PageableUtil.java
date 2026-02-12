package com.academy.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for creating Pageable objects from pagination parameters
 */
public final class PageableUtil {
    
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    
    private PageableUtil() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates a Pageable object from pagination parameters
     * 
     * @param page the page number (0-indexed), null defaults to 0
     * @param size the page size, null defaults to 20
     * @param sort the sort string in format "field,direction" (e.g., "name,asc" or "id,desc")
     *             If null or invalid format, returns unsorted Pageable
     * @return Pageable object configured with the provided parameters
     */
    public static Pageable createPageable(Integer page, Integer size, String sort) {
        int pageNum = page != null ? page : DEFAULT_PAGE;
        int pageSize = size != null ? size : DEFAULT_SIZE;
        
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            if (sortParts.length == 2) {
                Sort.Direction direction = "desc".equalsIgnoreCase(sortParts[1].trim()) 
                    ? Sort.Direction.DESC 
                    : Sort.Direction.ASC;
                return PageRequest.of(pageNum, pageSize, Sort.by(direction, sortParts[0].trim()));
            }
        }
        return PageRequest.of(pageNum, pageSize);
    }
    
    /**
     * Creates a Pageable object without sorting
     * 
     * @param page the page number (0-indexed), null defaults to 0
     * @param size the page size, null defaults to 20
     * @return Pageable object configured with the provided parameters
     */
    public static Pageable createPageable(Integer page, Integer size) {
        return createPageable(page, size, null);
    }
}

