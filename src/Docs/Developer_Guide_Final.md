# Developer Guide (Modernization)

## 1. Overview

This guide instructs developers (and AI agents) on how to apply the new "Modern Bootstrap" design system to the JobMoa project.

## 2. IntelliJ Setup

- **Live Templates:**
  - Create a template `bs5-card`:

        ```html
        <div class="card card-modern border-0 shadow-sm mb-4">
            <div class="card-header bg-transparent border-0 pt-3">
                <h3 class="card-title fw-bold">$Title$</h3>
            </div>
            <div class="card-body">
                $END$
            </div>
        </div>
        ```

  - Create a template `bs5-btn`:

        ```html
        <button type="button" class="btn btn-primary rounded-3 px-4">$Text$</button>
        ```

## 3. Development Workflow

1. **Check Design System:** Refer to `src/Docs/Custom_Design_System.md` for variable names.
2. **Edit CSS:** Modify `src/main/webapp/css/custom-modern.css`.
3. **Apply to JSP/Tags:** Update classes in `.jsp` or `.tag` files.
4. **Browser Verify:** Check usage in Chrome/Edge, ensure no layout breakage.

## 4. Key Implementation details

### GNB & Sidebar

The `gnb.tag` file controls the global layout.

- **Header:** `navbar-expand`, `bg-body`
- **Sidebar:** `app-sidebar`
- **Icons:** Use `bi bi-*` consistently.

### CSS Compilation

Currently, we use raw CSS inclusion.
Ensure `<link rel="stylesheet" href="/css/custom-modern.css">` is added to your `header.jsp` or common layout file.

## 5. FAQ

**Q: Can I use inline styles?**
A: Avoid if possible. Use Bootstrap utilities (`p-3`, `text-center`, `fw-bold`) or helper classes.

**Q: Where are the icons from?**
A: Bootstrap Icons (`<i class="bi bi-list"></i>`).

**Q: How do I make a table look modern?**
A: Use `<table class="table table-hover align-middle">` and ensure standard padding.
