package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//CourseDetailPage
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailSearchSectionDTO {
    private String name;
    private String description;
    private String contentDescription;
}
