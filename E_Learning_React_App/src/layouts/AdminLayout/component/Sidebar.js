import { useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import {
    LuLayoutDashboard,
    LuBookOpen,
    LuListTree,
    LuUserRound,
    LuLogOut,
    LuChevronDown,
    LuChevronRight,
    LuPanelLeftClose,
    LuPercent,
} from 'react-icons/lu';
import classNames from 'classnames/bind';
import PropTypes from 'prop-types';
import styles from './Sidebar.module.scss';

const cx = classNames.bind(styles);

function Sidebar({ isCollapsed, onToggle }) {
    const [openMenus, setOpenMenus] = useState({});
    const location = useLocation();

    const toggleSubmenu = (menuId) => {
        setOpenMenus((prev) => ({
            ...prev,
            [menuId]: !prev[menuId],
        }));
    };

    return (
        <div className={cx('sidebar', { collapsed: isCollapsed })}>
            <div className={cx('menu')}>
                <button className={cx('toggle-btn')} onClick={onToggle}>
                    <LuPanelLeftClose className={cx('icon')} />
                    <span className={cx('menu-text')}>Thu gọn menu</span>
                </button>

                <NavLink to="/admin/dashboard" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <LuLayoutDashboard className={cx('icon')} />
                    <span className={cx('menu-text')}>Dashboard</span>
                </NavLink>

                {/* Quản lý khóa học */}
                <div className={cx('menu-group')}>
                    <div
                        className={cx('menu-parent', { active: location.pathname.startsWith('/admin/course') })}
                        onClick={() => toggleSubmenu('courses')}
                    >
                        <div className={cx('parent-content')}>
                            <LuBookOpen className={cx('icon')} />
                            <span className={cx('menu-text')}>Quản lý khóa học</span>
                        </div>
                        {openMenus['courses'] ? (
                            <LuChevronDown className={cx('icon')} />
                        ) : (
                            <LuChevronRight className={cx('icon')} />
                        )}
                    </div>
                    <div className={cx('submenu', { open: openMenus['courses'] })}>
                        <NavLink
                            to="/admin/categories"
                            className={({ isActive }) => cx('submenu-item', { active: isActive })}
                        >
                            <LuListTree className={cx('icon')} />
                            <span className={cx('menu-text')}>Quản lý danh mục</span>
                        </NavLink>
                        <NavLink
                            to="/admin/course-manager"
                            className={({ isActive }) =>
                                cx('submenu-item', {
                                    active: isActive || location.pathname.startsWith('/admin/course-detail-manager'),
                                })
                            }
                        >
                            <LuBookOpen className={cx('icon')} />
                            <span className={cx('menu-text')}>Quản lý khóa học</span>
                        </NavLink>
                    </div>
                </div>

                {/* Khuyến mãi */}
                <div className={cx('menu-group')}>
                    <div
                        className={cx('menu-parent', { active: location.pathname.startsWith('/admin/discount') })}
                        onClick={() => toggleSubmenu('discount')}
                    >
                        <div className={cx('parent-content')}>
                            <LuPercent className={cx('icon')} />
                            <span className={cx('menu-text')}>Khuyến mãi</span>
                        </div>
                        {openMenus['discount'] ? (
                            <LuChevronDown className={cx('icon')} />
                        ) : (
                            <LuChevronRight className={cx('icon')} />
                        )}
                    </div>

                    <div className={cx('submenu', { open: openMenus['discount'] })}>
                        <NavLink
                            to="/admin/discount-manager"
                            className={({ isActive }) => cx('submenu-item', { active: isActive })}
                        >
                            <LuListTree className={cx('icon')} />
                            <span className={cx('menu-text')}>Mã giảm giá</span>
                        </NavLink>
                    </div>
                </div>
            </div>

            <div className={cx('bottom')}>
                <NavLink to="/admin/profile" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <LuUserRound className={cx('icon')} />
                    <span className={cx('menu-text')}>Profile</span>
                </NavLink>
                <button
                    className={cx('menu-item', 'logout')}
                    onClick={() => {
                        localStorage.clear();
                        window.location.href = '/login';
                    }}
                >
                    <LuLogOut className={cx('icon')} />
                    <span className={cx('menu-text')}>Đăng xuất</span>
                </button>
            </div>
        </div>
    );
}

Sidebar.propTypes = {
    isCollapsed: PropTypes.bool,
    onToggle: PropTypes.func.isRequired,
};

export default Sidebar;
