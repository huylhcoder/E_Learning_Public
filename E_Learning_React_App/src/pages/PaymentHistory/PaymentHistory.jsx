// Libs
import { useEffect, useState } from 'react';
import { FaEye } from 'react-icons/fa';
import axios from '~/utils/CustomizeAxios'; // bạn đã config sẵn baseURL
import { Modal, Button, Table } from 'react-bootstrap';

import { formatCurrency } from '~/utils/format';

const PaymentHistory = ({ userId }) => {
    const [payments, setPayments] = useState([]);
    const [selectedPayment, setSelectedPayment] = useState(null);
    const [showModal, setShowModal] = useState(false);

    // Load danh sách payment
    useEffect(() => {
        const fetchPayments = async () => {
            try {
                const token = localStorage.getItem('token');
                const res = await axios.get(`/payment/history`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Sắp xếp theo ngày giảm dần (mới nhất trước)
                const sorted = [...res.data].sort((a, b) => new Date(b.createAt) - new Date(a.createAt));

                setPayments(sorted);
            } catch (error) {
                console.error('Lỗi khi tải lịch sử thanh toán:', error);
            }
        };
        fetchPayments();
    }, [userId]);

    const handleViewDetail = (payment) => {
        setSelectedPayment(payment);
        setShowModal(true);
    };

    return (
        <div className="container mt-4">
            <h2 className="mb-3">Lịch sử thanh toán</h2>

            <Table striped bordered hover responsive>
                <thead className="table-dark">
                    <tr>
                        <th>#</th>
                        <th>Mã giao dịch</th>
                        <th>Ngân hàng</th>
                        <th>Số tiền</th>
                        <th>Ngày tạo</th>
                        <th>Khóa học</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {payments.length > 0 ? (
                        payments.map((p, index) => (
                            <tr key={p.paymentId}>
                                <td>{index + 1}</td>
                                <td>{p.transactionNo}</td>
                                <td>{p.bankCode}</td>
                                <td>{formatCurrency(p.amount)}</td>
                                <td>{new Date(p.createAt).toLocaleDateString('vi-VN')}</td>
                                <td>{p.courses.length}</td>
                                <td>
                                    <Button
                                        variant="primary"
                                        className="fs-5"
                                        size="sm"
                                        onClick={() => handleViewDetail(p)}
                                    >
                                        <FaEye className="me-1" />
                                        Chi tiết
                                    </Button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={7} className="text-center">
                                Chưa có giao dịch nào
                            </td>
                        </tr>
                    )}
                </tbody>
            </Table>

            {/* Modal chi tiết */}
            <Modal show={showModal} onHide={() => setShowModal(false)} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>Chi tiết thanh toán</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedPayment ? (
                        <>
                            <h5>Mã giao dịch: {selectedPayment.transactionNo}</h5>
                            <p>
                                Ngân hàng: <strong>{selectedPayment.bankCode}</strong> <br />
                                Tổng tiền: <strong>{selectedPayment.amount.toLocaleString()} VNĐ</strong> <br />
                                Ngày tạo: {new Date(selectedPayment.createAt).toLocaleDateString('vi-VN')}
                            </p>
                            <h6>Danh sách khóa học:</h6>
                            <Table bordered hover responsive>
                                <thead className="table-light">
                                    <tr>
                                        <th>Khóa học</th>
                                        <th>Tên</th>
                                        <th>Giá</th>
                                        <th>TB đánh giá</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {selectedPayment.courses.map((c, idx) => (
                                        <tr key={c.courseId}>
                                            <td>
                                                <img
                                                    src={c.avatar}
                                                    alt={c.name}
                                                    style={{ width: '60px', height: '40px', objectFit: 'cover' }}
                                                />
                                            </td>
                                            <td>{c.name}</td>
                                            <td>{c.price.toLocaleString()} đ</td>
                                            <td>{c.averageRating} ⭐</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </>
                    ) : (
                        <p>Không có dữ liệu</p>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Đóng
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default PaymentHistory;
