package com.learnify.plans.common.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ResponseListDTO<T> {
    private Long totalDocuments;
    private Integer totalPages;
    private Integer currentPage;
    private List<T> data;
}
