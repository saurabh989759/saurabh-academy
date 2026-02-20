interface ErrorDisplayProps {
  error: Error | unknown
  title?: string
  onRetry?: () => void
}

export const ErrorDisplay = ({ error, title = 'Error', onRetry }: ErrorDisplayProps) => {
  const message = error instanceof Error ? error.message : 'An unknown error occurred'

  return (
    <div className="bg-red-50 border border-red-100 rounded-2xl p-6">
      <div className="flex items-start gap-3">
        <div className="flex-shrink-0 w-9 h-9 bg-red-100 rounded-xl flex items-center justify-center">
          <svg className="h-5 w-5 text-red-500" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
          </svg>
        </div>
        <div className="flex-1">
          <h3 className="text-sm font-semibold text-red-800">{title}</h3>
          <p className="mt-1 text-sm text-red-600">{message}</p>
          {onRetry && (
            <button
              onClick={onRetry}
              className="mt-3 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors text-sm font-medium"
            >
              Try again
            </button>
          )}
        </div>
      </div>
    </div>
  )
}
