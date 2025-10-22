import { useEffect, useState, useRef } from 'react';
import { toast } from 'react-toastify';
import { FaPlus, FaSave, FaDownload, FaTrash, FaPencilAlt, FaUpload } from 'react-icons/fa';
import { Button } from 'react-bootstrap';

//Component
import axios from '~/utils/CustomizeAxios';
import sampleExcel from '~/assets/file/import_test.xlsx';

const QuizManager = ({ sectionId, token }) => {
    const [testId, setTestId] = useState(0);
    const [countdownTimer, setCountdownTimer] = useState(0);
    const [listQuestion, setListQuestion] = useState([]);
    const [questionDetail, setQuestionDetail] = useState(null);
    const [questionIdToDelete, setQuestionIdToDelete] = useState(null);
    const [newQuestion, setNewQuestion] = useState({
        contents: '',
        listAnswerDTO: [{ text: '', correct: false }],
    });

    const fileInputRef = useRef();
    let timeoutRef = useRef(null);

    // // Lấy bài test
    // const getTest = async () => {
    //     try {
    //         const resp = await axios.get(`/section-manager/${sectionId}/test-manager`, {
    //             headers: { Authorization: `Bearer ${token}` },
    //         });
    //         setTestId(resp?.data?.testID);
    //         setCountdownTimer(resp?.data?.countdownTimer);
    //         setListQuestion(resp?.data?.listQuestion);
    //     } catch (err) {
    //         setListQuestion([]);
    //         console.error('Không thể load bài kiểm tra', err);
    //     }
    // };

    // Lấy bài test
    const getTest = async () => {
        try {
            const resp = await axios.get(`/section-manager/${sectionId}/test-manager`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            // API đã được sửa để trả về body với testID = 0 khi chưa có test.
            // Dù là 200 OK, chúng ta vẫn nhận được resp.data và xử lý bình thường.
            setTestId(resp?.data?.testID);
            setCountdownTimer(resp?.data?.countdownTimer);
            setListQuestion(resp?.data?.listQuestion);
        } catch (err) {
            // Khối catch này chỉ bắt các lỗi không mong muốn (ví dụ: lỗi mạng, 500 Internal Server Error)
            // Vì 404 khi không có test đã được chuyển thành 200 OK với testID=0 ở backend.
            // KHÔNG cần setTestId(0) ở đây nữa vì nó đã được set thông qua resp.data ở khối try.
            console.error('Lỗi khi tải dữ liệu bài kiểm tra:', err);
            // Có thể thêm toast.error cho người dùng nếu đây là lỗi server thực sự
        }
    };

    useEffect(() => {
        getTest();
    }, [sectionId]);

    // Cập nhật countdownTimer
    const updateCountdownTimer = async () => {
        try {
            await axios.post(
                `/section-manager/${sectionId}/update-countdown-timer/${testId}/${countdownTimer}`,
                {},
                { headers: { Authorization: `Bearer ${token}` } },
            );
            toast.success('Cập nhật thành công!');
            getTest();
        } catch (err) {
            toast.error('Thời gian phải >= 0 và là số');
        }
    };

    // Upload file quiz
    const uploadFile = async () => {
        const file = fileInputRef.current?.files[0];
        if (!file) {
            toast.warning('Vui lòng chọn file!');
            return;
        }
        try {
            const formData = new FormData();
            formData.append('file', file);
            await axios.post(`/section-manager/${sectionId}/test/${testId}/import-quiz`, formData, {
                headers: { Authorization: `Bearer ${token}` },
            });
            toast.success('Tải file thành công!');
            getTest();
        } catch (err) {
            toast.error('Lỗi khi tải file');
        }
    };

    // Lấy chi tiết câu hỏi
    const showQuestionDetail = async (questionId) => {
        try {
            const resp = await axios.get(`/section-manager/${sectionId}/question-detail/${questionId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setQuestionDetail(resp.data);
        } catch (err) {
            setQuestionDetail(null);
        }
    };

    // Thêm đáp án mới
    const addAnswer = async (questionId) => {
        try {
            await axios.post(
                `/section-manager/${sectionId}/question-detail/${questionId}/add-answer`,
                {},
                { headers: { Authorization: `Bearer ${token}` } },
            );
            showQuestionDetail(questionId);
        } catch {
            toast.error('Lỗi khi thêm đáp án');
        }
    };

    // Xóa đáp án
    const removeAnswer = async (questionId, answerId) => {
        try {
            await axios.delete(
                `/section-manager/${sectionId}/question-detail/${questionId}/remove-answer/${answerId}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                },
            );
            showQuestionDetail(questionId);
            getTest();
            toast.success('Xóa đáp án thành công');
        } catch {
            toast.error('Lỗi khi xóa đáp án');
        }
    };

    // Cập nhật nội dung câu hỏi (debounce 2s)
    // const onQuestionChange = (questionId, contents) => {
    //     setQuestionDetail((prev) => ({ ...prev, contents }));
    //     if (timeoutRef.current) clearTimeout(timeoutRef.current);
    //     timeoutRef.current = setTimeout(async () => {
    //         try {
    //             await axios.put(
    //                 `/section-manager/${sectionId}/question-detail/${questionId}`,
    //                 { contents },
    //                 {
    //                     headers: {
    //                         Authorization: `Bearer ${token}`,
    //                         'Content-Type': 'application/json',
    //                     },
    //                 },
    //             );
    //             toast.success('Cập nhật câu hỏi thành công');
    //         } catch {
    //             toast.error('Lỗi khi cập nhật câu hỏi');
    //         }
    //     }, 2000);
    // };

    // // Cập nhật nội dung đáp án (debounce 2s)
    // const onAnswerChange = (questionId, answer) => {
    //     if (timeoutRef.current) clearTimeout(timeoutRef.current);
    //     timeoutRef.current = setTimeout(async () => {
    //         try {
    //             await axios.put(
    //                 `/section-manager/${sectionId}/question-detail/${questionId}/update-answer/${answer.answerId}`,
    //                 { content: answer.text, isCorrect: answer.correct },
    //                 {
    //                     headers: {
    //                         Authorization: `Bearer ${token}`,
    //                         'Content-Type': 'application/json',
    //                     },
    //                 },
    //             );
    //         } catch {
    //             toast.error('Lỗi khi cập nhật đáp án');
    //         }
    //     }, 2000);
    // };

    // // Cập nhật đáp án đúng
    // const updateCorrectAnswer = async (questionId, selectedAnswer) => {
    //     const updatedList = questionDetail.listAnswerDTO.map((a) => ({
    //         ...a,
    //         correct: a.answerId === selectedAnswer.answerId,
    //     }));
    //     setQuestionDetail((prev) => ({ ...prev, listAnswerDTO: updatedList }));
    //     try {
    //         await axios.put(
    //             `/section-manager/${sectionId}/question-detail/${questionId}/update-anwer-correct`,
    //             updatedList,
    //             {
    //                 headers: {
    //                     Authorization: `Bearer ${token}`,
    //                     'Content-Type': 'application/json',
    //                 },
    //             },
    //         );
    //         getTest();
    //     } catch {
    //         toast.error('Lỗi khi cập nhật đáp án đúng');
    //     }
    // };

    // Xóa câu hỏi
    const confirmDeleteQuestion = async () => {
        if (!questionIdToDelete) return;
        try {
            await axios.delete(`/section-manager/${sectionId}/question-detail/${questionIdToDelete}/remove-question`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            getTest();
            toast.success('Xóa câu hỏi thành công');
        } catch {
            toast.error('Lỗi khi xóa câu hỏi');
        }
    };

    // Tạo test mới
    const addTest = async () => {
        try {
            await axios.post(
                `/section-manager/${sectionId}/add-test`,
                {},
                { headers: { Authorization: `Bearer ${token}` } },
            );
            toast.success('Tạo bài kiểm tra thành công');
            getTest();
        } catch {
            toast.error('Lỗi khi tạo test');
        }
    };

    return (
        <div className="mt-3">
            {/* Nếu chưa có test */}
            {testId === 0 ? (
                <button className="btn btn-primary fs-5" onClick={addTest}>
                    <FaPlus /> Tạo bài kiểm tra
                </button>
            ) : (
                <>
                    <span className="text-primary fs-3 fw-bold mb-3">
                        Tổng số lượng câu hỏi: {listQuestion?.length}
                    </span>
                    <div className="d-flex align-items-center mb-3">
                        <label className="me-2">Đồng hồ đếm ngược (giây):</label>
                        <input
                            type="number"
                            value={countdownTimer}
                            onChange={(e) => setCountdownTimer(e.target.value)}
                            className="form-control fs-4"
                            style={{ width: 200 }}
                        />
                        <button className="btn btn-primary ms-2 ps-2 pe-2 fs-5" onClick={updateCountdownTimer}>
                            <FaSave /> Lưu
                        </button>
                        {/* <button className="btn btn-primary ms-2 ps-2 pe-2 fs-5" onClick={updateCountdownTimer}>
                            <FaUpload /> Import Excel
                        </button> */}
                        <button
                            className="btn btn-primary ms-2 ps-2 pe-2 fs-5"
                            data-bs-toggle="modal"
                            data-bs-target="#importModal"
                        >
                            <FaUpload /> Import Excel
                        </button>
                        {/* Modal import file */}
                        <div className="modal fade" id="importModal" tabIndex="-1">
                            <div className="modal-dialog">
                                <div className="modal-content">
                                    <div className="modal-header">
                                        <h5 className="modal-title">Import câu hỏi từ Excel</h5>
                                        <button
                                            type="button"
                                            className="btn-close"
                                            data-bs-dismiss="modal"
                                            aria-label="Close"
                                        ></button>
                                    </div>
                                    <div className="modal-body">
                                        <input
                                            type="file"
                                            ref={fileInputRef}
                                            accept=".xlsx, .xls"
                                            className="form-control"
                                        />
                                    </div>
                                    <div className="modal-footer">
                                        <button className="btn btn-secondary" data-bs-dismiss="modal">
                                            Đóng
                                        </button>
                                        <button
                                            className="btn btn-success"
                                            onClick={uploadFile}
                                            data-bs-dismiss="modal"
                                        >
                                            <FaUpload /> Tải lên
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        {/* Nút tải mẫu */}
                        <a href={sampleExcel} className="btn btn-primary ms-2 ps-2 pe-2 fs-5" download>
                            <FaDownload /> Tải Mẫu Excel
                        </a>
                    </div>

                    {/* Danh sách câu hỏi */}

                    {listQuestion?.map((q, idx) => (
                        <div key={q.questionId} className="card mt-3 mb-3">
                            <div className="card-body">
                                <div className="d-flex justify-content-between">
                                    <span className="text-dark fs-4 fw-bold">
                                        {idx + 1}. {q.contents}
                                    </span>
                                    <div>
                                        <button
                                            className="btn btn-sm btn-warning me-2"
                                            onClick={() => showQuestionDetail(q.questionId)}
                                            data-bs-toggle="modal"
                                            data-bs-target="#editModal"
                                        >
                                            <FaPencilAlt /> Chi tiết
                                        </button>
                                        <button
                                            className="btn btn-sm btn-danger"
                                            onClick={() => setQuestionIdToDelete(q.questionId)}
                                            data-bs-toggle="modal"
                                            data-bs-target="#deleteModal"
                                        >
                                            <FaTrash /> Xóa
                                        </button>
                                    </div>
                                </div>
                                {/* Đáp án */}
                                <div className="row mt-2">
                                    {q.listAnswerDTO.map((ans) => (
                                        <div key={ans.answerId} className="col-6">
                                            <div className="form-check">
                                                <input
                                                    type="radio"
                                                    className="form-check-input"
                                                    checked={ans.correct}
                                                    disabled
                                                />
                                                <label className="form-check-label">{ans.text}</label>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    ))}

                    {/* Modal chỉnh sửa câu hỏi */}
                    <div className="modal fade" id="editModal" tabIndex="-1">
                        <div className="modal-dialog modal-lg">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <h5 className="modal-title">Chỉnh sửa câu hỏi</h5>
                                    <button
                                        type="button"
                                        className="btn-close"
                                        data-bs-dismiss="modal"
                                        aria-label="Close"
                                    ></button>
                                </div>
                                <div className="modal-body">
                                    {questionDetail ? (
                                        <>
                                            {/* Nội dung câu hỏi */}
                                            <div className="mb-3">
                                                <label className="form-label fw-bold">Nội dung câu hỏi</label>
                                                <textarea
                                                    className="form-control"
                                                    rows="3"
                                                    value={questionDetail.contents}
                                                    onChange={(e) =>
                                                        setQuestionDetail((prev) => ({
                                                            ...prev,
                                                            contents: e.target.value,
                                                        }))
                                                    }
                                                />
                                            </div>

                                            {/* Danh sách đáp án */}
                                            <div className="mb-3">
                                                <label className="form-label fw-bold">Danh sách đáp án</label>
                                                {questionDetail.listAnswerDTO.map((ans, index) => (
                                                    <div key={ans.answerId} className="d-flex align-items-center mb-2">
                                                        <input
                                                            type="radio"
                                                            className="form-check-input me-2"
                                                            name="correctAnswer"
                                                            checked={ans.correct}
                                                            onChange={() =>
                                                                setQuestionDetail((prev) => ({
                                                                    ...prev,
                                                                    listAnswerDTO: prev.listAnswerDTO.map((a) =>
                                                                        a.answerId === ans.answerId
                                                                            ? { ...a, correct: true }
                                                                            : { ...a, correct: false },
                                                                    ),
                                                                }))
                                                            }
                                                        />
                                                        <input
                                                            type="text"
                                                            className="form-control"
                                                            value={ans.text}
                                                            onChange={(e) =>
                                                                setQuestionDetail((prev) => ({
                                                                    ...prev,
                                                                    listAnswerDTO: prev.listAnswerDTO.map((a) =>
                                                                        a.answerId === ans.answerId
                                                                            ? { ...a, text: e.target.value }
                                                                            : a,
                                                                    ),
                                                                }))
                                                            }
                                                        />
                                                        <button
                                                            className="btn btn-sm btn-danger ms-2"
                                                            onClick={() =>
                                                                removeAnswer(questionDetail.questionId, ans.answerId)
                                                            }
                                                        >
                                                            <FaTrash />
                                                        </button>
                                                    </div>
                                                ))}
                                                <button
                                                    className="btn btn-sm btn-success mt-2"
                                                    onClick={() => addAnswer(questionDetail.questionId)}
                                                >
                                                    <FaPlus /> Thêm đáp án
                                                </button>
                                            </div>
                                        </>
                                    ) : (
                                        <p>Đang tải...</p>
                                    )}
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">
                                        Đóng
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn-primary"
                                        onClick={async () => {
                                            try {
                                                // Cập nhật nội dung câu hỏi
                                                await axios.put(
                                                    `/section-manager/${sectionId}/question-detail/${questionDetail.questionId}`,
                                                    { contents: questionDetail.contents },
                                                    {
                                                        headers: {
                                                            Authorization: `Bearer ${token}`,
                                                            'Content-Type': 'application/json',
                                                        },
                                                    },
                                                );

                                                // Cập nhật danh sách đáp án
                                                for (let ans of questionDetail.listAnswerDTO) {
                                                    await axios.put(
                                                        `/section-manager/${sectionId}/question-detail/${questionDetail.questionId}/update-answer/${ans.answerId}`,
                                                        { content: ans.text, isCorrect: ans.correct },
                                                        {
                                                            headers: {
                                                                Authorization: `Bearer ${token}`,
                                                                'Content-Type': 'application/json',
                                                            },
                                                        },
                                                    );
                                                }

                                                toast.success('Cập nhật thành công!');
                                                getTest();
                                            } catch {
                                                toast.error('Lỗi khi cập nhật câu hỏi');
                                            }
                                        }}
                                        data-bs-dismiss="modal"
                                    >
                                        <FaSave /> Lưu thay đổi
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Modal xác nhận xóa */}
                    <div className="modal fade" id="deleteModal" tabIndex="-1">
                        <div className="modal-dialog">
                            <div className="modal-content">
                                <div className="modal-body">Bạn có chắc muốn xóa câu hỏi này không?</div>
                                <div className="modal-footer">
                                    <button className="btn btn-secondary" data-bs-dismiss="modal">
                                        Hủy
                                    </button>
                                    <button
                                        className="btn btn-danger"
                                        onClick={confirmDeleteQuestion}
                                        data-bs-dismiss="modal"
                                    >
                                        Xóa
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default QuizManager;
