# Yeah Mall visual specification v1

## Direction: Shelf & Ledger

The App represents a well-kept retail shelf: products are prominent, labels are precise and purchase actions stay close to the user's thumb.

The admin represents an operations ledger: filters, tables, statuses and next actions are easy to scan without decorative dashboard furniture.

The shared signature is the shelf ticket: a compact rectangular marker used for section identity, order type and state. It carries information and is never added as decoration.

## Tokens

| Role | Value | Usage |
| --- | --- | --- |
| Ink | `#17251f` | Primary text and dark actions |
| Brand | `#24634f` | Primary action, active navigation |
| Brand soft | `#e8f2ed` | Selected filters and quiet highlights |
| Price | `#be412b` | Price and irreversible commerce emphasis |
| Paper | `#f4f6f3` | App and admin content background |
| Surface | `#ffffff` | Panels and controls |
| Line | `#dce4de` | Borders and separators |
| Muted | `#65726a` | Secondary information |
| Warning | `#986019` | Waiting and attention states |
| Danger | `#b6403a` | Error, refund and destructive action |

Spacing follows a 4px base grid. Common steps are `4, 8, 12, 16, 20, 24, 32, 40`.

App cards and content panels use a symmetric `12px` radius. Nested controls and compact media use `8–10px`; prominent identity/result marks use `16px`. Alternating corner radii are not used. Admin radii use `6, 8, 10`. Shadows are reserved for floating controls, sheets and sticky action bars.

Body typography uses Chinese system sans-serif fonts. Numeric and operational data use `IBM Plex Mono` where available. Display text uses a condensed sans-serif treatment rather than a decorative serif.

The 390px App canvas uses production-scale typography: 13–14px body text, 11–12px secondary text, 17–20px titles and at least 44px for primary touch targets. Admin tables and forms use 13–14px text rather than miniature overview sizing.

Small body and secondary text must maintain WCAG-readable contrast. Muted, warning and price tokens are deliberately darker than decorative pastel palettes so that 11–13px labels remain legible on their tinted surfaces.

## Prototype flows

### App

1. Home, category, shop and product-list discovery.
2. Product detail gallery, favorite state, reviews and SKU selection.
3. Cart, order confirmation, coupon selection, payment result and total calculation.
4. Order list, logistics, refund application and refund progress.
5. Review creation, image preview, detail and conditional multi-round reply.
6. Profile, favorites, history, coupons, points, addresses and preferences.
7. Seckill and group-buy activity, campaign product and group-selection flows.
8. Customer service, feedback, authentication and about pages.

### Platform admin

1. Operations overview and task navigation.
2. Merchant, shop and member management.
3. Points increase/decrease with validation and simulated balance update.
4. Order filtering and shipping dialog with logistics provider selection.
5. Reviews, points, points mall, seckill, group-buy, content and feedback.

### Merchant admin

1. Operations overview and task navigation.
2. Product, category and order management.
3. Reviews, coupons, seckill and group-buy.
4. Shipping, customer service and shop settings.

## Production mapping

| Prototype concept | Target implementation |
| --- | --- |
| Mobile product card | App product list/home shared component |
| Mobile bottom action bar | Product detail and order action component |
| Status timeline | Order, refund, group-buy and points-order details |
| Admin filter bar | Mall-only filter shell wrapping existing controls |
| Admin data table frame | Mall-only toolbar/table/pagination composition |
| Detail drawer | Member, order, review and marketing application details |
| Action menu | Row actions beyond the primary one |
| Semantic status ticket | Central mall status mapping |

## Interaction requirements

- Every visible control in the prototype must respond.
- Forms validate before state changes.
- Status changes update mock data and produce feedback.
- Mobile flows preserve selected SKU, quantity and coupon state.
- TabBar root pages never show a back button; secondary pages always provide an explicit return target.
- Quantity steppers use one fixed `116 × 36px` geometry with equal-height minus, value and plus cells.
- Profile values use a consistent 16px right inset and reserve a separate chevron area. Phone, gender and birthday rows open task-specific sheets instead of behaving like inert text.
- Admin filters operate on mock data rather than acting as static decoration.
- Keyboard focus is visible and reduced-motion preferences are respected.
- Ordinary admin list pages rely on the existing sidebar and breadcrumb, so they do not repeat a large page title and description block.
- Dashboards and editors also rely on the breadcrumb for page identity; necessary context belongs inside the relevant metric, section or form card rather than in a repeated page heading.
- List-page create, export and batch actions live in the filter area's right action group; they never occupy a standalone row.
- Filter fields, reset and query form one continuous query group. Business actions follow as a separate right-aligned group and never interrupt the query sequence.
- Mall data tables contain only column headers, rows and pagination. Duplicate table titles, record-count headers and default refresh buttons are omitted.

## Multi-platform App adaptation

- The prototype defaults to WeChat Mini Program chrome because that is the first-release target.
- Custom-navigation pages reserve the top-right capsule area. Search, title and navigation controls align to capsule bounds rather than hard-coded top offsets.
- Production Mini Program code derives status-bar, capsule and safe-area geometry from runtime APIs such as `getMenuButtonBoundingClientRect`, `statusBarHeight` and `safeAreaInsets`.
- Android and iOS builds reuse content components and design tokens, while their shell adapter removes the WeChat capsule and applies native status-bar, back-navigation and bottom-safe-area behavior.
- Fixed purchase bars and tab bars include the platform bottom inset; content scrolling height is calculated from the active platform shell.
- Platform differences belong in navigation and safe-area adapters, not in duplicated business pages.

## Upstream merge policy

The prototype is a reference artifact. Production implementation must remain opt-in and mall-scoped. No visual change should require editing a yeahboot framework file unless the user explicitly approves that exact upstream-facing change.
