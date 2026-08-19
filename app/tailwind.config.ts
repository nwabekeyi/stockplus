import { defineConfig } from 'tailwindcss'

export default defineConfig({
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eaedfe',
          100: '#d0d8fd',
          500: '#254BF5',
          600: '#1e3dc4',
          700: '#172f98',
        }
      }
    }
  },
  plugins: []
})