package com.fpoly.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.entity.Voucher;
import com.fpoly.repository.VoucherRepository;

@Service
public class VoucherService {
	@Autowired
	VoucherRepository saleRepository;
	@Autowired
	VoucherRepository voucherRepository;

//MyVoucher Page
	public List<Voucher> getRDVoucher() {
		return voucherRepository.findRandomVouchers();
	}


//Khác
	public List<Voucher> getAllVoucher() {
		return saleRepository.findAll();
	}

	public void addVoucherToan(Voucher sale) {
		saleRepository.save(sale);
	}

	public Voucher updateSaleToan(Voucher sale) {
		return saleRepository.save(sale);
	}

	public Voucher findVoucherByIdToan(int id) {
		return saleRepository.findByVoucherId(id);
	}

	public Voucher findVoucherByCode_Huy(String voucherCode) {
		return saleRepository.findByVoucherCode(voucherCode);
	}

	public void deleteVoucherToan(int id) {
		saleRepository.deleteById(id);
	}

	public List<Voucher> getVouchersByEmail(String email) {
		return saleRepository.findVouchersByEmail(email);
	}

	public Voucher getVoucherById(int id) {
		return saleRepository.findById(id).orElse(null);
	}

	public void save(Voucher voucher) {
		saleRepository.save(voucher);
	}

	public boolean existsByVoucherCode(String voucherCode) {
		if (saleRepository.findByVoucherCode(voucherCode) != null) {
			return true;
		} else {
			return false;
		}
	}
}
