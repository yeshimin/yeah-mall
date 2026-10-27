const { useState } = React;

function LineIcon({ name, size = 20 }) {
  const paths = {
    search: <><circle cx="11" cy="11" r="6"></circle><path d="m16 16 4 4"></path></>,
    bag: <><path d="M5 8h14l-1 13H6L5 8Z"></path><path d="M9 9V6a3 3 0 0 1 6 0v3"></path></>,
    home: <><path d="m3 10 9-7 9 7"></path><path d="M5.5 9v11h13V9"></path></>,
    grid: <><rect x="3" y="3" width="7" height="7" rx="1"></rect><rect x="14" y="3" width="7" height="7" rx="1"></rect><rect x="3" y="14" width="7" height="7" rx="1"></rect><rect x="14" y="14" width="7" height="7" rx="1"></rect></>,
    heart: <path d="M20.6 5.8c-2-2-5.2-2-7.2 0L12 7.2l-1.4-1.4a5.1 5.1 0 0 0-7.2 7.2L12 21l8.6-8a5.1 5.1 0 0 0 0-7.2Z"></path>,
    chevron: <path d="m9 18 6-6-6-6"></path>,
    plus: <path d="M12 5v14M5 12h14"></path>,
    filter: <path d="M4 5h16M7 12h10M10 19h4"></path>,
    more: <><circle cx="5" cy="12" r="1" fill="currentColor"></circle><circle cx="12" cy="12" r="1" fill="currentColor"></circle><circle cx="19" cy="12" r="1" fill="currentColor"></circle></>,
    users: <><circle cx="8" cy="9" r="3"></circle><circle cx="16" cy="9" r="3"></circle><path d="M2 20c.5-4 2.5-6 6-6 1.7 0 3 .5 4 1.4 1-.9 2.3-1.4 4-1.4 3.5 0 5.5 2 6 6"></path></>,
    bolt: <path d="m13 2-8 12h6l-1 8 9-13h-6V2Z"></path>,
    ticket: <><path d="M3 7a2 2 0 0 0 2-2h14a2 2 0 0 0 2 2v10a2 2 0 0 0-2 2H5a2 2 0 0 0-2-2V7Z"></path><path d="M12 8v1M12 12v1M12 16v1"></path></>,
    package: <><path d="m4 7 8-4 8 4-8 4-8-4Z"></path><path d="M4 7v10l8 4 8-4V7M12 11v10"></path></>,
    chart: <><path d="M4 20V10M10 20V4M16 20v-7M22 20V8"></path></>,
  };
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{paths[name] || paths.grid}</svg>;
}

function ProductShape({ type = "headphone" }) {
  if (type === "chair") {
    return <div className="product-shape chair-shape"><i></i><b></b><em></em></div>;
  }
  if (type === "serum") {
    return <div className="product-shape serum-shape"><i></i><b></b><em>00</em></div>;
  }
  return <div className="product-shape headphone-shape"><i></i><b></b><em></em></div>;
}

