import classNames from 'classnames/bind';
import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

import styles from './CourseDetail.module.scss';
import axios from '~/utils/CustomizeAxios';
import { CartContext } from '~/context/CartContext'; // Thêm dòng này

const cx = classNames.bind(styles);

function CourseSidebar({ price, isInCart, setIsInCart, paymentStatus, courseId, avatar, authenticated }) {
    const navigate = useNavigate();
    const token = sessionStorage.getItem('token');
    const { refreshCart } = useContext(CartContext);

    const handleGoToLearning = () => {
        navigate(`/learning/?courseId=${courseId}`);
    };

    const handleBuyNow = () => {
        if (!authenticated) {
            navigate('/login');
        } else {
            navigate(`/checkout?courseId=${courseId}`);
        }
    };

    const handleAddToCart = async () => {
        if (!authenticated) {
            // localStorage key: cartItems
            const currentCart = JSON.parse(localStorage.getItem('cartItems')) || [];
            if (!currentCart.includes(courseId)) {
                currentCart.push(courseId);
                localStorage.setItem('cartItems', JSON.stringify(currentCart));
                toast.success('Đã thêm vào giỏ hàng');
                refreshCart(); // 👈 cập nhật context
            } else {
                toast.info('Khoá học đã có trong giỏ hàng');
            }
        } else {
            try {
                await axios.post(
                    `/cart/add?courseId=${courseId}`, // Chuyển courseId sang query
                    {}, // Không cần body vì backend không nhận body
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    },
                );
                setIsInCart(true);
                toast.success('Đã thêm vào giỏ hàng');
                refreshCart(); // 👈 cập nhật context
            } catch (error) {
                toast.error('Lỗi khi thêm vào giỏ hàng');
            }
        }
    };

    return (
        <div className={cx('sidebar', 'card', 'position-relative')}>
            <div className="position-relative">
                <img src={avatar} className="card-img-top" alt="Course" />
                {paymentStatus && (
                    <span className="badge bg-success position-absolute top-0 end-0 m-2" style={{ fontSize: '0.9rem' }}>
                        Đã đăng ký
                    </span>
                )}
            </div>
            <div className="card-body">
                {paymentStatus ? (
                    <button className="btn btn-primary w-100 fw-bold" onClick={handleGoToLearning}>
                        Chuyển qua trang học bài
                    </button>
                ) : (
                    <>
                        <span className="card-title text-danger h3 d-block mb-3">{price.toLocaleString()} VNĐ</span>
                        <button className="btn btn-primary w-100 mb-2 fw-bold" onClick={handleBuyNow}>
                            Mua ngay
                        </button>
                        {isInCart ? (
                            <button
                                className="btn btn-outline-secondary w-100 fw-bold"
                                disabled
                                onClick={handleAddToCart}
                            >
                                Đã có trong giỏ hàng
                            </button>
                        ) : (
                            <button className="btn btn-outline-secondary w-100 fw-bold" onClick={handleAddToCart}>
                                Thêm vào giỏ hàng
                            </button>
                        )}
                    </>
                )}
                <ul className="mt-4 list-unstyled text-muted small">
                    <li>✔ Truy cập trọn đời</li>
                    <li>✔ Giấy chứng nhận hoàn thành</li>
                    <li>✔ Học mọi lúc, mọi nơi</li>
                </ul>
            </div>
        </div>
    );
}

export default CourseSidebar;
