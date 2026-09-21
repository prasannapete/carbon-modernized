import getUniqueId from "../../utils/getUniqueId";
import { icon } from "../assets/assets";
import M from "materialize-css";

// A static method to get the toast HTML
const WarningToast = (toastMessage) => {
  const id = getUniqueId();
  const html = `
  <div id="${id}" class="warning-toast-container">
    <div class="toast-container-left flex-center">
      <img
        src="${icon.yellowFaceIcon}"
        class="toast-icon"
        alt="Warning Toast"
      />
    </div>
    <div class="toast-container-right">
      <p class="toast-heading">
        Warning
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

export default WarningToast;
