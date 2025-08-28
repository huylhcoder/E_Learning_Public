package com.fpoly.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.dto.CategoryDetailDto;
import com.fpoly.dto.CategoryDto;
import com.fpoly.dto.CategoryRequestDto;
import com.fpoly.dto.CategoryResponseDto;
import com.fpoly.dto.CategoryTreeDTO;
import com.fpoly.entity.Category;
import com.fpoly.entity.Course;
import com.fpoly.repository.AnswerRepository;
import com.fpoly.repository.CategoryRepository;
import com.fpoly.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

//Category Manager Page
	// tree - Trường hợp này chỉ cho phần chi tiết khóa học
	public List<CategoryTreeDTO> getCategoryTreeWithCounts() {
		Map<Integer, Long> productCountMap = categoryRepository.countCoursesByCategory().stream()
				.collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));

		return categoryRepository.findByParentIsNull().stream().map(cat -> buildTree(cat, productCountMap))
				.collect(Collectors.toList());
	}

	// dropdown
	public List<CategoryTreeDTO> getAllForDropdown() {
		List<Category> all = categoryRepository.findAll();
		Map<Integer, Long> dummyMap = new HashMap<>();
		return all.stream().filter(cat -> cat.getParent() == null).map(cat -> buildTree(cat, dummyMap))
				.collect(Collectors.toList());
	}

	// List danh sách danh mục + số sản phẩm của từng danh mục
	public List<CategoryResponseDto> getAllWithCourseCount() {
	    List<Category> categories = categoryRepository.findAllWithParent();

	    // Map categoryId -> số lượng course
	    Map<Integer, Long> courseCountMap = categoryRepository.countCoursesByCategory().stream()
	            .collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));

	    return categories.stream().map(c -> {
	        CategoryResponseDto dto = new CategoryResponseDto();
	        dto.setCategoryId(c.getCategoryId());
	        dto.setName(c.getName());
	        dto.setSlug(c.getSlug());

	        // set parent object (chỉ map thông tin cơ bản tránh vòng lặp)
	        if (c.getParent() != null) {
	            CategoryResponseDto parentDto = new CategoryResponseDto();
	            parentDto.setCategoryId(c.getParent().getCategoryId());
	            parentDto.setName(c.getParent().getName());
	            parentDto.setSlug(c.getParent().getSlug());
	            parentDto.setParent(null); // không set tiếp để tránh đệ quy vô hạn
	            parentDto.setCourseCount(0); // có thể bỏ qua
	            parentDto.setChildrenCount(0); // có thể bỏ qua
	            dto.setParent(parentDto);
	        }

	        // set course count
	        dto.setCourseCount(courseCountMap.getOrDefault(c.getCategoryId(), 0L).intValue());

	        // set children count
	        dto.setChildrenCount(c.getChildren() != null ? c.getChildren().size() : 0);

	        return dto;
	    }).collect(Collectors.toList());
	}


	// Chi tiết danh muc khóa học + Tên danh mục cha
	public CategoryDetailDto getCategoryDetailById(Integer categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		CategoryDetailDto categoryDetail = new CategoryDetailDto();
		categoryDetail.setCategoryId(category.getCategoryId());
		categoryDetail.setName(category.getName());
		categoryDetail.setSlug(category.getSlug());

		if (category.getParent() != null) {
			categoryDetail.setParentId(category.getParent().getCategoryId());
			categoryDetail.setParentName(category.getParent().getName());
		}

		return categoryDetail;
	}

	// Tạo một Category mới với name, slug, parentId (nếu có)
	public Category createCategory(CategoryRequestDto dto) {
		Category category = new Category();
		category.setName(dto.getName());
		category.setSlug(dto.getSlug());

		if (dto.getParentId() != null && dto.getParentId() != 0) {
			Category parent = categoryRepository.findById(dto.getParentId())
					.orElseThrow(() -> new RuntimeException("Parent category not found"));
			category.setParent(parent);
		}

		return categoryRepository.save(category);
	}

	public Category updateCategory(Integer categoryId, CategoryRequestDto dto) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		// 1. Không cho chọn chính nó làm cha
		if (dto.getParentId() != null && dto.getParentId().equals(categoryId)) {
			throw new RuntimeException("A category cannot be its own parent.");
		}

		// 2. Không cho chọn con/cháu làm cha
		if (dto.getParentId() != null && isDescendant(category, dto.getParentId())) {
			throw new RuntimeException("Cannot set a descendant as parent.");
		}

		// 3. Gán parent nếu hợp lệ
		if (dto.getParentId() != null) {
			Category parent = categoryRepository.findById(dto.getParentId())
					.orElseThrow(() -> new RuntimeException("Parent category not found"));
			category.setParent(parent);
		} else {
			category.setParent(null); // bỏ parent nếu không chọn
		}

		// 4. Cập nhật tên + slug
		category.setName(dto.getName());
		category.setSlug(dto.getSlug());

		return categoryRepository.save(category);
	}
	
	//Viết hàm mapper Entity → DTO
	public CategoryResponseDto toDto(Category entity) {
	    if (entity == null) return null;

	    CategoryResponseDto dto = new CategoryResponseDto();
	    dto.setCategoryId(entity.getCategoryId());
	    dto.setName(entity.getName());
	    dto.setSlug(entity.getSlug());

	    // Đếm course gắn với category
	    dto.setCourseCount(entity.getCourseCategories() != null ? entity.getCourseCategories().size() : 0);

	    // Đếm children
	    dto.setChildrenCount(entity.getChildren() != null ? entity.getChildren().size() : 0);

	    // Map parent nhưng chỉ lấy 1 cấp, tránh vòng lặp
	    if (entity.getParent() != null) {
	        CategoryResponseDto parentDto = new CategoryResponseDto();
	        parentDto.setCategoryId(entity.getParent().getCategoryId());
	        parentDto.setName(entity.getParent().getName());
	        parentDto.setSlug(entity.getParent().getSlug());
	        dto.setParent(parentDto);
	    }

	    return dto;
	}


	/**
	 * Kiểm tra xem category con/cháu có phải là cha đang được chọn không (tức là
	 * parentId có phải là con/cháu của danh mục hiện tại không)
	 */
	private boolean isDescendant(Category current, Integer parentId) {
		List<Category> stack = new ArrayList<>(current.getChildren());
		while (!stack.isEmpty()) {
			Category child = stack.remove(0);
			if (child.getCategoryId().equals(parentId))
				return true;
			stack.addAll(child.getChildren());
		}
		return false;
	}

