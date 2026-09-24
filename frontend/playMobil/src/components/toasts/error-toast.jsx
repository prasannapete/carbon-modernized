// import { icon } from "../assets/assets";
import M from "materialize-css";
import getUniqueId from "../../utils/getUniqueId";

// A static method to get the toast HTML
const ErrorToast = (toastMessage) => {
  const id = getUniqueId();

  const html = `
  <div id="${id}" class="error-toast-container">
    <div class="toast-container-left flex-center">
      <img
        src=""
        class="toast-icon"
        alt="Error Toast"
      />
    </div>
    <div class="toast-container-right">
      <p class="toast-heading">
        Error
        <img
          class="grayColorFilter cursor-pointer"
          src=""
          alt="Close Toast"
          onclick="dismissToast('${id}')"
        />
      </p>
      <p class="toast-desc">${toastMessage}</p>
    </div>
  </div>
  `

  M.toast({html});
};

export default ErrorToast;
