const MOCKS = window.YEAH_MALL_MOCKS;

const APP_PAGES = [
  ['app/home', '首页'], ['app/category', '分类'], ['app/products', '商品列表'], ['app/product', '商品详情'],
  ['app/cart', '购物车'], ['app/confirm', '确认订单'], ['app/orders', '订单列表'], ['app/order', '订单详情'],
  ['app/profile', '个人中心'], ['app/favorites', '我的收藏'], ['app/history', '浏览足迹'], ['app/coupons', '我的优惠券'],
  ['app/coupon-center', '领券中心'], ['app/points', '我的积分'], ['app/points-mall', '积分商城'], ['app/seckill', '秒杀活动'], ['app/groupbuy', '拼团活动'], ['app/service', '客服'],
  ['app/addresses', '地址管理'], ['app/reviews', '商品评价'], ['app/feedback', '意见反馈'], ['app/settings', '设置'],
  ['app/shops', '店铺列表'], ['app/shop', '店铺详情'], ['app/pay-result', '支付结果'], ['app/logistics', '物流详情'],
  ['app/refund', '申请退款'], ['app/review-form', '发表评价'], ['app/review-detail', '评价详情'], ['app/profile-edit', '编辑资料'],
  ['app/about', '关于我们'], ['app/seckill-products', '秒杀商品'], ['app/groupbuy-products', '拼团商品'], ['app/coupon-select', '选择优惠券'],
  ['app/points-product', '积分商品'], ['app/points-confirm', '确认兑换'], ['app/points-orders', '积分订单'], ['app/points-order', '积分订单详情'],
  ['app/seckill-detail', '秒杀商品详情'], ['app/groupbuy-detail', '拼团商品详情'], ['app/group-select', '选择拼团'], ['app/address-edit', '编辑地址'],
  ['app/refund-detail', '退款详情'], ['app/notifications', '消息通知'], ['app/privacy', '隐私设置'], ['app/login', '登录'], ['app/register', '注册']
];

const routePath = (route) => route.split('?')[0];
const routeParam = (route, key) => new URLSearchParams(route.split('?')[1] || '').get(key);

function usePrototypeRoute() {
  const read = () => {
    const raw = (location.hash || '#app/home').slice(1);
    try { return decodeURIComponent(raw); } catch (error) { return raw; }
  };
  const [route, setRouteState] = useState(read);
  useEffect(() => {
    const onHash = () => setRouteState(read());
    window.addEventListener('hashchange', onHash);
    return () => window.removeEventListener('hashchange', onHash);
  }, []);
  const navigate = (next) => {
    location.hash = next;
    setRouteState(next);
  };
  return [route, navigate];
}

function PrototypeToolbar({ surface, setSurface, route, navigate }) {
  const currentPage = routePath(route);
  return (
    <div className="prototype-toolbar">
      <div className="prototype-brand"><span className="brand-stamp">Y</span><b>Yeah Mall UI v1</b></div>
      <div className="surface-switch" role="tablist">
        <button className={surface === 'app' ? 'active' : ''} onClick={() => { setSurface('app'); navigate('app/home'); }}>App</button>
        <button className={surface === 'platform' ? 'active' : ''} onClick={() => { setSurface('platform'); navigate('platform/overview'); }}>平台后台</button>
        <button className={surface === 'merchant' ? 'active' : ''} onClick={() => { setSurface('merchant'); navigate('merchant/overview'); }}>商家后台</button>
      </div>
      {surface === 'app' && (
        <div className="scenario-switch">
          {APP_PAGES.slice(0, 5).map(([key, label]) => <button key={key} className={currentPage === key ? 'active' : ''} onClick={() => navigate(key)}>{label}</button>)}
          <select aria-label="App 页面地图" value={currentPage} onChange={(e) => navigate(e.target.value)}>
            {APP_PAGES.map(([key, label]) => <option value={key} key={key}>{label}</option>)}
          </select>
        </div>
      )}
      <span className="prototype-note">交互原型 · 模拟数据</span>
    </div>
  );
}

function MobileFrame({ children }) {
  return (
    <div className="mobile-stage">
      <div className="mobile-frame">
        <div className="mobile-speaker"></div>
        {children}
      </div>
      <div className="mobile-caption">
        <span>390px 设计宽度</span>
        <p>预览高度随窗口适配，正式页面按设备高度滚动。</p>
      </div>
    </div>
  );
}

function MobileStatusBar({ tone = 'surface' }) {
  return <div className={`mobile-status mobile-status-${tone}`}><b>10:26</b><span><i></i><i></i><i></i></span></div>;
}

function WechatCapsule() {
  return <div className="wechat-capsule" aria-label="微信小程序胶囊按钮"><span className="capsule-more" aria-hidden="true"><i></i><i></i><i></i></span><span className="capsule-target" aria-hidden="true"><i></i></span></div>;
}

function MobileBottomNav({ active, navigate }) {
  const items = [
    ['home', '首页', 'app/home'], ['grid', '分类', 'app/category'], ['cart', '购物车', 'app/cart'], ['user', '我的', 'app/profile']
  ];
  return (
    <nav className="mobile-bottom-nav">
      {items.map(([icon, label, target]) => (
        <button key={label} className={active === label ? 'active' : ''} onClick={() => navigate(target)}>
          <Icon name={icon} size={21}/><span>{label}</span>
        </button>
      ))}
    </nav>
  );
}

function AppHome({ navigate }) {
  const [category, setCategory] = useState('all');
  const homeBanners = [
    { id:'audio', kicker:'本周精选', title:['安静，','留给喜欢','的声音'], action:'查看新品', art:'headphone', tone:'forest' },
    { id:'living', kicker:'居家焕新', title:['把松弛感，','搬进日常','每个角落'], action:'逛逛家居', art:'chair', tone:'clay' },
    { id:'beauty', kicker:'秋日修护', title:['温和补水，','让肌肤回到','稳定状态'], action:'查看美护', art:'serum', tone:'ocean' }
  ];
  const [bannerIndex, setBannerIndex] = useState(0);
  const activeBanner = homeBanners[bannerIndex];
  useEffect(() => {
    const timer = window.setInterval(() => setBannerIndex((index) => (index + 1) % homeBanners.length), 3000);
    return () => window.clearInterval(timer);
  }, []);
  const products = MOCKS.products.filter((item) =>
    category === 'all' || item.category === category
  ).slice(0, 10);
  const waterfallColumns = products.reduce((columns, item) => {
    const estimatedHeight = (item.imageHeight || 176) + (item.promotionText ? 30 : 0) + (item.subtitle ? 34 : 0) + 92;
    const target = columns[0].height <= columns[1].height ? columns[0] : columns[1];
    target.items.push(item);
    target.height += estimatedHeight;
    return columns;
  }, [{ items:[], height:0 }, { items:[], height:0 }]);
  const quickRoutes = { seckill: 'app/seckill', group: 'app/groupbuy', coupon: 'app/coupon-center', points: 'app/points-mall', new: 'app/products' };
  return (
    <div className="mobile-screen app-home" data-screen-label="App 首页">
      <MobileStatusBar tone="paper"/>
      <header className="app-topbar tab-paper-topbar">
        <div className="wordmark"><span>YEAH</span><b>MALL</b></div>
        <button className="mobile-search" type="button" onClick={() => navigate('app/products')}><span className="mobile-search-content"><Icon name="search" size={18}/><span>搜索商品</span></span></button>
        <WechatCapsule/>
      </header>
      <main className="mobile-scroll with-bottom-nav">
        <section className={`commerce-banner banner-tone-${activeBanner.tone}`} onClick={() => navigate('app/product')}>
          <div className="banner-slide-content" key={activeBanner.id}>
          <div className="banner-copy">
            <span className="banner-kicker">{activeBanner.kicker}</span>
            <h1>{activeBanner.title.map((line) => <React.Fragment key={line}>{line}<br/></React.Fragment>)}</h1>
            <button onClick={(event)=>{event.stopPropagation();navigate('app/product');}}>{activeBanner.action} <Icon name="chevron" size={15}/></button>
          </div>
          <ProductArt type={activeBanner.art}/>
          </div>
          <div className="banner-dots" onClick={(event) => event.stopPropagation()}>
            {homeBanners.map((banner, index) => <button key={banner.id} className={bannerIndex === index ? 'active' : ''} aria-label={`切换到第 ${index + 1} 张 Banner`} onClick={() => setBannerIndex(index)}></button>)}
          </div>
        </section>
        <section className="quick-grid" aria-label="快捷入口">
          {MOCKS.quickLinks.map((item) => (
            <button key={item.id} onClick={() => navigate(quickRoutes[item.id])}>
              <span className={`quick-icon quick-${item.id}`}><Icon name={item.icon} size={21}/></span>
              <span>{item.name}</span>
            </button>
          ))}
        </section>
        <section className="product-section">
          <div className="section-heading">
            <div><span className="shelf-mark"></span><h2>精选好物</h2></div>
          </div>
          <div className="category-strip">
            {MOCKS.categories.map((item) => <button key={item.id} className={category === item.id ? 'active' : ''} onClick={() => setCategory(item.id)}>{item.name}</button>)}
          </div>
          <div className="home-waterfall">
            {waterfallColumns.map((column, columnIndex) => <div className="home-waterfall-column" key={columnIndex}>
              {column.items.map((item) => (
                <button className="home-waterfall-item" key={item.id} onClick={() => navigate('app/product')}>
                  <span className="home-waterfall-art" style={{ height: `${item.imageHeight || 176}px` }}><ProductArt type={item.art}/></span>
                  <span className="home-waterfall-copy">
                    <span className="home-waterfall-name">{item.name}</span>
                    {item.promotionText && <span className="home-waterfall-promotion">{item.promotionText}</span>}
                    <span className="home-waterfall-subtitle">{item.subtitle}</span>
                    <span className="home-waterfall-meta"><b>¥{item.price}</b><small>已售 {item.sales}</small></span>
                  </span>
                </button>
              ))}
            </div>)}
          </div>
          {!products.length && <div className="mobile-empty"><Icon name="search" size={26}/><b>没有找到相关商品</b><span>换个关键词或分类试试</span></div>}
        </section>
      </main>
      <MobileBottomNav active="首页" navigate={navigate}/>
    </div>
  );
}

function QuantityControl({ value, setValue, max = 9 }) {
  return (
    <div className="quantity-control">
      <button onClick={() => setValue(Math.max(1, value - 1))}><Icon name="minus" size={15}/></button>
      <b>{value}</b>
      <button onClick={() => setValue(Math.min(max, value + 1))}><Icon name="plus" size={15}/></button>
    </div>
  );
}

function AppProduct({ navigate, notify }) {
  const product = MOCKS.products[0];
  const [favorite, setFavorite] = useState(false);
  const [sheet, setSheet] = useState(false);
  const [color, setColor] = useState(product.colors[0]);
  const [spec, setSpec] = useState(product.specs[0]);
  const [quantity, setQuantity] = useState(1);
  const [gallery, setGallery] = useState(0);
  return (
    <div className="mobile-screen product-detail" data-screen-label="App 商品详情">
      <MobileStatusBar tone="transparent"/>
      <div className="detail-gallery">
        <div className="detail-floatbar">
          <button onClick={() => navigate('app/home')}><Icon name="back"/></button>
          <WechatCapsule/>
        </div>
        <div className="detail-side-actions"><button onClick={() => notify('分享链接已准备') }><Icon name="spark"/></button><button onClick={() => setFavorite(!favorite)} className={favorite ? 'is-favorite' : ''}><Icon name="heart"/></button></div>
        <ProductArt type={product.art} className={`gallery-variant variant-${gallery}`}/>
        <div className="gallery-dots">{[0,1,2].map((dot) => <button key={dot} className={gallery === dot ? 'active' : ''} onClick={() => setGallery(dot)}></button>)}</div>
      </div>
      <main className="mobile-scroll detail-scroll-content">
        <section className="detail-main-info">
          <div className="price-line"><b>¥{product.price}</b><del>¥{product.originPrice}</del><StatusTicket tone="price">限时直降</StatusTicket></div>
          <h1>{product.name}</h1>
          <p>{product.subtitle}</p>
          <div className="detail-metrics"><span>月售 {product.sales}</span><span>好评 {product.rating}%</span><span>库存 {product.stock}</span></div>
        </section>
        <button className="detail-choice-row" onClick={() => setSheet(true)}>
          <span className="choice-label">已选</span><b>{color} · {spec} · {quantity} 件</b><Icon name="chevron" size={18}/>
        </button>
        <div className="detail-choice-row static-row"><span className="choice-label">配送</span><b>现货 · 预计明日送达</b><span className="freight">免运费</span></div>
        <section className="review-block">
          <div className="section-heading compact"><div><span className="shelf-mark"></span><h2>买家评价</h2></div><button onClick={() => navigate('app/reviews')}>全部 {MOCKS.reviews.length * 63} <Icon name="chevron" size={15}/></button></div>
          {MOCKS.reviews.map((review) => (
            <article className="review-item" key={review.id}>
              <div><Avatar name={review.user}/><b>{review.user}</b><span>{review.time}</span></div>
              <p>{review.content}</p>
            </article>
          ))}
        </section>
        <section className="product-story">
          <div className="section-heading compact"><div><span className="shelf-mark"></span><h2>商品详情</h2></div></div>
          <div className="story-panel"><ProductArt type="headphone"/><div><b>柔和包覆，长时间佩戴也轻松</b><span>记忆海绵耳罩与轻量化头梁，保持稳定贴合。</span></div></div>
        </section>
      </main>
      <div className="mobile-action-bar">
        <button className="action-icon" onClick={() => navigate('app/shop')}><Icon name="store"/><span>店铺</span></button>
        <button className="action-icon" onClick={() => navigate('app/service')}><Icon name="message"/><span>客服</span></button>
        <button className="secondary-action" onClick={() => { setSheet(true); notify('请选择规格后加入购物车'); }}>加入购物车</button>
        <button className="primary-action" onClick={() => setSheet(true)}>立即购买</button>
      </div>
      {sheet && (
        <div className="sheet-mask" onClick={() => setSheet(false)}>
          <section className="mobile-sheet" onClick={(e) => e.stopPropagation()}>
            <div className="sheet-handle"></div>
            <div className="sku-summary"><ProductArt type={product.art}/><div><b>¥{product.price}</b><span>库存 {product.stock} 件</span><small>已选 {color} · {spec}</small></div><button onClick={() => setSheet(false)}>×</button></div>
            <div className="option-group"><label>颜色</label><div>{product.colors.map((item) => <button key={item} className={color === item ? 'active' : ''} onClick={() => setColor(item)}>{item}</button>)}</div></div>
            <div className="option-group"><label>规格</label><div>{product.specs.map((item) => <button key={item} className={spec === item ? 'active' : ''} onClick={() => setSpec(item)}>{item}</button>)}</div></div>
            <div className="quantity-row"><label>数量</label><QuantityControl value={quantity} setValue={setQuantity}/></div>
            <button className="sheet-confirm" onClick={() => { setSheet(false); navigate('app/confirm'); }}>确认，去结算</button>
          </section>
        </div>
      )}
    </div>
  );
}

function AppConfirm({ navigate, notify }) {
  const product = MOCKS.products[0];
  const [coupon, setCoupon] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const total = product.price * quantity;
  const discount = coupon ? 20 : 0;
  return (
    <div className="mobile-screen order-confirm-screen" data-screen-label="App 确认订单">
      <MobileStatusBar tone="surface"/>
      <header className="mobile-page-title"><button onClick={() => navigate('app/product')}><Icon name="back"/></button><h1>确认订单</h1><div className="mobile-header-tools"><WechatCapsule/></div></header>
      <main className="mobile-scroll confirm-scroll">
        <button className="address-panel" onClick={() => navigate('app/addresses')}>
          <span className="address-icon"><Icon name="store"/></span>
          <div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div>
          <Icon name="chevron"/>
        </button>
        <section className="confirm-shop-card">
          <div className="shop-line"><Icon name="store" size={17}/><b>Yeah 数码生活馆</b><Icon name="chevron" size={15}/></div>
          <div className="confirm-product"><ProductArt type={product.art}/><div><b>{product.name}</b><span>雾银 · 标准版</span><div><strong>¥{product.price}</strong><QuantityControl value={quantity} setValue={setQuantity} max={3}/></div></div></div>
          <label className="message-input"><span>订单备注</span><input placeholder="选填，请先和商家协商一致"/></label>
        </section>
        <section className="confirm-options">
          <button onClick={() => navigate('app/coupon-select')}><span>优惠券</span><span className={coupon ? 'saving' : ''}>{coupon ? '-¥20.00' : '1 张可用'} <Icon name="chevron" size={16}/></span></button>
          <div><span>配送方式</span><b>普通快递 · 免运费</b></div>
        </section>
        <section className="price-summary">
          <div><span>商品金额</span><b>¥{total.toFixed(2)}</b></div>
          <div><span>运费</span><b>¥0.00</b></div>
          <div><span>优惠金额</span><b className="saving">-¥{discount.toFixed(2)}</b></div>
          <div className="summary-total"><span>应付金额</span><strong>¥{(total - discount).toFixed(2)}</strong></div>
        </section>
      </main>
      <div className="confirm-submit-bar"><div><span>共 {quantity} 件</span><b>¥{(total - discount).toFixed(2)}</b></div><button onClick={() => { notify('订单提交成功'); navigate('app/pay-result'); }}>提交订单</button></div>
    </div>
  );
}

