// MyVoucher.jsx
import { useEffect, useState } from 'react';
import axios from '~/utils/CustomizeAxios';
import { toast } from 'react-toastify';
import { Modal, Button } from 'react-bootstrap';
import classNames from 'classnames/bind';
import styles from './MyVoucher.module.scss';

// Import icon từ React Icons
import { FaStar, FaGift } from 'react-icons/fa';

const cx = classNames.bind(styles);

export default function MyVoucher() {
    const [listVC, setListVC] = useState([]);
    const [visibleCount, setVisibleCount] = useState(4);
    const [selectedVoucher, setSelectedVoucher] = useState(null);
    const [myVouchers, setMyVouchers] = useState([]);
    const [showModal, setShowModal] = useState(false);

    const token = localStorage.getItem('token');

    useEffect(() => {
        loadVC();
        loadMyVouchers();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Định dạng ngày
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}-${month}-${year}`;
    };

    // Load danh sách voucher
    const loadVC = async () => {
        try {
            const resp = await axios.get('/voucher/rdVoucher', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            const formattedData = resp.data.map((item) => ({
                ...item,
                startDate: formatDate(item.startDate),
                endDate: formatDate(item.endDate),
            }));
            setListVC(formattedData);
        } catch (err) {
            console.error('Lỗi load voucher:', err);
        }
    };

    // Load voucher đã nhận
    const loadMyVouchers = async () => {
        if (!token) return;
        try {
            const resp = await axios.get('/voucher/myvoucher', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setMyVouchers(resp.data.map((item) => item.voucherId));
        } catch (err) {
            console.error('Lỗi load voucher của tôi:', err);
        }
    };

    // Kiểm tra voucher đã nhận
    const isVoucherCollected = (voucherId) => myVouchers.includes(voucherId);

    // Modal chi tiết
    const openModal = (vc) => {
        setSelectedVoucher(vc);
        setShowModal(true);
    };

    // Thu thập voucher
    const collectVoucher = async (voucherId) => {
        if (!token) {
            toast.error('Bạn cần đăng nhập để thu thập voucher');
            return;
        }
        try {
            await axios.post(
                `/voucher/collect/${voucherId}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                },
            );
            toast.success('Thu thập voucher thành công!');
            loadVC();
            loadMyVouchers();
            setShowModal(false);
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Thu thập voucher thất bại!';
            toast.error(errorMessage);
        }
    };

    // Chia 2 nhóm voucher
    const availableVouchers = listVC.filter((vc) => !isVoucherCollected(vc.voucherId));
    const collectedVouchers = listVC.filter((vc) => isVoucherCollected(vc.voucherId));

    return (
        <div className="container position-relative">
            {/* Voucher có thể thu thập */}
            <div className="mb-5">
                <h4>
                    <FaStar className="icon-wrapper me-2" />
                    Voucher có thể thu thập
                </h4>
                <div className="row g-4">
                    {availableVouchers.slice(0, visibleCount).map((vc) => (
                        <div key={vc.voucherId} className={cx('voucher-card', 'card', 'col-md-3')}>
                            <div className="card-body">
                                <p>
                                    <strong className="fw-bold fs-4">Giảm {vc.percentSale}%</strong>
                                </p>
                                <p>{vc.name}</p>

                                {vc.quantity > 0 ? (
                                    <button
                                        className="btn btn-outline-success w-100 mt-2 fs-5"
                                        onClick={() => openModal(vc)}
                                    >
                                        Thu Thập
                                    </button>
                                ) : (
                                    <button className="btn btn-success w-100 mt-2 fs-5" disabled>
                                        Đã hết
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>

                {availableVouchers.length > 4 && visibleCount <= 4 && (
                    <button
                        className="btn btn-link btn-show-all position-absolute"
                        onClick={() => setVisibleCount(availableVouchers.length)}
                    >
                        Hiển thị tất cả
                    </button>
                )}

                {visibleCount > 4 && (
                    <button className="btn btn-link btn-show-all position-absolute" onClick={() => setVisibleCount(4)}>
                        Thu gọn
                    </button>
                )}
            </div>

            {/* Voucher đã thu thập */}
            <div className="mb-4">
                <h4>
                    <FaGift className="icon-wrapper me-2" />
                    Voucher đã thu thập
                </h4>
                <div className="row g-4">
                    {collectedVouchers.length === 0 && <p>Bạn chưa thu thập voucher nào.</p>}
                    {collectedVouchers.map((vc) => (
                        <div key={vc.voucherId} className={cx('voucher-card', 'card', 'col-md-3')}>
                            <div className="card-body">
                                <p>
                                    <strong className="fw-bold fs-4">Giảm {vc.percentSale}%</strong>
                                </p>
                                <p>{vc.name}</p>
                                <p>
                                    <strong className="fw-bold">Hiệu Lực: </strong>
                                    Từ {selectedVoucher?.startDate} đến ngày {selectedVoucher?.endDate}
                                </p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* Modal chi tiết Voucher */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Chi tiết Voucher</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedVoucher && (
                        <>
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
                                Từ {selectedVoucher?.startDate} đến ngày {selectedVoucher?.endDate}
                            </p>
                            <p>
                                <strong className="fw-bold">Mô tả: </strong>
                                {selectedVoucher.description}
                            </p>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={() => collectVoucher(selectedVoucher.voucherId)}>
                        Thu thập
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}
