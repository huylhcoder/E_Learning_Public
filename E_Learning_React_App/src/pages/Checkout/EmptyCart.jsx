import { useNavigate } from 'react-router-dom';

const EmptyCart = () => {
  const navigate = useNavigate();
  return (
    <div className="text-center">
      <img
        src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTZLhV29HaDKqmUqIqveXIBCyMdcaINrOyLmA&s"
        alt="Empty"
        className="img-fluid"
      />
      <h3 className="text-danger fs-3">Chưa có khóa học trong đơn hàng</h3>
      <p>Trước khi thanh toán, hãy chọn khóa học</p>
      <button className="btn btn-outline-success" onClick={() => navigate('/course/search')}>
        Xem thêm
      </button>
    </div>
  );
};

export default EmptyCart;
