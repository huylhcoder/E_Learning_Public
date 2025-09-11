import classNames from 'classnames/bind';
import styles from './UserManager.module.scss';

const cx = classNames.bind(styles);

export default function SearchBar({ query, onChange }) {
    return (
        <div className="d-flex align-items-center justify-content-between mb-3">
            <h1>Quản lý người dùng</h1>
            <input
                type="text"
                placeholder="Tìm kiếm theo tên hoặc email"
                className={cx('fc')}
                value={query}
                onChange={(e) => onChange(e.target.value)}
            />
        </div>
    );
}
