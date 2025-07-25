package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetailDto {
    private Integer categoryId;
    private String name;
    private String slug;
    private Integer parentId;
    private String parentName;
}