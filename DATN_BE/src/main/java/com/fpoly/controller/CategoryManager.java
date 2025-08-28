package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.dto.CategoryDetailDto;
import com.fpoly.dto.CategoryRequestDto;
import com.fpoly.dto.CategoryResponseDto;
import com.fpoly.dto.CategoryTreeDTO;
import com.fpoly.entity.Category;
import com.fpoly.service.CategoryService;
import com.fpoly.service.CourseService;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/category-manager")
public class CategoryManager {
	@Autowired
	private CategoryService categoryService;

	// Hiển thị danh sách danh mục + danh mục cha + số lượng sản phẩm ứng với danh
	// mục
	@GetMapping("/list-category")
	public ResponseEntity<List<CategoryResponseDto>> getAllCategoriesWithCount(
			@RequestHeader("Authorization") String token) {
		return ResponseEntity.ok(categoryService.getAllWithCourseCount());
	}

	// Hiển thị danh mục theo cây: Có danh mục cha
	@GetMapping("/tree")
	public ResponseEntity<List<CategoryTreeDTO>> getCategoryTree() {
		return ResponseEntity.ok(categoryService.getCategoryTreeWithCounts());
	}

	// Hiển thị chi tiết danh mục + tên danh mục cha
	@GetMapping("/{id}")
	public ResponseEntity<CategoryDetailDto> getCategory(@PathVariable Integer id) {
		return ResponseEntity.ok(categoryService.getCategoryDetailById(id));
	}
	
	@PostMapping("/add-category")
	public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto dto) {
	    Category category = categoryService.createCategory(dto);
	    return ResponseEntity.ok(categoryService.toDto(category));
	}

	@PutMapping("/update-category/{id}")
	public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequestDto dto) {
	    Category category = categoryService.updateCategory(id, dto);
	    return ResponseEntity.ok(categoryService.toDto(category));
	}

}
