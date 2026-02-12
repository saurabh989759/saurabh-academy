import { useToast } from '../../hooks/useToast'
import { useEffect } from 'react'

export const Toaster = () => {
  const { toasts, dismiss } = useToast()

  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
      {toasts.map((toast) => (
        <Toast key={toast.id} toast={toast} onDismiss={() => dismiss(toast.id)} />
      ))}
    </div>
  )
}

interface ToastProps {
  toast: {
    id: string
    title: string
    description?: string
    variant: 'success' | 'error' | 'info' | 'warning'
  }
  onDismiss: () => void
}

const Toast = ({ toast, onDismiss }: ToastProps) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onDismiss()
    }, toast.variant === 'error' ? 7000 : 5000)

    return () => clearTimeout(timer)
  }, [toast, onDismiss])

  const variantStyles = {
    success: 'bg-green-50 border-green-200 text-green-800',
    error: 'bg-red-50 border-red-200 text-red-800',
    info: 'bg-blue-50 border-blue-200 text-blue-800',
    warning: 'bg-yellow-50 border-yellow-200 text-yellow-800',
  }

  return (
    <div
      className={`min-w-[300px] max-w-md rounded-lg border p-4 shadow-lg ${variantStyles[toast.variant]}`}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h4 className="font-semibold">{toast.title}</h4>
          {toast.description && <p className="mt-1 text-sm">{toast.description}</p>}
        </div>
        <button
          onClick={onDismiss}
          className="ml-4 text-gray-400 hover:text-gray-600"
          aria-label="Dismiss"
        >
          ×
        </button>
      </div>
    </div>
  )
}

