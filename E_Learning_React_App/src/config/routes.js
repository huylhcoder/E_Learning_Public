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

    //Admin routes
    courseManager: '/course-manager',
};

export default routes;