function AppOrder({ navigate, notify }) {
  const order = MOCKS.orders[1];
  return (
    <div className="mobile-screen order-detail-screen" data-screen-label="App 订单详情">
      <MobileStatusBar tone="surface"/>
      <header className="mobile-page-title"><button onClick={() => navigate('app/orders')}><Icon name="back"/></button><h1>订单详情</h1><div className="mobile-header-tools"><button onClick={()=>notify('订单操作：复制编号、联系客服')}><Icon name="more"/></button><WechatCapsule/></div></header>
      <main className="mobile-scroll order-detail-scroll">
        <section className="order-state-panel"><span className="state-icon"><Icon name="truck" size={28}/></span><div><h2>商家已发货</h2><p>包裹正在前往杭州转运中心</p></div></section>
        <button className="logistics-preview" onClick={() => navigate('app/logistics')}><div><span>今天 09:12</span><b>快件已从上海集散中心发出</b><small>顺丰速运 · SF1462903821</small></div><Icon name="chevron"/></button>
        <section className="order-address"><Icon name="store"/><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div></section>
        <section className="order-goods"><div className="shop-line"><Icon name="store" size={17}/><b>Yeah 美护精选</b></div><div className="confirm-product"><ProductArt type="serum"/><div><b>白茶屏障修护精华</b><span>30ml · 单瓶</span><div><strong>¥168.00</strong><small>×1</small></div></div></div></section>
        <section className="order-timeline">
          <div className="section-heading compact"><div><span className="shelf-mark"></span><h2>订单进度</h2></div></div>
          {[['已发货','今天 08:50'],['支付成功','10月27日 10:02'],['提交订单','10月27日 10:02']].map((item,index) => <div className={index === 0 ? 'timeline-row active' : 'timeline-row'} key={item[0]}><i></i><b>{item[0]}</b><span>{item[1]}</span></div>)}
        </section>
        <section className="order-info-list"><div><span>订单编号</span><b>{order.orderNo}</b></div><div><span>商品总额</span><b>¥168.00</b></div><div><span>优惠金额</span><b>-¥0.00</b></div><div className="summary-total"><span>实付金额</span><strong>¥168.00</strong></div></section>
      </main>
      <div className="order-detail-actions"><button onClick={() => navigate('app/refund')}>申请退款</button><button onClick={() => navigate('app/service')}>联系客服</button><button className="primary" onClick={() => notify('已确认收货')}>确认收货</button></div>
    </div>
  );
}

function SimpleMobileHeader({ title, navigate, back = 'app/home', action, showBack = true, tone = 'surface' }) {
  return <><MobileStatusBar tone={tone}/><header className={`mobile-page-title mobile-page-title-${tone}`}>{showBack?<button onClick={() => navigate(back)}><Icon name="back"/></button>:<span className="mobile-header-spacer"></span>}<h1>{title}</h1><div className="mobile-header-tools">{action}<WechatCapsule/></div></header></>;
}

function AppCategory({ navigate }) {
  const categoryTree = [
    { id:'digital', name:'数码', groups:[
      { id:'audio', name:'耳机音响', children:[['headphone','蓝牙耳机'],['headphone','头戴耳机'],['headphone','音响'],['headphone','耳机配件'],['headphone','智能穿戴'],['headphone','影音设备']] },
      { id:'phone', name:'手机通讯', children:[['headphone','手机'],['headphone','平板电脑'],['headphone','充电设备'],['headphone','手机配件'],['headphone','运营商'],['headphone','智能设备']] },
      { id:'computer', name:'电脑办公', children:[['headphone','笔记本'],['headphone','显示器'],['headphone','键鼠'],['headphone','办公文具'],['headphone','打印设备'],['headphone','数码配件']] }
    ]},
    { id:'home', name:'居家', groups:[
      { id:'furniture', name:'家具家居', children:[['chair','休闲椅'],['chair','桌椅'],['chair','收纳用品'],['chair','床品'],['chair','家居饰品'],['chair','灯具']] },
      { id:'kitchen', name:'厨房餐饮', children:[['coffee','咖啡器具'],['coffee','烹饪锅具'],['coffee','餐具'],['coffee','厨房收纳'],['coffee','水杯水壶'],['coffee','烘焙工具']] },
      { id:'clean', name:'生活日用', children:[['chair','清洁用品'],['chair','纸品湿巾'],['chair','洗护用品'],['chair','香薰'],['chair','雨具'],['chair','家纺']] }
    ]},
    { id:'beauty', name:'美护', groups:[
      { id:'skincare', name:'护肤美妆', children:[['serum','精华'],['serum','面霜'],['serum','洁面'],['serum','防晒'],['serum','面膜'],['serum','彩妆']] },
      { id:'personal', name:'个护健康', children:[['serum','洗发护发'],['serum','身体护理'],['serum','口腔护理'],['serum','美容仪'],['serum','健康监测'],['serum','香水香氛']] }
    ]},
    { id:'food', name:'食品', groups:[
      { id:'coffee', name:'咖啡冲饮', children:[['coffee','挂耳咖啡'],['coffee','咖啡豆'],['coffee','茶饮'],['coffee','冲泡粉'],['coffee','保温杯'],['coffee','礼盒']] },
      { id:'snack', name:'休闲零食', children:[['coffee','坚果炒货'],['coffee','糕点'],['coffee','果干'],['coffee','肉脯'],['coffee','糖巧'],['coffee','地方特产']] },
      { id:'gift', name:'礼盒定制', children:[] }
    ]},
    { id:'other', name:'其它', groups:[] }
  ];
  const [activePrimary, setActivePrimary] = useState('digital');
  const [activeGroup, setActiveGroup] = useState('audio');
  const categoryScrollRef = React.useRef(null);
  const currentPrimary = categoryTree.find((item) => item.id === activePrimary) || categoryTree[0];
  const switchPrimary = (id) => {
    const primary = categoryTree.find((item) => item.id === id) || categoryTree[0];
    setActivePrimary(id);
    setActiveGroup(primary.groups[0]?.id || '');
    requestAnimationFrame(() => categoryScrollRef.current?.scrollTo({ top:0, behavior:'smooth' }));
  };
  const scrollToGroup = (group) => {
    setActiveGroup(group.id);
    document.getElementById(`category-group-${currentPrimary.id}-${group.id}`)?.scrollIntoView({ behavior:'smooth', block:'start' });
  };
  const syncGroupOnScroll = (event) => {
    if (!currentPrimary.groups.length) return;
    const top = event.currentTarget.getBoundingClientRect().top;
    const passed = currentPrimary.groups.filter((group) => {
      const section = document.getElementById(`category-group-${currentPrimary.id}-${group.id}`);
      return section && section.getBoundingClientRect().top <= top + 20;
    });
    setActiveGroup((passed[passed.length - 1] || currentPrimary.groups[0]).id);
  };
  return (
    <div className="mobile-screen" data-screen-label="App 分类">
      <SimpleMobileHeader title="商品分类" navigate={navigate} showBack={false} tone="paper"/>
      <main className="category-page">
        <aside aria-label="一级分类">
          {categoryTree.map((item) => <button key={item.id} className={activePrimary === item.id ? 'active' : ''} onClick={() => switchPrimary(item.id)}>{item.name}</button>)}
        </aside>
        <section>
          {currentPrimary.groups.length ? <><div className="category-small-tabs" aria-label="二级分类快速定位">
            {currentPrimary.groups.map((group) => <button key={group.id} className={activeGroup === group.id ? 'active' : ''} onClick={() => scrollToGroup(group)}>{group.name}</button>)}
          </div>
          <div ref={categoryScrollRef} className="category-all-scroll" onScroll={syncGroupOnScroll}>
            {currentPrimary.groups.map((group) => <div key={group.id} id={`category-group-${currentPrimary.id}-${group.id}`} className="category-group-section">
              <div className="category-leaf-head"><b>{group.name}</b></div>
              {group.children.length ? <div className="category-leaf-grid">
                {group.children.map(([art, name]) => <button key={name} className="category-leaf-card" onClick={() => navigate('app/products')}><ProductArt type={art} bare/><span>{name}</span></button>)}
              </div> : <div className="category-sub-empty"><b>暂未配置子分类</b><span>该分类下暂时没有可浏览内容</span></div>}
            </div>)}
          </div></> : <div className="category-empty-state"><b>暂未配置二级分类</b><span>该分类下暂无可浏览内容</span></div>}
        </section>
      </main>
      <MobileBottomNav active="分类" navigate={navigate}/>
    </div>
  );
}

function AppProductList({ navigate }) {
  const [keyword, setKeyword] = useState('');
  const [sort, setSort] = useState('综合');
  const [layout, setLayout] = useState('grid');
  const filtered = MOCKS.products.filter((item) => item.name.includes(keyword));
  const sorted = [...filtered].sort((a,b) => sort === '价格' ? a.price-b.price : sort === '销量' ? b.sales-a.sales : 0);
  return <div className="mobile-screen" data-screen-label="App 商品列表"><SimpleMobileHeader title="商品列表" navigate={navigate}/><main className="mobile-scroll product-list-page"><label className="list-search"><Icon name="search" size={17}/><input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="搜索商品"/></label><div className="sort-row">{['综合','销量','价格'].map((item) => <button key={item} className={sort === item ? 'active' : ''} onClick={() => setSort(item)}>{item}</button>)}<button className={layout==='list'?'active':''} onClick={()=>setLayout(layout==='grid'?'list':'grid')}><Icon name="grid" size={16}/></button></div><div className={`product-grid list-grid ${layout==='list'?'single-column':''}`}>{sorted.map((item) => <button className="product-card" key={item.id} onClick={() => navigate('app/product')}><ProductArt type={item.art}/><span className="product-card-name">{item.name}</span><span className="product-card-sub">{item.subtitle}</span><span className="product-card-meta"><b>¥{item.price}</b><small>已售 {item.sales}</small></span></button>)}</div>{!sorted.length && <div className="mobile-empty"><Icon name="search" size={26}/><b>没有找到相关商品</b><span>换个关键词试试</span></div>}</main></div>;
}

function AppCart({ navigate, notify }) {
  const [items, setItems] = useState(MOCKS.products.slice(0,3).map((item,index) => ({...item, checked:index < 2, quantity:1})));
  const update = (id, edits) => setItems(items.map((item) => item.id === id ? {...item,...edits} : item));
  const checked = items.filter((item) => item.checked);
  const total = checked.reduce((sum,item) => sum + item.price*item.quantity,0);
  return <div className="mobile-screen" data-screen-label="App 购物车"><SimpleMobileHeader title="购物车" navigate={navigate} showBack={false} tone="paper"/><main className="mobile-scroll cart-page">{items.map((item) => <article className="cart-item" key={item.id}><button className={`check-dot ${item.checked ? 'active' : ''}`} onClick={() => update(item.id,{checked:!item.checked})}>{item.checked && <Icon name="check" size={13}/>}</button><ProductArt type={item.art}/><div><b>{item.name}</b><span>{item.colors[0]} · {item.specs[0]}</span><strong>¥{item.price}</strong><QuantityControl value={item.quantity} setValue={(value) => update(item.id,{quantity:value})} max={5}/></div></article>)}</main><div className="cart-submit"><button className={`check-dot ${checked.length === items.length ? 'active' : ''}`} onClick={() => setItems(items.map((item) => ({...item,checked:checked.length !== items.length})))}>{checked.length === items.length && <Icon name="check" size={13}/>}</button><span>全选</span><div><small>合计</small><b>¥{total.toFixed(2)}</b></div><button disabled={!checked.length} onClick={() => checked.length ? navigate('app/confirm') : notify('请先选择商品')}>结算（{checked.length}）</button></div><MobileBottomNav active="购物车" navigate={navigate}/></div>;
}

function AppOrders({ navigate, route, notify }) {
  const requestedTab = routeParam(route, 'tab');
  const [tab, setTab] = useState(requestedTab || '全部');
  const tabs = ['全部','待付款','待发货','待收货','退款/售后'];
  const visible = MOCKS.orders.filter((item) => tab === '全部' || (tab === '退款/售后' ? item.status === '退款中' : item.status === tab));
  return <div className="mobile-screen" data-screen-label="App 订单列表"><SimpleMobileHeader title="我的订单" navigate={navigate} back="app/profile"/><div className="order-tabs">{tabs.map((item) => <button key={item} className={tab === item ? 'active' : ''} onClick={() => setTab(item)}>{item}</button>)}</div><main className="mobile-scroll mobile-order-list">{visible.map((order) => <article className="mobile-order-card" key={order.id}><header><span className="mono">{order.orderNo}</span><StatusTicket tone={statusTone(order.status)}>{order.status}</StatusTicket></header><button className="mobile-order-product" onClick={() => navigate('app/order')}><ProductArt type={order.product.includes('耳机')?'headphone':order.product.includes('精华')?'serum':order.product.includes('咖啡')?'coffee':'chair'}/><div><b>{order.product}</b><span>{order.type} · 数量 × {order.quantity}</span><strong>¥{order.finalAmount.toFixed(2)}</strong></div></button><footer><span>{order.createdAt}</span><div><button onClick={() => navigate('app/order')}>查看详情</button>{order.status === '待收货' && <button className="primary" onClick={() => notify('已确认收货')}>确认收货</button>}</div></footer></article>)}{!visible.length&&<div className="mobile-empty"><Icon name="order" size={27}/><b>暂无{tab}订单</b><span>订单状态变化后会显示在这里</span></div>}</main></div>;
}

function AppProfile({ navigate }) {
  const orderLinks = [['order','待付款','待付款'],['box','待发货','待发货'],['truck','待收货','待收货'],['message','待评价','待评价'],['coupon','退款/售后','退款/售后']];
  const menus = [['heart','我的收藏','app/favorites'],['clock','浏览足迹','app/history'],['ticket','优惠券','app/coupons'],['points','我的积分','app/points'],['store','地址管理','app/addresses'],['message','客服中心','app/service'],['spark','意见反馈','app/feedback'],['settings','设置','app/settings']];
  return <div className="mobile-screen profile-screen" data-screen-label="App 个人中心"><SimpleMobileHeader title="我的" navigate={navigate} showBack={false} tone="dark"/><main className="mobile-scroll with-bottom-nav"><section className="profile-header" onClick={() => navigate('app/profile-edit')}><Avatar name="林可" size={54}/><div><h1>林可</h1><span>普通会员 · ID 211386217001</span></div><button onClick={(event)=>{event.stopPropagation();navigate('app/profile-edit');}}><Icon name="chevron"/></button></section><section className="profile-order-panel"><header><b>我的订单</b><button onClick={() => navigate('app/orders')}>全部订单 <Icon name="chevron" size={14}/></button></header><div>{orderLinks.map(([icon,label,targetTab]) => <button key={label} onClick={() => navigate(`app/orders?tab=${encodeURIComponent(targetTab)}`)}><span><Icon name={icon} size={19}/>{label === '待发货' && <i>1</i>}</span><small>{label}</small></button>)}</div></section><section className="profile-menu">{menus.map(([icon,label,target]) => <button key={label} onClick={() => navigate(target)}><span><Icon name={icon} size={18}/>{label}</span><Icon name="chevron" size={15}/></button>)}</section></main><MobileBottomNav active="我的" navigate={navigate}/></div>;
}

function AppCollection({ navigate, mode }) {
  const title = mode === 'favorites' ? '我的收藏' : '浏览足迹';
  return <div className="mobile-screen" data-screen-label={`App ${title}`}><SimpleMobileHeader title={title} navigate={navigate} back="app/profile"/><main className="mobile-scroll collection-page">{mode === 'history' && <div className="history-date">今天 · 浏览 4 件商品</div>}<div className="product-grid list-grid">{MOCKS.products.map((item) => <button className="product-card" key={item.id} onClick={() => navigate('app/product')}><ProductArt type={item.art}/><span className="product-card-name">{item.name}</span><span className="product-card-meta"><b>¥{item.price}</b><small>{mode === 'favorites' ? '已收藏' : '10:26'}</small></span></button>)}</div></main></div>;
}

