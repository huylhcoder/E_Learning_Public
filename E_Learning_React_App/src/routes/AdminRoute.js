import { Navigate } from 'react-router-dom';

const AdminRoute = ({ children }) => {
    const user = JSON.parse(localStorage.getItem('user'));
    const isAdmin = user?.role === 'admin';

    return isAdmin ? children : <Navigate to="/" replace />;
};

export default AdminRoute;
