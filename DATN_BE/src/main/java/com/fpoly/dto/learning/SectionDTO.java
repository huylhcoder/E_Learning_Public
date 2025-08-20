package com.fpoly.dto.learning;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDTO {
    private int sectionId;
    private String name;
    private List<LessonDTO> listLesson;
    private List<TestDTO> listTest;
}