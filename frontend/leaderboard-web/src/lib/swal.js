import Swal from 'sweetalert2';

// The reference app used this image as the SweetAlert confirm button everywhere.
export const CONFIRM_BTN =
  '<img src="/images/icons/submit-icon-selected.svg" width="30px" height="30px" style="margin-left:-3px">';

export function swalSuccess(text, opts = {}) {
  return Swal.fire({
    title: 'Success',
    text,
    confirmButtonText: CONFIRM_BTN,
    allowOutsideClick: false,
    ...opts,
  });
}

export function swalError(text, opts = {}) {
  return Swal.fire({
    title: 'Something went wrong',
    text,
    confirmButtonText: CONFIRM_BTN,
    ...opts,
  });
}

export default Swal;
