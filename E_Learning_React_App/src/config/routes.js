const routes = {
    // Public routes
    home: '/',
    homeAlias: '/home',
    courseDetail: '/course/detail-course/:courseId',
    login: '/login',
    register: '/register',
    about: '/about',
    searchCourses: '/course/search', ///courses/search?category=frontend truyền được nhiều param (cái này không cần khai báo)

    //Private routes (requires login)
    myCourse: '/my-course',
    courseSectionDetail: '/courses/:courseId/sections/:sectionId',

    //Admin routes
    courseManager: '/course-manager',
};

export default routes;
