package com.fpoly.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fpoly.entity.Role;
import com.fpoly.entity.User;
import com.fpoly.exceptions.InvalidParamException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//Cái này là cái thứ 3 được tạo sau SecurityConFig, WebSecurityConFig, UserService
@Component
public class JwtTokenUtils {
	// Chúng ta cần có thời gian để hết hạn token
	// Chúng ta sẽ lưu vào một biến môi trường
	// 30 ngày = 30 * 24h * 60 phút = 1h * 60 giây = 259200
	// Lấy giá trị nó ra bằng @Value
	@Value("${jwt.expiration}")
	private int expiration; // save to an environment variable

	// Chúng ta cũng có thể khai báo cái Key trong biến môi trường
	@Value("${jwt.secretKey}")
	private String secretKey;

	// Cần một cái hàm để trả về token
	// Kiểu trả về của nó là một cái String
	// Đầu vào User => Token
	// Tránh nhầm lẫn đối tượng User đâu đó trong Spring thì mình khai báo như thế
	// này
	public String generateToken(User user) throws Exception {
		Map<String, Object> claims = new HashMap<>();
		claims.put("email", user.getEmail());
		// Vì User chỉ có 1 role (ManyToOne)
		claims.put("roles", List.of(user.getRole().getName()));

		try {
			return Jwts.builder().setClaims(claims).setSubject(user.getEmail()) // dùng email để extract
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
					.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
		} catch (Exception e) {
			throw new InvalidParamException("JWT Token Utils - Không thể tạo Token err: " + e.getMessage());
		}
	}
//	public String generateToken(com.fpoly.entity.User user) throws Exception {
//		// properties => claims
//		// Thuộc tính đưa vào thì chúng ta gọi nó là Claim
//		// Key là String và Value là object
//		// HashMap bởi vì nó có nhiều Key - Value
//		Map<String, Object> claims = new HashMap<>();
//		// Đưa thuộc tính vào Claim
//		claims.put("email", user.getEmail());
//		// Nó hay bị lỗi nên chúng ta phải bắt lỗi nó
//		try {
//
//			String token = Jwts.builder().
//			// Làm thể nào để trích xuất Claim ra (2)
//					setClaims(claims) // how to extract claims from this ?
//					.setSubject(user.getEmail())
//					// Từ số giây chúng ta sẽ tính được cái ngày
//					// 100 là đổi từ s => ms , L kiểu Long
//					.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
//					// Khi sinh ra token này thì chúng ta cần có câu hỏi bảo mật
//					// Từ cái Key đó nó mới dịch cái Token => Claim (1)
//					// Truyền thêm thuật toán Signature
//					.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
//			return token;
//		} catch (Exception e) {
//			// System.err.println("Không thể tạo JWT Token vì: " + e.getMessage());// Sau
//			// này có thể dùng Logger
//			// you can "inject" Logger, instead System.out.println
//			throw new InvalidParamException("JWT Token Utils - Không thể tạo Token err: " + e.getMessage());
//		}
//	}

	// Viết riêng cho nó một cái hàm (1)
	// Mã hóa String => Đối tượng Key
	private Key getSignInKey() {
		byte[] bytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(bytes);
	}

	// Hàm tạo ra SecretKey xong mình gán vào biến môi trường
	private String generateSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[32]; // 256-bit key
		random.nextBytes(keyBytes);
		String secretKey = Encoders.BASE64.encode(keyBytes);
		return secretKey;
	}

	// (2)
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				// Truyền Key vào để lấy được Claim
				.setSigningKey(getSignInKey())
				// Build để tạo ra đối tượng
				.build()
				// Chô này sửa lại thành Jws .parseClaimsJwt(token)
				.parseClaimsJws(token)
				//
				.getBody();
	}

	// Nó sẽ trả về một cái Gennerie Type
	// Mình Extract tất và lấy cái mình cần thôi
	// Truyền vào Token và Interface
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		// Trả về danh sách các Claims
		final Claims claims = this.extractAllClaims(token);
		// Function này sẽ trả về một đối tượng
		// Mình cần thuộc tính gì thì truyền vào cái claimsResolver
		return claimsResolver.apply(claims);
	}

	// Check expiration = Kiểm tra cái hàm đó hết hạn chưa
	// Cái ngày hôm nay có nằm trước cái ngày quá hạn không
	public boolean isTokenExpired(String token) {
		// Dùng method reference để lưu cái ngày cần kiểm tra
		Date expirationDate = this.extractClaim(token, Claims::getExpiration);
		return expirationDate.before(new Date());
	}

	// Giải mã khí kiểm tra request
	// Có quyền đề thực hiện API nào
	// Đầu vào Token
	// Sử dụng tham chiếu để lấy ra thuộc tính của từng đối tượng
	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Kiểm tra Username
	// Kiểm tra nó còn hạn không
	public boolean validateToken(String token, UserDetails userDetails) {
		String email = extractEmail(token);
		return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}
}