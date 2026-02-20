interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg'
  text?: string
}

export const LoadingSpinner = ({ size = 'md', text }: LoadingSpinnerProps) => {
  const sizeClasses = {
    sm: 'h-6 w-6',
    md: 'h-10 w-10',
    lg: 'h-14 w-14',
  }

  return (
    <div className="flex flex-col items-center justify-center py-16">
      <div className={`animate-spin rounded-full border-2 border-slate-200 border-t-violet-600 ${sizeClasses[size]}`} />
      {text && <p className="mt-4 text-sm text-slate-500">{text}</p>}
    </div>
  )
}
