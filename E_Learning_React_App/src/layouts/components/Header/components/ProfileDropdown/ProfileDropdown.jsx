import { Button } from 'react-bootstrap';
import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAddressCard } from '@fortawesome/free-solid-svg-icons';
import styles from './Profile.module.scss';
import classNames from 'classnames/bind';

const cx = classNames.bind(styles);

// Ví dụ trong menu:
<FontAwesomeIcon icon={faAddressCard} className="me-2" />;

export const ProfileDropdown = ({ avatar, isTokenValid, role, handleLogout }) => {
    return (
        <div className="nav-item dropdown mx-2">
            <button
                className="btn btn-primary rounded-circle d-flex align-items-center justify-content-center p-0"
                style={{ width: '34px', height: '34px', overflow: 'hidden' }}
                data-bs-toggle="dropdown"
                aria-expanded="false"
            >
                <img
                    src={avatar || 'https://bootdey.com/img/Content/avatar/avatar7.png'}
                    alt="User Avatar"
                    style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover',
                        borderRadius: '50%',
                    }}
                />
            </button>

            <ul className={cx('profile-dropdown-menu', 'dropdown-menu', 'dropdown-menu-end', 'text-start')}>
                {isTokenValid && (
                    <>
                        <li>
                            <Link to="/profile" className={cx('dropdown-item')}>
                                Thông tin
                            </Link>
                        </li>
                        <li>
                            <Link to="/my-courses" className={cx('dropdown-item')}>
                                Tiến độ khóa học
                            </Link>
                        </li>
                        <li>
                            <Button
                                className={cx('dropdown-item', 'w-100', 'text-decoration-none')}
                                id="logout"
                                onClick={handleLogout}
                                variant="link"
                                as="a"
                                type="button"
                            >
                                Đăng xuất
                            </Button>
                        </li>
                    </>
                )}
            </ul>
        </div>
    );
};
