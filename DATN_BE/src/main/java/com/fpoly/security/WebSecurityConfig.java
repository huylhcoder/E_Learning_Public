package com.fpoly.security;

import static org.springframework.http.HttpMethod.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fpoly.entity.Role;

//Cái này là cái thứ 2 phải có sau cái Security Config
//Nó yêu cầu quyền từ các Request gửi tới
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
	@Autowired
	private JwtTokenFilter jwtTokenFilter;
	@Value("${api.prefix}")
	private String apiPrefix;

	// Bảo vệ khi request gửi đến
	// Có đã đủ giấy tờ chưa
	// Ông có quyền gì để vào
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(requests -> {

					requests
							// Mấy cái request không cần token nên để qua bên JWTTokenFilter
							// Do khi nhận request nó qua bên đó trước
							.requestMatchers(GET,
									// Category
									String.format("%s/category/list-category", apiPrefix),
									String.format("%s/category/tree/**", apiPrefix),
									// Course
									String.format("%s/course/fun-fact", apiPrefix),
									String.format("%s/course/top-registered-course", apiPrefix),
									String.format("%s/course/top-rated", apiPrefix),
									String.format("%s/course/search", apiPrefix),
									String.format("%s/course/suggestions", apiPrefix),
									String.format("%s/course/course-detail/**", apiPrefix),
									// Course Level
									String.format("%s/course-level", apiPrefix),
									String.format("%s/course-level/list-course-level", apiPrefix),
									// Payment
									String.format("%s/vnpayreturn/**", apiPrefix),
									String.format("%s/vnpayreturn", apiPrefix),
									// Upload - Download file
									String.format("%s/upload-file/download-video-on-server", apiPrefix),
									String.format("%s/upload-file/download-video", apiPrefix),
									// Role
									String.format("%s/roles**", apiPrefix))
							.permitAll()
							// Post Public
							.requestMatchers(POST,
									// Auth 
									String.format("%s/auth/register", apiPrefix),
									String.format("%s/auth/check-user", apiPrefix),
									String.format("%s/auth/send-verification-code", apiPrefix),
									String.format("%s/auth/login", apiPrefix),
									String.format("%s/auth/introspect", apiPrefix),
									// Course
									String.format("%s/course-manager/**", apiPrefix),
									// Section
									String.format("%s/section-manager/**", apiPrefix),
									// Category
									String.format("%s/category/add-category/**", apiPrefix))
							.permitAll()

							// Put Public
							.requestMatchers(PUT, 
									String.format("%s/course-manager-detail/**", apiPrefix),
									String.format("%s/category/update-category/**", apiPrefix))
							.permitAll()
							// Post With Role Admin
							.requestMatchers(GET, String.format("%s/category-manager/tree", apiPrefix)).hasAnyRole(Role.ADMIN)
							.requestMatchers(GET, String.format("%s/category-manager/list-category", apiPrefix)).hasAnyRole(Role.ADMIN)
							// Post With Role Admin
							.requestMatchers(POST, String.format("%s/category-manager/**", apiPrefix)).hasAnyRole(Role.ADMIN)
							// Put With Role Admin
							.requestMatchers(PUT, String.format("%s/category-manager/**", apiPrefix)).hasAnyRole(Role.ADMIN)
							// Delete With Role Admin
							.requestMatchers(DELETE, String.format("%s/category-manager/**", apiPrefix))
							.hasAnyRole(Role.ADMIN)
							// Tất cả request còn lại phải xác thực
							.anyRequest().authenticated();
					// .anyRequest().permitAll(); //Cho phép tất cả Request được thông qua

				}).csrf(AbstractHttpConfigurer::disable);
//		http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
//			@Override
//			public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
//				CorsConfiguration configuration = new CorsConfiguration();
//				configuration.setAllowedOrigins(List.of("*"));
//				configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//				configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
//				configuration.setExposedHeaders(List.of("x-auth-token"));
//				UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//				source.registerCorsConfiguration("/**", configuration);
//				httpSecurityCorsConfigurer.configurationSource(source);
//			}
//		});
		
		http.cors(cors -> {
		    CorsConfiguration config = new CorsConfiguration();
		    config.setAllowedOrigins(List.of("*"));
		    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		    config.setAllowedHeaders(List.of("authorization", "content-type", "x-auth-token"));
		    config.setExposedHeaders(List.of("x-auth-token"));

		    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		    source.registerCorsConfiguration("/**", config);

		    cors.configurationSource(source);
		});


		return http.build();
	}
}
