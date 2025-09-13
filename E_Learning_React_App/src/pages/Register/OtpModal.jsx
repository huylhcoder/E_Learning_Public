import { useState } from "react";
import { Modal, Button } from "react-bootstrap";

export default function OtpModal({ show, onClose, onSubmit }) {
  const [otp, setOtp] = useState("");

  const handleSubmit = () => {
    if (!otp.trim()) {
      return;
    }
    onSubmit(otp);
  };

  return (
    <Modal show={show} onHide={onClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>Nhập mã xác nhận</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <input
          type="text"
          className="form-control"
          placeholder="Nhập mã OTP"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
        />
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          Đóng
        </Button>
        <Button variant="primary" onClick={handleSubmit}>
          Xác nhận
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