function AppCoupons({ navigate, notify }) {
  const [tab,setTab] = useState('可使用');
  const rows = tab==='已使用'?MOCKS.coupons.slice(0,1):tab==='已失效'?MOCKS.coupons.slice(1):MOCKS.coupons;
  return <div className="mobile-screen" data-screen-label="App 我的优惠券"><SimpleMobileHeader title="我的优惠券" navigate={navigate} back="app/profile" action={<button onClick={()=>navigate('app/coupon-center')}>领券</button>}/><div className="coupon-tabs">{['可使用','已使用','已失效'].map((item) => <button key={item} className={tab === item ? 'active' : ''} onClick={() => setTab(item)}>{item}</button>)}</div><main className="mobile-scroll coupon-page">{rows.map((coupon) => <article className={`coupon-card ${tab!=='可使用'?'is-disabled':''}`} key={coupon.id}><div className="coupon-value"><b>{coupon.value}</b><span>{coupon.condition}</span></div><div className="coupon-copy"><b>{coupon.name}</b><span>{coupon.shop}</span><small>有效期至 {coupon.endAt}</small></div><button disabled={tab!=='可使用'} onClick={() => navigate('app/products')}>{tab==='已使用'?'已使用':tab==='已失效'?'已失效':'去使用'}</button></article>)}</main></div>;
}

function AppCouponCenter({ navigate, notify }) {
  const [claimed,setClaimed] = useState([]);
  return <div className="mobile-screen" data-screen-label="App 领券中心"><SimpleMobileHeader title="领券中心" navigate={navigate} back="app/coupons"/><main className="mobile-scroll coupon-page coupon-center-page"><div className="coupon-center-note"><b>店铺好券</b><span>领取后可在确认订单时选择使用</span></div>{MOCKS.coupons.map((coupon)=><article className="coupon-card" key={coupon.id}><div className="coupon-value"><b>{coupon.value}</b><span>{coupon.condition}</span></div><div className="coupon-copy"><b>{coupon.name}</b><span>{coupon.shop}</span><small>有效期至 {coupon.endAt}</small></div><button disabled={claimed.includes(coupon.id)} onClick={()=>{setClaimed([...claimed,coupon.id]);notify('领取成功，已放入我的优惠券');}}>{claimed.includes(coupon.id)?'已领取':'领取'}</button></article>)}</main></div>;
}

function AppPoints({ navigate }) {
  const [type,setType] = useState('全部');
  const records = [['订单完成赠送','+399','2026-10-24'],['完成评价赠送','+10','2026-10-23'],['积分商品兑换','-300','2026-10-20'],['平台活动奖励','+50','2026-10-18']];
  const visible = records.filter((row)=>type==='全部'||(type==='收入'?row[1][0]==='+':row[1][0]==='-'));
  const cycleType = () => setType(type==='全部'?'收入':type==='收入'?'支出':'全部');
  return <div className="mobile-screen" data-screen-label="App 我的积分"><SimpleMobileHeader title="我的积分" navigate={navigate} back="app/profile" action={<button onClick={()=>navigate('app/points-mall')}>去兑换</button>}/><main className="mobile-scroll points-page"><section className="points-balance"><span>可用积分</span><b>1,280</b><div><span>累计获得 2,320</span><span>累计使用 1,040</span></div></section><section className="points-records account-records"><div className="points-record-title"><h3>积分明细</h3><button onClick={cycleType}>{type} <Icon name="down" size={14}/></button></div>{visible.map((row) => <div key={row[0]}><span><b>{row[0]}</b><small>{row[2]}</small></span><strong className={row[1][0] === '+' ? 'plus' : 'minus'}>{row[1]}</strong></div>)}</section></main></div>;
}

function AppPointsMall({ navigate }) {
  const [category,setCategory] = useState('全部');
  const products = MOCKS.pointsProducts.filter((item)=>category==='全部'||item.type===category);
  return <div className="mobile-screen" data-screen-label="App 积分商城"><SimpleMobileHeader title="积分商城" navigate={navigate} back="app/home" action={<button onClick={()=>navigate('app/points-orders')}>订单</button>}/><main className="mobile-scroll points-mall-page"><div className="points-mall-tabs">{['全部','实物商品','优惠券','虚拟权益'].map((item)=><button key={item} className={category===item?'active':''} onClick={()=>setCategory(item)}>{item}</button>)}</div><div className="points-product-list">{products.map((item) => <article key={item.id}><ProductArt type={item.art}/><div><StatusTicket small>{item.type}</StatusTicket><b>{item.name}</b><span>{item.points} 积分 · 库存 {item.stock}</span></div><button onClick={() => navigate('app/points-product')}>查看</button></article>)}</div></main></div>;
}

function AppActivity({ navigate, type }) {
  const isSeckill = type === 'seckill';
  const rows = isSeckill ? MOCKS.activities.seckill : MOCKS.activities.group;
  const tabs = isSeckill?['10:00','14:00','20:00']:['全部活动','报名中','进行中'];
  const [active,setActive] = useState(tabs[0]);
  return <div className="mobile-screen" data-screen-label={`App ${isSeckill?'秒杀':'拼团'}`}><SimpleMobileHeader title={isSeckill?'秒杀活动':'拼团活动'} navigate={navigate}/><main className="mobile-scroll activity-page"><div className="activity-tabs">{tabs.map((item,index) => <button className={active===item?'active':''} onClick={()=>setActive(item)} key={item}>{item}<small>{index===0?'进行中':'即将开始'}</small></button>)}</div>{rows.map((row) => <article className="activity-card" key={row.id}><ProductArt type={row.art}/><div><StatusTicket tone={row.status.includes('进行')||row.status.includes('抢购')?'danger':'neutral'} small>{row.status}</StatusTicket><h3>{row.title}</h3><span>{row.time}</span><b>¥{row.price}</b><small>{isSeckill?`剩余 ${row.stock} 件`:row.people}</small></div><button onClick={() => navigate(isSeckill?'app/seckill-products':'app/groupbuy-products')}>{isSeckill?'查看商品':'查看拼团'}</button></article>)}</main></div>;
}

function AppService({ navigate, notify }) {
  const [active,setActive] = useState(MOCKS.conversations[0]);
  const [messages,setMessages] = useState([{side:'them',text:'您好，请问商品使用中遇到了什么问题？'},{side:'me',text:'想确认一下今天可以发货吗？'},{side:'them',text:'您好，订单今天可以发出。'}]);
  const [text,setText] = useState('');
  const send = () => {if(!text.trim()) return;setMessages([...messages,{side:'me',text:text.trim()}]);setText('');};
  return <div className="mobile-screen" data-screen-label="App 客服"><SimpleMobileHeader title={active.name} navigate={navigate} back="app/profile"/><main className="service-page"><div className="conversation-strip">{MOCKS.conversations.map((item) => <button key={item.id} className={active.id===item.id?'active':''} onClick={() => setActive(item)}><Avatar name={item.name}/><span>{item.name.replace('Yeah ','')}</span>{item.unread>0&&<i>{item.unread}</i>}</button>)}</div><div className="chat-stream">{messages.map((msg,index) => <div key={index} className={`chat-bubble ${msg.side}`}>{msg.text}</div>)}</div><div className="chat-input"><button onClick={()=>notify('图片与订单卡片面板已打开')}><Icon name="plus"/></button><input value={text} onChange={(e)=>setText(e.target.value)} onKeyDown={(e)=>e.key==='Enter'&&send()} placeholder="输入消息"/><button className="send" onClick={send}>发送</button></div></main></div>;
}

function AppAddresses({ navigate, notify }) {
  return <div className="mobile-screen" data-screen-label="App 地址管理"><SimpleMobileHeader title="地址管理" navigate={navigate} back="app/profile" action={<button onClick={()=>navigate('app/address-edit?mode=create')}><Icon name="plus"/></button>}/><main className="mobile-scroll address-list-page">{MOCKS.addresses.map((item) => <article key={item.id}><header><b>{item.name}</b><span>{item.mobile}</span>{item.isDefault&&<StatusTicket tone="success" small>默认</StatusTicket>}</header><p>{item.address}</p><footer><button onClick={()=>notify('已设为默认地址')}>设为默认</button><button onClick={()=>navigate('app/address-edit?mode=edit')}>编辑</button></footer></article>)}</main></div>;
}

function AppReviews({ navigate }) {
  const [filter,setFilter] = useState('全部');
  const [preview,setPreview] = useState(null);
  const rows = [...MOCKS.reviews,...MOCKS.reviews].map((review,index)=>({...review,index,hasImage:index%2===0}));
  const visible = rows.filter((review)=>filter==='全部'||filter==='好评'||review.hasImage);
  return <div className="mobile-screen" data-screen-label="App 商品评价"><SimpleMobileHeader title="商品评价" navigate={navigate} back="app/product"/><main className="mobile-scroll reviews-page"><section className="review-summary"><b>98<small>%</small></b><span>好评率</span><div><strong>126</strong><small>全部评价</small></div><div><strong>42</strong><small>有图评价</small></div></section><div className="review-filter">{['全部','有图','好评'].map((item)=><button key={item} className={filter===item?'active':''} onClick={()=>setFilter(item)}>{item}</button>)}</div>{visible.map((review)=><article className="review-full-item" key={`${review.id}-${review.index}`} onClick={()=>navigate('app/review-detail')}><header><Avatar name={review.user}/><b>{review.user}</b><span>{review.time}</span></header><div className="stars">★★★★★</div><p>{review.content}</p>{review.hasImage&&<div className="review-image-row" onClick={(event)=>event.stopPropagation()}><button onClick={()=>setPreview(review.index%4===0?'headphone':'serum')}><ProductArt type={review.index%4===0?'headphone':'serum'}/></button><button onClick={()=>setPreview(review.index%4===0?'headphone':'serum')}><ProductArt type={review.index%4===0?'headphone':'serum'}/></button></div>}<small>规格：雾银 · 标准版</small></article>)}</main>{preview&&<div className="image-preview-mask" onClick={()=>setPreview(null)}><button onClick={()=>setPreview(null)}>×</button><ProductArt type={preview}/><span>点击任意位置关闭</span></div>}</div>;
}

function AppFeedback({ navigate, notify }) {
  const [content,setContent] = useState('');
  const [submitted,setSubmitted] = useState(false);
  return <div className="mobile-screen" data-screen-label="App 意见反馈"><SimpleMobileHeader title="意见反馈" navigate={navigate} back="app/profile"/><main className="mobile-scroll feedback-page-proto"><section><h2>告诉我们你的想法</h2><p>问题与建议会进入平台处理列表。</p><textarea value={content} onChange={(e)=>setContent(e.target.value)} placeholder="请描述你遇到的问题或建议"></textarea><div className="feedback-upload"><button onClick={()=>notify('已添加一张模拟截图')}><Icon name="plus"/><span>添加图片</span></button></div><label>联系方式<input placeholder="选填，方便我们联系你"/></label><button className="feedback-submit" onClick={()=>{if(!content.trim())return notify('请填写反馈内容');setSubmitted(true);setContent('');notify('反馈已提交');}}>提交反馈</button></section><h3>我的反馈</h3>{submitted&&<article className="feedback-record"><StatusTicket tone="warning">待处理</StatusTicket><b>刚刚提交的反馈</b><span>平台会尽快处理</span></article>}<article className="feedback-record"><StatusTicket tone="success">已解决</StatusTicket><b>希望优化订单页面信息层级</b><span>平台回复：已记录并纳入本次 UI 优化。</span></article></main></div>;
}

function AppSettings({ navigate, notify }) {
  const rows = [['user','个人资料','app/profile-edit'],['message','消息通知','app/notifications'],['settings','隐私设置','app/privacy'],['spark','关于我们','app/about']];
  return <div className="mobile-screen" data-screen-label="App 设置"><SimpleMobileHeader title="设置" navigate={navigate} back="app/profile"/><main className="mobile-scroll settings-page">{rows.map(([icon,label,target])=><button key={label} onClick={()=>navigate(target)}><span><Icon name={icon}/>{label}</span><Icon name="chevron" size={16}/></button>)}<button className="logout-button" onClick={()=>{notify('已退出当前账号');navigate('app/login');}}>退出登录</button></main></div>;
}

function AppShops({ navigate }) {
  const shops = [['Yeah 数码生活馆','数码影音 · 126 件商品','headphone'],['山野生活店','居家生活 · 38 件商品','chair'],['白茶美护精选','美容护肤 · 45 件商品','serum']];
  return <div className="mobile-screen" data-screen-label="App 店铺列表"><SimpleMobileHeader title="店铺" navigate={navigate}/><main className="mobile-scroll secondary-page"><label className="list-search"><Icon name="search" size={17}/><input placeholder="搜索店铺"/></label>{shops.map((shop)=><button className="shop-list-card" key={shop[0]} onClick={()=>navigate('app/shop')}><ProductArt type={shop[2]}/><div><b>{shop[0]}</b><span>{shop[1]}</span><small>评分 4.9 · 支持次日达</small></div><Icon name="chevron" size={17}/></button>)}</main></div>;
}

function AppShop({ navigate }) {
  const [following,setFollowing] = useState(true); const [tab,setTab] = useState('首页');
  const products = tab==='新品'?MOCKS.products.slice(0,2):MOCKS.products.slice(0,3);
  return <div className="mobile-screen" data-screen-label="App 店铺详情"><SimpleMobileHeader title="店铺详情" navigate={navigate} back="app/shops"/><main className="mobile-scroll shop-detail-page"><section className="shop-identity"><span className="shop-logo-mark">Y</span><div><h2>Yeah 数码生活馆</h2><p>在售 126 件商品 · 店铺评分 4.9</p></div><button onClick={()=>setFollowing(!following)}>{following?'已关注':'+ 关注'}</button></section><div className="shop-tabs">{['首页','全部商品','新品'].map((item)=><button key={item} className={tab===item?'active':''} onClick={()=>setTab(item)}>{item}</button>)}</div>{tab==='首页'&&<section className="shop-feature"><div><span>店铺精选</span><h3>声音与生活的理想装备</h3><button onClick={()=>setTab('全部商品')}>查看全部</button></div><ProductArt type="headphone"/></section>}<div className="product-grid shop-product-grid">{products.map((item)=><button className="product-card" key={item.id} onClick={()=>navigate('app/product')}><ProductArt type={item.art}/><span className="product-card-name">{item.name}</span><span className="product-card-meta"><b>¥{item.price}</b><small>已售 {item.sales}</small></span></button>)}</div></main></div>;
}

function AppPayResult({ navigate }) {
  return <div className="mobile-screen" data-screen-label="App 支付结果"><SimpleMobileHeader title="支付结果" navigate={navigate}/><main className="result-page"><span className="result-icon"><Icon name="check" size={30}/></span><h1>支付成功</h1><p>订单已支付，商家会尽快安排发货。</p><div><span>支付金额</span><b>¥379.00</b></div><button className="primary" onClick={()=>navigate('app/order')}>查看订单</button><button onClick={()=>navigate('app/home')}>返回首页</button></main></div>;
}

function AppLogistics({ navigate }) {
  const nodes = [['今天 09:12','快件已从上海集散中心发出'],['今天 07:30','快件已到达上海集散中心'],['昨天 18:50','顺丰速运已揽收'],['昨天 16:42','商家已打包完成']];
  return <div className="mobile-screen" data-screen-label="App 物流详情"><SimpleMobileHeader title="物流详情" navigate={navigate} back="app/order"/><main className="mobile-scroll logistics-page"><section><Icon name="truck" size={25}/><div><b>顺丰速运</b><span className="mono">SF1462903821</span></div><StatusTicket tone="info">运输中</StatusTicket></section><div className="logistics-product"><ProductArt type="serum"/><span>白茶屏障修护精华 · 30ml</span></div><article>{nodes.map((node,index)=><div className={index===0?'active':''} key={node[0]}><i></i><span><b>{node[1]}</b><small>{node[0]}</small></span></div>)}</article></main></div>;
}