const directions = [
  {
    key: "grove",
    label: "01 · 山野货架",
    name: "Grove Shelf",
    note: "适合强调精选、品质与长期信任感的综合商城。以印刷标签和深松绿建立记忆点。",
    appTag: "本周精选",
    appTitle: <>把好东西，<br />留在日常里</>,
    appSub: "不追赶热闹，只挑真正耐用的那一件。",
    product: "云感降噪头戴耳机",
    price: "399",
    adminTitle: "商品管理",
    accent: "#1d5b48",
    phoneMode: "shelf",
    adminMode: "rail",
  },
  {
    key: "signal",
    label: "02 · 城市信号",
    name: "Signal Market",
    note: "适合活动、秒杀、拼团比重较高的商城。高对比色块让价格、库存和行动优先级更直接。",
    appTag: "LIMITED DROP",
    appTitle: <>今日上新，<br />现在就入手</>,
    appSub: "9:00 更新，热门色号正在快速售罄。",
    product: "澄澈随行蓝牙耳机",
    price: "269",
    adminTitle: "商品管理",
    accent: "#ed5a34",
    phoneMode: "promo",
    adminMode: "topbar",
  },
  {
    key: "studio",
    label: "03 · 静界数码",
    name: "Studio Index",
    note: "适合数码、家电和品牌旗舰店。以冷静的信息栅格与蓝黑基调强化参数、服务与专业感。",
    appTag: "CURATED TECH",
    appTitle: <>安静的科技，<br />清楚的选择</>,
    appSub: "参数、服务与体验，都有据可循。",
    product: "无线降噪监听耳机",
    price: "599",
    adminTitle: "商品管理",
    accent: "#136f8d",
    phoneMode: "studio",
    adminMode: "rail",
  },
  {
    key: "journal",
    label: "04 · 生活刊物",
    name: "Weekend Journal",
    note: "适合家居、美护与食品等生活方式品类。把商品组织成可阅读的生活提案，而不是纯货架。",
    appTag: "WEEKEND EDIT",
    appTitle: <>这一周，<br />给自己一点松弛</>,
    appSub: "一杯咖啡、一把椅子、一段留白的时间。",
    product: "山野拼配挂耳咖啡",
    price: "69",
    adminTitle: "商品管理",
    accent: "#9b613e",
    phoneMode: "journal",
    adminMode: "catalog",
  },
  {
    key: "ledger",
    label: "05 · 即刻交易",
    name: "Fast Ledger",
    note: "适合高频交易和营销活动较多的商家。信息优先、密度更高，减少装饰性分组。",
    appTag: "FLASH SELECT",
    appTitle: <>好价不用等，<br />现在就能带走</>,
    appSub: "库存、活动、到手价都在第一眼说清楚。",
    product: "轻量通勤降噪耳机",
    price: "199",
    adminTitle: "商品管理",
    accent: "#0d6d5b",
    phoneMode: "ledger",
    adminMode: "ledger",
  },
  {
    key: "club",
    label: "06 · 会员工坊",
    name: "Member Atelier",
    note: "适合强调会员权益、积分和复购的商城。视觉上更像一个可积累的品牌俱乐部。",
    appTag: "MEMBER EDITION",
    appTitle: <>留给会员的，<br />不只是一张券</>,
    appSub: "专属积分、优先购与更好的售后体验。",
    product: "云感降噪头戴耳机",
    price: "429",
    adminTitle: "商品管理",
    accent: "#6c4b67",
    phoneMode: "club",
    adminMode: "club",
  },
];

function StyleMark({ direction }) {
  return <span className="style-mark" aria-hidden="true">Y</span>;
}

