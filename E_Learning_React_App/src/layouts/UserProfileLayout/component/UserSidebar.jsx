//Libs
import { NavLink } from 'react-router-dom';
import { useContext } from 'react';
import { LuUser, LuLock, LuTicketPercent } from 'react-icons/lu';
import { RiRoadMapLine } from 'react-icons/ri';
import { FaCreditCard } from "react-icons/fa";
import classNames from 'classnames/bind';

//Components
import styles from './UserSidebar.module.scss';
import avatarDefault from '~/assets/images/avatar-default.jpg';

//Context
import { UserContext } from '~/context/UserContext';

const cx = classNames.bind(styles);

function UserSidebar() {
    const { userProfile, avatar } = useContext(UserContext);

    return (
        <div className={cx('sidebar', 'p-3')}>
            <div className={cx('avatar-section', 'text-center mb-4')}>
                <img src={avatar || avatarDefault} alt="avatar" className={cx('avatar', 'rounded-circle mb-2')} />
                <p className="fs-4 fw-bolder mb-0">{userProfile?.name || 'Unknown'}</p>
                <small className="text-muted">{userProfile?.email || ''}</small>
            </div>

            {/* Menu */}
            <nav className={cx('menu')}>
                <NavLink to="/user/profile" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <LuUser className={cx('icon')} />
                    Thông tin cá nhân
                </NavLink>

                <NavLink to="/user/password" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <LuLock className={cx('icon')} />
                    Đổi mật khẩu
                </NavLink>
                <NavLink to="/user/roadmap" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <RiRoadMapLine className={cx('icon')} />
                    Roadmap
                </NavLink>
                <NavLink to="/user/payment-history" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <FaCreditCard  className={cx('icon')} />
                    Lịch sử thanh toán
                </NavLink>
                <NavLink to="/user/promotions" className={({ isActive }) => cx('menu-item', { active: isActive })}>
                    <LuTicketPercent className={cx('icon')} />
                    Khuyến mãi của tôi
                </NavLink>
            </nav>
        </div>
    );
}

export default UserSidebar;