function AppRefund({ navigate, notify }) {
  const [reason,setReason] = useState('商品与描述不符');
  const [remark,setRemark] = useState('');
  return <div className="mobile-screen" data-screen-label="App 申请退款"><SimpleMobileHeader title="申请退款" navigate={navigate} back="app/order"/><main className="mobile-scroll form-page"><section className="refund-product"><ProductArt type="serum"/><div><b>白茶屏障修护精华</b><span>可退款金额</span><strong>¥168.00</strong></div></section><label><span>退款类型</span><select defaultValue="仅退款"><option>仅退款</option></select></label><label><span>退款原因</span><select value={reason} onChange={(e)=>setReason(e.target.value)}><option>商品与描述不符</option><option>不想要了</option><option>物流原因</option></select></label><label><span>补充说明 <small>选填</small></span><textarea value={remark} onChange={(e)=>setRemark(e.target.value)} placeholder="请补充问题说明"></textarea></label><div className="upload-grid"><button onClick={()=>notify('已添加一张模拟凭证')}><Icon name="plus"/><span>上传凭证</span></button></div><button className="form-submit" onClick={()=>{notify('退款申请已提交');navigate('app/refund-detail');}}>提交申请</button></main></div>;
}

function AppReviewForm({ navigate, notify }) {
  const [rating,setRating] = useState(5); const [content,setContent] = useState('');
  return <div className="mobile-screen" data-screen-label="App 发表评价"><SimpleMobileHeader title="发表评价" navigate={navigate} back="app/orders"/><main className="mobile-scroll form-page"><section className="review-product-head"><ProductArt type="headphone"/><b>云感降噪头戴耳机</b></section><div className="rating-row"><span>商品评分</span><div>{[1,2,3,4,5].map((star)=><button key={star} className={star<=rating?'active':''} onClick={()=>setRating(star)}>★</button>)}</div></div><label><span>评价内容</span><textarea value={content} onChange={(e)=>setContent(e.target.value)} placeholder="分享真实使用体验"></textarea></label><div className="upload-grid"><button onClick={()=>notify('已添加一张模拟评价图片')}><Icon name="plus"/><span>添加图片</span></button></div><label className="anonymous-row"><input type="checkbox"/>匿名评价</label><button className="form-submit" onClick={()=>content.trim()? (notify('评价发布成功'),navigate('app/review-detail')):notify('请填写评价内容')}>发布评价</button></main></div>;
}

function AppReviewDetail({ navigate, notify }) {
  const [replies,setReplies] = useState([{side:'merchant',text:'感谢您的认可，耳罩配件可单独购买。'},{side:'buyer',text:'好的，后续有需要再联系。'},{side:'merchant',text:'有需要可以直接在这里继续留言。'}]);
  const [text,setText] = useState('');
  const canReply = replies[replies.length-1]?.side === 'merchant';
  const submit = () => { if(!text.trim()) return notify('请输入回复内容'); setReplies([...replies,{side:'buyer',text:text.trim()}]); setText(''); notify('回复已发布，等待商家回复'); };
  return <div className="mobile-screen" data-screen-label="App 评价详情"><SimpleMobileHeader title="评价详情" navigate={navigate} back="app/orders?tab=待评价"/><main className="mobile-scroll review-detail-page"><div className="review-product-head"><ProductArt type="headphone"/><div><b>云感降噪头戴耳机</b><span>雾银 · 标准版</span></div></div><article className="review-conversation"><header><Avatar name="林可"/><div><b>林可</b><span>2026-10-23 14:20</span></div><span className="stars-inline">★★★★★</span></header><p>佩戴两小时没有压耳，降噪对通勤很有帮助。包装也很稳妥。</p>{replies.map((reply,index)=><div key={index} className={reply.side==='merchant'?'merchant-reply':'buyer-reply'}><b>{reply.side==='merchant'?'商家回复':'我的回复'}</b><span>{reply.text}</span></div>)}</article>{canReply?<section className="review-reply-box"><textarea value={text} onChange={(e)=>setText(e.target.value)} placeholder="回复商家，继续沟通使用体验"></textarea><button onClick={submit}>发表回复</button></section>:<div className="reply-waiting"><Icon name="clock" size={16}/><span>已回复，等待商家继续回复后可再次留言</span></div>}</main></div>;
}

function AppProfileEdit({ navigate, notify }) {
  const [nickname,setNickname] = useState('林可');
  const [mobile,setMobile] = useState('138****7621');
  const [gender,setGender] = useState('女');
  const [birthday,setBirthday] = useState('1996-08-12');
  const [picker,setPicker] = useState('');
  const [draftMobile,setDraftMobile] = useState('');
  const [draftCode,setDraftCode] = useState('');
  const closePicker = () => { setPicker(''); setDraftMobile(''); setDraftCode(''); };
  const saveMobile = () => {
    if (!/^1\d{10}$/.test(draftMobile)) return notify('请输入正确的手机号');
    if (!/^\d{4,6}$/.test(draftCode)) return notify('请输入正确的验证码');
    setMobile(`${draftMobile.slice(0,3)}****${draftMobile.slice(-4)}`);
    closePicker(); notify('手机号已更新');
  };
  return <div className="mobile-screen" data-screen-label="App 编辑资料"><SimpleMobileHeader title="编辑个人资料" navigate={navigate} back="app/profile"/><main className="mobile-scroll profile-edit-page"><button className="avatar-edit" onClick={()=>notify('已选择新的模拟头像')}><Avatar name={nickname} size={64}/><span>更换头像</span><Icon name="chevron"/></button><section className="profile-edit-fields"><label className="profile-field-row editable"><span>昵称</span><input value={nickname} maxLength="20" onChange={(e)=>setNickname(e.target.value)} aria-label="昵称"/></label><button className="profile-field-row" onClick={()=>setPicker('mobile')}><span>手机号</span><b>{mobile}<Icon name="chevron" size={16}/></b></button><button className="profile-field-row" onClick={()=>setPicker('gender')}><span>性别</span><b>{gender}<Icon name="chevron" size={16}/></b></button><button className="profile-field-row" onClick={()=>setPicker('birthday')}><span>生日</span><b>{birthday}<Icon name="chevron" size={16}/></b></button></section><button className="form-submit" onClick={()=>nickname.trim()?notify('个人资料已保存'):notify('昵称不能为空')}>保存修改</button></main>{picker&&<div className="sheet-mask" onClick={closePicker}><section className="mobile-sheet profile-picker-sheet" onClick={(e)=>e.stopPropagation()}><div className="sheet-handle"></div><header><h2>{picker==='mobile'?'更换手机号':picker==='gender'?'选择性别':'选择生日'}</h2><button onClick={closePicker}>×</button></header>{picker==='gender'&&<div className="profile-option-list">{['男','女','不公开'].map((item)=><button key={item} className={gender===item?'active':''} onClick={()=>setGender(item)}><span>{item}</span>{gender===item&&<Icon name="check" size={17}/>}</button>)}</div>}{picker==='birthday'&&<label className="profile-date-field"><span>出生日期</span><input type="date" value={birthday} max="2026-10-27" onChange={(e)=>setBirthday(e.target.value)}/><small>点击日期可调用当前平台的原生日期选择器</small></label>}{picker==='mobile'&&<div className="profile-mobile-form"><label><span>新手机号</span><input value={draftMobile} maxLength="11" inputMode="numeric" onChange={(e)=>setDraftMobile(e.target.value.replace(/\D/g,''))} placeholder="请输入新手机号"/></label><label><span>验证码</span><div><input value={draftCode} maxLength="6" inputMode="numeric" onChange={(e)=>setDraftCode(e.target.value.replace(/\D/g,''))} placeholder="请输入验证码"/><button onClick={()=>notify('验证码已发送')}>获取验证码</button></div></label></div>}<button className="sheet-confirm" onClick={()=>picker==='mobile'?saveMobile():closePicker()}>确认</button></section></div>}</div>;
}

function AppAbout({ navigate }) {
  return <div className="mobile-screen" data-screen-label="App 关于我们"><SimpleMobileHeader title="关于我们" navigate={navigate} back="app/settings"/><main className="about-page"><span className="about-mark">Y</span><h1>Yeah Mall</h1><p>一个面向多店铺零售场景的开源商城项目，覆盖商品、交易、营销、评价、客服与会员积分。</p><div><span>当前版本</span><b>1.0.0</b></div><button onClick={()=>navigate('app/privacy')}><span>服务协议</span><Icon name="chevron" size={15}/></button><button onClick={()=>navigate('app/privacy')}><span>隐私政策</span><Icon name="chevron" size={15}/></button></main></div>;
}

function AppActivityProducts({ navigate, type }) {
  const title = type==='seckill'?'秒杀商品':'拼团商品';
  return <div className="mobile-screen" data-screen-label={`App ${title}`}><SimpleMobileHeader title={title} navigate={navigate} back={type==='seckill'?'app/seckill':'app/groupbuy'}/><main className="mobile-scroll activity-products-page"><header><b>{type==='seckill'?'秋日数码秒杀':'双人好物团'}</b><span>{type==='seckill'?'10:00–12:00':'活动结束后，成团订单可发货'}</span></header>{MOCKS.products.map((item)=><article key={item.id}><ProductArt type={item.art}/><div><b>{item.name}</b><span>{item.subtitle}</span><strong>¥{type==='seckill'?Math.max(49,item.price-100):Math.max(39,item.price-30)}</strong><del>¥{item.price}</del></div><button onClick={()=>navigate(type==='seckill'?'app/seckill-detail':'app/groupbuy-detail')}>{type==='seckill'?'抢购':'拼团'}</button></article>)}</main></div>;
}

function AppCouponSelect({ navigate }) {
  const [selected,setSelected] = useState(MOCKS.coupons[0].id);
  return <div className="mobile-screen" data-screen-label="App 选择优惠券"><SimpleMobileHeader title="选择优惠券" navigate={navigate} back="app/confirm"/><main className="mobile-scroll coupon-page">{MOCKS.coupons.map((coupon)=><button className={`coupon-select-card ${selected===coupon.id?'active':''}`} key={coupon.id} onClick={()=>setSelected(coupon.id)}><div><b>{coupon.value}</b><span>{coupon.condition}</span></div><div><strong>{coupon.name}</strong><small>{coupon.shop} · {coupon.endAt} 到期</small></div><i>{selected===coupon.id&&<Icon name="check" size={13}/>}</i></button>)}</main><div className="single-bottom-action"><button onClick={()=>navigate('app/confirm')}>确认使用</button></div></div>;
}

function AppPointsProduct({ navigate }) {
  const item = MOCKS.pointsProducts[0]; const [quantity,setQuantity] = useState(1);
  return <div className="mobile-screen" data-screen-label="App 积分商品"><SimpleMobileHeader title="积分商品" navigate={navigate} back="app/points"/><main className="mobile-scroll points-product-page"><ProductArt type={item.art}/><section><StatusTicket>{item.type}</StatusTicket><h1>{item.name}</h1><b>{item.points} <small>积分</small></b><p>环保棉帆布材质，适合日常通勤与购物使用。</p></section><div className="detail-choice-row static-row"><span className="choice-label">库存</span><b>{item.stock} 件</b><QuantityControl value={quantity} setValue={setQuantity} max={3}/></div><article><h3>兑换说明</h3><p>每位会员限兑 2 件，实物商品兑换后由平台安排物流发货。</p></article></main><div className="single-bottom-action"><button onClick={()=>navigate('app/points-confirm')}>立即兑换</button></div></div>;
}

function AppPointsConfirm({ navigate }) {
  return <div className="mobile-screen" data-screen-label="App 确认兑换"><SimpleMobileHeader title="确认兑换" navigate={navigate} back="app/points-product"/><main className="mobile-scroll confirm-scroll"><button className="address-panel" onClick={()=>navigate('app/addresses')}><span className="address-icon"><Icon name="store"/></span><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div><Icon name="chevron"/></button><section className="confirm-shop-card"><div className="confirm-product"><ProductArt type="coffee"/><div><b>品牌帆布袋</b><span>标准款</span><div><strong>600 积分</strong><small>×1</small></div></div></div></section><section className="price-summary"><div><span>当前积分</span><b>1,280</b></div><div><span>兑换积分</span><b className="saving">600</b></div><div className="summary-total"><span>兑换后剩余</span><strong>680</strong></div></section></main><div className="single-bottom-action"><button onClick={()=>navigate('app/points-order')}>确认兑换</button></div></div>;
}

function AppPointsOrders({ navigate }) {
  const rows=[['PX20261027001','品牌帆布袋','600','待发货','coffee'],['PX20261020008','全店 20 元券','300','已完成','serum']];
  return <div className="mobile-screen" data-screen-label="App 积分订单"><SimpleMobileHeader title="兑换记录" navigate={navigate} back="app/points"/><main className="mobile-scroll mobile-order-list">{rows.map((row)=><article className="mobile-order-card" key={row[0]}><header><span className="mono">{row[0]}</span><StatusTicket tone={row[3]==='已完成'?'success':'warning'}>{row[3]}</StatusTicket></header><button className="mobile-order-product" onClick={()=>navigate('app/points-order')}><ProductArt type={row[4]}/><div><b>{row[1]}</b><span>数量 × 1</span><strong>{row[2]} 积分</strong></div></button><footer><span>2026-10-27 10:18</span><div><button onClick={()=>navigate('app/points-order')}>查看详情</button></div></footer></article>)}</main></div>;
}

function AppPointsOrder({ navigate }) {
  return <div className="mobile-screen" data-screen-label="App 积分订单详情"><SimpleMobileHeader title="积分订单详情" navigate={navigate} back="app/points-orders"/><main className="mobile-scroll order-detail-scroll"><section className="order-state-panel"><span className="state-icon"><Icon name="box" size={27}/></span><div><h2>等待平台发货</h2><p>兑换商品正在准备中</p></div></section><section className="order-address"><Icon name="store"/><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div></section><section className="order-goods"><div className="confirm-product"><ProductArt type="coffee"/><div><b>品牌帆布袋</b><span>标准款 × 1</span><div><strong>600 积分</strong></div></div></div></section><section className="order-info-list"><div><span>积分订单号</span><b>PX20261027001</b></div><div><span>兑换积分</span><b>600</b></div><div><span>兑换时间</span><b>2026-10-27 10:18</b></div></section></main></div>;
}

function AppCampaignDetail({ navigate, route, type }) {
  const seckill = type === 'seckill';
  const selectedGroup = routeParam(route,'group');
  const [quantity,setQuantity] = useState(1);
  return <div className="mobile-screen campaign-detail-page" data-screen-label={`App ${seckill?'秒杀':'拼团'}商品详情`}><SimpleMobileHeader title={seckill?'秒杀商品':'拼团商品'} navigate={navigate} back={seckill?'app/seckill-products':'app/groupbuy-products'}/><main className="mobile-scroll"><div className="campaign-art"><ProductArt type={seckill?'headphone':'serum'}/><StatusTicket tone="danger">{seckill?'距结束 01:42:18':'双人成团'}</StatusTicket></div><section className="campaign-copy"><div><strong>¥{seckill?'299':'139'}</strong><del>¥{seckill?'399':'168'}</del></div><h1>{seckill?'云感降噪头戴耳机':'白茶屏障修护精华'}</h1><p>{seckill?'秒杀库存 18 件，每人限购 1 件':'活动结束后仅成团订单可由商家安排发货'}</p></section>{!seckill&&<button className="group-choice" onClick={()=>navigate('app/group-select')}><span><small>拼团方式</small><b>{selectedGroup?`已选择 ${selectedGroup} 号团`:'请选择现有团或发起新团'}</b></span><Icon name="chevron" size={17}/></button>}<section className="campaign-rule"><h3>活动规则</h3><p>{seckill?'同一账号每场限购 1 件，库存售完即止。':'每位用户在同一活动中仅可参与一个团；待支付即占席，未满团不能发货。'}</p></section><div className="quantity-row campaign-quantity"><label>购买数量</label><QuantityControl value={quantity} setValue={setQuantity} max={seckill?1:1}/></div></main><div className="single-bottom-action"><button disabled={!seckill&&!selectedGroup} onClick={()=>navigate('app/confirm')}>{seckill?'立即抢购':selectedGroup?'按所选团购买':'先选择拼团方式'}</button></div></div>;
}

function AppGroupSelect({ navigate }) {
  const [selected,setSelected] = useState('2');
  const groups = [{id:'1',people:'2/2',status:'已满员',owner:'林可'},{id:'2',people:'1/2',status:'可参团',owner:'周予安'},{id:'3',people:'0/2',status:'新团',owner:'等待团长'}];
  return <div className="mobile-screen" data-screen-label="App 选择拼团"><SimpleMobileHeader title="选择拼团" navigate={navigate} back="app/groupbuy-detail"/><main className="mobile-scroll group-select-page"><section className="group-summary"><b>双人成团</b><span>当前 3 个团 · 1 个可直接加入</span></section><button className="random-group" onClick={()=>setSelected('2')}><Icon name="spark" size={18}/><span><b>加入随机可用团</b><small>避免所选团被其他用户抢先占满</small></span><i>{selected==='2'&&<Icon name="check" size={13}/>}</i></button>{groups.map((group)=><button disabled={group.status==='已满员'} className={`group-option ${selected===group.id?'active':''}`} key={group.id} onClick={()=>setSelected(group.id)}><Avatar name={group.owner}/><span><b>{group.id} 号团 · {group.owner}</b><small>{group.status==='新团'?'由你成为团长并发起新团':'已占席人数'}</small></span><strong>{group.people}</strong><i>{selected===group.id&&<Icon name="check" size={13}/>}</i></button>)}</main><div className="single-bottom-action"><button onClick={()=>navigate(`app/groupbuy-detail?group=${selected}`)}>确定选择</button></div></div>;
}