function PhoneHome({ direction }) {
  const [liked, setLiked] = useState(false);
  const isJournal = direction.phoneMode === "journal";
  const isLedger = direction.phoneMode === "ledger";
  const isClub = direction.phoneMode === "club";
  return <div className={`phone-shell theme-${direction.key} layout-${direction.phoneMode}`} data-screen-label={`App 首页 · ${direction.name}`}>
    <div className="phone-status"><span>10:26</span><div><i></i><i></i><i></i></div></div>
    <header className="phone-head">
      <StyleMark direction={direction} />
      <button className="phone-search"><LineIcon name="search" size={17} /><span>搜索商品、品牌和活动</span></button>
      <button className="phone-bag"><LineIcon name="bag" size={18} /></button>
    </header>
    <main className="phone-body">
      <section className="phone-hero">
        <div className="hero-copy">
          <span>{direction.appTag}</span>
          <h1>{direction.appTitle}</h1>
          <p>{direction.appSub}</p>
          <button>探索精选 <LineIcon name="chevron" size={14} /></button>
        </div>
        <div className="hero-product"><ProductShape /></div>
        <b className="hero-number">01</b>
      </section>
      {isJournal && <div className="journal-links"><button><span>生活提案</span><b>本周的温柔角落</b></button><button><span>新到好物</span><b>少一点，也刚刚好</b></button></div>}
      {isLedger && <div className="deal-strip"><article><span>今日秒杀</span><b>12:00 场</b><small>剩余 18 件</small></article><article><span>新人专享</span><b>满 199 减 20</b><small>立即领取</small></article></div>}
      {isClub && <div className="club-strip"><span>Y+ MEMBER</span><div><b>1,280</b><small>可用积分</small></div><button>去兑换</button></div>}
      {!isJournal && !isLedger && !isClub && <div className="quick-actions">
        {[["bolt", "秒杀"], ["users", "拼团"], ["ticket", "领券"], ["package", "积分"]].map(([icon, text]) => <button key={text}><LineIcon name={icon} size={19} /><span>{text}</span></button>)}
      </div>}
      <section className="mobile-section">
        <div className="mobile-section-head"><div><span></span><h2>{isJournal ? "这周的生活提案" : isClub ? "会员优先购" : "正在被选购"}</h2></div><button>查看全部</button></div>
        <div className="mobile-tabs"><button className="active">精选</button><button>数码</button><button>居家</button><button>美护</button></div>
        <div className="mobile-cards">
          {[['headphone', direction.product, direction.price, '2,364'], ['chair', '弧光人体工学休闲椅', '629', '642']].map(([shape, title, price, sales]) => <article key={title}><div className="mini-product"><ProductShape type={shape} /></div><h3>{title}</h3><p>日常使用，更舒服一些</p><div><b>¥{price}</b><small>已售 {sales}</small></div></article>)}
        </div>
      </section>
    </main>
    <nav className="phone-nav">
      {[["home", "首页"], ["grid", "分类"], ["bag", "购物车"], ["heart", "我的"]].map(([icon, text], index) => <button className={index === 0 ? "active" : ""} key={text} onClick={() => text === "我的" && setLiked(!liked)}><LineIcon name={icon} size={19} /><span>{liked && text === "我的" ? "已关注" : text}</span></button>)}
    </nav>
  </div>;
}

