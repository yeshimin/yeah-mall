const MOCKS = window.YEAH_MALL_MOCKS;

const APP_PAGES = [
  ['app/home', '首页'], ['app/category', '分类'], ['app/products', '商品列表'], ['app/product', '商品详情'],
  ['app/cart', '购物车'], ['app/confirm', '确认订单'], ['app/orders', '订单列表'], ['app/order', '订单详情'],
  ['app/profile', '个人中心'], ['app/favorites', '我的收藏'], ['app/history', '浏览足迹'], ['app/coupons', '我的优惠券'],
  ['app/coupon-center', '领券中心'], ['app/points', '我的积分'], ['app/points-mall', '积分商城'], ['app/seckill', '秒杀活动'], ['app/groupbuy', '拼团活动'], ['app/service', '客服'],
  ['app/addresses', '地址管理'], ['app/reviews', '商品评价'], ['app/feedback', '意见反馈'], ['app/settings', '设置'],
  ['app/shops', '店铺列表'], ['app/shop', '店铺详情'], ['app/pay-result', '支付结果'], ['app/logistics', '物流详情'],
  ['app/refund', '申请退款'], ['app/review-form', '发表评价'], ['app/review-detail', '评价详情'], ['app/profile-edit', '个人资料'],
  ['app/about', '关于我们'], ['app/legal', '服务协议与隐私政策'], ['app/seckill-products', '秒杀商品'], ['app/groupbuy-products', '拼团商品'], ['app/coupon-select', '选择优惠券'],
  ['app/points-product', '积分商品'], ['app/points-confirm', '确认兑换'], ['app/points-orders', '积分订单'], ['app/points-order', '积分订单详情'],
  ['app/seckill-detail', '秒杀商品详情'], ['app/groupbuy-detail', '拼团商品详情'], ['app/group-select', '选择拼团'], ['app/address-edit', '编辑地址'],
  ['app/refund-detail', '退款详情'], ['app/notifications', '消息通知'], ['app/privacy', '隐私设置'], ['app/login', '登录'], ['app/register', '注册']
];