function AppAddressEdit({ navigate, route, notify }) {
  const create = routeParam(route,'mode') === 'create';
  const [name,setName] = useState(create?'':'林可'); const [mobile,setMobile] = useState(create?'':'138****7621'); const [address,setAddress] = useState(create?'':'浙江省杭州市西湖区文三路 88 号 3 幢 502');
  return <div className="mobile-screen" data-screen-label={`App ${create?'新增':'编辑'}地址`}><SimpleMobileHeader title={create?'新增地址':'编辑地址'} navigate={navigate} back="app/addresses"/><main className="mobile-scroll form-page address-edit-page"><label><span>收货人</span><input value={name} onChange={(e)=>setName(e.target.value)} placeholder="请输入收货人姓名"/></label><label><span>手机号</span><input value={mobile} onChange={(e)=>setMobile(e.target.value)} placeholder="请输入手机号"/></label><label><span>所在地区</span><select defaultValue="浙江省 / 杭州市 / 西湖区"><option>浙江省 / 杭州市 / 西湖区</option><option>上海市 / 徐汇区</option></select></label><label><span>详细地址</span><textarea value={address} onChange={(e)=>setAddress(e.target.value)} placeholder="街道、楼牌号等"></textarea></label><label className="switch-row"><span>设为默认地址</span><input type="checkbox" defaultChecked={create}/></label><button className="form-submit" onClick={()=>{if(!name||!mobile||!address)return notify('请完整填写地址信息');notify('地址已保存');navigate('app/addresses');}}>保存地址</button></main></div>;
}

function AppRefundDetail({ navigate }) {
  const steps=[['退款申请已提交','10月27日 11:06'],['等待商家处理','预计 24 小时内'],['退款原路退回','审核通过后 1–3 个工作日']];
  return <div className="mobile-screen" data-screen-label="App 退款详情"><SimpleMobileHeader title="退款详情" navigate={navigate} back="app/order"/><main className="mobile-scroll refund-detail-page"><section><StatusTicket tone="warning">处理中</StatusTicket><h1>等待商家审核</h1><p>商家将在 24 小时内处理本次仅退款申请。</p></section><div className="refund-amount"><span>退款金额</span><b>¥168.00</b></div><article>{steps.map((step,index)=><div className={index===0?'active':''} key={step[0]}><i></i><span><b>{step[0]}</b><small>{step[1]}</small></span></div>)}</article><div className="order-info-list"><div><span>退款原因</span><b>商品与描述不符</b></div><div><span>申请时间</span><b>2026-10-27 11:06</b></div><div><span>退款编号</span><b className="mono">RF202610270016</b></div></div></main></div>;
}

function AppPreferencePage({ navigate, type }) {
  const notification = type==='notifications';
  const [values,setValues] = useState({order:true,promo:false,service:true,personalized:false});
  const rows = notification ? [['order','订单状态通知'],['promo','营销活动通知'],['service','客服消息通知']] : [['personalized','个性化商品推荐'],['order','订单信息用于履约'],['service','客服会话记录']];
  return <div className="mobile-screen" data-screen-label={`App ${notification?'消息通知':'隐私设置'}`}><SimpleMobileHeader title={notification?'消息通知':'隐私设置'} navigate={navigate} back="app/settings"/><main className="mobile-scroll preference-page">{rows.map(([key,label])=><label key={key}><span><b>{label}</b><small>{notification?'控制该类消息是否提醒':'管理该类信息的使用方式'}</small></span><input type="checkbox" checked={!!values[key]} onChange={(e)=>setValues({...values,[key]:e.target.checked})}/></label>)}{!notification&&<section><h3>数据说明</h3><p>关闭个性化推荐不会影响基本购物、订单履约与售后服务。</p></section>}</main></div>;
}

function AppAuth({ navigate, mode, notify }) {
  const register = mode==='register'; const [mobile,setMobile]=useState(''); const [code,setCode]=useState('');
  return <div className="mobile-screen auth-page" data-screen-label={`App ${register?'注册':'登录'}`}><MobileStatusBar/><div className="auth-capsule"><WechatCapsule/></div><main><span className="auth-mark">Y</span><h1>{register?'创建会员账号':'欢迎回来'}</h1><p>{register?'注册后即可使用购物车、优惠券和积分商城':'登录后继续浏览与管理订单'}</p><label><span>手机号</span><input value={mobile} onChange={(e)=>setMobile(e.target.value)} placeholder="请输入手机号"/></label><label><span>验证码</span><div><input value={code} onChange={(e)=>setCode(e.target.value)} placeholder="请输入验证码"/><button onClick={()=>notify('验证码已发送')}>获取验证码</button></div></label><button className="auth-submit" onClick={()=>mobile&&code?navigate('app/home'):notify('请填写手机号和验证码')}>{register?'注册并登录':'登录'}</button><button className="auth-switch" onClick={()=>navigate(register?'app/login':'app/register')}>{register?'已有账号，去登录':'没有账号，立即注册'}</button></main></div>;
}

function AppPrototype({ route, navigate, notify }) {
  const page = routePath(route).split('/')[1] || 'home';
  return (
    <MobileFrame>
      {page === 'home' && <AppHome navigate={navigate}/>}
      {page === 'category' && <AppCategory navigate={navigate}/>}
      {page === 'products' && <AppProductList navigate={navigate}/>}
      {page === 'product' && <AppProduct navigate={navigate} notify={notify}/>}
      {page === 'cart' && <AppCart navigate={navigate} notify={notify}/>}
      {page === 'confirm' && <AppConfirm navigate={navigate} notify={notify}/>}
      {page === 'orders' && <AppOrders navigate={navigate} route={route} notify={notify}/>}
      {page === 'order' && <AppOrder navigate={navigate} notify={notify}/>}
      {page === 'profile' && <AppProfile navigate={navigate}/>}
      {page === 'favorites' && <AppCollection navigate={navigate} mode="favorites"/>}
      {page === 'history' && <AppCollection navigate={navigate} mode="history"/>}
      {page === 'coupons' && <AppCoupons navigate={navigate} notify={notify}/>}
      {page === 'coupon-center' && <AppCouponCenter navigate={navigate} notify={notify}/>}
      {page === 'points' && <AppPoints navigate={navigate}/>}
      {page === 'points-mall' && <AppPointsMall navigate={navigate}/>}
      {page === 'seckill' && <AppActivity navigate={navigate} type="seckill"/>}
      {page === 'groupbuy' && <AppActivity navigate={navigate} type="groupbuy"/>}
      {page === 'service' && <AppService navigate={navigate} notify={notify}/>}
      {page === 'addresses' && <AppAddresses navigate={navigate} notify={notify}/>}
      {page === 'reviews' && <AppReviews navigate={navigate}/>}
      {page === 'feedback' && <AppFeedback navigate={navigate} notify={notify}/>}
      {page === 'settings' && <AppSettings navigate={navigate} notify={notify}/>}
      {page === 'shops' && <AppShops navigate={navigate}/>}
      {page === 'shop' && <AppShop navigate={navigate}/>}
      {page === 'pay-result' && <AppPayResult navigate={navigate}/>}
      {page === 'logistics' && <AppLogistics navigate={navigate}/>}
      {page === 'refund' && <AppRefund navigate={navigate} notify={notify}/>}
      {page === 'review-form' && <AppReviewForm navigate={navigate} notify={notify}/>}
      {page === 'review-detail' && <AppReviewDetail navigate={navigate} notify={notify}/>}
      {page === 'profile-edit' && <AppProfileEdit navigate={navigate} notify={notify}/>}
      {page === 'about' && <AppAbout navigate={navigate}/>}
      {page === 'seckill-products' && <AppActivityProducts navigate={navigate} type="seckill"/>}
      {page === 'groupbuy-products' && <AppActivityProducts navigate={navigate} type="groupbuy"/>}
      {page === 'coupon-select' && <AppCouponSelect navigate={navigate}/>}
      {page === 'points-product' && <AppPointsProduct navigate={navigate}/>}
      {page === 'points-confirm' && <AppPointsConfirm navigate={navigate}/>}
      {page === 'points-orders' && <AppPointsOrders navigate={navigate}/>}
      {page === 'points-order' && <AppPointsOrder navigate={navigate}/>}
      {page === 'seckill-detail' && <AppCampaignDetail navigate={navigate} route={route} type="seckill"/>}
      {page === 'groupbuy-detail' && <AppCampaignDetail navigate={navigate} route={route} type="groupbuy"/>}
      {page === 'group-select' && <AppGroupSelect navigate={navigate}/>}
      {page === 'address-edit' && <AppAddressEdit navigate={navigate} route={route} notify={notify}/>}
      {page === 'refund-detail' && <AppRefundDetail navigate={navigate}/>}
      {page === 'notifications' && <AppPreferencePage navigate={navigate} type="notifications"/>}
      {page === 'privacy' && <AppPreferencePage navigate={navigate} type="privacy"/>}
      {page === 'login' && <AppAuth navigate={navigate} mode="login" notify={notify}/>}
      {page === 'register' && <AppAuth navigate={navigate} mode="register" notify={notify}/>}
    </MobileFrame>
  );
}

function AdminSidebar({ surface, route, navigate }) {
  const [expanded,setExpanded] = useState(surface==='platform'?{'商城管理':true,'秒杀管理':false,'拼团管理':false,'系统管理':false}:{'商品管理':true,'交易服务':true,'优惠券管理':false,'秒杀管理':false,'拼团管理':false,'店铺管理':false});
  const platformSections = [
    { title:'商城管理', items:[['users','platform/merchants','商家管理'],['store','platform/shops','店铺管理'],['members','platform/members','会员管理'],['message','platform/reviews','评价管理'],['points','platform/points','积分管理'],['box','platform/points-mall','积分商城'],['spark','platform/content','首页运营'],['message','platform/feedback','意见反馈']] },
    { title:'秒杀管理', items:[['bolt','platform/seckill?tab=活动管理','活动管理'],['clock','platform/seckill?tab=场次管理','场次管理'],['check','platform/seckill?tab=报名审核','报名审核'],['box','platform/seckill?tab=商品管理','商品管理'],['eye','platform/seckill?tab=实时监控','实时监控'],['grid','platform/seckill?tab=成交分析','成交分析']] },
    { title:'拼团管理', items:[['users','platform/groupbuy?tab=活动管理','活动管理'],['check','platform/groupbuy?tab=申请审核','申请审核'],['box','platform/groupbuy?tab=商品管理','商品管理'],['order','platform/groupbuy?tab=订单管理','订单管理']] },
    { title:'系统管理', framework:true, items:[['user','platform/system?view=user','用户管理'],['users','platform/system?view=role','角色管理'],['grid','platform/system?view=resource','资源管理'],['members','platform/system?view=org','组织管理'],['user','platform/system?view=position','岗位管理'],['order','platform/system?view=dict','字典管理'],['settings','platform/system?view=config','参数设置'],['message','platform/system?view=log','日志管理'],['store','platform/system?view=area','地区管理'],['box','platform/system?view=file','文件管理'],['store','platform/system?view=storage','存储管理']] }
  ];
  const merchantSections = [
    { title:'商品管理', items:[['box','merchant/products','SPU 管理'],['grid','merchant/categories','商品分类'],['settings','merchant/specs','规格管理']] },
    { title:'交易服务', items:[['order','merchant/orders','订单管理'],['message','merchant/reviews','评价管理'],['message','merchant/service','客服消息']] },
    { title:'优惠券管理', items:[['coupon','merchant/coupons?tab=优惠券','优惠券'],['members','merchant/coupons?tab=领取记录','领取记录'],['order','merchant/coupons?tab=使用记录','使用记录']] },
    { title:'秒杀管理', items:[['bolt','merchant/seckill?tab=活动列表','活动列表'],['check','merchant/seckill?tab=报名记录','报名记录'],['box','merchant/seckill?tab=秒杀商品','秒杀商品']] },
    { title:'拼团管理', items:[['users','merchant/groupbuy?tab=活动列表','活动列表'],['check','merchant/groupbuy?tab=申请记录','申请记录'],['box','merchant/groupbuy?tab=拼团商品','拼团商品'],['order','merchant/groupbuy?tab=拼团订单','拼团订单']] },
    { title:'店铺管理', items:[['spark','merchant/banner','轮播图'],['truck','merchant/shipping','物流设置'],['settings','merchant/settings','店铺设置']] }
  ];
  const sections = surface==='platform'?platformSections:merchantSections;
  const current = routePath(route);
  const active = (target) => target.includes('?') ? route === target : current === target;
  useEffect(()=>setExpanded(surface==='platform'?{'商城管理':true,'秒杀管理':false,'拼团管理':false,'系统管理':false}:{'商品管理':true,'交易服务':true,'优惠券管理':false,'秒杀管理':false,'拼团管理':false,'店铺管理':false}),[surface]);
  useEffect(()=>{
    const section = sections.find((item)=>item.items.some((menu)=>active(menu[1])));
    if(section) setExpanded(Object.fromEntries(sections.map((item)=>[item.title,item.title===section.title])));
  },[route,surface]);
  return (
    <aside className="admin-sidebar">
      <div className="admin-logo"><span className="brand-stamp">Y</span><div><b>Yeah Mall</b><small>{surface === 'platform' ? '平台运营端' : '商家工作台'}</small></div></div>
      <nav className="admin-menu-tree">
        <button className={current===`${surface}/overview`?'active sidebar-overview':'sidebar-overview'} onClick={()=>navigate(`${surface}/overview`)}><Icon name="grid" size={19}/><span>经营概览</span>{current===`${surface}/overview`&&<i></i>}</button>
        {sections.map((section)=><section className="sidebar-menu-group" key={section.title}><button className="sidebar-group-toggle" onClick={()=>setExpanded({...expanded,[section.title]:!expanded[section.title]})}><span>{section.title}</span>{section.framework&&<small>沿用</small>}<Icon name="down" size={15} className={expanded[section.title]?'is-open':''}/></button>{expanded[section.title]&&<div className="sidebar-group-items">{section.items.map(([icon,target,label])=><button key={target} className={active(target)?'active':''} onClick={()=>navigate(target)}><Icon name={icon} size={17}/><span>{label}</span>{active(target)&&<i></i>}</button>)}</div>}</section>)}
      </nav>
      <div className="sidebar-shop"><Avatar name={surface === 'platform' ? '管' : '野'} size={34}/><div><b>{surface === 'platform' ? '平台管理员' : '山野生活店'}</b><small>{surface === 'platform' ? 'admin' : 'mch001'}</small></div><Icon name="more" size={18}/></div>
    </aside>
  );
}

function AdminTopbar({ surface, page, route, notify }) {
  const names = { overview:'经营概览', merchants:'商家管理', shops:'店铺管理', members:'会员管理', order:'订单管理', reviews:'评价管理', points:'积分管理', 'points-mall':'积分商城', seckill:'秒杀管理', groupbuy:'拼团管理', content:'首页运营', feedback:'意见反馈', products:'SPU 管理', sku:'SKU 管理', categories:'商品分类', specs:'规格管理', banner:'轮播图', orders:'订单管理', coupons:'优惠券管理', shipping:'物流设置', service:'客服消息', settings:'店铺设置', system:'系统管理' };
  const systemNames = {user:'用户管理',org:'组织管理',position:'岗位管理',role:'角色管理',resource:'资源管理',dict:'字典管理',config:'参数设置',file:'文件管理',storage:'存储管理',log:'日志管理',area:'地区管理'};
  const subName = routeParam(route,'tab') || systemNames[routeParam(route,'view')];
  return (
    <header className="admin-topbar"><div><span>{surface === 'platform' ? '平台后台' : '商家后台'}</span><Icon name="chevron" size={14}/><b>{names[page] || '商城管理'}</b>{subName&&<><Icon name="chevron" size={14}/><b>{subName}</b></>}</div><div><button onClick={()=>notify('全局搜索已聚焦当前业务模块')}><Icon name="search"/></button><button className="notification-button" onClick={()=>notify('有 3 条新的业务待办')}><Icon name="message"/><i></i></button><button className="topbar-avatar" onClick={()=>notify('账号菜单已展开')}><Avatar name={surface === 'platform' ? '管' : '野'} size={32}/></button></div></header>
  );
}

