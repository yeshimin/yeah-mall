# Yeah Mall UI coverage matrix

This matrix separates actual registered pages from supplementary interaction references. A page is covered when it has either a dedicated high-fidelity screen or an explicit mapping to a shared prototype template.

Coverage boundary:

- Current page-inventory coverage is 100% for the 41 registered App pages and 40 mall-owned admin business pages listed below.
- This does not mean every CRUD field, validation branch, permission combination, loading/empty/error state or platform-specific interaction has an independent mockup.
- Unique and high-risk flows receive dedicated interactive screens. Repetitive CRUD pages map to reviewed shared templates and are refined again when production implementation reaches that module.

## Production rollout status

Prototype coverage and production rollout are tracked separately. The prototype covers the registered page inventory, while the production UI is being applied in reviewable batches so existing API, permission and transaction behavior can be preserved.

Current production batch:

- App shared shell: global tokens and capsule-aware custom navigation.
- App pages: home, category, cart, profile, product list, product detail, order confirmation, order list and order detail.
- Admin shared shell: yeahboot-compatible visual theme for login, sidebar, header, Element Plus controls and system-management pages.
- Admin mall pages: merchant, shop, member, SPU, order, review and member-points management.

Pages not named above remain prototype-covered but are not yet considered production-restyled. A page is only marked complete after its business bindings are preserved, both target builds pass and the rendered page is visually reviewed.

## App: 41 registered pages

| Actual page | Prototype target | Coverage |
| --- | --- | --- |
| `pages/index/index` | `app/home` | Dedicated |
| `pages/category/category` | `app/category` | Dedicated |
| `pages/shop/shop` | `app/shops` | Dedicated |
| `pages/shop/detail` | `app/shop` | Dedicated |
| `pages/cart/cart` | `app/cart` | Dedicated |
| `pages/profile/profile` | `app/profile` | Dedicated |
| `pages/profile/points` | `app/points` | Dedicated |
| `pages/points-mall/list` | `app/points-mall` | Dedicated |
| `pages/points-mall/detail` | `app/points-product` | Dedicated |
| `pages/points-mall/confirm` | `app/points-confirm` | Dedicated |
| `pages/points-mall/orders` | `app/points-orders` | Dedicated |
| `pages/points-mall/order-detail` | `app/points-order` | Dedicated |
| `pages/login/index` | `app/login` | Dedicated |
| `pages/register/index` | `app/register` | Dedicated |
| `pages/product/list` | `app/products` | Dedicated |
| `pages/product/detail` | `app/product` | Dedicated, campaign variants use supplementary references |
| `pages/product/reviews` | `app/reviews` | Dedicated |
| `pages/order/confirm` | `app/confirm` | Dedicated |
| `pages/order/list` | `app/orders` | Dedicated |
| `pages/order/pay-result` | `app/pay-result` | Dedicated |
| `pages/order/detail` | `app/order` | Dedicated |
| `pages/order/logistics` | `app/logistics` | Dedicated |
| `pages/order/refund-apply` | `app/refund` | Dedicated |
| `pages/customer-service/index` | `app/service` | Dedicated |
| `pages/address/list` | `app/addresses` | Dedicated |
| `pages/address/edit` | `app/address-edit` | Dedicated |
| `pages/order/review` | `app/review-form` | Dedicated |
| `pages/order/review-detail` | `app/review-detail` | Dedicated |
| `pages/profile/edit` | `app/profile-edit` | Dedicated |
| `pages/profile/settings` | `app/settings` | Dedicated |
| `pages/profile/favorites` | `app/favorites` | Shared collection template |
| `pages/profile/browse-history` | `app/history` | Shared collection template |
| `pages/profile/feedback` | `app/feedback` | Dedicated |
| `pages/profile/about` | `app/about` | Dedicated |
| `pages/seckill/list` | `app/seckill` | Dedicated |
| `pages/seckill/product-list` | `app/seckill-products` | Dedicated |
| `pages/group-buy/list` | `app/groupbuy` | Dedicated |
| `pages/group-buy/product-list` | `app/groupbuy-products` | Dedicated |
| `pages/coupons/list` | `app/coupons` | Dedicated |
| `pages/coupons/center` | `app/coupon-center` | Dedicated |
| `pages/coupons/select` | `app/coupon-select` | Dedicated |

Supplementary App references: `app/seckill-detail`, `app/groupbuy-detail`, `app/group-select`, `app/refund-detail`, `app/notifications`, and `app/privacy`. These refine flows or future states without pretending to be additional registered pages.

## Admin: 40 mall business pages

