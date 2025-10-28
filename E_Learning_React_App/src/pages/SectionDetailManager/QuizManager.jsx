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
    const [validationErrors, setValidationErrors] = useState({});

    const [isNewQuestion, setIsNewQuestion] = useState(false);

    const fileInputRef = useRef();
    const inputRefs = useRef([]); // lưu danh sách ref của các input đáp án

    useEffect(() => {
        getTest();
    }, [sectionId]);

    useEffect(() => {
        if (questionDetail?.listAnswerDTO?.length) {
            inputRefs.current = inputRefs.current.slice(0, questionDetail.listAnswerDTO.length);
        }
    }, [questionDetail]);

    // **************************************************************************************
    // CÁC HÀM XỬ LÝ DỮ LIỆU CHUNG
    // **************************************************************************************

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

    // Lấy bài test và danh sách câu hỏi
    const getTest = async () => {
        try {
            const resp = await axios.get(`/section-manager/${sectionId}/test-manager`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setTestId(resp?.data?.testID);
            setCountdownTimer(resp?.data?.countdownTimer);
            setListQuestion(resp?.data?.listQuestion || []);
        } catch (err) {
            console.error('Lỗi khi tải dữ liệu bài kiểm tra:', err);
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

    // Chuẩn bị form cho việc THÊM MỚI câu hỏi
    const addNewQuestion = () => {
        const timestamp = Date.now();
        const newQuestion = {
            questionId: 0,
            contents: '',
            listAnswerDTO: [
                { answerId: timestamp, text: '', correct: true },
                { answerId: timestamp + 1, text: '', correct: false },
            ],
        };

        console.log('✅ Câu hỏi khởi tạo:', newQuestion);

        setIsNewQuestion(true);
        setQuestionDetail(newQuestion);
    };

    // Lấy chi tiết câu hỏi (Chế độ CHỈNH SỬA)
    const showQuestionDetail = async (questionId) => {
        setIsNewQuestion(false);
        try {
            const resp = await axios.get(`/section-manager/${sectionId}/question-detail/${questionId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setQuestionDetail(resp.data);
            console.log('Chi tiết câu hỏi: ' + JSON.stringify(resp.data.listAnswerDTO));
        } catch (err) {
            setQuestionDetail(null);
            toast.error('Không tìm thấy chi tiết câu hỏi.');
        }
    };

    // Thêm đáp án mới (API POST đáp án trống HOẶC cập nhật local)
    const addAnswer = async (questionId) => {
        if (questionId === 0) {
            // Trường hợp đang tạo mới (isNewQuestion = true) -> Chỉ thêm vào local state
            setQuestionDetail((prev) => ({
                ...prev,
                listAnswerDTO: [...prev.listAnswerDTO, { answerId: Date.now(), text: '', correct: false }],
            }));
            return;
        }

        // Trường hợp đang chỉnh sửa (isNewQuestion = false) -> Gọi API tạo đáp án trống
        // Sau đó gọi lại showQuestionDetail để tải đáp án với ID thực
        try {
            await axios.post(
                `/section-manager/${sectionId}/question-detail/${questionId}/add-answer`,
                {},
                { headers: { Authorization: `Bearer ${token}` } },
            );
            toast.success('Thêm đáp án thành công');
            showQuestionDetail(questionId); // Tải lại chi tiết để lấy ID mới
        } catch {
            toast.error('Lỗi khi thêm đáp án');
        }
    };

    // Xóa đáp án
    const removeAnswer = async (questionId, answerId) => {
        if (!questionDetail) return;

        if (questionDetail.listAnswerDTO.length <= 1) {
            toast.warning('Phải có ít nhất 1 đáp án.');
            return;
        }

        // Xóa local nếu đang tạo mới (questionId = 0) HOẶC nếu answerId là ID tạm thời (không phải số dương)
        if (questionId === 0 || !(answerId > 0)) {
            setQuestionDetail((prev) => {
                const newList = prev.listAnswerDTO.filter((a) => a.answerId !== answerId);

                const remainingCorrect = newList.find((a) => a.correct);
                if (!remainingCorrect && newList.length > 0) {
                    newList[0].correct = true;
                }

                return { ...prev, listAnswerDTO: newList };
            });
            return;
        }

        // Xóa API nếu đang chỉnh sửa (questionId > 0) và answerId là ID thực (> 0)
        try {
            await axios.delete(
                `/section-manager/${sectionId}/question-detail/${questionId}/remove-answer/${answerId}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                },
            );
            // Cập nhật lại state sau khi xóa API thành công
            setQuestionDetail((prev) => {
                const newList = prev.listAnswerDTO.filter((a) => a.answerId !== answerId);

                // Đảm bảo vẫn có 1 đáp án đúng sau khi xóa
                const remainingCorrect = newList.find((a) => a.correct);
                if (!remainingCorrect && newList.length > 0) {
                    newList[0].correct = true;
                    // CHÚ Ý: Cần cập nhật lại API nếu đáp án đúng bị thay đổi
                    // Trong trường hợp này, ta sẽ gọi getTest() và dựa vào saveOrUpdateQuestion để cập nhật sau.
                }

                return { ...prev, listAnswerDTO: newList };
            });
            getTest(); // Tải lại danh sách bên ngoài
            toast.success('Xóa đáp án thành công');
        } catch {
            toast.error('Lỗi khi xóa đáp án');
        }
    };

    // Xóa câu hỏi (dùng cho nút Xóa bên ngoài)
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

    // **************************************************************************************
    // HÀM XỬ LÝ CẬP NHẬT LOCAL STATE TRONG MODAL
    // **************************************************************************************

    const handleAnswerTextChange = (e, index) => {
        const newText = e.target.value;
        setQuestionDetail((prev) => {
            if (!prev) return prev;
            const newListAnswer = prev.listAnswerDTO.map((answer, i) => {
                if (i === index) {
                    return { ...answer, text: newText };
                }
                return answer;
            });
            return {
                ...prev,
                listAnswerDTO: newListAnswer,
            };
        });
    };

    const handleAnswerCorrectChange = (index) => {
        setQuestionDetail((prev) => {
            if (!prev) return prev;
            const newListAnswer = prev.listAnswerDTO.map((answer, i) => {
                return { ...answer, correct: i === index };
            });
            return {
                ...prev,
                listAnswerDTO: newListAnswer,
            };
        });
    };

    // **************************************************************************************
    // HÀM LƯU / CẬP NHẬT CHUNG
    // **************************************************************************************

    const saveOrUpdateQuestion = async () => {
        if (!questionDetail) return;

        let errors = {};

        // 🧩 Kiểm tra nội dung câu hỏi
        if (!questionDetail.contents.trim()) {
            errors.contents = 'Nội dung câu hỏi không được để trống';
        }

        // 🧩 Kiểm tra số lượng đáp án
        if (questionDetail.listAnswerDTO.length < 2) {
            errors.answers = 'Phải có ít nhất 2 đáp án';
        }

        // 🧩 Kiểm tra nội dung các đáp án
        const emptyAnswers = questionDetail.listAnswerDTO.some((a) => !a.text.trim());
        if (emptyAnswers) {
            errors.answerText = 'Không được để trống nội dung đáp án';
        }

        // 🧩 Kiểm tra đúng 1 đáp án đúng
        const correctCount = questionDetail.listAnswerDTO.filter((a) => a.correct).length;
        if (correctCount !== 1) {
            errors.correct = 'Phải có chính xác 1 đáp án đúng';
        }

        setValidationErrors(errors);

        if (Object.keys(errors).length > 0) {
            toast.error('Vui lòng kiểm tra lại thông tin trước khi lưu.');
            return;
        }

        try {
            if (isNewQuestion) {
                console.log('List câu hỏi khi tạo: ' + JSON.stringify(questionDetail.listAnswerDTO));

                const newQuestionDTO = {
                    contents: questionDetail.contents,
                    listAnswerDTO: questionDetail.listAnswerDTO.map((a) => ({
                        text: a.text,
                        correct: a.correct, // ✅ đổi từ isCorrect → correct
                    })),
                };

                try {
                    await axios.post(
                        `/section-manager/${sectionId}/test-manager/${testId}/add-question`,
                        newQuestionDTO,
                        {
                            headers: {
                                Authorization: `Bearer ${token}`,
                                'Content-Type': 'application/json',
                            },
                        },
                    );

                    toast.success('Thêm câu hỏi và đáp án thành công!');
                    getTest();
                } catch (error) {
                    console.error(error);
                    toast.error('Lỗi khi thêm câu hỏi hoặc đáp án!');
                }
            } else {
                const questionId = questionDetail.questionId;

                await axios.put(
                    `/section-manager/${sectionId}/question-detail/${questionId}`,
                    { contents: questionDetail.contents },
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            'Content-Type': 'application/json',
                        },
                    },
                );

                const updatePromises = questionDetail.listAnswerDTO.map((ans) => {
                    const answerData = { content: ans.text, isCorrect: ans.correct };
                    if (ans.answerId && ans.answerId > 0) {
                        return axios.put(
                            `/section-manager/${sectionId}/question-detail/${questionId}/update-answer/${ans.answerId}`,
                            answerData,
                            { headers: { Authorization: `Bearer ${token}` } },
                        );
                    } else {
                        return axios.post(
                            `/section-manager/${sectionId}/question-detail/${questionId}/add-answer-with-content`,
                            answerData,
                            { headers: { Authorization: `Bearer ${token}` } },
                        );
                    }
                });

                await Promise.all(updatePromises);
                toast.success('Cập nhật thành công!');
            }

            getTest();
        } catch (error) {
            console.error(error);
            toast.error('Lỗi khi lưu/cập nhật câu hỏi!');
        }
    };

    // **************************************************************************************
    // GIAO DIỆN (GIỮ NGUYÊN)
    // **************************************************************************************

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

                        {/* NÚT THÊM CÂU HỎI MỚI */}
                        <button
                            className="btn btn-success ms-2 ps-2 pe-2 fs-5"
                            onClick={addNewQuestion}
                            data-bs-toggle="modal"
                            data-bs-target="#editModal"
                        >
                            <FaPlus /> Thêm câu hỏi
                        </button>

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
                                            className="btn btn-primary"
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

                    {/* Modal chỉnh sửa/thêm mới câu hỏi */}
                    <div className="modal fade" id="editModal" tabIndex="-1">
                        <div className="modal-dialog modal-lg">
                            <div className="modal-content">
                                <div className="modal-header">
                                    {/* Tiêu đề động */}
                                    <h5 className="modal-title">
                                        {isNewQuestion ? 'Thêm mới câu hỏi' : 'Chỉnh sửa câu hỏi'}
                                    </h5>
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
                                                <label className="form-label fw-bold">
                                                    Danh sách đáp án (Tối thiểu 2)
                                                </label>
                                                {/* {questionDetail.listAnswerDTO.map((ans, index) => (
                                                    <div key={ans.answerId} className="d-flex align-items-center mb-2">
                                                        <input
                                                            type="radio"
                                                            className="form-check-input me-2"
                                                            name="correctAnswer"
                                                            checked={ans.correct}
                                                            onChange={() => handleAnswerCorrectChange(index)}
                                                        />
                                                        <input
                                                            type="text"
                                                            className="form-control"
                                                            value={ans.text}
                                                            onChange={(e) => handleAnswerTextChange(e, index)}
                                                        />
                                                        <button
                                                            className="btn btn-sm btn-danger ms-2"
                                                            onClick={() =>
                                                                removeAnswer(questionDetail.questionId, ans.answerId)
                                                            }
                                                            // Vô hiệu hóa nút xóa nếu chỉ còn 1 đáp án
                                                            disabled={questionDetail.listAnswerDTO.length <= 1}
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
                                                </button> */}
                                                {questionDetail.listAnswerDTO.map((ans, index) => (
                                                    <div key={ans.answerId} className="d-flex align-items-center mb-2">
                                                        <input
                                                            type="radio"
                                                            className="form-check-input me-2"
                                                            name="correctAnswer"
                                                            checked={ans.correct}
                                                            onChange={() => handleAnswerCorrectChange(index)}
                                                        />
                                                        <input
                                                            ref={(el) => (inputRefs.current[index] = el)}
                                                            type="text"
                                                            className="form-control"
                                                            value={ans.text}
                                                            onChange={(e) => handleAnswerTextChange(e, index)}
                                                            onKeyDown={(e) => {
                                                                if (e.key === 'Tab') {
                                                                    e.preventDefault();
                                                                    const next = inputRefs.current[index + 1];
                                                                    if (next) {
                                                                        next.focus(); // focus tới đáp án tiếp theo
                                                                    } else {
                                                                        // nếu đang ở ô cuối thì tự thêm đáp án mới
                                                                        addAnswer(questionDetail.questionId);
                                                                        setTimeout(() => {
                                                                            const newInput =
                                                                                inputRefs.current[index + 1];
                                                                            if (newInput) newInput.focus();
                                                                        }, 100);
                                                                    }
                                                                }
                                                            }}
                                                        />
                                                        <button
                                                            className="btn btn-sm btn-danger ms-2"
                                                            onClick={() =>
                                                                removeAnswer(questionDetail.questionId, ans.answerId)
                                                            }
                                                            disabled={questionDetail.listAnswerDTO.length <= 1}
                                                        >
                                                            <FaTrash />
                                                        </button>
                                                    </div>
                                                ))}
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
                                        onClick={saveOrUpdateQuestion}
                                        data-bs-dismiss="modal"
                                    >
                                        <FaSave /> {isNewQuestion ? 'Tạo mới' : 'Lưu thay đổi'}
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
