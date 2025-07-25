import { createContext, useEffect, useState } from 'react';
import { introspect } from '~/services/AuthenticationService';

const AuthContext = createContext({});

export const AuthProvider = ({ children }) => {
    const [authenticated, setAuthenticated] = useState(false);

    const refresh = async () => {
        const token = localStorage.getItem('token');

        if (!token) {
            setAuthenticated(false);
            return;
        }

        try {
            const result = await introspect(token);
            setAuthenticated(result.valid);
        } catch (error) {
            console.error('Error introspecting token:', error);
            setAuthenticated(false);
        }
    };

    useEffect(() => {
        refresh();
    }, []);

    return <AuthContext.Provider value={{ authenticated, refresh }}>{children}</AuthContext.Provider>;
};

export default AuthContext;
