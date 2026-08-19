import { useState, useRef, type DragEvent, type ChangeEvent } from 'react'

interface DropzoneProps {
  onFileSelect?: (file: File | null) => void
  accept?: string
  maxSizeMB?: number
  preview?: string | null
  onPreviewChange?: (url: string | null) => void
  onValueChange?: (url: string) => void
  label?: string
}

export default function Dropzone({
  onFileSelect,
  accept = 'image/*',
  maxSizeMB = 5,
  preview,
  onPreviewChange,
  onValueChange,
  label = 'Upload file',
}: DropzoneProps) {
  const [isDragging, setIsDragging] = useState(false)
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [uploading, setUploading] = useState(false)
  const [progress, setProgress] = useState(0)
  const [error, setError] = useState<string | null>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  const handleFile = (file: File) => {
    setError(null)
    setProgress(0)
    setSelectedFile(file)

    const reader = new FileReader()
    reader.onload = (e) => {
      const result = e.target?.result as string
      onPreviewChange?.(result)
      onFileSelect?.(file)
    }
    reader.readAsDataURL(file)
  }

  const onDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault()
    setIsDragging(false)
    const file = e.dataTransfer.files?.[0]
    if (file) handleFile(file)
  }

  const onInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) handleFile(file)
    e.target.value = ''
  }

  const remove = () => {
    setSelectedFile(null)
    setError(null)
    setProgress(0)
    onPreviewChange?.(null)
    onValueChange?.('')
    onFileSelect?.(null)
    if (inputRef.current) {
      inputRef.current.value = ''
    }
  }

  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>

      {!preview && (
        <div
          onDragOver={(e) => { e.preventDefault(); setIsDragging(true) }}
          onDragLeave={() => setIsDragging(false)}
          onDrop={onDrop}
          onClick={() => inputRef.current?.click()}
          className={`
            relative border-2 border-dashed rounded-xl p-6 text-center cursor-pointer
            transition-colors duration-200
            ${isDragging ? 'border-primary-500 bg-primary-50' : 'border-gray-300 hover:border-gray-400'}
            ${uploading ? 'opacity-75 pointer-events-none' : ''}
          `}
        >
          <input
            ref={inputRef}
            type="file"
            accept={accept}
            onChange={onInputChange}
            className="hidden"
          />

          <div className="flex flex-col items-center gap-2">
            <svg className="w-8 h-8 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            <p className="text-sm text-gray-600 font-medium">
              {isDragging ? 'Drop file here' : 'Click or drag file to upload'}
            </p>
            <p className="text-xs text-gray-400">PNG, JPG up to {maxSizeMB}MB</p>
          </div>
        </div>
      )}

      {uploading && (
        <div className="mt-2">
          <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
            <div
              className="bg-primary-600 h-full rounded-full transition-all duration-200"
              style={{ width: `${progress}%` }
              }
            />
          </div>
          <p className="text-xs text-gray-500 mt-1">{progress}%</p>
        </div>
      )}

      {preview && !uploading && selectedFile && (
        <div className="mt-2 flex items-center gap-3">
          <img src={preview} alt="Preview" className="h-16 w-16 object-cover rounded-lg border border-gray-200" />
          <div className="flex-1">
            <p className="text-sm text-gray-600">{selectedFile.name}</p>
          </div>
          <button
            type="button"
            onClick={remove}
            className="text-sm text-red-600 hover:text-red-700 font-medium"
          >
            Remove
          </button>
        </div>
      )}

      {error && <p className="text-sm text-red-600 mt-1">{error}</p>}
    </div>
  )
}
