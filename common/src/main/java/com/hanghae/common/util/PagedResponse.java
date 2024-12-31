package com.hanghae.common.util;

import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private int totalElements;
    private int startPage;
    private int endPage;

    public PagedResponse(List<T> content, int currentPage, int totalPages, int pageSize, int totalElements) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.totalElements = totalElements;

        int pageGroupSize = 5;
        int groupNumber = (currentPage - 1) / pageGroupSize;

        this.startPage = groupNumber * pageGroupSize + 1;
        this.endPage = Math.min(startPage + pageGroupSize - 1, totalPages);
    }

}
