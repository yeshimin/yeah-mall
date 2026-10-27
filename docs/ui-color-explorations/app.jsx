const { useState } = React;

const palettes = [
  { id: "pine", label: "松绿基准", note: "当前方案", brand: "#1d5b48", brandDark: "#143d31", soft: "#e4eee7", paper: "#f4f6f3", line: "#dce4de", price: "#bc5038", ink: "#18362c" },
  { id: "truffle", label: "松露棕", note: "温暖品质", brand: "#5a4737", brandDark: "#3d3027", soft: "#eee3d5", paper: "#f7f2ec", line: "#e3d8ca", price: "#b54a35", ink: "#403229" },
  { id: "ceramic", label: "陶瓷蓝", note: "专业清爽", brand: "#1c607c", brandDark: "#123f52", soft: "#e0eef3", paper: "#f4f9fb", line: "#d6e4ea", price: "#d05d39", ink: "#173743" },
  { id: "teal", label: "墨青", note: "克制自然", brand: "#184a4b", brandDark: "#103334", soft: "#e2efed", paper: "#f3f7f6", line: "#d5e2df", price: "#c05640", ink: "#183938" },
  { id: "garnet", label: "石榴红", note: "行动优先", brand: "#b9463c", brandDark: "#702d2a", soft: "#f8e5e2", paper: "#fcf7f6", line: "#ecd7d3", price: "#b9463c", ink: "#442523" },
  { id: "navy", label: "海军蓝", note: "稳定可信", brand: "#1d4e89", brandDark: "#12345d", soft: "#e5eef9", paper: "#f5f8fc", line: "#d8e2ef", price: "#d66543", ink: "#1d304d" },
  { id: "ochre", label: "秋金", note: "丰收温度", brand: "#95661d", brandDark: "#624313", soft: "#f5ebd4", paper: "#fbf8f0", line: "#e9dfcb", price: "#bd5b2f", ink: "#4c381b" },
  { id: "plum", label: "紫檀", note: "会员感", brand: "#66475d", brandDark: "#422d3b", soft: "#f0e7ed", paper: "#faf6f9", line: "#e6dce4", price: "#c35445", ink: "#3a2c37" },
  { id: "moss", label: "苔藓", note: "低饱和自然", brand: "#5e7652", brandDark: "#3f5137", soft: "#edf1e8", paper: "#f7f9f5", line: "#dfe5d9", price: "#bc5739", ink: "#34452f" },
  { id: "charcoal", label: "炭黑橙", note: "高对比数码", brand: "#27282b", brandDark: "#191a1c", soft: "#eeeeed", paper: "#f7f7f6", line: "#dddddb", price: "#eb6435", ink: "#242527" },
];

function Icon({ name, size = 18 }) {
  const paths = {
    search: <><circle cx="11" cy="11" r="6"></circle><path d="m16 16 4 4"></path></>,
    home: <><path d="m3 10 9-7 9 7"></path><path d="M5.5 9v11h13V9"></path></>,
    grid: <><rect x="3" y="3" width="7" height="7" rx="1"></rect><rect x="14" y="3" width="7" height="7" rx="1"></rect><rect x="3" y="14" width="7" height="7" rx="1"></rect><rect x="14" y="14" width="7" height="7" rx="1"></rect></>,
    bag: <><path d="M5 8h14l-1 13H6L5 8Z"></path><path d="M9 9V6a3 3 0 0 1 6 0v3"></path></>,
    user: <><circle cx="12" cy="8" r="4"></circle><path d="M4.5 21c.7-4.2 3.2-6.3 7.5-6.3s6.8 2.1 7.5 6.3"></path></>,
    bolt: <path d="m13 2-8 12h6l-1 8 9-13h-6V2Z"></path>,
    users: <><circle cx="8" cy="9" r="3"></circle><circle cx="16" cy="9" r="3"></circle><path d="M2 20c.5-4 2.5-6 6-6 1.7 0 3 .5 4 1.4 1-.9 2.3-1.4 4-1.4 3.5 0 5.5 2 6 6"></path></>,
    ticket: <><path d="M3 7a2 2 0 0 0 2-2h14a2 2 0 0 0 2 2v10a2 2 0 0 0-2 2H5a2 2 0 0 0-2-2V7Z"></path><path d="M12 8v1M12 12v1M12 16v1"></path></>,
    package: <><path d="m4 7 8-4 8 4-8 4-8-4Z"></path><path d="M4 7v10l8 4 8-4V7M12 11v10"></path></>,
    plus: <path d="M12 5v14M5 12h14"></path>,
    filter: <path d="M4 5h16M7 12h10M10 19h4"></path>,
    more: <><circle cx="5" cy="12" r="1" fill="currentColor"></circle><circle cx="12" cy="12" r="1" fill="currentColor"></circle><circle cx="19" cy="12" r="1" fill="currentColor"></circle></>,
  };
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">{paths[name] || paths.grid}</svg>;
}

