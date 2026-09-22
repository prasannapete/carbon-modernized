import { useEffect, useRef } from 'react';
import { Modal } from 'bootstrap';

// Drives a Bootstrap modal (backdrop + animation identical to the reference app)
// from React `open` state. Calls onClose when the modal is dismissed by the user.
export default function useBootstrapModal(open, onClose) {
  const ref = useRef(null);
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;

  useEffect(() => {
    const el = ref.current;
    if (!el) return undefined;
    const modal = Modal.getOrCreateInstance(el);
    const handleHidden = () => onCloseRef.current && onCloseRef.current();
    el.addEventListener('hidden.bs.modal', handleHidden);
    return () => {
      el.removeEventListener('hidden.bs.modal', handleHidden);
    };
  }, []);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const modal = Modal.getOrCreateInstance(el);
    if (open) modal.show();
    else modal.hide();
  }, [open]);

  return ref;
}
