//Thư viện
import React from 'react';
import { Link } from 'react-router-dom';

//Component
import Carousel from './Carousel/Carousel';
import BannerSection from './BannerSection/BannerSection';
import TopCourses from './TopCourses/TopCourses';
import VoucherSection from './VoucherSection/VoucherSection';
import MostFollowedCourses from './MostFollowedCourses/MostFollowedCourses';
import videoIntro from '~/assets/videos/about_us.mp4';
import Funfacts from './Funfacts/Funfacts';

const Home = () => {
    return (
        <div>
            {/* Carousel */}
            <Carousel />

            {/* Banner section */}
            <BannerSection />

            {/* Fun facts */}
            <Funfacts />

            {/* Top courses */}
            <TopCourses />

            {/* Voucher section */}
            <VoucherSection />

            {/* Video + text */}
            <div className="container mt-5 mb-5">
                <div className="row">
                    <div className="col-12 col-md-6">
                        <video src={videoIntro} width="100%" height="300" controls />
                    </div>
                    <div className="col-12 col-md-6">
                        <p>
                            Ngôn ngữ lập trình là các hệ thống cú pháp và quy tắc giúp lập trình viên giao tiếp với máy
                            tính và phát triển phần mềm, ứng dụng. Mỗi ngôn ngữ lập trình được thiết kế để giải quyết
                            những vấn đề cụ thể trong phát triển phần mềm, từ việc tạo ra các ứng dụng di động, website
                            cho đến các phần mềm hệ thống hoặc chương trình máy học.
                        </p>
                        <p>
                            Các ngôn ngữ lập trình như Python, JavaScript, Java, C++ và Ruby thường được sử dụng trong
                            các lĩnh vực khác nhau. Python là lựa chọn phổ biến cho người mới bắt đầu vì cú pháp dễ
                            hiểu, trong khi JavaScript là ngôn ngữ không thể thiếu trong phát triển web. Java và C++ lại
                            nổi bật trong việc xây dựng các ứng dụng lớn và yêu cầu hiệu suất cao.
                        </p>
                    </div>
                </div>
            </div>

            {/* Most followed courses */}
            <MostFollowedCourses />

            {/* Xem tất cả khóa học */}
            <div className="col-12 text-center mt-3">
                <Link
                    className="btn btn-theme btn-primary fs-3 fw-bold mb-5 mt-5"
                    to="/courses"
                    onClick={() => window.scrollTo(0, 0)}
                >
                    Xem tất cả khóa học
                </Link>
            </div>
        </div>
    );
};

export default Home;
