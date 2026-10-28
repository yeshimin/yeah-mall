const { useState, useEffect, useMemo } = React;

function Icon({ name, size = 20, strokeWidth = 1.8, className = '' }) {
  const paths = {
    search: <><circle cx="11" cy="11" r="6.5"/><path d="m16 16 4 4"/></>,
    home: <><path d="m3 10 9-7 9 7"/><path d="M5.5 9v11h13V9M9 20v-6h6v6"/></>,
    grid: <><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></>,
    cart: <><path d="M3 4h2l2.2 10.2h9.9L20 7H6"/><circle cx="9" cy="19" r="1.3"/><circle cx="17" cy="19" r="1.3"/></>,
    user: <><circle cx="12" cy="8" r="4"/><path d="M4.5 21c.7-4.2 3.2-6.3 7.5-6.3s6.8 2.1 7.5 6.3"/></>,
    heart: <path d="M20.6 5.8c-2-2-5.2-2-7.2 0L12 7.2l-1.4-1.4a5.1 5.1 0 0 0-7.2 7.2L12 21l8.6-8a5.1 5.1 0 0 0 0-7.2Z"/>,
    back: <path d="m15 18-6-6 6-6"/>,
    chevron: <path d="m9 18 6-6-6-6"/>,
    down: <path d="m6 9 6 6 6-6"/>,
    plus: <><path d="M12 5v14M5 12h14"/></>,
    minus: <path d="M5 12h14"/>,
    check: <path d="m5 12 4 4L19 6"/>,
    bag: <><path d="M5 8h14l-1 13H6L5 8Z"/><path d="M9 9V6a3 3 0 0 1 6 0v3"/></>,
    truck: <><path d="M3 5h11v11H3zM14 9h4l3 4v3h-7z"/><circle cx="7" cy="18" r="2"/><circle cx="18" cy="18" r="2"/></>,
    order: <><path d="M6 3h12v18H6z"/><path d="M9 8h6M9 12h6M9 16h4"/></>,
    members: <><circle cx="9" cy="8" r="3"/><circle cx="17" cy="9" r="2.4"/><path d="M3 20c.5-4 2.5-6 6-6s5.5 2 6 6M14 15c3.7-.5 5.9 1.2 6.6 5"/></>,
    box: <><path d="m4 7 8-4 8 4-8 4-8-4Z"/><path d="M4 7v10l8 4 8-4V7M12 11v10"/></>,
    coupon: <path d="M3 7a2 2 0 0 0 2-2h14a2 2 0 0 0 2 2v10a2 2 0 0 0-2 2H5a2 2 0 0 0-2-2V7Zm9-1v12"/>,
    points: <><path d="m12 3 8 6-3 10H7L4 9l8-6Z"/><path d="m4 9 8 4 8-4M12 13v6"/></>,
    message: <><path d="M4 5h16v12H9l-5 4V5Z"/><path d="M8 10h8M8 13h5"/></>,
    settings: <><circle cx="12" cy="12" r="3"/><path d="M19 13.5v-3l-2.2-.6a7 7 0 0 0-.6-1.4l1.1-2L15.2 4l-2 1.1a7 7 0 0 0-1.5-.6L11 2H8l-.6 2.3a7 7 0 0 0-1.4.6L4 3.8 1.8 6l1.1 2a7 7 0 0 0-.6 1.5L0 10v3l2.3.6a7 7 0 0 0 .6 1.4l-1.1 2L4 19.2l2-1.1a7 7 0 0 0 1.5.6L8 21h3l.6-2.3a7 7 0 0 0 1.4-.6l2 1.1 2.2-2.2-1.1-2a7 7 0 0 0 .6-1.5l2.3-.5Z" transform="translate(2) scale(.92)"/></>,
    bolt: <path d="m13 2-8 12h6l-1 8 9-13h-6V2Z"/>,
    spark: <><path d="m12 2 1.5 5.5L19 9l-5.5 1.5L12 16l-1.5-5.5L5 9l5.5-1.5L12 2Z"/><path d="m19 15 .8 2.2L22 18l-2.2.8L19 21l-.8-2.2L16 18l2.2-.8L19 15Z"/></>,
    diamond: <path d="m12 3 8 6-8 12L4 9l8-6Zm-8 6h16M8 4.8 12 9l4-4.2M12 9v12"/>,
    ticket: <><path d="M3 7a2 2 0 0 0 2-2h14a2 2 0 0 0 2 2v10a2 2 0 0 0-2 2H5a2 2 0 0 0-2-2V7Z"/><path d="M12 8v1M12 12v1M12 16v1"/></>,
    users: <><circle cx="8" cy="9" r="3"/><circle cx="16" cy="9" r="3"/><path d="M2 20c.5-4 2.5-6 6-6 1.7 0 3 .5 4 1.4 1-.9 2.3-1.4 4-1.4 3.5 0 5.5 2 6 6"/></>,
    eye: <><path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z"/><circle cx="12" cy="12" r="2.5"/></>,
    more: <><circle cx="5" cy="12" r="1" fill="currentColor"/><circle cx="12" cy="12" r="1" fill="currentColor"/><circle cx="19" cy="12" r="1" fill="currentColor"/></>,
    filter: <path d="M4 5h16M7 12h10M10 19h4"/>,
    store: <><path d="M4 9h16l-1-5H5L4 9Z"/><path d="M5 9v11h14V9M9 20v-6h6v6"/></>,
    clock: <><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></>
  };
  return (
    <svg className={`icon ${className}`} width={size} height={size} viewBox="0 0 24 24"
         fill="none" stroke="currentColor" strokeWidth={strokeWidth} strokeLinecap="round" strokeLinejoin="round"
         aria-hidden="true">
      {paths[name] || paths.box}
    </svg>
  );
}

