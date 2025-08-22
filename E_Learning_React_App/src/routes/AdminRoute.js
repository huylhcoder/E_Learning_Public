// AdminRoute.jsx
import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { introspect } from '~/services/AuthenticationService';
import LoadingSpinner from '~/components/LoadingSpinner';

const AdminRoute = ({ children }) => {
    const [authStatus, setAuthStatus] = useState({ loading: true, isValid: false, isAdmin: false });
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            setAuthStatus({ loading: false, isValid: false, isAdmin: false });
            return;
        }

        introspect()
            .then((data) => {
                setAuthStatus({
                    loading: false,
                    isValid: data?.valid ?? false,
                    isAdmin: data?.scope === 'ADMIN', // check scope thay vì role
                });
            })
            .catch(() => {
                setAuthStatus({ loading: false, isValid: false, isAdmin: false });
            });
    }, [token]);

    if (authStatus.loading) return <LoadingSpinner />;

    if (!authStatus.isValid) return <Navigate to="/login" replace />;
    if (!authStatus.isAdmin) return <Navigate to="/" replace />;

    return children;
};

export default AdminRoute;
