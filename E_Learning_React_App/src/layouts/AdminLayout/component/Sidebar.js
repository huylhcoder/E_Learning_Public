import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { FaTachometerAlt, FaBook, FaList, FaUserCircle, FaSignOutAlt, FaChevronDown, FaChevronRight, FaBars } from 'react-icons/fa';
import classNames from 'classnames/bind';
import PropTypes from 'prop-types';
import styles from './Sidebar.module.scss';

const cx = classNames.bind(styles);

function Sidebar({ isCollapsed, onToggle }) {
    const [openMenus, setOpenMenus] = useState({});

    const toggleSubmenu = (menuId) => {
        setOpenMenus(prev => ({
            ...prev,
            [menuId]: !prev[menuId]
        }));
    };

    return (
        <div className={cx('sidebar', { collapsed: isCollapsed })}>
            <div className={cx('menu')}>
                <button className={cx('toggle-btn')} onClick={onToggle}>
                    <FaBars className={cx('icon')} />
                    <span className={cx('menu-text')}>Thu gọn menu</span>
                </button>
                
                <NavLink to="/admin/dashboard" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <FaTachometerAlt className={cx('icon')} />
                    <span className={cx('menu-text')}>Dashboard</span>
                </NavLink>

                <div className={cx('menu-group')}>
                    <div 
                        className={cx('menu-parent')} 
                        onClick={() => toggleSubmenu('courses')}
                    >
                        <div className={cx('parent-content')}>
                            <FaBook className={cx('icon')} />
                            <span className={cx('menu-text')}>Quản lý khóa học</span>
                        </div>
                        {openMenus['courses'] ? 
                            <FaChevronDown className={cx('icon')} /> : 
                            <FaChevronRight className={cx('icon')} />
                        }
                    </div>
                    <div className={cx('submenu', { open: openMenus['courses'] })}>
                        <NavLink
                            to="/admin/categories"
                            className={({ isActive }) => cx('submenu-item', { active: isActive })}
                        >
                            <FaList className={cx('icon')} />
                            <span className={cx('menu-text')}>Quản lý danh mục</span>
                        </NavLink>
                        <NavLink
                            to="/admin/courses"
                            className={({ isActive }) => cx('submenu-item', { active: isActive })}
                        >
                            <FaBook className={cx('icon')} />
                            <span className={cx('menu-text')}>Quản lý khóa học</span>
                        </NavLink>
                    </div>
                </div>
            </div>

            <div className={cx('bottom')}>
                <NavLink to="/admin/profile" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <FaUserCircle className={cx('icon')} />
                    <span className={cx('menu-text')}>Profile</span>
                </NavLink>
                <button
                    className={cx('menu-item', 'logout')}
                    onClick={() => {
                        localStorage.clear();
                        window.location.href = '/login';
                    }}
                >
                    <FaSignOutAlt className={cx('icon')} />
                    <span className={cx('menu-text')}>Đăng xuất</span>
                </button>
            </div>
        </div>
    );
}

Sidebar.propTypes = {
    isCollapsed: PropTypes.bool,
    onToggle: PropTypes.func.isRequired
};

export default Sidebar;
