import React from 'react';
import './Checkout.module.scss'; // Custom SCSS file for styling

const VoucherList = ({ vouchers, selectedVoucher, onSelectVoucher }) => {
    return (
        <div className="voucher-list">
            {vouchers.map((voucher) => (
                <div
                    key={voucher.voucherId}
                    className={`voucher-item card p-3 mb-3 ${selectedVoucher?.voucherId === voucher.voucherId ? 'selected' : ''}`}
                    onClick={() => onSelectVoucher(voucher)}
                    style={{
                        borderColor: selectedVoucher?.voucherId === voucher.voucherId ? '#28a745' : '#ddd',
                        backgroundColor: selectedVoucher?.voucherId === voucher.voucherId ? '#c3e6cb' : '#fff',
                        cursor: 'pointer',
                        position: 'relative',
                    }}
                >
                    <span
                        style={{
                            position: 'absolute',
                            top: '10px',
                            right: '10px',
                            fontSize: '12px',
                            color: selectedVoucher?.voucherId === voucher.voucherId ? '#28a745' : '#888',
                        }}
                    >
                        {selectedVoucher?.voucherId === voucher.voucherId ? 'Đã chọn' : 'Sử dụng'}
                    </span>
                    <p className="h3 text-success">{voucher.name}</p>
                    <p className='h5 text-muted'>{voucher.description}</p>
                    <p className="h5 text-success">Giảm giá: {voucher.percentSale}%</p>
                </div>
            ))}
        </div>
    );
};

export default VoucherList;
