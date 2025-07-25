package com.fpoly.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDto {
    private String name;
    private String slug;
    private String description;
    private String avatar;
    private float price;
    private Integer usersId;
    private Integer courseLevelId;
    private List<Integer> categoryIds;
}
