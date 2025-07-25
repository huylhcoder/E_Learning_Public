import React, { useState } from 'react';
import classNames from 'classnames/bind';

import styles from './VoucherSection.module.scss';

const cx = classNames.bind(styles);

const vouchers = [
    {
        voucherId: 1,
        percentSale: 20,
        name: 'Giảm 20% cho mọi khóa học',
        quantity: 10,
        description: 'Áp dụng cho tất cả các khóa học.',
        startDate: '2024-07-01',
        endDate: '2024-07-31',
    },
    {
        voucherId: 2,
        percentSale: 50,
        name: 'Giảm 50% cho khóa học Python',
        quantity: 0,
        description: 'Chỉ áp dụng cho khóa học Python.',
        startDate: '2024-07-01',
        endDate: '2024-07-15',
    },
    {
        voucherId: 3,
        percentSale: 30,
        name: 'Giảm 30% cho học viên mới',
        quantity: 5,
        description: 'Chỉ áp dụng cho học viên đăng ký mới.',
        startDate: '2024-07-01',
        endDate: '2024-07-31',
    },
    {
        voucherId: 4,
        percentSale: 10,
        name: 'Giảm 10% cho mọi khóa học',
        quantity: 2,
        description: 'Áp dụng cho tất cả các khóa học.',
        startDate: '2024-07-01',
        endDate: '2024-07-31',
    },
];

const VoucherSection = () => {
    const [visibleCount, setVisibleCount] = useState(4);
    const [selectedVoucher, setSelectedVoucher] = useState(null);

    const handleShowAll = () => setVisibleCount(vouchers.length);
    const handleCollapse = () => setVisibleCount(4);

    const isVoucherCollected = (id) => false; // logic kiểm tra đã nhận voucher

    const openModal = (voucher) => setSelectedVoucher(voucher);
    const closeModal = () => setSelectedVoucher(null);

    return (
        <div className="container position-relative mb-5">
            <div className="mb-4">
                <div className={cx('fs-2', 'fw-bold', 'text-primary', 'mt-5', 'mb-5')}>Voucher</div>
                <div className="row g-4">
                    {vouchers.slice(0, visibleCount).map((vc) => (
                        <div className="card voucher-card col-md-3" key={vc.voucherId}>
                            <div className="card-body">
                                <p>
                                    <strong className="fw-bold fs-4">Giảm {vc.percentSale}%</strong>
                                </p>
                                <p>{vc.name}</p>
                                {!isVoucherCollected(vc.voucherId) && vc.quantity > 0 && (
                                    <button
                                        className="btn btn-use btn-success w-100 mt-2"
                                        onClick={() => openModal(vc)}
                                    >
                                        Thu Thập
                                    </button>
                                )}
                                {!isVoucherCollected(vc.voucherId) && vc.quantity === 0 && (
                                    <button className="btn btn-success w-100 mt-2" disabled>
                                        Đã hết
                                    </button>
                                )}
                                {isVoucherCollected(vc.voucherId) && (
                                    <button className="btn btn-success w-100 mt-2" disabled>
                                        Đã nhận
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
                {/* Nút hiển thị tất cả/thu gọn */}
                {vouchers.length > 4 && visibleCount <= 4 && (
                    <button
                        className="btn btn-link btn-show-all position-absolute"
                        style={{ right: 0, top: 0 }}
                        onClick={handleShowAll}
                    >
                        Hiển thị tất cả
                    </button>
                )}
                {visibleCount > 4 && (
                    <button
                        className="btn btn-link btn-show-all position-absolute"
                        style={{ right: 0, top: 0 }}
                        onClick={handleCollapse}
                    >
                        Thu gọn
                    </button>
                )}
            </div>

            {/* Modal voucher */}
            {selectedVoucher && (
                <div className="modal fade show" style={{ display: 'block', background: 'rgba(0,0,0,0.3)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Chi tiết Voucher</h5>
                                <button type="button" className="btn-close" onClick={closeModal}></button>
                            </div>
                            <div className="modal-body">
                                <p>
                                    <strong className="fw-bold">Voucher: </strong>
                                    {selectedVoucher.name}
                                </p>
                                <p>
                                    <strong className="fw-bold">Giảm giá: </strong>
                                    {selectedVoucher.percentSale}%
                                </p>
                                <p>
                                    <strong className="fw-bold">Hiệu Lực: </strong>
                                    từ {selectedVoucher.startDate} đến ngày {selectedVoucher.endDate}
                                </p>
                                <p>
                                    <strong className="fw-bold">Mô tả: </strong>
                                    {selectedVoucher.description}
                                </p>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-primary" onClick={closeModal}>
                                    Thu thập
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default VoucherSection;
