package com.fpoly.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CategoryTreeDTO {
    private Integer categoryId;
    private String name;
    private String slug;
    private Integer parentId;
    private List<CategoryTreeDTO> children = new ArrayList<>();
    private int productCount;
}