const SINGLE_SHOP_PAGES = APP_PAGES
  .filter(([key]) => !['app/shops', 'app/shop'].includes(key))
  .map(([key, label]) => [key.replace(/^app\//, 'shop-app/'), key === 'app/about' ? '品牌信息' : label])
  .concat([['shop-app/unavailable', '店铺不可用状态']]);

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
  const appPages = surface === 'shop-app' ? SINGLE_SHOP_PAGES : APP_PAGES;
  const isAppSurface = surface === 'app' || surface === 'shop-app';
  return (
    <div className="prototype-toolbar">
      <div className="prototype-brand"><span className="brand-stamp">Y</span><b>Yeah Mall UI v1</b></div>
      <div className="surface-switch" role="tablist">
        <button className={surface === 'app' ? 'active' : ''} onClick={() => { setSurface('app'); navigate('app/home'); }}>App</button>
        <button className={surface === 'shop-app' ? 'active' : ''} onClick={() => { setSurface('shop-app'); navigate('shop-app/home'); }}>App（单店）</button>
        <button className={surface === 'platform' ? 'active' : ''} onClick={() => { setSurface('platform'); navigate('platform/overview'); }}>平台后台</button>
        <button className={surface === 'merchant' ? 'active' : ''} onClick={() => { setSurface('merchant'); navigate('merchant/overview'); }}>商家后台</button>
      </div>
      {isAppSurface && (
        <div className="scenario-switch">
          {appPages.slice(0, 5).map(([key, label]) => <button key={key} className={currentPage === key ? 'active' : ''} onClick={() => navigate(key)}>{label}</button>)}
          <select aria-label={surface === 'shop-app' ? '单店 App 页面地图' : 'App 页面地图'} value={currentPage} onChange={(e) => navigate(e.target.value)}>
            {appPages.map(([key, label]) => <option value={key} key={key}>{label}</option>)}
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

function ListBoundary({ text = '已经到底了' }) {
  return <div className="list-boundary"><i></i><span>{text}</span><i></i></div>;
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
  const visibleQuickLinks = MOCKS.quickLinks.slice(0, 10);
  const firstQuickRowCount = visibleQuickLinks.length <= 4 ? visibleQuickLinks.length : Math.ceil(visibleQuickLinks.length / 2);
  const quickLinkRows = visibleQuickLinks.length <= 4
    ? [{ items: visibleQuickLinks, maxCount: visibleQuickLinks.length }]
    : [
        { items: visibleQuickLinks.slice(0, firstQuickRowCount), maxCount: firstQuickRowCount },
        { items: visibleQuickLinks.slice(firstQuickRowCount), maxCount: firstQuickRowCount }
      ];
  const quickRoutes = {
    seckill: 'app/seckill', group: 'app/groupbuy', new: 'app/products', coupon: 'app/coupon-center',
    shops: 'app/shops', points: 'app/points-mall', service: 'app/service', products: 'app/products'
  };
  return (
    <div className="mobile-screen app-home" data-screen-label="App 首页">
      <MobileStatusBar tone="paper"/>
      <header className="app-topbar tab-paper-topbar">
        <button className="wordmark" type="button" aria-label="查看关于我们" onClick={() => navigate('app/about')}><span>YEAH</span><b>MALL</b></button>
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
          {quickLinkRows.map((row, rowIndex) => (
            <div className="quick-grid-row" style={{ gridTemplateColumns:`repeat(${row.maxCount}, minmax(0, 1fr))` }} key={rowIndex}>
              {row.items.map((item) => (
                <button key={item.id} onClick={() => navigate(quickRoutes[item.id])}>
                  <span className={`quick-icon quick-${item.id}`}><Icon name={item.icon} size={21}/></span>
                  <span>{item.name}</span>
                </button>
              ))}
            </div>
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
          {!products.length && <EmptyState icon="search" title="没有找到相关商品" description="换个关键词或分类试试" fill/>}
          {!!products.length && <ListBoundary/>}
        </section>
      </main>
      <MobileBottomNav active="首页" navigate={navigate}/>
    </div>
  );
}

function SingleShopHome({ navigate }) {
  const [category, setCategory] = useState('all');
  const banners = [
    { id:'autumn', kicker:'山野生活 · 秋季上新', title:['把自然的','松弛感，','带回日常'], action:'查看新品', art:'coffee', tone:'forest' },
    { id:'home', kicker:'居家精选', title:['认真挑选，','每一件都','经得起日常'], action:'逛逛好物', art:'chair', tone:'clay' },
    { id:'care', kicker:'会员专享', title:['温和生活，','也照顾每一份','细小感受'], action:'领取好券', art:'serum', tone:'ocean' }
  ];
  const [bannerIndex, setBannerIndex] = useState(0);
  const activeBanner = banners[bannerIndex];
  useEffect(() => {
    const timer = window.setInterval(() => setBannerIndex((index) => (index + 1) % banners.length), 3200);
    return () => window.clearInterval(timer);
  }, []);
  const products = MOCKS.products.filter((item) => category === 'all' || item.category === category).slice(0, 10);
  const columns = products.reduce((result, item) => {
    const height = (item.imageHeight || 176) + (item.promotionText ? 30 : 0) + (item.subtitle ? 34 : 0) + 92;
    const target = result[0].height <= result[1].height ? result[0] : result[1];
    target.items.push(item);
    target.height += height;
    return result;
  }, [{ items:[], height:0 }, { items:[], height:0 }]);
  const shopCategories = [
    ['home','居家选物','让家慢下来','chair'], ['food','咖啡茶饮','每天一杯好味道','coffee'],
    ['beauty','温和护理','照顾细小感受','serum'], ['digital','随身好物','轻巧而耐用','headphone']
  ];
  const memberLinks = [
    ['ticket','领取好券','app/coupon-center'], ['diamond','积分换礼','app/points-mall'],
    ['message','专属客服','app/service'], ['storefront','品牌故事','app/about']
  ];
  return (
    <div className="mobile-screen app-home single-shop-home" data-screen-label="单店 App 首页">
      <MobileStatusBar tone="paper"/>
      <header className="app-topbar tab-paper-topbar single-shop-topbar">
        <button className="single-shop-lockup" onClick={() => navigate('app/about')}>
          <img src="assets/shop-logo-mountain.png" alt=""/>
          <span><b>山野生活</b><small>自然好物独立店</small></span>
        </button>
        <button className="mobile-search" type="button" onClick={() => navigate('app/products')}><span className="mobile-search-content"><Icon name="search" size={18}/><span>搜索店内商品</span></span></button>
        <WechatCapsule/>
      </header>
      <main className="mobile-scroll with-bottom-nav">
        <section className={`single-shop-editorial-hero single-shop-hero-${activeBanner.tone}`} onClick={() => navigate(activeBanner.id === 'care' ? 'app/coupon-center' : 'app/product')}>
          <div className="single-shop-hero-copy" key={activeBanner.id}>
            <span>SHANYE LIVING</span>
            <small>{activeBanner.kicker}</small>
            <h1>{activeBanner.title.map((line) => <React.Fragment key={line}>{line}<br/></React.Fragment>)}</h1>
            <p>居家、咖啡与自然选物</p>
            <button onClick={(event)=>{event.stopPropagation();navigate(activeBanner.id === 'care' ? 'app/coupon-center' : 'app/products');}}>{activeBanner.action} <Icon name="chevron" size={15}/></button>
          </div>
          <ProductArt type={activeBanner.art}/>
          <div className="single-shop-hero-dots" onClick={(event) => event.stopPropagation()}>
            {banners.map((banner, index) => <button key={banner.id} className={bannerIndex === index ? 'active' : ''} aria-label={`切换到第 ${index + 1} 张 Banner`} onClick={() => setBannerIndex(index)}></button>)}
          </div>
        </section>
        <section className="single-shop-promises" aria-label="店铺服务">
          <span><Icon name="check" size={14}/>正品保障</span><span><Icon name="truck" size={15}/>48 小时发货</span><button onClick={() => navigate('app/service')}><Icon name="message" size={14}/>售后无忧</button>
        </section>
        <section className="single-shop-section single-shop-category-showcase">
          <header><div><span>SHOP BY MOOD</span><h2>逛逛店内分类</h2></div><button onClick={()=>navigate('app/category')}>全部分类 <Icon name="chevron" size={14}/></button></header>
          <div>{shopCategories.map(([id,name,copy,art])=><button key={id} onClick={()=>navigate(`app/products?category=${id}`)}><span><ProductArt type={art} bare/></span><b>{name}</b><small>{copy}</small></button>)}</div>
        </section>
        <section className="single-shop-member-links" aria-label="会员服务">
          {memberLinks.map(([icon,label,target])=><button key={label} onClick={()=>navigate(target)}><Icon name={icon} size={20}/><span>{label}</span></button>)}
        </section>
        <section className="single-shop-campaigns">
          <button className="seckill" onClick={()=>navigate('app/seckill')}><span><small>限时闪购</small><b>今晚 20:00</b><em>精选单品直降</em></span><Icon name="bolt" size={24}/></button>
          <button className="group" onClick={()=>navigate('app/groupbuy')}><span><small>一起成团</small><b>双人好物团</b><em>找朋友更划算</em></span><Icon name="users" size={24}/></button>
        </section>
        <section className="product-section single-shop-product-section">
          <div className="section-heading"><div><span className="shelf-mark"></span><h2>店内好物</h2></div><button onClick={()=>navigate('app/products')}>全部商品 <Icon name="chevron" size={14}/></button></div>
          <div className="category-strip">{MOCKS.categories.map((item)=><button key={item.id} className={category===item.id?'active':''} onClick={()=>setCategory(item.id)}>{item.name}</button>)}</div>
          <div className="home-waterfall">{columns.map((column,columnIndex)=><div className="home-waterfall-column" key={columnIndex}>{column.items.map((item)=><button className="home-waterfall-item" key={item.id} onClick={()=>navigate('app/product')}><span className="home-waterfall-art" style={{height:`${item.imageHeight||176}px`}}><ProductArt type={item.art}/></span><span className="home-waterfall-copy"><span className="home-waterfall-name">{item.name}</span>{item.promotionText&&<span className="home-waterfall-promotion">{item.promotionText}</span>}<span className="home-waterfall-subtitle">{item.subtitle}</span><span className="home-waterfall-meta"><b>¥{item.price}</b><small>已售 {item.sales}</small></span></span></button>)}</div>)}</div>
          {!!products.length && <ListBoundary/>}
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

function AppProduct({ navigate, notify, singleShop = false }) {
  const product = MOCKS.products[0];
  const [favorite, setFavorite] = useState(false);
  const [sheet, setSheet] = useState(false);
  const [color, setColor] = useState(product.colors[0]);
  const [spec, setSpec] = useState(product.specs[0]);
  const [quantity, setQuantity] = useState(1);
  const [gallery, setGallery] = useState(0);
  const [purchaseMode, setPurchaseMode] = useState('buy');
  const [sectionTabsVisible, setSectionTabsVisible] = useState(false);
  const [activeSection, setActiveSection] = useState('base');
  const detailScrollRef = useRef(null);
  const reviewSectionRef = useRef(null);
  const storySectionRef = useRef(null);
  const handleDetailScroll = (event) => {
    const scrollTop = event.currentTarget.scrollTop;
    const marker = scrollTop + 128;
    setSectionTabsVisible(scrollTop > 250);
    if (storySectionRef.current && marker >= storySectionRef.current.offsetTop) setActiveSection('detail');
    else if (reviewSectionRef.current && marker >= reviewSectionRef.current.offsetTop) setActiveSection('review');
    else setActiveSection('base');
  };
  const scrollToProductSection = (section) => {
    const target = section === 'review' ? reviewSectionRef.current : section === 'detail' ? storySectionRef.current : null;
    detailScrollRef.current?.scrollTo({ top: target ? Math.max(target.offsetTop - 112, 0) : 0, behavior: 'smooth' });
    setActiveSection(section);
  };
  return (
    <div className="mobile-screen product-detail" data-screen-label={singleShop ? '单店 App 商品详情' : 'App 商品详情'}>
      <MobileStatusBar tone="transparent"/>
      <div className="detail-floatbar">
        <button onClick={() => navigate('app/home')}><Icon name="back"/></button>
        <WechatCapsule/>
      </div>
      <div className={`product-detail-nav-surface ${sectionTabsVisible ? 'visible' : ''}`}></div>
      <nav className={`product-section-tabs ${sectionTabsVisible ? 'visible' : ''}`}>{[['base','宝贝'],['review','评价'],['detail','详情']].map(([key,label])=><button key={key} className={activeSection===key?'active':''} onClick={()=>scrollToProductSection(key)}>{label}</button>)}</nav>
      <main ref={detailScrollRef} className="mobile-scroll detail-scroll-content" onScroll={handleDetailScroll}>
        <div className="detail-gallery">
        <div className="detail-side-actions"><button onClick={() => notify('分享入口已打开') }><Icon name="share"/></button><button onClick={() => setFavorite(!favorite)} className={favorite ? 'is-favorite' : ''}><Icon name="heart"/></button></div>
        <ProductArt type={product.art} className={`gallery-variant variant-${gallery}`}/>
        <div className="gallery-dots">{[0,1,2].map((dot) => <button key={dot} className={gallery === dot ? 'active' : ''} onClick={() => setGallery(dot)}></button>)}</div>
        </div>
        <section className="detail-main-info">
          <div className="price-line"><b>¥{product.price}</b></div>
          <h1>{product.name}</h1>
          <p>{product.subtitle}</p>
          <div className="detail-metrics"><span>已售 {product.sales}</span><span>好评率 {product.rating}%</span><span>总库存 {product.stock}</span></div>
        </section>
        <button className="detail-choice-row detail-choice-row-end" onClick={() => setSheet(true)}>
          <span className="choice-label">已选</span><b>{color} · {spec} · {quantity} 件</b><Icon name="chevron" size={18}/>
        </button>
        <section ref={reviewSectionRef} className="review-block">
          <div className="section-heading compact"><div><span className="shelf-mark"></span><h2>买家评价</h2></div><button onClick={() => navigate('app/reviews')}>全部 {MOCKS.reviews.length * 63} <Icon name="chevron" size={15}/></button></div>
          {MOCKS.reviews.map((review) => (
            <article className="review-item" key={review.id}>
              <div className="review-user"><Avatar name={review.user}/><b>{review.user}</b><span>{review.time}</span></div>
              <p>{review.content}</p>
              {!!review.images?.length&&<div className="review-item-images">{review.images.map((image,index)=><button key={`${review.id}-${index}`} onClick={()=>notify('已打开评价图片预览')}><ProductArt type={image}/></button>)}</div>}
              {!!review.replies?.length&&<div className="review-thread-preview">{review.replies.slice(0,2).map((reply)=><p key={reply.id}><b>{reply.sender==='merchant'?'商家回复':'买家回复'}：</b><span>{reply.content}</span></p>)}{review.replies.length>2&&<button onClick={()=>navigate('app/reviews')}>查看全部 {review.replies.length} 条回复 <Icon name="chevron" size={12}/></button>}</div>}
            </article>
          ))}
        </section>
        <section ref={storySectionRef} className="product-story">
          <div className="section-heading compact"><div><span className="shelf-mark"></span><h2>商品详情</h2></div></div>
          <div className="rich-text-prototype"><ProductArt type="headphone"/><h3>柔和包覆，长时间佩戴也轻松</h3><p>记忆海绵耳罩与轻量化头梁，保持稳定贴合。整段商品介绍由商家富文本统一维护，可连续包含图片、标题、正文和参数说明。</p><ProductArt type="headphone" className="rich-text-detail-art"/><h3>通勤与居家，都保持沉浸聆听</h3><p>高清语音、低延迟连接与长续航满足多场景使用，具体内容以商家实际发布的商品详情为准。</p></div>
        </section>
      </main>
      <div className={`mobile-action-bar ${singleShop ? 'single-shop-action-bar' : ''}`}>
        {!singleShop && <button className="action-icon" onClick={() => navigate('app/shop')}><Icon name="store"/><span>店铺</span></button>}
        <button className="action-icon" onClick={() => navigate('app/service')}><Icon name="message"/><span>客服</span></button>
        <button className="secondary-action" onClick={() => { setPurchaseMode('cart'); setSheet(true); }}>加入购物车</button>
        <button className="primary-action" onClick={() => { setPurchaseMode('buy'); setSheet(true); }}>立即购买</button>
      </div>
      {sheet && (
        <div className="sheet-mask" onClick={() => setSheet(false)}>
          <section className="mobile-sheet product-spec-sheet" onClick={(e) => e.stopPropagation()}>
            <div className="sheet-handle"></div>
            <div className="sku-summary"><ProductArt type={product.art}/><div><b>¥{product.price}</b><span>库存 {product.stock} 件</span><small>已选 {color} · {spec}</small></div><button onClick={() => setSheet(false)}>×</button></div>
            <div className="option-group"><label>颜色</label><div>{product.colors.map((item) => <button key={item} className={color === item ? 'active' : ''} onClick={() => setColor(item)}>{item}</button>)}</div></div>
            <div className="option-group"><label>规格</label><div>{product.specs.map((item) => <button key={item} className={spec === item ? 'active' : ''} onClick={() => setSpec(item)}>{item}</button>)}</div></div>
            <div className="quantity-row"><label>数量</label><QuantityControl value={quantity} setValue={setQuantity}/></div>
            <button className="sheet-confirm" onClick={() => { setSheet(false); purchaseMode === 'cart' ? notify('已加入购物车') : navigate('app/confirm'); }}>{purchaseMode === 'cart' ? '确认加入购物车' : '确认，去结算'}</button>
          </section>
        </div>
      )}
    </div>
  );
}

function AppConfirm({ navigate, notify, singleShop = false }) {
  const shops = singleShop
    ? [{ name: '山野生活', product: MOCKS.products[0], spec: '雾银 · 标准版' }]
    : [
        { name: 'Yeah 数码生活馆', product: MOCKS.products[0], spec: '雾银 · 标准版' },
        { name: '山野生活', product: MOCKS.products[1], spec: '奶油白 · 标准版' }
      ];
  const [coupon, setCoupon] = useState(true);
  const [quantities, setQuantities] = useState({ 0: 1, 1: 1 });
  const [remarks, setRemarks] = useState({});
  const totalQuantity = shops.reduce((sum, _, index) => sum + (quantities[index] || 1), 0);
  const total = shops.reduce((sum, shop, index) => sum + shop.product.price * (quantities[index] || 1), 0);
  const discount = coupon ? 20 : 0;
  return (
    <div className="mobile-screen order-confirm-screen" data-screen-label="App 确认订单">
      <MobileStatusBar tone="surface"/>
      <header className="mobile-page-title"><button onClick={() => navigate('app/product')}><Icon name="back"/></button><h1>确认订单</h1><div className="mobile-header-tools"><WechatCapsule/></div></header>
      <main className="mobile-scroll confirm-scroll">
        <button className="address-panel" onClick={() => navigate('app/addresses')}>
          <span className="address-icon"><Icon name="location"/></span>
          <div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div>
          <Icon name="chevron"/>
        </button>
        {shops.map((shop, index) => <section className="confirm-shop-card" key={shop.name}>
          <div className="shop-line"><Icon name="store" size={17}/><b>{shop.name}</b></div>
          <div className="confirm-product"><ProductArt type={shop.product.art}/><div><b>{shop.product.name}</b><span>{shop.spec}</span><div><strong>¥{shop.product.price}</strong><QuantityControl value={quantities[index] || 1} setValue={(value)=>setQuantities({...quantities,[index]:value})} max={3}/></div></div></div>
          <label className="message-input"><span>订单备注</span><input value={remarks[index] || ''} onChange={(event)=>setRemarks({...remarks,[index]:event.target.value})} placeholder="选填，请先和商家协商一致"/></label>
        </section>)}
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
      <div className="confirm-submit-bar"><div><span>共 {totalQuantity} 件</span><b>¥{(total - discount).toFixed(2)}</b></div><button onClick={() => { notify('订单提交成功'); navigate('app/pay-result'); }}>提交订单</button></div>
    </div>
  );
}

function AppOrder({ navigate, notify, singleShop = false }) {
  const order = MOCKS.orders.find((item)=>item.id==='o-2') || MOCKS.orders[0];
  return (
    <div className="mobile-screen order-detail-screen" data-screen-label="App 订单详情">
      <MobileStatusBar tone="surface"/>
      <header className="mobile-page-title"><button onClick={() => navigate('app/orders')}><Icon name="back"/></button><h1>订单详情</h1><div className="mobile-header-tools"><WechatCapsule/></div></header>
      <main className="mobile-scroll order-detail-scroll order-detail-v2">
        <section className="order-status-v2"><span><Icon name="truck" size={27}/></span><div><small>订单状态</small><h2>商家已发货</h2><p>包裹正在前往杭州转运中心</p></div></section>
        <section className="order-detail-section logistics-section-v2"><button onClick={() => navigate('app/logistics')}><span className="logistics-icon"><Icon name="truck" size={18}/></span><span><small>今天 09:12</small><b>快件已从上海集散中心发出</b><em>顺丰速运 · SF1462903821</em></span><Icon name="chevron" size={16}/></button></section>
        <section className="order-detail-section"><header className="order-section-title"><h2>收货信息</h2></header><div className="order-address-v2"><Icon name="location" size={19}/><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div></div></section>
        <section className="order-detail-section"><header className="order-section-title"><h2>商品信息</h2><span><Icon name="store" size={15}/>{singleShop ? '山野生活' : 'Yeah 美护精选'}</span></header><div className="order-product-v2"><ProductArt type="serum"/><div><b>白茶屏障修护精华</b><span>30ml · 单瓶</span><div><strong>¥168.00</strong><small>×1</small></div></div></div></section>
        <section className="order-detail-section"><header className="order-section-title"><h2>订单进度</h2></header><div className="order-timeline-v2">{[['已发货','今天 08:50'],['支付成功','10月27日 10:02'],['提交订单','10月27日 10:02']].map((item,index) => <div className={index === 0 ? 'active' : ''} key={item[0]}><i></i><b>{item[0]}</b><span>{item[1]}</span></div>)}</div></section>
        <section className="order-detail-section"><header className="order-section-title"><h2>订单金额</h2></header><div className="order-key-values"><div><span>商品总额</span><b>¥168.00</b></div><div><span>优惠金额</span><b>-¥0.00</b></div><div className="total"><span>优惠后应付</span><strong>¥168.00</strong></div><div className="order-actual-payment"><span>实际支付</span><b>¥0.01</b></div></div></section>
        <section className="order-detail-section"><header className="order-section-title"><h2>订单信息</h2></header><div className="order-key-values"><div><span>订单编号</span><span className="order-number-copy"><b className="mono">{order.orderNo}</b><button onClick={() => notify('订单编号已复制')}>复制</button></span></div><div><span>订单类型</span><b>{order.type}</b></div><div><span>订单备注</span><b>工作日请尽快发货</b></div><div><span>下单时间</span><b>{order.createdAt}</b></div><div><span>支付时间</span><b>{order.paidAt}</b></div></div></section>
      </main>
      <div className="order-detail-actions"><button onClick={() => navigate('app/refund')}>申请退款</button><button onClick={() => navigate('app/service')}>联系客服</button><button className="primary" onClick={() => notify('已确认收货')}>确认收货</button></div>
    </div>
  );
}

function SimpleMobileHeader({ title, navigate, back = 'app/home', action, showBack = true, tone = 'surface' }) {
  return <><MobileStatusBar tone={tone}/><header className={`mobile-page-title mobile-page-title-${tone}`}>{showBack?<button onClick={() => navigate(back)}><Icon name="back"/></button>:<span className="mobile-header-spacer"></span>}<h1>{title}</h1><div className="mobile-header-tools">{action}<WechatCapsule/></div></header></>;
}

function AppCategory({ navigate, singleShop = false }) {
  const platformCategoryTree = [
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
  const shopCategoryTree = [
    { id:'home', name:'居家', groups:[
      { id:'living', name:'舒适居家', children:[['chair','休闲座椅'],['chair','桌面好物'],['chair','灯具'],['chair','收纳'],['chair','床品'],['chair','香薰']] },
      { id:'kitchen', name:'厨房餐桌', children:[['coffee','咖啡器具'],['coffee','杯壶'],['coffee','餐具'],['coffee','烘焙'],['coffee','厨房收纳'],['coffee','清洁用品']] }
    ]},
    { id:'food', name:'咖啡', groups:[
      { id:'coffee', name:'咖啡茶饮', children:[['coffee','挂耳咖啡'],['coffee','咖啡豆'],['coffee','原叶茶'],['coffee','冲泡饮品'],['coffee','咖啡礼盒'],['coffee','茶具']] }
    ]},
    { id:'beauty', name:'护理', groups:[
      { id:'care', name:'温和护理', children:[['serum','面部护理'],['serum','身体护理'],['serum','洗发护发'],['serum','香氛'],['serum','清洁'],['serum','护理礼盒']] }
    ]},
    { id:'digital', name:'随身', groups:[
      { id:'portable', name:'随身好物', children:[['headphone','耳机音响'],['headphone','智能穿戴'],['headphone','充电设备'],['headphone','旅行配件'],['headphone','办公配件'],['headphone','户外装备']] }
    ]}
  ];
  const categoryTree = singleShop ? shopCategoryTree : platformCategoryTree;
  const [activePrimary, setActivePrimary] = useState(singleShop ? 'home' : 'digital');
  const [activeGroup, setActiveGroup] = useState(singleShop ? 'living' : 'audio');
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
      <SimpleMobileHeader title="分类" navigate={navigate} showBack={false} tone="paper"/>
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
                {group.children.map(([art, name]) => <button key={name} className="category-leaf-card" onClick={() => navigate(`app/products?category=${currentPrimary.id}`)}><ProductArt type={art} bare/><span>{name}</span></button>)}
              </div> : <EmptyState icon="grid" title="暂未配置子分类" description="该分类下暂时没有可浏览内容" compact/>}
            </div>)}
          </div></> : <EmptyState icon="grid" title="暂未配置二级分类" description="该分类下暂无可浏览内容" fill/>}
        </section>
      </main>
      <MobileBottomNav active="分类" navigate={navigate}/>
    </div>
  );
}

function AppProductList({ navigate, route, singleShop = false }) {
  const category = routeParam(route, 'category');
  const [draftKeyword, setDraftKeyword] = useState('');
  const [keyword, setKeyword] = useState('');
  const [searching, setSearching] = useState(false);
  const [sort, setSort] = useState('综合');
  const [layout, setLayout] = useState('grid');
  const filtered = MOCKS.products.filter((item) => (!category || item.category === category) && item.name.includes(keyword));
  const sorted = [...filtered].sort((a,b) => sort === '价格' ? a.price-b.price : sort === '销量' ? b.sales-a.sales : 0);
  const search = () => {
    if (searching) return;
    setSearching(true);
    window.setTimeout(() => {
      setKeyword(draftKeyword.trim());
      setSearching(false);
    }, 360);
  };
  return <div className="mobile-screen" data-screen-label={`${singleShop?'单店 App':'App'} 商品列表`}>
    <SimpleMobileHeader title={singleShop?'店内商品':'商品列表'} navigate={navigate}/>
    <main className="mobile-scroll product-list-page">
      <div className="product-list-search">
        <label className="list-search">
          <Icon name="search" size={17}/>
          <input
            value={draftKeyword}
            onChange={(event) => setDraftKeyword(event.target.value)}
            onKeyDown={(event) => event.key === 'Enter' && search()}
            placeholder={singleShop?'搜索店内商品':'搜索商品'}
          />
        </label>
        <button disabled={searching} onClick={search}>{searching ? '搜索中' : '搜索'}</button>
      </div>
      <div className="sort-row">
        {['综合','销量','价格'].map((item) => <button key={item} className={sort === item ? 'active' : ''} onClick={() => setSort(item)}>{item}</button>)}
        <button className="layout-toggle" aria-label={layout === 'grid' ? '当前双列，切换为单列' : '当前单列，切换为双列'} onClick={()=>setLayout(layout==='grid'?'list':'grid')}>
          <Icon name={layout === 'grid' ? 'grid' : 'list'} size={16}/>
        </button>
      </div>
      <div className={`product-grid list-grid ${layout==='list'?'single-column':''}`}>
        {sorted.map((item) => <button className="product-card" key={item.id} onClick={() => navigate('app/product')}><ProductArt type={item.art}/><span className="product-card-name">{item.name}</span><span className="product-card-sub">{item.subtitle}</span><span className="product-card-meta"><b>¥{item.price}</b><small>已售 {item.sales}</small></span></button>)}
      </div>
      {!sorted.length && <EmptyState icon="search" title="没有找到相关商品" description="换个关键词或店内分类试试" fill/>}
      {!!sorted.length&&<ListBoundary/>}
    </main>
  </div>;
}

function AppCart({ navigate, notify, singleShop = false }) {
  const [items, setItems] = useState(MOCKS.products.slice(0,3).map((item,index) => ({...item, checked:index < 2, quantity:1})));
  const shops = singleShop ? [
    { id:'single-shop', name:'山野生活', itemIds:items.map((item)=>item.id) }
  ] : [
    { id:'digital', name:'Yeah 数码生活馆', itemIds:['p-1001','p-1002'] },
    { id:'beauty', name:'白茶美护精选', itemIds:['p-1003'] }
  ];
  const update = (id, edits) => setItems(items.map((item) => item.id === id ? {...item,...edits} : item));
  const updateShop = (shop, checked) => setItems(items.map((item) => shop.itemIds.includes(item.id) ? {...item, checked} : item));
  const checked = items.filter((item) => item.checked);
  const total = checked.reduce((sum,item) => sum + item.price*item.quantity,0);
  return <div className="mobile-screen" data-screen-label={singleShop ? '单店 App 购物车' : 'App 购物车'}><SimpleMobileHeader title="购物车" navigate={navigate} showBack={false} tone="paper"/><main className={`mobile-scroll cart-page ${singleShop ? 'single-shop-cart-page' : ''}`}>{shops.map((shop) => { const shopItems = items.filter((item) => shop.itemIds.includes(item.id)); const shopSelected = shopItems.every((item) => item.checked); return <section className={`cart-shop-group ${singleShop ? 'single-shop-cart-group' : ''}`} key={shop.id}>{!singleShop&&<header><button className={`check-dot ${shopSelected ? 'active' : ''}`} onClick={() => updateShop(shop,!shopSelected)}>{shopSelected && <Icon name="check" size={13}/>}</button><Icon name="storefront" size={18} strokeWidth={1.8} className="cart-shop-icon"/><b>{shop.name}</b><small>共 {shopItems.length} 件商品</small></header>}{shopItems.map((item) => <article className="cart-item" key={item.id}><button className={`check-dot ${item.checked ? 'active' : ''}`} onClick={() => update(item.id,{checked:!item.checked})}>{item.checked && <Icon name="check" size={13}/>}</button><ProductArt type={item.art}/><div><b>{item.name}</b><span>{item.colors[0]} · {item.specs[0]}</span><strong>¥{item.price}</strong><QuantityControl value={item.quantity} setValue={(value) => update(item.id,{quantity:value})} max={5}/></div></article>)}</section>})}</main><div className="cart-submit"><button className={`check-dot ${checked.length === items.length ? 'active' : ''}`} onClick={() => setItems(items.map((item) => ({...item,checked:checked.length !== items.length})))}>{checked.length === items.length && <Icon name="check" size={13}/>}</button><span>全选</span><div><small>合计</small><b>¥{total.toFixed(2)}</b></div><button disabled={!checked.length} onClick={() => checked.length ? navigate('app/confirm') : notify('请先选择商品')}>结算（{checked.length}）</button></div><MobileBottomNav active="购物车" navigate={navigate}/></div>;
}

function SingleShopCart(props) {
  return <AppCart {...props} singleShop/>;
}

function AppOrders({ navigate, route, notify }) {
  const requestedTab = routeParam(route, 'tab');
  const [tab, setTab] = useState(['待评价','已评价'].includes(requestedTab) ? '评价' : (requestedTab || '全部'));
  const [reviewTab,setReviewTab] = useState(requestedTab === '已评价' ? '已评价' : '待评价');
  const tabs = ['全部','待付款','待发货','待收货','退款/售后','评价'];
  const forceEmpty = routeParam(route, 'state') === 'empty';
  const visible = forceEmpty ? [] : MOCKS.orders.filter((item) => tab === '全部' || (tab === '评价' ? item.status === reviewTab : (tab === '退款/售后' ? item.status === '退款中' : item.status === tab)));
  const emptyStatus = tab === '评价' ? reviewTab : tab;
  const emptyTitle = tab === '全部' ? '还没有订单' : `暂无${emptyStatus}订单`;
  const emptyDescription = tab === '全部' ? '选购喜欢的商品，订单进度会展示在这里' : `当前没有${emptyStatus}订单，可以查看其他订单状态`;
  const actionsFor = (order) => {
    const actions = [{label:'查看详情',value:'detail'}];
    if(order.status==='待付款') actions.push({label:'取消订单',value:'cancel'},{label:'付款',value:'pay',primary:true});
    if(order.status==='待发货') actions.push({label:'申请退款',value:'refund'});
    if(order.status==='待收货') actions.push({label:'查看物流',value:'logistics'},{label:'申请退款',value:'refund'},{label:'确认收货',value:'receive',primary:true});
    if(order.status==='退款中') actions.push({label:'退款进度',value:'refund-detail',primary:true});
    if(order.status==='待评价') actions.push({label:'申请退款',value:'refund'},{label:'去评价',value:'review',primary:true});
    if(order.status==='已评价') actions.push({label:'查看评价',value:'review-detail'});
    if(order.status==='已评价') actions.push({label:'查看评价',value:'review-detail',primary:true});
    return actions;
  };
  const act = (action,order) => {
    if(action==='detail') return navigate('app/order');
    if(action==='refund') return navigate('app/refund');
    if(action==='logistics') return navigate('app/logistics');
    if(action==='refund-detail') return navigate('app/refund-detail');
    if(action==='pay') return navigate('app/pay-result');
    if(action==='review') return navigate('app/review-form');
    if(action==='review-detail') return navigate('app/review-detail');
    notify(action==='cancel'?'订单已取消':'已确认收货');
  };
  return <div className="mobile-screen" data-screen-label="App 订单列表">
    <SimpleMobileHeader title="我的订单" navigate={navigate} back="app/profile"/>
    <div className="order-tabs">{tabs.map((item) => <button key={item} className={tab === item ? 'active' : ''} onClick={() => setTab(item)}>{item}</button>)}</div>
    {tab==='评价'&&<div className="order-review-tabs"><button className={reviewTab==='待评价'?'active':''} onClick={()=>setReviewTab('待评价')}>待评价</button><button className={reviewTab==='已评价'?'active':''} onClick={()=>setReviewTab('已评价')}>已评价</button></div>}
    <main className={`mobile-scroll mobile-order-list order-list-v2 ${tab==='评价'?'has-review-tabs':''}`}>
      {visible.map((order) => {
        const products = order.products || [{id:order.id,name:order.product,spec:order.type,price:order.finalAmount,quantity:order.quantity,art:'chair'}];
        const totalQuantity = products.reduce((sum,item)=>sum+item.quantity,0);
        return <article className="mobile-order-card order-card-v2" key={order.id}>
          <header className="order-card-head"><span className="order-card-shop"><Icon name="store" size={16}/><b>{order.shop}</b></span><StatusTicket tone={statusTone(order.status)}>{order.status}</StatusTicket></header>
          <div className="order-card-meta"><span className="mono">{order.orderNo}</span><StatusTicket tone={order.type==='秒杀订单'?'danger':order.type==='拼团订单'?'success':'neutral'} small>{order.type}</StatusTicket></div>
          <div className="order-product-list">{products.map((product)=><button className="order-product-row" key={product.id} onClick={()=>navigate('app/order')}><ProductArt type={product.art}/><span><b>{product.name}</b><small>{product.spec}</small><em><strong>¥{product.price.toFixed(2)}</strong><i>×{product.quantity}</i></em></span></button>)}</div>
          <div className="order-card-summary"><span>共 {totalQuantity} 件</span><span>商品总额 ¥{order.amount.toFixed(2)}</span>{order.discount>0&&<span className="saving">优惠 -¥{order.discount.toFixed(2)}</span>}<b>应付 ¥{order.finalAmount.toFixed(2)}</b></div>
          <footer className="order-card-footer"><span>{order.createdAt}</span><div>{actionsFor(order).map((action)=><button className={action.primary?'primary':''} key={action.value} onClick={()=>act(action.value,order)}>{action.label}</button>)}</div></footer>
        </article>;
      })}
      {!visible.length&&<EmptyState icon="order" title={emptyTitle} description={emptyDescription} actionLabel={tab==='全部'?'去逛逛':'查看全部订单'} onAction={()=>tab==='全部'?navigate('app/home'):setTab('全部')} fill/>}
      {!!visible.length&&<ListBoundary text="没有更多订单了"/>}
    </main>
  </div>;
}

function AppProfile({ navigate }) {
  const orderLinks = [['order','待付款','待付款'],['box','待发货','待发货'],['truck','待收货','待收货'],['message','待评价','待评价'],['refund','退款/售后','退款/售后']];
  const menuGroups = [
    [['heart','我的收藏','app/favorites'],['clock','浏览足迹','app/history'],['ticket','我的优惠券','app/coupons'],['diamond','我的积分','app/points'],['location','地址管理','app/addresses']],
    [['message','客服中心','app/service'],['spark','意见反馈','app/feedback'],['settings','设置','app/settings']]
  ];
  return <div className="mobile-screen profile-screen" data-screen-label="App 个人中心"><SimpleMobileHeader title="我的" navigate={navigate} showBack={false} tone="dark"/><main className="mobile-scroll with-bottom-nav"><section className="profile-header" onClick={() => navigate('app/profile-edit')}><Avatar name="林可" size={54}/><div><h1>林可</h1><span>会员 · 7Qm2K9xL</span></div><button onClick={(event)=>{event.stopPropagation();navigate('app/profile-edit');}}><Icon name="chevron"/></button></section><section className="profile-order-panel"><header><b>我的订单</b><button onClick={() => navigate('app/orders')}>全部订单 <Icon name="chevron" size={14}/></button></header><div>{orderLinks.map(([icon,label,targetTab]) => <button key={label} onClick={() => navigate(`app/orders?tab=${encodeURIComponent(targetTab)}`)}><span><Icon name={icon} size={19}/>{label === '待发货' && <i>1</i>}{label === '待评价' && <i>2</i>}</span><small>{label}</small></button>)}</div></section>{menuGroups.map((menus,index)=><section className="profile-menu" key={index}>{menus.map(([icon,label,target]) => <button key={label} onClick={() => navigate(target)}><span><Icon name={icon} size={18}/>{label}</span><Icon name="chevron" size={15}/></button>)}</section>)}</main><MobileBottomNav active="我的" navigate={navigate}/></div>;
}

function AppCollection({ navigate, mode }) {
  const title = mode === 'favorites' ? '我的收藏' : '浏览足迹';
  const [favoriteItems,setFavoriteItems] = useState(MOCKS.products);
  const [historyItems,setHistoryItems] = useState(MOCKS.products);
  const [clearConfirm,setClearConfirm] = useState(false);
  const rows = mode === 'favorites' ? favoriteItems : historyItems;
  return <div className="mobile-screen" data-screen-label={`App ${title}`}><SimpleMobileHeader title={title} navigate={navigate} back="app/profile"/><main className="mobile-scroll collection-page">{mode === 'history' && !!rows.length && <div className="history-toolbar"><span>今天 · 浏览 {rows.length} 件商品</span><button onClick={()=>setClearConfirm(true)}>清空</button></div>}{!!rows.length?<><div className="product-grid list-grid">{rows.map((item,index) => <button className="product-card" key={item.id} onClick={() => navigate('app/product')}><ProductArt type={item.art}/>{mode === 'favorites' ? <span className="collection-favorite-action" role="button" aria-label="取消收藏" onClick={(event)=>{event.stopPropagation();setFavoriteItems(favoriteItems.filter((row)=>row.id!==item.id));}}><Icon name="heart" size={14}/></span> : <span className="collection-history-action" role="button" aria-label="删除浏览记录" onClick={(event)=>{event.stopPropagation();setHistoryItems(historyItems.filter((row)=>row.id!==item.id));}}><Icon name="close" size={13}/></span>}<span className="product-card-name">{item.name}</span><span className="product-card-meta"><b>¥{item.price}</b><small>{mode === 'favorites' ? `${item.favoriteCount} 人收藏` : `10:${String(Math.max(0,26-index)).padStart(2,'0')}`}</small></span></button>)}</div><ListBoundary text={mode==='favorites'?'没有更多收藏了':'没有更多浏览记录了'}/></>:<EmptyState icon={mode==='favorites'?'heart':'clock'} title={mode==='favorites'?'暂未收藏商品':'暂无浏览足迹'} description={mode==='favorites'?'遇到喜欢的商品时，可以先收藏起来':'浏览过的商品会按时间记录在这里'} fill/>}</main>{clearConfirm&&<div className="sheet-mask" onClick={()=>setClearConfirm(false)}><section className="mobile-sheet history-clear-sheet" onClick={(event)=>event.stopPropagation()}><div className="sheet-handle"></div><h2>清空浏览足迹？</h2><p>全部浏览记录将被移除，清空后无法恢复。</p><div><button onClick={()=>setClearConfirm(false)}>取消</button><button className="danger" onClick={()=>{setHistoryItems([]);setClearConfirm(false);}}>确认清空</button></div></section></div>}</div>;
}

function AppCouponCard({ coupon, state = 'available', singleShop = false, center = false, claimed = false, onAction }) {
  const statusText = state === 'used' ? '已使用' : state === 'expired' ? '已失效' : claimed ? '已领取' : center ? '领取' : '去使用';
  const disabled = state !== 'available' || claimed;
  return <article className={`coupon-card coupon-card-v2 state-${state}${disabled?' is-disabled':''}`}>
    <div className="coupon-value"><b>{coupon.value}</b><span>{coupon.condition}</span></div>
    <div className="coupon-copy">
      <div className="coupon-title-line"><b>{coupon.name}</b><em>{coupon.useRangeText}</em></div>
      <span className="coupon-shop"><Icon name="store" size={12}/>{singleShop?'山野生活':coupon.shop}</span>
      <div className="coupon-tag-line">{coupon.supportGroupBuy&&<i>支持拼团</i>}{center&&<small>剩余 {coupon.remaining} 张 · 每人限领 {coupon.perLimit} 张</small>}</div>
      <small>{state==='used'?'使用时间 2026-10-26 19:36':`有效期至 ${coupon.endAt}`}</small>
    </div>
    <div className="coupon-card-action"><button disabled={disabled} onClick={onAction}>{statusText}</button></div>
  </article>;
}

function AppCoupons({ navigate, notify, singleShop = false }) {
  const [tab,setTab] = useState('可使用');
  const rows = tab==='已使用'?[MOCKS.coupons[1]]:tab==='已失效'?[{...MOCKS.coupons[2],endAt:'2026-10-20'}]:MOCKS.coupons;
  const state = tab==='已使用'?'used':tab==='已失效'?'expired':'available';
  return <div className="mobile-screen" data-screen-label="App 我的优惠券"><SimpleMobileHeader title="我的优惠券" navigate={navigate} back="app/profile"/><button className="coupon-center-entry" onClick={()=>navigate('app/coupon-center')}><span className="coupon-center-entry-icon"><Icon name="ticket" size={17}/></span><span><b>{singleShop?'领取店铺优惠券':'更多店铺好券等你领'}</b><small>领取后可在确认订单时选择使用</small></span><Icon name="chevron" size={15}/></button><div className="coupon-tabs">{['可使用','已使用','已失效'].map((item) => <button key={item} className={tab === item ? 'active' : ''} onClick={() => setTab(item)}>{item}</button>)}</div><main className="mobile-scroll coupon-page coupon-page-v2 owned-coupon-page">{rows.map((coupon) => <AppCouponCard key={`${state}-${coupon.id}`} coupon={coupon} state={state} singleShop={singleShop} onAction={()=>state==='available'&&navigate('app/products')}/>)}{!!rows.length&&<ListBoundary text="没有更多优惠券了"/>}</main></div>;
}

function AppCouponCenter({ navigate, notify, singleShop = false }) {
  const [claimed,setClaimed] = useState([]);
  const [range,setRange] = useState(0);
  const ranges = [[0,'全部'],[1,'店铺通用'],[2,'指定商品'],[3,'指定品类']];
  const rows = range===0?MOCKS.coupons:MOCKS.coupons.filter((coupon)=>coupon.useRange===range);
  return <div className="mobile-screen" data-screen-label="App 领券中心"><SimpleMobileHeader title="领券中心" navigate={navigate} back="app/coupons"/><main className="mobile-scroll coupon-page coupon-center-page coupon-page-v2"><div className="coupon-center-note coupon-center-note-v2"><span className="coupon-center-icon"><Icon name="ticket" size={19}/></span><span><b>{singleShop?'山野生活好券':'全场好券'}</b><small>领取后可在确认订单时选择使用</small></span></div><div className="coupon-range-tabs">{ranges.map(([value,label])=><button key={value} className={range===value?'active':''} onClick={()=>setRange(value)}>{label}</button>)}</div>{rows.map((coupon)=>{const isClaimed=claimed.includes(coupon.id)||coupon.receivedCount>=coupon.perLimit;return <AppCouponCard key={coupon.id} coupon={coupon} singleShop={singleShop} center claimed={isClaimed} onAction={()=>{if(isClaimed)return;setClaimed([...claimed,coupon.id]);notify('领取成功，已放入我的优惠券');}}/>;})}{!!rows.length&&<ListBoundary text="本页优惠券已全部展示"/>}</main></div>;
}

function AppPoints({ navigate, singleShop = false }) {
  const [type,setType] = useState('全部');
  const records = [['订单完成赠送','+399','2026-10-24 09:18:42'],['完成评价赠送','+10','2026-10-23 14:20:18'],['积分商品兑换','-300','2026-10-20 10:36:05'],[singleShop?'店铺活动奖励':'平台活动奖励','+50','2026-10-18 16:08:33']];
  const visible = records.filter((row)=>type==='全部'||(type==='收入'?row[1][0]==='+':row[1][0]==='-'));
  const cycleType = () => setType(type==='全部'?'收入':type==='收入'?'支出':'全部');
  return <div className="mobile-screen" data-screen-label="App 我的积分"><SimpleMobileHeader title="我的积分" navigate={navigate} back="app/profile"/><button className="coupon-center-entry points-mall-entry" onClick={()=>navigate('app/points-mall')}><span className="coupon-center-entry-icon"><Icon name="spark" size={17}/></span><span><b>积分商城好物等你兑</b><small>积分可兑换好物、优惠券与会员权益</small></span><Icon name="chevron" size={15}/></button><main className="mobile-scroll points-page points-page-with-entry"><section className="points-balance"><span>可用积分</span><b>1,280</b><div><span>累计获得 2,330</span><span>累计使用 1,040</span></div></section><section className="points-records account-records"><div className="points-record-title"><h3>积分明细</h3><button onClick={cycleType}>{type} <Icon name="down" size={14}/></button></div>{visible.map((row) => <div key={row[0]}><span><b>{row[0]}</b><small>{row[2]}</small></span><strong className={row[1][0] === '+' ? 'plus' : 'minus'}>{row[1]}</strong></div>)}{!!visible.length&&<ListBoundary text="没有更多积分记录了"/>}</section></main></div>;
}

function AppPointsMall({ navigate }) {
  const [category,setCategory] = useState('全部');
  const products = MOCKS.pointsProducts.filter((item)=>category==='全部'||item.type===category);
  return <div className="mobile-screen" data-screen-label="App 积分商城"><SimpleMobileHeader title="积分商城" navigate={navigate} back="app/home" action={<button onClick={()=>navigate('app/points-orders')}>订单</button>}/><main className="mobile-scroll points-mall-page"><div className="points-mall-tabs">{['全部','实物商品','优惠券','虚拟权益'].map((item)=><button key={item} className={category===item?'active':''} onClick={()=>setCategory(item)}>{item}</button>)}</div><div className="points-product-list">{products.map((item) => <article key={item.id}><ProductArt type={item.art}/><div><StatusTicket small>{item.type}</StatusTicket><b>{item.name}</b><span>{item.points} 积分 · 库存 {item.stock}</span></div><button onClick={() => navigate('app/points-product')}>查看</button></article>)}</div>{!!products.length&&<ListBoundary text="积分商品已全部展示"/>}</main></div>;
}

function AppActivity({ navigate, type, singleShop = false }) {
  const isSeckill = type === 'seckill';
  const sourceRows = isSeckill ? MOCKS.activities.seckill : MOCKS.activities.group;
  const rows = singleShop ? sourceRows.map((row,index)=>({...row,title:isSeckill?['山野晚八点闪购','周末会员专享'][index]||row.title:['山野生活双人团','咖啡好友三人团'][index]||row.title})) : sourceRows;
  const tabs = isSeckill?['10:00','14:00','20:00']:['全部活动','报名中','进行中'];
  const [active,setActive] = useState(tabs[0]);
  return <div className="mobile-screen" data-screen-label={`App ${isSeckill?'秒杀':'拼团'}`}><SimpleMobileHeader title={isSeckill?'秒杀活动':'拼团活动'} navigate={navigate}/><main className="mobile-scroll activity-page"><div className="activity-tabs">{tabs.map((item,index) => <button className={active===item?'active':''} onClick={()=>setActive(item)} key={item}>{item}<small>{index===0?'进行中':'即将开始'}</small></button>)}</div>{rows.map((row) => <article className="activity-card" key={row.id}><ProductArt type={row.art}/><div><StatusTicket tone={row.status.includes('进行')||row.status.includes('抢购')?'danger':'neutral'} small>{row.status}</StatusTicket><h3>{row.title}</h3><span>{row.time}</span><b>¥{row.price}</b><small>{isSeckill?`剩余 ${row.stock} 件`:row.people}</small></div><button onClick={() => navigate(isSeckill?'app/seckill-products':'app/groupbuy-products')}>{isSeckill?'查看商品':'查看拼团'}</button></article>)}{!!rows.length&&<ListBoundary text="没有更多活动了"/>}</main></div>;
}

function AppService({ navigate, notify, route = '', singleShop = false }) {
  const platformConversations = [
    ...MOCKS.conversations,
    { id:'cv-3', name:'白茶美护精选', preview:'图片已经收到，我们为您核实。', time:'周一' },
    { id:'cv-4', name:'木作家居馆', preview:'安装视频已发送。', time:'周日' },
    { id:'cv-5', name:'山海食品铺', preview:'订单预计明天发出。', time:'10月25日' },
    { id:'cv-6', name:'轻行户外', preview:'售后申请正在处理中。', time:'10月23日' },
    { id:'cv-7', name:'童趣生活馆', preview:'尺码表请查看附件。', time:'10月20日' },
    { id:'cv-8', name:'纸间文具', preview:'感谢您的咨询。', time:'10月18日' }
  ];
  const conversations = singleShop ? [{...MOCKS.conversations[0],name:'山野生活'}] : platformConversations;
  const requestedConversation = routeParam(route,'shop');
  const initialConversation = conversations.find((item)=>item.id===requestedConversation) || conversations[0];
  const initialMessages = {
    'cv-1':[{side:'them',kind:'text',text:'您好，请问商品使用中遇到了什么问题？',time:'10:18'},{side:'me',kind:'text',text:'想确认一下今天可以发货吗？',time:'10:21'},{side:'them',kind:'text',text:'您好，订单今天可以发出。',time:'10:24'}],
    'cv-2':[{side:'them',kind:'text',text:'您好，咖啡豆烘焙日期是本周一。',time:'昨天 16:42'},{side:'me',kind:'file',name:'订单信息.pdf',meta:'PDF · 248 KB',time:'昨天 16:45'}],
    'cv-3':[{side:'me',kind:'image',art:'serum',time:'周一 14:06'},{side:'them',kind:'text',text:'图片已经收到，我们为您核实。',time:'周一 14:08'}],
    'cv-4':[{side:'them',kind:'video',name:'安装说明视频',meta:'00:42 · 8.6 MB',time:'周日 09:32'}]
  };
  const [activeId,setActiveId] = useState(initialConversation.id);
  const [messageMap,setMessageMap] = useState(initialMessages);
  const [text,setText] = useState('');
  const [attachmentOpen,setAttachmentOpen] = useState(false);
  const [conversationSheet,setConversationSheet] = useState(false);
  const [conversationKeyword,setConversationKeyword] = useState('');
  const active = conversations.find((item)=>item.id===activeId) || conversations[0];
  const messages = messageMap[active.id] || [{side:'them',kind:'text',text:`您好，这里是${active.name}客服，请问有什么可以帮您？`,time:'最近'}];
  const visibleConversations = conversations.filter((item)=>item.name.includes(conversationKeyword.trim()));
  const selectConversation = (item) => { setActiveId(item.id); setAttachmentOpen(false); setConversationSheet(false); };
  const currentMessageTime = () => new Date().toLocaleTimeString('zh-CN',{hour:'2-digit',minute:'2-digit'});
  const appendMessage = (message) => setMessageMap({...messageMap,[active.id]:[...messages,{...message,time:message.time||currentMessageTime()}]});
  const send = () => {if(!text.trim()) return;appendMessage({side:'me',kind:'text',text:text.trim()});setText('');};
  const sendAttachment = (kind) => {
    const payload = kind==='image' ? {side:'me',kind,art:'headphone'} : kind==='video' ? {side:'me',kind,name:'商品问题视频.mp4',meta:'00:18 · 3.2 MB'} : {side:'me',kind,name:'售后凭证.pdf',meta:'PDF · 326 KB'};
    appendMessage(payload);
    setAttachmentOpen(false);
    notify(`${kind==='image'?'图片':kind==='video'?'视频':'文件'}已发送`);
  };
  const renderMessage = (msg) => {
    if(msg.kind==='image') return <span className="chat-image-message"><ProductArt type={msg.art||'headphone'}/><small>点击查看大图</small></span>;
    if(msg.kind==='video') return <span className="chat-media-message"><i><Icon name="video" size={22}/></i><b>{msg.name}</b><small>{msg.meta}</small></span>;
    if(msg.kind==='file') return <span className="chat-media-message"><i><Icon name="file" size={22}/></i><b>{msg.name}</b><small>{msg.meta}</small></span>;
    return msg.text;
  };
  const recentConversationIds = conversations.slice(0,singleShop?1:6).map((item)=>item.id);
  return <div className="mobile-screen" data-screen-label="App 客服"><SimpleMobileHeader title={active.name} navigate={navigate} back="app/profile"/><main className={`service-page service-page-v2 ${singleShop?'single-shop-service-page':''}`}><div className={`service-conversation-nav ${singleShop?'single-shop-conversation-strip':''}`}><div className="conversation-strip">{conversations.slice(0,singleShop?1:6).map((item) => <button key={item.id} className={active.id===item.id?'active':''} onClick={() => selectConversation(item)}><Avatar name={item.name}/><span>{item.name.replace('Yeah ','')}</span></button>)}</div>{!singleShop&&<button className={`all-conversations ${recentConversationIds.includes(active.id)?'':'active'}`} onClick={()=>setConversationSheet(true)}><Icon name="more" size={18}/><span>全部</span></button>}</div><div className="chat-stream"><div className="chat-history-boundary">没有更早消息</div>{messages.map((msg,index) => <div key={`${active.id}-${index}`} className={`chat-message ${msg.side}`}><div className={`chat-bubble ${msg.side} chat-kind-${msg.kind||'text'}`}>{renderMessage(msg)}</div><time>{msg.time}</time></div>)}</div>{attachmentOpen&&<div className="chat-attachment-panel">{[['image','图片'],['video','视频'],['file','文件']].map(([icon,label])=><button key={label} onClick={()=>sendAttachment(icon)}><span><Icon name={icon} size={21}/></span><b>{label}</b></button>)}</div>}<div className="chat-input"><button className={attachmentOpen?'active':''} onClick={()=>setAttachmentOpen(!attachmentOpen)}><Icon name={attachmentOpen?'minus':'plus'}/></button><input value={text} onChange={(e)=>setText(e.target.value)} onKeyDown={(e)=>e.key==='Enter'&&send()} placeholder="输入消息"/><button className="send" onClick={send}>发送</button></div></main>{conversationSheet&&<div className="sheet-mask" onClick={()=>setConversationSheet(false)}><section className="mobile-sheet conversation-sheet" onClick={(event)=>event.stopPropagation()}><div className="sheet-handle"></div><header><div><small>{conversations.length} 家店铺</small><h2>全部店铺会话</h2></div><button onClick={()=>setConversationSheet(false)}>×</button></header><label><Icon name="search" size={16}/><input value={conversationKeyword} onChange={(event)=>setConversationKeyword(event.target.value)} placeholder="输入店铺名称，实时筛选"/>{conversationKeyword&&<button onClick={()=>setConversationKeyword('')}>×</button>}</label><div>{visibleConversations.length?visibleConversations.map((item)=><button key={item.id} className={active.id===item.id?'active':''} onClick={()=>selectConversation(item)}><Avatar name={item.name}/><span><b>{item.name}</b><small>{item.preview}</small></span><time>{item.time}</time></button>):<EmptyState icon="search" title="没有找到相关店铺" description="换个关键词试试" compact/>}</div></section></div>}</div>;
}

function AppAddresses({ navigate, notify }) {
  const [addresses, setAddresses] = useState(MOCKS.addresses);
  const setDefault = (id) => {
    setAddresses(addresses.map((item) => ({ ...item, isDefault: item.id === id })));
    notify('已设为默认');
  };
  const removeAddress = (id) => {
    setAddresses(addresses.filter((item) => item.id !== id));
    notify('地址已删除');
  };
  return <div className="mobile-screen" data-screen-label="App 地址管理"><SimpleMobileHeader title="地址管理" navigate={navigate} back="app/profile"/><main className="mobile-scroll address-list-page">{addresses.length ? addresses.map((item) => <article key={item.id}><header><b>{item.name}</b><span>{item.mobile}</span>{item.isDefault&&<StatusTicket tone="success" small>默认</StatusTicket>}</header><p>{item.address}</p><footer>{!item.isDefault&&<button onClick={()=>setDefault(item.id)}>设为默认</button>}<button onClick={()=>navigate('app/address-edit?mode=edit')}>编辑</button><button className="address-delete-action" onClick={()=>removeAddress(item.id)}>删除</button></footer></article>) : <EmptyState icon="location" title="暂无收货地址" description="新增地址后可用于下单配送" fill/>}</main><div className="single-bottom-action address-create-bar"><button onClick={()=>navigate('app/address-edit?mode=create')}>新增地址</button></div></div>;
}

function AppReviews({ navigate }) {
  const [filter,setFilter] = useState('全部');
  const [preview,setPreview] = useState(null);
  const rows = [...MOCKS.reviews,...MOCKS.reviews].map((review,index)=>({...review,index}));
  const visible = rows.filter((review)=>filter==='全部'||(filter==='有图'&&review.images?.length)||(filter==='好评'&&review.rating>=4));
  return <div className="mobile-screen" data-screen-label="App 商品评价">
    <SimpleMobileHeader title="商品评价" navigate={navigate} back="app/product"/>
    <main className="mobile-scroll reviews-page reviews-page-v2">
      <section className="review-summary review-summary-v2">
        <div className="review-rate"><b>98<small>%</small></b><span>好评率</span></div>
        <div className="review-stat"><strong>126</strong><small>全部评价</small></div>
        <div className="review-stat"><strong>42</strong><small>有图评价</small></div>
      </section>
      <div className="review-filter review-filter-v2">{['全部','有图','好评'].map((item)=><button key={item} className={filter===item?'active':''} onClick={()=>setFilter(item)}>{item}</button>)}</div>
      <section className="review-list-v2">
        {visible.map((review)=><article className="review-full-item review-full-item-v2" key={`${review.id}-${review.index}`}>
          <header><Avatar name={review.user}/><b>{review.user}</b><time>{review.time}</time></header>
          <div className="stars" aria-label={`${review.rating}星`}>{'★'.repeat(review.rating)}{'☆'.repeat(5-review.rating)}</div>
          <p>{review.content}</p>
          {!!review.images?.length&&<div className="review-image-row">{review.images.map((image,imageIndex)=><button key={`${review.id}-${imageIndex}`} onClick={()=>setPreview(image)}><ProductArt type={image}/></button>)}</div>}
          {!!review.replies?.length&&<div className="review-thread-full">{review.replies.map((reply)=><div className={reply.sender} key={reply.id}><p><b>{reply.sender==='merchant'?'商家回复':'买家回复'}：</b><span>{reply.content}</span></p><time>{reply.time}</time></div>)}</div>}
          <small>规格：雾银 · 标准版</small>
        </article>)}
      </section>
      {!visible.length&&<EmptyState icon="message" title="暂无相关评价" description="切换筛选条件后再看看" fill/>}
      {!!visible.length&&<ListBoundary text="没有更多评价了"/>}
    </main>
    {preview&&<div className="image-preview-mask" onClick={()=>setPreview(null)}><button onClick={()=>setPreview(null)}>×</button><ProductArt type={preview}/><span>点击任意位置关闭</span></div>}
  </div>;
}

function AppFeedback({ navigate, notify, singleShop = false }) {
  const [content,setContent] = useState('');
  const [contact,setContact] = useState('');
  const [images,setImages] = useState([]);
  const [records,setRecords] = useState([
    {id:'f-1',status:'2',time:'2026-10-28 16:20',content:'商品详情里的规格切换后，希望价格变化能更明显。',images:['headphone'],reply:'我们正在优化规格与价格联动的视觉提示。',replyTime:'2026-10-29 09:40'},
    {id:'f-2',status:'3',time:'2026-10-22 11:08',content:'希望订单详情的信息层级更清晰。',images:[],reply:'已完成订单详情的信息层级优化，感谢你的建议。',replyTime:'2026-10-23 15:16'}
  ]);
  const statusMeta = { '1':['待处理','warning'], '2':['处理中','info'], '3':['已解决','success'] };
  const addImage = () => {
    if (images.length >= 6) return notify('反馈图片最多上传 6 张');
    const options = ['serum','chair','coffee','headphone'];
    setImages([...images,options[images.length % options.length]]);
    notify('已添加一张模拟图片');
  };
  const submit = () => {
    const value = content.trim();
    if (!value) return notify('请填写反馈内容');
    setRecords([{id:`f-${Date.now()}`,status:'1',time:'刚刚',content:value,images:[...images],reply:'',replyTime:''},...records]);
    setContent(''); setContact(''); setImages([]); notify('反馈已提交');
  };
  return <div className="mobile-screen" data-screen-label="App 意见反馈"><SimpleMobileHeader title="意见反馈" navigate={navigate} back="app/profile"/><main className="mobile-scroll feedback-page-proto feedback-page-v2"><section className="feedback-compose"><header><div><h2>提交意见反馈</h2><p>问题与建议将提交给平台运营团队</p></div></header><div className="feedback-textarea-shell"><textarea value={content} maxLength="500" onChange={(event)=>setContent(event.target.value)} placeholder="请描述你遇到的问题或建议，我们会认真处理"></textarea><small>{content.length}/500</small></div><div className="feedback-field-head"><b>反馈图片</b><span>选填，最多 6 张</span></div><div className="feedback-upload-grid">{images.map((image,index)=><button className="feedback-image-item" key={`${image}-${index}`} onClick={()=>setImages(images.filter((_,itemIndex)=>itemIndex!==index))}><ProductArt type={image}/><i>×</i></button>)}{images.length<6&&<button className="feedback-image-add" onClick={addImage}><Icon name="plus" size={18}/><span>添加图片</span></button>}</div><label className="feedback-contact"><span>联系方式 <small>选填</small></span><input value={contact} maxLength="50" onChange={(event)=>setContact(event.target.value)} placeholder="手机号、微信或邮箱，方便我们联系你"/></label><button className="feedback-submit" onClick={submit}>提交反馈</button></section><div className="feedback-history-head"><h3>我的反馈</h3><span>{records.length} 条</span></div><section className="feedback-history-list">{records.map((item)=>{const [label,tone]=statusMeta[item.status];return <article className="feedback-record-v2" key={item.id}><header><StatusTicket tone={tone} small>{label}</StatusTicket><time>{item.time}</time></header><p>{item.content}</p>{item.images.length>0&&<div className="feedback-history-images">{item.images.map((image,index)=><ProductArt type={image} key={`${item.id}-${index}`}/>)}</div>}{item.reply&&<div className="feedback-reply"><b>平台回复</b><span>{item.reply}</span><time>{item.replyTime}</time></div>}</article>;})}</section><ListBoundary text="没有更多反馈记录了"/></main></div>;
}

function AppSettings({ navigate, notify, singleShop = false }) {
  const rows = [['user','个人资料','app/profile-edit'],['message','消息通知','app/notifications'],['settings','隐私设置','app/privacy'],['spark',singleShop?'品牌信息':'关于我们','app/about']];
  return <div className="mobile-screen" data-screen-label="App 设置"><SimpleMobileHeader title="设置" navigate={navigate} back="app/profile"/><main className="mobile-scroll settings-page">{rows.map(([icon,label,target])=><button key={label} onClick={()=>navigate(target)}><span><Icon name={icon}/>{label}</span><Icon name="chevron" size={16}/></button>)}<button className="logout-button" onClick={()=>{notify('已退出当前账号');navigate('app/login');}}>退出登录</button></main></div>;
}

function AppShops({ navigate }) {
  const [keyword,setKeyword] = useState('');
  const [searchKeyword,setSearchKeyword] = useState('');
  const shops = [
    { name:'Yeah 数码生活馆', productCount:126, logo:'assets/shop-logo-digital.png' },
    { name:'山野生活店', productCount:38, logo:'assets/shop-logo-mountain.png' },
    { name:'白茶美护精选', productCount:45, logo:'assets/shop-logo-tea.png' }
  ];
  const visible = shops.filter((shop)=>!searchKeyword||shop.name.includes(searchKeyword));
  const search = () => setSearchKeyword(keyword.trim());
  return <div className="mobile-screen" data-screen-label="App 店铺列表"><SimpleMobileHeader title="店铺" navigate={navigate}/><main className="mobile-scroll secondary-page"><div className="shop-list-search"><label className="list-search"><Icon name="search" size={17}/><input value={keyword} onChange={(event)=>setKeyword(event.target.value)} onKeyDown={(event)=>event.key==='Enter'&&search()} placeholder="搜索店铺"/></label><button onClick={search}>搜索</button></div>{visible.map((shop)=><button className="shop-list-card" key={shop.name} onClick={()=>navigate('app/shop')}><img className="shop-list-logo" src={shop.logo} alt=""/><div><b>{shop.name}</b><span>商品 {shop.productCount} 件</span></div><Icon name="chevron" size={17}/></button>)}{!visible.length&&<EmptyState icon="search" title="没有找到相关店铺" description="换个关键词试试" fill/>}{!!visible.length&&<ListBoundary text="没有更多店铺了"/>}</main></div>;
}

function AppShop({ navigate, notify }) {
  const [view,setView] = useState('宝贝');
  const [category,setCategory] = useState('全部');
  const [categoryGroup,setCategoryGroup] = useState('耳机');
  const [categoryLeaf,setCategoryLeaf] = useState('头戴式耳机');
  const [sort,setSort] = useState('综合');
  const [keyword,setKeyword] = useState('');
  const [couponClaimed,setCouponClaimed] = useState(false);
  const [infoSheet,setInfoSheet] = useState(false);
  const [bannerIndex,setBannerIndex] = useState(0);
  const storeProductMeta = [
    ['静界 Pro 头戴降噪耳机','耳机','头戴式耳机','headphone',399,1862],
    ['云朵真无线蓝牙耳机','耳机','真无线耳机','headphone',269,2386],
    ['旅行耳机收纳保护盒','耳机','耳机配件','coffee',49,956],
    ['随行便携蓝牙音箱','音箱','便携音箱','headphone',239,983],
    ['桌面 HiFi 有源音箱','音箱','桌面音箱','headphone',699,531],
    ['山野户外防水音箱','音箱','户外音箱','coffee',329,726],
    ['轻量健康运动手表','智能穿戴','智能手表','headphone',529,1266],
    ['全天候睡眠运动手环','智能穿戴','运动手环','headphone',189,2108],
    ['磁吸充电运动表带','智能穿戴','穿戴配件','coffee',79,842],
    ['65W 氮化镓快充套装','数码配件','充电设备','coffee',159,3156],
    ['柔韧编织快充数据线','数码配件','数据线材','coffee',39,4820],
    ['便携数码分区收纳包','数码配件','收纳保护','coffee',69,1175]
  ];
  const storeProducts = storeProductMeta.flatMap(([name,shopCategory,shopLeaf,art,price,sales],index)=>[
    {
      ...MOCKS.products[index % MOCKS.products.length],
      id:`shop-product-${index + 1}-standard`,
      name,shopCategory,shopLeaf,art,price,sales
    },
    {
      ...MOCKS.products[(index + 4) % MOCKS.products.length],
      id:`shop-product-${index + 1}-upgrade`,
      name:`${name} · 升级款`,shopCategory,shopLeaf,art,
      price:Math.round(price * 1.18),
      sales:Math.max(126,Math.round(sales * .62))
    }
  ]);
  const categoryTree = [
    {name:'耳机',children:['头戴式耳机','真无线耳机','耳机配件']},
    {name:'音箱',children:['便携音箱','桌面音箱','户外音箱']},
    {name:'智能穿戴',children:['智能手表','运动手环','穿戴配件']},
    {name:'数码配件',children:['充电设备','数据线材','收纳保护']}
  ];
  const shopBanners = [
    { art:'headphone', label:'本周精选', title:'把好声音带进每一天', note:'店内数码好物持续上新' },
    { art:'chair', label:'生活灵感', title:'让工作与生活更松弛', note:'轻巧设备，自在连接' },
    { art:'serum', label:'会员专享', title:'认真挑选每一件日常好物', note:'店铺优惠持续更新' }
  ];
  const activeBanner = shopBanners[bannerIndex];
  useEffect(()=>{
    const timer = window.setInterval(()=>setBannerIndex((index)=>(index+1)%shopBanners.length),3200);
    return ()=>window.clearInterval(timer);
  },[]);
  const sortedProducts = useMemo(()=>{
    const filtered = storeProducts.filter((item)=>{
      const categoryMatched = category==='全部'||item.shopCategory===category;
      const keywordMatched = !keyword.trim()||item.name.includes(keyword.trim());
      return categoryMatched&&keywordMatched;
    });
    if(sort==='销量') return [...filtered].sort((a,b)=>b.sales-a.sales);
    if(sort==='价格') return [...filtered].sort((a,b)=>a.price-b.price);
    if(sort==='上新') return [...filtered].reverse();
    return filtered;
  },[category,sort,keyword]);
  const productCards = (items)=>items.map((item)=><button className="product-card" key={item.id} onClick={()=>navigate('app/product')}><ProductArt type={item.art}/><span className="product-card-name">{item.name}</span><span className="product-card-meta"><b>¥{item.price}</b><small>已售 {item.sales}</small></span></button>);
  const claimCoupon = ()=>{
    if(couponClaimed) return notify('这张店铺券已经领取');
    setCouponClaimed(true);
    notify('店铺券领取成功');
  };
  const chooseCategory = (group, leaf)=>{
    setCategory(group);
    setCategoryLeaf(leaf);
    notify(`已切换到“${leaf}”分类`);
  };
  const activeCategoryGroup = categoryTree.find((item)=>item.name===categoryGroup)||categoryTree[0];
  const categorySources = storeProducts.filter((item)=>item.shopCategory===categoryGroup&&item.shopLeaf===categoryLeaf);
  const categoryProductSuffixes = ['标准款','轻享款','长续航款','高配款','礼盒款','便携款','专业款','青春款','旗舰款','限定款'];
  const categoryProducts = categoryProductSuffixes.map((suffix,index)=>{
    const source = categorySources[index % Math.max(categorySources.length,1)] || storeProducts[index % storeProducts.length];
    return {
      ...source,
      id:`${categoryGroup}-${categoryLeaf}-${index}`,
      name:`${source.name.split(' · ')[0]} · ${suffix}`,
      price:Math.round(source.price * (1 + index * .035)),
      sales:Math.max(86,Math.round(source.sales * (1 - index * .055))),
      hasPriceRange:index % 3 !== 0
    };
  });
  return <div className="mobile-screen" data-screen-label="App 多租户店铺详情">
    <SimpleMobileHeader title="店铺" navigate={navigate} back="app/shops"/>
    <main className="mobile-scroll shop-detail-page shop-detail-v2">
      <section className="shop-identity shop-identity-v2">
        <img className="shop-detail-logo" src="assets/shop-logo-digital.png" alt=""/>
        <div><h2>Yeah 数码生活馆</h2><button className="shop-info-trigger" onClick={()=>setInfoSheet(true)}>在售 126 件 · 店铺信息 <Icon name="chevron" size={11}/></button></div>
        <span className="shop-open-state"><i></i>营业中</span>
        <button className="shop-icon-action" onClick={()=>notify('已打开分享面板')} aria-label="分享店铺"><Icon name="share" size={17}/></button>
      </section>

      {view==='宝贝'&&<div className="shop-treasure-content">
        <div className="shop-banner-wrap"><button className="shop-feature shop-feature-v2" onClick={()=>navigate('app/product')}>
          <div><span>{activeBanner.label}</span><h3>{activeBanner.title}</h3><small>{activeBanner.note}</small><em>查看商品 <Icon name="chevron" size={13}/></em></div>
          <ProductArt type={activeBanner.art}/>
        </button><div className="shop-banner-dots">{shopBanners.map((item,index)=><button aria-label={`第${index+1}张Banner`} key={item.title} className={bannerIndex===index?'active':''} onClick={()=>setBannerIndex(index)}></button>)}</div></div>
        <div className="shop-benefit-rows">
          <button className={`shop-coupon-strip ${couponClaimed?'claimed':''}`} onClick={claimCoupon}><span className="shop-coupon-value">¥20</span><span><b>全店满 199 减 20</b><small>领取后下单可用</small></span><em>{couponClaimed?'已领取':'领取'}</em></button>
        </div>
        <label className="shop-product-search"><Icon name="search" size={16}/><input value={keyword} onChange={(event)=>setKeyword(event.target.value)} placeholder="输入关键词，实时筛选"/>{keyword&&<button onClick={()=>setKeyword('')}>清除</button>}</label>
        <div className="shop-sort-row">{['综合','销量','价格','上新'].map((item)=><button key={item} className={sort===item?'active':''} onClick={()=>setSort(item)}>{item}</button>)}</div>
        <div className="shop-result-meta"><span>{category==='全部'?'全部宝贝':category}</span><button className={category==='全部'?'is-hidden':''} onClick={()=>setCategory('全部')}>清除分类</button><small>{sortedProducts.length} 件</small></div>
        {sortedProducts.length?<><div className="product-grid shop-product-grid">{productCards(sortedProducts)}</div><ListBoundary/></>:<EmptyState icon="search" title="没有找到相关商品" description="换个关键词或分类试试" compact/>}
      </div>}

      {view==='分类'&&<div className="shop-category-content">
        <div className="shop-category-browser">
          <aside>{categoryTree.map((item)=><button key={item.name} className={categoryGroup===item.name?'active':''} onClick={()=>{setCategoryGroup(item.name);setCategory(item.name);setCategoryLeaf(item.children[0]);}}>{item.name}</button>)}</aside>
          <section><div className="shop-category-tabs">{activeCategoryGroup.children.map((item)=><button className={categoryLeaf===item?'active':''} key={item} onClick={()=>chooseCategory(activeCategoryGroup.name,item)}>{item}</button>)}</div><div className="shop-category-products">{categoryProducts.length?<><div className="shop-category-product-list">{categoryProducts.map((item)=><button key={item.id} onClick={()=>navigate('app/product')}><ProductArt type={item.art}/><span><b className="shop-category-product-name">{item.name}</b><span className="product-card-meta shop-category-product-meta"><b className="shop-category-product-price">¥{item.price}{item.hasPriceRange&&<em>起</em>}</b><small>已售 {item.sales}</small></span></span><Icon name="chevron" size={15}/></button>)}</div><ListBoundary/></>:<EmptyState icon="box" title="该分类暂无商品" description="商品上架后会展示在这里" compact/>}</div></section>
        </div>
      </div>}
    </main>
    <nav className="shop-bottom-nav"><button className={view==='宝贝'?'active':''} onClick={()=>{setCategory('全部');setView('宝贝');}}><Icon name="bag" size={19}/><span>宝贝</span></button><button className={view==='分类'?'active':''} onClick={()=>setView('分类')}><Icon name="grid" size={19}/><span>分类</span></button><button onClick={()=>navigate('app/service?shop=cv-1')}><Icon name="message" size={19}/><span>客服</span></button></nav>
    {infoSheet&&<div className="sheet-mask" onClick={()=>setInfoSheet(false)}><section className="mobile-sheet shop-info-sheet" onClick={(event)=>event.stopPropagation()}><div className="sheet-handle"></div><header><div><small>店铺信息</small><h2>Yeah 数码生活馆</h2></div><button onClick={()=>setInfoSheet(false)}>×</button></header><section className="shop-sheet-story"><span>店铺简介</span><p>专注日常数码与声音体验，挑选耐用、简洁的日常好物。</p></section><div className="shop-sheet-row"><span>店铺编号</span><b>SHOP-10001</b></div><div className="shop-sheet-row"><span>营业状态</span><b>营业中</b></div><div className="shop-sheet-row"><span>在售商品</span><b>126 件</b></div></section></div>}
  </div>;
}

function AppPayResult({ navigate, route, notify }) {
  const requestedState = routeParam(route,'state');
  const supportedStates = ['success','processing','cancel','failed'];
  const [state,setState] = useState(supportedStates.includes(requestedState) ? requestedState : 'success');
  const stateMeta = {
    success:{icon:'check',title:'支付成功',copy:'支付结果已确认，订单状态已更新。',tone:'success'},
    processing:{icon:'clock',title:'支付处理中',copy:'正在确认支付结果，请稍候片刻。',tone:'processing'},
    cancel:{icon:'close',title:'支付已取消',copy:'订单仍为待付款，可以稍后继续支付。',tone:'neutral'},
    failed:{icon:'close',title:'支付失败',copy:'本次支付未完成，请重新尝试。',tone:'failed'}
  };
  const meta = stateMeta[state];
  const retry = () => { setState('processing'); notify('正在重新拉起支付'); };
  const refresh = () => { setState('success'); notify('支付结果已更新'); };
  return <div className="mobile-screen" data-screen-label="App 支付结果"><SimpleMobileHeader title="支付结果" navigate={navigate}/><main className={`pay-result-v2 is-${state}`}><section className="pay-result-hero"><span className={`pay-result-icon ${meta.tone}`}><Icon name={meta.icon} size={31}/></span><h1>{meta.title}</h1><p>{meta.copy}</p></section><section className="pay-result-info"><div><span>订单编号</span><b className="mono">YM202610270016</b></div><div><span>{state==='success'?'支付金额':'应付金额'}</span><strong>¥0.01</strong></div>{state==='success'&&<div><span>支付时间</span><b>2026-10-27 10:02:18</b></div>}</section>{state==='processing'&&<div className="pay-processing-note"><i></i><span>页面将自动刷新支付结果</span></div>}</main><div className="pay-result-actions">{state==='success'?<><button onClick={()=>navigate('app/home')}>返回首页</button><button className="primary" onClick={()=>navigate('app/order')}>查看订单</button></>:state==='processing'?<><button onClick={()=>navigate('app/order')}>查看订单</button><button className="primary" onClick={refresh}>刷新结果</button></>:<><button onClick={()=>navigate('app/order')}>查看订单</button><button className="primary" onClick={retry}>重新支付</button></>}</div></div>;
}

function AppLogistics({ navigate, route, notify }) {
  const state = routeParam(route,'state') || 'normal';
  const nodes = [
    ['今天 09:12','快件已从上海集散中心发出','物流轨迹'],
    ['今天 07:30','快件已到达上海集散中心','物流轨迹'],
    ['昨天 18:50','顺丰速运已揽收','物流轨迹'],
    ['昨天 16:42','商家已发货，物流信息已提交','订单节点']
  ];
  const content = state==='loading'
    ? <div className="logistics-state-v2 logistics-loading-v2"><i></i><b>物流信息加载中</b><span>正在查询最新物流轨迹</span></div>
    : state==='error'
      ? <div className="logistics-state-v2"><span className="logistics-state-icon"><Icon name="truck" size={24}/></span><b>物流信息加载失败</b><span>网络波动或物流服务暂不可用</span><button onClick={()=>notify('正在重新加载物流信息')}>重新加载</button></div>
      : state==='empty'
        ? <div className="logistics-state-v2"><span className="logistics-state-icon"><Icon name="box" size={24}/></span><b>暂无物流轨迹</b><span>商家发货后，物流进度会展示在这里</span></div>
        : <><section className="logistics-summary-v2"><span className="logistics-summary-icon"><Icon name="truck" size={22}/></span><div><b>顺丰速运</b><span><em className="mono">SF1462903821</em><button onClick={()=>notify('运单号已复制')}>复制</button></span></div><StatusTicket tone="info">运输中</StatusTicket></section><section className="logistics-order-v2"><div><ProductArt type="serum"/><ProductArt type="headphone"/><span>+1</span></div><p><b>白茶屏障修护精华等 3 件商品</b><small>查看订单中的全部商品</small></p><button onClick={()=>navigate('app/order')}><Icon name="chevron" size={15}/></button></section><section className="logistics-timeline-v2"><header><h2>物流轨迹</h2><span>更新于今天 09:12</span></header><article>{nodes.map((node,index)=><div className={index===0?'active':''} key={`${node[0]}-${node[1]}`}><i></i><span><b>{node[1]}</b><small>{node[0]} · {node[2]}</small></span></div>)}</article></section></>;
  return <div className="mobile-screen" data-screen-label="App 物流详情"><SimpleMobileHeader title="物流详情" navigate={navigate} back="app/order"/><main className="mobile-scroll logistics-page logistics-page-v2">{content}</main></div>;
}

function AppRefund({ navigate, route, notify }) {
  const state = routeParam(route,'state') || 'ready';
  const reapply = routeParam(route,'mode') === 'reapply';
  const [reason,setReason] = useState(reapply ? '商品质量问题' : '');
  const [remark,setRemark] = useState(reapply ? '商品存在轻微质量问题，希望重新申请退款。' : '');
  const [images,setImages] = useState(reapply ? ['serum'] : []);
  const [reasonSheet,setReasonSheet] = useState(false);
  const reasons = ['不想要了','拍错了','多拍了','商品无货','商品信息与描述不符','商品质量问题','商家发错货','物流问题','其他原因'];
  const addImage = () => {
    if(images.length>=6)return notify('退款凭证最多上传 6 张');
    setImages([...images,['chair','coffee','headphone'][images.length%3]]);
  };
  if(state==='loading')return <div className="mobile-screen" data-screen-label="App 申请退款"><SimpleMobileHeader title="申请退款" navigate={navigate} back="app/order"/><main className="refund-page-state-v2"><i></i><b>退款信息加载中</b><span>正在读取订单与退款信息</span></main></div>;
  if(state==='error')return <div className="mobile-screen" data-screen-label="App 申请退款"><SimpleMobileHeader title="申请退款" navigate={navigate} back="app/order"/><main className="refund-page-state-v2"><span className="refund-state-icon"><Icon name="order" size={24}/></span><b>退款信息加载失败</b><span>网络波动，请稍后重新加载</span><button onClick={()=>notify('正在重新加载退款信息')}>重新加载</button></main></div>;
  return <div className="mobile-screen" data-screen-label="App 申请退款"><SimpleMobileHeader title="申请退款" navigate={navigate} back="app/order"/><main className="mobile-scroll refund-apply-v2"><section className="refund-order-summary"><ProductArt type="serum"/><div><b>白茶屏障修护精华</b><span>30ml · 单瓶</span><p><small>共 1 件商品</small><strong>可退款 ¥0.01</strong></p></div></section><section className="refund-form-section"><h2>退款信息</h2><div className="refund-static-row"><span>退款方式</span><b>仅退款</b></div><button className="refund-select-row" onClick={()=>setReasonSheet(true)}><span>退款原因 <em>*</em></span><span className={reason?'selected':'placeholder'}>{reason||'请选择退款原因'}<Icon name="chevron" size={14}/></span></button></section><section className="refund-form-section"><div className="refund-field-head"><b>补充说明</b><small>选填</small></div><div className="refund-textarea-shell"><textarea value={remark} maxLength="500" onChange={(e)=>setRemark(e.target.value)} placeholder="请补充说明退款原因，便于商家处理"></textarea><small>{remark.length}/500</small></div><div className="refund-field-head refund-evidence-head"><b>退款凭证</b><small>选填，最多 6 张</small></div><div className="refund-evidence-grid">{images.map((image,index)=><button key={`${image}-${index}`} onClick={()=>setImages(images.filter((_,itemIndex)=>itemIndex!==index))}><ProductArt type={image}/><i>×</i></button>)}{images.length<6&&<button className="refund-evidence-add" onClick={addImage}><Icon name="plus" size={18}/><span>添加图片</span></button>}</div></section><div className="refund-submit-placeholder"></div></main><div className="single-bottom-action refund-submit-bar"><button onClick={()=>{if(!reason)return notify('请选择退款原因');notify(reapply?'退款申请已重新提交':'退款申请已提交');navigate('app/refund-detail');}}>{reapply?'重新提交申请':'提交申请'}</button></div>{reasonSheet&&<div className="sheet-mask" onClick={()=>setReasonSheet(false)}><section className="mobile-sheet refund-reason-sheet" onClick={(event)=>event.stopPropagation()}><div className="sheet-handle"></div><header><h2>选择退款原因</h2><button onClick={()=>setReasonSheet(false)}>×</button></header><div className="refund-reason-options">{reasons.map((item)=><button className={reason===item?'active':''} key={item} onClick={()=>{setReason(item);setReasonSheet(false);}}><span>{item}</span>{reason===item&&<Icon name="check" size={16}/>}</button>)}</div></section></div>}</div>;
}

function AppReviewForm({ navigate, notify }) {
  const [items,setItems] = useState([
    { id:'headphone', art:'headphone', name:'云感降噪头戴耳机', spec:'雾银 · 标准版', rating:5, content:'佩戴舒适，通勤降噪效果很好。', images:['headphone'] },
    { id:'serum', art:'serum', name:'氨基酸净澈洁面乳', spec:'120g · 单支', rating:4, content:'', images:[] }
  ]);
  const [shopRatings,setShopRatings] = useState({ description:5, delivery:5, service:5 });
  const [anonymous,setAnonymous] = useState(false);
  const [uploadingId,setUploadingId] = useState('');
  const [preview,setPreview] = useState(null);
  const [publishing,setPublishing] = useState(false);
  const ratingText = (value) => ['','很差','较差','一般','满意','非常满意'][value] || '';
  const updateItem = (id, patch) => setItems(items.map((item)=>item.id===id?{...item,...patch}:item));
  const addImage = (item) => {
    if(item.images.length>=6)return notify('每件商品最多上传 6 张图片');
    setUploadingId(item.id);
    setTimeout(()=>{
      const choices = ['chair','coffee','serum','headphone'];
      updateItem(item.id,{images:[...item.images,choices[item.images.length%choices.length]]});
      setUploadingId('');
      notify('图片上传成功');
    },500);
  };
  const removeImage = (item,imageIndex) => updateItem(item.id,{images:item.images.filter((_,index)=>index!==imageIndex)});
  const renderStars = (value,onChange) => <div className="review-star-control">{[1,2,3,4,5].map((star)=><button key={star} className={star<=value?'active':''} onClick={()=>onChange(star)}>★</button>)}</div>;
  const publish = () => {
    if(uploadingId)return notify('图片仍在上传，请稍候');
    if(items.some((item)=>item.rating<3&&!item.content.trim()))return notify('低评分商品请填写评价内容');
    setPublishing(true);
    setTimeout(()=>{setPublishing(false);notify('评价发布成功');navigate('app/review-detail');},700);
  };
  return <div className="mobile-screen" data-screen-label="App 发表评价">
    <SimpleMobileHeader title="发表评价" navigate={navigate} back="app/orders"/>
    <main className="mobile-scroll review-publish-page">
      {items.map((item)=><section className="review-publish-item" key={item.id}>
        <header className="review-publish-product"><ProductArt type={item.art}/><div><b>{item.name}</b><span>{item.spec}</span></div></header>
        <div className="review-publish-rating"><span>商品评分</span>{renderStars(item.rating,(rating)=>updateItem(item.id,{rating}))}<b>{ratingText(item.rating)}</b></div>
        <div className="review-publish-content"><textarea value={item.content} maxLength="500" onChange={(event)=>updateItem(item.id,{content:event.target.value})} placeholder="分享真实使用体验，帮助更多人做选择"></textarea><small>{item.content.length}/500</small></div>
        <div className="review-publish-images">
          {item.images.map((image,index)=><button className="review-uploaded-image" key={`${image}-${index}`} onClick={()=>setPreview(image)}><ProductArt type={image}/><i onClick={(event)=>{event.stopPropagation();removeImage(item,index);}}>×</i></button>)}
          {item.images.length<6&&<button className="review-image-add" disabled={uploadingId===item.id} onClick={()=>addImage(item)}><Icon name={uploadingId===item.id?'clock':'plus'} size={18}/><span>{uploadingId===item.id?'上传中':'添加图片'}</span><small>{item.images.length}/6</small></button>}
        </div>
      </section>)}
      <section className="review-shop-rating">
        <header><h2>店铺评分</h2><span>综合评价本次购物体验</span></header>
        {[['description','描述相符'],['delivery','物流服务'],['service','服务态度']].map(([key,label])=><div className="review-shop-rating-row" key={key}><span>{label}</span>{renderStars(shopRatings[key],(value)=>setShopRatings({...shopRatings,[key]:value}))}<b>{shopRatings[key]}分</b></div>)}
        <button className="review-anonymous-row" onClick={()=>setAnonymous(!anonymous)}><span><b>匿名评价</b><small>隐藏头像和昵称后公开展示评价</small></span><i className={anonymous?'active':''}><em></em></i></button>
      </section>
      <div className="review-publish-placeholder"></div>
    </main>
    <div className="single-bottom-action review-publish-bar"><button disabled={publishing||!!uploadingId} onClick={publish}>{uploadingId?'图片上传中':publishing?'发布中...':'发布评价'}</button></div>
    {preview&&<div className="image-preview-mask" onClick={()=>setPreview(null)}><button onClick={()=>setPreview(null)}>×</button><ProductArt type={preview}/><span>点击任意位置关闭</span></div>}
  </div>;
}

function AppReviewDetail({ navigate, notify }) {
  const [replies,setReplies] = useState([{side:'merchant',text:'感谢您的认可，耳罩配件可单独购买。'},{side:'buyer',text:'好的，后续有需要再联系。'},{side:'merchant',text:'有需要可以直接在这里继续留言。'}]);
  const [text,setText] = useState('');
  const canReply = replies[replies.length-1]?.side === 'merchant';
  const submit = () => { if(!text.trim()) return notify('请输入回复内容'); setReplies([...replies,{side:'buyer',text:text.trim()}]); setText(''); notify('回复已发布，等待商家回复'); };
  return <div className="mobile-screen" data-screen-label="App 评价详情"><SimpleMobileHeader title="评价详情" navigate={navigate} back="app/orders?tab=待评价"/><main className="mobile-scroll review-detail-page"><div className="review-product-head"><ProductArt type="headphone"/><div><b>云感降噪头戴耳机</b><span>雾银 · 标准版</span></div></div><article className="review-conversation"><header><Avatar name="林可"/><div><b>林可</b><span>2026-10-23 14:20</span></div><span className="stars-inline">★★★★★</span></header><p>佩戴两小时没有压耳，降噪对通勤很有帮助。包装也很稳妥。</p>{replies.map((reply,index)=><div key={index} className={reply.side==='merchant'?'merchant-reply':'buyer-reply'}><b>{reply.side==='merchant'?'商家回复':'我的回复'}</b><span>{reply.text}</span></div>)}</article>{canReply?<section className="review-reply-box"><textarea value={text} onChange={(e)=>setText(e.target.value)} placeholder="回复商家，继续沟通使用体验"></textarea><button onClick={submit}>发表回复</button></section>:<div className="reply-waiting"><Icon name="clock" size={16}/><span>已回复，等待商家继续回复后可再次留言</span></div>}</main></div>;
}

function ProfileDateWheel({ value, onChange }) {
  const [yearText='1996',monthText='08',dayText='12'] = String(value || '1996-08-12').split('-');
  const year = Number(yearText); const month = Number(monthText); const day = Number(dayText);
  const yearRef = React.useRef(null); const monthRef = React.useRef(null); const dayRef = React.useRef(null);
  const wheelReadyRef = React.useRef(false);
  const daysInMonth = (targetYear,targetMonth) => new Date(targetYear,targetMonth,0).getDate();
  const formatDate = (targetYear,targetMonth,targetDay) => `${targetYear}-${String(targetMonth).padStart(2,'0')}-${String(targetDay).padStart(2,'0')}`;
  const years = Array.from({length:127},(_,index)=>1900+index);
  const months = Array.from({length:12},(_,index)=>index+1);
  const days = Array.from({length:daysInMonth(year,month)},(_,index)=>index+1);
  const changePart = (part,nextValue) => {
    let nextYear = year; let nextMonth = month; let nextDay = day;
    if (part === 'year') nextYear = nextValue;
    if (part === 'month') nextMonth = nextValue;
    if (part === 'day') nextDay = nextValue;
    nextDay = Math.min(nextDay,daysInMonth(nextYear,nextMonth));
    onChange(formatDate(nextYear,nextMonth,nextDay));
  };
  const refs = { year:yearRef, month:monthRef, day:dayRef };
  useEffect(() => {
    wheelReadyRef.current = false;
    window.requestAnimationFrame(() => {
      if (yearRef.current) yearRef.current.scrollTop = (year-1900)*36;
      if (monthRef.current) monthRef.current.scrollTop = (month-1)*36;
      if (dayRef.current) dayRef.current.scrollTop = (day-1)*36;
      window.requestAnimationFrame(() => { wheelReadyRef.current = true; });
    });
  }, []);
  const syncScroll = (part,values,event) => {
    if (!wheelReadyRef.current) return;
    const index = Math.max(0,Math.min(values.length-1,Math.round(event.currentTarget.scrollTop/36)));
    if (values[index] !== ({year,month,day})[part]) changePart(part,values[index]);
  };
  const choose = (part,values,item) => {
    refs[part].current?.scrollTo({top:values.indexOf(item)*36,behavior:'smooth'});
    changePart(part,item);
  };
  const column = (part,current,values,suffix) => <div ref={refs[part]} className="date-wheel-column" onScroll={(event)=>syncScroll(part,values,event)}>{values.map((item)=><button key={item} className={item===current?'active':''} onClick={()=>choose(part,values,item)}><b>{String(item).padStart(part==='year'?4:2,'0')}</b><small>{suffix}</small></button>)}</div>;
  return <div className="profile-date-wheel"><div className="date-wheel-selection"></div>{column('year',year,years,'年')}{column('month',month,months,'月')}{column('day',day,days,'日')}</div>;
}

function AppProfileEdit({ navigate, notify }) {
  const [nickname,setNickname] = useState('林可');
  const [mobile] = useState('138****7621');
  const [gender,setGender] = useState('女');
  const [birthday,setBirthday] = useState('1996-08-12');
  const [picker,setPicker] = useState('');
  const [draftBirthday,setDraftBirthday] = useState('1996-08-12');
  const [avatarPreview,setAvatarPreview] = useState(false);
  const closePicker = () => setPicker('');
  const openBirthday = () => { setDraftBirthday(birthday); setPicker('birthday'); };
  const confirmPicker = () => { if (picker === 'birthday') setBirthday(draftBirthday); closePicker(); };
  return <div className="mobile-screen" data-screen-label="App 个人资料"><SimpleMobileHeader title="个人资料" navigate={navigate} back="app/profile"/><main className="mobile-scroll profile-edit-page"><div className="avatar-edit" role="button" tabIndex="0" onClick={()=>notify('已打开头像选择')}><button className="avatar-preview-trigger" aria-label="查看头像大图" onClick={(event)=>{event.stopPropagation();setAvatarPreview(true);}}><Avatar name={nickname} size={64}/></button><i className="avatar-edit-spacer"></i><Icon name="chevron"/></div><section className="profile-edit-fields"><label className="profile-field-row editable"><span>昵称</span><input value={nickname} maxLength="16" onChange={(e)=>setNickname(e.target.value)} aria-label="昵称"/></label><div className="profile-field-row"><span>手机号</span><b>{mobile}<StatusTicket tone="success" small>已绑定</StatusTicket></b></div><button className="profile-field-row" onClick={()=>setPicker('gender')}><span>性别</span><b>{gender}<Icon name="chevron" size={16}/></b></button><button className="profile-field-row" onClick={openBirthday}><span>生日</span><b>{birthday}<Icon name="chevron" size={16}/></b></button></section><button className="form-submit" onClick={()=>nickname.trim().length>=3?notify('个人资料已保存'):notify('昵称请输入3至16个字符')}>保存修改</button></main>{picker&&<div className="sheet-mask" onClick={closePicker}><section className="mobile-sheet profile-picker-sheet" onClick={(e)=>e.stopPropagation()}><div className="sheet-handle"></div>{picker==='birthday'?<><div className="date-picker-toolbar"><button onClick={closePicker}>取消</button><b>选择生日</b><button onClick={confirmPicker}>完成</button></div><ProfileDateWheel value={draftBirthday} onChange={setDraftBirthday}/></>:<><header><h2>选择性别</h2><button onClick={closePicker}>×</button></header><div className="profile-option-list">{['男','女','不公开'].map((item)=><button key={item} className={gender===item?'active':''} onClick={()=>setGender(item)}><span>{item}</span>{gender===item&&<Icon name="check" size={17}/>}</button>)}</div><button className="sheet-confirm" onClick={confirmPicker}>确认</button></>}</section></div>}{avatarPreview&&<div className="image-preview-mask" onClick={()=>setAvatarPreview(false)}><button onClick={()=>setAvatarPreview(false)}>×</button><div className="avatar-preview-art"><Avatar name={nickname} size={180}/></div></div>}</div>;
}

function AppAbout({ navigate }) {
  return <div className="mobile-screen" data-screen-label="App 关于我们"><SimpleMobileHeader title="关于我们" navigate={navigate} back="app/settings"/><main className="about-page"><span className="about-mark">Y</span><h1>Yeah Mall</h1><p>一个面向多店铺零售场景的开源商城项目，覆盖商品、交易、营销、评价、客服与会员积分。</p><div><span>当前版本</span><b>1.0.0</b></div><button onClick={()=>navigate('app/legal?type=agreement')}><span>服务协议</span><Icon name="chevron" size={15}/></button><button onClick={()=>navigate('app/legal?type=privacy')}><span>隐私政策</span><Icon name="chevron" size={15}/></button></main></div>;
}

function AppLegal({ navigate, route }) {
  const type = routeParam(route,'type') || 'legal';
  const title = type==='agreement'?'服务协议':type==='privacy'?'隐私政策':'服务协议与隐私政策';
  const agreement = '本服务协议为开源项目的默认示例内容。正式运营前，请由商城运营主体根据实际业务、服务范围和适用法律配置完整协议。\n\n使用本商城服务时，请遵守平台规则，不得利用本服务从事违法违规活动。商品交易、支付、配送、售后等具体权利义务，以订单信息、商家承诺及正式发布的服务协议为准。';
  const privacy = '本隐私政策为开源项目的默认示例内容。正式运营前，请由商城运营主体根据实际收集和处理的数据配置完整政策。\n\n为提供账号、购物、订单履约、支付结果、售后和客服服务，商城可能在必要范围内处理账号信息、订单信息、收货地址及客服记录。';
  return <div className="mobile-screen" data-screen-label={`App ${title}`}><SimpleMobileHeader title={title} navigate={navigate} back="app/about"/><main className="mobile-scroll legal-page">{type!=='privacy'&&<section><h2>服务协议</h2><p>{agreement}</p></section>}{type!=='agreement'&&<section><h2>隐私政策</h2><p>{privacy}</p></section>}</main></div>;
}

function SingleShopBrand({ navigate }) {
  return <div className="mobile-screen" data-screen-label="单店 App 品牌信息"><SimpleMobileHeader title="品牌信息" navigate={navigate} back="app/settings"/><main className="mobile-scroll single-shop-brand-page"><section className="single-shop-brand-hero"><img src="assets/shop-logo-mountain.png" alt=""/><div><StatusTicket tone="success" small>营业中</StatusTicket><h1>山野生活</h1><p>把自然、耐用和松弛感带回每一天。</p></div></section><section className="single-shop-brand-story"><span>OUR STORY</span><h2>认真挑选，经得起日常的生活好物</h2><p>我们关注真实使用中的舒适与耐久，从居家、饮食到个人护理，为每一件商品保留清楚、克制而诚实的介绍。</p></section><section className="single-shop-brand-facts"><div><span>在售商品</span><b>38 件</b></div><div><span>主营品类</span><b>居家生活</b></div><div><span>店铺编号</span><b className="mono">SHAN-YE-001</b></div></section><section className="single-shop-service-list"><button onClick={()=>navigate('app/service')}><span><Icon name="message" size={18}/><b>联系客服</b></span><Icon name="chevron" size={16}/></button><button onClick={()=>navigate('app/privacy')}><span><Icon name="truck" size={18}/><b>配送与售后说明</b></span><Icon name="chevron" size={16}/></button><button onClick={()=>navigate('app/legal?type=legal')}><span><Icon name="order" size={18}/><b>服务协议与隐私政策</b></span><Icon name="chevron" size={16}/></button></section></main></div>;
}

function SingleShopUnavailable({ navigate }) {
  return <div className="mobile-screen single-shop-unavailable" data-screen-label="单店 App 店铺不可用"><MobileStatusBar tone="paper"/><div className="single-shop-unavailable-capsule"><WechatCapsule/></div><main><img src="assets/shop-logo-mountain.png" alt=""/><StatusTicket tone="neutral">暂时停业</StatusTicket><h1>店铺暂不可用</h1><p>店铺可能正在维护、暂停营业，或当前商城配置尚未完成。请稍后重新进入。</p><button className="primary" onClick={()=>navigate('app/home')}>重新加载</button><button onClick={()=>navigate('app/service')}>联系客服</button></main></div>;
}

function AppActivityProducts({ navigate, type, singleShop = false }) {
  const title = type==='seckill'?'秒杀商品':'拼团商品';
  return <div className="mobile-screen" data-screen-label={`App ${title}`}><SimpleMobileHeader title={title} navigate={navigate} back={type==='seckill'?'app/seckill':'app/groupbuy'}/><main className="mobile-scroll activity-products-page"><header><b>{singleShop?(type==='seckill'?'山野晚八点闪购':'山野生活双人团'):(type==='seckill'?'秋日数码秒杀':'双人好物团')}</b><span>{type==='seckill'?'10:00–12:00':'活动结束后，成团订单可发货'}</span></header>{MOCKS.products.map((item)=><article key={item.id}><ProductArt type={item.art}/><div><b>{item.name}</b><span>{item.subtitle}</span><strong>¥{type==='seckill'?Math.max(49,item.price-100):Math.max(39,item.price-30)}</strong><del>¥{item.price}</del></div><button onClick={()=>navigate(type==='seckill'?'app/seckill-detail':'app/groupbuy-detail')}>{type==='seckill'?'抢购':'拼团'}</button></article>)}<ListBoundary text="活动商品已全部展示"/></main></div>;
}

function AppCouponSelect({ navigate, singleShop = false }) {
  const [selected,setSelected] = useState(MOCKS.coupons[0].id);
  return <div className="mobile-screen" data-screen-label="App 选择优惠券"><SimpleMobileHeader title="选择优惠券" navigate={navigate} back="app/confirm"/><main className="mobile-scroll coupon-page">{MOCKS.coupons.map((coupon)=><button className={`coupon-select-card ${selected===coupon.id?'active':''}`} key={coupon.id} onClick={()=>setSelected(coupon.id)}><div><b>{coupon.value}</b><span>{coupon.condition}</span></div><div><strong>{coupon.name}</strong><small>{singleShop?'山野生活':coupon.shop} · {coupon.endAt} 到期</small></div><i>{selected===coupon.id&&<Icon name="check" size={13}/>}</i></button>)}</main><div className="single-bottom-action"><button onClick={()=>navigate('app/confirm')}>确认使用</button></div></div>;
}

function AppPointsProduct({ navigate, singleShop = false }) {
  const item = MOCKS.pointsProducts[0]; const [quantity,setQuantity] = useState(1);
  return <div className="mobile-screen" data-screen-label="App 积分商品"><SimpleMobileHeader title="积分商品" navigate={navigate} back="app/points"/><main className="mobile-scroll points-product-page"><ProductArt type={item.art}/><section><StatusTicket>{item.type}</StatusTicket><h1>{item.name}</h1><b>{item.points} <small>积分</small></b><p>环保棉帆布材质，适合日常通勤与购物使用。</p></section><div className="detail-choice-row static-row"><span className="choice-label">库存</span><b>{item.stock} 件</b><QuantityControl value={quantity} setValue={setQuantity} max={3}/></div><article><h3>兑换说明</h3><p>每位会员限兑 2 件，实物商品兑换后由{singleShop?'店铺':'平台'}安排物流发货。</p></article></main><div className="single-bottom-action"><button onClick={()=>navigate('app/points-confirm')}>立即兑换</button></div></div>;
}

function AppPointsConfirm({ navigate }) {
  return <div className="mobile-screen" data-screen-label="App 确认兑换"><SimpleMobileHeader title="确认兑换" navigate={navigate} back="app/points-product"/><main className="mobile-scroll confirm-scroll"><button className="address-panel" onClick={()=>navigate('app/addresses')}><span className="address-icon"><Icon name="store"/></span><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div><Icon name="chevron"/></button><section className="confirm-shop-card"><div className="confirm-product"><ProductArt type="coffee"/><div><b>品牌帆布袋</b><span>标准款</span><div><strong>600 积分</strong><small>×1</small></div></div></div></section><section className="price-summary"><div><span>当前积分</span><b>1,280</b></div><div><span>兑换积分</span><b className="saving">600</b></div><div className="summary-total"><span>兑换后剩余</span><strong>680</strong></div></section></main><div className="single-bottom-action"><button onClick={()=>navigate('app/points-order')}>确认兑换</button></div></div>;
}

function AppPointsOrders({ navigate }) {
  const rows=[['PX20261027001','品牌帆布袋','600','待发货','coffee'],['PX20261020008','全店 20 元券','300','已完成','serum']];
  return <div className="mobile-screen" data-screen-label="App 积分订单"><SimpleMobileHeader title="兑换记录" navigate={navigate} back="app/points"/><main className="mobile-scroll mobile-order-list">{rows.map((row)=><article className="mobile-order-card" key={row[0]}><header><span className="mono">{row[0]}</span><StatusTicket tone={row[3]==='已完成'?'success':'warning'}>{row[3]}</StatusTicket></header><button className="mobile-order-product" onClick={()=>navigate('app/points-order')}><ProductArt type={row[4]}/><div><b>{row[1]}</b><span>数量 × 1</span><strong>{row[2]} 积分</strong></div></button><footer><span>2026-10-27 10:18</span><div><button onClick={()=>navigate('app/points-order')}>查看详情</button></div></footer></article>)}<ListBoundary text="没有更多兑换记录了"/></main></div>;
}

function AppPointsOrder({ navigate, singleShop = false }) {
  return <div className="mobile-screen" data-screen-label="App 积分订单详情"><SimpleMobileHeader title="积分订单详情" navigate={navigate} back="app/points-orders"/><main className="mobile-scroll order-detail-scroll"><section className="order-state-panel"><span className="state-icon"><Icon name="box" size={27}/></span><div><h2>等待{singleShop?'店铺':'平台'}发货</h2><p>兑换商品正在准备中</p></div></section><section className="order-address"><Icon name="store"/><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div></section><section className="order-goods"><div className="confirm-product"><ProductArt type="coffee"/><div><b>品牌帆布袋</b><span>标准款 × 1</span><div><strong>600 积分</strong></div></div></div></section><section className="order-info-list"><div><span>积分订单号</span><b>PX20261027001</b></div><div><span>兑换积分</span><b>600</b></div><div><span>兑换时间</span><b>2026-10-27 10:18</b></div></section></main></div>;
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
  const [isDefault,setIsDefault] = useState(create);
  return <div className="mobile-screen" data-screen-label={`App ${create?'新增':'编辑'}地址`}><SimpleMobileHeader title={create?'新增地址':'编辑地址'} navigate={navigate} back="app/addresses"/><main className="mobile-scroll form-page address-edit-page"><label><span><i className="field-required">*</i>收货人</span><input value={name} onChange={(e)=>setName(e.target.value)} placeholder="请输入收货人姓名"/></label><label><span><i className="field-required">*</i>手机号</span><input value={mobile} onChange={(e)=>setMobile(e.target.value)} placeholder="请输入手机号"/></label><label><span><i className="field-required">*</i>所在地区</span><select defaultValue="浙江省 / 杭州市 / 西湖区"><option>浙江省 / 杭州市 / 西湖区</option><option>上海市 / 徐汇区</option></select></label><label><span><i className="field-required">*</i>详细地址</span><textarea value={address} onChange={(e)=>setAddress(e.target.value)} placeholder="街道、楼牌号等"></textarea></label><label><span>邮政编码 <small>选填</small></span><input inputMode="numeric" placeholder="请输入邮政编码"/></label><div className="switch-row"><span>设为默认地址</span><button className={`address-default-checkbox ${isDefault?'active':''}`} onClick={()=>setIsDefault(!isDefault)} aria-label="设为默认地址">{isDefault&&<Icon name="check" size={13}/>}</button></div></main><div className="single-bottom-action"><button onClick={()=>{if(!name||!mobile||!address)return notify('请完整填写地址信息');notify('地址已保存');navigate('app/addresses');}}>保存地址</button></div></div>;
}

function AppRefundDetail({ navigate, route }) {
  const requestedStatus = routeParam(route,'status') || 'applying';
  const status = ['applying','processing','success','rejected','failed'].includes(requestedStatus) ? requestedStatus : 'applying';
  const statusMeta = {
    applying:{icon:'clock',title:'等待商家审核',copy:'申请已提交，商家将在 24 小时内处理。',tone:'warning'},
    processing:{icon:'clock',title:'退款处理中',copy:'商家已同意退款，款项正在原路退回。',tone:'processing'},
    success:{icon:'check',title:'退款成功',copy:'退款已完成，请留意原支付账户。',tone:'success'},
    rejected:{icon:'close',title:'退款申请未通过',copy:'请查看拒绝原因，修改后可以重新申请。',tone:'rejected'},
    failed:{icon:'close',title:'退款暂未成功',copy:'第三方退款处理异常，系统将继续处理。',tone:'failed'}
  };
  const eventMap = {
    applying:[['提交退款申请','商品与描述不符','2026-10-27 11:06']],
    processing:[['商家同意退款','退款正在原路处理','2026-10-27 14:20'],['提交退款申请','商品与描述不符','2026-10-27 11:06']],
    success:[['退款成功','款项已退回原支付账户','2026-10-28 09:15'],['商家同意退款','退款申请已通过','2026-10-27 14:20'],['提交退款申请','商品与描述不符','2026-10-27 11:06']],
    rejected:[['商家拒绝退款','凭证信息不足，请补充后重新申请','2026-10-27 14:20'],['提交退款申请','商品与描述不符','2026-10-27 11:06']],
    failed:[['退款失败','第三方退款暂未成功','2026-10-28 09:15'],['商家同意退款','退款申请已通过','2026-10-27 14:20'],['提交退款申请','商品与描述不符','2026-10-27 11:06']]
  };
  const meta = statusMeta[status];
  const events = eventMap[status];
  return <div className="mobile-screen" data-screen-label="App 退款详情"><SimpleMobileHeader title="退款详情" navigate={navigate} back="app/order"/><main className={`mobile-scroll refund-detail-v2 is-${status}`}><section className="refund-status-v2"><span className={`refund-status-icon ${meta.tone}`}><Icon name={meta.icon} size={28}/></span><div><small>退款状态</small><h1>{meta.title}</h1><p>{meta.copy}</p></div></section><section className="refund-amount-v2"><span>退款金额</span><b>¥0.01</b></section>{['applying','processing'].includes(status)&&<div className="refund-time-note"><Icon name="clock" size={14}/><span>{status==='applying'?'预计商家将在 24 小时内处理':'退款通常将在 1–3 个工作日内到账'}</span></div>}<section className="refund-progress-v2"><header><h2>退款进度</h2><span>{events.length} 个节点</span></header><article>{events.map((event,index)=><div className={index===0?'active':''} key={`${event[0]}-${event[2]}`}><i></i><span><b>{event[0]}</b><em>{event[1]}</em><small>{event[2]}</small></span></div>)}</article></section><section className="refund-info-v2"><h2>退款信息</h2><div><span>退款原因</span><b>商品与描述不符</b></div><div><span>补充说明</span><b>商品外包装完好，希望申请退款。</b></div><div><span>申请时间</span><b>2026-10-27 11:06</b></div>{status==='rejected'&&<div><span>拒绝原因</span><b>凭证信息不足，请补充后重新申请。</b></div>}{status==='failed'&&<div><span>退款结果</span><b>第三方退款暂未成功，系统将继续处理。</b></div>}</section><section className="refund-evidence-v2"><h2>退款凭证</h2><div><ProductArt type="serum"/><ProductArt type="chair"/></div></section><div className={status==='rejected'?'refund-detail-action-space':'refund-detail-page-end'}></div></main>{status==='rejected'&&<div className="single-bottom-action refund-detail-action"><button onClick={()=>navigate('app/refund?mode=reapply')}>再次申请退款</button></div>}</div>;
}

function AppPreferencePage({ navigate, type }) {
  const notification = type==='notifications';
  const [values,setValues] = useState({order:true,promo:false,service:true,personalized:true,browseHistory:true});
  const rows = notification
    ? [['service','客服消息通知','控制当前设备收到客服新消息时的前台提醒']]
    : [['personalized','个性化商品推荐','关闭后首页继续展示平台通用热门商品'],['browseHistory','记录浏览足迹','关闭后新查看的普通商品不再写入浏览足迹']];
  const intro = notification
    ? ['NOTIFICATION','只保留真正重要的提醒','客服提醒偏好保存在当前设备，关闭后仍可在客服页面查看完整会话记录。']
    : ['PRIVACY CONTROL','你的信息，由你决定如何使用','购物履约所需信息与个性化体验分开管理，基础服务不会因关闭推荐而受影响。'];
  const toggle = (key) => setValues({...values,[key]:!values[key]});
  return <div className="mobile-screen" data-screen-label={`App ${notification?'消息通知':'隐私设置'}`}>
    <SimpleMobileHeader title={notification?'消息通知':'隐私设置'} navigate={navigate} back="app/settings"/>
    <main className="mobile-scroll preference-page preference-page-v2">
      <section className="preference-hero">
        <span>{intro[0]}</span>
        <h2>{intro[1]}</h2>
        <p>{intro[2]}</p>
      </section>
      <section className="preference-list">
        {rows.map(([key,label,description])=><button className="preference-row" key={key} onClick={()=>toggle(key)}>
          <span><b>{label}</b><small>{description}</small></span>
          <i className={`preference-switch ${values[key]?'active':''}`} aria-label={values[key]?'已开启':'已关闭'}><em></em></i>
        </button>)}
      </section>
      {!notification&&<section className="preference-note"><span><Icon name="check" size={17}/></span><div><h3>基础服务不受影响</h3><p>关闭个性化推荐不会影响购物、订单履约、售后和客服功能。</p></div></section>}
      <button className="preference-link" onClick={()=>navigate('app/legal?type=legal')}><span>查看隐私政策与服务协议</span><Icon name="chevron" size={16}/></button>
    </main>
  </div>;
}

function AppAuth({ navigate, mode, notify, singleShop = false }) {
  const register = mode==='register';
  const [mobile,setMobile]=useState('');
  const [code,setCode]=useState('');
  const [accepted,setAccepted]=useState(false);
  const submit = () => {
    if(!mobile||!code) return notify('请填写手机号和验证码');
    if(!accepted) return notify('请先阅读并同意服务协议与隐私政策');
    navigate('app/home');
  };
  return <div className={`mobile-screen auth-page auth-page-v2 ${singleShop?'single-shop-auth-page':''}`} data-screen-label={`${singleShop?'单店 App':'App'} ${register?'注册':'登录'}`}>
    <MobileStatusBar tone="paper"/>
    <div className="auth-capsule"><WechatCapsule/></div>
    <main>
      <div className="auth-brand-lockup">
        {singleShop?<img className="single-shop-auth-logo" src="assets/shop-logo-mountain.png" alt=""/>:<span className="auth-mark">Y</span>}
        <span><b>{singleShop?'山野生活':'YEAH MALL'}</b><small>{singleShop?'生活好物，认真挑选':'SIMPLE COMMERCE'}</small></span>
      </div>
      <section className="auth-intro">
        <span>{register?'CREATE ACCOUNT':'WELCOME BACK'}</span>
        <h1>{register?(singleShop?'加入山野生活':'创建会员账号'):(singleShop?'欢迎回来':'欢迎回来')}</h1>
        <p>{register?'注册后即可使用购物车、优惠券与积分商城':'登录后继续浏览商品并管理你的订单'}</p>
      </section>
      <section className="auth-form">
        <label><span>手机号</span><div className="auth-field"><Icon name="user" size={18}/><input value={mobile} inputMode="numeric" maxLength="11" onChange={(e)=>setMobile(e.target.value.replace(/\D/g,''))} placeholder="请输入手机号"/></div></label>
        <label><span>验证码</span><div className="auth-code-field"><div className="auth-field"><Icon name="check" size={18}/><input value={code} inputMode="numeric" maxLength="6" onChange={(e)=>setCode(e.target.value.replace(/\D/g,''))} placeholder="请输入验证码"/></div><button onClick={()=>notify('验证码已发送')}>获取验证码</button></div></label>
      </section>
      <button className="auth-agreement" onClick={()=>setAccepted(!accepted)}><i className={accepted?'active':''}>{accepted&&<Icon name="check" size={12}/>}</i><span>我已阅读并同意 <b>服务协议</b> 与 <b>隐私政策</b></span></button>
      <button className="auth-submit" onClick={submit}>{register?'注册并登录':'登录'}</button>
      <div className="auth-switch"><span>{register?'已有会员账号？':'还没有会员账号？'}</span><button onClick={()=>navigate(register?'app/login':'app/register')}>{register?'去登录':'立即注册'}</button></div>
    </main>
  </div>;
}

function AppPrototype({ route, navigate, notify, appMode = 'platform' }) {
  const page = routePath(route).split('/')[1] || 'home';
  const singleShop = appMode === 'shop';
  const appNavigate = (target) => navigate(singleShop && target.startsWith('app/') ? target.replace(/^app\//, 'shop-app/') : target);
  return (
    <MobileFrame>
      {page === 'home' && (singleShop ? <SingleShopHome navigate={appNavigate}/> : <AppHome navigate={appNavigate}/>)}
      {page === 'category' && <AppCategory navigate={appNavigate} singleShop={singleShop}/>}
      {page === 'products' && <AppProductList navigate={appNavigate} route={route} singleShop={singleShop}/>}
      {page === 'product' && <AppProduct navigate={appNavigate} notify={notify} singleShop={singleShop}/>}
      {page === 'cart' && (singleShop ? <SingleShopCart navigate={appNavigate} notify={notify}/> : <AppCart navigate={appNavigate} notify={notify}/>)}
      {page === 'confirm' && <AppConfirm navigate={appNavigate} notify={notify} singleShop={singleShop}/>}
      {page === 'orders' && <AppOrders navigate={appNavigate} route={route} notify={notify}/>}
      {page === 'order' && <AppOrder navigate={appNavigate} notify={notify} singleShop={singleShop}/>}
      {page === 'profile' && <AppProfile navigate={appNavigate}/>}
      {page === 'favorites' && <AppCollection navigate={appNavigate} mode="favorites"/>}
      {page === 'history' && <AppCollection navigate={appNavigate} mode="history"/>}
      {page === 'coupons' && <AppCoupons navigate={appNavigate} notify={notify} singleShop={singleShop}/>}
      {page === 'coupon-center' && <AppCouponCenter navigate={appNavigate} notify={notify} singleShop={singleShop}/>}
      {page === 'points' && <AppPoints navigate={appNavigate} singleShop={singleShop}/>}
      {page === 'points-mall' && <AppPointsMall navigate={appNavigate}/>}
      {page === 'seckill' && <AppActivity navigate={appNavigate} type="seckill" singleShop={singleShop}/>}
      {page === 'groupbuy' && <AppActivity navigate={appNavigate} type="groupbuy" singleShop={singleShop}/>}
      {page === 'service' && <AppService navigate={appNavigate} notify={notify} route={route} singleShop={singleShop}/>}
      {page === 'addresses' && <AppAddresses navigate={appNavigate} notify={notify}/>}
      {page === 'reviews' && <AppReviews navigate={appNavigate}/>}
      {page === 'feedback' && <AppFeedback navigate={appNavigate} notify={notify} singleShop={singleShop}/>}
      {page === 'settings' && <AppSettings navigate={appNavigate} notify={notify} singleShop={singleShop}/>}
      {!singleShop && page === 'shops' && <AppShops navigate={appNavigate}/>}
      {!singleShop && page === 'shop' && <AppShop navigate={appNavigate} notify={notify}/>}
      {page === 'pay-result' && <AppPayResult navigate={appNavigate} route={route} notify={notify}/>}
      {page === 'logistics' && <AppLogistics navigate={appNavigate} route={route} notify={notify}/>}
      {page === 'refund' && <AppRefund navigate={appNavigate} route={route} notify={notify}/>}
      {page === 'review-form' && <AppReviewForm navigate={appNavigate} notify={notify}/>}
      {page === 'review-detail' && <AppReviewDetail navigate={appNavigate} notify={notify}/>}
      {page === 'profile-edit' && <AppProfileEdit navigate={appNavigate} notify={notify}/>}
      {page === 'about' && (singleShop ? <SingleShopBrand navigate={appNavigate}/> : <AppAbout navigate={appNavigate}/>)}
      {page === 'legal' && <AppLegal navigate={appNavigate} route={route}/>}
      {page === 'seckill-products' && <AppActivityProducts navigate={appNavigate} type="seckill" singleShop={singleShop}/>}
      {page === 'groupbuy-products' && <AppActivityProducts navigate={appNavigate} type="groupbuy" singleShop={singleShop}/>}
      {page === 'coupon-select' && <AppCouponSelect navigate={appNavigate} singleShop={singleShop}/>}
      {page === 'points-product' && <AppPointsProduct navigate={appNavigate} singleShop={singleShop}/>}
      {page === 'points-confirm' && <AppPointsConfirm navigate={appNavigate}/>}
      {page === 'points-orders' && <AppPointsOrders navigate={appNavigate}/>}
      {page === 'points-order' && <AppPointsOrder navigate={appNavigate} singleShop={singleShop}/>}
      {page === 'seckill-detail' && <AppCampaignDetail navigate={appNavigate} route={route} type="seckill"/>}
      {page === 'groupbuy-detail' && <AppCampaignDetail navigate={appNavigate} route={route} type="groupbuy"/>}
      {page === 'group-select' && <AppGroupSelect navigate={appNavigate}/>}
      {page === 'address-edit' && <AppAddressEdit navigate={appNavigate} route={route} notify={notify}/>}
      {page === 'refund-detail' && <AppRefundDetail navigate={appNavigate} route={route}/>}
      {page === 'notifications' && <AppPreferencePage navigate={appNavigate} type="notifications"/>}
      {page === 'privacy' && <AppPreferencePage navigate={appNavigate} type="privacy"/>}
      {page === 'login' && <AppAuth navigate={appNavigate} mode="login" notify={notify} singleShop={singleShop}/>}
      {page === 'register' && <AppAuth navigate={appNavigate} mode="register" notify={notify} singleShop={singleShop}/>}
      {singleShop && page === 'unavailable' && <SingleShopUnavailable navigate={appNavigate}/>}
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
  return ({ '待发货':'warning','待收货':'info','待成团':'neutral','待评价':'success','已评价':'success','退款中':'danger','已完成':'success' })[status] || 'neutral';
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
  const [surface, setSurface] = useState(['app','shop-app','platform','merchant'].includes(initialSurface) ? initialSurface : 'app');
  const [toast, setToast] = useState('');
  useEffect(() => {
    const next = route.split('/')[0];
    if (['app','shop-app','platform','merchant'].includes(next)) setSurface(next);
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
        {surface === 'app' || surface === 'shop-app'
          ? <AppPrototype route={route} navigate={navigate} notify={notify} appMode={surface === 'shop-app' ? 'shop' : 'platform'}/>
          : <AdminPrototype surface={surface} route={route} navigate={navigate} notify={notify}/>}
      </div>
      <Toast message={toast}/>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<PrototypeApp/>);