function ProductShape({ type = "headphone" }) {
  if (type === "chair") return <div className="product-shape chair"><i></i><b></b><em></em></div>;
  if (type === "serum") return <div className="product-shape serum"><i></i><b></b><em>00</em></div>;
  return <div className="product-shape headphone"><i></i><b></b><em></em></div>;
}

function AppPreview() {
  return <section className="phone" data-screen-label="固定版式 App 首页">
    <div className="phone-status"><b>10:26</b><span><i></i><i></i><i></i></span></div>
    <header className="phone-header"><span className="logo-mark">Y</span><div className="phone-search"><Icon name="search" size={16} />搜索商品、品牌和活动</div><Icon name="bag" size={17} /></header>
    <main className="phone-content">
      <section className="hero"><div><small>本周精选</small><h2>把好东西，<br />留在日常里</h2><p>精选耐用好物，适合每天使用。</p><button>探索精选</button></div><ProductShape /><b>01</b></section>
      <nav className="quick-links">{[["bolt", "秒杀"], ["users", "拼团"], ["ticket", "领券"], ["package", "积分"]].map(([icon, label]) => <span key={label}><Icon name={icon} size={17} /><b>{label}</b></span>)}</nav>
      <section className="goods-section"><header><h3>正在被选购</h3><span>查看全部</span></header><div className="chips"><b>精选</b><span>数码</span><span>居家</span><span>美护</span></div><div className="mobile-products">{[["headphone", "云感降噪头戴耳机", "399"], ["chair", "弧光人体工学休闲椅", "629"]].map(([shape, name, price]) => <article key={name}><div><ProductShape type={shape} /></div><b>{name}</b><small>日常使用，更舒服一些</small><strong>¥{price}</strong></article>)}</div></section>
    </main>
    <footer className="phone-nav">{[["home", "首页"], ["grid", "分类"], ["bag", "购物车"], ["user", "我的"]].map(([icon, label], index) => <span className={index === 0 ? "active" : ""} key={label}><Icon name={icon} size={18} /><b>{label}</b></span>)}</footer>
  </section>;
}

const products = [["headphone", "云感降噪头戴耳机", "4 个 SKU · ID m-1001", "¥399.00", "36", "2,364"], ["chair", "弧光人体工学休闲椅", "4 个 SKU · ID m-1002", "¥629.00", "12", "642"], ["serum", "白茶屏障修护精华", "2 个 SKU · ID m-1003", "¥168.00", "85", "4,268"]];

