//Thư viện
import React, { useContext, useEffect, useRef, useState } from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import classNames from 'classnames/bind';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCartShopping } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';

//Components
import styles from './Header.module.scss';
import CategoryDropdown from './components/CategoryDropdown/CategoryDropdown';
import Search from './components/Search/Search';
import { ProfileDropdown } from './components/ProfileDropdown/ProfileDropdown';
import avatarDefault from '~/assets/images/avatar-default.jpg';

//Services
import AuthContext from '~/context/AuthContext';
import { CartContext } from '~/context/CartContext';
import { UserContext } from '~/context/UserContext';
import { getAvatar } from '~/services/ProfileService';

const cx = classNames.bind(styles);

function Header() {
    const { userProfile, avatar: contextAvatar } = useContext(UserContext) || {}; // 👈 lấy avatar từ Context nếu có
    const [localAvatar, setLocalAvatar] = useState(null); // 👈 avatar local fallback
    const authContext = useContext(AuthContext);
    const { cartItems } = useContext(CartContext);
    const location = useLocation();
    const underlineRef = useRef(null);
    const navigate = useNavigate();

    // fallback: nếu không có contextAvatar thì fetch từ API
    useEffect(() => {
        const fetchAvatar = async () => {
            try {
                if (authContext.authenticated && !contextAvatar) {
                    const avatarUrl = await getAvatar();
                    setLocalAvatar(avatarUrl);
                } else if (!authContext.authenticated) {
                    setLocalAvatar(null);
                }
            } catch (error) {
                console.error('Lỗi lấy avatar:', error);
            }
        };
        fetchAvatar();
    }, [authContext.authenticated, contextAvatar]);

    useEffect(() => {
        const activeLink = document.querySelector(`.nav-item.active`);
        if (activeLink && underlineRef.current) {
            underlineRef.current.style.left = `${activeLink.offsetLeft}px`;
            underlineRef.current.style.width = `${activeLink.offsetWidth}px`;
        }
    }, [location.pathname]);

    const handleLogout = () => {
        localStorage.clear();
        authContext.refresh();
        navigate('/login');
        toast.success('Đăng xuất thành công');
    };

    // Ưu tiên contextAvatar > localAvatar > avatarDefault
    const avatarToShow = contextAvatar || localAvatar || avatarDefault;

    return (
        <div className={cx('header-page')}>
            <div className="container-fluid">
                <nav className="navbar navbar-expand-lg bg-white navbar-light py-3 py-lg-0 px-lg-5">
                    {/* Logo về trang chủ */}
                    <NavLink to="/home" className="navbar-brand ml-lg-3">
                        <h1 className="m-0 text-uppercase text-primary rounded">
                            <i className="fa fa-book-reader mr-3"></i>E-LEARNING
                        </h1>
                    </NavLink>

                    {/* Nút trên di động */}
                    <button
                        type="button"
                        className="navbar-toggler rounded"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarCollapse"
                    >
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse justify-content-between px-lg-3" id="navbarCollapse">
                        <CategoryDropdown />
                        <Search />
                        <NavLink to="/my-course" className={cx('custom-my-course-btn')}>
                            <span>Học tập của tôi</span>
                        </NavLink>
                        <NavLink to="/cart" className={cx('custom-my-course-btn', 'position-relative')}>
                            <span style={{ position: 'relative', display: 'inline-block' }}>
                                <FontAwesomeIcon icon={faCartShopping} size="lg" />
                                {cartItems?.length > 0 && (
                                    <span
                                        className="position-absolute badge rounded-pill bg-danger"
                                        style={{
                                            top: '-6px',
                                            right: '-10px',
                                            fontSize: '0.75rem',
                                            padding: '2px 6px',
                                            minWidth: 18,
                                            height: 18,
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                        }}
                                    >
                                        {cartItems?.length ?? 0}
                                    </span>
                                )}
                            </span>
                        </NavLink>

                        <div className="navbar-nav ml-auto d-flex align-items-center">
                            {!authContext.authenticated ? (
                                <>
                                    <NavLink
                                        to="/login"
                                        className="btn btn-outline-primary me-2 p-3"
                                        style={{ minWidth: 80, fontSize: '1.4rem', fontWeight: 700 }}
                                    >
                                        Đăng nhập
                                    </NavLink>
                                    <NavLink
                                        to="/register"
                                        className="btn btn-primary p-3"
                                        style={{ minWidth: 80, fontSize: '1.4rem', fontWeight: 700 }}
                                    >
                                        Đăng ký
                                    </NavLink>
                                </>
                            ) : (
                                <ProfileDropdown
                                    avatar={avatarToShow}
                                    isTokenValid={authContext.authenticated}
                                    handleLogout={handleLogout}
                                />
                            )}
                        </div>
                    </div>
                </nav>
            </div>
        </div>
    );
}

export default Header;
