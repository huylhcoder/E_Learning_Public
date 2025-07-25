import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import AuthContext from "../../context/AuthContext";

export const HandleLogout = () => {
  const authContext = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      console.log("No token found");
      navigate("/login");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/v1/auth/logout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ token }),
      });

      if (response.ok) {
        localStorage.clear();
        authContext.refresh();
        navigate("/login");
      } else {
        const errorData = await response.json();
        console.log(
          "Logout Failed: ",
          errorData.message || response.statusText
        );
      }
    } catch (error) {
      console.log("Logout error: ", error);
    }
  };

  return { handleLogout };
};
