import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import axios from '~/utils/CustomizeAxios';
import styles from './UserManager.module.scss';
import classNames from 'classnames/bind';

// Component con
import SearchBar from './SearchBar';
import UserTable from './UserTable';
import UserDetailModal from './UserDetailModal';

// Custom hook
import useDebounce from '~/hooks/useDebounce';

const cx = classNames.bind(styles);

export default function UserManager() {
    const [users, setUsers] = useState([]);
    const [filteredUsers, setFilteredUsers] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [userCourses, setUserCourses] = useState([]);
    const [selectedUserId, setSelectedUserId] = useState(null);
    const [showModal, setShowModal] = useState(false);

    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    // debounce giá trị searchQuery (chỉ thay đổi sau khi user ngừng gõ 500ms)
    const debouncedQuery = useDebounce(searchQuery, 500);

    useEffect(() => {
        loadUsers();
    }, []);

    useEffect(() => {
        filterUsers(debouncedQuery);
    }, [debouncedQuery, users]);

    const loadUsers = async () => {
        try {
            const resp = await axios.get('/course-progress/user-admin', { headers });
            const list = resp.data.filter((u) => u.role?.name !== 'Admin');
            setUsers(list);
            setFilteredUsers(list);
        } catch (err) {
            toast.error('Không thể tải danh sách người dùng');
        }
    };

    const filterUsers = (query) => {
        if (!query) {
            setFilteredUsers(users);
            return;
        }
        const q = query.toLowerCase();
        const list = users.filter((u) => u.name?.toLowerCase().includes(q) || u.email?.toLowerCase().includes(q));
        setFilteredUsers(list);
    };

    const viewCourses = async (userId) => {
        setSelectedUserId(userId);
        try {
            const resp = await axios.get(`/course-progress/${userId}`, { headers });
            setUserCourses(resp.data);
            setShowModal(true);
        } catch {
            toast.error('Không thể tải dữ liệu khóa học');
        }
    };

    const blockUser = async (userId) => {
        const confirmBlock = window.confirm('Bạn có chắc chắn muốn chặn người dùng này không?');
        if (!confirmBlock) return;

        try {
            await axios.put(`/user/blockUser/${userId}`, {}, { headers });
            toast.success('Đã chặn người dùng!');
            setUsers((prev) => prev.map((u) => (u.userId === userId ? { ...u, isActive: false } : u)));
        } catch {
            toast.error('Không thể chặn người dùng');
        }
    };

    const unblockUser = async (userId) => {
        try {
            await axios.put(`/user/unblockUser/${userId}`, {}, { headers });
            toast.success('Đã mở khóa người dùng!');
            setUsers((prev) => prev.map((u) => (u.userId === userId ? { ...u, isActive: true } : u)));
        } catch {
            toast.error('Không thể mở khóa người dùng');
        }
    };

    return (
        <div className="container">
            <SearchBar query={searchQuery} onChange={setSearchQuery} />

            <UserTable users={filteredUsers} onView={viewCourses} onBlock={blockUser} onUnblock={unblockUser} />

            {filteredUsers.length === 0 && <p>Không có dữ liệu phù hợp.</p>}

            <UserDetailModal show={showModal} onClose={() => setShowModal(false)} courses={userCourses} />
        </div>
    );
}
