//Thư viện
import { Fragment } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// Các route công khai, riêng tư và quản trị
import { publicRoutes, privateRoutes, adminRoutes } from '~/routes';
import { DefaultLayout } from '~/layouts';
//import { DefaultLayout, AdminLayout } from '~/layouts';
import PrivateRoute from '~/routes/PrivateRoute';
import AdminRoute from '~/routes/AdminRoute';

function App() {
    const renderRoute = (route, index, Wrapper = Fragment) => {
        const Page = route.component;
        let Layout = DefaultLayout;

        if (route.layout) Layout = route.layout;
        else if (route.layout === null) Layout = Fragment;

        return (
            <Route
                key={index}
                path={route.path}
                element={
                    <Wrapper>
                        <Layout>
                            <Page />
                        </Layout>
                    </Wrapper>
                }
            />
        );
    };

    return (
        <Router>
            <div className="App">
                <Routes>
                    {publicRoutes.map((route, index) => renderRoute(route, index))}

                    {privateRoutes.map((route, index) => renderRoute(route, index, PrivateRoute))}

                    {adminRoutes.map((route, index) => renderRoute(route, index, AdminRoute))}
                </Routes>
            </div>
        </Router>
    );
}

export default App;
