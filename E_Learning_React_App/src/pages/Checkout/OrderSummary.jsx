import { useState } from 'react';
import axios from '~/utils/CustomizeAxios';

const OrderSummary = ({ courses, voucher, paymentStatus, onCheckout, setVoucher }) => {
    const [voucherCodeInput, setVoucherCodeInput] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const totalPrice = courses.reduce((acc, c) => acc + c.price, 0);
    const discount = voucher ? totalPrice * (voucher.percentSale / 100) : 0;
    const discountedPrice = totalPrice - discount;

    const handleCheckVoucher = async () => {
        try {
            const token = localStorage.getItem('token');
            const headers = { Authorization: `Bearer ${token}` };
            const response = await axios.get(`/voucher/check-voucher/${voucherCodeInput}`, { headers });
            if (response.status === 200) {
                setVoucher(response.data);
                setErrorMessage('');
            }else{
                setErrorMessage(response.data.message || 'Mã giảm giá không hợp lệ hoặc đã hết hạn.');
            }
            
        } catch (error) {
            setVoucher('');
            setErrorMessage('Mã giảm giá không hợp lệ hoặc đã hết hạn.');
        }
    };

    return (
        <>
            <h3 className="text-muted">Tổng tiền</h3>
            <div className="card position-sticky top-0">
                <div className="p-3 bg-light">
                    <p className="fw-bold">
                        Giá gốc: <span className="float-end">{totalPrice.toLocaleString('vi-VN')} VND</span>
                    </p>
                    {voucher && (
                        <p className="text-success">
                            Giảm giá: <span className="float-end">-{discount.toLocaleString('vi-VN')} VND</span>
                        </p>
                    )}
                    <hr />
                    <p className="fw-bold">
                        Tổng tiền: <span className="float-end">{discountedPrice.toLocaleString('vi-VN')} VND</span>
                    </p>
                    <button
                        className="btn btn-success w-100 mt-2"
                        disabled={paymentStatus || courses.length === 0}
                        onClick={onCheckout}
                    >
                        {paymentStatus ? 'Đã thanh toán' : 'Thanh Toán'}
                    </button>
                    <hr />
                    <div className="mt-3">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Nhập mã giảm giá"
                            value={voucherCodeInput}
                            onChange={(e) => setVoucherCodeInput(e.target.value)}
                        />
                        <button className="btn btn-primary w-100 mt-2" onClick={handleCheckVoucher}>
                            Áp dụng mã giảm giá
                        </button>
                        {errorMessage && <p className="text-danger h5 mt-3">{errorMessage}</p>}
                    </div>
                </div>
            </div>
        </>
    );
};

export default OrderSummary;
