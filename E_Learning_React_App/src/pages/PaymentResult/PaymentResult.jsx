import { useEffect, useState } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import axios from '~/utils/CustomizeAxios';
import CourseList from '../Checkout/CourseList';
import styles from './PaymentSuccess.module.scss';

import { CheckCircle, XCircle } from 'react-feather'; // Biểu tượng hiện đại

const PaymentResult = () => {
    const { search } = useLocation();
    const params = new URLSearchParams(search);
    const vnPaymentStatus = params.get('vnPaymentStatus');
    const [courses, setCourses] = useState([]);

    useEffect(() => {
        if (vnPaymentStatus === 'true') {
            const fetchCourses = async () => {
                const token = localStorage.getItem('token');
                const headers = { Authorization: `Bearer ${token}` };
                const resp = await axios.get('/payment/payment-latest', { headers });
                console.log('Kết quả thanh toán:', resp);
                if (resp.status === 200 && resp.data && resp.data.courses && resp.data.courses.length > 0) {
                    toast.success('Thanh toán thành công');
                    setCourses(resp.data.courses);
                } else {
                    console.error('No courses found');
                }
            };
            fetchCourses();
        } else {
            toast.error('Thanh toán thất bại');
        }
    }, [vnPaymentStatus]);

    return (
        <div className={styles.resultWrapper}>
            {vnPaymentStatus === 'true' ? (
                <>
                    <div className={styles.card}>
                        <CheckCircle className={styles.iconSuccess} size={64} />
                        <h2 className={styles.title}>Thanh toán thành công</h2>
                        <p className={styles.message}>
                            Cảm ơn bạn đã mua khóa học. Bạn có thể truy cập khóa học ngay bây giờ.
                        </p>
                        <div className={styles.actions}>
                            <Link to="/my-course" className={styles.primaryButton}>
                                Truy cập khóa học
                            </Link>
                        </div>
                    </div>
                    <CourseList courses={courses} />
                </>
            ) : (
                <div className={styles.card}>
                    <XCircle className={styles.iconFail} size={64} />
                    <h2 className={styles.title}>Thanh toán thất bại</h2>
                    <p className={styles.message}>Có lỗi xảy ra. Vui lòng thử lại hoặc liên hệ hỗ trợ.</p>
                    <div className={styles.actions}>
                        <Link to="/" className={styles.secondaryButton}>
                            Quay về trang chủ
                        </Link>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PaymentResult;
