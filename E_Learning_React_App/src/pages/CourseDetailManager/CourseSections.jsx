import { Link } from 'react-router-dom';

function CourseSections({ listSection, handleAddSection, handleDeleteSection }) {
    return (
        <div>
            <div className="d-flex justify-content-between mb-3">
                <h5>Danh sách phần</h5>
                <button className="btn btn-primary" onClick={handleAddSection}>
                    + Thêm phần mới
                </button>
            </div>

            <table className="table table-bordered">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên phần</th>
                        <th>Số bài học</th>
                        <th>Thời lượng</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {listSection.length === 0 ? (
                        <tr>
                            <td colSpan="5" className="text-center">
                                Chưa có phần nào
                            </td>
                        </tr>
                    ) : (
                        listSection.map((section) => (
                            <tr key={section.sectionId}>
                                <td>{section.sectionId}</td>
                                <td>{section.name}</td>
                                <td>{section.totalLessons || 0}</td>
                                <td>{section.totalDuration || '0'} phút</td>
                                <td>
                                    <Link
                                        to={`/admin/course-detail-manager/${section.courseId}/section/${section.sectionId}`}
                                        className="btn btn-sm btn-warning me-2"
                                    >
                                        Sửa
                                    </Link>
                                    <button
                                        className="btn btn-sm btn-danger"
                                        onClick={() => handleDeleteSection(section.sectionId)}
                                    >
                                        Xóa
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
}

export default CourseSections;
