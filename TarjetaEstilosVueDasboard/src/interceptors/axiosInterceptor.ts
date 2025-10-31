import axios from "axios";
import swal from 'sweetalert';
import Global from "@/Global";

const api = axios.create({
  baseURL: Global.url,  
});

// Interceptor de respuesta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const status = error.response.status;
      const message = error.response.data.message ? error.response.data.message : error.response.data.error;
        swal({
          title: status === 401 ? "Sesión expirada" : `Error ${status}`,
          text: status === 401 ? "Debe iniciar sesión nuevamente." : `${message}`,
          icon: "error",
        }).then(() => {
          if (status === 401) {
            localStorage.clear();
            window.location.href = "/";
          }
        });
      
    }
    return Promise.reject(error);
  }
);

export default api;