| Actual page | Prototype target | Coverage |
| --- | --- | --- |
| `mall/banner/index.vue` | `merchant/banner` | Dedicated list template |
| `mall/category/index.vue` | `merchant/categories` | Dedicated list template |
| `mall/cschat/index.vue` | `merchant/service` | Dedicated workspace |
| `mall/feedback/index.vue` | `platform/feedback` tabs | Dedicated module template |
| `mall/group-buy/list/index.vue` | `platform/groupbuy?tab=活动管理` | Dedicated module tab |
| `mall/group-buy/audit/index.vue` | `platform/groupbuy?tab=申请审核` | Dedicated module tab |
| `mall/group-buy/product/index.vue` | `platform/groupbuy?tab=商品管理` | Dedicated module tab |
| `mall/group-buy/order/index.vue` | `platform/groupbuy?tab=订单管理` | Dedicated module tab |
| `mall/mch-coupon/list/index.vue` | `merchant/coupons?tab=优惠券` | Dedicated module tab |
| `mall/mch-coupon/receive/index.vue` | `merchant/coupons?tab=领取记录` | Dedicated module tab |
| `mall/mch-coupon/record/index.vue` | `merchant/coupons?tab=使用记录` | Dedicated module tab |
| `mall/mch-group-buy/activity/index.vue` | `merchant/groupbuy?tab=活动列表` | Dedicated module tab |
| `mall/mch-group-buy/apply/index.vue` | `merchant/groupbuy?tab=申请记录` | Dedicated module tab |
| `mall/mch-group-buy/product/index.vue` | `merchant/groupbuy?tab=拼团商品` | Dedicated module tab |
| `mall/mch-group-buy/order/index.vue` | `merchant/groupbuy?tab=拼团订单` | Dedicated module tab |
| `mall/mch-review/index.vue` | `merchant/reviews` tabs | Dedicated module template |
| `mall/mch-seckill/activity/index.vue` | `merchant/seckill?tab=活动列表` | Dedicated module tab |
| `mall/mch-seckill/apply/index.vue` | `merchant/seckill?tab=报名记录` | Dedicated module tab |
| `mall/mch-seckill/product/index.vue` | `merchant/seckill?tab=秒杀商品` | Dedicated module tab |
| `mall/member-points/index.vue` | `platform/points` tabs | Dedicated module template |
| `mall/member/index.vue` | `platform/members` | Dedicated page |
| `mall/merchant/index.vue` | `platform/merchants` | Shared management template |
| `mall/order/index.vue` | `merchant/orders` | Dedicated order template |
| `mall/platBanner/index.vue` | `platform/content?tab=Banner` | Dedicated module tab |
| `mall/points-mall/index.vue` | `platform/points-mall` tabs | Dedicated module template |
| `mall/quickEntry/index.vue` | `platform/content?tab=快捷入口` | Dedicated module tab |
| `mall/review/index.vue` | `platform/reviews` tabs | Dedicated module template |
| `mall/seckill/index.vue` | `platform/seckill` module entry | Shared module shell |
| `mall/seckill/list/index.vue` | `platform/seckill?tab=活动管理` | Dedicated module tab |
| `mall/seckill/session/index.vue` | `platform/seckill?tab=场次管理` | Dedicated module tab |
| `mall/seckill/audit/index.vue` | `platform/seckill?tab=报名审核` | Dedicated module tab |
| `mall/seckill/product/index.vue` | `platform/seckill?tab=商品管理` | Dedicated module tab |
| `mall/seckill/monitor/index.vue` | `platform/seckill?tab=实时监控` | Dedicated module tab |
| `mall/seckill/analysis/index.vue` | `platform/seckill?tab=成交分析` | Dedicated module tab |
| `mall/settings/index.vue` | `merchant/settings` | Dedicated editor |
| `mall/shipping/index.vue` | `merchant/shipping` tabs | Dedicated module template |
| `mall/shop/index.vue` | `platform/shops` | Shared management template |
| `mall/sku/index.vue` | `merchant/sku` | Dedicated list template |
| `mall/specDef/index.vue` | `merchant/specs` tabs | Dedicated module template |
| `mall/spu/index.vue` | `merchant/products` | Dedicated page |

The two `banner/test*.vue` files and `member-points/components/PointsAdjustDialog.vue` are excluded because they are test/internal component files, not business pages.

## Yeahboot system pages

The 11 user-facing system-management entries are present under the platform sidebar and marked `沿用`. Their production pages remain unchanged. `RouteContainerView.vue` and `RoutePlaceholderView.vue` are routing infrastructure and are not treated as user-facing screens.