function ProductArt({ type, className = '', bare = false }) {
  const defs = (
    <defs>
      <linearGradient id={`${type}-bg`} x1="0" y1="0" x2="1" y2="1">
        <stop offset="0" stopColor="#edf2ee"/>
        <stop offset="1" stopColor="#dfe8e2"/>
      </linearGradient>
      <linearGradient id={`${type}-dark`} x1="0" y1="0" x2="1" y2="1">
        <stop offset="0" stopColor="#29483b"/>
        <stop offset="1" stopColor="#16271f"/>
      </linearGradient>
      <filter id={`${type}-shadow`} x="-30%" y="-30%" width="160%" height="180%">
        <feDropShadow dx="0" dy="12" stdDeviation="12" floodColor="#183126" floodOpacity=".18"/>
      </filter>
    </defs>
  );
  let subject;
  if (type === 'headphone') {
    subject = <g filter={`url(#${type}-shadow)`}>
      <path d="M126 162c0-70 35-105 74-105s74 35 74 105" fill="none" stroke={`url(#${type}-dark)`} strokeWidth="24"/>
      <rect x="104" y="147" width="52" height="96" rx="24" fill="#1f3c31"/>
      <rect x="244" y="147" width="52" height="96" rx="24" fill="#1f3c31"/>
      <rect x="116" y="161" width="23" height="66" rx="11" fill="#8fa99d"/>
      <rect x="261" y="161" width="23" height="66" rx="11" fill="#8fa99d"/>
    </g>;
  } else if (type === 'chair') {
    subject = <g filter={`url(#${type}-shadow)`}>
      <path d="M128 91c0-19 15-34 34-34h77c19 0 34 15 34 34v96H128V91Z" fill="#d7c0a0"/>
      <rect x="111" y="171" width="178" height="62" rx="22" fill="#b49a79"/>
      <path d="M137 225 121 282M263 225l16 57" stroke="#5b4835" strokeWidth="12" strokeLinecap="round"/>
      <path d="M112 184 77 207M288 184l35 23" stroke="#5b4835" strokeWidth="11" strokeLinecap="round"/>
      <path d="M145 98h111" stroke="#eadcc9" strokeWidth="3" opacity=".8"/>
    </g>;
  } else if (type === 'serum') {
    subject = <g filter={`url(#${type}-shadow)`}>
      <rect x="165" y="45" width="70" height="48" rx="8" fill="#263c33"/>
      <path d="M151 92h98l12 163c1 17-11 29-27 29h-68c-16 0-28-12-27-29l12-163Z" fill="#d9c9a9"/>
      <rect x="151" y="151" width="98" height="73" fill="#f4efe3" opacity=".9"/>
      <path d="M174 174h52M184 191h32" stroke="#395448" strokeWidth="5" strokeLinecap="round"/>
      <circle cx="200" cy="117" r="8" fill="#b98a57"/>
    </g>;
  } else {
    subject = <g filter={`url(#${type}-shadow)`}>
      <path d="M118 75h164l-18 190H136L118 75Z" fill="#ceb08a"/>
      <path d="M118 75h164l-9 51H127l-9-51Z" fill="#25483a"/>
      <circle cx="200" cy="182" r="43" fill="#f0e1c9"/>
      <path d="M179 185c17-28 39-33 48-15-15 27-36 36-48 15Z" fill="#7b4d31"/>
      <path d="M196 164c6 12 5 25-4 37" fill="none" stroke="#f0e1c9" strokeWidth="3"/>
      <path d="M151 145h98" stroke="#76573d" strokeWidth="2" opacity=".45"/>
    </g>;
  }
  return (
    <svg className={`product-art ${className}`} viewBox="0 0 400 320" role="img" aria-label="商品示意图">
      {defs}
      {!bare && <><rect width="400" height="320" rx="28" fill={`url(#${type}-bg)`}/><circle cx="326" cy="62" r="54" fill="#fff" opacity=".4"/><path d="M26 264c76-46 153-54 231-23 49 20 88 18 117 0v79H26v-56Z" fill="#fff" opacity=".35"/></>}
      {subject}
    </svg>
  );
}

function StatusTicket({ children, tone = 'neutral', small = false }) {
  return <span className={`status-ticket tone-${tone}${small ? ' is-small' : ''}`}>{children}</span>;
}

function Avatar({ name, tone = 'sage', size = 38 }) {
  return <span className={`avatar avatar-${tone}`} style={{ width: size, height: size }}>{String(name || '会').slice(0, 1)}</span>;
}

function Toast({ message }) {
  if (!message) return null;
  return <div className="prototype-toast"><Icon name="check" size={17}/><span>{message}</span></div>;
}

Object.assign(window, { Icon, ProductArt, StatusTicket, Avatar, Toast });
