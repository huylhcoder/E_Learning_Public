import React from "react";
import { Button } from "react-bootstrap";
import { FaPlus, FaFileExport} from "react-icons/fa";

const CategoryToolbar = ({ filter, setFilter, onAdd }) => {
  return (
    <div className="d-flex mb-3 gap-2">
      <div className="input-group w-25">
        <input
          type="text"
          className="form-control"
          placeholder="Filter theo tên"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
        />
      </div>
      <Button variant="primary" onClick={onAdd}>
        <FaPlus /> Thêm danh mục
      </Button>
      <Button variant="success">
        <FaFileExport /> Export Excel
      </Button>
    </div>
  );
};

export default CategoryToolbar;
