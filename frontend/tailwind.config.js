/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{svelte,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        "surface-variant": "#273647",
        "outline-variant": "#3b494c",
        "error-container": "#93000a",
        "secondary-fixed-dim": "#e0b6ff",
        "background": "#051424",
        "surface-dim": "#051424",
        "on-surface-variant": "#bac9cc",
        "on-error": "#690005",
        "secondary-container": "#6d11ad",
        "inverse-surface": "#d4e4fa",
        "surface-container-highest": "#273647",
        "primary": "#c3f5ff",
        "error": "#ffb4ab",
        "primary-fixed": "#9cf0ff",
        "tertiary-container": "#d3d0cf",
        "on-tertiary": "#313030",
        "inverse-primary": "#006875",
        "tertiary-fixed": "#e5e2e1",
        "tertiary": "#efecec",
        "primary-fixed-dim": "#00daf3",
        "surface-container-lowest": "#010f1f",
        "on-tertiary-container": "#5a5959",
        "outline": "#849396",
        "on-tertiary-fixed": "#1c1b1b",
        "on-surface": "#d4e4fa",
        "on-primary": "#00363d",
        "on-error-container": "#ffdad6",
        "on-secondary-fixed-variant": "#6a0baa",
        "on-tertiary-fixed-variant": "#474646",
        "surface": "#051424",
        "surface-container-high": "#1c2b3c",
        "secondary-fixed": "#f2daff",
        "on-secondary-container": "#d7a4ff",
        "on-primary-fixed": "#001f24",
        "surface-container": "#122131",
        "secondary": "#e0b6ff",
        "tertiary-fixed-dim": "#c8c6c5",
        "on-primary-container": "#00626e",
        "on-secondary-fixed": "#2e004e",
        "surface-container-low": "#0d1c2d",
        "on-primary-fixed-variant": "#004f58",
        "inverse-on-surface": "#233143",
        "primary-container": "#00e5ff",
        "on-secondary": "#4c007d",
        "on-background": "#d4e4fa",
        "surface-tint": "#00daf3",
        "surface-bright": "#2c3a4c"
      },
      borderRadius: {
        "DEFAULT": "0.125rem",
        "lg": "0.25rem",
        "xl": "0.5rem",
        "full": "0.75rem"
      },
      spacing: {
        "unit": "0.25rem",
        "gutter": "1rem",
        "lg": "1.5rem",
        "margin-mobile": "1rem",
        "xs": "0.25rem",
        "xl": "2rem",
        "sm": "0.5rem",
        "md": "1rem",
        "margin-desktop": "3rem"
      },
      fontFamily: {
        "headline-xl": ["Geist", "sans-serif"],
        "body-md": ["Inter", "sans-serif"],
        "body-sm": ["Inter", "sans-serif"],
        "headline-lg-mobile": ["Geist", "sans-serif"],
        "label-caps": ["'JetBrains Mono'", "monospace"],
        "code-sm": ["'JetBrains Mono'", "monospace"],
        "headline-lg": ["Geist", "sans-serif"]
      },
      fontSize: {
        "headline-xl": [
          "2.5rem",
          {
            "lineHeight": "3rem",
            "letterSpacing": "-0.02em",
            "fontWeight": "700"
          }
        ],
        "body-md": [
          "1rem",
          {
            "lineHeight": "1.5rem",
            "fontWeight": "400"
          }
        ],
        "body-sm": [
          "0.875rem",
          {
            "lineHeight": "1.25rem",
            "fontWeight": "400"
          }
        ],
        "headline-lg-mobile": [
          "1.5rem",
          {
            "lineHeight": "2rem",
            "fontWeight": "600"
          }
        ],
        "label-caps": [
          "0.75rem",
          {
            "lineHeight": "1rem",
            "letterSpacing": "0.05em",
            "fontWeight": "500"
          }
        ],
        "code-sm": [
          "0.8125rem",
          {
            "lineHeight": "1.125rem",
            "fontWeight": "400"
          }
        ],
        "headline-lg": [
          "2rem",
          {
            "lineHeight": "2.5rem",
            "letterSpacing": "-0.01em",
            "fontWeight": "600"
          }
        ]
      }
    },
  },
  plugins: [
    import('@tailwindcss/forms'),
    import('@tailwindcss/container-queries')
  ],
}
