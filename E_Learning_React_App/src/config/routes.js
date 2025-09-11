import ChangePassword from '~/pages/ChangePassword/ChangPassword';
import PaymentHistory from '~/pages/PaymentHistory/PaymentHistory';

const routes = {
    // Public routes
    home: '/',
    homeAlias: '/home',
    courseDetail: '/course/course-detail/:courseId',
    login: '/login',
    register: '/register',
    about: '/about',
    searchCourses: '/course/search', ///courses/search?category=frontend truyền được nhiều param (cái này không cần khai báo)
    cart: '/cart',

    //Private routes (requires login)
    myCourse: '/my-course',
    checkout: '/checkout', // checkout?courseId=123
    paymentResult: '/payment-result', // paymentResult?vnPaymentStatus=true || false
    learning: '/learning', //courseId=1 & sectionId=1
    quiz: '/quiz', // quiz?quizId=1
    profile: '/user/profile',
    changePassword: '/user/password',
    promotions: '/user/promotions',
    roadmap: '/user/roadmap',
    paymentHistory: '/user/payment-history',

    //Admin routes
    admin: '/admin',
    dashboard: '/admin/dashboard',
    categoryManager: '/admin/categories',
    courseManager: '/admin/course-manager',
    courseDetailManager: '/admin/course-detail-manager/:courseId',
    sectionDetailManager: '/admin/course-detail-manager/:courseId/section/:sectionId',
    discountManager: '/admin/discount-manager',
    userManager: '/admin/user-manager',
    commentManager: '/admin/comment-manager',
};

export default routes;
