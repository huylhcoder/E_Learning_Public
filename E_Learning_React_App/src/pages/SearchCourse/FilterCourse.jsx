import { useEffect, useState } from 'react';
import styles from './SearchCourse.module.scss';
import classNames from 'classnames/bind';
import { FaStar } from 'react-icons/fa';
import axios from '~/utils/CustomizeAxios';

const cx = classNames.bind(styles);

function FilterCourse({ setSearchParams }) {
    const [ratedStar, setRatedStar] = useState(0);
    const [levelId, setLevelId] = useState('');
    const [categorySlug, setCategorySlug] = useState('');
    const [minPrice, setMinPrice] = useState(0);
    const [maxPrice, setMaxPrice] = useState(1000000);
    const [free, setFree] = useState(false);

    const [levels, setLevels] = useState([]);
    const [categories, setCategories] = useState([]);

    useEffect(() => {
         axios.get(`/course-level/list-course-level`).then((res) => setLevels(res.data));
        axios.get(`/category/list-category`).then((res) => setCategories(res.data));
    }, []);

    const handleApplyFilter = () => {
        const params = {
            page: 0,
            ratedStar,
            minPrice,
            maxPrice,
        };

        if (levelId) params.levelId = levelId;
        if (categorySlug) params.categorySlug = categorySlug;
        if (free) params.free = true;

        setSearchParams(params);
    };

    return (
        <div className={cx('filter-container', 'bg-white', 'p-4', 'rounded', 'shadow-sm')}>
            <h5 className="fw-bold mb-4">Bộ lọc khóa học</h5>

            {/* Đánh giá */}
            <div className="mb-4">
                <div className="fw-semibold mb-2">Đánh giá</div>
                {[5, 4, 3, 2, 1].map((star) => (
                    <div className="form-check" key={star}>
                        <input
                            className="form-check-input"
                            type="radio"
                            name="ratedStar"
                            id={`ratedStar${star}`}
                            checked={ratedStar === star}
                            onChange={() => setRatedStar(star)}
                        />
                        <label
                            className="form-check-label d-flex align-items-center gap-1"
                            htmlFor={`ratedStar${star}`}
                        >
                            {[...Array(star)].map((_, i) => (
                                <FaStar key={i} className="text-warning" />
                            ))}
                            <span className="ms-1">& trở lên</span>
                        </label>
                    </div>
                ))}
            </div>

            {/* Danh mục */}
            <div className="mb-4">
                <div className="fw-semibold mb-2">Danh mục</div>
                <select className="form-select" value={categorySlug} onChange={(e) => setCategorySlug(e.target.value)}>
                    <option value="">-- Chọn danh mục --</option>
                    {categories.map((cat) => (
                        <option key={cat.categoryId} value={cat.slug}>
                            {cat.name}
                        </option>
                    ))}
                </select>
            </div>

            {/* Trình độ */}
            <div className="mb-4">
                <div className="fw-semibold mb-2">Trình độ khó</div>
                <select className="form-select" value={levelId} onChange={(e) => setLevelId(e.target.value)}>
                    <option value="">-- Chọn độ khó --</option>
                    {levels.map((level) => (
                        <option key={level.id} value={level.id}>
                            {level.name}
                        </option>
                    ))}
                </select>
            </div>

            {/* Giá */}
            <div className="mb-4">
                <div className="fw-semibold mb-2">Khoảng giá (VNĐ)</div>
                <div className="d-flex gap-2 align-items-center">
                    <input
                        type="number"
                        className="form-control"
                        value={minPrice}
                        min={0}
                        onChange={(e) => setMinPrice(Number(e.target.value))}
                        placeholder="Tối thiểu"
                    />
                    <span>-</span>
                    <input
                        type="number"
                        className="form-control"
                        value={maxPrice}
                        min={minPrice}
                        onChange={(e) => setMaxPrice(Number(e.target.value))}
                        placeholder="Tối đa"
                    />
                </div>
                <small className="text-muted d-block mt-1">
                    {minPrice.toLocaleString()}đ - {maxPrice.toLocaleString()}đ
                </small>
            </div>

            {/* Miễn phí */}
            <div className="form-check mb-4">
                <input
                    className="form-check-input"
                    type="checkbox"
                    id="freeOnly"
                    checked={free}
                    onChange={(e) => setFree(e.target.checked)}
                />
                <label className="form-check-label" htmlFor="freeOnly">
                    Chỉ hiển thị khóa học miễn phí
                </label>
            </div>

            {/* Nút */}
            <button className="btn btn-primary w-100 fw-bold" onClick={handleApplyFilter}>
                <i className="fa-solid fa-filter me-2" />
                Áp dụng bộ lọc
            </button>
        </div>
    );
}

export default FilterCourse;
