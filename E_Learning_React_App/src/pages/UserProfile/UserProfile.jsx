import React, { useState, useEffect, useContext } from 'react';
import axios from '~/utils/CustomizeAxios';
import { toast, ToastContainer } from 'react-toastify';
import { FaSave } from 'react-icons/fa';
import 'react-toastify/dist/ReactToastify.css';

// Context
import { UserContext } from '~/context/UserContext';

const Profile = () => {
    const token = localStorage.getItem('token');
    const [user, setUser] = useState({});
    const [selectedImage, setSelectedImage] = useState(null);
    const [loading, setLoading] = useState(false);
    const { updateUser } = useContext(UserContext);

    // state cho error
    const [errors, setErrors] = useState({ name: '', phone: '' });

    // Lấy thông tin user
    const getUserDataByEmail = async () => {
        try {
            const response = await axios.get(`/user/profile`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (response.data) {
                setUser(response.data);
            } else {
                toast.error('Không tìm thấy dữ liệu người dùng.');
            }
        } catch (error) {
            console.error('Lỗi khi lấy dữ liệu người dùng:', error);
            toast.error('Lỗi khi lấy dữ liệu người dùng.');
        }
    };

    useEffect(() => {
        getUserDataByEmail();
    }, []);

    // validate realtime
    const validateField = (field, value) => {
        let error = '';
        if (field === 'name') {
            if (!value.trim()) {
                error = 'Họ và tên không được để trống.';
            } else if (!/^[a-zA-ZÀ-ỹ\s]+$/.test(value)) {
                error = 'Họ và tên chỉ chứa chữ cái và khoảng trắng.';
            }
        }
        if (field === 'phone') {
            if (!value.trim()) {
                error = 'Số điện thoại không được để trống.';
            } else if (!/^0\d{9}$/.test(value)) {
                error = 'Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.';
            }
        }
        setErrors((prev) => ({ ...prev, [field]: error }));
    };

    const handleChange = (field, value) => {
        setUser({ ...user, [field]: value });
        validateField(field, value);
    };

    // Xử lý khi chọn ảnh
    const onFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (ev) => setSelectedImage(ev.target.result);
            reader.readAsDataURL(file);
        }
    };

    // Chuyển base64 sang Blob
    const dataURLtoBlob = (dataURL) => {
        const byteString = atob(dataURL.split(',')[1]);
        const mimeString = dataURL.split(',')[0].split(':')[1].split(';')[0];
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);
        for (let i = 0; i < byteString.length; i++) {
            ia[i] = byteString.charCodeAt(i);
        }
        return new Blob([ab], { type: mimeString });
    };

    // Cập nhật thông tin user
    const updateUserData = async () => {
        // kiểm tra lỗi trước khi gửi
        validateField('name', user.name || '');
        validateField('phone', user.phone || '');
        if (errors.name || errors.phone) {
            toast.warn('Vui lòng sửa các lỗi trước khi lưu.');
            return;
        }

        const formData = new FormData();
        formData.append('name', user.name || '');
        formData.append('phone', user.phone || '');
        formData.append('urlProfileImage', user.urlProfileImage || '');

        if (selectedImage) {
            const fileBlob = dataURLtoBlob(selectedImage);
            formData.append('file', fileBlob, 'avatar.png');
        }

        try {
            setLoading(true);
            const resp = await axios.put(`/user/update/${token}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${token}`,
                },
            });
            setUser(resp.data);
            toast.success('Cập nhật thông tin thành công!');
            await updateUser(); // reload lại user info
        } catch (err) {
            console.error('Cập nhật thất bại', err);
            toast.error('Cập nhật thất bại, vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <ToastContainer />
            <h2 className="mb-4 text-primary mb-3">Cài đặt trang cá nhân</h2>

            {/* Tabs */}
            <ul className="nav nav-tabs" id="profileTab" role="tablist">
                <li className="nav-item" role="presentation">
                    <button
                        className="nav-link active"
                        id="profile-tab"
                        data-bs-toggle="tab"
                        data-bs-target="#profile"
                        type="button"
                        role="tab"
                        aria-controls="profile"
                        aria-selected="true"
                    >
                        Thông tin cá nhân
                    </button>
                </li>
                <li className="nav-item" role="presentation">
                    <button
                        className="nav-link"
                        id="orders-tab"
                        data-bs-toggle="tab"
                        data-bs-target="#orders"
                        type="button"
                        role="tab"
                        aria-controls="orders"
                        aria-selected="false"
                    >
                        Ảnh đại diện
                    </button>
                </li>
            </ul>

            {/* Tab content */}
            <div className="tab-content" id="profileTabContent">
                {/* Thông tin cá nhân */}
                <div className="tab-pane fade show active" id="profile" role="tabpanel" aria-labelledby="profile-tab">
                    <form className="mt-3">
                        <div className="row">
                            <div className="col-md-6">
                                {/* Họ và tên */}
                                <div className="mb-3">
                                    <label className="form-label fw-bold">
                                        Họ và Tên <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="text"
                                        className={`fs-4 form-control rounded-0 ${errors.name ? 'is-invalid' : ''}`}
                                        value={user.name || ''}
                                        onChange={(e) => handleChange('name', e.target.value)}
                                        placeholder="Họ và Tên"
                                    />
                                    {errors.name && <div className="invalid-feedback">{errors.name}</div>}
                                </div>

                                {/* Số điện thoại */}
                                <div className="mb-3">
                                    <label className="form-label fw-bold">
                                        Số điện thoại <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="text"
                                        className={`fs-4 form-control rounded-0 ${errors.phone ? 'is-invalid' : ''}`}
                                        value={user.phone || ''}
                                        onChange={(e) => handleChange('phone', e.target.value)}
                                        placeholder="Số điện thoại"
                                    />
                                    {errors.phone && <div className="invalid-feedback">{errors.phone}</div>}
                                </div>
                            </div>

                            <div className="col-md-6">
                                {/* Email */}
                                <div className="mb-3">
                                    <label className="form-label fw-bold">Email</label>
                                    <input
                                        type="text"
                                        className="fs-4 form-control rounded-0"
                                        value={user.email || ''}
                                        disabled
                                    />
                                </div>

                                {/* Vai trò */}
                                <div className="mb-3">
                                    <label className="form-label fw-bold">Vai trò</label>
                                    <input
                                        type="text"
                                        className="fs-4 form-control rounded-0"
                                        value={user.role?.name || ''}
                                        disabled
                                    />
                                </div>
                            </div>
                        </div>

                        <button
                            type="button"
                            onClick={updateUserData}
                            className="btn btn-primary mt-3 rounded-0 fs-5"
                            disabled={loading}
                        >
                            <FaSave /> Lưu
                        </button>
                    </form>
                </div>

                {/* Ảnh đại diện */}
                <div className="tab-pane fade" id="orders" role="tabpanel" aria-labelledby="orders-tab">
                    <h2 className="fw-bold fs-4 mt-3">Ảnh xem trước</h2>
                    <span className="mt-0 fs-6 text-secondary">Minimum 200x200 pixels, Maximum 6000x6000 pixels</span>

                    <div className="profile-image-preview rounded-0 border-dark">
                        {(selectedImage || user.urlProfileImage) && (
                            <img
                                className="w-25"
                                src={selectedImage || user.urlProfileImage}
                                alt="Profile"
                                id="profileImage"
                            />
                        )}
                    </div>

                    <form>
                        <div className="mb-3">
                            <label className="form-label">Thêm / Cập nhật ảnh</label>
                            <input
                                className="form-control rounded-0 fs-4"
                                type="file"
                                accept="image/*"
                                onChange={onFileChange}
                            />
                        </div>
                        <button
                            type="button"
                            onClick={updateUserData}
                            className="btn btn-primary mt-3 rounded-0 fs-5"
                            disabled={loading}
                        >
                            <FaSave /> Lưu
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Profile;
