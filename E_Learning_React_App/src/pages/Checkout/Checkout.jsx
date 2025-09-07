import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from '~/utils/CustomizeAxios';
import CourseList from './CourseList';
import EmptyCart from './EmptyCart';
import OrderSummary from './OrderSummary';
import VoucherList from './VoucherList';

import './Checkout.module.scss'; // Assuming you have some styles for the checkout page

const Checkout = () => {
    const navigate = useNavigate();
    const { search } = useLocation();
    const [courses, setCourses] = useState([]);
    const [paymentStatus, setPaymentStatus] = useState(null);
    const [voucher, setVoucher] = useState(null);
    const [vouchers, setVouchers] = useState([]);

    useEffect(() => {
        document.title = 'Thanh toán - E-Learning';
        const loadData = async () => {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }
            const headers = { Authorization: `Bearer ${token}` };
            const params = new URLSearchParams(search);
            const courseId = params.get('courseId');

            try {
                if (courseId) {
                    // Thanh toán khóa học từ CourseDetail
                    const res = await axios.get(`/course/course-detail/${courseId}`, { headers });
                    setCourses([res.data]);
                    setPaymentStatus(false);
                } else {
                    // Trường hợp không có query param
                    const resp = await axios.get('/payment/payment-latest', { headers });
                    console.log('Kết quả thanh toán:', resp);
                    if (resp.data.paymentStatus) {
                        navigate('/payment-result?vnPaymentStatus=true');
                    }
                    setCourses(resp.data.courses);
                    setPaymentStatus(resp.data.paymentStatus);
                }

                // Lấy danh sách voucher
                const voucherResp = await axios.get('/voucher/myvoucher', { headers });
                setVouchers(voucherResp.data);

            } catch (err) {
                console.error(err);
                alert('Lỗi khi lấy dữ liệu thanh toán.');
            }
        };

        loadData();
    }, [search, navigate]);

    const handleCheckout = async () => {
        console.log(voucher);
        console.log(courses);

        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
            return;
        }

        const formData = new FormData();
        const listCourseId = courses.map((c) => c.courseId);
        formData.append('listCourseId', JSON.stringify(listCourseId));

        const voucherCode = voucher?.voucherCode?.trim() || '';
        formData.append('voucherCode', voucherCode);

        try {
            const resp = await axios.post('/vnpayajax', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log('Kết quả thanh toán:', resp);
            const paymentUrl = resp.data?.data;
            if (paymentUrl) {
                window.location.href = paymentUrl;
            } else {
                alert('Không nhận được link thanh toán từ server.');
            }
        } catch (err) {
            console.error(err);
            alert('Lỗi khi thực hiện thanh toán.');
        }
    };

    // Hàm xử lý chọn voucher
    const onSelectVoucher = (selectedVoucher) => {
        setVoucher(selectedVoucher); // Cập nhật voucherId thay vì voucher.code
    };

    return (
        <div className="container mt-5">
            <div className="row">
                <div className="col-lg-9">
                    <h2 className="text-primary">Chi tiết đơn hàng</h2>
                    {courses.length === 0 ? <EmptyCart /> : <CourseList courses={courses} />}
                    <h2 className="text-primary mt-5">Khuyến mãi của tôi</h2>
                    <VoucherList vouchers={vouchers} selectedVoucher={voucher} onSelectVoucher={onSelectVoucher} />
                </div>
                <div className="col-lg-3">
                    <OrderSummary
                        courses={courses}
                        voucher={voucher}
                        setVoucher={setVoucher}
                        paymentStatus={paymentStatus}
                        onCheckout={handleCheckout}
                    />
                </div>
            </div>
        </div>
    );
};

export default Checkout;