function AdminPreview() {
  const [keyword, setKeyword] = useState("");
  const [notice, setNotice] = useState("");
  const visible = products.filter((product) => product[1].includes(keyword));
  const act = (text) => { setNotice(text); window.setTimeout(() => setNotice(""), 1500); };
  return <section className="admin-preview" data-screen-label="固定版式 商家后台商品管理">
    <aside><div className="admin-logo"><span>Y</span><div><b>Yeah Mall</b><small>商家工作台</small></div></div><p>工作台</p><span>经营概览</span><p>商品管理</p><b className="side-active">商品管理</b><span>商品分类</span><span>规格管理</span><p>交易服务</p><span>订单管理</span><span>评价管理</span><footer><i>野</i><div><b>山野生活店</b><small>mch001</small></div></footer></aside>
    <div className="admin-main"><header><span>商家后台</span><b>›</b><strong>商品管理</strong><div><Icon name="search" size={17} /><Icon name="more" size={18} /><i>野</i></div></header><main><section className="filters"><label>商品名称<input value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder="输入商品名称" /></label><label>商品分类<select defaultValue="all"><option value="all">全部分类</option><option>数码影音</option><option>家具家居</option></select></label><label>销售状态<select defaultValue="all"><option value="all">全部状态</option><option>销售中</option><option>已下架</option></select></label><div className="query"><button onClick={() => setKeyword("")}>重置</button><button onClick={() => act(`找到 ${visible.length} 个商品`)}><Icon name="search" size={14} />查询</button></div><div className="business"><i></i><button onClick={() => act("请先勾选需要处理的商品")}>批量操作</button><button onClick={() => act("新增商品表单已打开")}><Icon name="plus" size={15} />新增商品</button></div></section><section className="table"><div className="table-columns"><span>商品</span><span>分类</span><span>售价</span><span>库存</span><span>销量</span><span>状态</span><span>操作</span></div>{visible.map((product) => <div className="product-row" key={product[1]}><div className="admin-product"><i><ProductShape type={product[0]} /></i><div><b>{product[1]}</b><small>{product[2]}</small></div></div><span>数码影音</span><b>{product[3]}</b><span>{product[4]}</span><span>{product[5]}</span><em>销售中</em><div><button onClick={() => act("商品编辑抽屉已打开")}>编辑</button><button onClick={() => act("商品已下架")}>下架</button><button onClick={() => act("更多商品操作已打开")}><Icon name="more" size={17} /></button></div></div>)}{!visible.length && <div className="empty">没有匹配的商品</div>}<footer><span>第 1 / 1 页</span><button>‹</button><button className="active">1</button><button>›</button><select defaultValue="10"><option value="10">10 条/页</option></select></footer></section></main></div>{notice && <div className="toast">{notice}</div>}
  </section>;
}

function PaletteExplorer() {
  const [selected, setSelected] = useState(palettes[0]);
  const vars = { "--brand": selected.brand, "--brand-dark": selected.brandDark, "--soft": selected.soft, "--paper": selected.paper, "--line": selected.line, "--price": selected.price, "--ink": selected.ink };
  return <div className="palette-explorer" style={vars}>
    <aside className="palette-sidebar"><div><span>YEAH MALL</span><h1>配色对照</h1><p>版式、字号、间距和组件形态固定，仅切换颜色。</p></div><nav>{palettes.map((palette, index) => <button className={palette.id === selected.id ? "active" : ""} onClick={() => setSelected(palette)} key={palette.id}><i style={{ background: palette.brand }}></i><span><b>{String(index + 1).padStart(2, "0")} · {palette.label}</b><small>{palette.note}</small></span></button>)}</nav><footer><span>当前方案</span><b>{selected.label}</b><div><i style={{ background: selected.brand }}></i><i style={{ background: selected.soft }}></i><i style={{ background: selected.price }}></i><i style={{ background: selected.ink }}></i></div></footer></aside>
    <main className="palette-stage"><header><div><span>固定版式 / Palette only</span><h2>{selected.label}</h2><p>{selected.note}。切换左侧色板以同步预览 App 与后台。</p></div><div className="color-code"><span>Brand</span><b>{selected.brand}</b><span>Price</span><b>{selected.price}</b></div></header><div className="preview-grid"><AppPreview /><AdminPreview /></div></main>
  </div>;
}

ReactDOM.createRoot(document.getElementById("root")).render(<PaletteExplorer />);
