import React from 'react';

const Pagination = ({ currentPage, totalPages, goToPage }) => {
    const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

    if (totalPages <= 1) return null;

    return (
        <nav className="d-flex justify-content-center mt-4">
            <ul className="pagination">
                <li className={`page-item ${currentPage === 1 && 'disabled'}`}>
                    <button className="page-link" onClick={() => goToPage(currentPage - 1)}>
                        ‹
                    </button>
                </li>
                {pages.map((p) => (
                    <li key={p} className={`page-item ${p === currentPage ? 'active' : ''}`}>
                        <button className="page-link" onClick={() => goToPage(p)}>
                            {p}
                        </button>
                    </li>
                ))}
                <li className={`page-item ${currentPage === totalPages && 'disabled'}`}>
                    <button className="page-link" onClick={() => goToPage(currentPage + 1)}>
                        ›
                    </button>
                </li>
            </ul>
        </nav>
    );
};

export default Pagination;
