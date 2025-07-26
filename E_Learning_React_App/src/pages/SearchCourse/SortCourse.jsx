import { useSearchParams } from 'react-router-dom';

function SortCourse({ totalElements}) {
    const [searchParams, setSearchParams] = useSearchParams();

    const handleSort = (sortType) => {
        const newParams = new URLSearchParams(searchParams);

        // Xoá cả 2 trước rồi set đúng cái cần
        newParams.delete('priceASC');
        newParams.delete('priceDESC');

        if (sortType === 'asc') {
            newParams.set('priceASC', true);
        } else if (sortType === 'desc') {
            newParams.set('priceDESC', true);
        }

        newParams.set('page', 0); // reset page về 0 khi sort thay đổi
        setSearchParams(newParams);
    };

    return (
        <div className="d-flex float-end">
            <h6 className="m-auto px-3 text-primary fw-bold">Tìm thấy {totalElements} kết quả</h6>
            <ul className="nav nav-pills border border-1">
                <li className="nav-item dropdown">
                    <span className="nav-link dropdown-toggle text-dark" data-bs-toggle="dropdown" role="button">
                        Sắp xếp theo giá
                    </span>
                    <ul className="dropdown-menu">
                        <li>
                            <button
                                onClick={() => handleSort('asc')}
                                className="dropdown-item"
                            >
                                Giá tăng dần
                            </button>
                        </li>
                        <li>
                            <button
                                onClick={() => handleSort('desc')}
                                className="dropdown-item"
                            >
                                Giá giảm dần
                            </button>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    );
}

export default SortCourse;
