import React from 'react';
import { Table, Button } from 'react-bootstrap';
import { FaEdit, FaTrash } from 'react-icons/fa';

const CategoryTable = ({ categories, onEdit, onDelete }) => {
    console.log(categories);

    return (
        <Table bordered hover>
            <thead>
                <tr>
                    <th>Tên danh mục</th>
                    <th>Slug</th>
                    <th>Danh mục cha</th>
                    <th>Tổng số khóa học</th>
                    <th>Danh mục con</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                {categories.map((cat) => (
                    <tr key={cat.categoryId}>
                        <td>{cat.name}</td>
                        <td>{cat.slug}</td>
                        <td>                    
                            <span className="badge text-primary border border-primary bg-light">
                                {cat.parent?.name || ''}
                            </span>                            
                        </td>
                        <td>{cat.courseCount}</td>
                        <td>{cat.childrenCount}</td>
                        <td>
                            <Button size="sm" variant="warning" className="me-2" onClick={() => onEdit(cat)}>
                                <FaEdit />
                            </Button>
                            <Button size="sm" variant="danger" onClick={() => onDelete(cat.categoryId)}>
                                <FaTrash />
                            </Button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </Table>
    );
};

export default CategoryTable;
