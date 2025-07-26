import { useSearchParams } from 'react-router-dom';

function Pagination({ page, totalPages, setSearchParams }) {
    const [searchParams] = useSearchParams();

    const handlePageChange = (newPage) => {
        const newParams = new URLSearchParams(searchParams);
        newParams.set('page', newPage);
        setSearchParams(newParams);

        // Cuộn lên đầu danh sách (mượt)
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    if (totalPages <= 1) return null;

    const getPageNumbers = () => {
        const visiblePages = 5;
        const half = Math.floor(visiblePages / 2);
        let start = Math.max(0, page - half);
        let end = Math.min(totalPages - 1, start + visiblePages - 1);

        if (end - start < visiblePages - 1) {
            start = Math.max(0, end - visiblePages + 1);
        }

        return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    };

    return (
        <nav>
            <ul className="pagination">
                {/* Previous button */}
                <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                    <button
                        className="page-link"
                        onClick={() => handlePageChange(page - 1)}
                        disabled={page === 0}
                    >
                        &laquo;
                    </button>
                </li>

                {/* Page numbers */}
                {getPageNumbers().map((p) => (
                    <li key={p} className={`page-item ${p === page ? 'active' : ''}`}>
                        <button className="page-link" onClick={() => handlePageChange(p)}>
                            {p + 1}
                        </button>
                    </li>
                ))}

                {/* Next button */}
                <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
                    <button
                        className="page-link"
                        onClick={() => handlePageChange(page + 1)}
                        disabled={page >= totalPages - 1}
                    >
                        &raquo;
                    </button>
                </li>
            </ul>
        </nav>
    );
}

export default Pagination;
