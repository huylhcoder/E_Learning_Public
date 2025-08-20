package com.fpoly.dto;

import java.util.Date;
import java.util.List;

import com.fpoly.entity.Category;
import com.fpoly.entity.CourseLevel;
import com.fpoly.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

public class CourseDetailDTO {
	private int courseId;
	private String avatar;
	private String name;
	private String description;
	private String categoryName;
	private float price;
	List <SectionCourseDetailDTO> listSectionCourseDetailDTO;
	private float totalRate;
	private float averageRating;
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public List<SectionCourseDetailDTO> getListSectionCourseDetailDTO() {
		return listSectionCourseDetailDTO;
	}
	public void setListSectionCourseDetailDTO(List<SectionCourseDetailDTO> listSectionCourseDetailDTO) {
		this.listSectionCourseDetailDTO = listSectionCourseDetailDTO;
	}
	public float getTotalRate() {
		return totalRate;
	}
	public void setTotalRate(float totalRate) {
		this.totalRate = totalRate;
	}
	public float getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}
	
}
