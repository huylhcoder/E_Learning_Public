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

//config.routes.myCourse => myCourse: '/my-course'

const publicRoutes = [
    { path: config.routes.home, component: Home },
    { path: config.routes.homeAlias, component: Home },
    { path: config.routes.login, component: Login },
    { path: config.routes.searchCourses, component: SearchCourse },
    { path: config.routes.courseDetail, component: CourseDetail },
];

// User routes (đăng nhập mới xem được)
const privateRoutes = [{ path: config.routes.myCourse, component: MyCourse }];

// Admin routes (phải là admin)
const adminRoutes = [{ path: config.routes.courseManager, component: CourseManager, layout: AdminLayout }];

//Do no co 2 thang nen minh phai export tung cai le ra
export { publicRoutes, privateRoutes, adminRoutes };
