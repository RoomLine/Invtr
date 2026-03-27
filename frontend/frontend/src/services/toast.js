import { reactive } from 'vue'

// Global reactive list of active toasts
export const toasts = reactive([])

let _nextId = 0

/**
 * Add a toast notification
 * @param {string} message
 * @param {'success'|'error'|'warning'|'info'} type
 * @param {number} duration  ms before auto-dismiss (0 = sticky)
 */
function addToast(message, type = 'info', duration = 3800) {
  const id = ++_nextId
  toasts.push({ id, message, type })
  if (duration > 0) {
    setTimeout(() => removeToast(id), duration)
  }
}

export function removeToast(id) {
  const idx = toasts.findIndex(t => t.id === id)
  if (idx !== -1) toasts.splice(idx, 1)
}

/** Convenience helpers — mirror the spec: toast.success() / toast.error() etc. */
export const toast = {
  success: (msg, duration)  => addToast(msg, 'success', duration),
  error:   (msg, duration)  => addToast(msg, 'error',   duration ?? 5000),
  warning: (msg, duration)  => addToast(msg, 'warning', duration),
  info:    (msg, duration)  => addToast(msg, 'info',    duration),
}
