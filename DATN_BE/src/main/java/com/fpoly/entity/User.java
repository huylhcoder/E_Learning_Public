package com.fpoly.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fpoly.security.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class User extends BaseEntity implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "users_id")
	private int userId;

	@ManyToOne
	@JoinColumn(name = "role_id")
	Role role;

	@Column(name = "password")
	private String password;

	@Column(name = "name")
	private String name;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "phone")
	private String phone;

	@Column(name = "url_profile_image")
	private String urlProfileImage;

	@Column(name = "is_active")
	private boolean isActive;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_at")
	private Date createAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_at")
	private Date updateAt;

	@Column(name = "facebook_account_id")
	private int facebookAccountId;

	@Column(name = "google_account_id")
	private String googleAccountId; //nvarchar(50)
	
	@Column(name = "login_provider")
	private String loginProvider; // "GOOGLE", "EMAIL", "FACEBOOK"


	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<SocialAccount> listSocialAccount;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<LearnerInterest> listLearnerInterest;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<LearnerSkill> listLearnerSkill;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<LearnerLevel> listLearnerLevel;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<LearnerGoal> listLearnerGoal;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<Voucher> listVoucher;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<Payment> listPayment;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<RegisteredCourse> listRegisteredCourse;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<Cart> listCart;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<CourseProgress> listCourseProgress;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<Course> listCourse;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<FavoriteCourse> listFavoriteCourse;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<Comment> listComment;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<Reply> listReply;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<LessonComplete> listLessonComplte;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<UserTestResult> UserTestResult;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<UserAnswerHistory> listUserAnswerHistory;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	List<MyVoucher> listMyVoucher;

	// Đầu tiên là lấy ra các quyền = bản role
	// Nó chạy tới chỗ này nó lấy quyền ra kiểm tra có dùng được API không
	// Trong JWT Token Filter
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
		authorityList.add(new SimpleGrantedAuthority("ROLE_" + getRole().getName()));
		return authorityList;
	}

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
//		// Phát hiện coi mình có cái Role gì
//		// authorityList.add(new SimpleGrantedAuthority("ROLE_" + getRole().getName()));
//
////		Mình cũng có thể Fake quyền để Test JWT Filter
////		authorityList.add(new SimpleGrantedAuthority("ADMIN"));
//		authorityList.add(new SimpleGrantedAuthority("USER"));
//		return authorityList;
//	}

	// Spring Security nếu để nó bằng null thì nó sẽ hiểu
	// Trường duy nhất chính là Username
	@Override
	public String getUsername() {
		return email;// Cụ thể ở đây mình dùng Email để đăng nhập
	}

	// Account này có thời lượng vô thời hạn
	// True
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
}
