import { useEffect, useState } from "react";
import { introspect } from "../AuthenticationService";

export const UseAuth = ({ loggedOut }) => {
  const [isTokenValid, setIsTokenValid] = useState(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
      const introspectToken = async () => {
          if (!token || loggedOut) {
              setIsTokenValid(false);
              return;
          }
          try {
              const result = await introspect(token);
              setIsTokenValid(result.valid);
          } catch (error) {
              console.error("Error introspecting token:", error);
              setIsTokenValid(false);
          }
      };

      introspectToken();
  }, [token, loggedOut]);

  return { isTokenValid };
};
