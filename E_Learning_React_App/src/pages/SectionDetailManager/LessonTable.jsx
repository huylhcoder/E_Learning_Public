import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';

import { formatDuration } from '~/utils/format';

const LessonTable = ({ listLesson, handleEditLesson, handleDeleteLesson }) => {
    return (
        <div>
            <div className="d-flex justify-content-between mb-3">
                <button
                    className="btn btn-primary fs-5 mb-3"
                    onClick={() => handleEditLesson({ lessonId: 0, name: '', description: '' })}
                    data-bs-toggle="offcanvas"
                    data-bs-target="#lessonOffcanvas"
                >
                    <FaPlus className="me-1" /> Thêm bài học
                </button>
            </div>

            <div className="table-responsive">
                <table className="table">
                    <thead>
                        <tr>
                            <th>Bài học</th>
                            <th>Mô tả</th>
                            <th>Thời lượng</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {listLesson.map((lesson, index) => (
                            <tr key={lesson.lessonId}>
                                <td>
                                    Bài {index + 1}: {lesson.name}
                                </td>
                                <td>{lesson.description}</td>
                                <td>{formatDuration(lesson.lessionDuration) || 'Chưa có video'}</td>
                                <td>
                                    <button
                                        className="btn btn-sm btn-warning me-2"
                                        onClick={() => handleEditLesson(lesson)}
                                        data-bs-toggle="offcanvas"
                                        data-bs-target="#lessonOffcanvas"
                                    >
                                        <FaEdit />
                                    </button>
                                    <button
                                        className="btn btn-sm btn-danger"
                                        onClick={() => handleDeleteLesson(lesson.lessonId)}
                                    >
                                        <FaTrash />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default LessonTable;
