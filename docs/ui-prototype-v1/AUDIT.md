# Yeah Mall UI audit

## Scope

This audit covers mall-owned screens only:

- App: discovery, shops, products, cart, payment, order lifecycle, after-sales, reviews, profile, marketing, points and service.
- Platform admin: all current platform mall pages are mapped through real menu groups and module tabs.
- Merchant admin: all current merchant mall pages are mapped, including SPU, SKU, specifications and shop operations.
- Yeahboot system-management entries remain visible for shell fidelity but are explicitly excluded from visual redesign.

The following yeahboot-owned areas are protected and must not be restyled by this work:

- Admin layout, router, request layer and authentication shell.
- Shared system-management views.
- Shared Element Plus wrappers such as the global pagination component.
- Framework-wide CSS and theme entry files.

## Current UI findings

### App

- Spacing, radii, shadows and page backgrounds vary by page.
- Functional icons mix text, emoji and icon-font glyphs.
- Product, order, address and profile cards use unrelated visual rules.
- Primary actions use several unrelated colors.
- Empty, loading, error and disabled states do not share one pattern.
- Several pages rely on large decorative cards that do not improve task clarity.
- Inline styles make global refinement difficult.

### Platform and merchant admin

- Search areas alternate between cards, gray bands and ungrouped inline forms.
- Page headers, toolbars and table actions have inconsistent height and alignment.
- Table action columns become unnecessarily wide because every action is visible.
- Details alternate between dialogs, drawers, collapses and toast-only feedback.
- Status colors and labels are repeated locally instead of following one semantic map.
- Pagination placement and surrounding spacing vary by page.
- Element Plus defaults are visible without a mall-specific visual layer.

## Design response

- Use one mall-only token set and component vocabulary.
- Keep the admin shell from yeahboot; redesign only the content region of mall pages.
- Use a compact filter bar, a predictable table toolbar, fixed action hierarchy and drawers for read-heavy details.
- Use product imagery, price, stock and order state as the App's visual hierarchy.
- Replace decorative containers with meaningful grouping and separators.
- Provide the same loading, empty, error, success and disabled language across flows.

## Merge-safe implementation boundary

- New admin UI code should live under a mall-specific directory such as `src/views/mall/_ui`.
- Mall pages opt into the visual layer explicitly.
- Do not edit framework layout, global style entry, router generation or system pages.
- App may own a dedicated theme file and mall components because it is not merged from yeahboot.
- Rollout commits should be grouped by business module to keep future upstream merges reviewable.
