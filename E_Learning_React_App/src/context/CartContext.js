import { createContext, useState, useEffect, useContext } from 'react';
import axios from '~/utils/CustomizeAxios';
import AuthContext from '~/context/AuthContext'; // 👈 thêm dòng này

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [cartItems, setCartItems] = useState([]);
    const authContext = useContext(AuthContext); // 👈 lấy trạng thái đăng nhập

    const refreshCart = async () => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const response = await axios.get('/cart', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setCartItems(response.data);
            } catch (err) {
                console.error('Lỗi khi reload giỏ hàng:', err);
            }
        } else {
            const localCart = localStorage.getItem('cartItems');
            if (localCart) {
                setCartItems(JSON.parse(localCart));
            } else {
                setCartItems([]); // reset khi không có giỏ hàng
            }
        }
    };

    useEffect(() => {
        refreshCart();
    }, [authContext.authenticated]); // 👈 re-fetch khi đăng nhập/đăng xuất

    return (
        <CartContext.Provider value={{ cartItems, setCartItems, refreshCart }}>
            {children}
        </CartContext.Provider>
    );
};

//Cách dùng
    //import { CartContext } from '~/context/CartContext'; // Thêm dòng này
    //const { refreshCart } = useContext(CartContext);
    //refreshCart(); // 👈 cập nhật context