function AdminPagination({ count = 46 }) {
  const [size,setSize] = useState(10);
  const [page,setPage] = useState(1);
  const pages = Math.max(1, Math.ceil(count / size));
  useEffect(()=>{ if(page>pages) setPage(pages); },[pages,page]);
  return <div className="admin-pagination"><span>第 {page}/{pages} 页</span><button disabled={page===1} onClick={()=>setPage(Math.max(1,page-1))}><Icon name="back" size={16}/></button>{Array.from({length:pages},(_,index)=>index+1).slice(0,5).map((item)=><button key={item} className={page===item?'active':''} onClick={()=>setPage(item)}>{item}</button>)}<button disabled={page===pages} onClick={()=>setPage(Math.min(pages,page+1))}><Icon name="chevron" size={16}/></button><select value={size} onChange={(e)=>{setSize(Number(e.target.value));setPage(1);}}><option value="10">10 条/页</option><option value="20">20 条/页</option></select></div>;
}

function FilterBar({ children, onReset, onSearch, actions }) {
  return <div className="admin-filter-bar"><div className="filter-query-group"><div className="filter-fields">{children}</div><div className="filter-actions"><button onClick={onReset}>重置</button><button className="primary" onClick={onSearch}><Icon name="search" size={16}/>查询</button></div></div>{actions&&<div className="filter-business-actions"><i className="filter-action-divider"></i>{actions}</div>}</div>;
}