function AdminScreen({ direction }) {
  const [search, setSearch] = useState("");
  const products = [
    ["headphone", direction.product, "4 个 SKU", `¥${direction.price}.00`, "36", "销售中"],
    ["chair", "弧光人体工学休闲椅", "4 个 SKU", "¥629.00", "12", "销售中"],
    ["serum", "白茶屏障修护精华", "2 个 SKU", "¥168.00", "85", "销售中"],
  ].filter((row) => row[1].includes(search));
  const catalog = direction.adminMode === "catalog";
  const ledger = direction.adminMode === "ledger";
  const club = direction.adminMode === "club";
  return <div className={`admin-screen theme-${direction.key} layout-${direction.adminMode}`} data-screen-label={`商家后台 · ${direction.name}`}>
    <aside className="explore-sidebar">
      <div className="explore-logo"><StyleMark direction={direction} /><div><b>Yeah Mall</b><small>商家工作台</small></div></div>
      <div className="explore-menu-title">工作台</div>
      <button><LineIcon name="chart" size={18} />经营概览</button>
      <div className="explore-menu-title">商品管理</div>
      <button className="active"><LineIcon name="package" size={18} />商品管理</button>
      <button><LineIcon name="grid" size={18} />商品分类</button>
      <button><LineIcon name="filter" size={18} />规格管理</button>
      <div className="explore-menu-title">交易服务</div>
      <button><LineIcon name="bag" size={18} />订单管理</button>
      <button><LineIcon name="users" size={18} />评价管理</button>
      <div className="sidebar-user"><span>野</span><div><b>山野生活店</b><small>mch001</small></div><LineIcon name="more" size={17} /></div>
    </aside>
    <section className="admin-content">
      <header className="admin-crumb"><span>商家后台</span><LineIcon name="chevron" size={14} /><b>{direction.adminTitle}</b><div><button><LineIcon name="search" size={18} /></button><button><LineIcon name="more" size={18} /></button><span className="admin-avatar">野</span></div></header>
      <main className="admin-workarea">
        {catalog && <section className="admin-feature-strip catalog-strip"><span>CURATED CATALOG</span><b>按空间与场景整理商品，而不是只按类目罗列。</b><button>查看橱窗分类</button></section>}
        {ledger && <section className="admin-feature-strip ledger-strip"><article><span>待发货</span><b>12</b></article><article><span>库存预警</span><b>4</b></article><article><span>今日成交</span><b>¥12,680</b></article></section>}
        {club && <section className="admin-feature-strip club-strip-admin"><div><span>会员复购</span><b>42.6%</b><small>较上周 +3.8%</small></div><div><span>积分兑换</span><b>78</b><small>本月订单</small></div><button>查看会员经营</button></section>}
        <section className="search-panel">
          <label><span>商品名称</span><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="输入商品名称" /></label>
          <label><span>商品分类</span><select defaultValue="all"><option value="all">全部分类</option><option>数码影音</option><option>家具家居</option></select></label>
          <label><span>销售状态</span><select defaultValue="all"><option value="all">全部状态</option><option>销售中</option><option>已下架</option></select></label>
          <div className="query-actions"><button>重置</button><button className="query"><LineIcon name="search" size={15} />查询</button></div>
          <div className="business-actions"><i></i><button><LineIcon name="filter" size={15} />批量操作</button><button className="create"><LineIcon name="plus" size={16} />新增商品</button></div>
        </section>
        <section className="product-table">
          <div className="table-head"><span>商品</span><span>分类</span><span>售价</span><span>库存</span><span>销量</span><span>状态</span><span>操作</span></div>
          {products.map((item) => <div className="table-row" key={item[1]}><div className="table-product"><span className="tiny-product"><ProductShape type={item[0]} /></span><div><b>{item[1]}</b><small>{item[2]} · ID m-1001</small></div></div><span>数码影音</span><b>{item[3]}</b><span>{item[4]}</span><span>2,364</span><em>{item[5]}</em><div className="row-actions"><button>编辑</button><button>下架</button><button><LineIcon name="more" size={18} /></button></div></div>)}
          {!products.length && <div className="explore-empty">没有匹配的商品</div>}
          <footer className="table-pagination"><span>第 1 / 1 页</span><button>‹</button><button className="active">1</button><button>›</button><select defaultValue="10"><option value="10">10 条/页</option></select></footer>
        </section>
      </main>
    </section>
  </div>;
}

function DirectionNote({ direction }) {
  return <div className={`direction-note note-${direction.key}`}>
    <span className="direction-index">{direction.label}</span>
    <h2>{direction.name}</h2>
    <p>{direction.note}</p>
    <div><b>App</b><span>首页</span><b>后台</b><span>商品管理</span></div>
  </div>;
}

function ExplorationApp() {
  return <div className="exploration-root">
    <DesignCanvas minScale={0.38} maxScale={1.15} initialView={{ x: 86, y: 8, scale: 0.38 }}>
      {directions.map((direction) => <DCSection key={direction.key} id={direction.key} title={direction.label} subtitle={direction.name} gap={54}>
        <DCArtboard id={`${direction.key}-note`} label="方向说明" width={300} height={300}><DirectionNote direction={direction} /></DCArtboard>
        <DCArtboard id={`${direction.key}-app`} label="App 首页" width={430} height={888} chromeless><PhoneHome direction={direction} /></DCArtboard>
        <DCArtboard id={`${direction.key}-admin`} label="商家后台 · 商品管理" width={1280} height={760} chromeless><AdminScreen direction={direction} /></DCArtboard>
      </DCSection>)}
    </DesignCanvas>
  </div>;
}

ReactDOM.createRoot(document.getElementById("root")).render(<ExplorationApp />);