//Search Course - Filtter
	// Show list for choise category
	public List<CategoryDto> getCategories() {
		List<Category> categories = categoryRepository.findAll();

		return categories.stream()
				.map(category -> new CategoryDto(category.getCategoryId(), category.getName(), category.getSlug()))
				.collect(Collectors.toList());
	}

	public Category themDanhMucTam(Category a) {
		return categoryRepository.save(a);
	}

	public Category capNhatDanhMucTam(Category a) {
		return categoryRepository.save(a);
	}

	public Category timKiemDanhMucTheoIDTam(int id) {
		return categoryRepository.findByCategoryId(id);
	}

	public void xoaDanhMucTheoIDTam(int id) {
		categoryRepository.deleteById(id);
	}

	private CategoryTreeDTO buildTree(Category category, Map<Integer, Long> countMap) {
		CategoryTreeDTO dto = new CategoryTreeDTO();
		dto.setCategoryId(category.getCategoryId());
		dto.setName(category.getName());
		dto.setSlug(category.getSlug());
		dto.setParentId(category.getParent() != null ? category.getParent().getCategoryId() : null);
		dto.setProductCount(countMap.getOrDefault(category.getCategoryId(), 0L).intValue());
		dto.setChildren(
				category.getChildren().stream().map(child -> buildTree(child, countMap)).collect(Collectors.toList()));
		return dto;
	}

}
