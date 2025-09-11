import { FaBan, FaUnlock, FaEye } from 'react-icons/fa';
import classNames from 'classnames/bind';
import { formatDate } from '~/utils/format';
import styles from './UserManager.module.scss';

const cx = classNames.bind(styles);

export default function UserTable({ users, onView, onBlock, onUnblock }) {
    return (
        <table className="table table-striped table-hover">
            <thead className="table-dark">
                <tr>
                    <th>Tên</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                    <th>Ngày tạo</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                {users.map((u) => (
                    <tr key={u.userId}>
                        <td>{u.name}</td>
                        <td className={cx('truncateText')}>{u.email}</td>
                        <td>{u.phone}</td>
                        <td>{formatDate(u.createAt)}</td>
                        <td className={u.isActive ? 'text-success' : 'text-danger'}>
                            {u.isActive ? 'Hoạt động' : 'Đã khóa'}
                        </td>
                        <td className={cx('actionsCell')}>
                            <button className="btn btn-outline-primary btn-sm fs-5" onClick={() => onView(u.userId)}>
                                <FaEye className="me-1" /> Xem tiến độ học
                            </button>
                            {u.isActive ? (
                                <button className="btn btn-outline-danger btn-sm" onClick={() => onBlock(u.userId)}>
                                    <FaBan className="me-1" /> Khóa
                                </button>
                            ) : (
                                <button className="btn btn-outline-success btn-sm" onClick={() => onUnblock(u.userId)}>
                                    <FaUnlock className="me-1" /> Mở khóa
                                </button>
                            )}
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}
