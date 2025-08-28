import React from 'react';
import { Link } from 'react-router-dom';

const Breadcrumb = () => (
    <ul className="list-unstyled d-flex align-items-center gap-2 mb-3">
        <li>
            <Link to="/admin/course-manager" className="fs-3 link-primary ">
                Danh sách khóa học &gt;
            </Link>
        </li>
    </ul>
);
export default React.memo(Breadcrumb);
