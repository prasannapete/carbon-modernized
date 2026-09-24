import { icon } from "../assets/assets";
import M from "materialize-css";
import getUniqueId from "../../utils/getUniqueId";

// A static method to get the toast HTML
const SuccessToast = (toastMessage) => {
  const id = getUniqueId();
  const html = `
  <div id="${id}" class="success-toast-container">
    <div class="toast-container-left flex-center">
      <img
        src="${icon.greenFaceIcon}"
        class="toast-icon"
        alt="Success Toast"
      />
    </div>
    <div class="toast-container-right">
      <p class="toast-heading">
        Success
        <img
          class="grayColorFilter cursor-pointer"
          src="${icon.crossIcon}"
          altText="Close Toast"
          onclick="dismissToast('${id}')"
        />
      </p>
      <p class="toast-desc">${toastMessage}</p>
    </div>
  </div>
  `

  M.toast({html});
};

export default SuccessToast;
