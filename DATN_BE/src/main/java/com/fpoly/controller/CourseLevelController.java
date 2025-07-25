package com.fpoly.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.dto.CourseDetailDTO;
import com.fpoly.dto.CourseDetailManagerDTO;
import com.fpoly.dto.CourseLevelDto;
import com.fpoly.entity.CourseLevel;
import com.fpoly.service.CourseLevelService;
import com.fpoly.service.CourseService;
import com.fpoly.service.LessonService;
import com.fpoly.service.SectionService;
import com.fpoly.service.UserService;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/course-level")
public class CourseLevelController {
	@Autowired
	private CourseLevelService courseLevelService;

//Search Page
	//Filter Course
	@GetMapping("/list-course-level")
    public ResponseEntity<List<CourseLevelDto>> getListCourseLevel() {
        List<CourseLevelDto> listLevel = courseLevelService.hienThiTatCaLevel();
        return ResponseEntity.ok(listLevel);
    }
	
}
