import React, { useEffect, useState } from 'react';
import axios from '~/utils/CustomizeAxios';
import CategoryToolbar from './CategoryToolbar';
import CategoryTable from './CategoryTable';
import CategoryPagination from './CategoryPagination';
import CategoryOffcanvas from './CategoryOffcanvas';

const generateSlug = (text) =>
    text
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/[^a-z0-9 ]/g, '')
        .trim()
        .replace(/\s+/g, '-');

const CategoryManager = () => {
    const [categories, setCategories] = useState([]);
    const [filter, setFilter] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [editingCategory, setEditingCategory] = useState(null);
    const [categoryTree, setCategoryTree] = useState([]);

    const [currentPage, setCurrentPage] = useState(1);
    const categoriesPerPage = 5;
    const tokenLogin = localStorage.getItem('token');

    useEffect(() => {
        fetchCategories();
        fetchCategoryTree();
    }, []);

    const fetchCategories = async () => {
        try {
            const resp = await axios.get(`/category-manager/list-category`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            setCategories(resp.data);
        } catch (error) {
            console.error('Lỗi fetch categories:', error);
        }
    };

    const fetchCategoryTree = async () => {
        const res = await axios.get('/category-manager/tree', {
            headers: { Authorization: `Bearer ${tokenLogin}` },
        });
        setCategoryTree(res.data);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Bạn có chắc chắn muốn xóa danh mục này?')) {
            await axios.delete(`/category-manager/${id}`, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
            fetchCategories();
        }
    };

    const handleSave = async () => {
        //console.log(editingCategory);
        if (editingCategory.categoryId) {
            await axios.put(`/category-manager/update-category/${editingCategory.categoryId}`, editingCategory, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
        } else {
            await axios.post(`/category-manager/add-category`, editingCategory, {
                headers: { Authorization: `Bearer ${tokenLogin}` },
            });
        }
        setShowModal(false);
        fetchCategories();
        fetchCategoryTree();
    };

    const handleGenerateSlug = () => {
        const slug = generateSlug(editingCategory.name || '');
        setEditingCategory({ ...editingCategory, slug });
    };

    const renderTreeOptions = (nodes, level = 0) =>
        nodes.map((node) => (
            <React.Fragment key={node.categoryId}>
                <option value={node.categoryId} disabled={node.children && node.children.length > 0}>
                    {'-'.repeat(level)} {node.name}
                </option>
                {node.children && node.children.length > 0 && renderTreeOptions(node.children, level + 1)}
            </React.Fragment>
        ));

    const handleRemoveParent = async (id) => {
        if (window.confirm('Bạn có chắc chắn muốn hủy danh mục cha?')) {
            try {
                const updated = {
                    categoryId: editingCategory.categoryId,
                    name: editingCategory.name,
                    slug: editingCategory.slug,
                    parentId: null, // 👈 quan trọng
                };

                await axios.put(`/category-manager/update-category/${id}`, updated, {
                    headers: { Authorization: `Bearer ${tokenLogin}` },
                });

                fetchCategories();
                fetchCategoryTree();
                setEditingCategory(updated);
            } catch (err) {
                console.error('Lỗi khi xóa danh mục cha:', err);
            }
        }
    };

    const filteredCategories = categories.filter((c) => c.name.toLowerCase().includes(filter.toLowerCase()));
    const indexOfLast = currentPage * categoriesPerPage;
    const indexOfFirst = indexOfLast - categoriesPerPage;
    const currentCategories = filteredCategories.slice(indexOfFirst, indexOfLast);

    return (
        <div className="container mt-3">
            <p className="fs-2 fw-bold">Quản lý danh mục khóa học</p>

            <CategoryToolbar
                filter={filter}
                setFilter={setFilter}
                onAdd={() => {
                    setEditingCategory({ name: '', slug: '', parentId: null });
                    setShowModal(true);
                }}
            />

            <CategoryTable
                categories={currentCategories}
                onEdit={(cat) => {
                    setEditingCategory({
                        ...cat,
                        parentId: cat.parent ? cat.parent.categoryId : null,
                    });
                    setShowModal(true);
                }}
                onDelete={handleDelete}
            />

            <CategoryPagination
                total={filteredCategories.length}
                perPage={categoriesPerPage}
                currentPage={currentPage}
                setCurrentPage={setCurrentPage}
            />

            {/* <CategoryOffcanvas
                show={showModal}
                onHide={() => setShowModal(false)}
                editingCategory={editingCategory}
                setEditingCategory={setEditingCategory}
                onSave={handleSave}
                renderTreeOptions={renderTreeOptions}
                handleGenerateSlug={handleGenerateSlug}
                categoryTree={categoryTree}
            /> */}

            <CategoryOffcanvas
                show={showModal}
                onHide={() => setShowModal(false)}
                editingCategory={editingCategory}
                setEditingCategory={setEditingCategory}
                onSave={handleSave}
                renderTreeOptions={renderTreeOptions}
                handleGenerateSlug={handleGenerateSlug}
                categoryTree={categoryTree}
                onRemoveParent={handleRemoveParent} // 👈 thêm
            />
        </div>
    );
};

export default CategoryManager;
