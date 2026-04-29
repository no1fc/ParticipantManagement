# Custom Design System

**Based on:** Bootstrap 5.3 + AdminLTE v4
**File:** `src/main/webapp/css/custom-modern.css`

## 1. Global Variables (`:root`)

We define a modern color palette and spacing system here. These variables override or complement BS5 defaults.

```css
:root {
    /* Brand Colors - Premium & Trustworthy */
    --brand-primary: #4361ee;       /* Vibrant Blue */
    --brand-secondary: #3f37c9;     /* Deep Blue */
    --brand-success: #4cc9f0;       /* Cyan/Teal for positivity */
    --brand-danger: #f72585;        /* Pink/Red for alerts */
    --brand-light: #f8f9fa;         /* Off-white background */
    --brand-dark: #212529;          /* Deep dark for text */

    /* UI Semantics */
    --bg-body: #f3f5f9;             /* Light gray/blue tint for app background */
    --bg-card: #ffffff;             
    --text-main: #2b2d42;
    --text-muted: #8d99ae;

    /* Modern Shadows */
    --shadow-sm: 0 2px 4px rgba(0,0,0,0.05);
    --shadow-md: 0 4px 6px rgba(0,0,0,0.07);
    --shadow-lg: 0 10px 15px -3px rgba(0,0,0,0.1);
    --shadow-hover: 0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04);

    /* Border Radius */
    --radius-sm: 0.5rem;
    --radius-md: 0.75rem;
    --radius-lg: 1rem;
}
```

## 2. Component Styles

### Cards (`.card-modern`)

Replace standard AdminLTE cards with a cleaner look.

- **Background:** White (`--bg-card`)
- **Border:** None (`border: 0`)
- **Shadow:** Soft (`box-shadow: var(--shadow-md)`)
- **Radius:** Rounded (`border-radius: var(--radius-md)`)
- **Header:** Transparent background, bold title.

### Navbar (`.app-header`)

- **Style:** Glassmorphism or Clean White.
- **Structure:** `navbar-expand`, `bg-body` (or custom white).
- **Items:** Hover effects with subtle background change.

### Sidebar (`.app-sidebar`)

- **Background:** Dark Slate or Deep Blue (currently `#77706b` in `gnb.tag` - recommend shifting to a codified variable).
- **Brand:** Cleaner logo area.
- **Menu:** `nav-link` with rounded corners and hover effects.

## 3. Consistency Guardrails (AI Rules)

When creating new pages:

1. **Wrapper:** Always wrap content in `app-content` > `container-fluid`.
2. **Cards:** Use `<div class="card card-modern shadow-sm border-0 mb-4">`.
3. **Buttons:** Use `btn-primary` (mapped to `--brand-primary`) with `rounded-3`. Avoid `btn-flat`.
4. **Typography:** Use `fw-bold` for headers, `text-muted` for descriptions.
5. **Spacing:** Use `gap-2`, `mb-3`, `p-4` for breathable layouts.

## 4. Anti-Patterns (Do Not Use)

- ❌ `box-shadow: none;` on main content cards (makes them look flat/old).
- ❌ Heavy borders (`border: 1px solid #ddd`).
- ❌ Default Bootstrap blue (`#0d6efd`) - use `--brand-primary` instead.
- ❌ `btn-flat` (AdminLTE legacy style) - use rounded buttons.
