import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import axios from '~/utils/CustomizeAxios';
import FilterCourse from './FilterCourse';
import SortCourse from './SortCourse';
import ListCourse from './ListCourse';
import Pagination from './Pagination';

function SearchCourse() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [sortType, setSortType] = useState(null); // <--- thêm
    const [paginationInfo, setPaginationInfo] = useState({
        page: 0,
        totalPages: 0,
        totalElements: 0,
    });

    useEffect(() => {
        const fetchCourses = async () => {
            try {
                setLoading(true);
                const queryString = searchParams.toString();
                const response = await axios.get(`/course/search?${queryString}`);
                setCourses(response.data.content || []);
                setPaginationInfo({
                    page: response.data.number,
                    totalPages: response.data.totalPages,
                    totalElements: response.data.totalElements,
                });
            } catch (error) {
                console.error('Error fetching courses:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchCourses();
    }, [searchParams]);

    // ✅ Sắp xếp ở FE mỗi khi sortType hoặc courses thay đổi
    const sortedCourses = [...courses].sort((a, b) => {
        if (!sortType) return 0;
        if (sortType === 'asc') return a.price - b.price;
        if (sortType === 'desc') return b.price - a.price;
        return 0;
    });

    return (
        <main className="container py-4">
            <div className="row">
                <div className="col-lg-3 mb-4">
                    <FilterCourse setSearchParams={setSearchParams} />
                </div>

                <div className="col-lg-9">
                    <div className="d-flex justify-content-end mb-3">
                        <SortCourse
                            totalElements={paginationInfo.totalElements}
                            onSortChange={setSortType} // <--- nhận event từ SortCourse
                        />
                    </div>

                    <ListCourse loading={loading} courses={sortedCourses} />

                    <div className="mt-4 d-flex justify-content-center">
                        <Pagination
                            page={paginationInfo.page}
                            totalPages={paginationInfo.totalPages}
                            setSearchParams={setSearchParams}
                        />
                    </div>
                </div>
            </div>
        </main>
    );
}

export default SearchCourse;
