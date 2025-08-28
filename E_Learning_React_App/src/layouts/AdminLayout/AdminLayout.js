//Thư viện
import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import { useState } from 'react';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Cũng bắt buộc

//Component 
import Sidebar from './component/Sidebar';
import styles from './AdminLayout.module.scss';

const cx = classNames.bind(styles);

function AdminLayout({ children }) {
    const [isCollapsed, setIsCollapsed] = useState(false);

    const toggleSidebar = () => {
        setIsCollapsed(!isCollapsed);
    };

    return (
        <div className={cx('wrapper')}>
            <div className={cx('container', { collapsed: isCollapsed })}>
                <Sidebar isCollapsed={isCollapsed} onToggle={toggleSidebar} />
                <div className={cx('content')}>{children}</div>
                <ToastContainer position="top-right" autoClose={3000} />
            </div>
        </div>
    );
}

AdminLayout.propTypes = {
    children: PropTypes.node.isRequired,
};

export default AdminLayout;
