package com.instalite.instalite.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Data
public class PaginatedResultsDto<T> {
    private List<T> data;
    private int itemsPerPage;
    private long itemCount;
    private int page;
    private int pageCount;

    public static <T> PaginatedResultsDto<T> from(Page<T> page) {
        return PaginatedResultsDto.<T>builder()
            .data(page.getContent())
            .itemsPerPage(page.getSize())
            .itemCount(page.getTotalElements())
            .page(page.getNumber())
            .pageCount(page.getTotalPages())
            .build();
    }
}
