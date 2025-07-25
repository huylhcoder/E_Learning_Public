package com.fpoly.repository;

import com.fpoly.entity.Course;
import com.fpoly.repository.CourseRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseRepositoryCustomImpl implements CourseRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<Course> searchCourses(String categorySlug, String courseName, Boolean free, Float minPrice,
			Float maxPrice, Integer ratedStar, Integer levelId, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> cq = cb.createQuery(Course.class);
		Root<Course> root = cq.from(Course.class);
		List<Predicate> predicates = new ArrayList<>();

		// Chỉ lấy khóa học công khai
		predicates.add(cb.equal(root.get("status"), 1));

		// Lọc theo categorySlug (nếu có)
		if (categorySlug != null && !categorySlug.isEmpty()) {
			Join<Object, Object> joinCat = root.join("courseCategories").join("category");
			predicates.add(cb.equal(joinCat.get("slug"), categorySlug));
		}

		// Lọc theo tên khóa học
		if (courseName != null && !courseName.isEmpty()) {
			predicates.add(cb.like(cb.lower(root.get("name")), "%" + courseName.toLowerCase() + "%"));
		}

		// Lọc miễn phí
		if (free != null && free) {
			predicates.add(cb.equal(root.get("price"), 0));
		} else {
			// Lọc theo khoảng giá
			if (minPrice != null) {
				predicates.add(cb.ge(root.get("price"), minPrice));
			}
			if (maxPrice != null) {
				predicates.add(cb.le(root.get("price"), maxPrice));
			}
		}

		// Lọc theo số sao
		if (ratedStar != null) {
			predicates.add(cb.ge(root.get("averageRating"), ratedStar));
		}

		// Lọc theo level
		if (levelId != null) {
			predicates.add(cb.equal(root.get("courseLevel").get("courseLevelId"), levelId));
		}

		cq.where(predicates.toArray(new Predicate[0]));

		// Sắp xếp sẽ do Pageable xử lý

		TypedQuery<Course> query = entityManager.createQuery(cq);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Lấy tổng số bản ghi
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Course> countRoot = countQuery.from(Course.class);
		List<Predicate> countPredicates = new ArrayList<>();

		countPredicates.add(cb.equal(countRoot.get("status"), 1));
		if (categorySlug != null && !categorySlug.isEmpty()) {
			Join<Object, Object> joinCat = countRoot.join("courseCategories").join("category");
			countPredicates.add(cb.equal(joinCat.get("slug"), categorySlug));
		}
		if (courseName != null && !courseName.isEmpty()) {
			countPredicates.add(cb.like(cb.lower(countRoot.get("name")), "%" + courseName.toLowerCase() + "%"));
		}
		if (free != null && free) {
			countPredicates.add(cb.equal(countRoot.get("price"), 0));
		} else {
			if (minPrice != null) {
				countPredicates.add(cb.ge(countRoot.get("price"), minPrice));
			}
			if (maxPrice != null) {
				countPredicates.add(cb.le(countRoot.get("price"), maxPrice));
			}
		}
		if (ratedStar != null) {
			countPredicates.add(cb.ge(countRoot.get("averageRating"), ratedStar));
		}
		if (levelId != null) {
			countPredicates.add(cb.equal(countRoot.get("courseLevel").get("courseLevelId"), levelId));
		}

		countQuery.select(cb.count(countRoot));
		countQuery.where(countPredicates.toArray(new Predicate[0]));
		Long total = entityManager.createQuery(countQuery).getSingleResult();
		// ...existing code...

		List<Course> resultList = query.getResultList();
		return new org.springframework.data.domain.PageImpl<>(resultList, pageable, total);
	}
}