function SortCourse({ setSearchParams }) {
    const handleSort = (isAsc) => {
        setSearchParams((prev) => {
            const newParams = new URLSearchParams(prev);
            newParams.set('priceASC', isAsc); // true hoặc false
            newParams.set('page', 0); // reset page khi thay đổi sort
            return newParams;
        });
    };

    return (
        <div className="d-flex float-end">
            <h6 className="m-auto px-3 text-success fw-bold">Sắp xếp</h6>
            <ul className="nav nav-pills border border-1">
                <li className="nav-item dropdown">
                    <span className="nav-link dropdown-toggle text-success" data-bs-toggle="dropdown" role="button">
                        Sắp xếp theo giá
                    </span>
                    <ul className="dropdown-menu">
                        <li>
                            <button
                                onClick={() => handleSort(true)}
                                className="dropdown-item"
                            >
                                Giá tăng dần
                            </button>
                        </li>
                        <li>
                            <button
                                onClick={() => handleSort(false)}
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
