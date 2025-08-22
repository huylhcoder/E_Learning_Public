import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import { useState } from 'react';
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
            </div>
        </div>
    );
}

AdminLayout.propTypes = {
    children: PropTypes.node.isRequired,
};

export default AdminLayout;
