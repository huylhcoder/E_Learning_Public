import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Component
import Header from '~/layouts/components/Header';
import UserSidebar from './component/UserSidebar';
import styles from './UserProfileLayout.module.scss';

//Context
import { CartProvider } from '~/context/CartContext'; // Giả sử bạn đã tạo CartContext
import { UserProvider } from '~/context/UserContext';

const cx = classNames.bind(styles);

function UserProfileLayout({ children }) {
    return (
        <CartProvider>
            <UserProvider>
                <div className={cx('wrapper')}>
                    <Header />
                    <section className="container my-4">
                        <div className="row">
                            {/* Sidebar */}
                            <div className="col-md-3 col-12 mb-4">
                                <UserSidebar />
                            </div>
                            {/* Content */}
                            <div className="col-md-9 col-12">
                                <div className={cx('content')}>{children}</div>
                            </div>
                        </div>
                    </section>
                    <ToastContainer position="top-right" autoClose={3000} />
                </div>
            </UserProvider>
        </CartProvider>
    );
}

UserProfileLayout.propTypes = {
    children: PropTypes.node.isRequired,
};

export default UserProfileLayout;
