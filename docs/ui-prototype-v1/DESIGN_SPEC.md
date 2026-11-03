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
6. Platform category, settlement account audit, settlement ledger and payment channels.

### Merchant admin

1. Operations overview and task navigation.
2. Product, category and order management.
3. Reviews, coupons, seckill and group-buy.
4. Shipping, customer service and shop settings.
5. Settlement records, collection account and independent App payment settings.

## App visual language

- Use a white-first, flat canvas. Secondary pages use a near-white page background with white content sections separated by `10px` whitespace bands.
- Every secondary-page title bar is `48px` high with a white background. TabBar root pages and immersive product detail pages follow their dedicated shell rules.
- Primary form, confirmation and fixed-bottom actions use one `44px` control height and `10px` radius. Product-detail action bars and authentication hero actions may keep their role-specific height.
- Brand green is reserved for primary actions, active states and positive values. Price red is reserved for prices, discounts and destructive commerce emphasis.
- Ordinary content sections do not use shadows. Shadows are limited to device chrome, modal sheets and temporary overlays.
- Borders are not used to wrap every section. Prefer whitespace and background changes; retain subtle borders only where a coupon, selectable option or product card needs a clear boundary.
- Lists use inset separators and one shared end-of-list boundary. Empty states, loading states and list boundaries are visually distinct.
- Form controls use the shared subtle surface, consistent labels and full-width alignment. Native checkboxes are replaced by the shared switch or selection treatment in the prototype.
- Authentication, privacy, notification, order, marketing, points, address, review and after-sales pages share these rules. Business-specific hero colors are allowed only when they communicate state or account value.

## Admin visual language

- The default shell uses a light left navigation and compact readable content density. Desktop navigation starts expanded, supports a 68px collapsed state and preserves the user's last choice; narrow layouts may collapse automatically.
- Platform and merchant consoles share one content system. Their differences come from permissions, fields and workflows rather than separate visual implementations.
- The navigation shell and business content are independent. Navigation position, navigation theme, content density and collapsed state must not require duplicated pages.
- YeahBoot provides framework layout abilities such as left or top navigation, tabs, breadcrumbs and dynamic menu rendering. YeahMall owns mall-specific filters, tables, detail drawers, audit dialogs and semantic status presentation.
- Admin body text and table cells use 13px, supporting text uses 11-12px, section titles use 15-16px and dialog or drawer titles use 18-20px. Compact density must never reduce business text to miniature overview sizing.
- The page canvas uses the paper background, white functional panels, subtle separators and almost no shadows. Shadows are reserved for drawers, dialogs and floating menus.
- List pages contain one filter panel and one table panel. Query controls stay together; create, export and batch actions form a separate right-aligned group in the same panel.
- Tables show column headers, rows and right-aligned pagination only. Duplicate page titles, record-count toolbars and default refresh controls are omitted.
- Read-heavy details use drawers. Short create or edit flows use dialogs. Long product editors and settings use full content pages. Read-only fields use descriptions or visually muted surfaces rather than enabled-looking inputs.
- Semantic colors are stable: brand for primary and positive actions, warning for pending work, danger for rejection, refund or destructive actions, price for monetary emphasis and neutral gray for closed or disabled states.
- Page tabs remain available for frequent task switching. They are compact, closable and independent from module tabs inside a business page.

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

## Admin information architecture

Platform and merchant consoles both use “经营概览” as the fixed first entry. The route and dashboard data differ by login subject, while the navigation label remains consistent.

Platform navigation follows this structure:

```text
经营概览
商城管理：商家管理 / 店铺管理 / 平台分类 / 会员管理 / 评价管理 / 积分管理 / 积分商城 / 首页运营 / 意见反馈
秒杀管理：活动管理 / 场次管理 / 报名审核 / 商品管理 / 实时监控 / 成交分析
拼团管理：活动管理 / 申请审核 / 商品管理 / 订单管理
资金结算：结算管理
系统管理：用户管理 / 角色管理 / 资源管理 / 组织管理 / 岗位管理 / 字典管理 / 系统参数 / 系统日志 / 地区管理 / 文件管理 / 存储管理
```

Merchant navigation follows this structure:

```text
经营概览
商品管理：SPU 管理 / 商品分类 / 规格管理
交易服务：订单管理 / 评价管理 / 客服消息
优惠券管理：优惠券 / 领取记录 / 使用记录
秒杀管理：活动列表 / 报名记录 / 秒杀商品
拼团管理：活动列表 / 申请记录 / 拼团商品 / 拼团订单
店铺管理：轮播图 / 物流设置 / 店铺设置
资金结算：结算管理
```

“首页运营”保持一个侧栏入口，Banner 与快捷入口放在页面内部 Tab；“结算管理”从商城或商品管理中独立为一级业务域；SKU 作为 SPU 的关联管理页，从商品操作进入，不单独占用侧栏菜单。

## Interaction requirements

- Every visible control in the prototype must respond.
- The home search field is a navigation entry rather than an inline input; clicking it opens the dedicated product search page.
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

The prototype is a reference artifact. UI rollout must not require backend Controller, DTO, Service, Mapper or API-contract changes. Yeahboot-owned pages and layout components may receive UI-only changes such as design tokens, spacing, color, typography and visual composition, while their routes, permissions, APIs, state management and business behavior remain unchanged. Prefer a small shared theme layer over repeated page-level edits so future yeahboot merges stay reviewable. Mall-owned pages may be adapted more deeply when required by the prototype, provided existing business behavior is preserved or intentionally improved.
