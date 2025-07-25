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
    const [paginationInfo, setPaginationInfo] = useState({
        page: 0,
        totalPages: 1,
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
                });
            } catch (error) {
                console.error('Error fetching courses:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchCourses();
    }, [searchParams]);

    return (
        <main className="container py-4">
            <div className="row">
                {/* Sidebar: Bộ lọc (3 cột) */}
                <div className="col-lg-3 mb-4">
                    <FilterCourse setSearchParams={setSearchParams} />
                </div>

                {/* Nội dung chính: danh sách + phân trang (9 cột) */}
                <div className="col-lg-9">
                    <div className="d-flex justify-content-end mb-3">
                        <SortCourse setSearchParams={setSearchParams} />
                    </div>

                    <ListCourse loading={loading} courses={courses} />

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
