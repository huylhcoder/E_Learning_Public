import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { Link, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrashCan } from '@fortawesome/free-solid-svg-icons';
import axios from '~/utils/CustomizeAxios'; // hoặc axios thường

const Cart = () => {
    const [listGioHang, setListGioHang] = useState([]);
    const [tongTien, setTongTien] = useState(0);
    const [donHang, setDonHang] = useState([]);
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    useEffect(() => {
        document.title = 'E-Learning - Giỏ hàng';
        if (!token) {
            console.log('Token không tồn tại, vui lòng đăng nhập.');
            return;
        }
        loadGioHang();
    }, []);

    const loadGioHang = async () => {
        try {
            const res = await axios.get(`/cart`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const updatedCart = res.data.map((item) => ({ ...item, checked: true }));
            setListGioHang(updatedCart);
            calculateTotalPrice(updatedCart);
            setDonHang(updatedCart.map((item) => item.course));
        } catch (err) {
            toast.error('Lỗi không thể hiển thị giỏ hàng');
        }
    };

    const calculateTotalPrice = (cart) => {
        let selectedCourses = [];
        const total = cart.reduce((sum, item) => {
            if (item.checked) selectedCourses.push(item.course);
            return sum + (item.checked ? item.course.price || 0 : 0);
        }, 0);
        setTongTien(total);
        setDonHang(selectedCourses);
    };

    const toggleCheckbox = (cartId) => {
        const newList = listGioHang.map((item) =>
            item.cartId === cartId ? { ...item, checked: !item.checked } : item,
        );
        setListGioHang(newList);
        calculateTotalPrice(newList);
    };

    const formatCurrency = (value) => {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.') + ' VND';
    };

    const deleteItem = async (cartId) => {
        if (!window.confirm('Bạn có chắc chắn muốn xóa mục này khỏi giỏ hàng?')) return;
        try {
            await axios.delete(`/cart/delete/${cartId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            loadGioHang();
            toast.error('Cập nhật giỏ hàng thành công');
        } catch (err) {
            toast.error('Lỗi không thể xóa giỏ hàng');
        }
    };

    const thanhToan = async () => {
        try {
            // Lấy danh sách courseId từ đơn hàng
            const listCourseId = donHang.map((item) => item.courseId);
            console.log('Danh sách courseId gửi lên:', listCourseId);

            // Gửi POST request
            await axios.post(
                '/payment/add-payment',
                listCourseId, // Gửi body dạng JSON array
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json', // Đảm bảo header đúng
                    },
                },
            );

            // Chuyển hướng sang trang thanh toán
            navigate('/checkout');
        } catch (error) {
            console.error('Lỗi khi tạo payment:', error);
            toast.error('Tạo đơn hàng thất bại, vui lòng thử lại!');
        }
    };

    return (
        <main className="container pb-5 mt-n2 mt-md-n3">
            <div className="row">
                <div className="col-md-8">
                    <h3 className="text-primary ms-3 mt-4 fw-bold">Giỏ hàng</h3>

                    {listGioHang.length === 0 ? (
                        <div className="d-flex justify-content-center align-items-center">
                            <div className="empty-courseCartHB text-center">
                                <img
                                    src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTZLhV29HaDKqmUqIqveXIBCyMdcaINrOyLmA&s"
                                    alt="Empty Cart"
                                />
                                <h3>Giỏ hàng của bạn hiện đang trống</h3>
                                <p>
                                    Trước khi tiến hành thanh toán, bạn phải thêm một số sản phẩm vào giỏ hàng của mình.
                                </p>
                                <Link className="btn btn-outline-primary p-3" to="/course/search">
                                    Tìm kiếm khóa học phù hợp với bạn
                                </Link>
                            </div>
                        </div>
                    ) : (
                        <>
                            {listGioHang.map((item) => (
                                <div
                                    key={item.cartId}
                                    className="d-sm-flex justify-content-between my-4 pb-4 border-bottom"
                                >
                                    <div className="d-block d-sm-flex text-center text-sm-start">
                                        <Link to="/" className="cart-item-thumb mx-auto me-sm-4 align-content-center">
                                            <img src={item.course.avatar} alt="Product" style={{ width: '120px' }} />
                                        </Link>
                                        <div className="pt-3">
                                            <h3 className="fs-5 fw-semibold">
                                                <Link to="/" className="text-primary">
                                                    {item.course.name}
                                                </Link>
                                            </h3>
                                            <div>
                                                <span className="text-muted me-2">Mô tả:</span>
                                                {item.course.description}
                                            </div>
                                            <div>
                                                <span className="text-muted me-2">Đánh giá:</span>
                                                <span>({item.course.averageRating}.0)</span>
                                                <span className="ms-2">
                                                    {[...Array(5)].map((_, i) =>
                                                        i < item.course.averageRating ? (
                                                            <i key={i} className="fa-solid fa-star text-warning"></i>
                                                        ) : (
                                                            <i key={i} className="fa-regular fa-star text-warning"></i>
                                                        ),
                                                    )}
                                                    ({item.course.follow})
                                                </span>
                                            </div>
                                            <div className="text-primary fw-bold pt-2 fs-5">
                                                {item.course.price === 0
                                                    ? 'Miễn phí'
                                                    : formatCurrency(item.course.price)}
                                            </div>
                                        </div>
                                    </div>

                                    <div
                                        className="pt-2 pt-sm-0 ps-sm-3 mx-auto mx-sm-0 text-center text-sm-start"
                                        style={{ maxWidth: '10rem' }}
                                    >
                                        <div className="d-flex align-items-center">
                                            <div className="form-check me-2">
                                                <input
                                                    className="form-check-input form-check-input-sm"
                                                    type="checkbox"
                                                    checked={item.checked}
                                                    onChange={() => toggleCheckbox(item.cartId)}
                                                    id={`largeCheck_${item.cartId}`}
                                                />
                                            </div>
                                            <button
                                                className="btn btn-outline-danger d-flex align-items-center justify-content-center"
                                                type="button"
                                                onClick={() => deleteItem(item.cartId)}
                                                style={{
                                                    height: '2rem',
                                                    width: '2rem',
                                                    padding: 0,
                                                    borderRadius: '4px',
                                                    transition: 'all 0.2s ease',
                                                }}
                                                onMouseEnter={(e) => {
                                                    e.currentTarget.style.backgroundColor = '#dc3545'; // Bootstrap danger
                                                    e.currentTarget.style.color = '#fff';
                                                }}
                                                onMouseLeave={(e) => {
                                                    e.currentTarget.style.backgroundColor = 'transparent';
                                                    e.currentTarget.style.color = '#dc3545';
                                                }}
                                            >
                                                <FontAwesomeIcon icon={faTrashCan} />
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </>
                    )}
                </div>

                <div className="col-md-4 d-flex pt-3 pt-md-0 border-1 total-price-container">
                    <div className="total-price-content w-100">
                        <h6 className="h3 px-4 text-primary fw-bold text-center mt-2">Tổng tiền</h6>
                        <div className="text-center h4">{formatCurrency(tongTien)}</div>
                        <div className="text-center h4">Số lượng khóa học: {donHang.length}</div>
                        <hr />
                        <button onClick={thanhToan} className="btn btn-primary btn-shadow w-100 p-3">
                            Tiến hành thanh toán
                        </button>
                    </div>
                </div>
            </div>
        </main>
    );
};

export default Cart;
