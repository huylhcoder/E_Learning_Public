import React from 'react';

const Tabs = ({ activeTab, setActiveTab }) => (
  <ul className="nav nav-tabs" role="tablist">
    <li className="nav-item" role="presentation">
      <button
        className={`nav-link ${activeTab === 'posted' ? 'active' : ''}`}
        type="button"
        onClick={() => setActiveTab('posted')}
      >
        Khóa Học Đã Đăng
      </button>
    </li>
    <li className="nav-item" role="presentation">
      <button
        className={`nav-link ${activeTab === 'draft' ? 'active' : ''}`}
        type="button"
        onClick={() => setActiveTab('draft')}
      >
        Bản Nháp
      </button>
    </li>
  </ul>
);

export default React.memo(Tabs);
