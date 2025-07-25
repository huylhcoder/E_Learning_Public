package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.fpoly.dto.CategoryDetailDto;
import com.fpoly.dto.CategoryDto;
import com.fpoly.dto.CategoryRequestDto;
import com.fpoly.dto.CategoryResponseDto;
import com.fpoly.dto.CategoryTreeDTO;
import com.fpoly.entity.Category;
import com.fpoly.entity.Course;
import com.fpoly.repository.CategoryRepository;
import com.fpoly.service.CategoryService;
import com.fpoly.service.CourseService;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CourseService courseService;

//Header - DefaultLayout - Dropdown Menu
	// Hiển thị danh sách danh mục lồng nhau + số sản phẩm
	@GetMapping("/tree")
	public ResponseEntity<List<CategoryTreeDTO>> getCategoryTree() {
		return ResponseEntity.ok(categoryService.getCategoryTreeWithCounts());
	}

	// Dùng để render dropdown chọn danh mục cha trong popup
	@GetMapping("/dropdown")
	public ResponseEntity<List<CategoryTreeDTO>> getDropdownTree() {
		return ResponseEntity.ok(categoryService.getAllForDropdown());
	}
	
//Search Course
    @GetMapping("/list-category")
    public List<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }
	
//Category Manager
	// Hiển thị danh sách danh mục + danh mục cha + số lượng sản phẩm ứng với danh
	// mục
	@GetMapping("/category-manager/list-category")
	public ResponseEntity<List<CategoryResponseDto>> getAllCategoriesWithCount() {
		return ResponseEntity.ok(categoryService.getAllWithCourseCount());
	}

	// Hiển thị chi tiết danh mục + tên danh mục cha
	@GetMapping("/{id}")
	public ResponseEntity<CategoryDetailDto> getCategory(@PathVariable Integer id) {
		return ResponseEntity.ok(categoryService.getCategoryDetailById(id));
	}

	//Tạo một danh mục mới
	@PostMapping("/add-category")
	public ResponseEntity<Category> createCategory(@RequestBody CategoryRequestDto dto) {
		return ResponseEntity.ok(categoryService.createCategory(dto));
	}

	@PutMapping("/update-category/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequestDto dto) {
		return ResponseEntity.ok(categoryService.updateCategory(id, dto));
	}

//	@PostMapping
//	public ResponseEntity<Category> themDanhMucTam(@RequestBody Category a) {
//		try {
//			categoryService.themDanhMucTam(a);
//			return ResponseEntity.ok(a);
//		} catch (Exception e) {
//			return ResponseEntity.ok(a);
//		}
//
//	}

//	@PutMapping("/{id}")
//	public ResponseEntity<Category> capNhatDanhMucTam(@PathVariable("id") int id, @RequestBody Category a) {
//		Category kiemTraTonTai = categoryService.timKiemDanhMucTheoIDTam(id);
//		if (kiemTraTonTai != null) {
//			a.setCategoryId(id);
//			categoryService.capNhatDanhMucTam(a);
//			return ResponseEntity.ok(a);
//		}
//		return ResponseEntity.ok(a);
//	}

//	@DeleteMapping("/{id}")
//	public ResponseEntity<Category> xoaDanhMucTam(@PathVariable("id") int id) {
//		Category kiemTraTonTai = categoryService.timKiemDanhMucTheoIDTam(id);
//		if (kiemTraTonTai != null) {
//			categoryService.xoaDanhMucTheoIDTam(id);
//			return ResponseEntity.ok(kiemTraTonTai);
//		}
//		return ResponseEntity.ok(kiemTraTonTai);
//	}




//	@GetMapping("/cat/{categoryId}")
//    public List<Course> getCoursesByCategory(@PathVariable("categoryId") int categoryId) {
//        Category category = categoryService.timKiemDanhMucTheoIDTam(categoryId);
//        List<Course> listCourse = courseService.getCoursesByCategory(category);
//        return listCourse; 
//    }
	
//	@GetMapping("/{id}")
//	public ResponseEntity<Category> timkiemDanhMucTheoIDTam(@PathVariable("id") int id) {
//		try {
//			Category danhMuc = categoryService.timKiemDanhMucTheoIDTam(id);
//			if (danhMuc == null) {
//				throw new RuntimeException("Không tìm thấy danh mục");
//			}
//			return ResponseEntity.ok(danhMuc);
//		} catch (RuntimeException e) {
//			return ResponseEntity.notFound().build();
//		}
//	}

}
