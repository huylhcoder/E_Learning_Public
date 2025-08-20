//Thư viện
import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Cũng bắt buộc

import Header from '~/layouts/components/Header';
import Footer from '~/layouts/components/Footer';
import styles from './DefaultLayout.module.scss';
import { CartProvider } from '~/context/CartContext'; // Giả sử bạn đã tạo CartContext

const cx = classNames.bind(styles);

function DefaultLayout({ children }) {
    return (
        <CartProvider>
            <div className={cx('wrapper')}>
                <Header />
                <div className={cx('content', 'container', 'mt-3')}>{children}</div>
                <ToastContainer position="top-right" autoClose={3000} />
                <Footer />
            </div>
        </CartProvider>
    );
}

DefaultLayout.propTypes = {
    children: PropTypes.node.isRequired,
};

export default DefaultLayout;
