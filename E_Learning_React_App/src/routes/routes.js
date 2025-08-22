import config from '~/config';

// Layouts
import { AdminLayout } from '~/layouts';

// Pages
import Home from '~/pages/Home';
import CourseDetail from '~/pages/CourseDetail';
import CourseManager from '~/pages/CourseManager';
import MyCourse from '~/pages/MyCourse';
import Login from '~/pages/Login';
import SearchCourse from '~/pages/SearchCourse';
import Checkout from '~/pages/Checkout/Checkout';
import PaymentResult from '~/pages/PaymentResult/PaymentResult';
import Cart from '~/pages/Cart/Cart';
import Learning from '~/pages/Learning/Learning';
import Quiz from '~/pages/Quiz/Quiz';
//Admin Page
import Dashboard from '~/pages/Dashboard/Dashboard';
import CategoryManager from '~/pages/CategoryManager/CategoryManager';

//config.routes.myCourse => myCourse: '/my-course'

const publicRoutes = [
    { path: config.routes.home, component: Home },
    { path: config.routes.homeAlias, component: Home },
    { path: config.routes.login, component: Login },
    { path: config.routes.searchCourses, component: SearchCourse },
    { path: config.routes.courseDetail, component: CourseDetail },
    { path: config.routes.cart, component: Cart },
];

// Private routes (đăng nhập mới xem được)
// Tại '~/routes/PrivateRoute'; sử dụng introspection để kiểm tra token để trả về isValid
// Nếu không có token thì sẽ chuyển hướng về trang login
const privateRoutes = [
    { path: config.routes.myCourse, component: MyCourse },
    { path: config.routes.paymentResult, component: PaymentResult },
    { path: config.routes.checkout, component: Checkout },
    { path: config.routes.learning, component: Learning },
    { path: config.routes.quiz, component: Quiz },
];

// Admin routes (phải là admin)
const adminRoutes = [
    { path: config.routes.admin, component: Dashboard, layout: AdminLayout },
    { path: config.routes.dashboard, component: Dashboard, layout: AdminLayout },
    { path: config.routes.categoryManager, component: CategoryManager, layout: AdminLayout },
    { path: config.routes.courseManager, component: CourseManager, layout: AdminLayout },
];

//Do no co 2 thang nen minh phai export tung cai le ra
export { publicRoutes, privateRoutes, adminRoutes };