function AdminMembers({ members, setMembers, notify }) {
  const [keyword, setKeyword] = useState('');
  const [filtered, setFiltered] = useState(members);
  const [drawer, setDrawer] = useState(null);
  const [menu, setMenu] = useState(null);
  const [adjust, setAdjust] = useState(null);
  const [amount, setAmount] = useState(100);
  const [remark, setRemark] = useState('');
  useEffect(() => setFiltered(members), [members]);
  const search = () => setFiltered(members.filter((item) => `${item.id}${item.nickname}${item.account}${item.mobile}`.includes(keyword.trim())));
  const saveAdjust = () => {
    const delta = adjust.type === 'increase' ? Number(amount) : -Number(amount);
    if (!amount || amount < 1) return notify('调整积分必须大于 0');
    if (adjust.member.points + delta < 0) return notify('可用积分不足，不能扣减');
    const next = members.map((item) => item.id === adjust.member.id ? { ...item, points: item.points + delta, earned: delta > 0 ? item.earned + delta : item.earned, used: delta < 0 ? item.used + Math.abs(delta) : item.used } : item);
    setMembers(next); setAdjust(null); setRemark(''); notify(`已${delta > 0 ? '增加' : '扣减'} ${Math.abs(delta)} 积分`);
  };
  return (
    <section className="admin-page" data-screen-label="平台后台会员管理">
      <FilterBar onReset={() => { setKeyword(''); setFiltered(members); }} onSearch={search} actions={<button onClick={()=>notify('当前会员结果已导出')}><Icon name="filter" size={17}/>导出结果</button>}>
        <label><span>会员关键词</span><input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="昵称、账号或手机号" onKeyDown={(e) => e.key === 'Enter' && search()}/></label>
        <label><span>积分范围</span><select defaultValue="all"><option value="all">全部积分</option><option>0 积分</option><option>1–999</option><option>1000 以上</option></select></label>
        <label><span>注册时间</span><input type="date" defaultValue="2026-10-01"/></label>
      </FilterBar>
      <div className="admin-table-frame">
        <table className="admin-table"><thead><tr><th>会员</th><th>账号</th><th>手机号</th><th className="numeric">可用积分</th><th className="numeric">累计获得</th><th>注册时间</th><th className="actions">操作</th></tr></thead>
          <tbody>{filtered.map((member) => <tr key={member.id}><td><div className="member-table-cell"><Avatar name={member.nickname} tone={member.tone}/><div><b>{member.nickname}</b><small>ID {member.id}</small></div></div></td><td>{member.account}</td><td className="mono">{member.mobile}</td><td className="numeric points-number">{member.points.toLocaleString()}</td><td className="numeric">{member.earned.toLocaleString()}</td><td><span>{member.createdAt.slice(0,10)}</span><small className="cell-sub">{member.createdAt.slice(11)}</small></td><td className="actions"><button className="table-link" onClick={() => setDrawer(member)}>查看</button><span className="row-menu-wrap"><button className="table-more" onClick={() => setMenu(menu === member.id ? null : member.id)}>更多 <Icon name="down" size={14}/></button>{menu === member.id && <span className="row-menu"><button onClick={() => { setAdjust({ type:'increase', member }); setMenu(null); }}>增加积分</button><button onClick={() => { setAdjust({ type:'decrease', member }); setMenu(null); }}>扣减积分</button><button onClick={() => { setMenu(null); notify(`已筛选 ${member.nickname} 的积分流水`); }}>积分流水</button></span>}</span></td></tr>)}</tbody>
        </table>
        {!filtered.length && <div className="admin-empty"><Icon name="members" size={30}/><b>没有匹配的会员</b><span>修改筛选条件后重试</span></div>}
        <AdminPagination count={filtered.length || 0}/>
      </div>
      {drawer && <div className="drawer-mask" onClick={() => setDrawer(null)}><aside className="admin-drawer" onClick={(e) => e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">MEMBER PROFILE</span><h2>会员详情</h2></div><button onClick={() => setDrawer(null)}>×</button></div><div className="profile-summary"><Avatar name={drawer.nickname} tone={drawer.tone} size={54}/><div><h3>{drawer.nickname}</h3><span>{drawer.account} · {drawer.mobile}</span></div><StatusTicket tone="success">正常</StatusTicket></div><div className="profile-metrics"><div><span>可用积分</span><b>{drawer.points.toLocaleString()}</b></div><div><span>累计获得</span><b>{drawer.earned.toLocaleString()}</b></div><div><span>累计使用</span><b>{drawer.used.toLocaleString()}</b></div></div><div className="description-grid">{[['会员ID',drawer.id],['性别',drawer.gender],['生日',drawer.birthday],['注册时间',drawer.createdAt],['最近更新','2026-10-26 18:42:10']].map(([k,v]) => <div key={k}><span>{k}</span><b>{v}</b></div>)}</div><div className="drawer-actions"><button onClick={() => { setAdjust({type:'increase',member:drawer}); setDrawer(null); }}>调整积分</button><button className="primary" onClick={() => notify('已打开会员积分流水')}>查看积分流水</button></div></aside></div>}
      {adjust && <div className="dialog-mask" onClick={() => setAdjust(null)}><section className="admin-dialog points-dialog" onClick={(e) => e.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">POINTS ADJUSTMENT</span><h2>{adjust.type === 'increase' ? '增加积分' : '扣减积分'}</h2></div><button onClick={() => setAdjust(null)}>×</button></div><div className="dialog-member"><Avatar name={adjust.member.nickname} tone={adjust.member.tone}/><div><b>{adjust.member.nickname}</b><span>当前可用积分 {adjust.member.points.toLocaleString()}</span></div></div><label className="dialog-field"><span>调整积分</span><input type="number" min="1" value={amount} onChange={(e) => setAmount(Number(e.target.value))}/></label><div className="balance-preview"><span>调整后积分</span><b>{Math.max(0, adjust.member.points + (adjust.type === 'increase' ? Number(amount) : -Number(amount))).toLocaleString()}</b></div><label className="dialog-field"><span>备注 <small>选填</small></span><textarea value={remark} onChange={(e) => setRemark(e.target.value)} placeholder="说明本次调整用途"></textarea></label><div className="dialog-actions"><button onClick={() => setAdjust(null)}>取消</button><button className="primary" onClick={saveAdjust}>确认调整</button></div></section></div>}
    </section>
  );
}

function statusTone(status) {
  return ({ '待发货':'warning','待收货':'info','待成团':'neutral','退款中':'danger','已完成':'success' })[status] || 'neutral';
}

function AdminOrders({ orders, setOrders, notify }) {
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState('全部状态');
  const [detail, setDetail] = useState(null);
  const [ship, setShip] = useState(null);
  const [provider, setProvider] = useState(MOCKS.providers[0].code);
  const [tracking, setTracking] = useState('');
  const filtered = orders.filter((item) => (status === '全部状态' || item.status === status) && `${item.orderNo}${item.buyer}`.includes(keyword));
  const submitShip = () => {
    if (!tracking.trim()) return notify('请输入物流单号');
    const providerName = MOCKS.providers.find((item) => item.code === provider)?.name;
    setOrders(orders.map((item) => item.id === ship.id ? { ...item, status:'待收货', provider:providerName, trackingNo:tracking } : item));
    setShip(null); setTracking(''); notify('订单已发货');
  };
  return (
    <section className="admin-page" data-screen-label="后台订单管理">
      <FilterBar onReset={() => {setKeyword('');setStatus('全部状态');}} onSearch={() => notify(`已筛选 ${filtered.length} 笔订单`)} actions={<button onClick={()=>notify('订单导出任务已创建')}><Icon name="filter" size={17}/>导出订单</button>}>
        <label><span>订单编号</span><input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="订单编号或买家"/></label>
        <label><span>订单状态</span><select value={status} onChange={(e) => setStatus(e.target.value)}>{['全部状态','待发货','待收货','待成团','退款中'].map((item) => <option key={item}>{item}</option>)}</select></label>
        <label><span>订单类型</span><select defaultValue="全部类型"><option>全部类型</option><option>普通订单</option><option>秒杀订单</option><option>拼团订单</option></select></label>
        <label><span>下单时间</span><input type="date" defaultValue="2026-10-27"/></label>
      </FilterBar>
      <div className="admin-table-frame">
        <table className="admin-table orders-table"><thead><tr><th>订单编号 / 时间</th><th>买家</th><th>订单类型</th><th className="numeric">商品总额</th><th className="numeric">优惠</th><th className="numeric">应付</th><th>状态</th><th className="actions">操作</th></tr></thead><tbody>{filtered.map((order) => <tr key={order.id}><td><b className="mono order-number">{order.orderNo}</b><small className="cell-sub">{order.createdAt}</small></td><td><b>{order.buyer}</b><small className="cell-sub mono">{order.mobile}</small></td><td><StatusTicket tone={order.type === '秒杀订单' ? 'danger' : order.type === '拼团订单' ? 'success' : 'neutral'} small>{order.type}</StatusTicket></td><td className="numeric">¥{order.amount.toFixed(2)}</td><td className="numeric saving">-¥{order.discount.toFixed(2)}</td><td className="numeric amount-strong">¥{order.finalAmount.toFixed(2)}</td><td><StatusTicket tone={statusTone(order.status)}>{order.status}</StatusTicket></td><td className="actions"><button className="table-link" onClick={() => setDetail(order)}>详情</button>{order.status === '待发货' && <button className="table-primary" onClick={() => setShip(order)}>发货</button>}<button className="table-more" onClick={()=>notify('订单操作菜单已展开')}><Icon name="more" size={18}/></button></td></tr>)}</tbody></table>
        <AdminPagination count={filtered.length}/>
      </div>
      {detail && <div className="drawer-mask" onClick={() => setDetail(null)}><aside className="admin-drawer order-drawer" onClick={(e) => e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">ORDER DETAIL</span><h2>订单详情</h2></div><button onClick={() => setDetail(null)}>×</button></div><div className="order-detail-heading"><div><span>订单编号</span><b className="mono">{detail.orderNo}</b></div><StatusTicket tone={statusTone(detail.status)}>{detail.status}</StatusTicket></div><div className="drawer-section"><h3>商品信息</h3><div className="drawer-product"><ProductArt type={detail.product.includes('耳机') ? 'headphone' : detail.product.includes('精华') ? 'serum' : detail.product.includes('咖啡') ? 'coffee' : 'chair'}/><div><b>{detail.product}</b><span>数量 × {detail.quantity}</span><strong>¥{detail.finalAmount.toFixed(2)}</strong></div></div></div><div className="drawer-section"><h3>收货信息</h3><div className="description-grid"><div><span>收货人</span><b>{detail.buyer} {detail.mobile}</b></div><div className="wide"><span>收货地址</span><b>{detail.address}</b></div></div></div><div className="drawer-section"><h3>金额信息</h3><div className="order-money"><span>商品总额<b>¥{detail.amount.toFixed(2)}</b></span><span>优惠金额<b>-¥{detail.discount.toFixed(2)}</b></span><span className="total">优惠后应付<b>¥{detail.finalAmount.toFixed(2)}</b></span></div></div></aside></div>}
      {ship && <div className="dialog-mask" onClick={() => setShip(null)}><section className="admin-dialog" onClick={(e) => e.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">FULFILLMENT</span><h2>订单发货</h2></div><button onClick={() => setShip(null)}>×</button></div><div className="shipping-order"><span>订单编号</span><b className="mono">{ship.orderNo}</b></div><label className="dialog-field"><span>物流公司</span><select value={provider} onChange={(e) => setProvider(e.target.value)}>{MOCKS.providers.map((item) => <option key={item.code} value={item.code}>{item.name} · {item.code}</option>)}</select></label><label className="dialog-field"><span>物流单号</span><input value={tracking} onChange={(e) => setTracking(e.target.value)} placeholder="请输入物流单号"/></label><div className="dialog-hint"><Icon name="truck" size={17}/><span>发货后买家将看到物流公司与单号。</span></div><div className="dialog-actions"><button onClick={() => setShip(null)}>取消</button><button className="primary" onClick={submitShip}>确认发货</button></div></section></div>}
    </section>
  );
}

function MerchantProducts({ notify }) {
  const [keyword, setKeyword] = useState('');
  const [products, setProducts] = useState(MOCKS.merchantProducts);
  const [edit, setEdit] = useState(null);
  const filtered = products.filter((item) => item.name.includes(keyword));
  const toggle = (product) => {
    const next = product.status === '销售中' ? '已下架' : product.stock === 0 ? '已售罄' : '销售中';
    setProducts(products.map((item) => item.id === product.id ? { ...item, status:next } : item));
    notify(`商品已${next === '销售中' ? '上架' : '下架'}`);
  };
  return (
    <section className="admin-page" data-screen-label="商家后台商品管理">
      <FilterBar onReset={() => setKeyword('')} onSearch={() => notify(`找到 ${filtered.length} 个商品`)} actions={<><button onClick={()=>notify('请选择商品后执行批量操作')}><Icon name="filter" size={17}/>批量操作</button><button className="business-primary" onClick={() => setEdit({id:null,name:'',category:'数码影音',skuCount:1,price:'',stock:0,art:'headphone'})}><Icon name="plus" size={17}/>新增商品</button></>}><label><span>商品名称</span><input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="输入商品名称"/></label><label><span>商品分类</span><select defaultValue="全部分类"><option>全部分类</option><option>数码影音</option><option>家具家居</option><option>美容护肤</option></select></label><label><span>销售状态</span><select defaultValue="全部状态"><option>全部状态</option><option>销售中</option><option>已下架</option><option>已售罄</option></select></label></FilterBar>
      <div className="admin-table-frame"><table className="admin-table product-admin-table"><thead><tr><th>商品</th><th>分类</th><th className="numeric">售价</th><th className="numeric">库存</th><th className="numeric">销量</th><th>状态</th><th className="actions">操作</th></tr></thead><tbody>{filtered.map((product) => <tr key={product.id}><td><div className="admin-product-cell"><ProductArt type={product.art}/><div><b>{product.name}</b><small>{product.skuCount} 个 SKU · ID {product.id}</small></div></div></td><td>{product.category}</td><td className="numeric amount-strong">¥{product.price}</td><td className={`numeric ${product.stock <= 12 ? 'low-stock' : ''}`}>{product.stock}</td><td className="numeric">{product.sales.toLocaleString()}</td><td><StatusTicket tone={product.status === '销售中' ? 'success' : product.status === '已售罄' ? 'danger' : 'neutral'}>{product.status}</StatusTicket></td><td className="actions"><button className="table-link" onClick={() => setEdit(product)}>编辑</button><button className="table-link" onClick={() => toggle(product)}>{product.status === '销售中' ? '下架' : '上架'}</button><button className="table-more" onClick={()=>notify('商品操作菜单已展开')}><Icon name="more" size={18}/></button></td></tr>)}</tbody></table><AdminPagination count={filtered.length}/></div>
      {edit && <div className="drawer-mask" onClick={() => setEdit(null)}><aside className="admin-drawer product-edit-drawer" onClick={(e) => e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">PRODUCT EDITOR</span><h2>{edit.id?'编辑商品':'新增商品'}</h2></div><button onClick={() => setEdit(null)}>×</button></div><div className="edit-product-preview"><ProductArt type={edit.art}/><div><b>{edit.name||'待填写商品名称'}</b><span>{edit.category} · {edit.skuCount} 个 SKU</span></div></div><div className="edit-form-grid"><label><span>商品名称</span><input defaultValue={edit.name} placeholder="请输入商品名称"/></label><label><span>商品分类</span><select defaultValue={edit.category}><option>数码影音</option><option>家具家居</option><option>美容护肤</option></select></label><label><span>默认售价</span><input defaultValue={edit.price} placeholder="0.00"/></label><label><span>当前库存</span><input defaultValue={edit.stock}/></label><label className="wide"><span>商品卖点</span><textarea defaultValue={edit.id?'轻量设计，稳定连接，适合通勤与居家使用。':''} placeholder="概括商品核心卖点"></textarea></label></div><div className="drawer-actions"><button onClick={() => setEdit(null)}>取消</button><button className="primary" onClick={() => {setEdit(null);notify(edit.id?'商品信息已保存':'商品已创建');}}>{edit.id?'保存修改':'创建商品'}</button></div></aside></div>}
    </section>
  );
}

function AdminOverview({ surface, navigate }) {
  const merchant = surface === 'merchant';
  const metrics = merchant
    ? [['今日支付订单','38','较昨日 +12%'],['今日销售额','¥12,680.00','较昨日 +8.4%'],['待发货','12','需及时处理'],['退款待处理','3','最早等待 46 分钟']]
    : [['今日支付订单','126','全平台'],['今日交易额','¥58,240.00','较昨日 +9.7%'],['活跃店铺','18','共 24 家'],['退款待处理','7','跨 5 家店铺']];
  const tasks = merchant
    ? [['待发货订单','12','merchant/orders'],['低库存商品','4','merchant/products'],['待回复评价','6','merchant/reviews'],['拼团报名结果','2','merchant/groupbuy']]
    : [['商家入驻待审','3','platform/merchants'],['秒杀报名待审','2','platform/seckill?tab=报名审核'],['待审核评价','8','platform/reviews'],['意见反馈待处理','5','platform/feedback']];
  return <section className="admin-page overview-page" data-screen-label={`${merchant?'商家':'平台'}经营概览`}><div className="overview-metrics">{metrics.map((item,index)=><article key={item[0]}><span>{item[0]}</span><b>{item[1]}</b><small className={index>1?'attention':''}>{item[2]}</small></article>)}</div><div className="overview-grid"><section className="overview-tasks"><div className="table-toolbar"><div><span className="shelf-mark"></span><b>待办事项</b></div><StatusTicket tone="warning">今日</StatusTicket></div>{tasks.map((task)=><button key={task[0]} onClick={()=>navigate(task[2])}><span><b>{task[0]}</b><small>点击进入对应业务列表</small></span><strong>{task[1]}</strong><Icon name="chevron" size={16}/></button>)}</section><section className="trend-panel"><div className="table-toolbar"><div><span className="shelf-mark"></span><b>近 7 日交易趋势</b></div><span className="mini-legend"><i></i>支付金额</span></div><div className="fake-chart">{[42,55,48,68,61,78,72].map((value,index)=><span key={index} style={{height:`${value}%`}}><i>{['21','22','23','24','25','26','27'][index]}</i></span>)}</div></section></div></section>;
}

function moduleDefinition(surface, page) {
  const modules = {
    platform: {
      merchants: { title:'商家管理', desc:'查看商家资料、店铺关系与经营状态', action:'新增商家', columns:['商家','账号','店铺数','联系人','状态'], rows:[['山野生活','mch001','2','周予安','正常'],['Yeah 数码','mch002','1','林可','正常'],['白茶研究所','mch003','1','陈一禾','待审核']] },
      shops: { title:'店铺管理', desc:'管理平台店铺资料与营业状态', action:'新增店铺', columns:['店铺','所属商家','主营分类','商品数','状态'], rows:[['山野生活店','山野生活','居家生活','38','营业中'],['Yeah 数码生活馆','Yeah 数码','数码影音','126','营业中'],['白茶美护精选','白茶研究所','美容护肤','45','已停业']] },
      reviews: { title:'评价管理', desc:'审核、屏蔽和查看商品评价及回复', tabs:['全部评价','待审核','已屏蔽'], columns:['商品','评价用户','评分','评价内容','状态'], rows:[['云感降噪头戴耳机','林可','5 星','佩戴舒适，降噪效果很好','已展示'],['白茶屏障修护精华','陈一禾','5 星','敏感肌使用没有刺激','已展示'],['山野拼配挂耳咖啡','周予安','3 星','包装有轻微压痕','待审核']] },
      points: { title:'积分管理', desc:'查询积分流水并维护积分获取规则', tabs:['积分流水','积分规则'], columns:['会员','业务类型','变动积分','变动前','变动后'], rows:[['林可','订单完成','+399','881','1,280'],['林可','完成评价','+10','871','881'],['陈一禾','平台调整','+500','3,000','3,500']] },
      'points-mall': { title:'积分商城', desc:'管理积分商品、分类和兑换订单', action:'新增积分商品', tabs:['商品分类','积分商品','兑换订单'], columns:['积分商品','类型','积分价格','库存','状态'], rows:[['品牌帆布袋','实物商品','600','36','已上架'],['全店 20 元券','优惠券','300','88','已上架'],['专属客服权益','虚拟权益','1,200','12','已下架']] },
      seckill: { title:'秒杀管理', desc:'管理活动、场次、报名审核、商品与成交数据', action:'新建活动', tabs:['活动管理','场次管理','报名审核','商品管理','实时监控','成交分析'], columns:['活动','报名时间','活动时间','商品数','状态'], rows:[['秋日数码秒杀','10/20–10/24','10/27 10:00–12:00','8','进行中'],['晚八点居家场','10/22–10/26','10/28 20:00–22:00','5','待开始']] },
      groupbuy: { title:'拼团管理', desc:'管理拼团活动、审核、商品与订单', action:'新建活动', tabs:['活动管理','申请审核','商品管理','订单管理'], columns:['活动','成团人数','开团上限','活动时间','状态'], rows:[['双人好物团','2 人','20 团','10/27–10/31','活动中'],['山野咖啡拼团','3 人','不限','11/01–11/05','待开始']] },
      content: { title:'首页运营', desc:'配置 Banner 与 App 快捷入口', action:'新增内容', tabs:['Banner','快捷入口'], columns:['名称','展示位置','跳转目标','排序','状态'], rows:[['秋日上新','首页 Banner','商品详情','1','启用'],['积分商城','快捷入口','积分商城','5','启用'],['拼团活动','快捷入口','拼团活动','2','启用']] },
      feedback: { title:'意见反馈', desc:'处理会员问题与建议', tabs:['全部','待处理','处理中','已解决'], columns:['会员','反馈内容','联系方式','提交时间','状态'], rows:[['林可','希望优化订单页面信息层级','138****7621','10/27 09:12','待处理'],['周予安','商品图片加载偶尔较慢','-','10/26 18:40','处理中'],['陈一禾','建议增加积分兑换商品','177****4403','10/24 15:22','已解决']] }
    },
    merchant: {
      categories: { title:'商品分类', desc:'维护当前店铺的商品分类树', action:'新增分类', columns:['分类名称','分类编码','层级','商品数','状态'], rows:[['数码影音','digital','一级','126','启用'],['耳机音响','audio','二级','32','启用'],['智能穿戴','wearable','二级','18','启用']] },
      sku: { title:'SKU 管理', desc:'查看商品规格组合、售价与库存', columns:['SKU','所属商品','规格值','售价','库存'], rows:[['SKU-1001-01','云感降噪头戴耳机','雾银 / 标准版','¥399.00','18'],['SKU-1001-02','云感降噪头戴耳机','曜石黑 / 长续航版','¥469.00','12']] },
      specs: { title:'规格管理', desc:'维护店铺商品规格与可选值', action:'新增规格', tabs:['规格定义','规格选项'], columns:['规格名称','规格编码','选项数','排序','状态'], rows:[['颜色','color','5','1','启用'],['容量','capacity','4','2','启用'],['重量','weight','3','3','启用']] },
      banner: { title:'店铺轮播图', desc:'维护当前店铺首页轮播内容', action:'新增轮播图', columns:['标题','跳转类型','跳转目标','排序','状态'], rows:[['耳机新品上市','商品详情','云感降噪头戴耳机','1','启用'],['秋日居家节','商品列表','居家生活','2','启用']] },
      reviews: { title:'评价管理', desc:'查看并回复本店商品评价', tabs:['全部评价','待回复','已回复'], columns:['商品','评价用户','评分','评价内容','回复状态'], rows:[['云感降噪头戴耳机','林可','5 星','佩戴两小时没有压耳','已回复'],['便携蓝牙音箱','木棉','4 星','低音表现不错','待回复']] },
      coupons: { title:'优惠券管理', desc:'配置店铺券并查看领取和使用记录', action:'新增优惠券', tabs:['优惠券','领取记录','使用记录'], columns:['优惠券','类型','优惠内容','已领取/总量','状态'], rows:[['全店满 199 减 20','满减券','满199减20','88/300','启用'],['护肤专享 9 折券','折扣券','9折','42/100','启用'],['新人无门槛券','无门槛券','减8元','100/100','已领完']] },
      seckill: { title:'秒杀活动', desc:'查看平台活动并提交商品报名', tabs:['活动列表','报名记录','秒杀商品'], columns:['活动','报名时间','活动时间','报名状态','操作'], rows:[['秋日数码秒杀','10/20–10/24','10/27 10:00','审核通过','查看商品'],['晚八点居家场','10/22–10/26','10/28 20:00','可报名','申请参加']] },
      groupbuy: { title:'拼团活动', desc:'申请活动并管理拼团商品与订单', tabs:['活动列表','申请记录','拼团商品','拼团订单'], columns:['活动','成团人数','活动时间','申请状态','状态'], rows:[['双人好物团','2 人','10/27–10/31','审核通过','活动中'],['山野咖啡拼团','3 人','11/01–11/05','待审核','待开始']] },
      shipping: { title:'物流设置', desc:'管理发货地址和物流服务商', action:'新增物流公司', tabs:['物流公司','发货地址'], columns:['物流公司','编码','是否常用','更新时间','状态'], rows:[['顺丰速运','SF','是','10/26 17:22','启用'],['京东物流','JD','是','10/25 10:16','启用'],['圆通速递','YTO','否','10/24 09:05','启用']] },
      settings: { title:'店铺设置', desc:'维护当前登录店铺的基础资料与营业信息', action:'保存设置', columns:['配置项','当前内容','更新时间','操作','状态'], rows:[['店铺名称','山野生活店','10/27 08:12','编辑','已生效'],['客服电话','400-800-2026','10/24 16:30','编辑','已生效'],['营业状态','正常营业','10/20 09:00','编辑','已生效']] }
    }
  };
  return modules[surface]?.[page];
}

function moduleTabView(surface, page, tab, fallback) {
  const views = {
    'platform:reviews:待审核': { columns:['商品','评价用户','评分','评价内容','状态'], rows:[['山野拼配挂耳咖啡','周予安','3 星','包装有轻微压痕','待审核']] },
    'platform:reviews:已屏蔽': { columns:['商品','评价用户','屏蔽原因','处理时间','状态'], rows:[['便携蓝牙音箱','匿名会员','包含无关广告内容','10/25 16:20','已屏蔽']] },
    'platform:points:积分规则': { columns:['规则名称','触发事件','积分值','每日上限','状态'], rows:[['订单完成赠送','订单完成','实付 1 元 = 1 积分','2,000','启用'],['评价奖励','首次发表评价','10 积分','10','启用'],['注册奖励','完成会员注册','50 积分','50','停用']] },
    'platform:points-mall:商品分类': { columns:['分类名称','分类编码','商品数','排序','状态'], rows:[['实物好礼','physical','6','1','启用'],['优惠券','coupon','4','2','启用'],['会员权益','benefit','3','3','启用']] },
    'platform:points-mall:兑换订单': { columns:['兑换单号','会员','兑换商品','消耗积分','状态'], rows:[['PX20261027001','林可','品牌帆布袋','600','待发货'],['PX20261020008','周予安','全店 20 元券','300','已完成']] },
    'platform:seckill:场次管理': { columns:['场次','活动','开始时间','结束时间','商品数'], rows:[['10:00 场','秋日数码秒杀','10/27 10:00','10/27 12:00','8'],['20:00 场','晚八点居家场','10/28 20:00','10/28 22:00','5']] },
    'platform:seckill:报名审核': { columns:['商家','店铺','活动','商品','审核状态'], rows:[['Yeah 数码','Yeah 数码生活馆','秋日数码秒杀','云感降噪头戴耳机','待审核'],['山野生活','山野生活店','晚八点居家场','弧光人体工学休闲椅','审核通过']] },
    'platform:seckill:商品管理': { columns:['秒杀商品','活动','商家','秒杀价','状态'], rows:[['云感降噪头戴耳机','秋日数码秒杀','Yeah 数码','¥299.00','销售中'],['弧光人体工学休闲椅','晚八点居家场','山野生活','¥499.00','待开始']] },
    'platform:seckill:实时监控': { columns:['活动','支付订单','待支付','已售库存','运行状态'], rows:[['秋日数码秒杀','126','18','126/180','监控中'],['晚八点居家场','0','0','0/80','待开始']] },
    'platform:seckill:成交分析': { columns:['活动','成交订单','成交额','退款订单','转化状态'], rows:[['秋日数码秒杀','126','¥37,674.00','2','统计中'],['晚八点居家场','0','¥0.00','0','待开始']] },
    'platform:groupbuy:申请审核': { columns:['申请时间','商家','店铺','活动','审核状态'], rows:[['10/24 09:18','Yeah 数码','Yeah 数码生活馆','双人好物团','待审核'],['10/23 16:40','山野生活','山野生活店','山野咖啡拼团','审核通过']] },
    'platform:groupbuy:商品管理': { columns:['拼团商品','源商品','拼团价','拼团库存','状态'], rows:[['白茶修护双人团','白茶屏障修护精华','¥139.00','48','销售中'],['山野咖啡三人团','山野拼配挂耳咖啡','¥49.00','90','销售中']] },
    'platform:groupbuy:订单管理': { columns:['订单编号','拼团活动','团编号','已占席/成团','状态'], rows:[['YM202610260018','双人好物团','G20261027003','2/2','待发货'],['YM202610270021','山野咖啡拼团','G20261027008','2/3','待成团']] },
    'platform:content:快捷入口': { columns:['入口名称','图标','跳转目标','排序','状态'], rows:[['秒杀','闪电','秒杀活动','1','启用'],['拼团','用户组','拼团活动','2','启用'],['积分商城','钻石','积分中心','4','启用']] },
    'platform:feedback:待处理': { columns:['会员','反馈内容','联系方式','提交时间','状态'], rows:[['林可','希望优化订单页面信息层级','138****7621','10/27 09:12','待处理']] },
    'platform:feedback:处理中': { columns:['会员','反馈内容','处理人','更新时间','状态'], rows:[['周予安','商品图片加载偶尔较慢','admin','10/27 08:40','处理中']] },
    'platform:feedback:已解决': { columns:['会员','反馈内容','处理结果','完成时间','状态'], rows:[['陈一禾','建议增加积分兑换商品','已上线积分商城','10/24 18:02','已解决']] },
    'merchant:reviews:待回复': { columns:['商品','评价用户','评分','评价内容','回复状态'], rows:[['便携蓝牙音箱','木棉','4 星','低音表现不错','待回复']] },
    'merchant:reviews:已回复': { columns:['商品','评价用户','评分','最后回复时间','回复状态'], rows:[['云感降噪头戴耳机','林可','5 星','10/27 10:22','已回复']] },
    'merchant:specs:规格选项': { columns:['规格名称','选项名称','选项编码','排序','状态'], rows:[['颜色','雾银','silver','1','启用'],['颜色','曜石黑','black','2','启用'],['容量','标准版','standard','1','启用']] },
    'merchant:coupons:领取记录': { columns:['会员','优惠券','领取时间','有效期','状态'], rows:[['林可','全店满 199 减 20','10/27 09:20','11/30','未使用'],['周予安','新人无门槛券','10/26 15:10','11/18','已使用']] },
    'merchant:coupons:使用记录': { columns:['会员','优惠券','关联订单','优惠金额','使用时间'], rows:[['周予安','新人无门槛券','YM202610260018','¥8.00','10/26 19:36'],['陈一禾','护肤专享 9 折券','YM202610270002','¥16.80','10/27 10:02']] },
    'merchant:seckill:报名记录': { columns:['活动','报名商品','秒杀价','报名时间','状态'], rows:[['秋日数码秒杀','云感降噪头戴耳机','¥299.00','10/23 09:12','审核通过'],['晚八点居家场','弧光人体工学休闲椅','¥499.00','10/25 14:36','待审核']] },
    'merchant:seckill:秒杀商品': { columns:['活动','商品','秒杀价','活动库存','状态'], rows:[['秋日数码秒杀','云感降噪头戴耳机','¥299.00','18','销售中'],['晚八点居家场','弧光人体工学休闲椅','¥499.00','10','待开始']] },
    'merchant:groupbuy:申请记录': { columns:['申请时间','活动','商品','审核结果','状态'], rows:[['10/24 09:18','双人好物团','白茶屏障修护精华','审核通过','活动中'],['10/23 16:40','山野咖啡拼团','山野拼配挂耳咖啡','待审核','待审核']] },
    'merchant:groupbuy:拼团商品': { columns:['拼团商品','源商品','拼团价','库存','状态'], rows:[['白茶修护双人团','白茶屏障修护精华','¥139.00','48','销售中'],['山野咖啡三人团','山野拼配挂耳咖啡','¥49.00','90','待开始']] },
    'merchant:groupbuy:拼团订单': { columns:['订单编号','拼团活动','团编号','已占席/成团','状态'], rows:[['YM202610260018','双人好物团','G20261027003','2/2','待发货'],['YM202610270021','山野咖啡拼团','G20261027008','2/3','待成团']] },
    'merchant:shipping:发货地址': { columns:['地址名称','联系人','联系电话','详细地址','状态'], rows:[['默认发货仓','周予安','138****1622','杭州市余杭区仓前街道 18 号','默认'],['售后收货点','林可','186****7304','杭州市西湖区文三路 88 号','启用']] }
  };
  return views[`${surface}:${page}:${tab}`] || fallback;
}

function isStatusCell(value) {
  return ['启用','停用','正常','待审核','审核通过','已展示','待发货','已完成','活动中','待开始','销售中','待成团','处理中','待处理','已解决','默认','统计中','未使用','已使用','已下架','已领完','待回复','已回复'].some((status)=>String(value).includes(status));
}

function moduleAction(surface,page,tab,defaultAction) {
  const actions = {
    'platform:points-mall:商品分类':'新增分类',
    'platform:points-mall:积分商品':'新增积分商品',
    'platform:seckill:活动管理':'新建活动',
    'platform:seckill:场次管理':'新建场次',
    'platform:groupbuy:活动管理':'新建活动',
    'platform:content:Banner':'新增 Banner',
    'platform:content:快捷入口':'新增入口',
    'merchant:coupons:优惠券':'新增优惠券',
    'merchant:specs:规格定义':'新增规格',
    'merchant:specs:规格选项':'新增选项',
    'merchant:shipping:物流公司':'新增物流公司',
    'merchant:shipping:发货地址':'新增地址'
  };
  const key = `${surface}:${page}:${tab}`;
  if (Object.prototype.hasOwnProperty.call(actions,key)) return actions[key];
  return tab ? '' : defaultAction;
}

function AdminModulePage({ surface, page, route, navigate, notify }) {
  const config = moduleDefinition(surface,page);
  const requestedTab = routeParam(route,'tab');
  const [tab,setTab] = useState(requestedTab || config?.tabs?.[0] || '');
  const [keyword,setKeyword] = useState('');
  const [selected,setSelected] = useState(null);
  const [creating,setCreating] = useState(false);
  useEffect(()=>{ setTab(requestedTab || config?.tabs?.[0] || ''); setKeyword(''); setSelected(null); setCreating(false); },[surface,page,requestedTab]);
  if (!config) return null;
  const view = moduleTabView(surface,page,tab,{columns:config.columns,rows:config.rows});
  const rows = view.rows.filter((row) => row.join('').includes(keyword));
  const action = moduleAction(surface,page,tab,config.action);
  return <section className="admin-page" data-screen-label={`${surface}-${config.title}-${tab||'列表'}`}>{config.tabs&&<div className="module-tabs">{config.tabs.map((item)=><button key={item} className={tab===item?'active':''} onClick={()=>{setKeyword('');navigate(`${surface}/${page}?tab=${encodeURIComponent(item)}`);}}>{item}</button>)}</div>}<FilterBar onReset={()=>setKeyword('')} onSearch={()=>notify(`找到 ${rows.length} 条记录`)} actions={action&&<button className="business-primary" onClick={()=>setCreating(true)}><Icon name="plus" size={17}/>{action}</button>}><label><span>关键词</span><input value={keyword} onChange={(e)=>setKeyword(e.target.value)} placeholder={`搜索${tab||config.title}`}/></label><label><span>状态</span><select defaultValue="全部状态"><option>全部状态</option><option>启用</option><option>停用</option></select></label></FilterBar><div className="admin-table-frame"><table className="admin-table"><thead><tr>{view.columns.map((col)=><th key={col}>{col}</th>)}<th className="actions">操作</th></tr></thead><tbody>{rows.map((row,index)=><tr key={index}>{row.map((cell,cellIndex)=><td key={cellIndex}>{isStatusCell(cell)&&cellIndex===row.length-1?<StatusTicket tone={String(cell).includes('待')?'warning':String(cell).includes('停')||String(cell).includes('完成')?'neutral':'success'}>{cell}</StatusTicket>:cell}</td>)}<td className="actions"><button className="table-link" onClick={()=>setSelected(row)}>查看</button><button className="table-more" onClick={()=>notify('该记录的状态操作已展开')}><Icon name="more" size={18}/></button></td></tr>)}</tbody></table><AdminPagination count={rows.length}/></div>{selected&&<div className="drawer-mask" onClick={()=>setSelected(null)}><aside className="admin-drawer" onClick={(e)=>e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">DETAIL</span><h2>{tab||config.title}详情</h2></div><button onClick={()=>setSelected(null)}>×</button></div><div className="description-grid">{view.columns.map((col,index)=><div className={index===0?'wide':''} key={col}><span>{col}</span><b>{selected[index]}</b></div>)}</div>{page==='reviews'&&<label className="dialog-field"><span>{surface==='platform'?'审核备注':'回复内容'}</span><textarea placeholder={surface==='platform'?'选填本次审核说明':'输入对会员的回复'}></textarea></label>}<div className="drawer-actions"><button onClick={()=>setSelected(null)}>关闭</button><button className="primary" onClick={()=>{setSelected(null);notify(page==='reviews'?'处理结果已保存':'操作已保存');}}>{page==='reviews'?(surface==='platform'?'确认审核':'发表回复'):'保存'}</button></div></aside></div>}{creating&&<div className="dialog-mask" onClick={()=>setCreating(false)}><section className="admin-dialog" onClick={(e)=>e.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">CREATE</span><h2>{action}</h2></div><button onClick={()=>setCreating(false)}>×</button></div><label className="dialog-field"><span>名称</span><input placeholder={`请输入${config.title}名称`}/></label><label className="dialog-field"><span>状态</span><select defaultValue="启用"><option>启用</option><option>停用</option></select></label><label className="dialog-field"><span>备注 <small>选填</small></span><textarea placeholder="补充业务说明"></textarea></label><div className="dialog-actions"><button onClick={()=>setCreating(false)}>取消</button><button className="primary" onClick={()=>{setCreating(false);notify(`${action}已保存`);}}>确认</button></div></section></div>}</section>;
}

function MerchantSettingsPage({ notify }) {
  const [open,setOpen] = useState(true);
  const [name,setName] = useState('山野生活店');
  const [phone,setPhone] = useState('400-800-2026');
  const [intro,setIntro] = useState('挑选耐用、舒适、有生活气息的日常好物。');
  return <section className="admin-page merchant-settings-page" data-screen-label="商家后台店铺设置"><div className="settings-form-card"><div className="settings-section-title"><span className="shelf-mark"></span><div><b>基础资料</b><small>资料仅作用于当前店铺，保存后同步展示在 App 店铺页</small></div><StatusTicket tone={open?'success':'neutral'}>{open?'正常营业':'暂停营业'}</StatusTicket></div><div className="settings-form-grid"><label><span>店铺名称</span><input value={name} onChange={(e)=>setName(e.target.value)}/></label><label><span>客服电话</span><input value={phone} onChange={(e)=>setPhone(e.target.value)}/></label><label className="wide"><span>店铺简介</span><textarea value={intro} onChange={(e)=>setIntro(e.target.value)}></textarea></label><label><span>主营分类</span><select defaultValue="居家生活"><option>居家生活</option><option>数码影音</option><option>美容护肤</option></select></label><label className="business-switch"><span><b>营业状态</b><small>关闭后商品仍保留，但店铺停止接单</small></span><input type="checkbox" checked={open} onChange={(e)=>setOpen(e.target.checked)}/></label></div><div className="settings-save-bar"><span>上次保存：2026-10-27 08:12</span><button onClick={()=>notify('店铺设置已保存')}>保存设置</button></div></div></section>;
}

function FrameworkReferencePage({ route, notify }) {
  const view = routeParam(route,'view') || 'user';
  const definitions = {
    user:['用户管理',['用户名','昵称','组织','状态'],[['admin','平台管理员','总部','正常'],['operator','运营人员','运营中心','正常']]],
    org:['组织管理',['组织名称','组织编码','负责人','状态'],[['平台总部','headquarters','平台管理员','正常'],['运营中心','operation','运营人员','正常']]],
    position:['岗位管理',['岗位名称','岗位编码','排序','状态'],[['平台管理员','admin','1','正常'],['商城运营','mall_operator','2','正常']]],
    role:['角色管理',['角色名称','角色编码','用户数','状态'],[['超级管理员','super_admin','1','正常'],['商城运营','mall_operator','3','正常']]],
    resource:['资源管理',['资源名称','资源类型','路由','状态'],[['系统管理','目录','/system','正常'],['商城管理','目录','/mall','正常']]],
    dict:['字典管理',['字典名称','字典编码','数据项','状态'],[['订单状态','order_status','8','正常'],['启停状态','enable_status','2','正常']]],
    config:['参数设置',['参数名称','参数键','参数值','状态'],[['平台名称','system.name','Yeah Mall','正常'],['开放注册','member.register.enabled','是','正常']]],
    file:['文件管理',['文件名称','文件类型','文件大小','状态'],[['banner-autumn.jpg','image/jpeg','286 KB','正常'],['product-guide.pdf','application/pdf','1.2 MB','正常']]],
    storage:['存储管理',['存储名称','存储类型','使用情况','状态'],[['本地存储','LOCAL','2.8 GB','启用'],['对象存储','OSS','未配置','停用']]],
    log:['日志管理',['操作模块','操作用户','请求方式','状态'],[['订单管理','admin','GET','成功'],['商品管理','operator','POST','成功']]],
    area:['地区管理',['地区名称','地区编码','层级','状态'],[['浙江省','330000','省','正常'],['杭州市','330100','市','正常']]
    ]
  };
  const [title,columns,rows] = definitions[view] || definitions.user;
  return <section className="admin-page framework-reference-page" data-screen-label={`平台后台系统管理-${title}`}><div className="framework-preserved-note"><Icon name="settings" size={18}/><span className="framework-note-copy"><b>{title}沿用 yeahboot 现有页面</b><small>原型仅还原菜单位置，不改变框架页面结构与交互。</small></span><StatusTicket>FRAMEWORK</StatusTicket></div><FilterBar onReset={()=>notify('筛选条件已重置')} onSearch={()=>notify(`找到 ${rows.length} 条记录`)}><label><span>关键词</span><input placeholder={`搜索${title}`}/></label><label><span>状态</span><select defaultValue="全部状态"><option>全部状态</option><option>正常</option><option>停用</option></select></label></FilterBar><div className="admin-table-frame"><div className="table-toolbar"><div><span className="shelf-mark"></span><b>{title}</b><StatusTicket small>{rows.length} 条</StatusTicket></div><button onClick={()=>notify('列表已刷新')}><Icon name="clock" size={16}/>刷新</button></div><table className="admin-table"><thead><tr>{columns.map((col)=><th key={col}>{col}</th>)}<th className="actions">操作</th></tr></thead><tbody>{rows.map((row,index)=><tr key={index}>{row.map((cell)=><td key={cell}>{cell}</td>)}<td className="actions"><button className="table-link" onClick={()=>notify(`${title}详情已打开`)}>查看</button></td></tr>)}</tbody></table><AdminPagination count={rows.length}/></div></section>;
}

function AdminServicePage({ notify }) {
  const [active,setActive] = useState(MOCKS.conversations[0]);
  const [text,setText] = useState('');
  const [messages,setMessages] = useState(['您好，请问有什么可以帮您？','想确认订单今天可以发货吗？','可以，预计下午四点前交给快递。']);
  const send=()=>{if(!text.trim())return;setMessages([...messages,text.trim()]);setText('');notify('消息已发送');};
  return <section className="admin-page service-admin-page" data-screen-label="商家客服消息"><div className="service-console"><aside><label><Icon name="search" size={16}/><input placeholder="搜索会话"/></label>{MOCKS.conversations.map((item)=><button key={item.id} className={active.id===item.id?'active':''} onClick={()=>setActive(item)}><Avatar name={item.name}/><span><b>{item.name}</b><small>{item.preview}</small></span><time>{item.time}</time></button>)}</aside><main><header><Avatar name={active.name}/><div><b>{active.name}</b><span>最近活跃 · 订单咨询</span></div><StatusTicket tone="success">在线</StatusTicket><button onClick={()=>notify('会话已标记完成')}>结束会话</button></header><div className="service-chat">{messages.map((msg,index)=><div className={index%2===0?'them':'me'} key={index}>{msg}</div>)}</div><footer><input value={text} onChange={(e)=>setText(e.target.value)} onKeyDown={(e)=>e.key==='Enter'&&send()} placeholder="输入回复内容"/><button onClick={send}>发送</button></footer></main></div></section>;
}

function AdminPrototype({ surface, route, navigate, notify }) {
  const page = routePath(route).split('/')[1] || 'overview';
  const [members, setMembers] = useState(MOCKS.members);
  const [orders, setOrders] = useState(MOCKS.orders);
  let content;
  if (page === 'overview') content = <AdminOverview surface={surface} navigate={navigate}/>;
  else if (surface === 'platform' && page === 'members') content = <AdminMembers members={members} setMembers={setMembers} notify={notify}/>;
  else if (surface === 'merchant' && page === 'orders') content = <AdminOrders orders={orders} setOrders={setOrders} notify={notify}/>;
  else if (surface === 'merchant' && page === 'products') content = <MerchantProducts notify={notify}/>;
  else if (surface === 'merchant' && page === 'service') content = <AdminServicePage notify={notify}/>;
  else if (surface === 'merchant' && page === 'settings') content = <MerchantSettingsPage notify={notify}/>;
  else if (surface === 'platform' && page === 'system') content = <FrameworkReferencePage route={route} notify={notify}/>;
  else content = <AdminModulePage surface={surface} page={page} route={route} navigate={navigate} notify={notify}/>;
  return (
    <div className="admin-shell" data-screen-label={surface === 'platform' ? '平台后台' : '商家后台'}>
      <AdminSidebar surface={surface} route={route} navigate={navigate}/>
      <div className="admin-workspace"><AdminTopbar surface={surface} page={page} route={route} notify={notify}/>{content}</div>
    </div>
  );
}

function PrototypeApp() {
  const [route, navigate] = usePrototypeRoute();
  const initialSurface = route.split('/')[0];
  const [surface, setSurface] = useState(['app','platform','merchant'].includes(initialSurface) ? initialSurface : 'app');
  const [toast, setToast] = useState('');
  useEffect(() => {
    const next = route.split('/')[0];
    if (['app','platform','merchant'].includes(next)) setSurface(next);
  }, [route]);
  const notify = (message) => {
    setToast(message);
    window.clearTimeout(window.__yeahMallToastTimer);
    window.__yeahMallToastTimer = window.setTimeout(() => setToast(''), 2200);
  };
  return (
    <div className={`prototype-root surface-${surface}`}>
      <PrototypeToolbar surface={surface} setSurface={setSurface} route={route} navigate={navigate}/>
      <div className="prototype-content">
        {surface === 'app' ? <AppPrototype route={route} navigate={navigate} notify={notify}/> : <AdminPrototype surface={surface} route={route} navigate={navigate} notify={notify}/>}
      </div>
      <Toast message={toast}/>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<PrototypeApp/>);
