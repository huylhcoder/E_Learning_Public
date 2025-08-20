
package com.fpoly.entity;

import java.util.Date;
import java.util.List;
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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private int paymentId;
	
	@ManyToOne
	@JoinColumn(name = "users_id")
	User user;

	@Column(name = "transaction_no")
	private String transactionNo;

	@Column(name = "transaction_status")
	private boolean transactionStatus; //Thanh toán thành công || Thanh toán thất bại

	@Column(name = "banktran_no")
	private String banktranNo;

	@Column(name = "txn_ref")
	private String txnRef;

	@Column(name = "amount")
	private float amount;

	@Column(name = "bank_code")
	private String bankCode;

	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;
	
	@JsonIgnore
	@OneToMany(mappedBy = "payment")
	List<RegisteredCourse> listRegisteredCourse;

}
