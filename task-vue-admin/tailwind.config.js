/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      width: {
        '120': '120px',
        '300': '300px',
        '400': '400px',
        '440': '440px',
        '1200': '1200px'
      },
      height: {
        '600': '600px',
      }
    },
  },
  plugins: [],
}