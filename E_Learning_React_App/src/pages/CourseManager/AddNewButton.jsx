import React from 'react';

const AddNewButton = ({ onAdd }) => (
  <button className="btn btn-primary fs-4" onClick={onAdd}>
    <i className="fas fa-plus"></i> Thêm mới khóa học
  </button>
);

export default React.memo(AddNewButton);
