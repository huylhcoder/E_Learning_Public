package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private Integer categoryId;
    private String name;
    private String slug;

    // Trả về nguyên đối tượng parent
    private CategoryResponseDto parent;

    // Số lượng course gắn với category
    private int courseCount;

    // Tổng số lượng danh mục con
    private int childrenCount;
}
