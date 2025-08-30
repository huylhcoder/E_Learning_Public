import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from '~/utils/CustomizeAxios';
import { toast } from 'react-toastify';
import Breadcrumb from './Breadcrumb';
import CourseTabs from './CourseTabs';
import CourseOverview from './CourseOverview';
import CourseSections from './CourseSections';

function CourseManagerDetail() {
    const { courseId } = useParams();
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const [course, setCourse] = useState({});
    const [listCategory, setListCategory] = useState([]);
    const [listLevel, setListLevel] = useState([]);
    const [listSection, setListSection] = useState([]);
    const [selectedImage, setSelectedImage] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    // ----------- Nhóm 1 Fetch API----------
    useEffect(() => {
        const loadData = async () => {
            setIsLoading(true);
            try {
                await fetchCourse();
                await fetchCategories();
                await fetchLevels();
                await fetchSections();
            } finally {
                setIsLoading(false);
            }
        };
        loadData();
    }, [courseId]); // Chỉ chạy khi courseId thay đổi

    // ----------- Nhóm 2 Map dữ liệu---------
    useEffect(() => {
        if (course?.categoryIds && listCategory.length > 0) {
            const flatList = flattenCategories(listCategory);
            const selected = course.categoryIds.map((id) => flatList.find((c) => c.categoryId === id)).filter(Boolean);

            setCourse((prev) => ({
                ...prev, // giữ lại tất cả field cũ
                categories: selected, // thêm field categories mới
            }));
        }
    }, [course?.categoryIds, listCategory]);

    // utils: flatten tree category -> array
    const flattenCategories = (nodes) => {
        let result = [];
        for (let node of nodes) {
            result.push({ categoryId: node.categoryId, name: node.name });
            if (node.children && node.children.length > 0) {
                result = result.concat(flattenCategories(node.children));
            }
        }
        return result;
    };

    const fetchCourse = async () => {
        try {
            const resp = await axios.get(`/course-manager-detail/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            console.log('Fetch course response:', resp.data);

            // Đảm bảo cập nhật toàn bộ state một lần
            setCourse(resp.data);
        } catch (err) {
            toast.error('Không thể tải thông tin khóa học');
        }
    };

    const fetchCategories = async () => {
        try {
            const resp = await axios.get(`/category/tree`);
            setListCategory(resp.data);
        } catch {
            toast.error('Không thể tải danh mục');
        }
    };

    const fetchLevels = async () => {
        try {
            const resp = await axios.get(`/course-level/list-course-level`);
            setListLevel(resp.data);
        } catch {
            toast.error('Không thể tải độ khó');
        }
    };

    const fetchSections = async () => {
        try {
            const resp = await axios.get(`/section-manager/course/${courseId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setListSection(resp.data);
        } catch {
            toast.error('Không thể tải phần của khóa học');
        }
    };

    // ----------- Handler ----------
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (ev) => setSelectedImage(ev.target.result);
            reader.readAsDataURL(file);
        }
    };

    const handleSave = async (e) => {
        e.preventDefault();

        console.log('contentDescription: ' + course.contentDescription || '');

        try {
            const formData = new FormData();
            formData.append('courseId', course.courseId || '');
            formData.append('name', course.name || '');
            formData.append('status', course.status || '');
            formData.append('description', course.description || '');
            formData.append('contentDescription', course.contentDescription || ''); // ✅ thêm Quill
            formData.append('avatar', course.avatar || '');
            formData.append('price', course.price || '');
            formData.append('topic', course.topic || '');
            formData.append('levelId', course.levelId || '');

            console.log(course.categories);

            // Truyền nhiều category
            (course.categories || []).forEach((cat) => {
                formData.append('categoryIds', cat.categoryId);
            });

            // // ✅ gửi nhiều categoryIds
            // if (course.categories && course.categories.length > 0) {
            //     course.categories.forEach((cat) => formData.append('categoryIds', cat.categoryId));
            // }

            if (selectedImage) {
                const blob = dataURLtoBlob(selectedImage);
                formData.append('file', blob, 'avatar.png');
            }

            const resp = await axios.put(`/course-manager-detail/${courseId}`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            setCourse(resp.data);
            toast.success('Cập nhật thành công!');
        } catch (err) {
            toast.error(err.response?.data || 'Cập nhật thất bại!');
        }
    };

    const handleAddSection = async () => {
        try {
            const resp = await axios.post(
                `/section-manager/course/${courseId}`,
                {},
                { headers: { Authorization: `Bearer ${token}` } },
            );
            const newSectionId = resp.data;
            navigate(`/admin/course-detail-manager/${courseId}/section/${newSectionId}`);
            toast.success('Thêm phần mới thành công!');
        } catch {
            toast.error('Không thể thêm phần mới');
        }
    };

    const handleDeleteSection = async (sectionId) => {
        try {
            await axios.delete(`/course-manager-detail/remove-section/${sectionId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchSections();
            toast.success('Xóa thành công!');
        } catch {
            toast.error('Xóa thất bại!');
        }
    };

    // ----------- Utils ----------
    function dataURLtoBlob(dataURL) {
        const byteString = atob(dataURL.split(',')[1]);
        const mimeString = dataURL.split(',')[0].split(':')[1].split(';')[0];
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);
        for (let i = 0; i < byteString.length; i++) ia[i] = byteString.charCodeAt(i);
        return new Blob([ab], { type: mimeString });
    }

    if (isLoading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="container mt-4">
            <Breadcrumb courseName={course?.name} />

            <CourseTabs
                overviewTab={
                    <CourseOverview
                        course={course}
                        setCourse={setCourse}
                        listCategory={listCategory}
                        listLevel={listLevel}
                        handleFileChange={handleFileChange}
                        handleSave={handleSave}
                        selectedImage={selectedImage}
                    />
                }
                detailsTab={
                    <CourseSections
                        courseId={course.courseId}
                        listSection={listSection}
                        handleAddSection={handleAddSection}
                        handleDeleteSection={handleDeleteSection}
                    />
                }
            />
        </div>
    );
}

export default CourseManagerDetail;
