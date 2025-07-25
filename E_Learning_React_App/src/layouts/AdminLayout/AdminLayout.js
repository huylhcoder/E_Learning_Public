import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import Sidebar from '~/layouts/components/Sidebar';
import styles from './AdminLayout.module.scss';

const cx = classNames.bind(styles);

function AdminLayout({ children }) {
    return (
        <div className={cx('wrapper')}>
            <div className={cx('container')}>
                <Sidebar />
                {/* Cai phan nay no dong (thay doi) nen phai truyen cai children tu ngoai vao */}
                <div className={cx('content')}>{children}</div>
            </div>
        </div>
    );
}

AdminLayout.propTypes = {
    children: PropTypes.node.isRequired,
};

export default AdminLayout;
