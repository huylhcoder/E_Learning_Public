import { useState } from 'react';
import styles from './SortCourse.module.scss';
import classNames from 'classnames/bind';

const cx = classNames.bind(styles);

function SortCourse({ totalElements, onSortChange }) {
    const [selectedSort, setSelectedSort] = useState(null);

    const handleSort = (sortType) => {
        // Nếu chọn lại loại đang được chọn, có thể reset hoặc giữ nguyên (tùy logic mong muốn)
        if (selectedSort === sortType) {
            // Có thể thêm logic reset ở đây nếu muốn
        }
        setSelectedSort(sortType);
        onSortChange(sortType); // Gửi event ra ngoài FE
    };

    // Xác định văn bản hiển thị cho nút dropdown
    const getDropdownText = () => {
        if (selectedSort === 'asc') return 'Giá: Tăng dần';
        if (selectedSort === 'desc') return 'Giá: Giảm dần';
        return 'Sắp xếp theo giá';
    };

    return (
        <div className="d-flex align-items-center justify-content-end mb-4">
            {/* Hiển thị số lượng kết quả */}
            <h6 className="m-auto me-3 text-secondary fw-bold fs-5">
                Tìm thấy <span className="text-primary">{totalElements}</span> kết quả
            </h6>
            
            {/* Khối Dropdown Sắp xếp */}
            <div className={cx('sort-dropdown-group', 'dropdown')}>
                <button
                    className={cx('sort-toggle', 'btn', 'btn-outline-secondary fs-5', 'dropdown-toggle', {
                        'btn-active': selectedSort !== null
                    })}
                    type="button"
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                >
                    <i className="bi bi-sort-down me-2"></i>
                    {getDropdownText()}
                </button>

                <ul className="dropdown-menu dropdown-menu-end">
                    <li>
                        <button
                            onClick={() => handleSort('asc')}
                            className={cx('dropdown-item', 'sort-item fs-5', { active: selectedSort === 'asc' })}
                        >
                            <i className="bi bi-arrow-up-circle me-2"></i> Giá tăng dần
                        </button>
                    </li>
                    <li>
                        <button
                            onClick={() => handleSort('desc')}
                            className={cx('dropdown-item', 'sort-item fs-5', { active: selectedSort === 'desc' })}
                        >
                            <i className="bi bi-arrow-down-circle me-2"></i> Giá giảm dần
                        </button>
                    </li>
                    {selectedSort && (
                        <>
                            <li><hr className="dropdown-divider"/></li>
                            <li>
                                <button
                                    onClick={() => handleSort(null)}
                                    className={cx('dropdown-item', 'sort-item', 'reset-item fs-5')}
                                >
                                    <i className="bi bi-x-circle me-2"></i> Bỏ sắp xếp
                                </button>
                            </li>
                        </>
                    )}
                </ul>
            </div>
        </div>
    );
}

export default SortCourse;
