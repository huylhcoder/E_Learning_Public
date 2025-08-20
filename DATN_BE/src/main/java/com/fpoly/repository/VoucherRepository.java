package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.Question;
import com.fpoly.entity.Test;
import com.fpoly.entity.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
	@Query(value = "SELECT v.* " + "FROM my_voucher mv " + "JOIN voucher v ON mv.voucher_id = v.voucher_id "
			+ "JOIN users u ON mv.users_id = u.users_id " + "WHERE u.email = :email " + "AND mv.status = 1 "+ "AND v.status = 1 ", nativeQuery = true)
	List<Voucher> findVouchersByEmail(@Param("email") String email);
	
	Voucher findByVoucherId(int voucherId);	

	// @Query(value = "SELECT * FROM voucher WHERE status = 1 ORDER BY quantity Desc", nativeQuery = true)
	// List<Voucher> findRandomVouchers();
	
	 @Query(value = "SELECT * FROM voucher WHERE status = 1 ORDER BY quantity Desc", nativeQuery = true)
	 List<Voucher> findRandomVouchers();
	 
	 Voucher findByVoucherCode(String voucherCode);

}
