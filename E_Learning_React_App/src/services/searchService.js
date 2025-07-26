import axios from '~/utils/CustomizeAxios';

export const search = async (query) => {
    const res = await axios.get(`/course/suggestions`, {
        params: { query },
    });
    return res.data;
};
