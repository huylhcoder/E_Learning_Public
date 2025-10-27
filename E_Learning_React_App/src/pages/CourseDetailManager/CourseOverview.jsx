import { useState, useEffect } from 'react';
import { Button, Form } from 'react-bootstrap';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import Tippy from '@tippyjs/react/headless';
import { FaPlus, FaChevronDown, FaTimes } from 'react-icons/fa';
import { FaImage } from 'react-icons/fa';

import styles from './CourseOverview.module.scss';

function CourseOverview({
    course = {},
    setCourse,
    listCategory,
    listLevel,
    handleFileChange,
    handleSave,
    selectedImage,
}) {
    const [showDropdown, setShowDropdown] = useState(false);
    const [filterText, setFilterText] = useState('');

    // Thêm useEffect để debug
    useEffect(() => {
        console.log('Course data in Overview:', {
            name: course?.name,
            description: course?.description,
            price: course?.price,
            status: course?.status,
            levelId: course?.levelId,
            categories: course?.categories,
        });
    }, [course]);

    // Kiểm tra nếu course là undefined hoặc null
    useEffect(() => {
        if (!course || Object.keys(course).length === 0) {
            console.log('Course is empty or undefined');
            return;
        }
        console.log('Course data loaded:', course);
    }, [course]);

    // Nếu không có dữ liệu, hiển thị loading
    if (!course || Object.keys(course).length === 0) {
        return <div>Loading course data...</div>;
    }

    // Lọc cây danh mục
    const filterTree = (nodes, text) => {
        if (!text) return nodes;
        return nodes
            .map((node) => {
                if (node.name.toLowerCase().includes(text.toLowerCase())) {
                    return node;
                }
                if (node.children) {
                    const filteredChildren = filterTree(node.children, text);
                    if (filteredChildren.length > 0) {
                        return { ...node, children: filteredChildren };
                    }
                }
                return null;
            })
            .filter(Boolean);
    };

    const filteredTree = filterTree(listCategory, filterText);

    const renderTree = (nodes, level = 0) =>
        nodes.map((node) => {
            const isSelected = course?.categories?.some((c) => c.categoryId === node.categoryId);

            return (
                <div key={node.categoryId} style={{ paddingLeft: level * 16 }}>
                    <Button
                        variant="outline-primary"
                        size="sm"
                        className="w-100 text-start fs-6 mt-2"
                        disabled={isSelected}
                        onClick={() => {
                            setCourse({
                                ...course,
                                categories: [...(course.categories || []), node],
                            });
                            setShowDropdown(false);
                        }}
                    >
                        {node.name}
                    </Button>
                    {node.children && node.children.length > 0 && renderTree(node.children, level + 1)}
                </div>
            );
        });

    // // Sửa lại các event handler để tránh stale closure
    // const handleInputChange = (field, value) => {
    //     setCourse((prev) => ({
    //         ...prev,
    //         [field]: value,
    //     }));
    // };

    // Sửa lại các event handler để tránh stale closure
    const handleInputChange = (field, value) => {
        setCourse((prev) => ({
            ...prev,
            [field]: value,
        }));
    };

    // return (
    //     <form onSubmit={handleSave} className="mt-3">
    //         {/* Header với nút lưu */}
    //         <div className="d-flex justify-content-between align-items-center mb-4">
    //             <span className="fs-3 text-primary fw-bold ms-3">Thông tin khóa học</span>
    //             <Button type="submit" className="btn btn-primary fs-5 fw-bold">
    //                 Cập nhật
    //             </Button>
    //         </div>

    //         <div className="row">
    //             {/* Bên trái */}
    //             <div className="col-md-9">
    //                 <div className="bg-light border rounded p-3 mb-3">
    //                     <div className="mb-3">
    //                         <label className="form-label">Tên khóa học</label>
    //                         <input
    //                             type="text"
    //                             className="form-control"
    //                             value={course.name || ''}
    //                             onChange={(e) => handleInputChange('name', e.target.value)}
    //                         />
    //                     </div>

    //                     <div className="mb-3">
    //                         <label className="form-label">Mô tả ngắn</label>
    //                         <textarea
    //                             className="form-control"
    //                             rows="3"
    //                             value={course.description || ''}
    //                             onChange={(e) => handleInputChange('description', e.target.value)}
    //                         />
    //                     </div>

    //                     <div className="mb-3">
    //                         <label className="form-label">Chủ đề</label>
    //                         <input
    //                             type="text"
    //                             className="form-control"
    //                             value={course?.topic || ''}
    //                             onChange={(e) => setCourse({ ...course, topic: e.target.value })}
    //                         />
    //                     </div>

    //                     <div className="mb-3">
    //                         <label className="form-label">Mô tả chi tiết</label>
    //                         <div className={styles.quillWrapper}>
    //                             <ReactQuill
    //                                 theme="snow"
    //                                 value={course?.contentDescription || ''}
    //                                 onChange={(val) => setCourse({ ...course, contentDescription: val })}
    //                             />
    //                         </div>
    //                     </div>
    //                 </div>

    //                 {/* Ảnh đại diện */}
    //                 <div className="bg-light border rounded p-3 mb-3">
    //                     <label className="form-label">Ảnh đại diện</label>
    //                     {!selectedImage && !course?.avatar ? (
    //                         <div
    //                             className={`${styles.uploadBox}`}
    //                             onClick={() => document.getElementById('avatarInput').click()}
    //                         >
    //                             <p>Chọn ảnh cho khóa học</p>
    //                         </div>
    //                     ) : (
    //                         <div className={`${styles.previewBox}`}>
    //                             <img src={selectedImage || course.avatar} alt="Avatar" className="img-fluid" />
    //                             <Button
    //                                 variant="outline-primary"
    //                                 size="sm"
    //                                 className={`${styles.changeBtn} fs-5 fw-bold mt-2 ms-3 p-5 btn btn-outline-secondary`}
    //                                 onClick={() => document.getElementById('avatarInput').click()}
    //                             >
    //                                 <FaImage className="me-2" /> Đổi ảnh
    //                             </Button>
    //                         </div>
    //                     )}
    //                     <input id="avatarInput" type="file" className="d-none" onChange={handleFileChange} />
    //                 </div>

    //                 {/* Giá khóa học */}
    //                 <div className="bg-light border rounded p-3">
    //                     <label className="form-label">Giá khóa học</label>
    //                     <input
    //                         type="number"
    //                         className="form-control"
    //                         value={course.price || ''}
    //                         onChange={(e) => handleInputChange('price', e.target.value)}
    //                     />
    //                 </div>
    //             </div>

    //             {/* Bên phải */}
    //             <div className="col-md-3">
    //                 {/* Trạng thái */}
    //                 <div className="bg-light border rounded p-3 mb-3">
    //                     <label className="form-label d-block">Trạng thái</label>

    //                     {course?.status === 0 ? (
    //                         // Nếu là Nháp → cho chọn Nháp hoặc Công khai
    //                         <>
    //                             <Form.Check
    //                                 type="radio"
    //                                 label="Nháp"
    //                                 checked={course?.status === 0}
    //                                 onChange={() => setCourse({ ...course, status: 0 })}
    //                             />
    //                             <Form.Check
    //                                 type="radio"
    //                                 label="Công khai"
    //                                 checked={course?.status === 1}
    //                                 onChange={() => setCourse({ ...course, status: 1 })}
    //                             />
    //                         </>
    //                     ) : (
    //                         // Nếu là Công khai hoặc Không công khai → cho chọn 2 trạng thái này
    //                         <>
    //                             <Form.Check
    //                                 type="radio"
    //                                 label="Công khai"
    //                                 checked={course?.status === 1}
    //                                 onChange={() => setCourse({ ...course, status: 1 })}
    //                             />
    //                             <Form.Check
    //                                 type="radio"
    //                                 label="Không công khai"
    //                                 checked={course?.status === 2}
    //                                 onChange={() => setCourse({ ...course, status: 2 })}
    //                             />
    //                         </>
    //                     )}
    //                 </div>

    //                 {/* Độ khó */}
    //                 <div className="bg-light border rounded p-3 mb-3">
    //                     <label className="form-label">Độ khó</label>
    //                     <select
    //                         className="form-select"
    //                         value={course?.levelId ?? ''}
    //                         onChange={(e) => setCourse({ ...course, levelId: Number(e.target.value) })}
    //                     >
    //                         <option value="">-- Chọn độ khó --</option>
    //                         {listLevel.map((lv) => (
    //                             <option key={lv.id} value={lv.id}>
    //                                 {lv.name}
    //                             </option>
    //                         ))}
    //                     </select>
    //                 </div>

    //                 {/* Danh mục */}
    //                 <div className="bg-light border rounded p-3">
    //                     <label className="form-label">Danh mục</label>
    //                     <div className="mb-2">
    //                         {course?.categories?.map((cat) => (
    //                             <div
    //                                 key={cat.categoryId}
    //                                 className="d-flex justify-content-between align-items-center border rounded p-1 mb-2"
    //                             >
    //                                 <span className="text-muted ps-3 pt-1 pb-1">{cat.name}</span>
    //                                 <Button
    //                                     variant="outline-dark"
    //                                     size="sm"
    //                                     onClick={() =>
    //                                         setCourse({
    //                                             ...course,
    //                                             categories: course.categories.filter(
    //                                                 (c) => c.categoryId !== cat.categoryId,
    //                                             ),
    //                                         })
    //                                     }
    //                                 >
    //                                     <FaTimes />
    //                                 </Button>
    //                             </div>
    //                         ))}
    //                     </div>

    //                     <Tippy
    //                         interactive
    //                         visible={showDropdown}
    //                         onClickOutside={() => setShowDropdown(false)}
    //                         placement="bottom-start"
    //                         render={(attrs) => (
    //                             <div className={styles.dropdownCard} tabIndex="-1" {...attrs}>
    //                                 <div className={styles.header}>
    //                                     <Form.Control
    //                                         className="fs-6"
    //                                         type="text"
    //                                         placeholder="Tìm danh mục..."
    //                                         value={filterText}
    //                                         onChange={(e) => setFilterText(e.target.value)}
    //                                     />
    //                                 </div>
    //                                 <div className={styles.body}>
    //                                     {filteredTree.length > 0 ? (
    //                                         renderTree(filteredTree)
    //                                     ) : (
    //                                         <p className={styles.empty}>Không tìm thấy</p>
    //                                     )}
    //                                 </div>
    //                             </div>
    //                         )}
    //                     >
    //                         <Button
    //                             variant="outline-secondary"
    //                             className="d-flex align-items-center justify-content-center gap-2 mt-2 w-100"
    //                             onClick={() => setShowDropdown(!showDropdown)}
    //                         >
    //                             {showDropdown ? <FaChevronDown /> : <FaPlus />}
    //                             {showDropdown ? 'Thu gọn' : 'Thêm danh mục'}
    //                         </Button>
    //                     </Tippy>
    //                 </div>
    //             </div>
    //         </div>
    //     </form>
    // );

    return (
        <form onSubmit={handleSave} className="mt-3">
            {/* Header với nút lưu */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <span className="fs-3 text-primary fw-bold ms-3">Thông tin khóa học</span>
                <Button type="submit" className="btn btn-primary fs-5 fw-bold">
                    Cập nhật
                </Button>
            </div>

            <div className="row">
                {/* Bên trái */}
                <div className="col-md-9">
                    <div className="bg-light border rounded p-3 mb-3">
                        <div className="mb-3">
                            <label className="form-label">
                                Tên khóa học <span className="text-danger">*</span>
                            </label>
                            <input
                                type="text"
                                className="form-control"
                                value={course.name || ''}
                                onChange={(e) => handleInputChange('name', e.target.value)}
                                required // ⬅️ Thêm required
                            />
                        </div>
                        {/* ... (các trường Mô tả ngắn, Chủ đề, Mô tả chi tiết khác) */}
                        <div className="mb-3">
                            <label className="form-label">Mô tả ngắn</label>
                            <textarea
                                className="form-control"
                                rows="3"
                                value={course.description || ''}
                                onChange={(e) => handleInputChange('description', e.target.value)}
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Chủ đề</label>
                            <input
                                type="text"
                                className="form-control"
                                value={course?.topic || ''}
                                onChange={(e) => setCourse({ ...course, topic: e.target.value })}
                            />
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Mô tả chi tiết</label>
                            <div className={styles.quillWrapper}>
                                <ReactQuill
                                    theme="snow"
                                    value={course?.contentDescription || ''}
                                    onChange={(val) => setCourse({ ...course, contentDescription: val })}
                                />
                            </div>
                        </div>
                    </div>

                    {/* Ảnh đại diện (giữ nguyên) */}
                    <div className="bg-light border rounded p-3 mb-3">
                        <label className="form-label">Ảnh đại diện</label>
                        {/* ... (phần code hiển thị ảnh) */}
                        {!selectedImage && !course?.avatar ? (
                            <div
                                className={`${styles.uploadBox}`}
                                onClick={() => document.getElementById('avatarInput').click()}
                            >
                                <p>Chọn ảnh cho khóa học</p>
                            </div>
                        ) : (
                            <div className={`${styles.previewBox}`}>
                                <img src={selectedImage || course.avatar} alt="Avatar" className="img-fluid" />
                                <Button
                                    variant="outline-primary"
                                    size="sm"
                                    className={`${styles.changeBtn} fs-5 fw-bold mt-2 ms-3 p-5 btn btn-outline-secondary`}
                                    onClick={() => document.getElementById('avatarInput').click()}
                                >
                                    <FaImage className="me-2" /> Đổi ảnh
                                </Button>
                            </div>
                        )}
                        <input id="avatarInput" type="file" className="d-none" onChange={handleFileChange} />
                    </div>

                    {/* Giá khóa học */}
                    <div className="bg-light border rounded p-3">
                        <label className="form-label">
                            Giá khóa học <span className="text-danger">*</span>
                        </label>
                        <input
                            type="number"
                            className="form-control"
                            value={course.price || ''}
                            onChange={(e) => handleInputChange('price', e.target.value)}
                            required // ⬅️ Thêm required
                            min="0" // ⬅️ Thêm min để đảm bảo không âm
                        />
                    </div>
                </div>

                {/* Bên phải */}
                <div className="col-md-3">
                    {/* Trạng thái (giữ nguyên) */}
                    <div className="bg-light border rounded p-3 mb-3">
                        {/* ... (phần code Trạng thái) */}
                        <label className="form-label d-block">Trạng thái</label>

                        {course?.status === 0 ? (
                            // Nếu là Nháp → cho chọn Nháp hoặc Công khai
                            <>
                                <Form.Check
                                    type="radio"
                                    label="Nháp"
                                    checked={course?.status === 0}
                                    onChange={() => setCourse({ ...course, status: 0 })}
                                />
                                <Form.Check
                                    type="radio"
                                    label="Công khai"
                                    checked={course?.status === 1}
                                    onChange={() => setCourse({ ...course, status: 1 })}
                                />
                            </>
                        ) : (
                            // Nếu là Công khai hoặc Không công khai → cho chọn 2 trạng thái này
                            <>
                                <Form.Check
                                    type="radio"
                                    label="Công khai"
                                    checked={course?.status === 1}
                                    onChange={() => setCourse({ ...course, status: 1 })}
                                />
                                <Form.Check
                                    type="radio"
                                    label="Không công khai"
                                    checked={course?.status === 2}
                                    onChange={() => setCourse({ ...course, status: 2 })}
                                />
                            </>
                        )}
                    </div>

                    {/* Độ khó */}
                    <div className="bg-light border rounded p-3 mb-3">
                        <label className="form-label">
                            Độ khó <span className="text-danger">*</span>
                        </label>
                        <select
                            className="form-select"
                            value={course?.levelId ?? ''}
                            onChange={(e) => setCourse({ ...course, levelId: Number(e.target.value) })}
                            required // ⬅️ Thêm required
                        >
                            <option value="">-- Chọn độ khó --</option>
                            {listLevel.map((lv) => (
                                <option key={lv.id} value={lv.id}>
                                    {lv.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* Danh mục */}
                    <div className="bg-light border rounded p-3">
                        <label className="form-label">
                            Danh mục <span className="text-danger">*</span> (Tối thiểu 1)
                        </label>
                        {/* ... (phần code hiển thị danh mục đã chọn và Tippy) */}
                        <div className="mb-2">
                            {course?.categories?.map((cat) => (
                                <div
                                    key={cat.categoryId}
                                    className="d-flex justify-content-between align-items-center border rounded p-1 mb-2"
                                >
                                    <span className="text-muted ps-3 pt-1 pb-1">{cat.name}</span>
                                    <Button
                                        variant="outline-dark"
                                        size="sm"
                                        onClick={() =>
                                            setCourse({
                                                ...course,
                                                categories: course.categories.filter(
                                                    (c) => c.categoryId !== cat.categoryId,
                                                ),
                                            })
                                        }
                                    >
                                        <FaTimes />
                                    </Button>
                                </div>
                            ))}
                            {/* 💡 Lưu ý: Đối với Danh mục, việc kiểm tra hợp lệ được xử lý trong hàm `validateCourse` của CourseManagerDetail.jsx */}
                        </div>

                        <Tippy
                            interactive
                            visible={showDropdown}
                            onClickOutside={() => setShowDropdown(false)}
                            placement="bottom-start"
                            render={(attrs) => (
                                <div className={styles.dropdownCard} tabIndex="-1" {...attrs}>
                                    <div className={styles.header}>
                                        <Form.Control
                                            className="fs-6"
                                            type="text"
                                            placeholder="Tìm danh mục..."
                                            value={filterText}
                                            onChange={(e) => setFilterText(e.target.value)}
                                        />
                                    </div>
                                    <div className={styles.body}>
                                        {filteredTree.length > 0 ? (
                                            renderTree(filteredTree)
                                        ) : (
                                            <p className={styles.empty}>Không tìm thấy</p>
                                        )}
                                    </div>
                                </div>
                            )}
                        >
                            <Button
                                variant="outline-secondary"
                                className="d-flex align-items-center justify-content-center gap-2 mt-2 w-100"
                                onClick={() => setShowDropdown(!showDropdown)}
                            >
                                {showDropdown ? <FaChevronDown /> : <FaPlus />}
                                {showDropdown ? 'Thu gọn' : 'Thêm danh mục'}
                            </Button>
                        </Tippy>
                    </div>
                </div>
            </div>
        </form>
    );
}

export default CourseOverview;
