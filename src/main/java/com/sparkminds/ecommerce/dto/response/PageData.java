package com.sparkminds.ecommerce.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageData<T> {
    @Builder.Default
    private List<T> content = new ArrayList<>();
    
    private Integer currentPage;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;

}
