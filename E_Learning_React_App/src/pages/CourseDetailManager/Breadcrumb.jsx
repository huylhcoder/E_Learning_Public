import { Link } from 'react-router-dom';

function Breadcrumb({ courseName }) {
    return (
        <ul className="list-unstyled d-flex align-items-center gap-2 mb-5">
            <li className="fs-5">
                <Link to="/admin/course-manager" className='fs-3'>Danh sách khóa học &gt;</Link>
            </li>
            <li className="fs-3 text-primary">Danh sách phần của khóa: {courseName} &gt;</li>
        </ul>
    );
}
export default Breadcrumb;
