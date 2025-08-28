import React from "react";
import { Pagination } from "react-bootstrap";

const CategoryPagination = ({ total, perPage, currentPage, setCurrentPage }) => {
  const totalPages = Math.ceil(total / perPage);

  return (
    <Pagination>
      {Array.from({ length: totalPages }).map((_, i) => (
        <Pagination.Item
          key={i + 1}
          active={i + 1 === currentPage}
          onClick={() => setCurrentPage(i + 1)}
        >
          {i + 1}
        </Pagination.Item>
      ))}
    </Pagination>
  );
};

export default CategoryPagination;
