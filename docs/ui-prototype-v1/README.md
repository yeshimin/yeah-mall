# Yeah Mall interactive UI prototype

Run a local static server in this directory, then open the shown URL in a browser:

```bash
python3 -m http.server 4179
```

The prototype uses pinned React and Babel browser builds, so it needs network access when first loaded.

Use the surface switcher to move between App, platform admin and merchant admin. The App page-map selector exposes every implemented screen; admin pages are available from their sidebars.

Coverage:

- App: all 41 registered pages are mapped, plus 6 supplementary flow/state references.
- Platform admin: all current platform mall pages are mapped, with the yeahboot system-management menu retained and marked as framework-owned.
- Merchant admin: all current merchant mall pages are mapped, including SPU, SKU, specifications, marketing, service, logistics and settings.

See `COVERAGE.md` for the exact actual-page-to-prototype mapping.

Verified end-to-end prototype flows:

- Seckill: home -> activity -> activity products -> campaign product -> order confirmation -> payment result.
- Group-buy: home -> activity -> activity products -> campaign product -> group selection -> order confirmation.
- After-sales: order detail -> refund form -> refund progress.
- Review: order/review list -> review detail -> conditional multi-round reply.
- Points: points center -> points product -> exchange confirmation -> points order.

Final visual QA:

- 47 App prototype states checked with no horizontal overflow.
- 47 App states checked with no diagonal/asymmetric card radii.
- 47 App states checked with no partially clipped visible text or out-of-bounds interactive control.
- 32 platform-admin and 22 merchant-admin route states checked with no horizontal overflow.
- No browser console errors and no visible prototype button without an interaction handler.
- Ordinary admin pages use breadcrumb-only page identity; no repeated content-area page title.
- All four TabBar root pages follow root-navigation rules; category, cart and profile do not expose a back button.
- Quantity steppers share one fixed geometry across cart, checkout, points and campaign purchase flows.

All data is simulated and stored only in browser memory.
