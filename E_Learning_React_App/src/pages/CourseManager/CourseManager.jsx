//Thư viện
import React, { useEffect, useState, useCallback } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

//Component
import axios from '~/utils/CustomizeAxios';
import Breadcrumb from './Breadcrumb';
import AddNewButton from './AddNewButton';
import Tabs from './Tabs';
import PostedCoursesTable from './PostedCoursesTable';
import DraftCoursesTable from './DraftCoursesTable';

const CourseManager = () => {
    const token = sessionStorage.getItem('token');
    const navigate = useNavigate();

    const [listKhoaHocDaDang, setListKhoaHocDaDang] = useState([]);
    const [listKhoaHocNhap, setListKhoaHocNhap] = useState([]);
    const [activeTab, setActiveTab] = useState('posted');

    const fetchPostedCourses = useCallback(async () => {
        try {
            const resp = await axios.get(`/course-manager/posted-course`);
            setListKhoaHocDaDang(resp.data);
        } catch {
            toast.error('Lỗi khi tải danh sách khóa học đã đăng');
        }
    }, []);

    const fetchDraftCourses = useCallback(async () => {
        try {
            const resp = await axios.get(`/course-manager/draft-course`);
            setListKhoaHocNhap(resp.data);
        } catch {
            toast.error('Lỗi khi tải danh sách khóa học nháp');
        }
    }, []);

    const removeDraftCourse = useCallback(
        async (courseId) => {
            try {
                await axios.delete(`/course-manager/draft-course/remove-course/${courseId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                toast.success('Xóa khóa học nháp thành công');
                fetchDraftCourses();
            } catch {
                toast.error('Có lỗi khi xóa khóa học nháp, vui lòng thử lại');
            }
        },
        [token, fetchDraftCourses],
    );

    const themMoiKhoaHoc = async () => {
        try {
            const resp = await axios.post(`/course-manager/add-draf-course`);
            const newId = resp.data;         
            toast.success('Thêm mới khóa học thành công');
            navigate(`/admin/course-detail-manager/${newId}`);
        } catch (error) {
            toast.error('Lỗi khi thêm mới khóa học');
            console.error(error);
        }
    };

    useEffect(() => {
        fetchPostedCourses();
        fetchDraftCourses();
    }, [fetchPostedCourses, fetchDraftCourses]);

    return (
        <div className="container mt-3">
            <Breadcrumb />
            <AddNewButton onAdd={themMoiKhoaHoc} />
            <hr />
            <Tabs activeTab={activeTab} setActiveTab={setActiveTab} />
            <div className="tab-content mt-3">
                {activeTab === 'posted' && <PostedCoursesTable list={listKhoaHocDaDang} />}
                {activeTab === 'draft' && <DraftCoursesTable list={listKhoaHocNhap} onRemove={removeDraftCourse} />}
            </div>
        </div>
    );
};

export default CourseManager;
