import classNames from 'classnames/bind';
import { FaStar } from 'react-icons/fa';
import { useEffect, useState } from 'react';
import axios from '~/utils/CustomizeAxios';

import styles from './Funfacts.module.scss';

const cx = classNames.bind(styles);

const Funfacts = () => {
    const [funFactsData, setFunFactsData] = useState({
        totalUsers: 0,
        totalCourses: 0,
        totalCategories: 0,
        averageRating: 0,
    });

    useEffect(() => {
        const fetchFunFacts = async () => {
            try {
                const res = await axios.get('/course/fun-fact');
                setFunFactsData(res.data);
                console.log(res.data);
            } catch (error) {
                console.error('Lỗi khi lấy dữ liệu fun-fact:', error);
            }
        };

        fetchFunFacts();
    }, []);

    const funFacts = [
        {
            value: funFactsData.totalUsers,
            label: 'Học viên đã tham gia',
            style: 'style-1',
        },
        {
            value: funFactsData.totalCourses,
            label: 'Khóa học',
            style: 'style-2',
        },
        {
            value: funFactsData.totalCategories,
            label: 'Tổng số danh mục',
            style: 'style-4',
        },
        {
            value: funFactsData.averageRating,
            label: 'Trung bình đánh giá',
            style: 'style-3',
            isStar: true,
        },
    ];

    return (
        <section className={cx('fun-facts-section', 'mb-5')}>
            <div className="container mb-3">
                <div className={cx('box', 'py-2')}>
                    <div className="row text-center">
                        {funFacts.map((fact, idx) => (
                            <div className="col-md-6 col-lg-3" key={idx}>
                                <div className={cx('fun-facts-item')}>
                                    <h2 className={cx(fact.style)}>
                                        {fact.value}
                                        {fact.isStar && <FaStar className="ms-2 text-warning" />}
                                    </h2>
                                    <p>{fact.label}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </section>
    );
};

export default Funfacts;
