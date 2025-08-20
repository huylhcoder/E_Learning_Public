import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { introspect} from '~/services/AuthenticationService';
import LoadingSpinner from '~/components/LoadingSpinner';

const PrivateRoute = ({ children }) => {
    const [isValidToken, setIsValidToken] = useState(null);
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            setIsValidToken(false);
            return;
        }
        introspect()
            .then((data) => {
                setIsValidToken(data?.valid ?? false);
            })
            .catch(() => setIsValidToken(false));
    }, [token]);

    if (isValidToken === null) return <LoadingSpinner />;
    return isValidToken ? children : <Navigate to="/login" />;
};

export default PrivateRoute;
