# Modernization Master Plan

**Project:** Legacy UI Modernization (AdminLTE v4 + Bootstrap 5)
**Version:** 1.0.0
**Date:** 2026-02-13

## 1. Vision & Principles

To transform the existing legacy Admin interface into a premium, modern web application using **pure Bootstrap 5** and **CSS variables**, without relying on utility frameworks like Tailwind CSS.

### Core Design Principles

1. **Modern Minimalism:** Reduce visual clutter, maximize whitespace, and use a refined color palette.
2. **Soft & Fluid:** Replace harsh borders with soft shadows and rounded corners (Card-borderless, Soft-shadow).
3. **Bootstrap Native:** Leverage Bootstrap 5's utility classes (`m-*`, `p-*`, `d-*`, `shadow-*`) to their fullest extent.
4. **CSS Variable Driven:** Control theming (colors, spacing, typography) via global CSS variables for easy maintenance.

## 2. Technical Stack

- **Framework:** Spring Boot 3.4.1 (JSP/Taglibs)
- **UI Library:** AdminLTE v4.0.0-beta3 (Bootstrap 5 based)
- **CSS Strategy:**
  - `bootstrap.min.css` (Core)
  - `adminlte.min.css` (Layout Structure)
  - `custom-modern.css` (Theming & Overrides - **NEW**)
- **Icons:** Bootstrap Icons (`bi-*`)

## 3. Migration Priority

The modernization will proceed in the following order to maximize impact and maintain stability:

| Phase | Target Area | Description | Status |
| :--- | :--- | :--- | :--- |
| **Phase 1** | **Global Layout (`tags`)** | Modernize `gnb.tag` (Header) and Sidebar. Apply new color scheme and layout spacing. | **In Progress** |
| **Phase 2** | **Design System** | Establish `custom-modern.css` with CSS variables for colors, typography, and shadows. | **In Progress** |
| **Phase 3** | **Dashboard** | Refactor `dashboard.login` components to use the new card styles and charts. | Planned |
| **Phase 4** | **Forms & Tables** | Apply modern styling to input fields (floating labels) and data tables (hover, clean borders). | Planned |
| **Phase 5** | **Content Pages** | Roll out changes to standard content pages. | Planned |

## 4. Work Rules

- **No Tailwind:** Do not introduce Tailwind CSS.
- **Preserve `adminlte.js`:** Ensure class changes do not break sidebar toggles or dropdowns.
- **Mobile First:** Verify layouts on mobile breakpoints (`d-md-block`, etc.).
- **JSP Compatibility:** Ensure EL expressions (`${...}`) remain intact during HTML refactoring.
