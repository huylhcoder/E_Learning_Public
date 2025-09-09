import config from '~/config';

// Layouts
import { AdminLayout, UserProfileLayout } from '~/layouts';

// Pages
import Home from '~/pages/Home';
import CourseDetail from '~/pages/CourseDetail';
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
import CourseManager from '~/pages/CourseManager';
import CourseDetailManager from '~/pages/CourseDetailManager/CourseDetailManager';
import SectionDetailManager from '~/pages/SectionDetailManager/SectionDetailManager';
import DiscountManager from '~/pages/DiscountManager/DiscountManager';
import Profile from '~/pages/UserProfile/UserProfile';
import ChangePassword from '~/pages/ChangePassword/ChangPassword';
import MyVoucher from '~/pages/MyVoucher/MyVoucher';

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
    { path: config.routes.profile, component: Profile, layout: UserProfileLayout },
    { path: config.routes.changePassword, component: ChangePassword, layout: UserProfileLayout },
    { path: config.routes.promotions, component: MyVoucher, layout: UserProfileLayout },
];

// Admin routes (phải là admin)
const adminRoutes = [
    { path: config.routes.admin, component: Dashboard, layout: AdminLayout },
    { path: config.routes.dashboard, component: Dashboard, layout: AdminLayout },
    { path: config.routes.categoryManager, component: CategoryManager, layout: AdminLayout },
    { path: config.routes.courseManager, component: CourseManager, layout: AdminLayout },
    { path: config.routes.courseDetailManager, component: CourseDetailManager, layout: AdminLayout },
    { path: config.routes.sectionDetailManager, component: SectionDetailManager, layout: AdminLayout },
    { path: config.routes.discountManager, component: DiscountManager, layout: AdminLayout },
];

//Do no co 2 thang nen minh phai export tung cai le ra
export { publicRoutes, privateRoutes, adminRoutes };
