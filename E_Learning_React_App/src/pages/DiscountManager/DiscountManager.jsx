import { useState, useEffect } from 'react';
import axios from '~/utils/CustomizeAxios';
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';
import { toast } from 'react-toastify';
import { Offcanvas, Button } from 'react-bootstrap';

const DiscountManager = () => {
    const [vouchers, setVouchers] = useState([]);
    const [voucher, setVoucher] = useState({});
    const [errors, setErrors] = useState({});
    const [isEdit, setIsEdit] = useState(false);
    const [show, setShow] = useState(false);

    const token = localStorage.getItem('token');

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    useEffect(() => {
        loadVouchers();
    }, []);

    // Reset form
    const resetVoucher = () => {
        setVoucher({});
        setErrors({});
        setIsEdit(false);
    };

    // Validate từng field
    const validateField = (name, value) => {
        let msg = '';
        const today = new Date().toISOString().split('T')[0]; // yyyy-mm-dd
        const startDate = voucher.startDate || '';
        const endDate = voucher.endDate || '';

        switch (name) {
            case 'voucherCode':
                if (!value) msg = 'Mã voucher không được bỏ trống';
                else if (!/^[a-zA-Z0-9]+$/.test(value)) msg = 'Mã chỉ chứa chữ và số, không có khoảng trắng';
                break;
            case 'name':
                if (!value) msg = 'Tên voucher không được bỏ trống';
                break;
            case 'percentSale':
                if (!value) msg = 'Phần trăm không được bỏ trống';
                else if (value > 100) msg = 'Không được lớn hơn 100%';
                else if (value < 0) msg = 'Không được âm';
                break;
            case 'quantity':
                if (value === '') msg = 'Số lượng không được bỏ trống';
                else if (value < 0) msg = 'Số lượng không được âm';
                break;
            case 'startDate':
                if (!value) msg = 'Ngày bắt đầu không được bỏ trống';
                else if (value < today) msg = 'Ngày bắt đầu không nhỏ hơn hôm nay';
                else if (endDate && value >= endDate) msg = 'Ngày bắt đầu phải nhỏ hơn ngày kết thúc';
                break;
            case 'endDate':
                if (!value) msg = 'Ngày kết thúc không được bỏ trống';
                else if (startDate && value <= startDate) msg = 'Ngày kết thúc phải lớn hơn ngày bắt đầu';
                break;
            default:
                break;
        }
        return msg;
    };

    // Validate khi nhập
    const handleChange = (e) => {
        const { name, value } = e.target;
        setVoucher({ ...voucher, [name]: value });
        const errorMsg = validateField(name, value);
        setErrors({ ...errors, [name]: errorMsg });
    };

    // Load danh sách voucher
    const loadVouchers = async () => {
        try {
            const resp = await axios.get('/voucher/voucher-manager', {
                headers: { Authorization: `Bearer ${token}` },
            });

            const list = resp.data.map((v) => {
                const startDate = new Date(v.startDate);
                const endDate = new Date(v.endDate);
                const now = new Date();

                const formatDate = (date) => {
                    if (!date) return '';
                    const dd = String(date.getDate()).padStart(2, '0');
                    const mm = String(date.getMonth() + 1).padStart(2, '0');
                    const yyyy = date.getFullYear();
                    return `${yyyy}-${mm}-${dd}`;
                };

                return {
                    ...v,
                    startDate: formatDate(startDate),
                    endDate: formatDate(endDate),
                    status: endDate < now || v.quantity === 0 || now < startDate ? false : true,
                };
            });

            setVouchers(list);
        } catch (err) {
            console.error('Lỗi load vouchers:', err);
            toast.error('Không thể tải danh sách voucher!');
        }
    };

    // Xem chi tiết
    const viewDetail = async (id) => {
        try {
            const resp = await axios.get(`/voucher/voucher-manager/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setVoucher(resp.data);
            setIsEdit(true);
            handleShow();
        } catch (err) {
            toast.error('Không thể lấy thông tin voucher!');
        }
    };

    // Submit form
    const handleSubmit = async (e) => {
        e.preventDefault();
        // Check tất cả field
        const newErrors = {};
        Object.keys(voucher).forEach((key) => {
            const msg = validateField(key, voucher[key]);
            if (msg) newErrors[key] = msg;
        });
        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) {
            toast.error('Vui lòng sửa các lỗi trước khi lưu!');
            return;
        }

        try {
            // Trong handleSubmit
            if (isEdit) {
                await axios.put(`/voucher/${voucher.voucherId}`, voucher, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                toast.success('Cập nhật voucher thành công!');
                loadVouchers(); // reload danh sách
            } else {
                await axios.post('/voucher', voucher, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                toast.success('Thêm voucher mới thành công!');
                loadVouchers(); // reload danh sách
            }
            resetVoucher();
            handleClose();
        } catch (err) {
            toast.error('Không thể lưu voucher!');
        }
    };

    // Xóa voucher
    // Xóa voucher
    const deleteVoucher = async (id) => {
        const confirmed = window.confirm('Bạn có chắc chắn muốn xóa voucher này?');
        if (!confirmed) return;

        try {
            await axios.delete(`/voucher/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            toast.success('Xóa voucher thành công!');
            loadVouchers();
        } catch (err) {
            toast.error('Mã khuyến mãi này không thể xóa!');
        }
    };

    return (
        <div className="container">
            <h2 className="mb-4 fw-bold">Danh sách voucher</h2>
            <button
                className="btn btn-primary fs-5 mb-3"
                onClick={() => {
                    resetVoucher();
                    handleShow();
                }}
            >
                <FaPlus /> Tạo Voucher
            </button>

            {/* Bảng danh sách */}
            <table className="table table-striped">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Tên</th>
                        <th>Mã</th>
                        <th>Giá trị</th>
                        <th>SL</th>
                        <th>Bắt đầu</th>
                        <th>Kết thúc</th>
                        <th>Trạng thái</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    {vouchers.map((sale, idx) => (
                        <tr key={sale.voucherId}>
                            <td>{idx + 1}</td>
                            <td>{sale.name}</td>
                            <td>{sale.voucherCode}</td>
                            <td className="text-success fw-bold">{sale.percentSale}%</td>
                            <td>{sale.quantity}</td>
                            <td>{sale.startDate}</td>
                            <td>{sale.endDate}</td>
                            <td>
                                <span
                                    className={sale.status ? 'text-success fw-bold fs-6' : 'text-danger fw-bold fs-6'}
                                >
                                    {sale.status ? 'Còn hiệu lực' : 'Hết hiệu lực'}
                                </span>
                            </td>
                            <td>
                                <button className="btn btn-primary me-2" onClick={() => viewDetail(sale.voucherId)}>
                                    <FaEdit /> Edit
                                </button>
                                <button className="btn btn-danger" onClick={() => deleteVoucher(sale.voucherId)}>
                                    <FaTrash /> Xóa
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* Offcanvas */}
            <Offcanvas show={show} onHide={handleClose} placement="end">
                <Offcanvas.Header closeButton>
                    <Offcanvas.Title>{isEdit ? 'Cập nhật Voucher' : 'Thêm Voucher'}</Offcanvas.Title>
                </Offcanvas.Header>
                <Offcanvas.Body>
                    <form onSubmit={handleSubmit}>
                        <div className="row">
                            {/* Voucher Code */}
                            <div className="col-md-6 mb-3">
                                <label className="form-label">
                                    Mã Voucher <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="text"
                                    name="voucherCode"
                                    className="form-control fs-5"
                                    value={voucher.voucherCode || ''}
                                    onChange={handleChange}
                                    disabled={isEdit}
                                />
                                {errors.voucherCode && <div className="text-danger">{errors.voucherCode}</div>}
                            </div>
                            {/* Name */}
                            <div className="col-md-6 mb-3">
                                <label className="form-label">
                                    Tên Voucher <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="text"
                                    name="name"
                                    className="form-control fs-5"
                                    value={voucher.name || ''}
                                    onChange={handleChange}
                                    disabled={isEdit}
                                />
                                {errors.name && <div className="text-danger">{errors.name}</div>}
                            </div>
                            {/* Percent */}
                            <div className="col-md-6 mb-3">
                                <label className="form-label">
                                    Phần trăm giảm (%) <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="number"
                                    name="percentSale"
                                    className="form-control fs-5"
                                    value={voucher.percentSale || ''}
                                    onChange={handleChange}
                                />
                                {errors.percentSale && <div className="text-danger">{errors.percentSale}</div>}
                            </div>
                            {/* Quantity */}
                            <div className="col-md-6 mb-3">
                                <label className="form-label">
                                    Số lượng <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="number"
                                    name="quantity"
                                    className="form-control fs-5"
                                    value={voucher.quantity || ''}
                                    onChange={handleChange}
                                />
                                {errors.quantity && <div className="text-danger">{errors.quantity}</div>}
                            </div>
                            {/* Dates */}
                            <div className="col-md-6 mb-3">
                                <label className="form-label">
                                    Ngày bắt đầu <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="date"
                                    name="startDate"
                                    className="form-control fs-5"
                                    value={voucher.startDate || ''}
                                    onChange={handleChange}
                                />
                                {errors.startDate && <div className="text-danger">{errors.startDate}</div>}
                            </div>
                            <div className="col-md-6 mb-3">
                                <label className="form-label">
                                    Ngày kết thúc <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="date"
                                    name="endDate"
                                    className="form-control fs-5"
                                    value={voucher.endDate || ''}
                                    onChange={handleChange}
                                />
                                {errors.endDate && <div className="text-danger">{errors.endDate}</div>}
                            </div>
                            {/* Description */}
                            <div className="col-md-12 mb-3">
                                <label className="form-label">Mô tả</label>
                                <textarea
                                    name="description"
                                    className="form-control fs-5"
                                    rows="3"
                                    value={voucher.description || ''}
                                    onChange={handleChange}
                                ></textarea>
                            </div>
                        </div>
                        {/* Footer */}
                        <div className="d-flex justify-content-end gap-2">
                            <Button type="submit" className="btn btn-primary fs-5">
                                {isEdit ? 'Cập nhật' : 'Thêm'}
                            </Button>
                            <Button type="button" className="btn btn-secondary fs-5" onClick={resetVoucher}>
                                Làm mới
                            </Button>
                        </div>
                    </form>
                </Offcanvas.Body>
            </Offcanvas>
        </div>
    );
};

export default DiscountManager;
