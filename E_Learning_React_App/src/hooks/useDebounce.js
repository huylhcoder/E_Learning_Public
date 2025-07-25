import { useState, useEffect } from 'react';

function useDebounce(value, delay) {
    const [debouncedValue, setDebouncedValue] = useState(value);
    useEffect(() => {
        const handler = setTimeout(() => setDebouncedValue(value), delay);

        return () => clearTimeout(handler);
    }, [delay, value]); //Chỉ cần value là đủ rồi do cái delay là cố định khi truyền vào

    return debouncedValue;
}

export default useDebounce;

// Doan code nay cua ben su dung
// const [searchValue, setSearchValue] = useState('');
// const debouncedValue = useDebounce(searchValue, 500);

// useEffect(() => {
//     if (!debouncedValue.trim()) {
//         setSearchResult([]);
//         return;
//     }

//     const fetchApi = async () => {
//         setLoading(true);

//         const result = await searchServices.search(debouncedValue);

//         setSearchResult(result);
//         setLoading(false);
//     };

//     fetchApi();
//  Lan cuoi minh gui thi cai debouncedValue moi thay doi no moi thuc hien useEffect()
// }, [debouncedValue]);
