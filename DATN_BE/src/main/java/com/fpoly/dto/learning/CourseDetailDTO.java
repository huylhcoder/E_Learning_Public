package com.fpoly.dto.learning;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailDTO {
    private int courseId;
    private String name;
    private String topic;
    private String description;
    private List<SectionDTO> listSection;
}
