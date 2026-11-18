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

function AppProduct({ navigate, notify, singleShop = false, route = '' }) {
  const product = MOCKS.products[0];
  const [favorite, setFavorite] = useState(true);
  const [sheet, setSheet] = useState(routeParam(route,'sheet') === '1');
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
        <div className="detail-side-actions"><button onClick={() => notify('分享入口已打开') }><Icon name="share"/></button><button aria-label={favorite?'取消收藏':'收藏商品'} aria-pressed={favorite} onClick={() => setFavorite(!favorite)} className={favorite ? 'is-favorite' : ''}><Icon name="heart" filled={favorite}/></button></div>
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
            <header className="purchase-sheet-heading"><div><b>{purchaseMode === 'cart' ? '加入购物车' : '立即购买'}</b><small>选择商品规格和购买数量</small></div></header>
            <button className="purchase-sheet-close" aria-label="关闭规格弹层" onClick={() => setSheet(false)}><Icon name="close" size={16}/></button>
            <div className="sku-summary"><ProductArt type={product.art}/><div><b>¥{product.price}</b><span>库存 {product.stock} 件</span><small>已选 {color} · {spec}</small></div></div>
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
  const visible = (forceEmpty ? [] : MOCKS.orders.filter((item) => tab === '全部' || (tab === '评价' ? item.status === reviewTab : (tab === '退款/售后' ? item.status === '退款中' : item.status === tab))))
    .slice()
    .sort((left,right)=>String(right.createdAt).localeCompare(String(left.createdAt)));
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
  return <div className="mobile-screen" data-screen-label={`App ${title}`}><SimpleMobileHeader title={title} navigate={navigate} back="app/profile"/><main className="mobile-scroll collection-page">{mode === 'history' && !!rows.length && <div className="history-toolbar"><span>今天 · 浏览 {rows.length} 件商品</span><button onClick={()=>setClearConfirm(true)}>清空</button></div>}{!!rows.length?<><div className="product-grid list-grid">{rows.map((item,index) => <button className="product-card" key={item.id} onClick={() => navigate('app/product')}><ProductArt type={item.art}/>{mode === 'favorites' ? <span className="collection-favorite-action" role="button" aria-label="取消收藏" onClick={(event)=>{event.stopPropagation();setFavoriteItems(favoriteItems.filter((row)=>row.id!==item.id));}}><Icon name="heart" size={14} filled/></span> : <span className="collection-history-action" role="button" aria-label="删除浏览记录" onClick={(event)=>{event.stopPropagation();setHistoryItems(historyItems.filter((row)=>row.id!==item.id));}}><Icon name="close" size={13}/></span>}<span className="product-card-name">{item.name}</span><span className="product-card-meta"><b>¥{item.price}</b><small>{mode === 'favorites' ? `${item.favoriteCount} 人收藏` : `10:${String(Math.max(0,26-index)).padStart(2,'0')}`}</small></span></button>)}</div><ListBoundary text={mode==='favorites'?'没有更多收藏了':'没有更多浏览记录了'}/></>:<EmptyState icon={mode==='favorites'?'heart':'clock'} title={mode==='favorites'?'暂未收藏商品':'暂无浏览足迹'} description={mode==='favorites'?'遇到喜欢的商品时，可以先收藏起来':'浏览过的商品会按时间记录在这里'} fill/>}</main>{clearConfirm&&<div className="sheet-mask" onClick={()=>setClearConfirm(false)}><section className="mobile-sheet history-clear-sheet" onClick={(event)=>event.stopPropagation()}><div className="sheet-handle"></div><h2>清空浏览足迹？</h2><p>全部浏览记录将被移除，清空后无法恢复。</p><div><button onClick={()=>setClearConfirm(false)}>取消</button><button className="danger" onClick={()=>{setHistoryItems([]);setClearConfirm(false);}}>确认清空</button></div></section></div>}</div>;
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
  const categories = ['全部','生活好物','卡券权益','会员服务','限定周边'];
  const products = MOCKS.pointsProducts.filter((item)=>category==='全部'||item.category===category);
  return <div className="mobile-screen" data-screen-label="App 积分商城"><SimpleMobileHeader title="积分商城" navigate={navigate} back="app/home"/><main className="mobile-scroll points-mall-page"><button className="points-orders-entry" onClick={()=>navigate('app/points-orders')}><Icon name="order" size={17}/><span>兑换记录</span><Icon name="chevron" size={15}/></button><div className="points-mall-tabs">{categories.map((item)=><button key={item} className={category===item?'active':''} onClick={()=>setCategory(item)}>{item}</button>)}</div>{products.length?<><div className="points-product-list">{products.map((item) => <article key={item.id} role="button" tabIndex="0" onClick={() => navigate('app/points-product')} onKeyDown={(event)=>{if(event.key==='Enter'||event.key===' ') navigate('app/points-product');}}><ProductArt type={item.art}/><div><StatusTicket small>{item.type}</StatusTicket><b>{item.name}</b><span>{item.points} 积分起 · 库存 {item.stock}</span></div><Icon name="chevron" size={16}/></article>)}</div><ListBoundary text="积分商品已全部展示"/></>:<div className="points-mall-empty"><Icon name="box" size={30}/><b>当前分类暂无可兑换商品</b><span>可以看看其他分类的积分好物</span></div>}</main></div>;
}

function AppActivity({ navigate, type, singleShop = false }) {
  const isSeckill = type === 'seckill';
  const sourceRows = isSeckill ? MOCKS.activities.seckill : MOCKS.activities.group;
  const rows = singleShop ? sourceRows.map((row,index)=>({...row,title:isSeckill?['山野晚八点闪购','周末会员专享'][index]||row.title:['山野生活双人团','咖啡好友三人团'][index]||row.title})) : sourceRows;
  if (isSeckill) {
    return <div className="mobile-screen" data-screen-label="App 秒杀活动"><SimpleMobileHeader title="秒杀活动" navigate={navigate}/><main className="mobile-scroll activity-page seckill-activity-page"><section className="seckill-activity-intro"><span>LIMITED SALE</span><h2>限时好价，按活动选购</h2><p>活动结束后仍可查看商品与价格记录，下架后不再展示。</p></section><div className="seckill-activity-list">{rows.map((row)=><article className="seckill-activity-card" key={row.id}><ProductArt type={row.art}/><div><span className="seckill-activity-card-head"><StatusTicket tone={row.status==='进行中'?'danger':'neutral'} small>{row.status}</StatusTicket><small>{row.time}</small></span><h3>{row.title}</h3><p>{row.description}</p><button onClick={()=>navigate(`app/seckill-products?activity=${row.id}&status=${row.status==='进行中'?'active':'ended'}`)}>查看活动商品 <Icon name="chevron" size={13}/></button></div></article>)}</div>{!!rows.length&&<ListBoundary text="没有更多活动了"/>}</main></div>;
  }
  return <div className="mobile-screen" data-screen-label="App 拼团"><SimpleMobileHeader title="拼团活动" navigate={navigate}/><main className="mobile-scroll activity-page groupbuy-activity-page"><section className="groupbuy-rule-note"><span><Icon name="users" size={19}/></span><div><b>活动结束统一结算</b><p>满团后等待活动结束，成团订单才可由商家安排发货</p></div></section><section className="groupbuy-activity-list">{rows.map((row)=><article className="groupbuy-activity-card" key={row.id}><ProductArt type={row.art}/><div><header><StatusTicket tone={row.status==='进行中'?'success':'neutral'} small>{row.status}</StatusTicket><small>{row.minPeople} 人成团</small></header><h3>{row.title}</h3><p>{row.description}</p><time>{row.time}</time><button onClick={()=>navigate(`app/groupbuy-products?activity=${row.id}&status=${row.status==='已结束'?'ended':'active'}`)}>查看活动商品 <Icon name="chevron" size={13}/></button></div></article>)}</section>{!!rows.length&&<ListBoundary text="没有更多活动了"/>}</main></div>;
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
          <RatingStars value={review.rating} size={12} className="stars"/>
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
  const renderStars = (value,onChange) => <RatingStars value={value} size={20} onChange={onChange}/>;
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

function AppReviewDetail({ navigate, route, notify }) {
  const state = routeParam(route,'state') || 'ready';
  const [items,setItems] = useState([
    { id:'review-headphone', art:'headphone', name:'云感降噪头戴耳机', spec:'雾银 · 标准版', rating:5, ratingText:'非常满意', content:'佩戴两小时没有压耳，降噪对通勤很有帮助。包装也很稳妥。', images:['headphone','chair'], replies:[{id:1,side:'merchant',text:'感谢您的认可，耳罩配件可单独购买。',time:'10月24日 09:18'}] },
    { id:'review-serum', art:'serum', name:'氨基酸净澈洁面乳', spec:'120g · 单支', rating:4, ratingText:'满意', content:'泡沫细腻，洗完不紧绷，包装完好。', images:[], replies:[{id:2,side:'merchant',text:'感谢反馈，后续有需要可以继续留言。',time:'10月24日 09:26'},{id:3,side:'buyer',text:'好的，使用一段时间后再来反馈。',time:'10月24日 10:02'}] }
  ]);
  const [drafts,setDrafts] = useState({});
  const [editing,setEditing] = useState({});
  const [preview,setPreview] = useState(null);
  const canReply = (item) => item.replies[item.replies.length-1]?.side === 'merchant';
  const canEditReply = (item) => item.replies[item.replies.length-1]?.side === 'buyer';
  const submitReply = (item) => {
    const content = (drafts[item.id] || '').trim();
    if(!content)return notify('请输入回复内容');
    setItems(items.map((row)=>{
      if(row.id!==item.id)return row;
      const replies = editing[item.id]
        ? row.replies.map((reply,index)=>index===row.replies.length-1?{...reply,text:content,time:'刚刚'}:reply)
        : [...row.replies,{id:Date.now(),side:'buyer',text:content,time:'刚刚'}];
      return {...row,replies};
    }));
    setDrafts({...drafts,[item.id]:''});
    setEditing({...editing,[item.id]:false});
    notify(editing[item.id]?'回复已修改':'回复已发布，等待商家继续回复');
  };
  if(state==='loading')return <div className="mobile-screen" data-screen-label="App 评价详情"><SimpleMobileHeader title="评价详情" navigate={navigate} back="app/orders?tab=已评价"/><main className="review-detail-state"><i></i><b>评价详情加载中</b><span>正在读取评价与回复记录</span></main></div>;
  if(state==='error')return <div className="mobile-screen" data-screen-label="App 评价详情"><SimpleMobileHeader title="评价详情" navigate={navigate} back="app/orders?tab=已评价"/><main className="review-detail-state"><span><Icon name="close" size={22}/></span><b>评价详情加载失败</b><small>网络波动，请稍后重新加载</small><button onClick={()=>notify('正在重新加载评价详情')}>重新加载</button></main></div>;
  return <div className="mobile-screen" data-screen-label="App 评价详情">
    <SimpleMobileHeader title="评价详情" navigate={navigate} back="app/orders?tab=已评价"/>
    <main className="mobile-scroll review-detail-v2">
      <section className="review-detail-meta"><div><small>订单编号</small><b className="mono">YM202610250021</b></div><span><StatusTicket tone="neutral" small>匿名评价</StatusTicket><time>2026-10-25 18:36</time></span></section>
      <section className="review-detail-shop-ratings"><header><h2>店铺评分</h2><span>本次购物体验</span></header>{[['描述相符',5],['物流服务',5],['服务态度',5]].map(([label,value])=><div key={label}><span>{label}</span><RatingStars value={value} size={16}/><b>{value}分</b></div>)}</section>
      {items.map((item)=><section className="review-detail-item" key={item.id}>
        <header className="review-detail-product"><ProductArt type={item.art}/><div><b>{item.name}</b><span>{item.spec}</span></div></header>
        <div className="review-detail-score"><RatingStars value={item.rating} size={16}/><b>{item.ratingText}</b></div>
        <p className="review-detail-content">{item.content}</p>
        {!!item.images.length&&<div className="review-detail-images">{item.images.map((image,index)=><button key={`${image}-${index}`} onClick={()=>setPreview(image)}><ProductArt type={image}/></button>)}</div>}
        {!!item.replies.length&&<div className="review-detail-thread">{item.replies.map((reply)=><article className={reply.side} key={reply.id}><header><b>{reply.side==='merchant'?'商家回复':'我的回复'}</b><time>{reply.time}</time></header><p>{reply.text}</p></article>)}</div>}
        {canReply(item)||editing[item.id]?<div className="review-detail-reply"><textarea value={drafts[item.id] || ''} maxLength="500" onChange={(event)=>setDrafts({...drafts,[item.id]:event.target.value})} placeholder={editing[item.id]?'修改你的最新回复':'回复商家，继续沟通使用体验'}></textarea><footer><small>{(drafts[item.id] || '').length}/500</small><span>{editing[item.id]&&<button className="secondary" onClick={()=>{setDrafts({...drafts,[item.id]:''});setEditing({...editing,[item.id]:false});}}>取消</button>}<button onClick={()=>submitReply(item)}>{editing[item.id]?'保存修改':'发表回复'}</button></span></footer></div>:canEditReply(item)?<div className="review-detail-edit-ready"><span>你已回复，可修改最新回复</span><button onClick={()=>{const latest=item.replies[item.replies.length-1];setDrafts({...drafts,[item.id]:latest?.text||''});setEditing({...editing,[item.id]:true});}}>修改回复</button></div>:<div className="review-detail-waiting"><Icon name="clock" size={15}/><span>等待商家回复后可继续留言</span></div>}
      </section>)}
      {!items.length&&<EmptyState icon="message" title="暂无评价信息" description="评价发布后会显示在这里" fill/>}
      <div className="review-detail-end"><ListBoundary/></div>
    </main>
    {preview&&<div className="image-preview-mask" onClick={()=>setPreview(null)}><button onClick={()=>setPreview(null)}>×</button><ProductArt type={preview}/><span>点击任意位置关闭</span></div>}
  </div>;
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

function AppActivityProducts({ navigate, route = '', type, singleShop = false }) {
  const title = type==='seckill'?'秒杀商品':'拼团商品';
  const sessions = [
    { id:'all', name:'全部', time:'10:00–22:00', status:'进行中' },
    { id:'morning', name:'10:00 场', time:'10:00–12:00', status:'已结束' },
    { id:'afternoon', name:'14:00 场', time:'14:00–16:00', status:'进行中' },
    { id:'evening', name:'20:00 场', time:'20:00–22:00', status:'未开始' }
  ];
  const [activeSession,setActiveSession] = useState('afternoon');
  const activityEnded = routeParam(route,'status') === 'ended';
  const seckillRows = MOCKS.products.slice(0,8).map((item,index)=>({
    ...item,
    sessionId:['morning','afternoon','afternoon','evening'][index%4],
    shop:['Yeah 数码生活馆','山野生活店','Yeah 美护精选','山野咖啡'][index%4],
    seckillPrice:Math.max(39,item.price-[100,130,50,20][index%4])
  }));
  const visibleRows = activeSession==='all' ? seckillRows : seckillRows.filter((item)=>item.sessionId===activeSession);
  const openSeckillProduct = (item, session, event) => {
    event?.stopPropagation();
    const status = activityEnded || session?.status === '已结束' ? 'ended' : session?.status === '未开始' ? 'upcoming' : 'active';
    navigate(`app/seckill-detail?session=${item.sessionId}&status=${status}`);
  };
  if (type === 'seckill') {
    return <div className="mobile-screen" data-screen-label="App 秒杀商品"><SimpleMobileHeader title="秒杀商品" navigate={navigate} back="app/seckill"/><main className="mobile-scroll activity-products-page seckill-products-page"><section className="seckill-products-summary"><span><StatusTicket tone={activityEnded?'neutral':'danger'} small>{activityEnded?'已结束':'进行中'}</StatusTicket><small>活动时间 · 10月27日 10:00–22:00</small></span><h2>{singleShop?'山野生活限时闪购':'秋日品质生活秒杀'}</h2><p>{activityEnded?'活动已经结束，商品仅供查看。':'选择当前场次商品，秒杀库存售完即止。'}</p></section><nav className="seckill-session-tabs" aria-label="秒杀场次">{sessions.map((session)=><button key={session.id} className={activeSession===session.id?'active':''} onClick={()=>setActiveSession(session.id)}><b>{session.name}</b><small>{session.status}</small></button>)}</nav><section className="seckill-product-list">{visibleRows.map((item)=>{const session=sessions.find((candidate)=>candidate.id===item.sessionId);const purchasable=!activityEnded&&session?.status==='进行中';return <article className="seckill-product-row" key={item.id} onClick={(event)=>openSeckillProduct(item,session,event)}><ProductArt type={item.art}/><div className="seckill-product-copy"><h3>{item.name}</h3><p>{item.shop} · {session?.name}</p><span><strong>¥{item.seckillPrice}</strong><del>¥{item.price}</del><small>已售 {item.sales}</small></span></div><button className={purchasable?'':'ended'} onClick={(event)=>openSeckillProduct(item,session,event)}>{purchasable?'抢购':'查看'}</button></article>;})}</section>{!!visibleRows.length&&<ListBoundary text="活动商品已全部展示"/>}</main></div>;
  }
  const groupActivityId = routeParam(route,'activity') || 'g-1';
  const groupActivity = MOCKS.activities.group.find((item)=>item.id===groupActivityId) || MOCKS.activities.group[0];
  const groupActivityEnded = routeParam(route,'status')==='ended' || groupActivity.status==='已结束';
  const groupActivityTitle = singleShop
    ? (groupActivity.id==='g-1'?'山野生活双人团':'咖啡好友三人团')
    : groupActivity.title;
  const groupRows = MOCKS.products.slice(0,8).map((item,index)=>{
    const groupPrice = Math.max(39,item.price-[30,60,20,45][index%4]);
    return {
      ...item,
      groupPrice,
      maxGroupPrice:index%3===0?groupPrice:groupPrice+[20,30,40][index%3],
      groupStock:[48,36,90,24][index%4]
    };
  });
  return <div className="mobile-screen" data-screen-label={`App ${title}`}><SimpleMobileHeader title={title} navigate={navigate} back="app/groupbuy"/><main className="mobile-scroll activity-products-page groupbuy-products-page"><section className="groupbuy-products-summary"><span><StatusTicket tone={groupActivityEnded?'neutral':'success'} small>{groupActivityEnded?'已结束':'进行中'}</StatusTicket><small>{groupActivity.time}</small></span><h2>{groupActivityTitle}</h2><p>{groupActivityEnded?'活动已经结束，商品与拼团结果仍可查看。':`${groupActivity.minPeople} 人成团，活动结束后仅成团订单可由商家安排发货。`}</p></section><section className="groupbuy-product-list">{groupRows.map((item)=><article className="groupbuy-product-row" key={item.id}><ProductArt type={item.art}/><div className="groupbuy-product-copy"><h3>{item.name}</h3><p>{groupActivity.minPeople} 人成团 · 已售 {item.sales}</p><span><strong>¥{item.groupPrice}</strong>{item.maxGroupPrice>item.groupPrice&&<em>起</em>}<del>¥{item.price}</del><small>库存 {item.groupStock}</small></span></div><button className={groupActivityEnded?'ended':''} onClick={()=>navigate(`app/groupbuy-detail?status=${groupActivityEnded?'ended':'active'}`)}>{groupActivityEnded?'查看':'去拼团'}</button></article>)}</section><ListBoundary text="活动商品已全部展示"/></main></div>;
}

function AppCouponSelect({ navigate, singleShop = false }) {
  const shopName = singleShop ? '山野生活' : 'Yeah 数码生活馆';
  const rows = [
    {...MOCKS.coupons[0],shop:shopName,name:'全店满 199 减 20',useRangeText:'店铺通用'},
    {...MOCKS.coupons[1],shop:shopName,name:'数码配件 9 折券',useRangeText:'指定商品'},
    {...MOCKS.coupons[2],shop:shopName,name:'新客无门槛券',useRangeText:'指定分类'}
  ];
  const [selected,setSelected] = useState(null);
  return <div className="mobile-screen" data-screen-label="App 选择优惠券">
    <SimpleMobileHeader title="选择优惠券" navigate={navigate} back="app/confirm"/>
    <main className="mobile-scroll coupon-select-page">
      <section className="coupon-order-context"><span><Icon name="store" size={17}/><b>{shopName}</b></span><small>{rows.length} 张可用</small></section>
      <button className={`coupon-none-option ${selected===null?'active':''}`} onClick={()=>setSelected(null)}><span><b>不使用优惠券</b><small>按当前订单金额结算</small></span><i className="coupon-select-radio">{selected===null&&<Icon name="check" size={12}/>}</i></button>
      <section className="coupon-select-list">{rows.map((coupon)=><button className={`coupon-select-card-v2 ${selected===coupon.id?'active':''}`} key={coupon.id} onClick={()=>setSelected(coupon.id)}>
        <span className="coupon-select-value"><b>{coupon.value}</b><small>{coupon.condition}</small></span>
        <span className="coupon-select-copy"><strong>{coupon.name}</strong><span><i>{coupon.useRangeText}</i><small>{coupon.shop}</small></span><small>{coupon.endAt} 到期</small></span>
        <i className="coupon-select-radio">{selected===coupon.id&&<Icon name="check" size={12}/>}</i>
      </button>)}</section>
    </main>
    <div className="single-bottom-action"><button onClick={()=>navigate('app/confirm')}>{selected===null?'不使用优惠券':'确认使用'}</button></div>
  </div>;
}

function AppPointsProduct({ navigate, singleShop = false }) {
  const item = MOCKS.pointsProducts[0];
  const skus = [
    {id:'points-sku-1',name:'标准款',points:600,stock:36,art:'coffee'},
    {id:'points-sku-2',name:'加厚款',points:880,stock:12,art:'coffee'},
    {id:'points-sku-3',name:'限定配色',points:1200,stock:0,art:'coffee'}
  ];
  const [selectedSkuId,setSelectedSkuId] = useState('points-sku-1');
  const [quantity,setQuantity] = useState(1);
  const selectedSku = skus.find((sku)=>sku.id===selectedSkuId) || skus[0];
  const selectSku = (sku) => { if (sku.stock<=0) return; setSelectedSkuId(sku.id); setQuantity(1); };
  return <div className="mobile-screen" data-screen-label="App 积分商品"><SimpleMobileHeader title="积分商品" navigate={navigate} back="app/points-mall"/><main className="mobile-scroll points-product-page points-product-v2"><ProductArt type={item.art}/><section className="points-product-summary"><div className="points-product-tags"><StatusTicket small>{item.type}</StatusTicket><StatusTicket tone="success" small>物流配送</StatusTicket></div><h1>{item.name}</h1><p>环保棉帆布材质，适合日常通勤与购物使用。</p><b>{selectedSku.points} <small>积分/件</small></b></section><section className="points-sku-section"><h2>选择规格</h2><div>{skus.map((sku)=><button key={sku.id} disabled={sku.stock<=0} className={selectedSkuId===sku.id?'active':''} onClick={()=>selectSku(sku)}><ProductArt type={sku.art}/><span><b>{sku.name}</b><small>{sku.stock>0?`${sku.points} 积分 · 库存 ${sku.stock}`:'已兑完'}</small></span><i>{selectedSkuId===sku.id&&<Icon name="check" size={12}/>}</i></button>)}</div></section><section className="points-quantity-section"><span><b>兑换数量</b><small>每位会员限兑 2 件</small></span><QuantityControl value={quantity} setValue={setQuantity} max={Math.min(selectedSku.stock,2)}/></section><section className="points-rules-section"><h2>兑换说明</h2><div><span>每人限兑</span><b>2 件</b></div><div><span>兑换截止</span><b>2026-12-31 23:59</b></div><p>兑换后由{singleShop?'店铺':'平台'}安排物流发货，请在确认兑换页填写收货地址。</p></section><section className="points-detail-section"><h2>商品详情</h2><ProductArt type="coffee"/><p>轻便耐用的品牌帆布袋，适合通勤、购物与日常收纳。商品颜色与尺寸以实际发货为准。</p></section></main><div className="points-product-action"><span><small>合计</small><b>{selectedSku.points*quantity} 积分</b></span><button onClick={()=>navigate('app/points-confirm')}>立即兑换</button></div></div>;
}

function AppPointsConfirm({ navigate, route }) {
  const type = routeParam(route,'type') || 'logistics';
  const isLogistics = type==='logistics';
  const isCoupon = type==='coupon';
  const product = isCoupon ? {name:'全店 20 元券',sku:'20 元券',points:300,art:'serum'} : type==='manual' ? {name:'专属客服权益',sku:'30 天权益',points:1200,art:'headphone'} : {name:'品牌帆布袋',sku:'标准款',points:600,art:'coffee'};
  const fulfillment = isCoupon ? '自动发券' : type==='manual' ? '人工履约' : '物流配送';
  return <div className="mobile-screen" data-screen-label="App 确认兑换"><SimpleMobileHeader title="确认兑换" navigate={navigate} back="app/points-product"/><main className="mobile-scroll confirm-scroll points-confirm-v2">{isLogistics&&<button className="address-panel" onClick={()=>navigate('app/addresses')}><span className="address-icon"><Icon name="location"/></span><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div><Icon name="chevron"/></button>}<section className="confirm-shop-card"><div className="confirm-product"><ProductArt type={product.art}/><div><b>{product.name}</b><span>{product.sku}</span><div><strong>{product.points} 积分</strong><small>×1</small></div></div></div></section><section className="price-summary"><div><span>履约方式</span><b>{fulfillment}</b></div><div><span>当前积分</span><b>1,280</b></div><div><span>兑换积分</span><b className="saving">{product.points}</b></div><div className="summary-total"><span>兑换后剩余</span><strong>{1280-product.points}</strong></div></section><section className="points-confirm-notice"><h3>兑换须知</h3><p>积分兑换成功后将生成独立的积分订单。</p><p>{isCoupon?'优惠券会自动发放，兑换成功后不可取消。':'订单处于待履约状态时可以取消，积分和库存会自动退回。'}</p></section></main><div className="single-bottom-action"><button onClick={()=>navigate(`app/points-order?status=${isCoupon?'completed':'pending'}&type=${type}`)}>确认兑换</button></div></div>;
}

function AppPointsOrders({ navigate }) {
  const [status,setStatus] = useState('全部');
  const rows=[
    {id:'PX20261027001',name:'品牌帆布袋',sku:'标准款',points:600,status:'待履约',tone:'warning',art:'coffee',time:'2026-10-27 10:18',fulfillment:'物流配送',route:'status=pending&type=logistics'},
    {id:'PX20261026006',name:'品牌帆布袋',sku:'加厚款',points:880,status:'已发货',tone:'neutral',art:'coffee',time:'2026-10-26 16:42',fulfillment:'物流配送',route:'status=shipped&type=logistics'},
    {id:'PX20261020008',name:'全店 20 元券',sku:'20 元券',points:300,status:'已完成',tone:'success',art:'serum',time:'2026-10-20 09:35',fulfillment:'自动发券',route:'status=completed&type=coupon'},
    {id:'PX20261018003',name:'专属客服权益',sku:'30 天权益',points:1200,status:'已取消',tone:'neutral',art:'headphone',time:'2026-10-18 14:06',fulfillment:'人工履约',route:'status=canceled&type=manual'}
  ];
  const visible = status==='全部' ? rows : rows.filter((row)=>row.status===status);
  return <div className="mobile-screen" data-screen-label="App 积分订单"><SimpleMobileHeader title="兑换记录" navigate={navigate} back="app/points-mall"/><main className="mobile-scroll points-orders-page"><div className="points-order-tabs">{['全部','待履约','已发货','已完成','已取消'].map((item)=><button key={item} className={status===item?'active':''} onClick={()=>setStatus(item)}>{item}</button>)}</div><div className="points-order-list">{visible.map((row)=><article key={row.id} role="button" tabIndex="0" onClick={()=>navigate(`app/points-order?${row.route}`)}><header><span className="mono">{row.id}</span><StatusTicket tone={row.tone} small>{row.status}</StatusTicket></header><div className="points-order-product"><ProductArt type={row.art}/><span><b>{row.name}</b><small>{row.sku} × 1</small><strong>{row.points} 积分</strong></span><Icon name="chevron" size={16}/></div><footer><time>{row.time}</time><span>{row.fulfillment}</span></footer></article>)}</div>{visible.length?<ListBoundary text="没有更多兑换记录了"/>:<div className="points-orders-empty"><Icon name="order" size={30}/><b>暂无相关兑换记录</b><span>兑换积分商品后可在这里查看进度</span></div>}</main></div>;
}

function AppPointsOrder({ navigate, route, singleShop = false }) {
  const status = routeParam(route,'status') || 'pending';
  const type = routeParam(route,'type') || 'logistics';
  const stateMap = {
    pending:{title:'待履约',hint:type==='logistics'?`等待${singleShop?'店铺':'平台'}发货`:'平台正在处理本次兑换',icon:'clock',tone:'pending'},
    shipped:{title:'已发货',hint:'商品正在运输中，请留意物流信息',icon:'truck',tone:'shipped'},
    completed:{title:'已完成',hint:type==='coupon'?'优惠券已发放至你的账户':'本次积分兑换已经完成',icon:'check',tone:'completed'},
    canceled:{title:'已取消',hint:'积分和商品库存已经退回',icon:'close',tone:'canceled'}
  };
  const state = stateMap[status] || stateMap.pending;
  const isLogistics = type==='logistics';
  const isCoupon = type==='coupon';
  const product = isCoupon ? {name:'全店 20 元券',sku:'20 元券',points:300,art:'serum'} : type==='manual' ? {name:'专属客服权益',sku:'30 天权益',points:1200,art:'headphone'} : {name:'品牌帆布袋',sku:status==='shipped'?'加厚款':'标准款',points:status==='shipped'?880:600,art:'coffee'};
  return <div className="mobile-screen" data-screen-label="App 积分订单详情"><SimpleMobileHeader title="积分订单详情" navigate={navigate} back="app/points-orders"/><main className={`mobile-scroll points-order-detail-v2 ${state.tone}`}><section className="points-order-state"><span><Icon name={state.icon} size={26}/></span><div><h2>{state.title}</h2><p>{state.hint}</p></div></section><section className="points-order-goods"><ProductArt type={product.art}/><div><b>{product.name}</b><span>{product.sku} × 1</span><strong>{product.points} 积分</strong></div></section>{isLogistics&&<section className="points-order-address"><Icon name="location" size={20}/><div><b>林可&nbsp;&nbsp;138****7621</b><p>浙江省杭州市西湖区文三路 88 号 3 幢 502</p></div></section>}<section className="points-order-info"><h3>订单信息</h3><div><span>积分订单号</span><b>{status==='shipped'?'PX20261026006':'PX20261027001'}</b></div><div><span>履约方式</span><b>{isCoupon?'自动发券':type==='manual'?'人工履约':'物流配送'}</b></div><div><span>兑换积分</span><b>{product.points}</b></div><div><span>兑换时间</span><b>2026-10-27 10:18</b></div>{status==='completed'&&<div><span>完成时间</span><b>2026-10-27 10:19</b></div>}{status==='canceled'&&<><div><span>取消时间</span><b>2026-10-18 14:12</b></div><div><span>取消原因</span><b>会员主动取消</b></div></>}</section>{status==='shipped'&&<section className="points-order-info"><h3>物流信息</h3><div><span>物流公司</span><b>顺丰速运</b></div><div><span>物流单号</span><b>SF1234567890</b></div><div><span>发货时间</span><b>2026-10-27 08:30</b></div></section>}{isCoupon&&status==='completed'&&<button className="points-coupon-link" onClick={()=>navigate('app/coupons')}><span><Icon name="ticket" size={20}/><b>优惠券已发放</b></span><Icon name="chevron" size={16}/></button>}{type==='manual'&&status!=='canceled'&&<section className="points-order-info"><h3>履约信息</h3><p>平台将在 1 个工作日内完成权益开通，请留意消息通知。</p></section>}{status==='canceled'&&<section className="points-refund-note"><Icon name="check" size={18}/><span><b>{product.points} 积分已退回</b><small>商品库存已同步恢复</small></span></section>}</main>{status==='pending'&&!isCoupon&&<div className="single-bottom-action points-order-action"><button className="secondary" onClick={()=>navigate('app/points-orders')}>取消兑换</button></div>}{status==='shipped'&&<div className="single-bottom-action points-order-action"><button onClick={()=>navigate('app/points-orders')}>确认收货</button></div>}</div>;
}

function AppSeckillDetail({ navigate, route, notify }) {
  const product = MOCKS.products[0];
  const sessionMap = { morning:'10:00 场', afternoon:'14:00 场', evening:'20:00 场' };
  const sessionName = sessionMap[routeParam(route,'session')] || '14:00 场';
  const activityEnded = routeParam(route,'status') === 'ended';
  const activityUpcoming = routeParam(route,'status') === 'upcoming';
  const skus = [
    { id:'sku-1', name:'雾银 · 标准版', price:299, originPrice:399, stock:12 },
    { id:'sku-2', name:'曜石黑 · 标准版', price:309, originPrice:399, stock:6 },
    { id:'sku-3', name:'雾银 · 长续航版', price:349, originPrice:459, stock:0 }
  ];
  const [favorite,setFavorite] = useState(false);
  const [sheet,setSheet] = useState(routeParam(route,'sheet') === '1');
  const [selectedSkuId,setSelectedSkuId] = useState('sku-1');
  const [sectionTabsVisible,setSectionTabsVisible] = useState(false);
  const [activeSection,setActiveSection] = useState('base');
  const detailScrollRef = useRef(null);
  const reviewSectionRef = useRef(null);
  const storySectionRef = useRef(null);
  const selectedSku = skus.find((item)=>item.id===selectedSkuId) || skus[0];
  const handleDetailScroll = (event) => {
    const scrollTop = event.currentTarget.scrollTop;
    const marker = scrollTop + 128;
    setSectionTabsVisible(scrollTop > 250);
    if (storySectionRef.current && marker >= storySectionRef.current.offsetTop) setActiveSection('detail');
    else if (reviewSectionRef.current && marker >= reviewSectionRef.current.offsetTop) setActiveSection('review');
    else setActiveSection('base');
  };
  const scrollToSection = (section) => {
    const target = section==='review' ? reviewSectionRef.current : section==='detail' ? storySectionRef.current : null;
    detailScrollRef.current?.scrollTo({ top:target?Math.max(target.offsetTop-112,0):0, behavior:'smooth' });
    setActiveSection(section);
  };
  return <div className="mobile-screen product-detail campaign-product-detail" data-screen-label="App 秒杀商品详情">
    <MobileStatusBar tone="transparent"/>
    <div className="detail-floatbar"><button onClick={()=>navigate('app/seckill-products')}><Icon name="back"/></button><WechatCapsule/></div>
    <div className={`product-detail-nav-surface ${sectionTabsVisible?'visible':''}`}></div>
    <nav className={`product-section-tabs ${sectionTabsVisible?'visible':''}`}>{[['base','宝贝'],['review','评价'],['detail','详情']].map(([key,label])=><button key={key} className={activeSection===key?'active':''} onClick={()=>scrollToSection(key)}>{label}</button>)}</nav>
    <main ref={detailScrollRef} className="mobile-scroll detail-scroll-content campaign-product-scroll" onScroll={handleDetailScroll}>
      <div className="detail-gallery campaign-detail-gallery"><div className="detail-side-actions"><button onClick={()=>notify?.('分享入口已打开')}><Icon name="share"/></button><button aria-label={favorite?'取消收藏':'收藏商品'} aria-pressed={favorite} className={favorite?'is-favorite':''} onClick={()=>setFavorite(!favorite)}><Icon name="heart" filled={favorite}/></button></div><ProductArt type={product.art}/><div className="gallery-dots"><button className="active"></button><button></button><button></button></div></div>
      <section className={`seckill-detail-band ${activityEnded||activityUpcoming?'is-inactive':''}`}><span><b>{sessionName}</b><small>限时秒杀</small></span><span><small>{activityEnded?'本场状态':activityUpcoming?'距本场开始':'距本场结束'}</small><b>{activityEnded?'已结束':activityUpcoming?'02 : 18 : 00':'01 : 42 : 18'}</b></span></section>
      <section className="detail-main-info seckill-detail-info"><div className="price-line"><b>¥{selectedSku.price}</b><del>¥{selectedSku.originPrice}</del></div><h1>{product.name}</h1><p>{product.subtitle}</p><div className="detail-metrics"><span>已售 {product.sales}</span><span>好评率 {product.rating}%</span><span>剩余库存 18</span></div></section>
      <button className="detail-choice-row detail-choice-row-end" onClick={()=>setSheet(true)}><span className="choice-label">已选</span><b>{selectedSku.name} · 1 件</b><Icon name="chevron" size={18}/></button>
      <section className="seckill-rule-strip"><span><Icon name="clock" size={16}/></span><div><b>秒杀规则</b><p>同一秒杀商品每人限购 1 件，库存售完即止。</p></div></section>
      <section ref={reviewSectionRef} className="review-block"><div className="section-heading compact"><div><span className="shelf-mark"></span><h2>买家评价</h2></div><button onClick={()=>navigate('app/reviews')}>全部 {MOCKS.reviews.length*63} <Icon name="chevron" size={15}/></button></div>{MOCKS.reviews.slice(0,2).map((review)=><article className="review-item" key={review.id}><div className="review-user"><Avatar name={review.user}/><b>{review.user}</b><span>{review.time}</span></div><p>{review.content}</p>{!!review.images?.length&&<div className="review-item-images">{review.images.map((image,index)=><button key={`${review.id}-${index}`} onClick={()=>notify?.('已打开评价图片预览')}><ProductArt type={image}/></button>)}</div>}</article>)}</section>
      <section ref={storySectionRef} className="product-story"><div className="section-heading compact"><div><span className="shelf-mark"></span><h2>商品详情</h2></div></div><div className="rich-text-prototype"><ProductArt type="headphone"/><h3>柔和包覆，长时间佩戴也轻松</h3><p>记忆海绵耳罩与轻量化头梁保持稳定贴合，秒杀商品详情与原商品图文信息保持一致。</p><ProductArt type="headphone" className="rich-text-detail-art"/><h3>通勤与居家，都保持沉浸聆听</h3><p>高清语音、低延迟连接与长续航满足多场景使用，具体内容以商家发布信息为准。</p></div></section>
    </main>
    <div className="mobile-action-bar campaign-detail-action"><button className="action-icon" onClick={()=>navigate('app/shop')}><Icon name="store"/><span>店铺</span></button><button className="action-icon" onClick={()=>navigate('app/service')}><Icon name="message"/><span>客服</span></button><button className={`primary-action ${activityEnded||activityUpcoming?'view-only':''}`} onClick={()=>setSheet(true)}>{activityEnded||activityUpcoming?'查看规格':'立即抢购'}</button></div>
    {sheet&&<div className="sheet-mask" onClick={()=>setSheet(false)}><section className="mobile-sheet product-spec-sheet campaign-spec-sheet seckill-spec-sheet" onClick={(event)=>event.stopPropagation()}><div className="sheet-handle"></div><header className="purchase-sheet-heading"><div><b>{activityEnded?'活动已结束':activityUpcoming?'活动未开始':'秒杀抢购'}</b><small>{sessionName} · 每人限购 1 件</small></div></header><button className="purchase-sheet-close" aria-label="关闭规格弹层" onClick={()=>setSheet(false)}><Icon name="close" size={16}/></button><div className="sku-summary"><ProductArt type={product.art}/><div><span className="campaign-sheet-price"><b>¥{selectedSku.price}</b><del>¥{selectedSku.originPrice}</del></span><span>库存 {selectedSku.stock} 件</span><small>已选 {selectedSku.name}</small></div></div><div className="option-group campaign-sku-options seckill-sku-options"><label>选择规格</label><div>{skus.map((sku)=><button key={sku.id} disabled={sku.stock===0} className={selectedSkuId===sku.id?'active':''} onClick={()=>setSelectedSkuId(sku.id)}><span>{sku.name}</span><small>{sku.stock===0?'已售罄':`¥${sku.price} · 剩余 ${sku.stock} 件`}</small></button>)}</div></div><div className="quantity-row campaign-fixed-quantity"><label>购买数量</label><b>1 件</b><small>秒杀商品数量固定为 1 件</small></div><button className="sheet-confirm" disabled={activityEnded||activityUpcoming} onClick={()=>{setSheet(false);navigate('app/confirm');}}>{activityEnded?'活动已结束':activityUpcoming?'活动未开始':'确认抢购'}</button></section></div>}
  </div>;
}

function AppCampaignDetail({ navigate, route, type, notify }) {
  if (type === 'seckill') return <AppSeckillDetail navigate={navigate} route={route} notify={notify}/>;
  const selectedGroup = routeParam(route,'group');
  const activityEnded = routeParam(route,'status') === 'ended';
  const product = MOCKS.products[2];
  const groupSkus = [
    {id:'group-sku-1',name:'30ml · 单瓶',price:139,originPrice:168,stock:24},
    {id:'group-sku-2',name:'30ml · 双瓶装',price:259,originPrice:336,stock:10},
    {id:'group-sku-3',name:'旅行装 · 10ml',price:49,originPrice:69,stock:0}
  ];
  const [sheet,setSheet] = useState(routeParam(route,'sheet') === '1');
  const [selectedSkuId,setSelectedSkuId] = useState('group-sku-1');
  const [sectionTabsVisible,setSectionTabsVisible] = useState(false);
  const [activeSection,setActiveSection] = useState('base');
  const detailScrollRef = useRef(null);
  const reviewSectionRef = useRef(null);
  const storySectionRef = useRef(null);
  const selectedSku = groupSkus.find((item)=>item.id===selectedSkuId) || groupSkus[0];
  const handleDetailScroll = (event) => {
    const scrollTop = event.currentTarget.scrollTop;
    const marker = scrollTop + 128;
    setSectionTabsVisible(scrollTop > 250);
    if (storySectionRef.current && marker >= storySectionRef.current.offsetTop) setActiveSection('detail');
    else if (reviewSectionRef.current && marker >= reviewSectionRef.current.offsetTop) setActiveSection('review');
    else setActiveSection('base');
  };
  const scrollToSection = (section) => {
    const target = section==='review' ? reviewSectionRef.current : section==='detail' ? storySectionRef.current : null;
    detailScrollRef.current?.scrollTo({ top:target?Math.max(target.offsetTop-112,0):0, behavior:'smooth' });
    setActiveSection(section);
  };
  const openPurchaseSheet = () => {
    if (activityEnded) { setSheet(true); return; }
    if (!selectedGroup) { navigate('app/group-select'); return; }
    setSheet(true);
  };
  return <div className="mobile-screen product-detail campaign-product-detail campaign-detail-page" data-screen-label="App 拼团商品详情">
    <MobileStatusBar tone="transparent"/>
    <div className="detail-floatbar"><button onClick={()=>navigate('app/groupbuy-products')}><Icon name="back"/></button><WechatCapsule/></div>
    <div className={`product-detail-nav-surface ${sectionTabsVisible?'visible':''}`}></div>
    <nav className={`product-section-tabs ${sectionTabsVisible?'visible':''}`}>{[['base','宝贝'],['review','评价'],['detail','详情']].map(([key,label])=><button key={key} className={activeSection===key?'active':''} onClick={()=>scrollToSection(key)}>{label}</button>)}</nav>
    <main ref={detailScrollRef} className="mobile-scroll detail-scroll-content campaign-product-scroll groupbuy-detail-scroll" onScroll={handleDetailScroll}>
      <div className="campaign-art"><ProductArt type="serum"/><StatusTicket tone={activityEnded?'neutral':'danger'}>{activityEnded?'活动已结束':'双人成团'}</StatusTicket></div>
      <section className="campaign-copy"><div><strong>¥{selectedSku.price}</strong><del>¥{selectedSku.originPrice}</del></div><h1>{product.name}</h1><p>{activityEnded?'活动已经结束，商品与拼团结果仅供查看':'活动结束后仅成团订单可由商家安排发货'}</p></section>
      <button className={`group-choice ${activityEnded?'ended':''}`} disabled={activityEnded} onClick={()=>navigate('app/group-select')}><span><small>拼团方式</small><b>{activityEnded?'活动已结束，不可继续参团':selectedGroup?`已选择 ${selectedGroup} 号团`:'请选择现有团或发起新团'}</b></span><Icon name="chevron" size={17}/></button>
      <section className="campaign-rule"><h3>活动规则</h3><p>每位用户在同一活动中仅可参与一个团；待支付即占席，未满团不能发货。</p></section>
      <section ref={reviewSectionRef} className="review-block"><div className="section-heading compact"><div><span className="shelf-mark"></span><h2>买家评价</h2></div><button onClick={()=>navigate('app/reviews')}>全部 {MOCKS.reviews.length*63} <Icon name="chevron" size={15}/></button></div>{MOCKS.reviews.slice(0,2).map((review)=><article className="review-item" key={review.id}><div className="review-user"><Avatar name={review.user}/><b>{review.user}</b><span>{review.time}</span></div><p>{review.content}</p>{!!review.images?.length&&<div className="review-item-images">{review.images.map((image,index)=><button key={`${review.id}-${index}`} onClick={()=>notify?.('已打开评价图片预览')}><ProductArt type={image}/></button>)}</div>}</article>)}</section>
      <section ref={storySectionRef} className="product-story"><div className="section-heading compact"><div><span className="shelf-mark"></span><h2>商品详情</h2></div></div><div className="rich-text-prototype"><ProductArt type="serum"/><h3>轻盈质地，日常护理更从容</h3><p>拼团商品复用原商品的图文详情、评价和店铺信息，活动结束后仍可查看商品资料。</p><ProductArt type="serum" className="rich-text-detail-art"/><h3>规格与活动价格清晰展示</h3><p>购买前请确认拼团方式与所选规格，成团订单将在活动结束后由商家统一安排发货。</p></div></section>
    </main>
    <div className="single-bottom-action"><button className={activityEnded?'view-only':''} onClick={openPurchaseSheet}>{activityEnded?'查看规格':selectedGroup?'立即拼团':'先选择拼团方式'}</button></div>
    {sheet&&<div className="sheet-mask" onClick={()=>setSheet(false)}><section className="mobile-sheet product-spec-sheet campaign-spec-sheet groupbuy-spec-sheet" onClick={(event)=>event.stopPropagation()}><div className="sheet-handle"></div><header className="purchase-sheet-heading"><div><b>{activityEnded?'活动已结束':'拼团购买'}</b><small>{activityEnded?'历史拼团规格仅供查看':`双人成团 · 已选择 ${selectedGroup} 号团`}</small></div></header><button className="purchase-sheet-close" aria-label="关闭规格弹层" onClick={()=>setSheet(false)}><Icon name="close" size={16}/></button><div className="sku-summary"><ProductArt type={product.art}/><div><span className="campaign-sheet-price"><b>¥{selectedSku.price}</b><del>¥{selectedSku.originPrice}</del></span><span>拼团库存 {selectedSku.stock} 件</span><small>已选 {selectedSku.name}</small></div></div><div className="option-group campaign-sku-options groupbuy-sku-options"><label>选择规格</label><div>{groupSkus.map((sku)=><button key={sku.id} disabled={sku.stock===0} className={selectedSkuId===sku.id?'active':''} onClick={()=>setSelectedSkuId(sku.id)}><span>{sku.name}</span><small>{sku.stock===0?'已售罄':`¥${sku.price} · 库存 ${sku.stock} 件`}</small></button>)}</div></div><div className="campaign-purchase-context"><Icon name="users" size={18}/><span><b>{activityEnded?'拼团活动已结束':`${selectedGroup} 号团 · 双人成团`}</b><small>{activityEnded?'订单与拼团结果仍可查看，不可继续购买':'支付后占用 1 个席位，活动结束统一结算'}</small></span></div><div className="quantity-row campaign-fixed-quantity"><label>购买数量</label><b>1 件</b><small>拼团商品数量固定为 1 件</small></div><button className="sheet-confirm" disabled={activityEnded} onClick={()=>{setSheet(false);navigate('app/confirm');}}>{activityEnded?'活动已结束':'确认拼团'}</button></section></div>}
  </div>;
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
      {page === 'product' && <AppProduct navigate={appNavigate} notify={notify} singleShop={singleShop} route={route}/>}
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
      {page === 'review-detail' && <AppReviewDetail navigate={appNavigate} route={route} notify={notify}/>}
      {page === 'profile-edit' && <AppProfileEdit navigate={appNavigate} notify={notify}/>}
      {page === 'about' && (singleShop ? <SingleShopBrand navigate={appNavigate}/> : <AppAbout navigate={appNavigate}/>)}
      {page === 'legal' && <AppLegal navigate={appNavigate} route={route}/>}
      {page === 'seckill-products' && <AppActivityProducts navigate={appNavigate} route={route} type="seckill" singleShop={singleShop}/>}
      {page === 'groupbuy-products' && <AppActivityProducts navigate={appNavigate} route={route} type="groupbuy" singleShop={singleShop}/>}
      {page === 'coupon-select' && <AppCouponSelect navigate={appNavigate} singleShop={singleShop}/>}
      {page === 'points-product' && <AppPointsProduct navigate={appNavigate} singleShop={singleShop}/>}
      {page === 'points-confirm' && <AppPointsConfirm navigate={appNavigate} route={route}/>}
      {page === 'points-orders' && <AppPointsOrders navigate={appNavigate}/>}
      {page === 'points-order' && <AppPointsOrder navigate={appNavigate} route={route} singleShop={singleShop}/>}
      {page === 'seckill-detail' && <AppCampaignDetail navigate={appNavigate} route={route} type="seckill" notify={notify}/>}
      {page === 'groupbuy-detail' && <AppCampaignDetail navigate={appNavigate} route={route} type="groupbuy" notify={notify}/>}
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

function AdminSidebar({ surface, route, navigate, collapsed = false }) {
  const [expanded,setExpanded] = useState(surface==='platform'?{'商城管理':true,'秒杀管理':false,'拼团管理':false,'系统管理':false}:{'商品管理':true,'交易服务':true,'优惠券管理':false,'秒杀管理':false,'拼团管理':false,'店铺管理':false});
  const platformSections = [
    { title:'商城管理', icon:'ShoppingBag', items:[['Avatar','platform/merchants','商家管理'],['Shop','platform/shops','店铺管理'],['Grid','platform/categories','平台分类'],['Avatar','platform/members','会员管理'],['ChatDotRound','platform/reviews','评价管理'],['Coin','platform/points','积分管理'],['Goods','platform/points-mall','积分商城'],['MagicStick','platform/content','首页运营'],['ChatDotRound','platform/feedback','意见反馈']] },
    { title:'秒杀管理', icon:'Lightning', items:[['Calendar','platform/seckill?tab=活动管理','活动管理'],['Clock','platform/seckill?tab=场次管理','场次管理'],['DocumentChecked','platform/seckill?tab=报名审核','报名审核'],['Goods','platform/seckill?tab=商品管理','商品管理'],['Monitor','platform/seckill?tab=实时监控','实时监控'],['DataAnalysis','platform/seckill?tab=成交分析','成交分析']] },
    { title:'拼团管理', icon:'Connection', items:[['Calendar','platform/groupbuy?tab=活动管理','活动管理'],['DocumentChecked','platform/groupbuy?tab=申请审核','申请审核'],['Goods','platform/groupbuy?tab=商品管理','商品管理'],['Tickets','platform/groupbuy?tab=订单管理','订单管理']] },
    { title:'资金结算', icon:'Money', items:[['Money','platform/settlement?tab=结算台账','结算管理']] },
    { title:'系统管理', icon:'Setting', framework:true, items:[['User','platform/system?view=user','用户管理'],['Avatar','platform/system?view=role','角色管理'],['Menu','platform/system?view=resource','资源管理'],['OfficeBuilding','platform/system?view=org','组织管理'],['Briefcase','platform/system?view=position','岗位管理'],['Collection','platform/system?view=dict','字典管理'],['EditPen','platform/system?view=config','系统参数'],['Document','platform/system?view=log','系统日志'],['Location','platform/system?view=area','地区管理'],['Folder','platform/system?view=file','文件管理'],['UploadFilled','platform/system?view=storage','存储管理']] }
  ];
  const merchantSections = [
    { title:'商品管理', icon:'ShoppingBag', items:[['Goods','merchant/products','SPU 管理'],['Grid','merchant/categories','商品分类'],['Operation','merchant/specs','规格管理']] },
    { title:'交易服务', icon:'Tickets', items:[['Tickets','merchant/orders','订单管理'],['ChatDotRound','merchant/reviews','评价管理'],['ChatDotRound','merchant/service','客服消息']] },
    { title:'优惠券管理', icon:'Ticket', items:[['Ticket','merchant/coupons?tab=优惠券','优惠券'],['Collection','merchant/coupons?tab=领取记录','领取记录'],['Document','merchant/coupons?tab=使用记录','使用记录']] },
    { title:'秒杀管理', icon:'Lightning', items:[['Calendar','merchant/seckill?tab=活动列表','活动列表'],['DocumentChecked','merchant/seckill?tab=报名记录','报名记录'],['Goods','merchant/seckill?tab=秒杀商品','秒杀商品']] },
    { title:'拼团管理', icon:'Connection', items:[['Calendar','merchant/groupbuy?tab=活动列表','活动列表'],['DocumentChecked','merchant/groupbuy?tab=申请记录','申请记录'],['Goods','merchant/groupbuy?tab=拼团商品','拼团商品'],['Tickets','merchant/groupbuy?tab=拼团订单','拼团订单']] },
    { title:'店铺管理', icon:'Shop', items:[['Picture','merchant/banner','轮播图'],['Van','merchant/shipping','物流设置'],['Setting','merchant/settings','店铺设置']] },
    { title:'资金结算', icon:'Money', items:[['Money','merchant/settlement?tab=结算记录','结算管理']] }
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
    <aside className={`admin-sidebar${collapsed?' is-collapsed':''}`}>
      <div className="admin-logo"><span className="brand-stamp">Y</span><div><b>Yeah Mall</b><small>{surface === 'platform' ? '平台运营端' : '商家工作台'}</small></div></div>
      <nav className="admin-menu-tree">
        <button className={current===`${surface}/overview`?'active sidebar-overview':'sidebar-overview'} onClick={()=>navigate(`${surface}/overview`)}><AdminIcon name="House" size={19}/><span>经营概览</span>{current===`${surface}/overview`&&<i></i>}</button>
        {collapsed
          ? sections.map((section)=><button title={section.title} className={section.items.some((menu)=>active(menu[1]))?'active sidebar-collapsed-item':'sidebar-collapsed-item'} key={section.title} onClick={()=>navigate(section.items[0][1])}><AdminIcon name={section.icon} size={18}/><span>{section.title}</span></button>)
          : sections.map((section)=><section className="sidebar-menu-group" key={section.title}><button className="sidebar-group-toggle" onClick={()=>setExpanded({...expanded,[section.title]:!expanded[section.title]})}><span>{section.title}</span>{section.framework&&<small>沿用</small>}<AdminIcon name="ArrowDown" size={15} className={expanded[section.title]?'is-open':''}/></button>{expanded[section.title]&&<div className="sidebar-group-items">{section.items.map(([icon,target,label])=><button key={target} className={active(target)?'active':''} onClick={()=>navigate(target)}><AdminIcon name={icon} size={17}/><span>{label}</span>{active(target)&&<i></i>}</button>)}</div>}</section>)}
      </nav>
    </aside>
  );
}

function AdminAvatar({ size = 32 }) {
  return <span className="admin-user-avatar" style={{ width:size, height:size }}><AdminIcon name="Avatar" size={Math.round(size * .62)}/></span>;
}

function AdminTopbar({ surface, page, route, navigate, notify, collapsed, onToggleSidebar }) {
  const [panel,setPanel] = useState('');
  const names = { overview:'经营概览', merchants:'商家管理', shops:'店铺管理', members:'会员管理', order:'订单管理', reviews:'评价管理', points:'积分管理', 'points-mall':'积分商城', seckill:'秒杀管理', groupbuy:'拼团管理', content:'首页运营', feedback:'意见反馈', products:'SPU 管理', sku:'SKU 管理', categories:surface==='platform'?'平台分类':'商品分类', specs:'规格管理', banner:'轮播图', orders:'订单管理', coupons:'优惠券管理', shipping:'物流设置', service:'客服消息', settings:'店铺设置', settlement:'结算管理', system:'系统管理' };
  const systemNames = {user:'用户管理',org:'组织管理',position:'岗位管理',role:'角色管理',resource:'资源管理',dict:'字典管理',config:'系统参数',file:'文件管理',storage:'存储管理',log:'系统日志',area:'地区管理'};
  const subName = routeParam(route,'tab') || systemNames[routeParam(route,'view')];
  const sectionNames = surface === 'platform'
    ? { merchants:'商城管理', shops:'商城管理', categories:'商城管理', members:'商城管理', reviews:'商城管理', points:'商城管理', 'points-mall':'商城管理', content:'商城管理', feedback:'商城管理', seckill:'秒杀管理', groupbuy:'拼团管理', settlement:'资金结算', system:'系统管理' }
    : { products:'商品管理', categories:'商品管理', specs:'商品管理', orders:'交易服务', reviews:'交易服务', service:'交易服务', coupons:'优惠券管理', seckill:'秒杀管理', groupbuy:'拼团管理', banner:'店铺管理', shipping:'店铺管理', settings:'店铺管理', settlement:'资金结算' };
  const sectionName = sectionNames[page] || '';
  const pageTitle = names[page] || '商城管理';
  const showSection = Boolean(sectionName && sectionName !== pageTitle);
  const notifications = surface==='platform'
    ? [['3 个商家账号待复核','platform/merchants','Avatar'],['2 条秒杀报名待审核','platform/seckill?tab=报名审核','DocumentChecked'],['5 条意见反馈待处理','platform/feedback','ChatDotRound']]
    : [['12 笔订单等待发货','merchant/orders','Tickets'],['6 条评价等待回复','merchant/reviews','ChatDotRound'],['4 个商品库存偏低','merchant/sku','Goods']];
  useEffect(()=>{setPanel('');},[route,surface]);
  const openRoute=(target)=>{setPanel('');navigate(target);};
  return (
    <>
      {panel&&<button className="admin-popover-backdrop" aria-label="关闭浮层" onClick={()=>setPanel('')}></button>}
      <header className="admin-topbar"><div><button className="sidebar-collapse-button" title={collapsed?'展开菜单':'折叠菜单'} onClick={onToggleSidebar}><AdminIcon name={collapsed?'Expand':'Fold'} size={18}/></button>{showSection&&<><span>{sectionName}</span><AdminIcon name="ArrowRight" size={14}/></>}<b>{pageTitle}</b>{subName&&<><AdminIcon name="ArrowRight" size={14}/><b>{subName}</b></>}</div><div>{surface==='merchant'&&<span className="current-shop-chip"><AdminIcon name="Shop" size={15}/>山野生活店</span>}
        <span className="topbar-action-wrap"><button className="notification-button" title="通知中心" onClick={()=>setPanel(panel==='notice'?'':'notice')}><AdminIcon name="Bell"/><i></i></button>{panel==='notice'&&<div className="topbar-popover notice-popover"><div className="popover-heading"><div><b>通知中心</b><span>待处理业务提醒</span></div><StatusTicket tone="warning">3 条</StatusTicket></div><div className="popover-list">{notifications.map(([label,target,icon],index)=><button key={target} onClick={()=>openRoute(target)}><span><i className="notice-icon"><AdminIcon name={icon} size={16}/></i><span><b>{label}</b><small>{index===0?'刚刚':index===1?'12 分钟前':'1 小时前'}</small></span></span><AdminIcon name="ArrowRight" size={14}/></button>)}</div><button className="popover-footer" onClick={()=>{setPanel('');notify('全部通知已标记为已读');}}>全部标记为已读</button></div>}</span>
        <span className="topbar-action-wrap"><button className="topbar-account-trigger" title="账号菜单" onClick={()=>setPanel(panel==='account'?'':'account')}><AdminAvatar size={32}/><span>{surface==='platform'?'管理员':'山野生活店'}</span><AdminIcon name="ArrowDown" size={14}/></button>{panel==='account'&&<div className="topbar-popover account-popover"><div className="account-summary"><AdminAvatar size={42}/><span><b>{surface==='platform'?'平台管理员':'山野生活店'}</b><small>{surface==='platform'?'admin · 超级管理员':'mch001 · 商家账号'}</small></span></div><div className="account-menu"><button onClick={()=>setPanel('profile')}><AdminIcon name="User" size={16}/>个人中心</button><button onClick={()=>{setPanel('');notify('已安全退出原型账号');}}><AdminIcon name="SwitchButton" size={16}/>退出登录</button></div></div>}{panel==='profile'&&<div className="topbar-popover profile-popover"><div className="popover-heading"><div><b>个人资料</b><span>当前登录账号信息</span></div><button className="popover-back" onClick={()=>setPanel('account')}><AdminIcon name="Back" size={15}/></button></div><div className="profile-popover-body"><div className="account-summary"><AdminAvatar size={46}/><span><b>{surface==='platform'?'平台管理员':'山野生活店'}</b><small>{surface==='platform'?'超级管理员':'商家账号'}</small></span></div><label><span>登录账号</span><input value={surface==='platform'?'admin':'mch001'} readOnly/></label><label><span>显示名称</span><input defaultValue={surface==='platform'?'平台管理员':'山野生活店'}/></label></div><div className="profile-popover-actions"><button onClick={()=>setPanel('account')}>取消</button><button className="primary" onClick={()=>{setPanel('');notify('个人资料已保存');}}>保存</button></div></div>}</span>
      </div></header>
    </>
  );
}

function AdminTabs({ tabs, route, navigate, onClose, onCloseOthers, onCloseAll }) {
  const [open,setOpen] = useState(false);
  useEffect(()=>setOpen(false),[route]);
  return <div className="admin-tabs"><div className="admin-tabs-track">{tabs.map((tab)=><button key={tab.route} className={route===tab.route?'active':''} onClick={()=>navigate(tab.route)}><span>{tab.title}</span>{tabs.length>1&&<i onClick={(event)=>{event.stopPropagation();onClose(tab.route);}}>×</i>}</button>)}</div><span className="admin-tabs-menu-wrap"><button className="admin-tabs-more" title="页签操作" onClick={()=>setOpen(!open)}><AdminIcon name="more" size={17}/></button>{open&&<span className="admin-tabs-menu"><button onClick={()=>{setOpen(false);onClose(route);}}>关闭当前</button><button disabled={tabs.length<=1} onClick={()=>{setOpen(false);onCloseOthers();}}>关闭其他</button><button onClick={()=>{setOpen(false);onCloseAll();}}>关闭全部</button></span>}</span></div>;
}

function AdminPagination({ count = 46 }) {
  const [size,setSize] = useState(10);
  const [page,setPage] = useState(1);
  const pages = Math.max(1, Math.ceil(count / size));
  useEffect(()=>{ if(page>pages) setPage(pages); },[pages,page]);
  return <div className="admin-pagination"><span>第 {page}/{pages} 页</span><button disabled={page===1} onClick={()=>setPage(Math.max(1,page-1))}><AdminIcon name="back" size={16}/></button>{Array.from({length:pages},(_,index)=>index+1).slice(0,5).map((item)=><button key={item} className={page===item?'active':''} onClick={()=>setPage(item)}>{item}</button>)}<button disabled={page===pages} onClick={()=>setPage(Math.min(pages,page+1))}><AdminIcon name="chevron" size={16}/></button><select value={size} onChange={(e)=>{setSize(Number(e.target.value));setPage(1);}}><option value="10">10 条/页</option><option value="20">20 条/页</option></select></div>;
}

function FilterBar({ children, onReset, onSearch, actions }) {
  return <div className="admin-filter-bar"><div className="filter-query-group"><div className="filter-fields">{children}</div><div className="filter-actions"><button onClick={onReset}>重置</button><button className="primary" onClick={onSearch}><AdminIcon name="search" size={16}/>查询</button></div></div>{actions&&<div className="filter-business-actions"><i className="filter-action-divider"></i>{actions}</div>}</div>;
}

function RowActionMenu({ open, onToggle, children }) {
  return <span className="row-menu-wrap"><button className="table-more" onClick={onToggle}>更多 <AdminIcon name="down" size={13}/></button>{open&&<span className="row-menu">{children}</span>}</span>;
}

function PlatformMerchants({ notify }) {
  const [merchants,setMerchants] = useState([
    {id:'2117483241846419458',account:'mch001',nickname:'山野生活',enabled:true,shopCount:2,enabledShopCount:2,createdAt:'2026-10-18 09:20',updatedAt:'2026-10-27 10:16',shops:[{no:'SHAN-YE',name:'山野生活店',enabled:true,open:true},{no:'YEAH-OUTLET',name:'山野折扣店',enabled:true,open:false}]},
    {id:'2117483241846419459',account:'mch002',nickname:'Yeah 数码',enabled:true,shopCount:1,enabledShopCount:1,createdAt:'2026-10-20 14:32',updatedAt:'2026-10-26 16:08',shops:[{no:'YEAH-DIGITAL',name:'Yeah 数码生活馆',enabled:true,open:true}]},
    {id:'2117483241846419460',account:'mch003',nickname:'白茶生活',enabled:false,shopCount:0,enabledShopCount:0,createdAt:'2026-10-24 11:05',updatedAt:'2026-10-24 11:05',shops:[]}
  ]);
  const [keyword,setKeyword] = useState('');
  const [status,setStatus] = useState('all');
  const [selected,setSelected] = useState([]);
  const [editing,setEditing] = useState(null);
  const [deleting,setDeleting] = useState(null);
  const [detail,setDetail] = useState(null);
  const [menu,setMenu] = useState(null);
  const [resetting,setResetting] = useState(null);
  const [account,setAccount] = useState('');
  const [password,setPassword] = useState('');
  const [confirmPassword,setConfirmPassword] = useState('');
  const filtered = merchants.filter((item)=>`${item.account}${item.nickname}${item.id}`.includes(keyword.trim())&&(status==='all'||String(item.enabled)===status));
  const allSelected = filtered.length>0&&filtered.every((item)=>selected.includes(item.id));
  const openCreate=()=>{setAccount('');setPassword('');setEditing({id:null});};
  const openEdit=(merchant)=>{setMenu(null);setAccount(merchant.account);setPassword('');setEditing(merchant);};
  const save=()=>{
    if(!account.trim()) return notify('请输入商家账号');
    if(!editing.id&&!password.trim()) return notify('请输入初始登录密码');
    if(editing.id) {
      const next={...editing,account:account.trim(),updatedAt:'2026-11-02 16:48'};
      setMerchants(merchants.map((item)=>item.id===editing.id?next:item));
      if(detail?.id===editing.id) setDetail(next);
    } else {
      const nextId=(merchants.reduce((max,item)=>BigInt(item.id)>max?BigInt(item.id):max,0n)+1n).toString();
      setMerchants([...merchants,{id:nextId,account:account.trim(),nickname:account.trim(),enabled:true,shopCount:0,enabledShopCount:0,shops:[],createdAt:'2026-11-02 16:48',updatedAt:'2026-11-02 16:48'}]);
    }
    setEditing(null);setPassword('');notify(editing.id?'商家信息已保存':'商家已创建');
  };
  const toggleEnabled=(merchant)=>{
    const next={...merchant,enabled:!merchant.enabled,updatedAt:'2026-11-02 16:48'};
    setMerchants(merchants.map((item)=>item.id===merchant.id?next:item));
    if(detail?.id===merchant.id) setDetail(next);
    setMenu(null);notify(`商家账号已${next.enabled?'启用':'停用'}`);
  };
  const openReset=(merchant)=>{setMenu(null);setPassword('');setConfirmPassword('');setResetting(merchant);};
  const savePassword=()=>{
    if(!password.trim()) return notify('请输入新密码');
    if(password!==confirmPassword) return notify('两次输入的密码不一致');
    setResetting(null);setPassword('');setConfirmPassword('');notify('登录密码已重置');
  };
  const confirmDelete=()=>{
    const ids=deleting.ids;
    if(merchants.some((item)=>ids.includes(item.id)&&item.shopCount>0)){setDeleting(null);notify('存在关联店铺的商家，暂不可删除');return;}
    setMerchants(merchants.filter((item)=>!ids.includes(item.id)));
    setSelected(selected.filter((id)=>!ids.includes(id)));
    setDeleting(null);notify(`已删除 ${ids.length} 个商家账号`);
  };
  return <section className="admin-page merchant-admin-page" data-screen-label="平台后台商家管理">
    <FilterBar onReset={()=>{setKeyword('');setStatus('all');}} onSearch={()=>notify(`找到 ${filtered.length} 个商家`)} actions={<><button className="business-danger" disabled={!selected.length} onClick={()=>setDeleting({ids:selected,accounts:merchants.filter((item)=>selected.includes(item.id)).map((item)=>item.account)})}><AdminIcon name="Close" size={15}/>批量删除</button><button className="business-primary" onClick={openCreate}><AdminIcon name="Plus" size={16}/>新增商家</button></>}>
      <label><span>商家账号</span><input value={keyword} onChange={(event)=>setKeyword(event.target.value)} onKeyDown={(event)=>event.key==='Enter'&&notify(`找到 ${filtered.length} 个商家`)} placeholder="账号、昵称或 ID"/></label>
      <label><span>账号状态</span><select value={status} onChange={(event)=>setStatus(event.target.value)}><option value="all">全部状态</option><option value="true">启用</option><option value="false">停用</option></select></label>
    </FilterBar>
    <div className="admin-table-frame"><table data-align-table="t1" className="admin-table merchant-admin-table"><thead><tr><th className="selection-cell"><input type="checkbox" checked={allSelected} onChange={()=>setSelected(allSelected?selected.filter((id)=>!filtered.some((item)=>item.id===id)):[...new Set([...selected,...filtered.map((item)=>item.id)])])}/></th><th>商家账号</th><th>店铺数量</th><th>账号状态</th><th>创建时间</th><th className="actions">操作</th></tr></thead><tbody>{filtered.map((merchant)=><tr key={merchant.id}><td className="selection-cell"><input type="checkbox" checked={selected.includes(merchant.id)} onChange={()=>setSelected(selected.includes(merchant.id)?selected.filter((id)=>id!==merchant.id):[...selected,merchant.id])}/></td><td><div className="cb"><div className="merchant-account-cell"><Avatar name={merchant.nickname||merchant.account} size={36}/><span><b><CopyableText value={merchant.account} label="商家账号" notify={notify}/></b><small><CopyableText value={merchant.nickname} label="商家名称" notify={notify}/> · ID {merchant.id}</small></span></div></div></td><td><div className="cb"><button className="merchant-shop-count" onClick={()=>setDetail(merchant)}><b>{merchant.shopCount}</b><span>家店铺</span></button></div></td><td><div className="cb"><span className={`merchant-account-status ${merchant.enabled?'is-enabled':'is-disabled'}`}><i></i>{merchant.enabled?'启用':'停用'}</span></div></td><td><div className="cb">{merchant.createdAt}</div></td><td className="actions"><div className="cb"><button className="table-link" onClick={()=>setDetail(merchant)}>查看</button><button className="table-link" onClick={()=>openEdit(merchant)}>编辑</button><RowActionMenu open={menu===merchant.id} onToggle={()=>setMenu(menu===merchant.id?null:merchant.id)}><button onClick={()=>openReset(merchant)}>重置密码</button><button onClick={()=>toggleEnabled(merchant)}>{merchant.enabled?'停用账号':'启用账号'}</button><button className="danger-link" onClick={()=>{setMenu(null);setDeleting({ids:[merchant.id],accounts:[merchant.account]});}}>删除商家</button></RowActionMenu></div></td></tr>)}</tbody></table>{!filtered.length&&<div className="admin-empty"><AdminIcon name="Avatar" size={28}/><b>没有匹配的商家</b><span>修改筛选条件后重新查询</span></div>}<AdminPagination count={filtered.length}/></div>
    {detail&&<div className="drawer-mask" onClick={()=>setDetail(null)}><aside className="admin-drawer merchant-detail-drawer" onClick={(event)=>event.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">MERCHANT DETAIL</span><h2>商家详情</h2></div><button onClick={()=>setDetail(null)}>×</button></div><div className="merchant-detail-profile"><Avatar name={detail.nickname||detail.account} size={54}/><div><h3><CopyableText value={detail.nickname} label="商家名称" notify={notify}/></h3><span><CopyableText value={detail.account} label="商家账号" notify={notify}/> · ID {detail.id}</span></div><span className={`merchant-account-status ${detail.enabled?'is-enabled':'is-disabled'}`}><i></i>{detail.enabled?'账号启用':'账号停用'}</span></div><div className="description-grid"><div><span>登录账号</span><b><CopyableText value={detail.account} label="登录账号" notify={notify}/></b></div><div><span>关联店铺</span><b>{detail.shopCount} 家，其中 {detail.enabledShopCount} 家启用</b></div><div><span>创建时间</span><b>{detail.createdAt}</b></div><div><span>更新时间</span><b>{detail.updatedAt}</b></div></div><section className="drawer-section merchant-shop-section"><div className="merchant-detail-section-title"><h3>关联店铺</h3><button onClick={()=>notify(`正在进入 ${detail.account} 的店铺列表`)}>进入店铺管理</button></div>{detail.shops.length?<table data-align-table="t2" className="merchant-shop-table"><thead><tr><th>店铺编号</th><th>店铺名称</th><th>平台状态</th><th>营业状态</th></tr></thead><tbody>{detail.shops.map((shop)=><tr key={shop.no}><td className="mono"><div className="cb"><CopyableText value={shop.no} label="店铺编号" notify={notify}/></div></td><td><div className="cb"><CopyableText value={shop.name} label="店铺名称" notify={notify}/></div></td><td><div className="cb"><StatusTicket tone={shop.enabled?'success':'danger'} small>{shop.enabled?'启用':'停用'}</StatusTicket></div></td><td><div className="cb">{shop.open?'营业中':'休息中'}</div></td></tr>)}</tbody></table>:<div className="merchant-shop-empty"><AdminIcon name="Shop" size={22}/><span>该商家暂未创建店铺</span></div>}</section><div className="drawer-actions"><button onClick={()=>openReset(detail)}>重置密码</button><button onClick={()=>toggleEnabled(detail)}>{detail.enabled?'停用账号':'启用账号'}</button><button className="primary" onClick={()=>{setDetail(null);openEdit(detail);}}>编辑商家</button></div></aside></div>}
    {editing&&<div className="dialog-mask" onClick={()=>setEditing(null)}><section className="admin-dialog merchant-account-form" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">MERCHANT ACCOUNT</span><h2>{editing.id?'编辑商家':'新增商家'}</h2></div><button onClick={()=>setEditing(null)}>×</button></div><label className="dialog-field"><span>商家账号</span><input value={account} onChange={(event)=>setAccount(event.target.value)} placeholder="请输入商家账号"/></label>{!editing.id&&<label className="dialog-field"><span>登录密码 <small>必填</small></span><input type="password" value={password} onChange={(event)=>setPassword(event.target.value)} placeholder="请输入初始登录密码"/></label>}<div className="dialog-hint"><AdminIcon name="Setting" size={16}/><span>{editing.id?'账号状态和密码请通过“更多”操作单独维护。':'保存前将按系统密码规则进行加密处理。'}</span></div><div className="dialog-actions"><button onClick={()=>setEditing(null)}>取消</button><button className="primary" onClick={save}>保存</button></div></section></div>}
    {resetting&&<div className="dialog-mask" onClick={()=>setResetting(null)}><section className="admin-dialog merchant-password-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">RESET PASSWORD</span><h2>重置登录密码</h2></div><button onClick={()=>setResetting(null)}>×</button></div><div className="dialog-member"><Avatar name={resetting.nickname||resetting.account} size={38}/><div><b>{resetting.account}</b><span>{resetting.nickname}</span></div></div><label className="dialog-field"><span>新密码</span><input type="password" value={password} onChange={(event)=>setPassword(event.target.value)} placeholder="请输入新密码"/></label><label className="dialog-field"><span>确认密码</span><input type="password" value={confirmPassword} onChange={(event)=>setConfirmPassword(event.target.value)} placeholder="请再次输入新密码"/></label><div className="dialog-actions"><button onClick={()=>setResetting(null)}>取消</button><button className="primary" onClick={savePassword}>确认重置</button></div></section></div>}
    {deleting&&<div className="dialog-mask" onClick={()=>setDeleting(null)}><section className="admin-dialog merchant-delete-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker danger-kicker">DELETE MERCHANT</span><h2>删除商家</h2></div><button onClick={()=>setDeleting(null)}>×</button></div><div className="danger-confirm"><AdminIcon name="warning" size={22}/><div><b>确认删除选中的 {deleting.ids.length} 个商家吗？</b><span>{deleting.accounts.join('、')}</span><small>若商家仍关联店铺或业务数据，实际系统应由后端拒绝删除。</small></div></div><div className="dialog-actions"><button onClick={()=>setDeleting(null)}>取消</button><button className="danger" onClick={confirmDelete}>确认删除</button></div></section></div>}
  </section>;
}

function PlatformShops({ notify }) {
  const [shops,setShops] = useState([
    {id:'10',shopNo:'SHAN-YE',shopName:'山野生活店',mchId:'1',mchName:'mch001',intro:'挑选耐用、舒适、有生活气息的日常好物。',enabled:true,open:true,commission:5,delay:7,createdAt:'2026-10-18 09:20',updatedAt:'2026-11-02 14:06',deletable:false,tone:'forest'},
    {id:'11',shopNo:'YEAH-DIGITAL',shopName:'Yeah 数码生活馆',mchId:'2',mchName:'mch002',intro:'专注数码影音、智能设备与可靠配件。',enabled:true,open:false,commission:4,delay:7,createdAt:'2026-10-20 14:32',updatedAt:'2026-11-01 18:20',deletable:false,tone:'ocean'},
    {id:'12',shopNo:'WHITE-TEA',shopName:'白茶生活馆',mchId:'3',mchName:'mch003',intro:'温和护肤与松弛生活方式精选。',enabled:false,open:true,commission:6,delay:15,createdAt:'2026-10-24 11:05',updatedAt:'2026-10-30 09:12',deletable:false,tone:'sand'}
  ]);
  const [merchant,setMerchant] = useState('all');
  const [keyword,setKeyword] = useState('');
  const [status,setStatus] = useState('all');
  const [selected,setSelected] = useState([]);
  const [detail,setDetail] = useState(null);
  const [editing,setEditing] = useState(null);
  const [deleting,setDeleting] = useState(null);
  const emptyForm=()=>({mchId:'1',mchName:'mch001',shopName:'',shopNo:'',commission:0,delay:7,enabled:true});
  const [form,setForm] = useState(emptyForm());
  const statusMatched=(shop)=>status==='all'||(status==='platform-enabled'&&shop.enabled)||(status==='platform-disabled'&&!shop.enabled)||(status==='open'&&shop.open)||(status==='closed'&&!shop.open);
  const filtered=shops.filter((shop)=>(merchant==='all'||shop.mchId===merchant)&&(`${shop.shopName}${shop.shopNo}${shop.id}`.toLowerCase().includes(keyword.trim().toLowerCase()))&&statusMatched(shop));
  const allSelected=filtered.length>0&&filtered.every((shop)=>selected.includes(shop.id));
  const resetFilters=()=>{setMerchant('all');setKeyword('');setStatus('all');};
  const openCreate=()=>{setForm(emptyForm());setEditing({id:null});};
  const openEdit=(shop)=>{setForm({mchId:shop.mchId,mchName:shop.mchName,shopName:shop.shopName,shopNo:shop.shopNo,commission:shop.commission,delay:shop.delay,enabled:shop.enabled});setEditing(shop);};
  const updateForm=(key,value)=>setForm({...form,[key]:value});
  const save=()=>{
    if(!form.shopName.trim()) return notify('请输入店铺名称');
    const shopNo=form.shopNo.trim();
    if(shopNo.length>32) return notify('店铺编号长度不能超过32个字符');
    if(shopNo&&!/^[A-Za-z0-9_-]+$/.test(shopNo)) return notify('店铺编号仅支持数字、英文字母、中划线和下划线');
    if(editing.id){
      const next={...editing,shopName:form.shopName.trim(),commission:Number(form.commission)||0,delay:Number(form.delay)||0,enabled:Boolean(form.enabled),updatedAt:'2026-11-03 13:30'};
      setShops(shops.map((shop)=>shop.id===editing.id?next:shop));
      if(detail?.id===editing.id) setDetail(next);
    } else {
      const id=String(Math.max(...shops.map((shop)=>Number(shop.id)))+1);
      const merchantName={1:'mch001',2:'mch002',3:'mch003'}[form.mchId]||'mch001';
      setShops([...shops,{id,shopNo:shopNo||`SHOP-${id}`,shopName:form.shopName.trim(),mchId:form.mchId,mchName:merchantName,intro:'店铺暂未填写简介',enabled:Boolean(form.enabled),open:false,commission:Number(form.commission)||0,delay:Number(form.delay)||0,createdAt:'2026-11-03 13:30',updatedAt:'2026-11-03 13:30',deletable:true,tone:'forest'}]);
    }
    setEditing(null);notify(editing.id?'店铺设置已保存':'店铺已创建');
  };
  const confirmDelete=()=>{
    const targets=shops.filter((shop)=>deleting.ids.includes(shop.id));
    if(targets.some((shop)=>!shop.deletable)){setDeleting(null);notify('存在关联业务数据的店铺，不可删除');return;}
    setShops(shops.filter((shop)=>!deleting.ids.includes(shop.id)));
    setSelected(selected.filter((id)=>!deleting.ids.includes(id)));
    setDeleting(null);notify(`已删除 ${targets.length} 家店铺`);
  };
  return <section className="admin-page platform-shop-page" data-screen-label="平台后台店铺管理">
    <FilterBar onReset={resetFilters} onSearch={()=>notify(`找到 ${filtered.length} 家店铺`)} actions={<><button className="business-danger" disabled={!selected.length} onClick={()=>setDeleting({ids:selected,names:shops.filter((shop)=>selected.includes(shop.id)).map((shop)=>shop.shopName)})}><AdminIcon name="Delete" size={15}/>批量删除</button><button className="business-primary" onClick={openCreate}><AdminIcon name="Plus" size={16}/>新增店铺</button></>}>
      <label><span>所属商家</span><select value={merchant} onChange={(event)=>setMerchant(event.target.value)}><option value="all">全部商家</option><option value="1">mch001</option><option value="2">mch002</option><option value="3">mch003</option></select></label>
      <label><span>店铺</span><input value={keyword} onChange={(event)=>setKeyword(event.target.value)} placeholder="店铺名称、编号或 ID"/></label>
      <label><span>状态</span><select value={status} onChange={(event)=>setStatus(event.target.value)}><option value="all">全部状态</option><option value="platform-enabled">平台已启用</option><option value="platform-disabled">平台已停用</option><option value="open">营业中</option><option value="closed">已停业</option></select></label>
    </FilterBar>
    <div className="admin-table-frame">
      <table data-align-table="t3" className="admin-table platform-shop-table">
        <thead><tr><th className="selection-cell"><input type="checkbox" checked={allSelected} onChange={()=>setSelected(allSelected?selected.filter((id)=>!filtered.some((shop)=>shop.id===id)):[...new Set([...selected,...filtered.map((shop)=>shop.id)])])}/></th><th>店铺</th><th>所属商家</th><th>平台状态</th><th>营业状态</th><th>平台佣金</th><th>结算周期</th><th>创建时间</th><th className="actions">操作</th></tr></thead>
        <tbody>{filtered.map((shop)=><tr key={shop.id}>
          <td className="selection-cell"><input type="checkbox" checked={selected.includes(shop.id)} onChange={()=>setSelected(selected.includes(shop.id)?selected.filter((id)=>id!==shop.id):[...selected,shop.id])}/></td>
          <td><div className="cb"><div className="platform-shop-cell"><span className={`platform-shop-logo tone-${shop.tone}`}>{shop.shopName.slice(0,1)}</span><span><b><CopyableText value={shop.shopName} label="店铺名称" notify={notify}/></b><small className="platform-shop-identity"><CopyableText className="platform-shop-no" value={shop.shopNo} label="店铺编号" notify={notify}/><span>· ID</span><CopyableText className="platform-shop-id" value={shop.id} label="店铺 ID" notify={notify}/></small></span></div></div></td>
          <td><div className="cb"><CopyableText value={shop.mchName} label="商家账号" notify={notify}/></div></td>
          <td><div className="cb"><ReadOnlyStatus tone={shop.enabled?'success':'neutral'}>{shop.enabled?'已启用':'已停用'}</ReadOnlyStatus></div></td>
          <td><div className="cb"><ReadOnlyStatus tone={shop.open?'success':'warning'}>{shop.open?'营业中':'已停业'}</ReadOnlyStatus></div></td>
          <td><div className="cb">{shop.commission}%</div></td><td><div className="cb">T+{shop.delay}</div></td><td><div className="cb">{shop.createdAt}</div></td>
          <td className="actions"><div className="cb"><button className="table-link" onClick={()=>setDetail(shop)}>查看</button><button className="table-link" onClick={()=>openEdit(shop)}>编辑</button><button className="table-link danger-link" onClick={()=>setDeleting({ids:[shop.id],names:[shop.shopName]})}>删除</button></div></td>
        </tr>)}</tbody>
      </table>
      {!filtered.length&&<div className="admin-empty"><AdminIcon name="Shop" size={28}/><b>没有匹配的店铺</b><span>调整筛选条件后重新查询</span></div>}
      <AdminPagination count={filtered.length}/>
    </div>
    {detail&&<div className="drawer-mask" onClick={()=>setDetail(null)}><aside className="admin-drawer platform-shop-drawer" onClick={(event)=>event.stopPropagation()}>
      <div className="drawer-head"><div><span className="drawer-kicker">SHOP DETAIL</span><h2>店铺详情</h2></div><button onClick={()=>setDetail(null)}>×</button></div>
      <div className="platform-shop-profile"><span className={`platform-shop-logo is-large tone-${detail.tone}`}>{detail.shopName.slice(0,1)}</span><div><h3><CopyableText value={detail.shopName} label="店铺名称" notify={notify}/></h3><span className="platform-shop-profile-identity"><CopyableText value={detail.shopNo} label="店铺编号" notify={notify}/><span>· ID</span><CopyableText value={detail.id} label="店铺 ID" notify={notify}/></span></div><span className="platform-shop-profile-status"><ReadOnlyStatus tone={detail.enabled?'success':'neutral'}>{detail.enabled?'平台已启用':'平台已停用'}</ReadOnlyStatus><ReadOnlyStatus tone={detail.open?'success':'warning'}>{detail.open?'营业中':'已停业'}</ReadOnlyStatus></span></div>
      <div className="description-grid"><div><span>所属商家</span><b><CopyableText value={detail.mchName} label="商家账号" notify={notify}/></b></div><div><span>平台佣金</span><b>{detail.commission}%</b></div><div><span>结算周期</span><b>订单完成后 {detail.delay} 天</b></div><div><span>创建时间</span><b>{detail.createdAt}</b></div><div><span>更新时间</span><b>{detail.updatedAt}</b></div><div><span>店铺编号</span><b className="mono"><CopyableText value={detail.shopNo} label="店铺编号" notify={notify}/></b></div></div>
      <section className="drawer-section platform-shop-intro"><h3>店铺简介</h3><p>{detail.intro||'店铺暂未填写简介'}</p></section>
      <div className="drawer-actions"><button className="primary" onClick={()=>{setDetail(null);openEdit(detail);}}>编辑平台设置</button></div>
    </aside></div>}
    {editing&&<div className="dialog-mask" onClick={()=>setEditing(null)}><section className="admin-dialog platform-shop-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">SHOP SETTINGS</span><h2>{editing.id?'编辑店铺':'新增店铺'}</h2></div><button onClick={()=>setEditing(null)}>×</button></div><div className="platform-shop-form-grid"><label><span>所属商家</span><select disabled={Boolean(editing.id)} value={form.mchId} onChange={(event)=>updateForm('mchId',event.target.value)}><option value="1">mch001</option><option value="2">mch002</option><option value="3">mch003</option></select><small>{editing.id?'创建后不可更换所属商家':''}</small></label><label><span>店铺名称</span><input value={form.shopName} onChange={(event)=>updateForm('shopName',event.target.value)} placeholder="请输入店铺名称"/></label><label><span>店铺编号</span><input disabled={Boolean(editing.id)} value={form.shopNo} onChange={(event)=>updateForm('shopNo',event.target.value)} placeholder="留空由系统自动生成"/><small>支持字母、数字、中划线和下划线</small></label><label><span>平台状态</span><select value={String(form.enabled)} onChange={(event)=>updateForm('enabled',event.target.value==='true')}><option value="true">已启用</option><option value="false">已停用</option></select><small>停用后平台不再允许该店铺交易</small></label><label><span>平台佣金比例</span><span className="input-suffix-field"><input type="number" min="0" max="100" step="0.1" value={form.commission} onChange={(event)=>updateForm('commission',event.target.value)}/><i>%</i></span></label><label><span>结算周期</span><span className="input-suffix-field"><input type="number" min="0" max="365" value={form.delay} onChange={(event)=>updateForm('delay',event.target.value)}/><i>天</i></span><small>从订单完成时间开始计算</small></label></div><div className="dialog-hint"><AdminIcon name="Operation" size={16}/><span>店铺 Logo、简介和营业状态由商家在店铺设置中维护。</span></div><div className="dialog-actions"><button onClick={()=>setEditing(null)}>取消</button><button className="primary" onClick={save}>保存</button></div></section></div>}
    {deleting&&<div className="dialog-mask" onClick={()=>setDeleting(null)}><section className="admin-dialog platform-shop-delete-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker danger-kicker">DELETE SHOP</span><h2>删除店铺</h2></div><button onClick={()=>setDeleting(null)}>×</button></div><div className="danger-confirm"><AdminIcon name="WarningFilled" size={22}/><div><b>确认删除选中的 {deleting.ids.length} 家店铺吗？</b><span>{deleting.names.join('、')}</span><small>仅无商品及其他关联业务数据的店铺允许删除，日常经营请优先使用“停用”。</small></div></div><div className="dialog-actions"><button onClick={()=>setDeleting(null)}>取消</button><button className="danger" onClick={confirmDelete}>确认删除</button></div></section></div>}
  </section>;
}

function AdminMembers({ members, setMembers, notify, navigate }) {
  const [keyword, setKeyword] = useState('');
  const [pointsRange, setPointsRange] = useState('all');
  const [registeredFrom, setRegisteredFrom] = useState('');
  const [registeredTo, setRegisteredTo] = useState('');
  const [filtered, setFiltered] = useState(members);
  const [drawer, setDrawer] = useState(null);
  const [menu, setMenu] = useState(null);
  const [adjust, setAdjust] = useState(null);
  const [amount, setAmount] = useState(100);
  const [remark, setRemark] = useState('');
  useEffect(() => setFiltered(members), [members]);
  const search = () => setFiltered(members.filter((item) => {
    const textMatch = `${item.id}${item.nickname}${item.account}${item.mobile}`.includes(keyword.trim());
    const points = Number(item.points || 0);
    const pointsMatch = pointsRange === 'all'
      || (pointsRange === 'zero' && points === 0)
      || (pointsRange === 'low' && points > 0 && points < 1000)
      || (pointsRange === 'high' && points >= 1000);
    const registeredDate = item.createdAt.slice(0, 10);
    const afterFrom = !registeredFrom || registeredDate >= registeredFrom;
    const beforeTo = !registeredTo || registeredDate <= registeredTo;
    return textMatch && pointsMatch && afterFrom && beforeTo;
  }));
  const saveAdjust = () => {
    const delta = adjust.type === 'increase' ? Number(amount) : -Number(amount);
    if (!amount || amount < 1) return notify('调整积分必须大于 0');
    if (adjust.member.points + delta < 0) return notify('可用积分不足，不能扣减');
    const next = members.map((item) => item.id === adjust.member.id ? { ...item, points: item.points + delta, earned: delta > 0 ? item.earned + delta : item.earned, used: delta < 0 ? item.used + Math.abs(delta) : item.used } : item);
    setMembers(next); setAdjust(null); setRemark(''); notify(`已${delta > 0 ? '增加' : '扣减'} ${Math.abs(delta)} 积分`);
  };
  return (
    <section className="admin-page" data-screen-label="平台后台会员管理">
      <FilterBar onReset={() => { setKeyword(''); setPointsRange('all'); setRegisteredFrom(''); setRegisteredTo(''); setFiltered(members); }} onSearch={search} actions={<button onClick={()=>notify('当前会员结果已导出')}><AdminIcon name="filter" size={17}/>导出结果</button>}>
        <label><span>会员关键词</span><input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="昵称、账号或手机号" onKeyDown={(e) => e.key === 'Enter' && search()}/></label>
        <label><span>积分范围</span><select value={pointsRange} onChange={(e)=>setPointsRange(e.target.value)}><option value="all">全部积分</option><option value="zero">0 积分</option><option value="low">1–999</option><option value="high">1000 以上</option></select></label>
        <label className="member-date-range"><span>注册时间</span><span className="filter-date-range"><input type="date" value={registeredFrom} onChange={(e)=>setRegisteredFrom(e.target.value)}/><i>至</i><input type="date" value={registeredTo} onChange={(e)=>setRegisteredTo(e.target.value)}/></span></label>
      </FilterBar>
      <div className="admin-table-frame">
        <table data-align-table="t4" className="admin-table"><thead><tr><th>会员</th><th>账号</th><th>手机号</th><th className="numeric">可用积分</th><th className="numeric">累计获得</th><th className="numeric">累计使用</th><th>注册时间</th><th className="actions">操作</th></tr></thead>
          <tbody>{filtered.map((member) => <tr key={member.id}><td><div className="cb"><div className="member-table-cell"><Avatar name={member.nickname} tone={member.tone}/><div><b><CopyableText value={member.nickname} label="会员名称" notify={notify}/></b><small>ID <CopyableText value={member.id} label="会员ID" notify={notify}/></small></div></div></div></td><td><div className="cb"><CopyableText value={member.account} label="会员账号" notify={notify}/></div></td><td className="mono"><div className="cb"><CopyableText value={member.mobile} label="手机号" notify={notify}/></div></td><td className="numeric points-number"><div className="cb">{member.points.toLocaleString()}</div></td><td className="numeric"><div className="cb">{member.earned.toLocaleString()}</div></td><td className="numeric"><div className="cb">{member.used.toLocaleString()}</div></td><td><div className="cb"><span>{member.createdAt.slice(0,10)}</span><small className="cell-sub">{member.createdAt.slice(11)}</small></div></td><td className="actions"><div className="cb"><button className="table-link" onClick={() => setDrawer(member)}>查看</button><span className="row-menu-wrap"><button className="table-more" onClick={() => setMenu(menu === member.id ? null : member.id)}>更多 <AdminIcon name="down" size={14}/></button>{menu === member.id && <span className="row-menu"><button onClick={() => { setAdjust({ type:'increase', member }); setMenu(null); }}>增加积分</button><button onClick={() => { setAdjust({ type:'decrease', member }); setMenu(null); }}>扣减积分</button><button onClick={() => { setMenu(null); navigate(`platform/points?tab=${encodeURIComponent('积分流水')}&memberId=${member.id}`); }}>积分流水</button></span>}</span></div></td></tr>)}</tbody>
        </table>
        {!filtered.length && <div className="admin-empty"><AdminIcon name="members" size={30}/><b>没有匹配的会员</b><span>修改筛选条件后重试</span></div>}
        <AdminPagination count={filtered.length || 0}/>
      </div>
      {drawer && <div className="drawer-mask" onClick={() => setDrawer(null)}><aside className="admin-drawer" onClick={(e) => e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">MEMBER PROFILE</span><h2>会员详情</h2></div><button onClick={() => setDrawer(null)}>×</button></div><div className="profile-summary"><Avatar name={drawer.nickname} tone={drawer.tone} size={54}/><div><h3><CopyableText value={drawer.nickname} label="会员名称" notify={notify}/></h3><span><CopyableText value={drawer.account} label="会员账号" notify={notify}/> · <CopyableText value={drawer.mobile} label="手机号" notify={notify}/></span></div><StatusTicket tone="success">正常</StatusTicket></div><div className="profile-metrics"><div><span>可用积分</span><b>{drawer.points.toLocaleString()}</b></div><div><span>累计获得</span><b>{drawer.earned.toLocaleString()}</b></div><div><span>累计使用</span><b>{drawer.used.toLocaleString()}</b></div></div><div className="description-grid">{[['会员ID',drawer.id],['性别',drawer.gender],['生日',drawer.birthday],['注册时间',drawer.createdAt],['最近更新',drawer.updatedAt || drawer.createdAt]].map(([k,v]) => <div key={k}><span>{k}</span><b>{k==='会员ID'?<CopyableText value={v} label="会员ID" notify={notify}/>:v}</b></div>)}</div><div className="drawer-actions"><button onClick={() => { setAdjust({type:'increase',member:drawer}); setDrawer(null); }}>调整积分</button><button className="primary" onClick={() => {setDrawer(null);navigate(`platform/points?tab=${encodeURIComponent('积分流水')}&memberId=${drawer.id}`);}}>查看积分流水</button></div></aside></div>}
      {adjust && <div className="dialog-mask" onClick={() => setAdjust(null)}><section className="admin-dialog points-dialog" onClick={(e) => e.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">POINTS ADJUSTMENT</span><h2>{adjust.type === 'increase' ? '增加积分' : '扣减积分'}</h2></div><button onClick={() => setAdjust(null)}>×</button></div><div className="dialog-member"><Avatar name={adjust.member.nickname} tone={adjust.member.tone}/><div><b>{adjust.member.nickname}</b><span>当前可用积分 {adjust.member.points.toLocaleString()}</span></div></div><label className="dialog-field"><span>调整积分</span><input type="number" min="1" value={amount} onChange={(e) => setAmount(Number(e.target.value))}/></label><div className="balance-preview"><span>调整后积分</span><b>{Math.max(0, adjust.member.points + (adjust.type === 'increase' ? Number(amount) : -Number(amount))).toLocaleString()}</b></div><label className="dialog-field"><span>备注 <small>选填</small></span><textarea value={remark} onChange={(e) => setRemark(e.target.value)} placeholder="说明本次调整用途"></textarea></label><div className="dialog-actions"><button onClick={() => setAdjust(null)}>取消</button><button className="primary" onClick={saveAdjust}>确认调整</button></div></section></div>}
          </section>
  );
}

const REVIEW_ROWS_INITIAL = [
  { id:'r-1', spuId:'2117483241846419458', product:'云感降噪头戴耳机', spec:'雾银 / 标准版', orderNo:'YM202611020018', shopId:'SHAN-YE', user:'林可', rating:5, content:'佩戴舒适，降噪效果很好，通勤使用很合适。', hasImage:true, replied:true, lastReply:'merchant', visible:true, time:'2026-11-01 18:42:10', art:'headphone', images:['headphone','headphone'], replies:[{sender:'merchant',content:'感谢认可，耳罩支持拆卸清洁。',time:'2026-11-01 19:20'},{sender:'buyer',content:'好的，请问替换耳罩可以单独购买吗？',time:'2026-11-01 19:36'}] },
  { id:'r-2', spuId:'2117483241846419461', product:'白茶屏障修护精华', spec:'30ml', orderNo:'YM202611010086', shopId:'YEAH-DIGITAL', user:'陈一禾', rating:5, content:'敏感肌使用没有刺激，吸收速度比较快。', hasImage:false, replied:false, lastReply:'', visible:true, time:'2026-10-28 09:16:22', art:'serum', images:[], replies:[] },
  { id:'r-3', spuId:'2117483241846419464', product:'山野拼配挂耳咖啡', spec:'10 片装', orderNo:'YM202610290032', shopId:'SHAN-YE', user:'周予安', rating:3, content:'包装有轻微压痕，咖啡本身没有受影响。', hasImage:true, replied:true, lastReply:'buyer', visible:false, time:'2026-10-24 16:30:08', art:'coffee', images:['coffee'], replies:[{sender:'merchant',content:'已为您登记包装问题并反馈仓库。',time:'2026-10-25 09:12'},{sender:'buyer',content:'收到，希望后续改进包装。',time:'2026-10-25 09:40'}] }
];

const REVIEW_SPU_OPTIONS = [
  { value:'2117483241846419458', label:'云感降噪头戴耳机', id:'SPU 2117483241846419458' },
  { value:'2117483241846419461', label:'白茶屏障修护精华', id:'SPU 2117483241846419461' },
  { value:'2117483241846419464', label:'山野拼配挂耳咖啡', id:'SPU 2117483241846419464' },
  { value:'2117483241846419470', label:'弧光人体工学休闲椅', id:'SPU 2117483241846419470' }
];
const REVIEW_SHOP_OPTIONS = [
  { value:'SHAN-YE', label:'山野生活店', id:'SHAN-YE' },
  { value:'YEAH-DIGITAL', label:'Yeah 数码生活馆', id:'YEAH-DIGITAL' },
  { value:'WHITE-TEA', label:'白茶生活馆', id:'WHITE-TEA' }
];

function ReviewSearchPicker({ value, options, placeholder, onChange }) {
  const [open,setOpen] = useState(false);
  const [keyword,setKeyword] = useState('');
  const selected = options.find((item)=>item.value===value);
  const filtered = options.filter((item)=>`${item.label}${item.id}`.toLowerCase().includes(keyword.trim().toLowerCase()));
  return <div className="review-search-picker">
    <input
      value={open?keyword:(selected?selected.label:'')}
      placeholder={placeholder}
      onFocus={()=>{setOpen(true);setKeyword('');}}
      onChange={(event)=>{setKeyword(event.target.value); if(!open) setOpen(true);}}
      onBlur={()=>window.setTimeout(()=>setOpen(false),120)}
    />
    {value&&!open&&<button type="button" className="review-search-clear" onMouseDown={(event)=>event.preventDefault()} onClick={()=>onChange('')}>×</button>}
    {open&&<div className="review-search-menu">
      {filtered.map((item)=><button type="button" key={item.value} onMouseDown={(event)=>event.preventDefault()} onClick={()=>{onChange(item.value);setOpen(false);}}><b>{item.label}</b><span>{item.id}</span></button>)}
      {!filtered.length&&<div className="review-search-empty">无匹配结果</div>}
    </div>}
  </div>;
}

function AdminReviews({ notify }) {
  const [rows,setRows] = useState(REVIEW_ROWS_INITIAL);
  const [filters,setFilters] = useState({ spu:'', shop:'', rating:'全部等级', image:'全部图片', reply:'全部状态', visible:'全部状态', from:'', to:'' });
  const [detail,setDetail] = useState(null);
  const updateFilter=(key,value)=>setFilters({...filters,[key]:value});
  const resetFilters=()=>setFilters({ spu:'', shop:'', rating:'全部等级', image:'全部图片', reply:'全部状态', visible:'全部状态', from:'', to:'' });
  const replyLabel=(row)=>row.lastReply==='buyer'?'买家已回复':row.replied?'商家已回复':'待商家回复';
  const replyTone=(row)=>!row.replied?'warning':row.lastReply==='buyer'?'info':'success';
  const filtered = rows.filter((row) => {
    if(filters.spu && row.spuId!==filters.spu) return false;
    if(filters.shop && row.shopId!==filters.shop) return false;
    if(filters.rating!=='全部等级'){
      const bucket=filters.rating==='好评'?[4,5]:filters.rating==='中评'?[3]:[1,2];
      if(!bucket.includes(row.rating)) return false;
    }
    if(filters.image!=='全部图片' && row.hasImage!==(filters.image==='有图')) return false;
    if(filters.reply!=='全部状态' && row.replied!==(filters.reply==='已回复')) return false;
    if(filters.visible!=='全部状态' && row.visible!==(filters.visible==='公开')) return false;
    const day=row.time.slice(0,10);
    if(filters.from && day<filters.from) return false;
    if(filters.to && day>filters.to) return false;
    return true;
  });
  const toggleVisibility=(row)=>{
    const next=rows.map((item)=>item.id===row.id?{...item,visible:!item.visible}:item);
    setRows(next);
    if(detail?.id===row.id) setDetail({...row,visible:!row.visible});
    notify(`评价已${row.visible?'隐藏':'恢复公开'}`);
  };
  return (
    <section className="admin-page review-admin-page" data-screen-label="平台后台评价管理">
      <FilterBar onReset={resetFilters} onSearch={()=>notify(`找到 ${filtered.length} 条评价`)}>
        <label className="review-search-field"><span>商品 SPU</span><ReviewSearchPicker value={filters.spu} options={REVIEW_SPU_OPTIONS} placeholder="搜索商品名称或 SPU ID" onChange={(value)=>updateFilter('spu',value)}/></label>
        <label className="review-search-field"><span>店铺</span><ReviewSearchPicker value={filters.shop} options={REVIEW_SHOP_OPTIONS} placeholder="搜索店铺名称或编号" onChange={(value)=>updateFilter('shop',value)}/></label>
        <label><span>评价等级</span><select value={filters.rating} onChange={(e)=>updateFilter('rating',e.target.value)}>{['全部等级','好评','中评','差评'].map((item)=><option key={item}>{item}</option>)}</select></label>
        <label><span>评价图片</span><select value={filters.image} onChange={(e)=>updateFilter('image',e.target.value)}>{['全部图片','有图','无图'].map((item)=><option key={item}>{item}</option>)}</select></label>
        <label><span>回复状态</span><select value={filters.reply} onChange={(e)=>updateFilter('reply',e.target.value)}>{['全部状态','已回复','未回复'].map((item)=><option key={item}>{item}</option>)}</select></label>
        <label><span>公开状态</span><select value={filters.visible} onChange={(e)=>updateFilter('visible',e.target.value)}>{['全部状态','公开','已隐藏'].map((item)=><option key={item}>{item}</option>)}</select></label>
        <label className="review-date-range"><span>评价时间</span><span className="filter-date-range"><input type="date" value={filters.from} onChange={(e)=>updateFilter('from',e.target.value)}/><i>至</i><input type="date" value={filters.to} onChange={(e)=>updateFilter('to',e.target.value)}/></span></label>
      </FilterBar>
      <div className="admin-table-frame">
        <table data-align-table="t5" className="admin-table review-admin-table">
          <thead><tr><th>商品</th><th>规格</th><th>订单编号</th><th>用户</th><th className="numeric">评分</th><th>评价内容</th><th>回复</th><th>状态</th><th>评价时间</th><th className="actions">操作</th></tr></thead>
          <tbody>{filtered.map((row)=><tr key={row.id}>
            <td><div className="cb"><div className="admin-product-cell review-product-cell"><ProductArt type={row.art}/><div><b><CopyableText value={row.product} label="商品名称" notify={notify}/></b><small>SPU {row.spuId}</small></div></div></div></td>
            <td><div className="cb">{row.spec}</div></td>
            <td className="mono"><div className="cb"><CopyableText value={row.orderNo} label="订单编号" notify={notify}/></div></td>
            <td><div className="cb">{row.user}</div></td>
            <td className="numeric"><div className="cb">{row.rating} 星</div></td>
            <td><div className="cb"><span className="review-content-text">{row.content}</span></div></td>
            <td><div className="cb"><StatusTicket tone={replyTone(row)}>{replyLabel(row)}</StatusTicket></div></td>
            <td><div className="cb"><StatusTicket tone={row.visible?'success':'neutral'}>{row.visible?'公开':'已隐藏'}</StatusTicket></div></td>
            <td><div className="cb"><span>{row.time.slice(0,10)}</span><small className="cell-sub">{row.time.slice(11)}</small></div></td>
            <td className="actions"><div className="cb"><button className="table-link" onClick={()=>setDetail(row)}>详情</button><button className={`table-link ${row.visible?'danger-link':''}`} onClick={()=>toggleVisibility(row)}>{row.visible?'隐藏':'恢复'}</button></div></td>
          </tr>)}</tbody>
        </table>
        {!filtered.length&&<div className="admin-empty"><AdminIcon name="ChatDotRound" size={28}/><b>没有匹配的评价</b><span>调整筛选条件后重试</span></div>}
        <AdminPagination count={filtered.length}/>
      </div>
      {detail&&<div className="drawer-mask" onClick={()=>setDetail(null)}><aside className="admin-drawer review-detail-drawer" onClick={(event)=>event.stopPropagation()}>
        <div className="drawer-head"><div><span className="drawer-kicker">REVIEW DETAIL</span><h2>评价详情</h2></div><button onClick={()=>setDetail(null)}>×</button></div>
        <div className="review-detail-product"><ProductArt type={detail.art}/><div><b>{detail.product}</b><span>{detail.spec} · 订单 {detail.orderNo}</span><span>{detail.user} · {detail.time}</span></div><StatusTicket tone={detail.visible?'success':'neutral'}>{detail.visible?'公开':'已隐藏'}</StatusTicket></div>
        <div className="review-detail-content">{detail.content}</div>
        {detail.images?.length>0&&<div className="review-detail-images">{detail.images.map((image,index)=><ProductArt key={index} type={image}/>)}</div>}
        <div className="drawer-section"><h3>回复记录</h3>{detail.replies?.length?<div className="review-reply-list">{detail.replies.map((reply,index)=><div className="review-reply-item" key={index}><b>{reply.sender==='merchant'?'商家回复':'买家回复'}</b><p>{reply.content}</p><span>{reply.time}</span></div>)}</div>:<p className="review-no-reply">暂无回复记录</p>}</div>
        <div className="drawer-actions"><button onClick={()=>setDetail(null)}>关闭</button><button className={detail.visible?'':'primary'} onClick={()=>toggleVisibility(detail)}>{detail.visible?'隐藏评价':'恢复公开'}</button></div>
      </aside></div>}
    </section>
  );
}

const FEEDBACK_NOW = new Date('2026/11/16 12:30:00').getTime();
const FEEDBACK_STATUS_META = {
  '1': { label:'待处理', tone:'warning' },
  '2': { label:'处理中', tone:'info' },
  '3': { label:'已解决', tone:'success' }
};
const FEEDBACK_TABS = [
  { key:'all', label:'全部' },
  { key:'1', label:'待处理' },
  { key:'2', label:'处理中' },
  { key:'3', label:'已解决' }
];
const FEEDBACK_IMAGE_META = {
  order:{ label:'订单页截图', icon:'order', tone:'sage' },
  points:{ label:'积分商城截图', icon:'points', tone:'amber' },
  dark:{ label:'深色模式截图', icon:'image', tone:'ink' },
  detail:{ label:'商品详情截图', icon:'image', tone:'blue' },
  search:{ label:'搜索页截图', icon:'search', tone:'blue' },
  chair:{ label:'商品照片', icon:'image', tone:'clay' },
  manual:{ label:'说明书照片', icon:'file', tone:'clay' }
};
const FEEDBACK_REPLY_PRESETS = [
  '感谢反馈，我们已记录并安排排查。',
  '问题已处理完成，请更新至最新版本后重试。',
  '该建议已纳入后续迭代计划，感谢你的建议。'
];
const FEEDBACK_ROWS_INITIAL = [
  { id:'2117483241846421008', memberId:'211386217002', nickname:'周予安', account:'zhouyuan', mobile:'186****3048', tone:'amber', content:'App 夜间模式下部分商品详情文字对比度太低，看久了有点吃力，希望能优化一下配色。', contact:'186****3048', status:'1', createTime:'2026-11-16 10:02:55', updateTime:'2026-11-16 10:02:55', replyContent:'', replyTime:'', images:['dark','detail'] },
  { id:'2117483241846421001', memberId:'211386217001', nickname:'林可', account:'linke', mobile:'138****7621', tone:'sage', content:'希望优化订单页面的信息层级，每次查物流都要往下滑很久，建议把物流状态放到更醒目的位置。', contact:'138****7621', status:'1', createTime:'2026-11-16 09:12:36', updateTime:'2026-11-16 09:12:36', replyContent:'', replyTime:'', images:['order'] },
  { id:'2117483241846421005', memberId:'211386217005', nickname:'木棉', account:'mumin', mobile:'135****2287', tone:'violet', content:'拼团订单成团后物流一直没有更新，麻烦帮忙看下是不是卡在中转了。', contact:'微信：mumin_88', status:'2', createTime:'2026-11-16 08:26:11', updateTime:'2026-11-16 09:02:40', replyContent:'', replyTime:'', images:[] },
  { id:'2117483241846421002', memberId:'211386217002', nickname:'周予安', account:'zhouyuan', mobile:'186****3048', tone:'amber', content:'商品图片加载偶尔比较慢，切换分类后要等两三秒，Wi-Fi 下也会出现，希望排查一下。', contact:'', status:'1', createTime:'2026-11-15 18:40:09', updateTime:'2026-11-15 18:40:09', replyContent:'', replyTime:'', images:[] },
  { id:'2117483241846421004', memberId:'211386217003', nickname:'海盐气泡', account:'sea_soda', mobile:'152****9156', tone:'blue', content:'收到的休闲椅安装说明书不够清晰，螺丝包没有标注对应步骤，希望补充图示说明。', contact:'152****9156', status:'2', createTime:'2026-11-14 20:05:44', updateTime:'2026-11-15 09:30:12', replyContent:'已反馈给商家核实说明书版本，确认后会同步处理结果。', replyTime:'2026-11-15 09:30:12', images:['chair','manual'] },
  { id:'2117483241846421003', memberId:'211386217004', nickname:'陈一禾', account:'chenyihe', mobile:'177****4403', tone:'red', content:'建议增加积分兑换的实物商品，现在可选的品类偏少，希望上一些家居用品。', contact:'177****4403', status:'3', createTime:'2026-11-13 15:22:41', updateTime:'2026-11-14 10:06:18', replyContent:'感谢建议，积分商城已新增居家好物分类，后续会持续补充实物商品。', replyTime:'2026-11-14 10:06:18', images:['points'] },
  { id:'2117483241846421007', memberId:'211386217004', nickname:'陈一禾', account:'chenyihe', mobile:'177****4403', tone:'red', content:'优惠券到期前希望能收到提醒，最近错过了一张满减券，有点可惜。', contact:'177****4403', status:'1', createTime:'2026-11-12 16:18:03', updateTime:'2026-11-12 16:18:03', replyContent:'', replyTime:'', images:[] },
  { id:'2117483241846421006', memberId:'211386217001', nickname:'林可', account:'linke', mobile:'138****7621', tone:'sage', content:'搜索框输入关键词后没有历史记录，想重复搜索上次的商品不太方便。', contact:'', status:'3', createTime:'2026-11-10 11:04:27', updateTime:'2026-11-11 09:12:40', replyContent:'搜索历史功能已在规划中，本期先上线了热门搜索词，感谢反馈。', replyTime:'2026-11-11 09:12:40', images:[] },
  { id:'2117483241846421009', memberId:'211386217003', nickname:'海盐气泡', account:'sea_soda', mobile:'152****9156', tone:'blue', content:'积分兑换的优惠券到账有点慢，兑换后大约十分钟才在券包里看到。', contact:'微信：sea_soda', status:'3', createTime:'2026-11-08 09:40:18', updateTime:'2026-11-08 14:22:36', replyContent:'兑换券由系统自动发放，到账可能存在短暂延迟，已记录并评估优化。', replyTime:'2026-11-08 14:22:36', images:[] }
];

const feedbackHours = (time) => Math.max(0, Math.floor((FEEDBACK_NOW - new Date(String(time).replace(/-/g,'/')).getTime()) / 36e5));
const feedbackOverdue = (row) => row.status === '1' && feedbackHours(row.createTime) >= 24;

function FeedbackShot({ type, size = 44, className = '' }) {
  const meta = FEEDBACK_IMAGE_META[type] || { label:'反馈图片', icon:'image', tone:'sage' };
  return <span className={`fb-shot tone-${meta.tone} ${className}`.trim()} style={{ width:size, height:size }} title={meta.label}>
    <AdminIcon name={meta.icon} size={Math.max(14, Math.round(size * .34))}/>
  </span>;
}

function AdminFeedback({ notify }) {
  const [rows,setRows] = useState(FEEDBACK_ROWS_INITIAL);
  const [tab,setTab] = useState('all');
  const [onlyOverdue,setOnlyOverdue] = useState(false);
  const [filters,setFilters] = useState({ keyword:'', image:'全部图片', from:'', to:'' });
  const [applied,setApplied] = useState({ keyword:'', image:'全部图片', from:'', to:'' });
  const [detail,setDetail] = useState(null);
  const [handling,setHandling] = useState(null);
  const [handleStatus,setHandleStatus] = useState('2');
  const [reply,setReply] = useState('');

  const overdueCount = rows.filter(feedbackOverdue).length;
  const baseRows = rows.filter((row) => {
    const keyword = applied.keyword.trim().toLowerCase();
    const textMatch = !keyword || `${row.nickname}${row.memberId}${row.content}${row.contact}`.toLowerCase().includes(keyword);
    const imageMatch = applied.image === '全部图片' || (applied.image === '有图' ? row.images.length > 0 : row.images.length === 0);
    const day = row.createTime.slice(0,10);
    return textMatch && imageMatch && (!applied.from || day >= applied.from) && (!applied.to || day <= applied.to);
  });
  const counts = { all:baseRows.length, '1':0, '2':0, '3':0 };
  baseRows.forEach((row) => { counts[row.status] += 1; });
  const filtered = baseRows.filter((row) => (tab === 'all' || row.status === tab) && (!onlyOverdue || feedbackOverdue(row)));
  const search = () => setApplied({ ...filters });
  const reset = () => {
    const empty = { keyword:'', image:'全部图片', from:'', to:'' };
    setFilters(empty);
    setApplied(empty);
    setTab('all');
    setOnlyOverdue(false);
  };
  const toggleOverdue = () => {
    if (!onlyOverdue) setTab('1');
    setOnlyOverdue(!onlyOverdue);
  };
  const openHandle = (row) => {
    setHandling(row);
    setHandleStatus(row.status === '3' ? '3' : '2');
    setReply(row.replyContent || '');
    setDetail(null);
  };
  const saveHandle = () => {
    if (!handling) return;
    const content = reply.trim();
    if (handleStatus === '3' && !content) return;
    const stamp = '2026-11-16 12:30';
    setRows(rows.map((row) => row.id === handling.id ? { ...row, status:handleStatus, replyContent:content, replyTime:content ? stamp : '', updateTime:stamp } : row));
    notify(handleStatus === '3' ? '反馈已标记为已解决' : '反馈已更新为处理中');
    setHandling(null);
  };

  const detailMeta = detail ? FEEDBACK_STATUS_META[detail.status] : null;
  const detailRecent = detail && detail.status !== '1' ? (detail.replyTime || detail.updateTime) : '';

  return (
    <section className="admin-page feedback-admin-page" data-screen-label="平台后台意见反馈">
      <div className="fb-queue">
        <div className="fb-tabs">
          {FEEDBACK_TABS.map((item)=><button key={item.key} data-status={item.key} className={tab===item.key?'active':''} onClick={()=>setTab(item.key)}>{item.label}<span className="fb-tab-count">{counts[item.key]}</span></button>)}
        </div>
        {overdueCount>0 && <button className={`fb-overdue-toggle${onlyOverdue?' active':''}`} onClick={toggleOverdue}><AdminIcon name="clock" size={14}/>超 24 小时待处理 {overdueCount}</button>}
      </div>
      <FilterBar onReset={reset} onSearch={search}>
        <label><span>关键词</span><input value={filters.keyword} onChange={(event)=>setFilters({...filters,keyword:event.target.value})} placeholder="反馈内容、联系方式或会员" onKeyDown={(event)=>event.key==='Enter'&&search()}/></label>
        <label><span>反馈图片</span><select value={filters.image} onChange={(event)=>setFilters({...filters,image:event.target.value})}>{['全部图片','有图','无图'].map((item)=><option key={item}>{item}</option>)}</select></label>
        <label className="fb-date-range"><span>提交时间</span><span className="filter-date-range"><input type="date" value={filters.from} onChange={(event)=>setFilters({...filters,from:event.target.value})}/><i>至</i><input type="date" value={filters.to} onChange={(event)=>setFilters({...filters,to:event.target.value})}/></span></label>
      </FilterBar>
      <div className="admin-table-frame">
        <div className="fb-table-intro">
          <span><AdminIcon name="message" size={15}/>标记已解决时必须填写平台回复，回复内容会展示在 App「我的反馈」中。</span>
          <b>当前 {filtered.length} 条</b>
        </div>
        <table data-align-table="t6" className="admin-table feedback-admin-table">
          <thead><tr><th>会员</th><th>反馈内容</th><th>联系方式</th><th>提交时间</th><th>状态</th><th>最近处理</th><th className="actions">操作</th></tr></thead>
          <tbody>{filtered.map((row)=>{
            const meta = FEEDBACK_STATUS_META[row.status];
            const overdue = feedbackOverdue(row);
            const recent = row.status === '1' ? '' : (row.replyTime || row.updateTime);
            return <tr key={row.id}>
              <td><div className="cb"><div className="fb-member-cell"><Avatar name={row.nickname} tone={row.tone} size={36}/><div><b><CopyableText value={row.nickname} label="会员昵称" notify={notify}/></b><small>ID <CopyableText value={row.memberId} label="会员 ID" notify={notify}/></small></div></div></div></td>
              <td><div className="cb"><div className="fb-content-cell"><b className="fb-content-text" title={row.content}>{row.content}</b><span className="fb-content-meta">{row.images.length>0&&<span className="fb-image-chip"><AdminIcon name="image" size={11}/>{row.images.length} 张图片</span>}{overdue&&<span className="fb-overdue-tag"><AdminIcon name="clock" size={11}/>超 24 小时</span>}</span></div></div></td>
              <td><div className="cb">{row.contact?<div className="fb-contact-cell"><CopyableText value={row.contact} label="联系方式" notify={notify}/></div>:<span className="fb-contact-empty">未留联系方式</span>}</div></td>
              <td><div className="cb"><div className="fb-time-cell"><span>{row.createTime.slice(0,10)}</span><small className="cell-sub">{row.createTime.slice(11)}</small></div></div></td>
              <td><div className="cb"><StatusTicket tone={meta.tone}>{meta.label}</StatusTicket></div></td>
              <td><div className="cb">{recent?<div className="fb-time-cell"><span>{recent.slice(0,10)}</span><small className="cell-sub">{recent.slice(11)}</small></div>:<span className="fb-contact-empty">—</span>}</div></td>
              <td className="actions"><div className="cb"><button className="table-link" onClick={()=>setDetail(row)}>详情</button><button className="table-link" onClick={()=>openHandle(row)}>{row.status==='3'?'更新':'处理'}</button></div></td>
            </tr>;
          })}</tbody>
        </table>
        {!filtered.length&&<div className="admin-empty"><AdminIcon name="ChatDotRound" size={28}/><b>{onlyOverdue?'没有超时未处理的反馈':'没有匹配的反馈'}</b><span>{onlyOverdue?'当前没有超过 24 小时仍未处理的反馈':'调整筛选条件后重试'}</span></div>}
        <AdminPagination count={filtered.length}/>
      </div>

      {detail&&<div className="drawer-mask" onClick={()=>setDetail(null)}><aside className="admin-drawer fb-detail-drawer" onClick={(event)=>event.stopPropagation()}>
        <div className="drawer-head"><div><span className="drawer-kicker">FEEDBACK DETAIL</span><h2>反馈详情</h2></div><button onClick={()=>setDetail(null)}>×</button></div>
        <div className="fb-member-summary">
          <Avatar name={detail.nickname} tone={detail.tone} size={48}/>
          <div><h3>{detail.nickname}<StatusTicket tone={detailMeta.tone} small>{detailMeta.label}</StatusTicket></h3><span>会员 ID <CopyableText value={detail.memberId} label="会员 ID" notify={notify}/></span><span>{detail.account} · {detail.mobile}</span></div>
        </div>
        <div className="fb-meta-grid">
          <div><span>反馈编号</span><b className="mono"><CopyableText value={detail.id} label="反馈编号" notify={notify}/></b></div>
          <div><span>提交时间</span><b>{detail.createTime}</b></div>
          <div><span>联系方式</span><b>{detail.contact||'未留联系方式'}</b></div>
          <div><span>最近处理</span><b>{detailRecent||'尚未处理'}</b></div>
        </div>
        {feedbackOverdue(detail)&&<div className="fb-overdue-note"><AdminIcon name="warning" size={16}/><span>该反馈已提交 {feedbackHours(detail.createTime)} 小时仍未处理，建议尽快跟进。</span></div>}
        <div className="drawer-section"><h3>反馈内容</h3><p className="fb-detail-content">{detail.content}</p></div>
        {detail.images.length>0&&<div className="drawer-section"><h3>反馈图片 <small className="fb-section-note">共 {detail.images.length} 张</small></h3><div className="fb-detail-images">{detail.images.map((image,index)=><FeedbackShot key={index} type={image} size={92}/>)}</div></div>}
        <div className="drawer-section"><h3>平台回复</h3>{detail.replyContent?<div className="fb-reply-card"><b>平台运营团队</b><p>{detail.replyContent}</p><span>{detail.replyTime||detail.updateTime}</span></div>:<p className="fb-no-reply">尚未回复用户。标记已解决前必须填写平台回复，用户可在 App「我的反馈」中查看。</p>}</div>
        <div className="drawer-actions"><button onClick={()=>setDetail(null)}>关闭</button><button className="primary" onClick={()=>openHandle(detail)}>{detail.status==='3'?'更新处理':'处理反馈'}</button></div>
      </aside></div>}

      {handling&&<div className="dialog-mask" onClick={()=>setHandling(null)}><section className="admin-dialog fb-handle-dialog" onClick={(event)=>event.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">FEEDBACK HANDLE</span><h2>{handling.status==='3'?'更新处理':'处理反馈'}</h2></div><button onClick={()=>setHandling(null)}>×</button></div>
        <div className="fb-handle-context">
          <Avatar name={handling.nickname} tone={handling.tone} size={38}/>
          <div><b>{handling.nickname}</b><p>{handling.content}</p><small>提交于 {handling.createTime}</small></div>
          <StatusTicket tone={FEEDBACK_STATUS_META[handling.status].tone}>{FEEDBACK_STATUS_META[handling.status].label}</StatusTicket>
        </div>
        {feedbackOverdue(handling)&&<div className="fb-overdue-note"><AdminIcon name="warning" size={16}/><span>该反馈已超过 24 小时未处理，建议优先跟进。</span></div>}
        <div className="fb-status-choice">
          <button type="button" className={handleStatus==='2'?'selected':''} onClick={()=>setHandleStatus('2')}><i className="fb-choice-dot"></i><span><b>处理中</b><small>已受理该反馈，可稍后补充平台回复</small></span></button>
          <button type="button" className={handleStatus==='3'?'selected':''} onClick={()=>setHandleStatus('3')}><i className="fb-choice-dot"></i><span><b>已解决</b><small>需填写回复，用户可在 App 查看处理结果</small></span></button>
        </div>
        <label className="dialog-field">
          <span>平台回复 {handleStatus==='3'?<em className="fb-required">必填</em>:<em className="fb-optional">选填</em>}</span>
          <textarea maxLength="500" value={reply} onChange={(event)=>setReply(event.target.value)} placeholder="填写处理结论或给用户的建议，最多 500 字"></textarea>
          <small className="fb-counter">{reply.length}/500</small>
        </label>
        <div className="fb-reply-presets"><span>快捷回复</span>{FEEDBACK_REPLY_PRESETS.map((preset)=><button type="button" key={preset} onClick={()=>setReply(preset)}>{preset}</button>)}</div>
        <p className={`fb-handle-tip${handleStatus==='3'&&!reply.trim()?' is-warn':''}`}>{handleStatus==='3'?(reply.trim()?'保存后该反馈将标记为已解决，回复内容同步展示给用户。':'标记已解决时必须填写平台回复。'):'保存后反馈进入处理中，用户可先了解到平台已受理。'}</p>
        <div className="dialog-actions"><button onClick={()=>setHandling(null)}>取消</button><button className="primary" disabled={handleStatus==='3'&&!reply.trim()} onClick={saveHandle}>保存处理</button></div>
      </section></div>}
    </section>
  );
}

const SECKILL_SESSION_NOW = '2026-11-17 20:30:00';
const SECKILL_SESSION_TABS = [
  { key:'all', label:'全部' },
  { key:'upcoming', label:'未开始' },
  { key:'ongoing', label:'进行中' },
  { key:'ended', label:'已结束' }
];
const SECKILL_SESSION_PRESETS = [
  { label:'10:00 – 12:00', begin:'10:00', end:'12:00' },
  { label:'14:00 – 16:00', begin:'14:00', end:'16:00' },
  { label:'20:00 – 22:00', begin:'20:00', end:'22:00' }
];
const SECKILL_SESSIONS_INITIAL = [
  { id:'ss-1', activityId:'sa-1', name:'早鸟场', beginTime:'2026-10-27 10:00:00', endTime:'2026-10-27 10:40:00', sort:1, enabled:true, productCount:3, remark:'开抢前 40 分钟', updatedAt:'2026-10-26 18:20' },
  { id:'ss-2', activityId:'sa-1', name:'主会场', beginTime:'2026-10-27 10:40:00', endTime:'2026-10-27 11:20:00', sort:2, enabled:true, productCount:5, remark:'主推降噪耳机', updatedAt:'2026-10-26 18:26' },
  { id:'ss-3', activityId:'sa-1', name:'收尾场', beginTime:'2026-10-27 11:20:00', endTime:'2026-10-27 12:00:00', sort:3, enabled:true, productCount:2, remark:'', updatedAt:'2026-10-26 18:30' },
  { id:'ss-4', activityId:'sa-2', name:'晚八点场', beginTime:'2026-11-22 20:00:00', endTime:'2026-11-22 21:00:00', sort:1, enabled:true, productCount:3, remark:'', updatedAt:'2026-11-16 19:12' },
  { id:'ss-5', activityId:'sa-2', name:'加场', beginTime:'2026-11-22 21:00:00', endTime:'2026-11-22 22:00:00', sort:2, enabled:false, productCount:0, remark:'商品待补充', updatedAt:'2026-11-16 19:18' },
  { id:'ss-6', activityId:'sa-3', name:'日间场', beginTime:'2026-11-25 10:00:00', endTime:'2026-11-25 14:00:00', sort:1, enabled:true, productCount:4, remark:'', updatedAt:'2026-11-17 09:24' },
  { id:'ss-7', activityId:'sa-3', name:'晚间场', beginTime:'2026-11-25 18:00:00', endTime:'2026-11-25 22:00:00', sort:2, enabled:true, productCount:3, remark:'', updatedAt:'2026-11-17 09:26' },
  { id:'ss-8', activityId:'sa-5', name:'日间场', beginTime:'2026-11-18 10:00:00', endTime:'2026-11-18 14:00:00', sort:1, enabled:true, productCount:2, remark:'', updatedAt:'2026-11-16 15:02' },
  { id:'ss-9', activityId:'sa-5', name:'夜间场', beginTime:'2026-11-18 18:00:00', endTime:'2026-11-18 22:00:00', sort:2, enabled:true, productCount:1, remark:'', updatedAt:'2026-11-16 15:04' },
  { id:'ss-10', activityId:'sa-6', name:'上午场', beginTime:'2026-11-03 10:00:00', endTime:'2026-11-03 13:00:00', sort:1, enabled:true, productCount:3, remark:'返场保留数据', updatedAt:'2026-11-04 08:10' },
  { id:'ss-11', activityId:'sa-6', name:'下午场', beginTime:'2026-11-03 14:00:00', endTime:'2026-11-03 18:00:00', sort:2, enabled:true, productCount:2, remark:'', updatedAt:'2026-11-04 08:10' },
  { id:'ss-12', activityId:'sa-6', name:'晚间场', beginTime:'2026-11-03 19:00:00', endTime:'2026-11-03 22:00:00', sort:3, enabled:true, productCount:1, remark:'', updatedAt:'2026-11-04 08:10' },
  { id:'ss-13', activityId:'sa-7', name:'全天场', beginTime:'2026-08-08 10:00:00', endTime:'2026-08-08 22:00:00', sort:1, enabled:true, productCount:2, remark:'', updatedAt:'2026-08-09 10:05' },
  { id:'ss-14', activityId:'sa-8', name:'会员首场', beginTime:'2026-11-17 20:00:00', endTime:'2026-11-17 21:00:00', sort:1, enabled:true, productCount:3, remark:'限量降噪耳机', updatedAt:'2026-11-17 12:08' },
  { id:'ss-15', activityId:'sa-8', name:'会员加场', beginTime:'2026-11-17 21:00:00', endTime:'2026-11-17 22:00:00', sort:2, enabled:true, productCount:0, remark:'音箱返场，商品待补充', updatedAt:'2026-11-17 12:10' }
];
const SECKILL_WEEKDAYS = ['周日','周一','周二','周三','周四','周五','周六'];

function seckillSessionLocal(value) {
  return String(value || '').slice(0,16).replace(' ','T');
}

function seckillWeekday(value) {
  const date = new Date(`${String(value).slice(0,10)}T00:00:00`);
  return Number.isNaN(date.getTime()) ? '' : SECKILL_WEEKDAYS[date.getDay()];
}

function seckillSessionDuration(begin,end) {
  const start = new Date(String(begin).replace(' ','T')).getTime();
  const finish = new Date(String(end).replace(' ','T')).getTime();
  if (!Number.isFinite(start) || !Number.isFinite(finish) || finish <= start) return '';
  const minutes = Math.round((finish - start) / 60000);
  const hours = Math.floor(minutes / 60);
  const rest = minutes % 60;
  if (!hours) return `${rest} 分钟`;
  return rest ? `${hours} 小时 ${rest} 分钟` : `${hours} 小时`;
}

function seckillSessionState(session) {
  if (SECKILL_SESSION_NOW < session.beginTime) return { key:'upcoming', label:'未开始', tone:'info' };
  if (SECKILL_SESSION_NOW < session.endTime) return { key:'ongoing', label:'进行中', tone:'success' };
  return { key:'ended', label:'已结束', tone:'neutral' };
}

function seckillActivityState(activity) {
  if (SECKILL_SESSION_NOW < activity.activityBeginTime) return { key:'upcoming', label:'活动未开始', tone:'info' };
  if (SECKILL_SESSION_NOW < activity.activityEndTime) return { key:'ongoing', label:'活动进行中', tone:'success' };
  return { key:'ended', label:'活动已结束', tone:'neutral' };
}

function seckillShiftDay(value,days) {
  const date = new Date(`${String(value).slice(0,10)}T${String(value).slice(11,16)}:00`);
  date.setDate(date.getDate() + days);
  const pad = (part)=>String(part).padStart(2,'0');
  return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:00`;
}

function validateSeckillDraft(draft, activity, sessions, editingId) {
  const errors = [];
  const name = String(draft.name || '').trim();
  const begin = draft.beginTime;
  const end = draft.endTime;
  const sort = Number(draft.sort);
  const sortValid = Number.isInteger(sort) && sort >= 1 && sort <= 999;
  if (draft.sort !== '' && !sortValid) errors.push('排序需为 1 至 999 的整数');
  if (begin && end) {
    if (end <= begin) {
      errors.push('结束时间需晚于开始时间');
    } else {
      const activityBegin = seckillSessionLocal(activity.activityBeginTime);
      const activityEnd = seckillSessionLocal(activity.activityEndTime);
      if (begin < activityBegin || end > activityEnd) {
        errors.push(`场次时间需位于活动时间 ${activity.activityBeginTime.slice(0,16)} 至 ${activity.activityEndTime.slice(0,16)} 内`);
      } else {
        const conflict = sessions.find((session)=>session.id !== editingId
          && begin < seckillSessionLocal(session.endTime)
          && end > seckillSessionLocal(session.beginTime));
        if (conflict) errors.push(`与「${conflict.name}」(${conflict.beginTime.slice(5,16)} – ${conflict.endTime.slice(11,16)}) 时间重叠`);
      }
    }
  }
  return {
    errors,
    ready: Boolean(name && begin && end && sortValid),
    duration: begin && end && end > begin ? seckillSessionDuration(begin,end) : ''
  };
}

function AdminSeckillSessions({ activities, setActivities, sessionsByActivity, setSessionsByActivity, route, navigate, notify }) {
  const routeActivityId = routeParam(route,'activityId');
  const defaultActivityId = ()=>{
    const ongoing = activities.find((item)=>item.activityBeginTime <= SECKILL_SESSION_NOW && SECKILL_SESSION_NOW < item.activityEndTime);
    if (ongoing) return ongoing.id;
    const upcoming = activities.filter((item)=>item.activityBeginTime > SECKILL_SESSION_NOW).sort((a,b)=>a.activityBeginTime.localeCompare(b.activityBeginTime))[0];
    return (upcoming || activities[0] || {}).id || '';
  };
  const [activityId,setActivityId] = useState(()=>activities.some((item)=>item.id===routeActivityId)?routeActivityId:defaultActivityId());
  const [tab,setTab] = useState('all');
  const [filters,setFilters] = useState({ name:'', enabled:'全部状态', date:'' });
  const [applied,setApplied] = useState({ name:'', enabled:'全部状态', date:'' });
  const [editor,setEditor] = useState(null);
  const [deleting,setDeleting] = useState(null);
  useEffect(()=>{
    if (routeActivityId && activities.some((item)=>item.id===routeActivityId)) setActivityId(routeActivityId);
  },[routeActivityId]);
  useEffect(()=>{
    setTab('all');
    setFilters({ name:'', enabled:'全部状态', date:'' });
    setApplied({ name:'', enabled:'全部状态', date:'' });
  },[activityId]);
  const activity = activities.find((item)=>item.id===activityId) || activities.find((item)=>item.id===defaultActivityId()) || activities[0];
  const activityState = seckillActivityState(activity);
  const activitySessions = sessionsByActivity[activity.id] || [];
  const sortedSessions = useMemo(()=>[...activitySessions].sort((a,b)=>a.beginTime.localeCompare(b.beginTime)||a.sort-b.sort),[activitySessions]);
  const baseRows = sortedSessions.filter((row)=>{
    const keyword = applied.name.trim().toLowerCase();
    const nameMatch = !keyword || `${row.name}${row.remark||''}`.toLowerCase().includes(keyword);
    const enabledMatch = applied.enabled === '全部状态' || (applied.enabled === '已启用' ? row.enabled : !row.enabled);
    const dateMatch = !applied.date || row.beginTime.slice(0,10) === applied.date;
    return nameMatch && enabledMatch && dateMatch;
  });
  const counts = { all:baseRows.length, upcoming:0, ongoing:0, ended:0 };
  baseRows.forEach((row)=>{ counts[seckillSessionState(row).key] += 1; });
  const filtered = baseRows.filter((row)=>tab === 'all' || seckillSessionState(row).key === tab);
  const enabledCount = activitySessions.filter((row)=>row.enabled).length;
  const productTotal = activitySessions.reduce((total,row)=>total+row.productCount,0);
  const nextSort = activitySessions.reduce((max,row)=>Math.max(max,row.sort),0) + 1;
  const validation = useMemo(()=>editor?validateSeckillDraft(editor,activity,sortedSessions,editor.id):null,[editor,activity,sortedSessions]);
  const updateEditor = (patch)=>setEditor((current)=>({...current,...patch}));
  const search = ()=>setApplied({...filters});
  const reset = ()=>{
    const empty = { name:'', enabled:'全部状态', date:'' };
    setFilters(empty);
    setApplied(empty);
    setTab('all');
  };
  const openEditor = (row,mode)=>{
    if (mode === 'create') {
      setEditor({ mode:'create', id:null, name:'', beginTime:'', endTime:'', sort:String(nextSort), enabled:true, remark:'' });
      return;
    }
    if (mode === 'copy') {
      setEditor({
        mode:'copy', id:null, name:`${row.name}（副本）`,
        beginTime:seckillSessionLocal(seckillShiftDay(row.beginTime,1)),
        endTime:seckillSessionLocal(seckillShiftDay(row.endTime,1)),
        sort:String(nextSort), enabled:row.enabled, remark:row.remark
      });
      return;
    }
    setEditor({ mode:'edit', id:row.id, name:row.name, beginTime:seckillSessionLocal(row.beginTime), endTime:seckillSessionLocal(row.endTime), sort:String(row.sort), enabled:row.enabled, remark:row.remark });
  };
  const applyPreset = (preset)=>{
    const today = SECKILL_SESSION_NOW.slice(0,10);
    const date = editor.beginTime
      ? editor.beginTime.slice(0,10)
      : (today >= activity.activityBeginTime.slice(0,10) && today <= activity.activityEndTime.slice(0,10) ? today : activity.activityBeginTime.slice(0,10));
    updateEditor({ beginTime:`${date}T${preset.begin}`, endTime:`${date}T${preset.end}` });
  };
  const saveSession = ()=>{
    if (!editor || !validation || !validation.ready || validation.errors.length) return;
    const payload = {
      name:editor.name.trim(),
      beginTime:`${editor.beginTime.slice(0,16).replace('T',' ')}:00`,
      endTime:`${editor.endTime.slice(0,16).replace('T',' ')}:00`,
      sort:Number(editor.sort),
      enabled:editor.enabled,
      remark:editor.remark.trim(),
      updatedAt:SECKILL_SESSION_NOW.slice(0,16)
    };
    const list = sessionsByActivity[activity.id] || [];
    const nextList = editor.mode === 'edit'
      ? list.map((item)=>item.id === editor.id ? { ...item, ...payload } : item)
      : [...list, { ...payload, id:`ss-${Date.now()}`, productCount:0 }];
    setSessionsByActivity({ ...sessionsByActivity, [activity.id]:nextList });
    if (editor.mode !== 'edit') setActivities(activities.map((item)=>item.id === activity.id ? { ...item, sessionCount:nextList.length } : item));
    notify(editor.mode === 'edit' ? '场次已更新' : editor.mode === 'copy' ? '场次副本已创建，请确认时间' : '场次已新增');
    setEditor(null);
  };
  const toggleEnabled = (row)=>{
    setSessionsByActivity((current)=>({
      ...current,
      [activity.id]:(current[activity.id] || []).map((item)=>item.id === row.id ? { ...item, enabled:!item.enabled, updatedAt:SECKILL_SESSION_NOW.slice(0,16) } : item)
    }));
    notify(row.enabled ? '场次已停用，App 端将不再展示' : '场次已启用');
  };
  const confirmDelete = ()=>{
    if (!deleting || deleting.productCount > 0) return;
    const nextList = (sessionsByActivity[activity.id] || []).filter((item)=>item.id !== deleting.id);
    setSessionsByActivity({ ...sessionsByActivity, [activity.id]:nextList });
    setActivities(activities.map((item)=>item.id === activity.id ? { ...item, sessionCount:nextList.length } : item));
    notify('场次已删除');
    setDeleting(null);
  };
  const hintText = !validation ? '' : validation.errors.length
    ? validation.errors.join('；')
    : validation.ready
      ? `场次时长 ${validation.duration}，时间范围校验通过。`
      : editor.beginTime && editor.endTime
        ? '时间未发现冲突；请填写场次名称后保存。'
        : '场次时间需位于活动时间内，且不能与其他场次重叠（可首尾相接）。';

  return <section className="admin-page seckill-session-page" data-screen-label="平台后台-秒杀场次管理">
    <div className="ss-activity-bar">
      <span className="ss-activity-icon"><AdminIcon name="bolt" size={22}/></span>
      <div className="ss-activity-copy">
        <div className="ss-activity-title"><h2>{activity.name}</h2><StatusTicket tone={activityState.tone}>{activityState.label}</StatusTicket></div>
        <div className="ss-activity-meta">
          <span><AdminIcon name="Calendar" size={13}/>活动时间 {activity.activityBeginTime.slice(0,16)} 至 {activity.activityEndTime.slice(0,16)}</span>
          <span><AdminIcon name="clock" size={13}/>共 {activitySessions.length} 个场次</span>
          <span><AdminIcon name="Goods" size={13}/>参与商品 {productTotal} 件</span>
        </div>
      </div>
      <div className="ss-activity-controls">
        <label className="ss-activity-switch"><span>切换活动</span><select value={activity.id} onChange={(event)=>setActivityId(event.target.value)}>{activities.map((item)=><option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
        <button className="ss-back-link" onClick={()=>navigate('platform/seckill?tab=活动管理')}><AdminIcon name="back" size={15}/>返回活动管理</button>
      </div>
    </div>
    <div className="ss-queue">
      <div className="ss-tabs">{SECKILL_SESSION_TABS.map((item)=><button key={item.key} data-status={item.key} className={tab===item.key?'active':''} onClick={()=>setTab(item.key)}>{item.label}<span className="ss-tab-count">{counts[item.key]}</span></button>)}</div>
      <div className="ss-queue-summary">
        <span><i className="ss-live-dot"></i>已启用 <b>{enabledCount}</b> 场</span>
        <span><AdminIcon name="Goods" size={13}/>参与商品 <b>{productTotal}</b> 件</span>
      </div>
    </div>
    <FilterBar onReset={reset} onSearch={search} actions={<button className="business-primary" onClick={()=>openEditor(null,'create')}><AdminIcon name="plus" size={17}/>新增场次</button>}>
      <label><span>场次名称</span><input value={filters.name} onChange={(event)=>setFilters({...filters,name:event.target.value})} placeholder="场次名称或备注" onKeyDown={(event)=>event.key==='Enter'&&search()}/></label>
      <label><span>启用状态</span><select value={filters.enabled} onChange={(event)=>setFilters({...filters,enabled:event.target.value})}>{['全部状态','已启用','已停用'].map((item)=><option key={item}>{item}</option>)}</select></label>
      <label><span>场次日期</span><input type="date" value={filters.date} onChange={(event)=>setFilters({...filters,date:event.target.value})}/></label>
    </FilterBar>
    <div className="admin-table-frame">
      <div className="ss-table-intro">
        <span><AdminIcon name="warning" size={15}/>启用后的场次会展示在 App 秒杀页；场次时间需位于活动时间内且不可重叠，停用后 App 端不再展示。</span>
        <b>当前 {filtered.length} 场</b>
      </div>
      <table data-align-table="t7" className="admin-table seckill-session-table">
        <thead><tr><th>场次</th><th>场次时间</th><th>时间状态</th><th>启用</th><th>商品数</th><th>最近更新</th><th className="actions">操作</th></tr></thead>
        <tbody>{filtered.map((row)=>{
          const state = seckillSessionState(row);
          return <tr key={row.id} className={row.enabled?'':'is-off'}>
            <td><div className="cb"><div className="ss-session-cell"><span className="ss-sort-chip">{String(row.sort).padStart(2,'0')}</span><div><b title={row.name}>{row.name}</b><small>{row.remark||'未填写备注'} · 排序 {row.sort}</small></div></div></div></td>
            <td><div className="cb"><div className="ss-time-cell"><span className="ss-date-line">{row.beginTime.slice(5,10)}<em>{seckillWeekday(row.beginTime)}</em></span><b>{row.beginTime.slice(11,16)} – {row.endTime.slice(11,16)}</b><small>时长 {seckillSessionDuration(row.beginTime,row.endTime)}</small></div></div></td>
            <td><div className="cb"><StatusTicket tone={state.tone}><span className="ss-status-live">{state.key==='ongoing'&&<i className="ss-live-dot"></i>}{state.label}</span></StatusTicket></div></td>
            <td><div className="cb"><button type="button" className={`ss-toggle${row.enabled?' is-on':''}`} role="switch" aria-checked={row.enabled} title={row.enabled?'点击停用：App 端将不再展示该场次':'点击启用：App 端将展示该场次'} onClick={()=>toggleEnabled(row)}><span className="ss-toggle-track"><i></i></span>{row.enabled?'已启用':'已停用'}</button></div></td>
            <td><div className="cb"><span className={`ss-product-count${row.productCount?'':' is-zero'}`}><b>{row.productCount}</b>件</span></div></td>
            <td><div className="cb"><div className="ss-time-stamp"><span>{row.updatedAt.slice(0,10)}</span><small>{row.updatedAt.slice(11)}</small></div></div></td>
            <td className="actions"><div className="cb"><button className="table-link" onClick={()=>openEditor(row,'edit')}>编辑</button><button className="table-link" onClick={()=>openEditor(row,'copy')}>复制</button><button className="table-link ss-danger-link" onClick={()=>setDeleting(row)}>删除</button></div></td>
          </tr>;
        })}</tbody>
      </table>
      {!filtered.length&&<div className="admin-empty"><AdminIcon name="Clock" size={28}/><b>{activitySessions.length?'没有匹配的场次':'当前活动还没有场次'}</b><span>{activitySessions.length?'调整筛选条件后重试':'点击「新增场次」创建第一场秒杀'}</span></div>}
      <AdminPagination count={filtered.length}/>
    </div>
    {editor&&<div className="dialog-mask" onClick={()=>setEditor(null)}>
      <section className="admin-dialog ss-session-dialog" onClick={(event)=>event.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">{editor.mode==='edit'?'EDIT SESSION':editor.mode==='copy'?'COPY SESSION':'NEW SESSION'}</span><h2>{editor.mode==='edit'?'编辑场次':editor.mode==='copy'?'复制场次':'新增场次'}</h2></div><button onClick={()=>setEditor(null)}>×</button></div>
        <div className="ss-dialog-context">
          <span className="ss-context-icon"><AdminIcon name="bolt" size={16}/></span>
          <div><b>{activity.name}</b><small>活动时间 {activity.activityBeginTime.slice(0,16)} 至 {activity.activityEndTime.slice(0,16)}</small></div>
          <StatusTicket tone={activityState.tone}>{activityState.label}</StatusTicket>
        </div>
        {editor.mode==='copy'&&<div className="ss-form-hint"><AdminIcon name="warning" size={15}/><span>副本时间已顺延 1 天，请确认仍在活动时间内且不与其他场次重叠后再保存。</span></div>}
        <div className="ss-form-grid">
          <label className="wide"><span>场次名称<em>*</em></span><input value={editor.name} maxLength={32} onChange={(event)=>updateEditor({name:event.target.value})} placeholder="例如：晚八点场"/></label>
          <label><span>开始时间<em>*</em></span><input type="datetime-local" step="60" value={editor.beginTime} onChange={(event)=>updateEditor({beginTime:event.target.value})}/></label>
          <label><span>结束时间<em>*</em></span><input type="datetime-local" step="60" value={editor.endTime} onChange={(event)=>updateEditor({endTime:event.target.value})}/></label>
          <div className="ss-presets wide"><span>快捷时段</span>{SECKILL_SESSION_PRESETS.map((preset)=><button type="button" key={preset.label} onClick={()=>applyPreset(preset)}>{preset.label}</button>)}<small>按开始日期填充</small></div>
          <label><span>排序</span><input type="number" min="1" max="999" value={editor.sort} onChange={(event)=>updateEditor({sort:event.target.value})}/></label>
          <label><span>启用状态</span><select value={editor.enabled?'启用':'停用'} onChange={(event)=>updateEditor({enabled:event.target.value==='启用'})}><option>启用</option><option>停用</option></select></label>
          <label className="wide"><span>备注</span><textarea value={editor.remark} maxLength={100} onChange={(event)=>updateEditor({remark:event.target.value})} placeholder="选填，用于说明场次用途或运营策略"></textarea></label>
        </div>
        <div className={`ss-form-hint${validation.errors.length?' is-error':validation.ready?' is-ok':''}`}>
          <AdminIcon name={validation.errors.length?'warning':validation.ready?'check':'clock'} size={15}/>
          <span>{hintText}</span>
        </div>
        <div className="dialog-actions"><button onClick={()=>setEditor(null)}>取消</button><button className="primary" disabled={!validation.ready||validation.errors.length>0} onClick={saveSession}>保存场次</button></div>
      </section>
    </div>}
    {deleting&&<div className="dialog-mask" onClick={()=>setDeleting(null)}>
      <section className="admin-dialog ss-delete-dialog" onClick={(event)=>event.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">DELETE SESSION</span><h2>{deleting.productCount?'无法删除场次':'删除场次'}</h2></div><button onClick={()=>setDeleting(null)}>×</button></div>
        <div className={`ss-delete-warning${deleting.productCount?'':' is-plain'}`}>
          <AdminIcon name={deleting.productCount?'warning':'clock'} size={18}/>
          <div>{deleting.productCount
            ? <><b>该场次下仍关联 {deleting.productCount} 件秒杀商品</b><p>请先在「商品管理」中移除或调整这些商品的场次后再删除，避免商品数据失去关联。</p></>
            : <><b>确定删除「{deleting.name}」？</b><p>删除后不可恢复；若只是暂停展示，可在列表中直接停用该场次。</p></>}</div>
        </div>
        <div className="ss-delete-meta">
          <span>场次时间</span><b>{deleting.beginTime.slice(0,16)} 至 {deleting.endTime.slice(0,16)}</b>
          <span>商品数</span><b>{deleting.productCount} 件</b>
        </div>
        <div className="dialog-actions"><button onClick={()=>setDeleting(null)}>{deleting.productCount?'关闭':'取消'}</button>{deleting.productCount
          ? <button className="primary" onClick={()=>{setDeleting(null);navigate('platform/seckill?tab=商品管理');}}>去商品管理</button>
          : <button className="danger" onClick={confirmDelete}>确认删除</button>}</div>
      </section>
    </div>}
  </section>;
}

const SECKILL_APPLY_TABS = [
  { key:'all', label:'全部' },
  { key:'pending', label:'待审核' },
  { key:'passed', label:'审核通过' },
  { key:'rejected', label:'审核驳回' }
];
const SECKILL_APPLY_STATUS = {
  1: { key:'pending', label:'待审核', tone:'warning' },
  2: { key:'passed', label:'审核通过', tone:'success' },
  3: { key:'rejected', label:'审核驳回', tone:'danger' }
};
const SECKILL_AUDIT_NOW = '2026-11-17 20:30:00';
const seckillApplySku = (name,specCode,originPrice,seckillPrice,stock)=>({ name,specCode,originPrice,seckillPrice,stock });
const seckillRichPlain = (html)=>{ if(!html) return ''; const div=document.createElement('div'); div.innerHTML=html; return (div.textContent||'').replace(/\s+/g,' ').trim(); };
const seckillRichHasContent = (html)=> !!html && (!!seckillRichPlain(html) || /<img|<iframe/i.test(html));
const SECKILL_APPLIES_INITIAL = [
  {
    id:'ap-1', activityId:'sa-2', sessionId:'ss-5', mchName:'山野生活', shopName:'山野生活店',
    spuName:'轻量露营折叠桌', art:'chair', detailDesc:'<p>铝合金折叠结构，桌面承重 30kg，附收纳袋。</p><h3>产品亮点</h3><ul><li><strong>一秒开合</strong>：免安装折叠骨架，收纳厚度仅 8cm</li><li>阳极氧化铝桌面，<span style="color:#e6a23c">耐刮耐高温</span>，可直接放置卡式炉</li><li>附 600D 牛津布收纳袋，肩背手提两用</li></ul><p><img src="data:image/svg+xml,%3Csvg xmlns=%27http://www.w3.org/2000/svg%27 width=%27560%27 height=%27220%27%3E%3Crect width=%27560%27 height=%27220%27 rx=%2712%27 fill=%27%23e8efe9%27/%3E%3Ctext x=%27280%27 y=%27118%27 text-anchor=%27middle%27 font-size=%2718%27 fill=%27%2365726a%27%3EFOLDING TABLE · 120×60cm%3C/text%3E%3C/svg%3E" alt="折叠桌展开示意"/></p><blockquote>露营实测：四人围坐不晃动，桌面可同时放炉具与餐具。</blockquote>',
    auditStatus:1, applyTime:'2026-11-17 10:24:00', auditTime:'', applyRemark:'加场首推，计划限量 80 件。', auditRemark:'',
    skus:[seckillApplySku('原木色 · 中号','CAMP-TABLE-M',459,269,50),seckillApplySku('曜石黑 · 中号','CAMP-TABLE-BK',459,269,30)]
  },
  {
    id:'ap-2', activityId:'sa-2', sessionId:'ss-4', mchName:'日常咖啡', shopName:'日常咖啡研究所',
    spuName:'便携手冲咖啡壶', art:'coffee', detailDesc:'<p>304 不锈钢细口壶，600ml，适配 1–2 人份手冲。</p><p><strong>鹤嘴壶口</strong>断水利落，新手也能稳定控流；壶身一体拉伸，无焊缝死角。</p><ul><li>容量 600ml，约 2 杯手冲水量</li><li>适配电磁炉 / 明火 / 电陶炉</li><li><span style="color:#67c23a">随单赠滤纸 50 张</span>（首发限定）</li></ul>',
    auditStatus:1, applyTime:'2026-11-17 09:08:00', auditTime:'', applyRemark:'新品首发，随单赠滤纸 50 张。', auditRemark:'',
    skus:[seckillApplySku('雾银 · 600ml','KETTLE-600-SV',199,109,60),seckillApplySku('哑黑 · 600ml','KETTLE-600-BK',209,119,40)]
  },
  {
    id:'ap-3', activityId:'sa-2', sessionId:'ss-4', mchName:'山野生活', shopName:'山野生活店',
    spuName:'弧光人体工学休闲椅', art:'chair', detailDesc:'<p>高回弹海绵与弧形靠背，适配客厅阅读角。</p><h3>坐感说明</h3><ol><li>45D 高回弹海绵，久坐不塌陷</li><li>弧形靠背贴合腰椎曲线，倾角 105°</li><li>实木框架 + 蛇形弹簧，承重 150kg</li></ol><blockquote>面料可选棉麻 / 科技布，秒杀场次默认发棉麻款。</blockquote>',
    auditStatus:2, applyTime:'2026-11-15 16:40:00', auditTime:'2026-11-16 09:30:00', applyRemark:'主推款，参与晚八点场。', auditRemark:'资料完整，秒杀价符合活动要求。',
    skus:[seckillApplySku('暖棕 · 标准款','ARC-CHAIR-BR',1299,699,60),seckillApplySku('雾灰 · 标准款','ARC-CHAIR-GY',1299,699,60)]
  },
  {
    id:'ap-4', activityId:'sa-2', sessionId:'ss-4', mchName:'日常咖啡', shopName:'日常咖啡研究所',
    spuName:'便携手摇磨豆机', art:'coffee', detailDesc:'<p>CNC 不锈钢磨芯，外置调节环，支持 36 档研磨。</p><ul><li>420 不锈钢刀盘，<em>意式 / 手冲通用</em></li><li>外置刻度环，换豆无需拆机调档</li><li>双轴承固定，研磨均匀度提升约 30%</li></ul>',
    auditStatus:3, applyTime:'2026-11-15 11:18:00', auditTime:'2026-11-16 10:12:00', applyRemark:'希望进入晚八点场，库存 100 件。', auditRemark:'秒杀价高于近 30 天最低成交价，请调整价格后重新报名。',
    skus:[seckillApplySku('银色 · 标准版','GRINDER-S',329,239,50),seckillApplySku('黑色 · 标准版','GRINDER-BK',329,239,50)]
  },
  {
    id:'ap-5', activityId:'sa-2', sessionId:'ss-5', mchName:'森屿香氛', shopName:'森屿香氛店',
    spuName:'无火香薰礼盒', art:'serum', detailDesc:'<p>雪松与白茶香调，含藤条与 200ml 香薰液。</p><p>礼盒内含：<strong>香薰液 200ml × 1</strong>、纤维藤条 × 6、贺卡 × 1。</p><ul><li>前调白茶 / 中调雪松 / 尾调琥珀</li><li><span style="background-color:#fdf6ec">扩香约 8–10㎡，持香 60 天</span></li></ul>',
    auditStatus:2, applyTime:'2026-11-14 14:05:00', auditTime:'2026-11-15 09:22:00', applyRemark:'加场新品，礼盒装。', auditRemark:'通过，礼盒信息与主图一致。',
    skus:[seckillApplySku('雪松白茶 · 200ml','DIFFUSER-200',259,139,80),seckillApplySku('无花果 · 200ml','DIFFUSER-FIG',259,139,60)]
  },
  {
    id:'ap-6', activityId:'sa-8', sessionId:'ss-14', mchName:'Yeah 数码', shopName:'Yeah 数码生活馆',
    spuName:'主动降噪真无线耳机', art:'headphone', detailDesc:'<p>42dB 主动降噪，双设备连接，单次续航 8 小时。</p><h3>核心参数</h3><ul><li><strong>42dB 混合降噪</strong>，地铁 / 机舱场景实测</li><li>蓝牙 5.4，双设备无缝切换</li><li>单次 8h，含仓 32h，支持快充</li></ul>',
    auditStatus:1, applyTime:'2026-11-05 21:40:00', auditTime:'', applyRemark:'会员专享场，计划报名 200 件。', auditRemark:'',
    skus:[seckillApplySku('曜石黑 · 标准版','TWS-ANC-BK',399,299,100),seckillApplySku('雾银 · 标准版','TWS-ANC-SV',399,299,100)]
  },
  {
    id:'ap-7', activityId:'sa-8', sessionId:'ss-14', mchName:'Yeah 数码', shopName:'Yeah 数码生活馆',
    spuName:'云感降噪头戴耳机', art:'headphone', detailDesc:'<p>40mm 动圈单元，佩戴轻量，支持有线模式。</p><p>整机 <strong>248g</strong>，蛋白皮耳罩 + 减压头梁，长时间佩戴不夹头。</p><ul><li>有线 / 蓝牙双模，没电也能听</li><li>折叠收纳，附硬壳收纳包</li></ul>',
    auditStatus:2, applyTime:'2026-11-03 10:15:00', auditTime:'2026-11-04 09:30:00', applyRemark:'主推款，备货 120 件。', auditRemark:'品牌授权与质检资料已核对，通过。',
    skus:[seckillApplySku('曜石黑 · 标准版','HEAD-ANC-BK',999,599,60),seckillApplySku('雾银 · 标准版','HEAD-ANC-SV',999,599,60)]
  },
  {
    id:'ap-8', activityId:'sa-8', sessionId:'ss-15', mchName:'声屿音频', shopName:'声屿音频店',
    spuName:'开放式运动耳机', art:'headphone', detailDesc:'<p><br></p>',
    auditStatus:3, applyTime:'2026-11-04 16:22:00', auditTime:'2026-11-05 11:10:00', applyRemark:'加场报名 60 件。', auditRemark:'秒杀价与近期活动价差不足，请调整后再报。',
    skus:[seckillApplySku('曜石黑 · 运动版','OPEN-SPORT-BK',299,229,60)]
  },
  {
    id:'ap-9', activityId:'sa-8', sessionId:'ss-15', mchName:'山野生活', shopName:'山野生活店',
    spuName:'户外便携音箱', art:'headphone', detailDesc:'<p>双单元立体声，IPX7 防水，续航 12 小时。</p><ul><li>20W 双单元 + 被动振膜，低频下潜 60Hz</li><li>IPX7 防水，雨天 / 泳池边可用</li><li>支持 TWS 串联，两台组立体声</li></ul><blockquote>挂绳孔位兼容主流登山扣，徒步可外挂背包。</blockquote>',
    auditStatus:2, applyTime:'2026-11-03 14:08:00', auditTime:'2026-11-04 10:02:00', applyRemark:'户外场景主推，库存 80 件。', auditRemark:'资料齐全，通过。',
    skus:[seckillApplySku('松针绿 · 标准版','SPK-OUT-GN',499,329,50),seckillApplySku('岩石灰 · 标准版','SPK-OUT-GY',499,329,30)]
  },
  {
    id:'ap-10', activityId:'sa-5', sessionId:'ss-8', mchName:'山野生活', shopName:'山野生活店',
    spuName:'轻量露营折叠椅', art:'chair', detailDesc:'<p>铝合金支架，收纳体积小，承重 120kg。</p><ul><li>7075 铝合金杖杆，整椅仅 980g</li><li>收纳后约雨伞大小，侧袋可放</li><li>牛津布坐面，透气耐磨</li></ul>',
    auditStatus:2, applyTime:'2026-11-13 09:16:00', auditTime:'2026-11-14 10:05:00', applyRemark:'日间场主推，备货 80 件。', auditRemark:'通过，库存与价格符合要求。',
    skus:[seckillApplySku('沙丘色 · 标准款','CAMP-CHAIR-SD',329,199,40),seckillApplySku('墨绿色 · 标准款','CAMP-CHAIR-GN',329,199,40)]
  },
  {
    id:'ap-11', activityId:'sa-5', sessionId:'ss-8', mchName:'户外星球', shopName:'户外星球装备店',
    spuName:'钛合金户外水杯', art:'coffee', detailDesc:'<p>双层真空结构，轻量钛合金杯体，容量 450ml。</p><p><strong>纯钛内胆</strong>无金属味，盛茶 / 咖啡不串味；双层真空保温约 6 小时。</p><ul><li>整杯 186g，含折叠钛勺</li><li>杯盖可作小杯使用</li></ul>',
    auditStatus:3, applyTime:'2026-11-14 18:30:00', auditTime:'2026-11-15 09:12:00', applyRemark:'新品首发，报名夜间场。', auditRemark:'缺少质检报告，请在报名截止前补充后重新提交。',
    skus:[seckillApplySku('钛本色 · 450ml','CUP-TI-450',399,259,60)]
  },
  {
    id:'ap-12', activityId:'sa-1', sessionId:'ss-1', mchName:'Yeah 数码', shopName:'Yeah 数码生活馆',
    spuName:'头戴式降噪耳机 Pro', art:'headphone', detailDesc:'<p>自适应降噪与空间音频，支持多设备切换。</p><h3>Pro 升级点</h3><ul><li><strong>自适应降噪</strong>：随环境噪声实时调节深度</li><li>空间音频 + 头部追踪，观影更沉浸</li><li><span style="color:#409eff">三设备同时连接</span>，会议 / 手机 / 平板秒切</li></ul>',
    auditStatus:2, applyTime:'2026-10-20 10:22:00', auditTime:'2026-10-21 09:18:00', applyRemark:'早鸟场主推款。', auditRemark:'通过，资料与价格均符合要求。',
    skus:[seckillApplySku('曜石黑 · Pro','HEAD-PRO-BK',1299,799,40),seckillApplySku('雾银 · Pro','HEAD-PRO-SV',1299,799,40)]
  },
  {
    id:'ap-13', activityId:'sa-6', sessionId:'ss-10', mchName:'声屿音频', shopName:'声屿音频店',
    spuName:'便携蓝牙音箱', art:'headphone', detailDesc:'织物网罩与金属提手，支持立体声串联。',
    auditStatus:3, applyTime:'2026-10-29 15:40:00', auditTime:'2026-10-30 10:05:00', applyRemark:'返场活动报名。', auditRemark:'同款近 30 天存在更低成交价，驳回。',
    skus:[seckillApplySku('砂岩灰 · 标准版','SPK-GRAY',399,279,40)]
  },
  {
    id:'ap-14', activityId:'sa-7', sessionId:'ss-13', mchName:'日常咖啡', shopName:'日常咖啡研究所',
    spuName:'夏日冷萃咖啡壶', art:'coffee', detailDesc:'耐热玻璃壶身，配不锈钢滤网，容量 1L。',
    auditStatus:2, applyTime:'2026-08-02 09:50:00', auditTime:'2026-08-03 09:10:00', applyRemark:'夏季清凉节报名。', auditRemark:'通过，历史活动资料完整。',
    skus:[seckillApplySku('透明 · 1L','COLD-BREW-1L',159,89,50)]
  }
];

function seckillApplyStatus(status) {
  return SECKILL_APPLY_STATUS[Number(status)] || SECKILL_APPLY_STATUS[1];
}

function seckillApplyWindowState(activity) {
  if (!activity?.applyBeginTime || !activity?.applyEndTime) return { key:'none', label:'报名未开放', tone:'neutral' };
  if (SECKILL_SESSION_NOW < activity.applyBeginTime) return { key:'upcoming', label:'报名未开始', tone:'info' };
  if (SECKILL_SESSION_NOW < activity.applyEndTime) return { key:'open', label:'报名中', tone:'success' };
  return { key:'closed', label:'报名已结束', tone:'neutral' };
}

function seckillApplySummary(apply) {
  const skus = apply.skus || [];
  const seckillPrices = skus.map((sku)=>Number(sku.seckillPrice)).filter((value)=>Number.isFinite(value));
  const originPrices = skus.map((sku)=>Number(sku.originPrice)).filter((value)=>Number.isFinite(value));
  return {
    skuCount:skus.length,
    stock:skus.reduce((total,sku)=>total+Number(sku.stock||0),0),
    minSeckill:seckillPrices.length?Math.min(...seckillPrices):0,
    maxSeckill:seckillPrices.length?Math.max(...seckillPrices):0,
    minOrigin:originPrices.length?Math.min(...originPrices):0,
    maxOrigin:originPrices.length?Math.max(...originPrices):0
  };
}

const seckillApplyMoney = (value)=>`¥${Number(value||0).toFixed(2)}`;
const seckillApplyRange = (min,max)=>min===max?seckillApplyMoney(min):`${seckillApplyMoney(min)} – ${seckillApplyMoney(max)}`;

function AdminSeckillAudits({ activities, applies, setApplies, sessionsByActivity, route, navigate, notify }) {
  const routeActivityId = routeParam(route,'activityId');
  const defaultActivityId = ()=>{
    const open = activities.find((item)=>item.applyBeginTime <= SECKILL_SESSION_NOW && SECKILL_SESSION_NOW < item.applyEndTime);
    if (open) return open.id;
    const withPending = activities.find((item)=>applies.some((apply)=>apply.activityId===item.id && Number(apply.auditStatus)===1));
    if (withPending) return withPending.id;
    return (activities[0]||{}).id || '';
  };
  const [activityId,setActivityId] = useState(()=>activities.some((item)=>item.id===routeActivityId)?routeActivityId:defaultActivityId());
  const [tab,setTab] = useState('all');
  const [filters,setFilters] = useState({ merchant:'', sessionId:'' });
  const [applied,setApplied] = useState({ merchant:'', sessionId:'' });
  const [reviewing,setReviewing] = useState(null);
  const [auditStatus,setAuditStatus] = useState('');
  const [auditRemark,setAuditRemark] = useState('');
  useEffect(()=>{
    if (routeActivityId && activities.some((item)=>item.id===routeActivityId)) setActivityId(routeActivityId);
  },[routeActivityId]);
  useEffect(()=>{
    setTab('all');
    setFilters({ merchant:'', sessionId:'' });
    setApplied({ merchant:'', sessionId:'' });
  },[activityId]);

  const activity = activities.find((item)=>item.id===activityId) || activities.find((item)=>item.id===defaultActivityId()) || activities[0] || { id:'', name:'暂无活动' };
  const applyState = seckillApplyWindowState(activity);
  const activitySessions = sessionsByActivity[activity.id] || [];
  const sortedSessions = useMemo(()=>[...activitySessions].sort((a,b)=>a.beginTime.localeCompare(b.beginTime)||a.sort-b.sort),[activity.id,sessionsByActivity]);
  const activityApplies = useMemo(()=>applies.filter((row)=>row.activityId===activity.id),[applies,activity.id]);
  const baseRows = useMemo(()=>activityApplies.filter((row)=>{
    const keyword = applied.merchant.trim().toLowerCase();
    const merchantMatch = !keyword || `${row.mchName}${row.shopName}`.toLowerCase().includes(keyword);
    const sessionMatch = !applied.sessionId || row.sessionId === applied.sessionId;
    return merchantMatch && sessionMatch;
  }).sort((a,b)=>b.applyTime.localeCompare(a.applyTime)),[activityApplies,applied]);
  const counts = { all:baseRows.length, pending:0, passed:0, rejected:0 };
  baseRows.forEach((row)=>{ counts[seckillApplyStatus(row.auditStatus).key] += 1; });
  const filtered = baseRows.filter((row)=>tab==='all'||seckillApplyStatus(row.auditStatus).key===tab);
  const pendingInActivity = activityApplies.filter((row)=>Number(row.auditStatus)===1).length;
  const processed = counts.passed + counts.rejected;
  const passRate = processed ? Math.round((counts.passed / processed) * 100) : null;
  const search = ()=>setApplied({ ...filters });
  const reset = ()=>{
    const empty = { merchant:'', sessionId:'' };
    setFilters(empty);
    setApplied(empty);
    setTab('all');
  };
  const openReview = (row)=>{
    setReviewing(row);
    setAuditStatus('');
    setAuditRemark('');
  };
  const closeReview = ()=>{
    setReviewing(null);
    setAuditStatus('');
    setAuditRemark('');
  };
  const submitAudit = ()=>{
    if (!reviewing || !auditStatus) return;
    const nextStatus = Number(auditStatus);
    setApplies((current)=>current.map((item)=>item.id===reviewing.id
      ? { ...item, auditStatus:nextStatus, auditRemark:auditRemark.trim(), auditTime:SECKILL_AUDIT_NOW }
      : item));
    notify(nextStatus===2?'报名已审核通过':'报名已审核驳回');
    closeReview();
  };
  const tableIntroTone = applyState.key==='closed'&&pendingInActivity ? 'is-warning' : applyState.key==='open' ? 'is-open' : '';
  const tableIntroText = applyState.key==='open'
    ? `报名进行中，截止 ${activity.applyEndTime.slice(0,16)}；仅待审核记录可执行审核。`
    : applyState.key==='upcoming'
      ? `报名将于 ${activity.applyBeginTime.slice(0,16)} 开始；当前可提前核对活动与场次。`
      : pendingInActivity
        ? `报名已于 ${activity.applyEndTime.slice(0,16)} 结束，仍有 ${pendingInActivity} 条待审核；未通过的报名不会进入秒杀商品池。`
        : `报名已于 ${activity.applyEndTime.slice(0,16)} 结束；历史审核记录仅可查看。`;
  const reviewingApply = reviewing ? applies.find((item)=>item.id===reviewing.id) || reviewing : null;
  const reviewSummary = reviewingApply ? seckillApplySummary(reviewingApply) : null;
  const reviewMeta = reviewingApply ? seckillApplyStatus(reviewingApply.auditStatus) : null;
  const reviewPending = Boolean(reviewingApply && Number(reviewingApply.auditStatus)===1);
  const reviewSession = reviewingApply ? sortedSessions.find((item)=>item.id===reviewingApply.sessionId) : null;
  const reviewPriceText = reviewSummary ? seckillApplyRange(reviewSummary.minSeckill,reviewSummary.maxSeckill) : '';
  const reviewOriginText = reviewSummary ? seckillApplyRange(reviewSummary.minOrigin,reviewSummary.maxOrigin) : '';

  return <section className="admin-page seckill-audit-page" data-screen-label="平台后台-秒杀报名审核">
    <div className="ss-activity-bar">
      <span className="ss-activity-icon"><AdminIcon name="DocumentChecked" size={22}/></span>
      <div className="ss-activity-copy">
        <div className="ss-activity-title"><h2>{activity.name}</h2><StatusTicket tone={applyState.tone}><span className="ss-status-live">{applyState.key==='open'&&<i className="ss-live-dot"></i>}{applyState.label}</span></StatusTicket></div>
        <div className="ss-activity-meta">
          <span><AdminIcon name="Calendar" size={13}/>报名 {activity.applyBeginTime&&activity.applyEndTime?`${activity.applyBeginTime.slice(0,16)} 至 ${activity.applyEndTime.slice(0,16)}`:'待设置'}</span>
          <span><AdminIcon name="Lightning" size={13}/>活动 {activity.activityBeginTime&&activity.activityEndTime?`${activity.activityBeginTime.slice(0,16)} 至 ${activity.activityEndTime.slice(0,16)}`:'待设置'}</span>
          <span><AdminIcon name="Clock" size={13}/>共 {sortedSessions.length} 个场次</span>
          <span><AdminIcon name="DocumentChecked" size={13}/>报名 {activityApplies.length} 条</span>
        </div>
      </div>
      <div className="ss-activity-controls">
        <label className="ss-activity-switch"><span>切换活动</span><select value={activity.id} onChange={(event)=>setActivityId(event.target.value)}>{activities.map((item)=><option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
        <button className="ss-back-link" onClick={()=>navigate('platform/seckill?tab=活动管理')}><AdminIcon name="back" size={15}/>返回活动管理</button>
      </div>
    </div>
    <div className="ss-queue">
      <div className="ss-tabs">{SECKILL_APPLY_TABS.map((item)=><button key={item.key} data-status={item.key} className={tab===item.key?'active':''} onClick={()=>setTab(item.key)}>{item.label}<span className="ss-tab-count">{counts[item.key]}</span></button>)}</div>
      <div className="ss-queue-summary">
        <span className={counts.pending?'is-warning':''}>{counts.pending>0&&<i className="ss-live-dot"></i>}待审核 <b>{counts.pending}</b> 条</span>
        <span><AdminIcon name="check" size={13}/>通过率 <b>{passRate===null?'—':`${passRate}%`}</b></span>
      </div>
    </div>
    <FilterBar onReset={reset} onSearch={search}>
      <label><span>申请商家</span><input value={filters.merchant} onChange={(event)=>setFilters({...filters,merchant:event.target.value})} placeholder="商家名称或店铺名称" onKeyDown={(event)=>event.key==='Enter'&&search()}/></label>
      <label><span>报名场次</span><select value={filters.sessionId} onChange={(event)=>setFilters({...filters,sessionId:event.target.value})}><option value="">全部场次</option>{sortedSessions.map((item)=><option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
    </FilterBar>
    <div className="admin-table-frame">
      <div className={`ss-table-intro sa-table-intro${tableIntroTone?` ${tableIntroTone}`:''}`}>
        <span><AdminIcon name={tableIntroTone==='is-warning'?'warning':applyState.key==='open'?'clock':'DocumentChecked'} size={15}/>{tableIntroText}</span>
        <div className="sa-intro-right"><b>筛选 {filtered.length} 条</b>{counts.pending>0&&<span className="sa-pending-chip">待审 {counts.pending}</span>}</div>
      </div>
      <table data-align-table="t8" className="admin-table seckill-audit-table">
        <thead><tr><th>报名商品</th><th>商家 / 店铺</th><th>场次</th><th>报名时间</th><th>审核状态</th><th>申请备注</th><th>审核备注</th><th className="actions">操作</th></tr></thead>
        <tbody>{filtered.map((row)=>{
          const meta = seckillApplyStatus(row.auditStatus);
          const session = sortedSessions.find((item)=>item.id===row.sessionId);
          return <tr key={row.id}>
            <td><div className="cb"><div className="sa-product-cell">
              <span className="sa-product-art"><ProductArt type={row.art}/></span>
              <div className="sa-product-copy"><b title={row.spuName}>{row.spuName}</b><small title={seckillRichPlain(row.detailDesc)||'暂无商品描述'}>{seckillRichPlain(row.detailDesc)||'暂无商品描述'}</small></div>
            </div></div></td>
            <td><div className="cb"><div className="sa-party-cell"><b title={row.mchName}>{row.mchName}</b><small title={row.shopName}>{row.shopName}</small></div></div></td>
            <td><div className="cb"><div className="sa-session-cell"><b title={session?.name}>{session?.name||'—'}</b><small>{session?`${session.beginTime.slice(5,16)} – ${session.endTime.slice(11,16)}`:'场次待确认'}</small></div></div></td>
            <td><div className="cb"><div className="sa-time-cell"><b>{row.applyTime.slice(0,10)}</b><small>{row.applyTime.slice(11,16)}</small></div></div></td>
            <td><div className="cb"><StatusTicket tone={meta.tone}>{meta.label}</StatusTicket></div></td>
            <td><div className="cb"><span className={`sa-remark${row.applyRemark?'':' is-empty'}`} title={row.applyRemark||'未填写申请备注'}>{row.applyRemark||'未填写'}</span></div></td>
            <td><div className="cb"><span className={`sa-remark${row.auditRemark?'':' is-empty'}`} title={row.auditRemark||(meta.key==='pending'?'待审核':'未填写审核备注')}>{row.auditRemark||(meta.key==='pending'?'—':'未填写')}</span></div></td>
            <td className="actions"><div className="cb"><button className={`table-link${meta.key==='pending'?' sa-review-link':''}`} onClick={()=>openReview(row)}>{meta.key==='pending'?'审核':'查看'}</button></div></td>
          </tr>;
        })}</tbody>
      </table>
      {!filtered.length&&<div className="admin-empty"><AdminIcon name="DocumentChecked" size={28}/><b>{activityApplies.length?'没有匹配的报名记录':'当前活动还没有报名记录'}</b><span>{activityApplies.length?'调整申请商家或报名场次筛选后重试':applyState.key==='upcoming'?'报名开始后，商家提交的记录会出现在这里':'商家提交报名后，记录会出现在这里'}</span></div>}
      <AdminPagination count={filtered.length}/>
    </div>

    {reviewingApply&&reviewMeta&&reviewSummary&&<div className="dialog-mask" onClick={closeReview}>
      <section className="admin-dialog sa-audit-dialog" onClick={(event)=>event.stopPropagation()}>
        <div className="dialog-head">
          <div><span className="drawer-kicker">{reviewPending?'APPLY REVIEW':'APPLY DETAIL'}</span><h2>{reviewPending?'报名审核':'报名详情'}</h2></div>
          <button onClick={closeReview}>×</button>
        </div>
        <div className="sa-dialog-scroll">
          <div className="sa-apply-context">
            <div className="sa-context-item"><span>商家 / 店铺</span><b title={reviewingApply.mchName}>{reviewingApply.mchName}</b><small title={reviewingApply.shopName}>{reviewingApply.shopName}</small></div>
            <div className="sa-context-item"><span>活动 / 场次</span><b title={activity.name}>{activity.name}</b><small title={reviewSession?.name}>{reviewSession?.name||'场次待确认'}</small></div>
            <div className="sa-context-item"><span>报名时间</span><b>{reviewingApply.applyTime.slice(0,16)}</b><small>{reviewingApply.auditTime?`审核 ${reviewingApply.auditTime.slice(0,16)}`:'等待平台审核'}</small></div>
            <div className="sa-context-item"><span>审核状态</span><StatusTicket tone={reviewMeta.tone}>{reviewMeta.label}</StatusTicket><small>{reviewPending?'当前可提交审核结果':reviewingApply.auditRemark?'已填写审核备注':'未填写审核备注'}</small></div>
          </div>
          <div className="sa-product-panel">
            <span className="sa-product-art large"><ProductArt type={reviewingApply.art}/></span>
            <div className="sa-product-info">
              <h3>{reviewingApply.spuName}</h3>
              <p>{seckillRichPlain(reviewingApply.detailDesc)||'暂无商品描述'}</p>
              <div className="sa-product-tags"><span><AdminIcon name="Goods" size={13}/>{reviewSummary.skuCount} 个 SKU</span><span><AdminIcon name="Tickets" size={13}/>报名库存 {reviewSummary.stock} 件</span></div>
            </div>
            <div className="sa-price-panel"><span>秒杀价</span><b>{reviewPriceText}</b><small>原价 {reviewOriginText}</small></div>
          </div>
          <div className="sa-apply-note"><AdminIcon name="message" size={15}/><div><b>商家报名备注</b><p>{reviewingApply.applyRemark||'未填写报名备注'}</p></div></div>
          <div className="sa-section">
            <div className="sa-section-head"><b>SKU 信息</b><small>共 {reviewSummary.skuCount} 个规格 · 报名库存 {reviewSummary.stock} 件</small></div>
            {reviewSummary.skuCount?<table data-align-table="t9" className="sa-sku-table">
              <thead><tr><th>SKU 名称</th><th>规格编码</th><th>原价</th><th>秒杀价</th><th>库存</th><th>SKU 图片</th></tr></thead>
              <tbody>{reviewingApply.skus.map((sku,index)=><tr key={`${sku.specCode}-${index}`}>
                <td><div className="cb"><b className="sa-sku-name" title={sku.name}>{sku.name}</b></div></td>
                <td><div className="cb"><span className="mono">{sku.specCode}</span></div></td>
                <td><div className="cb"><span className="sa-sku-origin">{seckillApplyMoney(sku.originPrice)}</span></div></td>
                <td><div className="cb"><span className="sa-sku-price">{seckillApplyMoney(sku.seckillPrice)}</span></div></td>
                <td><div className="cb">{sku.stock} 件</div></td>
                <td><div className="cb"><span className="sa-sku-art"><ProductArt type={reviewingApply.art}/></span></div></td>
              </tr>)}</tbody>
            </table>:<div className="sa-empty-sku">暂无 SKU 信息</div>}
          </div>
          <div className="sa-section">
            <div className="sa-section-head"><b>商品详情</b><small>商家提交的富文本内容 · 只读预览</small></div>
            {seckillRichHasContent(reviewingApply.detailDesc)
              ?<div className="sa-richtext" dangerouslySetInnerHTML={{__html:reviewingApply.detailDesc}}/>
              :<div className="sa-empty-sku">商家未提交商品详情</div>}
          </div>
          <div className="sa-section sa-review-section">
            <div className="sa-section-head"><b>{reviewPending?'审核操作':'审核结果'}</b><small>{reviewPending?'审核结果将同步至秒杀商品':'审核记录仅供查看，不可再次提交'}</small></div>
            {reviewPending?<>
              <div className="sa-result-options" role="radiogroup" aria-label="审核结果">
                <label className={`sa-result-option is-pass${auditStatus==='2'?' is-active':''}`}>
                  <input type="radio" name="sa-audit-result" value="2" checked={auditStatus==='2'} onChange={()=>setAuditStatus('2')}/>
                  <span className="sa-result-check"><AdminIcon name="check" size={16}/></span>
                  <span className="sa-result-copy"><b>审核通过</b><small>商品资料与价格符合要求，进入对应场次的秒杀商品池。</small></span>
                </label>
                <label className={`sa-result-option is-reject${auditStatus==='3'?' is-active':''}`}>
                  <input type="radio" name="sa-audit-result" value="3" checked={auditStatus==='3'} onChange={()=>setAuditStatus('3')}/>
                  <span className="sa-result-check"><AdminIcon name="close" size={16}/></span>
                  <span className="sa-result-copy"><b>审核驳回</b><small>商品资料或价格不符合要求，商家可调整后重新报名。</small></span>
                </label>
              </div>
              <label className="sa-remark-field">
                <span>审核备注<small>选填，审核结果与备注将同步给商家</small></span>
                <textarea maxLength={200} value={auditRemark} onChange={(event)=>setAuditRemark(event.target.value)} placeholder={auditStatus==='3'?'建议说明驳回原因，便于商家调整后重新报名':'可补充审核说明，最多 200 字'}></textarea>
                <div className="sa-remark-meta"><small>{auditRemark.length}/200</small>{auditStatus==='3'&&<small className="is-warning">驳回时建议填写原因</small>}</div>
              </label>
              <div className="sa-audit-tip"><AdminIcon name="warning" size={14}/>审核结果提交后不可修改，请先核对商品、价格与库存信息。</div>
            </>:<div className={`sa-audit-result is-${reviewMeta.key}`}>
              <div className="sa-result-head"><StatusTicket tone={reviewMeta.tone}>{reviewMeta.label}</StatusTicket><span>审核时间 <b>{reviewingApply.auditTime?reviewingApply.auditTime.slice(0,16):'—'}</b></span></div>
              <div className="sa-result-note"><span>审核备注</span><p>{reviewingApply.auditRemark||'未填写审核备注'}</p></div>
            </div>}
          </div>
        </div>
        <div className="dialog-actions">
          {reviewPending?<>
            <button onClick={closeReview}>取消</button>
            <button className="primary" disabled={!auditStatus} onClick={submitAudit}>提交审核</button>
          </>:<button onClick={closeReview}>关闭</button>}
        </div>
      </section>
    </div>}
  </section>;
}

const POINTS_BIZ_TYPES = [
  { value:'REGISTER', label:'注册', tone:'success' },
  { value:'ORDER_COMPLETE', label:'订单完成', tone:'success' },
  { value:'ORDER_REFUND', label:'订单退款', tone:'danger' },
  { value:'REVIEW', label:'完成评价', tone:'info' },
  { value:'MANUAL_ADJUST', label:'平台调整', tone:'warning' },
  { value:'POINTS_EXCHANGE', label:'积分兑换', tone:'neutral' },
  { value:'POINTS_CANCEL', label:'兑换取消', tone:'neutral' }
];

const POINTS_LEDGER_INITIAL = [
  { id:'pl-1', memberId:'211386217001', bizType:'ORDER_COMPLETE', description:'订单完成赠送', changePoints:399, balanceBefore:881, balanceAfter:1280, bizNo:'YM202611020018', createTime:'2026-11-01 18:42:10' },
  { id:'pl-2', memberId:'211386217001', bizType:'REVIEW', description:'订单完成评价奖励', changePoints:10, balanceBefore:871, balanceAfter:881, bizNo:'RV202610280001', createTime:'2026-10-28 09:16:22' },
  { id:'pl-3', memberId:'211386217004', bizType:'POINTS_EXCHANGE', description:'兑换品牌帆布袋', changePoints:-300, balanceBefore:3800, balanceAfter:3500, bizNo:'PX202610240012', createTime:'2026-10-24 16:30:08' },
  { id:'pl-4', memberId:'211386217002', bizType:'MANUAL_ADJUST', description:'平台调整积分', changePoints:500, balanceBefore:3000, balanceAfter:3500, bizNo:'ADJ202610200003', createTime:'2026-10-20 10:12:45' },
  { id:'pl-5', memberId:'211386217005', bizType:'REGISTER', description:'新会员注册赠送', changePoints:100, balanceBefore:0, balanceAfter:100, bizNo:'REG202610180005', createTime:'2026-10-18 08:12:53' }
];

const POINTS_RULES_INITIAL = [
  { id:'rule-register', code:'REGISTER', name:'注册赠送', calcType:1, fixedPoints:100, pointsPerYuan:0, enabled:true, description:'新会员注册成功赠送 100 积分' },
  { id:'rule-order', code:'ORDER_COMPLETE', name:'订单完成', calcType:2, fixedPoints:0, pointsPerYuan:1, enabled:true, description:'订单完成后，每 1 元优惠后应付金额获得 1 积分' },
  { id:'rule-review', code:'REVIEW', name:'完成评价', calcType:1, fixedPoints:10, pointsPerYuan:0, enabled:true, description:'订单完成评价赠送 10 积分' }
];

function AdminPoints({ route, notify }) {
  const requestedTab=routeParam(route,'tab');
  const initialMemberId=routeParam(route,'memberId')||'';
  const [tab,setTab] = useState(requestedTab==='积分规则'?'rule':'ledger');
  const [filters,setFilters] = useState({ memberId:initialMemberId, bizType:'全部类型' });
  const [applied,setApplied] = useState({ memberId:initialMemberId, bizType:'全部类型' });
  const [rules,setRules] = useState(POINTS_RULES_INITIAL);
  const [editing,setEditing] = useState(null);
  useEffect(()=>{
    setTab(requestedTab==='积分规则'?'rule':'ledger');
    setFilters({ memberId:initialMemberId, bizType:'全部类型' });
    setApplied({ memberId:initialMemberId, bizType:'全部类型' });
  },[requestedTab,initialMemberId]);
  const bizOption=(type)=>POINTS_BIZ_TYPES.find((item)=>item.value===type);
  const filtered = POINTS_LEDGER_INITIAL.filter((item) => {
    const memberKeyword=applied.memberId.trim();
    const matchMember=!memberKeyword || item.memberId.includes(memberKeyword);
    const matchBiz=applied.bizType==='全部类型' || item.bizType===applied.bizType;
    return matchMember && matchBiz;
  });
  const search=()=>setApplied({...filters});
  const reset=()=>{const empty={ memberId:'', bizType:'全部类型' };setFilters(empty);setApplied(empty);};
  const saveRule=()=>{
    if(!editing) return;
    if(Number(editing.fixedPoints)<0 || Number(editing.pointsPerYuan)<0) return notify('规则值不能小于 0');
    setRules(rules.map((rule)=>rule.id===editing.id?{...editing}:rule));
    setEditing(null);
    notify('积分规则已保存');
  };
  const changeRuleValue=(value)=>setEditing({...editing,[editing.calcType===1?'fixedPoints':'pointsPerYuan']:Number(value)});
  return (
    <section className="admin-page points-admin-page" data-screen-label={`平台后台积分管理-${tab}`}>
      <div className="module-tabs">
        <button className={tab==='ledger'?'active':''} onClick={()=>setTab('ledger')}>积分流水</button>
        <button className={tab==='rule'?'active':''} onClick={()=>setTab('rule')}>积分规则</button>
      </div>

      {tab==='ledger'?<>
        <FilterBar onReset={reset} onSearch={search}>
          <label><span>会员 ID</span><input value={filters.memberId} onChange={(e)=>setFilters({...filters,memberId:e.target.value})} placeholder="请输入会员 ID"/></label>
          <label><span>业务类型</span><select value={filters.bizType} onChange={(e)=>setFilters({...filters,bizType:e.target.value})}><option>全部类型</option>{POINTS_BIZ_TYPES.map((item)=><option key={item.value} value={item.value}>{item.label}</option>)}</select></label>
        </FilterBar>
        <div className="admin-table-frame">
          <table data-align-table="t10" className="admin-table points-ledger-table">
            <thead><tr><th>会员ID</th><th>业务类型</th><th>说明</th><th className="numeric">变动</th><th className="numeric">变动前</th><th className="numeric">变动后</th><th>业务单号</th><th>创建时间</th></tr></thead>
            <tbody>{filtered.map((item)=>{const biz=bizOption(item.bizType);return <tr key={item.id}>
              <td className="mono"><div className="cb"><CopyableText value={item.memberId} label="会员 ID" notify={notify}/></div></td>
              <td><div className="cb"><StatusTicket tone={biz?.tone||'neutral'}>{biz?.label||item.bizType}</StatusTicket></div></td>
              <td><div className="cb">{item.description}</div></td>
              <td className={`numeric ${item.changePoints>=0?'positive-amount':'negative-amount'}`}><div className="cb">{item.changePoints>=0?'+':''}{item.changePoints.toLocaleString()}</div></td>
              <td className="numeric"><div className="cb">{item.balanceBefore.toLocaleString()}</div></td>
              <td className="numeric"><div className="cb">{item.balanceAfter.toLocaleString()}</div></td>
              <td className="mono"><div className="cb"><CopyableText value={item.bizNo} label="业务单号" notify={notify}/></div></td>
              <td><div className="cb"><span>{item.createTime.slice(0,10)}</span><small className="cell-sub">{item.createTime.slice(11)}</small></div></td>
            </tr>;})}</tbody>
          </table>
          {!filtered.length&&<div className="admin-empty"><AdminIcon name="Coin" size={28}/><b>没有匹配的积分流水</b><span>调整筛选条件后重试</span></div>}
          <AdminPagination count={filtered.length}/>
        </div>
      </>:<>
        <div className="admin-table-frame">
          <table data-align-table="t11" className="admin-table points-rule-table">
            <thead><tr><th>规则名称</th><th>编码</th><th>计算方式</th><th className="numeric">规则值</th><th>说明</th><th>状态</th><th className="actions">操作</th></tr></thead>
            <tbody>{rules.map((rule)=><tr key={rule.id}>
              <td><div className="cb"><b>{rule.name}</b></div></td>
              <td className="mono"><div className="cb">{rule.code}</div></td>
              <td><div className="cb">{rule.calcType===1?'固定积分':'按金额比例'}</div></td>
              <td className="numeric"><div className="cb">{rule.calcType===1?`${rule.fixedPoints} 积分`:`每元 ${rule.pointsPerYuan} 积分`}</div></td>
              <td><div className="cb">{rule.description}</div></td>
              <td><div className="cb"><StatusTicket tone={rule.enabled?'success':'neutral'}>{rule.enabled?'启用':'停用'}</StatusTicket></div></td>
              <td className="actions"><div className="cb"><button className="table-link" onClick={()=>setEditing({...rule})}>编辑</button></div></td>
            </tr>)}</tbody>
          </table>
        </div>
      </>}

      {editing&&<div className="dialog-mask" onClick={()=>setEditing(null)}><section className="admin-dialog points-rule-dialog" onClick={(event)=>event.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">POINTS RULE</span><h2>编辑积分规则</h2></div><button onClick={()=>setEditing(null)}>×</button></div>
        <label className="dialog-field"><span>规则名称</span><input value={editing.name} disabled/></label>
        <label className="dialog-field"><span>规则编码</span><input value={editing.code} disabled/></label>
        <label className="dialog-field"><span>{editing.calcType===1?'固定积分':'每元积分'}</span><input type="number" min="0" step={editing.calcType===1?'1':'0.1'} value={editing.calcType===1?editing.fixedPoints:editing.pointsPerYuan} onChange={(event)=>changeRuleValue(event.target.value)}/></label>
        <label className="points-rule-enabled"><span>启用规则</span><input type="checkbox" checked={editing.enabled} onChange={(event)=>setEditing({...editing,enabled:event.target.checked})}/></label>
        <label className="dialog-field"><span>规则说明</span><textarea rows="3" maxLength="255" value={editing.description} onChange={(event)=>setEditing({...editing,description:event.target.value})}/></label>
        <div className="dialog-actions"><button onClick={()=>setEditing(null)}>取消</button><button className="primary" onClick={saveRule}>保存</button></div>
      </section></div>}
    </section>
  );
}

const POINTS_MALL_CATEGORIES_INITIAL = [
  { id:'pc-1', name:'实物好礼', parentId:0, art:'coffee', sort:1, enabled:true, createTime:'2026-10-01 09:12:10' },
  { id:'pc-2', name:'优惠券', parentId:0, art:'serum', sort:2, enabled:true, createTime:'2026-10-01 09:16:32' },
  { id:'pc-3', name:'会员权益', parentId:0, art:'headphone', sort:3, enabled:true, createTime:'2026-10-01 09:20:48' },
  { id:'pc-4', name:'居家好物', parentId:'pc-1', art:'chair', sort:1, enabled:true, createTime:'2026-10-02 10:08:20' },
  { id:'pc-5', name:'数码周边', parentId:'pc-1', art:'headphone', sort:2, enabled:false, createTime:'2026-10-02 10:12:36' }
];

const POINTS_MALL_PRODUCTS_INITIAL = [
  { id:'pp-1', name:'品牌帆布袋', subtitle:'环保棉帆布，通勤百搭', categoryId:'pc-1', productType:'PHYSICAL', fulfillmentType:'LOGISTICS', beginTime:'2026-10-01 00:00:00', endTime:'2026-12-31 23:59:59', perMemberLimit:1, enabled:true, art:'coffee', detailDesc:'环保棉帆布，适合日常通勤与购物。', sort:1, skus:[{id:'sku-1',name:'标准款',pointsPrice:600,stock:36,totalStock:60,enabled:true},{id:'sku-2',name:'加厚款',pointsPrice:880,stock:12,totalStock:30,enabled:true}] },
  { id:'pp-2', name:'全店 20 元券', subtitle:'满 100 元可用', categoryId:'pc-2', productType:'COUPON', fulfillmentType:'AUTO_COUPON', beginTime:'2026-10-01 00:00:00', endTime:'2026-12-31 23:59:59', perMemberLimit:0, enabled:true, art:'serum', detailDesc:'兑换后自动发放至会员优惠券账户。', sort:2, skus:[{id:'sku-3',name:'20 元券',pointsPrice:300,stock:88,totalStock:200,enabled:true}] },
  { id:'pp-3', name:'专属客服权益', subtitle:'30 天专属服务通道', categoryId:'pc-3', productType:'BENEFIT', fulfillmentType:'MANUAL', beginTime:'2026-10-01 00:00:00', endTime:'2026-12-31 23:59:59', perMemberLimit:1, enabled:false, art:'headphone', detailDesc:'兑换后由平台客服人工开通。', sort:3, skus:[{id:'sku-4',name:'30 天权益',pointsPrice:1200,stock:12,totalStock:20,enabled:true}] }
];

const POINTS_MALL_ORDERS_INITIAL = [
  { id:'po-1', orderNo:'PX20261027001', memberId:'211386217001', productId:'pp-1', productName:'品牌帆布袋', skuName:'标准款', art:'coffee', pointsPrice:600, quantity:1, totalPoints:600, fulfillmentType:'LOGISTICS', status:'1', createTime:'2026-10-27 10:18:22', receiverName:'林可', receiverContact:'138****7621', receiverAddress:'浙江省杭州市西湖区文三路 88 号', providerCode:'', trackingNo:'', fulfillmentRemark:'', cancelReason:'', logs:[{time:'2026-10-27 10:18:22',text:'会员提交兑换'}] },
  { id:'po-2', orderNo:'PX20261026006', memberId:'211386217004', productId:'pp-3', productName:'专属客服权益', skuName:'30 天权益', art:'headphone', pointsPrice:1200, quantity:1, totalPoints:1200, fulfillmentType:'MANUAL', status:'1', createTime:'2026-10-26 16:42:08', receiverName:'陈一禾', receiverContact:'177****4403', receiverAddress:'', providerCode:'', trackingNo:'', fulfillmentRemark:'', cancelReason:'', logs:[{time:'2026-10-26 16:42:08',text:'会员提交兑换'}] },
  { id:'po-3', orderNo:'PX20261020008', memberId:'211386217002', productId:'pp-2', productName:'全店 20 元券', skuName:'20 元券', art:'serum', pointsPrice:300, quantity:1, totalPoints:300, fulfillmentType:'AUTO_COUPON', status:'3', createTime:'2026-10-20 09:35:40', receiverName:'周予安', receiverContact:'186****3048', receiverAddress:'', providerCode:'', trackingNo:'', fulfillmentRemark:'优惠券已自动发放', cancelReason:'', logs:[{time:'2026-10-20 09:35:40',text:'会员提交兑换'},{time:'2026-10-20 09:35:42',text:'系统自动发放优惠券，兑换完成'}] },
  { id:'po-4', orderNo:'PX20261018003', memberId:'211386217005', productId:'pp-1', productName:'品牌帆布袋', skuName:'加厚款', art:'coffee', pointsPrice:880, quantity:1, totalPoints:880, fulfillmentType:'LOGISTICS', status:'4', createTime:'2026-10-18 14:06:18', receiverName:'木棉', receiverContact:'135****2287', receiverAddress:'浙江省杭州市滨江区江南大道 66 号', providerCode:'', trackingNo:'', fulfillmentRemark:'', cancelReason:'会员主动取消', logs:[{time:'2026-10-18 14:06:18',text:'会员提交兑换'},{time:'2026-10-18 14:20:05',text:'会员取消兑换'}] }
];

const pointsProductTypeText = (type) => ({ PHYSICAL:'实物商品', COUPON:'优惠券', BENEFIT:'虚拟权益' }[type] || '-');
const pointsFulfillmentText = (type) => ({ LOGISTICS:'物流发货', AUTO_COUPON:'自动发券', MANUAL:'人工履约' }[type] || '-');
const pointsOrderStatusText = (status) => ({ '1':'待履约', '2':'已发货', '3':'已完成', '4':'已取消' }[status] || '-');
const pointsOrderTone = (status) => ({ '1':'warning', '2':'info', '3':'success', '4':'neutral' }[status] || 'neutral');

function AdminPointsMall({ route, notify }) {
  const requestedTab=routeParam(route,'tab');
  const toTab=(value)=>value==='积分商品'?'product':value==='兑换订单'?'order':'category';
  const [tab,setTab] = useState(toTab(requestedTab));
  useEffect(()=>setTab(toTab(requestedTab)),[requestedTab]);
  const [categories,setCategories] = useState(POINTS_MALL_CATEGORIES_INITIAL);
  const [products,setProducts] = useState(POINTS_MALL_PRODUCTS_INITIAL);
  const [orders,setOrders] = useState(POINTS_MALL_ORDERS_INITIAL);
  const [categoryEditing,setCategoryEditing] = useState(null);
  const [productEditing,setProductEditing] = useState(null);
  const [skuDialog,setSkuDialog] = useState(null);
  const [orderDetail,setOrderDetail] = useState(null);
  const [orderAction,setOrderAction] = useState(null);
  const [productFilters,setProductFilters] = useState({ keyword:'', categoryId:'全部', status:'全部状态' });
  const [appliedProductFilters,setAppliedProductFilters] = useState({ keyword:'', categoryId:'全部', status:'全部状态' });
  const [orderFilters,setOrderFilters] = useState({ orderNo:'', memberId:'', status:'全部状态' });
  const [appliedOrderFilters,setAppliedOrderFilters] = useState({ orderNo:'', memberId:'', status:'全部状态' });
  const categoryName = (id) => categories.find((item)=>item.id===id)?.name || (id===0?'一级分类':'-');
  const productTypeText = (type) => pointsProductTypeText(type);
  const fulfillmentText = (type) => pointsFulfillmentText(type);
  const filteredProducts = products.filter((item) => {
    const keyword=appliedProductFilters.keyword.trim();
    const matchKeyword=!keyword || item.name.includes(keyword);
    const matchCategory=appliedProductFilters.categoryId==='全部' || item.categoryId===appliedProductFilters.categoryId;
    const matchStatus=appliedProductFilters.status==='全部状态' || item.enabled===(appliedProductFilters.status==='已上架');
    return matchKeyword && matchCategory && matchStatus;
  });
  const filteredOrders = orders.filter((item) => {
    const matchNo=!appliedOrderFilters.orderNo.trim() || item.orderNo.includes(appliedOrderFilters.orderNo.trim());
    const matchMember=!appliedOrderFilters.memberId.trim() || item.memberId.includes(appliedOrderFilters.memberId.trim());
    const matchStatus=appliedOrderFilters.status==='全部状态' || item.status===appliedOrderFilters.status;
    return matchNo && matchMember && matchStatus;
  });
  const toggleProduct = (product) => {
    setProducts(products.map((item)=>item.id===product.id?{...item,enabled:!item.enabled}:item));
    notify(`积分商品已${product.enabled?'下架':'上架'}`);
  };
  const openCategory = (category) => setCategoryEditing(category ? {...category} : { id:'', name:'', parentId:0, art:'coffee', sort:1, enabled:true });
  const saveCategory = () => {
    if (!categoryEditing.name.trim()) return notify('请输入分类名称');
    if (categoryEditing.id) {
      setCategories(categories.map((item)=>item.id===categoryEditing.id?{...categoryEditing,createTime:item.createTime}:item));
    } else {
      setCategories([...categories,{...categoryEditing,id:`pc-${Date.now()}`,createTime:'2026-11-11 10:00:00'}]);
    }
    setCategoryEditing(null);
    notify('积分商城分类已保存');
  };
  const removeCategory = (category) => {
    if (categories.some((item)=>item.parentId===category.id) || products.some((item)=>item.categoryId===category.id)) {
      notify('该分类下仍有子分类或商品，暂不可删除');
      return;
    }
    setCategories(categories.filter((item)=>item.id!==category.id));
    notify('积分商城分类已删除');
  };
  const openProduct = (product) => setProductEditing(product ? {...product} : { id:'', name:'', subtitle:'', categoryId:'pc-1', productType:'PHYSICAL', fulfillmentType:'LOGISTICS', beginTime:'2026-11-01 00:00:00', endTime:'2026-12-31 23:59:59', perMemberLimit:0, enabled:false, art:'coffee', detailDesc:'', sort:1, skus:[] });
  const saveProduct = () => {
    if (!productEditing.name.trim()) return notify('请输入商品名称');
    const fulfillmentType=productEditing.productType==='COUPON'?'AUTO_COUPON':productEditing.productType==='BENEFIT'?'MANUAL':'LOGISTICS';
    const art=({ 'pc-1':'coffee','pc-2':'serum','pc-3':'headphone','pc-4':'chair','pc-5':'headphone' })[productEditing.categoryId]||'coffee';
    if (productEditing.id) {
      setProducts(products.map((item)=>item.id===productEditing.id?{...productEditing,fulfillmentType,art}:item));
    } else {
      setProducts([...products,{...productEditing,id:`pp-${Date.now()}`,fulfillmentType,art,skus:productEditing.skus||[]}]);
    }
    setProductEditing(null);
    notify('积分商品已保存');
  };
  const openSku = (product) => setSkuDialog({ product, rows:(product.skus||[]).map((item)=>({...item})), draft:{ name:'', pointsPrice:100, stock:10, imageUrl:'' }, editingId:'' });
  const addSku = () => {
    if (!skuDialog.draft.name.trim()) return notify('请输入 SKU 名称');
    const values={ name:skuDialog.draft.name.trim(),pointsPrice:Number(skuDialog.draft.pointsPrice)||0,stock:Number(skuDialog.draft.stock)||0,imageUrl:skuDialog.draft.imageUrl||'' };
    if (skuDialog.editingId) {
      setSkuDialog({...skuDialog,rows:skuDialog.rows.map((item)=>item.id===skuDialog.editingId?{...item,...values}:item),draft:{ name:'', pointsPrice:100, stock:10, imageUrl:'' },editingId:''});
      notify('SKU 已更新');
    } else {
      const next={ id:`sku-${Date.now()}`,...values,totalStock:values.stock,enabled:true };
      setSkuDialog({...skuDialog,rows:[...skuDialog.rows,next],draft:{ name:'', pointsPrice:100, stock:10, imageUrl:'' }});
      notify('SKU 已添加');
    }
  };
  const editSku = (sku) => setSkuDialog({...skuDialog,editingId:sku.id,draft:{ name:sku.name, pointsPrice:sku.pointsPrice, stock:sku.stock, imageUrl:sku.imageUrl||'' }});
  const setSkuImage = (file) => {
    if (!file) return;
    const imageUrl = URL.createObjectURL(file);
    setSkuDialog({...skuDialog,draft:{...skuDialog.draft,imageUrl}});
  };
  const removeSku = (sku) => setSkuDialog({...skuDialog,rows:skuDialog.rows.filter((item)=>item.id!==sku.id)});
  const saveSku = () => {
    setProducts(products.map((item)=>item.id===skuDialog.product.id?{...item,skus:skuDialog.rows}:item));
    setSkuDialog(null);
    notify('SKU 配置已保存');
  };
  const canShip = (order) => order.status==='1' && order.fulfillmentType==='LOGISTICS';
  const canFulfill = (order) => order.status==='1' && order.fulfillmentType==='MANUAL';
  const canCancel = (order) => order.status==='1';
  const openOrderAction = (order,type) => setOrderAction({ order,type,providerCode:'SF',trackingNo:'',remark:'' });
  const submitOrderAction = () => {
    const { order,type,providerCode,trackingNo,remark }=orderAction;
    if (type==='ship' && !trackingNo.trim()) return notify('请输入物流单号');
    if (type!=='ship' && !remark.trim()) return notify(type==='fulfill'?'请输入履约说明':'请输入取消原因');
    const status=type==='ship'?'2':type==='fulfill'?'3':'4';
    const logText=type==='ship'?`订单发货，物流单号 ${trackingNo}`:type==='fulfill'?`完成人工履约：${remark}`:`订单取消：${remark}`;
    setOrders(orders.map((item)=>item.id===order.id?{...item,status,providerCode:type==='ship'?providerCode:item.providerCode,trackingNo:type==='ship'?trackingNo:item.trackingNo,fulfillmentRemark:type==='fulfill'?remark:item.fulfillmentRemark,cancelReason:type==='cancel'?remark:item.cancelReason,logs:[...item.logs,{time:'2026-11-11 10:30:00',text:logText}]}:item));
    if (orderDetail?.id===order.id) setOrderDetail({...order,status,logs:[...order.logs,{time:'2026-11-11 10:30:00',text:logText}]});
    setOrderAction(null);
    notify(type==='ship'?'积分订单已发货':type==='fulfill'?'积分订单已完成履约':'积分订单已取消');
  };
  return (
    <section className="admin-page points-mall-admin-page" data-screen-label={`平台后台积分商城-${tab}`}>
      <div className="module-tabs">
        <button className={tab==='category'?'active':''} onClick={()=>setTab('category')}>商品分类</button>
        <button className={tab==='product'?'active':''} onClick={()=>setTab('product')}>积分商品</button>
        <button className={tab==='order'?'active':''} onClick={()=>setTab('order')}>兑换订单</button>
      </div>

      {tab==='category'&&<>
        <div className="points-mall-toolbar"><button className="business-primary" onClick={()=>openCategory()}><AdminIcon name="plus" size={17}/>新增分类</button></div>
        <div className="admin-table-frame">
          <table data-align-table="t12" className="admin-table points-category-table">
            <thead><tr><th>分类名称</th><th>父分类</th><th>图标</th><th className="numeric">排序</th><th>状态</th><th>创建时间</th><th className="actions">操作</th></tr></thead>
            <tbody>{categories.map((item)=><tr key={item.id}>
              <td><div className="cb"><b>{item.name}</b></div></td>
              <td><div className="cb">{categoryName(item.parentId)}</div></td>
              <td><div className="cb"><ProductArt type={item.art} className="points-category-art"/></div></td>
              <td className="numeric"><div className="cb">{item.sort}</div></td>
              <td><div className="cb"><StatusTicket tone={item.enabled?'success':'neutral'}>{item.enabled?'启用':'停用'}</StatusTicket></div></td>
              <td><div className="cb">{item.createTime}</div></td>
              <td className="actions"><div className="cb"><button className="table-link" onClick={()=>openCategory(item)}>编辑</button><button className="table-link danger-link" onClick={()=>removeCategory(item)}>删除</button></div></td>
            </tr>)}</tbody>
          </table>
        </div>
      </>}

      {tab==='product'&&<>
        <FilterBar
          onReset={()=>{const empty={ keyword:'',categoryId:'全部',status:'全部状态' };setProductFilters(empty);setAppliedProductFilters(empty);}}
          onSearch={()=>setAppliedProductFilters({...productFilters})}
          actions={<button className="business-primary" onClick={()=>openProduct()}><AdminIcon name="plus" size={17}/>新增商品</button>}
        >
          <label><span>商品名称</span><input value={productFilters.keyword} onChange={(e)=>setProductFilters({...productFilters,keyword:e.target.value})} placeholder="搜索积分商品"/></label>
          <label><span>分类</span><select value={productFilters.categoryId} onChange={(e)=>setProductFilters({...productFilters,categoryId:e.target.value})}><option>全部</option>{categories.filter((item)=>item.enabled).map((item)=><option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
          <label><span>状态</span><select value={productFilters.status} onChange={(e)=>setProductFilters({...productFilters,status:e.target.value})}><option>全部状态</option><option>已上架</option><option>已下架</option></select></label>
        </FilterBar>
        <div className="admin-table-frame">
          <table data-align-table="t13" className="admin-table points-product-table">
            <thead><tr><th>商品</th><th>分类</th><th>类型 / 履约</th><th>兑换时间</th><th className="numeric">每人限兑</th><th>状态</th><th className="actions">操作</th></tr></thead>
            <tbody>{filteredProducts.map((item)=><tr key={item.id}>
              <td><div className="cb"><div className="points-mall-product-cell"><ProductArt type={item.art}/><div><b>{item.name}</b><small>{item.subtitle||'暂无副标题'}</small></div></div></div></td>
              <td><div className="cb">{categoryName(item.categoryId)}</div></td>
              <td><div className="cb"><div>{productTypeText(item.productType)}</div><small className="cell-sub">{fulfillmentText(item.fulfillmentType)}</small></div></td>
              <td><div className="cb"><span>{item.beginTime.slice(0,10)}</span><small className="cell-sub">至 {item.endTime.slice(0,10)}</small></div></td>
              <td className="numeric"><div className="cb">{item.perMemberLimit>0?item.perMemberLimit:'不限'}</div></td>
              <td><div className="cb"><label className="points-switch"><input type="checkbox" checked={item.enabled} onChange={()=>toggleProduct(item)}/><i></i><span>{item.enabled?'已上架':'已下架'}</span></label></div></td>
              <td className="actions"><div className="cb"><button className="table-link" onClick={()=>openProduct(item)}>编辑</button><button className="table-link" onClick={()=>openSku(item)}>SKU管理</button><button className="table-link danger-link" onClick={()=>{setProducts(products.filter((product)=>product.id!==item.id));notify('积分商品已删除');}}>删除</button></div></td>
            </tr>)}</tbody>
          </table>
          {!filteredProducts.length&&<div className="admin-empty"><AdminIcon name="Goods" size={28}/><b>没有匹配的积分商品</b><span>调整筛选条件后重试</span></div>}
          <AdminPagination count={filteredProducts.length}/>
        </div>
      </>}

      {tab==='order'&&<>
        <FilterBar
          onReset={()=>{const empty={ orderNo:'',memberId:'',status:'全部状态' };setOrderFilters(empty);setAppliedOrderFilters(empty);}}
          onSearch={()=>setAppliedOrderFilters({...orderFilters})}
        >
          <label><span>订单编号</span><input value={orderFilters.orderNo} onChange={(e)=>setOrderFilters({...orderFilters,orderNo:e.target.value})} placeholder="兑换订单编号"/></label>
          <label><span>会员 ID</span><input value={orderFilters.memberId} onChange={(e)=>setOrderFilters({...orderFilters,memberId:e.target.value})} placeholder="会员 ID"/></label>
          <label><span>订单状态</span><select value={orderFilters.status} onChange={(e)=>setOrderFilters({...orderFilters,status:e.target.value})}><option>全部状态</option><option value="1">待履约</option><option value="2">已发货</option><option value="3">已完成</option><option value="4">已取消</option></select></label>
        </FilterBar>
        <div className="admin-table-frame">
          <table data-align-table="t14" className="admin-table points-order-table">
            <thead><tr><th>订单编号</th><th>会员ID</th><th>商品</th><th className="numeric">兑换</th><th>履约方式</th><th>状态</th><th>兑换时间</th><th className="actions">操作</th></tr></thead>
            <tbody>{filteredOrders.map((item)=><tr key={item.id}>
              <td className="mono"><div className="cb"><CopyableText value={item.orderNo} label="订单编号" notify={notify}/></div></td>
              <td className="mono"><div className="cb"><CopyableText value={item.memberId} label="会员 ID" notify={notify}/></div></td>
              <td><div className="cb"><div className="points-mall-product-cell compact"><ProductArt type={item.art}/><div><b>{item.productName}</b><small>{item.skuName}</small></div></div></div></td>
              <td className="numeric"><div className="cb"><div>{item.pointsPrice} 积分 × {item.quantity}</div><small className="cell-sub">合计 {item.totalPoints} 积分</small></div></td>
              <td><div className="cb">{fulfillmentText(item.fulfillmentType)}</div></td>
              <td><div className="cb"><StatusTicket tone={pointsOrderTone(item.status)}>{pointsOrderStatusText(item.status)}</StatusTicket></div></td>
              <td><div className="cb"><span>{item.createTime.slice(0,10)}</span><small className="cell-sub">{item.createTime.slice(11)}</small></div></td>
              <td className="actions"><div className="cb"><button className="table-link" onClick={()=>setOrderDetail(item)}>详情</button>{canShip(item)&&<button className="table-link" onClick={()=>openOrderAction(item,'ship')}>发货</button>}{canFulfill(item)&&<button className="table-link" onClick={()=>openOrderAction(item,'fulfill')}>完成履约</button>}{canCancel(item)&&<button className="table-link danger-link" onClick={()=>openOrderAction(item,'cancel')}>取消</button>}</div></td>
            </tr>)}</tbody>
          </table>
          {!filteredOrders.length&&<div className="admin-empty"><AdminIcon name="Tickets" size={28}/><b>没有匹配的兑换订单</b><span>调整筛选条件后重试</span></div>}
          <AdminPagination count={filteredOrders.length}/>
        </div>
      </>}

      {categoryEditing&&<div className="dialog-mask" onClick={()=>setCategoryEditing(null)}><section className="admin-dialog points-mall-dialog" onClick={(e)=>e.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">POINTS CATEGORY</span><h2>{categoryEditing.id?'编辑分类':'新增分类'}</h2></div><button onClick={()=>setCategoryEditing(null)}>×</button></div>
        <label className="dialog-field"><span>父分类</span><select value={categoryEditing.parentId} onChange={(e)=>setCategoryEditing({...categoryEditing,parentId:e.target.value==='0'?0:e.target.value})}><option value="0">一级分类</option>{categories.filter((item)=>item.id!==categoryEditing.id).map((item)=><option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
        <label className="dialog-field"><span>分类名称</span><input value={categoryEditing.name} onChange={(e)=>setCategoryEditing({...categoryEditing,name:e.target.value})} placeholder="请输入分类名称"/></label>
        <label className="dialog-field"><span>分类图标</span><select value={categoryEditing.art} onChange={(e)=>setCategoryEditing({...categoryEditing,art:e.target.value})}><option value="coffee">咖啡生活</option><option value="serum">美妆护理</option><option value="headphone">数码周边</option><option value="chair">居家好物</option></select></label>
        <label className="dialog-field"><span>排序</span><input type="number" min="0" value={categoryEditing.sort} onChange={(e)=>setCategoryEditing({...categoryEditing,sort:Number(e.target.value)})}/></label>
        <label className="points-rule-enabled"><span>启用分类</span><input type="checkbox" checked={categoryEditing.enabled} onChange={(e)=>setCategoryEditing({...categoryEditing,enabled:e.target.checked})}/></label>
        <div className="dialog-actions"><button onClick={()=>setCategoryEditing(null)}>取消</button><button className="primary" onClick={saveCategory}>保存</button></div>
      </section></div>}

      {productEditing&&<div className="dialog-mask" onClick={()=>setProductEditing(null)}><section className="admin-dialog points-mall-dialog points-product-dialog" onClick={(e)=>e.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">POINTS PRODUCT</span><h2>{productEditing.id?'编辑积分商品':'新增积分商品'}</h2></div><button onClick={()=>setProductEditing(null)}>×</button></div>
        <div className="points-form-grid">
          <label className="dialog-field"><span>商品分类</span><select value={productEditing.categoryId} onChange={(e)=>setProductEditing({...productEditing,categoryId:e.target.value})}>{categories.filter((item)=>item.enabled).map((item)=><option key={item.id} value={item.id}>{item.name}</option>)}</select></label>
          <label className="dialog-field"><span>商品名称</span><input value={productEditing.name} onChange={(e)=>setProductEditing({...productEditing,name:e.target.value})} maxLength="64" placeholder="请输入商品名称"/></label>
          <label className="dialog-field wide"><span>商品副标题</span><input value={productEditing.subtitle} onChange={(e)=>setProductEditing({...productEditing,subtitle:e.target.value})} maxLength="128" placeholder="选填"/></label>
          <label className="dialog-field wide"><span>商品主图</span><button type="button" className="upload-placeholder" onClick={()=>notify('商品主图上传入口')}><AdminIcon name="image" size={17}/>上传商品主图</button></label>
          <label className="dialog-field"><span>商品类型</span><select value={productEditing.productType} onChange={(e)=>setProductEditing({...productEditing,productType:e.target.value})}><option value="PHYSICAL">实物商品</option><option value="COUPON">优惠券</option><option value="BENEFIT">虚拟权益</option></select></label>
          <label className="dialog-field"><span>履约方式</span><input value={productEditing.productType==='COUPON'?'自动发券':productEditing.productType==='BENEFIT'?'人工履约':'物流发货'} disabled/></label>
          {productEditing.productType==='COUPON'&&<label className="dialog-field wide"><span>兑换优惠券</span><select defaultValue="coupon-1"><option value="coupon-1">全店 20 元券</option><option value="coupon-2">满 100 减 20 券</option></select></label>}
          <label className="dialog-field"><span>开始时间</span><input type="date" value={productEditing.beginTime.slice(0,10)} onChange={(e)=>setProductEditing({...productEditing,beginTime:`${e.target.value} 00:00:00`})}/></label>
          <label className="dialog-field"><span>结束时间</span><input type="date" value={productEditing.endTime.slice(0,10)} onChange={(e)=>setProductEditing({...productEditing,endTime:`${e.target.value} 23:59:59`})}/></label>
          <label className="dialog-field"><span>每人限兑</span><input type="number" min="0" value={productEditing.perMemberLimit} onChange={(e)=>setProductEditing({...productEditing,perMemberLimit:Number(e.target.value)})}/></label>
          <label className="dialog-field"><span>排序</span><input type="number" min="0" value={productEditing.sort} onChange={(e)=>setProductEditing({...productEditing,sort:Number(e.target.value)})}/></label>
          <label className="dialog-field wide"><span>商品详情</span><textarea rows="3" value={productEditing.detailDesc} onChange={(e)=>setProductEditing({...productEditing,detailDesc:e.target.value})}/></label>
          <label className="points-rule-enabled wide"><span>上架商品</span><input type="checkbox" checked={productEditing.enabled} onChange={(e)=>setProductEditing({...productEditing,enabled:e.target.checked})}/></label>
        </div>
        <div className="dialog-actions"><button onClick={()=>setProductEditing(null)}>取消</button><button className="primary" onClick={saveProduct}>保存</button></div>
      </section></div>}

      {skuDialog&&<div className="dialog-mask" onClick={()=>setSkuDialog(null)}><section className="admin-dialog points-mall-dialog points-sku-dialog" onClick={(e)=>e.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">POINTS SKU</span><h2>{skuDialog.product.name} · SKU 管理</h2></div><button onClick={()=>setSkuDialog(null)}>×</button></div>
        <div className="points-sku-add">
          <label className="points-sku-field"><span>SKU 名称</span><input value={skuDialog.draft.name} onChange={(e)=>setSkuDialog({...skuDialog,draft:{...skuDialog.draft,name:e.target.value}})} placeholder="请输入 SKU 名称"/></label>
          <label className="points-sku-field"><span>积分价格</span><input type="number" min="1" value={skuDialog.draft.pointsPrice} onChange={(e)=>setSkuDialog({...skuDialog,draft:{...skuDialog.draft,pointsPrice:e.target.value}})} placeholder="请输入积分价格"/></label>
          <label className="points-sku-field"><span>可用库存</span><input type="number" min="0" value={skuDialog.draft.stock} onChange={(e)=>setSkuDialog({...skuDialog,draft:{...skuDialog.draft,stock:e.target.value}})} placeholder="请输入库存"/></label>
          <div className="points-sku-field points-sku-image-field">
            <span>SKU 图片 <small>可选，为空使用商品主图</small></span>
            <div className="points-sku-image-control">
              <span className="points-sku-image-preview">
                {skuDialog.draft.imageUrl
                  ? <img src={skuDialog.draft.imageUrl} alt="SKU 图片"/>
                  : <ProductArt type={skuDialog.product.art} bare/>}
              </span>
              <label className="points-sku-image-upload">选择图片<input type="file" accept="image/*" onChange={(e)=>{setSkuImage(e.target.files?.[0]);e.target.value='';}}/></label>
              {skuDialog.draft.imageUrl&&<button type="button" className="table-link" onClick={()=>setSkuDialog({...skuDialog,draft:{...skuDialog.draft,imageUrl:''}})}>清除</button>}
            </div>
          </div>
          <button className="business-primary points-sku-add-button" onClick={addSku}>{skuDialog.editingId?'保存 SKU':'新增 SKU'}</button>
        </div>
        <table data-align-table="t15" className="mini-data-table"><thead><tr><th>SKU 名称</th><th>图片</th><th>积分价格</th><th>可用库存</th><th>累计库存</th><th>状态</th><th className="actions">操作</th></tr></thead><tbody>{skuDialog.rows.map((sku)=><tr key={sku.id}><td><div className="cb">{sku.name}</div></td><td><div className="cb"><span className="points-sku-table-image">{sku.imageUrl?<img src={sku.imageUrl} alt={sku.name}/>:<ProductArt type={skuDialog.product.art} bare/>}</span></div></td><td><div className="cb">{sku.pointsPrice}</div></td><td><div className="cb">{sku.stock}</div></td><td><div className="cb">{sku.totalStock}</div></td><td><div className="cb"><StatusTicket tone={sku.enabled?'success':'neutral'}>{sku.enabled?'启用':'停用'}</StatusTicket></div></td><td className="actions"><div className="cb"><button className="table-link" onClick={()=>editSku(sku)}>编辑</button><button className="table-link danger-link" onClick={()=>removeSku(sku)}>删除</button></div></td></tr>)}</tbody></table>
        <div className="dialog-actions"><button onClick={()=>setSkuDialog(null)}>取消</button><button className="primary" onClick={saveSku}>保存 SKU</button></div>
      </section></div>}

      {orderDetail&&<div className="drawer-mask" onClick={()=>setOrderDetail(null)}><aside className="admin-drawer points-order-detail" onClick={(e)=>e.stopPropagation()}>
        <div className="drawer-head"><div><span className="drawer-kicker">POINTS ORDER</span><h2>兑换订单详情</h2></div><button onClick={()=>setOrderDetail(null)}>×</button></div>
        <div className="points-order-detail-head"><div><span>订单编号</span><b className="mono">{orderDetail.orderNo}</b></div><StatusTicket tone={pointsOrderTone(orderDetail.status)}>{pointsOrderStatusText(orderDetail.status)}</StatusTicket></div>
        <div className="points-mall-product-cell"><ProductArt type={orderDetail.art}/><div><b>{orderDetail.productName}</b><small>{orderDetail.skuName} × {orderDetail.quantity}</small><small>合计 {orderDetail.totalPoints} 积分</small></div></div>
        <div className="description-grid">
          <div><span>会员 ID</span><b className="mono">{orderDetail.memberId}</b></div>
          <div><span>履约方式</span><b>{fulfillmentText(orderDetail.fulfillmentType)}</b></div>
          <div><span>兑换时间</span><b>{orderDetail.createTime}</b></div>
          <div><span>收货人</span><b>{orderDetail.receiverName} {orderDetail.receiverContact}</b></div>
          {orderDetail.receiverAddress&&<div className="wide"><span>收货地址</span><b>{orderDetail.receiverAddress}</b></div>}
          {orderDetail.trackingNo&&<div className="wide"><span>物流信息</span><b>{orderDetail.providerCode} / {orderDetail.trackingNo}</b></div>}
          {orderDetail.fulfillmentRemark&&<div className="wide"><span>履约说明</span><b>{orderDetail.fulfillmentRemark}</b></div>}
          {orderDetail.cancelReason&&<div className="wide"><span>取消原因</span><b>{orderDetail.cancelReason}</b></div>}
        </div>
        <div className="drawer-section"><h3>状态记录</h3><div className="points-order-logs">{orderDetail.logs.map((log,index)=><div key={index}><i></i><span><b>{log.text}</b><small>{log.time}</small></span></div>)}</div></div>
        <div className="drawer-actions"><button onClick={()=>setOrderDetail(null)}>关闭</button>{canShip(orderDetail)&&<button className="primary" onClick={()=>{setOrderAction({order:orderDetail,type:'ship',providerCode:'SF',trackingNo:'',remark:''});setOrderDetail(null);}}>发货</button>}{canFulfill(orderDetail)&&<button className="primary" onClick={()=>{setOrderAction({order:orderDetail,type:'fulfill',providerCode:'SF',trackingNo:'',remark:''});setOrderDetail(null);}}>完成履约</button>}{canCancel(orderDetail)&&<button onClick={()=>{setOrderAction({order:orderDetail,type:'cancel',providerCode:'SF',trackingNo:'',remark:''});setOrderDetail(null);}}>取消订单</button>}</div>
      </aside></div>}

      {orderAction&&<div className="dialog-mask" onClick={()=>setOrderAction(null)}><section className="admin-dialog points-mall-dialog" onClick={(e)=>e.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">ORDER ACTION</span><h2>{orderAction.type==='ship'?'订单发货':orderAction.type==='fulfill'?'完成人工履约':'取消积分订单'}</h2></div><button onClick={()=>setOrderAction(null)}>×</button></div>
        {orderAction.type==='ship'?<>
          <label className="dialog-field"><span>物流公司</span><select value={orderAction.providerCode} onChange={(e)=>setOrderAction({...orderAction,providerCode:e.target.value})}><option value="SF">顺丰速运</option><option value="ZTO">中通快递</option><option value="YTO">圆通速递</option></select></label>
          <label className="dialog-field"><span>物流单号</span><input value={orderAction.trackingNo} onChange={(e)=>setOrderAction({...orderAction,trackingNo:e.target.value})} placeholder="请输入物流单号"/></label>
        </>:<label className="dialog-field"><span>{orderAction.type==='fulfill'?'履约说明':'取消原因'}</span><textarea rows="4" value={orderAction.remark} onChange={(e)=>setOrderAction({...orderAction,remark:e.target.value})} placeholder="请填写说明"/></label>}
        <div className="dialog-actions"><button onClick={()=>setOrderAction(null)}>取消</button><button className="primary" onClick={submitOrderAction}>确认</button></div>
      </section></div>}
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
  const [menu,setMenu] = useState(null);
  const [refundOrder,setRefundOrder] = useState(null);
  const filtered = orders.filter((item) => (status === '全部状态' || item.status === status) && `${item.orderNo}${item.buyer}`.includes(keyword));
  const submitShip = () => {
    if (!tracking.trim()) return notify('请输入物流单号');
    const providerName = MOCKS.providers.find((item) => item.code === provider)?.name;
    setOrders(orders.map((item) => item.id === ship.id ? { ...item, status:'待收货', provider:providerName, trackingNo:tracking } : item));
    setShip(null); setTracking(''); notify('订单已发货');
  };
  return (
    <section className="admin-page" data-screen-label="后台订单管理">
      <FilterBar onReset={() => {setKeyword('');setStatus('全部状态');}} onSearch={() => notify(`已筛选 ${filtered.length} 笔订单`)} actions={<button onClick={()=>notify('订单导出任务已创建')}><AdminIcon name="filter" size={17}/>导出订单</button>}>
        <label><span>订单编号</span><input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="订单编号或买家"/></label>
        <label><span>订单状态</span><select value={status} onChange={(e) => setStatus(e.target.value)}>{['全部状态','待发货','待收货','待成团','退款中'].map((item) => <option key={item}>{item}</option>)}</select></label>
        <label><span>订单类型</span><select defaultValue="全部类型"><option>全部类型</option><option>普通订单</option><option>秒杀订单</option><option>拼团订单</option></select></label>
        <label><span>下单时间</span><input type="date" defaultValue="2026-10-27"/></label>
      </FilterBar>
      <div className="admin-table-frame">
        <table data-align-table="t16" className="admin-table orders-table"><thead><tr><th>订单编号 / 时间</th><th>买家</th><th>订单类型</th><th className="numeric">商品总额</th><th className="numeric">优惠</th><th className="numeric">应付</th><th>状态</th><th className="actions">操作</th></tr></thead><tbody>{filtered.map((order) => <tr key={order.id}><td><div className="cb"><b className="mono order-number"><CopyableText value={order.orderNo} label="订单编号" notify={notify}/></b><small className="cell-sub">{order.createdAt}</small></div></td><td><div className="cb"><b><CopyableText value={order.buyer} label="买家名称" notify={notify}/></b><small className="cell-sub mono"><CopyableText value={order.mobile} label="买家手机号" notify={notify}/></small></div></td><td><div className="cb"><StatusTicket tone={order.type === '秒杀订单' ? 'danger' : order.type === '拼团订单' ? 'success' : 'neutral'} small>{order.type}</StatusTicket></div></td><td className="numeric"><div className="cb">¥{order.amount.toFixed(2)}</div></td><td className="numeric saving"><div className="cb">-¥{order.discount.toFixed(2)}</div></td><td className="numeric amount-strong"><div className="cb">¥{order.finalAmount.toFixed(2)}</div></td><td><div className="cb"><StatusTicket tone={statusTone(order.status)}>{order.status}</StatusTicket></div></td><td className="actions"><div className="cb"><button className="table-link" onClick={() => setDetail(order)}>详情</button>{order.status === '待发货' && <button className="table-primary" onClick={() => setShip(order)}>发货</button>}<RowActionMenu open={menu===order.id} onToggle={()=>setMenu(menu===order.id?null:order.id)}><button onClick={()=>{setDetail(order);setMenu(null);}}>查看完整详情</button>{order.status==='待发货'&&<button onClick={()=>{setShip(order);setMenu(null);}}>订单发货</button>}<button onClick={()=>{setRefundOrder(order);setMenu(null);}}>退款/售后记录</button></RowActionMenu></div></td></tr>)}</tbody></table>
        <AdminPagination count={filtered.length}/>
      </div>
      {detail && <div className="drawer-mask" onClick={() => setDetail(null)}><aside className="admin-drawer order-drawer" onClick={(e) => e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">ORDER DETAIL</span><h2>订单详情</h2></div><button onClick={() => setDetail(null)}>×</button></div><div className="order-detail-heading"><div><span>订单编号</span><b className="mono">{detail.orderNo}</b></div><StatusTicket tone={statusTone(detail.status)}>{detail.status}</StatusTicket></div><div className="drawer-section"><h3>商品信息</h3><div className="drawer-product"><ProductArt type={detail.product.includes('耳机') ? 'headphone' : detail.product.includes('精华') ? 'serum' : detail.product.includes('咖啡') ? 'coffee' : 'chair'}/><div><b>{detail.product}</b><span>数量 × {detail.quantity}</span><strong>¥{detail.finalAmount.toFixed(2)}</strong></div></div></div><div className="drawer-section"><h3>收货信息</h3><div className="description-grid"><div><span>收货人</span><b>{detail.buyer} {detail.mobile}</b></div><div className="wide"><span>收货地址</span><b>{detail.address}</b></div></div></div><div className="drawer-section"><h3>金额信息</h3><div className="order-money"><span>商品总额<b>¥{detail.amount.toFixed(2)}</b></span><span>优惠金额<b>-¥{detail.discount.toFixed(2)}</b></span><span className="total">优惠后应付<b>¥{detail.finalAmount.toFixed(2)}</b></span></div></div></aside></div>}
      {ship && <div className="dialog-mask" onClick={() => setShip(null)}><section className="admin-dialog" onClick={(e) => e.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">FULFILLMENT</span><h2>订单发货</h2></div><button onClick={() => setShip(null)}>×</button></div><div className="shipping-order"><span>订单编号</span><b className="mono">{ship.orderNo}</b></div><label className="dialog-field"><span>物流公司</span><select value={provider} onChange={(e) => setProvider(e.target.value)}>{MOCKS.providers.map((item) => <option key={item.code} value={item.code}>{item.name} · {item.code}</option>)}</select></label><label className="dialog-field"><span>物流单号</span><input value={tracking} onChange={(e) => setTracking(e.target.value)} placeholder="请输入物流单号"/></label><div className="dialog-hint"><AdminIcon name="truck" size={17}/><span>发货后买家将看到物流公司与单号。</span></div><div className="dialog-actions"><button onClick={() => setShip(null)}>取消</button><button className="primary" onClick={submitShip}>确认发货</button></div></section></div>}
      {refundOrder&&<div className="dialog-mask" onClick={()=>setRefundOrder(null)}><section className="admin-dialog refund-record-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">AFTER SALES</span><h2>退款/售后记录</h2></div><button onClick={()=>setRefundOrder(null)}>×</button></div><div className="shipping-order"><span>订单编号</span><b className="mono"><CopyableText value={refundOrder.orderNo} label="订单编号" notify={notify}/></b></div><div className="refund-record-card"><div><span>当前状态</span><StatusTicket tone={refundOrder.status==='退款中'?'danger':'neutral'}>{refundOrder.status==='退款中'?'退款处理中':'暂无进行中的售后'}</StatusTicket></div><p>{refundOrder.status==='退款中'?'买家已提交退款申请，等待商家处理。':'该订单目前没有退款或售后申请。'}</p><small>最近更新：2026-11-02 09:36</small></div><div className="dialog-actions"><button onClick={()=>setRefundOrder(null)}>关闭</button>{refundOrder.status==='退款中'&&<button className="primary" onClick={()=>{setRefundOrder(null);notify('已进入退款处理流程');}}>处理退款</button>}</div></section></div>}
    </section>
  );
}

function MerchantProducts({ notify }) {
  const [keyword, setKeyword] = useState('');
  const [products, setProducts] = useState(MOCKS.merchantProducts);
  const [edit, setEdit] = useState(null);
  const [menu,setMenu] = useState(null);
  const filtered = products.filter((item) => item.name.includes(keyword));
  const toggle = (product) => {
    const next = product.status === '销售中' ? '已下架' : product.stock === 0 ? '已售罄' : '销售中';
    setProducts(products.map((item) => item.id === product.id ? { ...item, status:next } : item));
    notify(`商品已${next === '销售中' ? '上架' : '下架'}`);
  };
  return (
    <section className="admin-page" data-screen-label="商家后台商品管理">
      <FilterBar onReset={() => setKeyword('')} onSearch={() => notify(`找到 ${filtered.length} 个商品`)} actions={<><button onClick={()=>notify('请选择商品后执行批量操作')}><AdminIcon name="filter" size={17}/>批量操作</button><button className="business-primary" onClick={() => setEdit({id:null,name:'',category:'数码影音',skuCount:1,price:'',stock:0,art:'headphone'})}><AdminIcon name="plus" size={17}/>新增商品</button></>}><label><span>商品名称</span><input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="输入商品名称"/></label><label><span>商品分类</span><select defaultValue="全部分类"><option>全部分类</option><option>数码影音</option><option>家具家居</option><option>美容护肤</option></select></label><label><span>销售状态</span><select defaultValue="全部状态"><option>全部状态</option><option>销售中</option><option>已下架</option><option>已售罄</option></select></label></FilterBar>
      <div className="admin-table-frame"><table data-align-table="t17" className="admin-table product-admin-table"><thead><tr><th>商品</th><th>分类</th><th className="numeric">售价</th><th className="numeric">库存</th><th className="numeric">销量</th><th>状态</th><th className="actions">操作</th></tr></thead><tbody>{filtered.map((product) => <tr key={product.id}><td><div className="cb"><div className="admin-product-cell"><ProductArt type={product.art}/><div><b><CopyableText value={product.name} label="商品名称" notify={notify}/></b><small>{product.skuCount} 个 SKU · ID {product.id}</small></div></div></div></td><td><div className="cb"><CopyableText value={product.category} label="分类名称" notify={notify}/></div></td><td className="numeric amount-strong"><div className="cb">¥{product.price}</div></td><td className={`numeric ${product.stock <= 12 ? 'low-stock' : ''}`}><div className="cb">{product.stock}</div></td><td className="numeric"><div className="cb">{product.sales.toLocaleString()}</div></td><td><div className="cb"><StatusTicket tone={product.status === '销售中' ? 'success' : product.status === '已售罄' ? 'danger' : 'neutral'}>{product.status}</StatusTicket></div></td><td className="actions"><div className="cb"><button className="table-link" onClick={() => setEdit(product)}>编辑</button><button className="table-link" onClick={() => toggle(product)}>{product.status === '销售中' ? '下架' : '上架'}</button><RowActionMenu open={menu===product.id} onToggle={()=>setMenu(menu===product.id?null:product.id)}><button onClick={()=>{setEdit(product);setMenu(null);}}>编辑商品</button><button onClick={()=>{setMenu(null);notify(`已打开 ${product.name} 的 SKU 管理`);}}>SKU 管理</button><button onClick={()=>{setMenu(null);notify('图片管理抽屉已打开');}}>图片管理</button><button onClick={()=>{setMenu(null);notify('已复制为新的商品草稿');}}>复制商品</button></RowActionMenu></div></td></tr>)}</tbody></table><AdminPagination count={filtered.length}/></div>
      {edit && <div className="drawer-mask" onClick={() => setEdit(null)}><aside className="admin-drawer product-edit-drawer" onClick={(e) => e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">PRODUCT EDITOR</span><h2>{edit.id?'编辑商品':'新增商品'}</h2></div><button onClick={() => setEdit(null)}>×</button></div><div className="edit-product-preview"><ProductArt type={edit.art}/><div><b>{edit.name||'待填写商品名称'}</b><span>{edit.category} · {edit.skuCount} 个 SKU</span></div><StatusTicket tone={edit.id?'success':'neutral'}>{edit.id?'已保存':'新建中'}</StatusTicket></div><section className="product-editor-section"><h3>基础资料</h3><div className="edit-form-grid"><label><span>商品名称</span><input defaultValue={edit.name} placeholder="请输入商品名称"/></label><label><span>店铺分类</span><select defaultValue={edit.category}><option>数码影音</option><option>家具家居</option><option>美容护肤</option></select></label><label className="wide"><span>商品卖点</span><textarea defaultValue={edit.id?'轻量设计，稳定连接，适合通勤与居家使用。':''} placeholder="概括商品核心卖点"></textarea></label></div></section><section className="product-editor-section"><h3>商品图片</h3><div className="product-media-row"><button className="product-main-image"><ProductArt type={edit.art}/><span>更换主图</span></button><button className="product-media-add" onClick={()=>notify('请选择轮播图片')}><AdminIcon name="plus" size={20}/><span>添加轮播图</span></button><small>已配置 3 张轮播图，支持拖动排序</small></div></section><section className="product-editor-section"><h3>规格与 SKU</h3><div className="spec-chip-row"><button className="active">颜色</button><button className="active">版本</button><button onClick={()=>notify('规格选择器已打开')}><AdminIcon name="plus" size={14}/>选择规格</button></div><table data-align-table="t18" className="sku-editor-table"><thead><tr><th>规格组合</th><th>销售价</th><th>库存</th><th>SKU 编码</th></tr></thead><tbody><tr><td><div className="cb">雾银 / 标准版</div></td><td><div className="cb"><input defaultValue="299.00"/></div></td><td><div className="cb"><input defaultValue="18"/></div></td><td><div className="cb"><input defaultValue="HEAD-S-01"/></div></td></tr><tr><td><div className="cb">曜石黑 / 长续航</div></td><td><div className="cb"><input defaultValue="369.00"/></div></td><td><div className="cb"><input defaultValue="12"/></div></td><td><div className="cb"><input defaultValue="HEAD-B-02"/></div></td></tr></tbody></table></section><section className="product-editor-section"><h3>商品详情</h3><div className="richtext-placeholder"><div><b>B</b><span>标题</span><span>图片</span><span>链接</span></div><p>输入商品详情说明，可插入图片并进行基础排版。</p></div></section><div className="drawer-actions sticky-actions"><button onClick={() => setEdit(null)}>取消</button><button className="primary" onClick={() => {setEdit(null);notify(edit.id?'商品信息已保存':'商品已创建');}}>{edit.id?'保存修改':'创建商品'}</button></div></aside></div>}
    </section>
  );
}

function AdminOverview({ surface, navigate }) {
  const merchant = surface === 'merchant';
  const metrics = merchant
    ? [['今日支付订单','38','较昨日 +12%'],['今日销售额','¥12,680.00','较昨日 +8.4%'],['待发货','12','需及时处理'],['退款待处理','3','申请中或处理中']]
    : [['今日支付订单','126','全平台'],['今日交易额','¥58,240.00','较昨日 +9.7%'],['活跃店铺','18','共 24 家'],['退款待处理','7','跨 5 家店铺']];
  const tasks = merchant
    ? [['待发货订单','12','merchant/orders'],['低库存商品','4','merchant/products'],['待回复评价','6','merchant/reviews'],['拼团报名待审','2','merchant/groupbuy']]
    : [['待启用商家','3','platform/merchants'],['秒杀报名待审','2','platform/seckill?tab=报名审核'],['已隐藏评价','8','platform/reviews'],['意见反馈待处理','5','platform/feedback']];
  return <section className="admin-page overview-page" data-screen-label={`${merchant?'商家':'平台'}经营概览`}><div className="overview-metrics">{metrics.map((item,index)=><article key={item[0]}><span>{item[0]}</span><b>{item[1]}</b><small className={index>1?'attention':''}>{item[2]}</small></article>)}</div><div className="overview-grid"><section className="overview-tasks"><div className="table-toolbar"><div><span className="shelf-mark"></span><b>待办事项</b></div><StatusTicket tone="warning">今日</StatusTicket></div>{tasks.map((task)=><button key={task[0]} onClick={()=>navigate(task[2])}><span><b>{task[0]}</b><small>点击进入对应业务列表</small></span><strong>{task[1]}</strong><AdminIcon name="chevron" size={16}/></button>)}</section><section className="trend-panel"><div className="table-toolbar"><div><span className="shelf-mark"></span><b>近 7 日交易趋势</b></div><span className="mini-legend"><i></i>支付金额</span></div><div className="fake-chart">{[42,55,48,68,61,78,72].map((value,index)=><span key={index} style={{height:`${value}%`}}><i>{['21','22','23','24','25','26','27'][index]}</i></span>)}</div></section></div></section>;
}

function moduleDefinition(surface, page) {
  const modules = {
    platform: {
      merchants: { title:'商家管理', desc:'维护商家登录账号和关联店铺', action:'新增商家', columns:['商家账号','店铺数量','账号状态','创建时间'], rows:[['mch001','2 家','启用','2026-10-18 09:20'],['mch002','1 家','启用','2026-10-20 14:32'],['mch003','0 家','停用','2026-10-24 11:05']] },
      shops: { title:'店铺管理', desc:'管理平台店铺、佣金、结算周期和平台状态', action:'新增店铺', columns:['店铺编号','店铺名称','所属商家','平台佣金','结算周期','平台状态','创建时间'], rows:[['SHAN-YE','山野生活店','mch001','5%','T+7','正常营业','2026-10-18'],['YEAH-DIGITAL','Yeah 数码生活馆','mch002','4%','T+7','正常营业','2026-10-20'],['WHITE-TEA','白茶生活馆','mch003','6%','T+15','暂停营业','2026-10-24']] },
      categories: { title:'平台分类', desc:'维护 App 分类页使用的平台三级分类树', action:'新增平台分类', columns:['分类图片','平台分类名称','分类编码','层级','排序','备注','更新时间'], rows:[['数','数码家电','digital','1','1','平台一级分类','2026-10-28 09:12'],['耳','耳机音响','audio','2','2','数码家电子分类','2026-10-28 09:16'],['机','头戴耳机','headphone','3','1','三级分类','2026-10-28 09:20']] },
      reviews: { title:'评价管理', desc:'查看、隐藏和恢复商品评价及回复', tabs:['全部评价','已隐藏'], columns:['商品','评价用户','评分','评价内容','状态'], rows:[['云感降噪头戴耳机','林可','5 星','佩戴舒适，降噪效果很好','公开'],['白茶屏障修护精华','陈一禾','5 星','敏感肌使用没有刺激','公开'],['山野拼配挂耳咖啡','周予安','3 星','包装有轻微压痕','已隐藏']] },
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
    'platform:reviews:已隐藏': { columns:['商品','评价用户','评分','评价内容','状态'], rows:[['山野拼配挂耳咖啡','周予安','3 星','包装有轻微压痕','已隐藏']] },
    'platform:points:积分流水': { columns:['会员ID','会员','业务类型','变动积分','变动前','变动后','业务单号','创建时间'], rows:[['211386217001','林可','订单完成','+399','881','1,280','YM202611020018','2026-11-01 18:42'],['211386217001','林可','完成评价','+10','871','881','YM202611010086','2026-10-28 09:16'],['211386217004','陈一禾','积分兑换','-300','3,800','3,500','PX202610240012','2026-10-24 16:30'],['211386217002','周予安','平台调整','+500','3,000','3,500','ADJ202610200003','2026-10-20 10:12']] },
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
  return ['启用','禁用','停用','正常','营业','待审核','审核通过','已通过','已拒绝','已展示','公开','已隐藏','待发货','待履约','已完成','已取消','活动中','待开始','进行中','已结束','销售中','监控中','待成团','已成团','拼团失败','处理中','待处理','已解决','默认','统计中','未使用','已使用','已过期','已下架','已上架','上架','已领完','待回复','已回复','低库存','已售罄','待结算','已结算'].some((status)=>String(value).includes(status));
}

function semanticStatusTone(value) {
  const text=String(value);
  if(['驳回','拒绝','退款','失败','低库存','已售罄'].some((item)=>text.includes(item))) return 'danger';
  if(['待','处理中','进行中','活动中','监控中','统计中','待结算'].some((item)=>text.includes(item))) return 'warning';
  if(['停用','禁用','下架','关闭','取消','过期','已使用'].some((item)=>text.includes(item))) return 'neutral';
  return 'success';
}

function isCopyableAdminColumn(column) {
  const text=String(column||'');
  if(/状态|时间|金额|价格|库存|数量|排序|评分|比例|周期|备注|内容|类型|操作|层级|范围/.test(text)) return false;
  return /编号|单号|编码|账号|名称|SKU|商品|店铺|商家|会员|优惠券|活动|场次|联系人|联系电话|手机号|参数键|收款账号/.test(text);
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

function moduleFilterDefinition(surface,page,tab) {
  const key=`${surface}:${page}:${tab}`;
  const definitions={
    'platform:merchants:':[{key:'keyword',label:'商家账号',placeholder:'账号、昵称或 ID'},{key:'status',label:'账号状态',options:['全部状态','启用','停用']}],
    'platform:shops:':[{key:'merchant',label:'所属商家',placeholder:'商家账号或 ID'},{key:'keyword',label:'店铺名称',placeholder:'请输入店铺名称'},{key:'status',label:'平台状态',options:['全部状态','正常营业','暂停营业']}],
    'platform:categories:':[{key:'keyword',label:'分类名称',placeholder:'请输入分类名称'}],
    'platform:reviews:全部评价':[{key:'keyword',label:'商品或订单',placeholder:'SPU ID、商品名称或订单号'},{key:'rating',label:'评价等级',options:['全部等级','好评','中评','差评']},{key:'reply',label:'回复状态',options:['全部状态','已回复','未回复']},{key:'visible',label:'公开状态',options:['全部状态','公开','已隐藏']}],
    'platform:reviews:已隐藏':[{key:'keyword',label:'商品或订单',placeholder:'SPU ID、商品名称或订单号'},{key:'rating',label:'评价等级',options:['全部等级','好评','中评','差评']},{key:'reply',label:'回复状态',options:['全部状态','已回复','未回复']}],
    'platform:points:积分流水':[{key:'keyword',label:'会员 ID',placeholder:'请输入会员 ID'},{key:'biz',label:'业务类型',options:['全部类型','订单完成','完成评价','平台调整']}],
    'platform:points-mall:积分商品':[{key:'keyword',label:'商品名称',placeholder:'请输入积分商品名称'},{key:'type',label:'商品类型',options:['全部类型','实物商品','优惠券','虚拟权益']},{key:'status',label:'状态',options:['全部状态','已上架','已下架']}],
    'platform:points-mall:兑换订单':[{key:'keyword',label:'订单或会员',placeholder:'兑换单号、会员 ID'},{key:'status',label:'订单状态',options:['全部状态','待履约','待发货','已完成','已取消']}],
    'platform:feedback:全部':[{key:'status',label:'处理状态',options:['全部状态','待处理','处理中','已解决']}],
    'merchant:categories:':[{key:'keyword',label:'分类名称',placeholder:'请输入分类名称'}],
    'merchant:sku:':[{key:'keyword',label:'SKU 或商品',placeholder:'SKU ID、商品名称'},{key:'status',label:'库存状态',options:['全部状态','库存正常','低库存','已售罄']}],
    'merchant:specs:规格定义':[{key:'keyword',label:'规格名称',placeholder:'请输入规格名称'}],
    'merchant:reviews:全部评价':[{key:'keyword',label:'商品或用户',placeholder:'商品名称、用户昵称'},{key:'rating',label:'评价等级',options:['全部等级','好评','中评','差评']},{key:'reply',label:'回复状态',options:['全部状态','已回复','待回复']}],
    'merchant:coupons:优惠券':[{key:'keyword',label:'优惠券名称',placeholder:'请输入优惠券名称'},{key:'type',label:'优惠券类型',options:['全部类型','满减券','折扣券','无门槛券']},{key:'status',label:'状态',options:['全部状态','启用','禁用']}],
    'merchant:coupons:领取记录':[{key:'keyword',label:'优惠券',placeholder:'优惠券名称'},{key:'type',label:'优惠券类型',options:['全部类型','满减券','折扣券','无门槛券']},{key:'range',label:'使用范围',options:['全部范围','店铺通用','指定商品','指定分类']}],
    'merchant:coupons:使用记录':[{key:'keyword',label:'优惠券或用户',placeholder:'优惠券、用户或订单号'},{key:'status',label:'使用状态',options:['全部状态','未使用','已使用','已过期']}],
    'merchant:shipping:物流公司':[{key:'keyword',label:'物流公司',placeholder:'公司名称或编码'},{key:'status',label:'状态',options:['全部状态','启用','停用']}],
    'merchant:shipping:发货地址':[{key:'keyword',label:'地址或联系人',placeholder:'地址名称、联系人'}]
  };
  if(definitions[key]) return definitions[key];
  if(page==='seckill') return [{key:'keyword',label:tab.includes('审核')||tab.includes('报名')?'活动或商品':'活动名称',placeholder:`搜索${tab}`},{key:'status',label:'状态',options:['全部状态','待开始','进行中','待审核','审核通过','已结束']}];
  if(page==='groupbuy') return [{key:'keyword',label:tab.includes('订单')?'订单或活动':'活动或商品',placeholder:`搜索${tab}`},{key:'status',label:'状态',options:['全部状态','待审核','审核通过','活动中','待成团','已成团']}];
  if(page==='content'||page==='banner') return [{key:'keyword',label:'名称或标题',placeholder:`搜索${tab||'轮播图'}`},{key:'status',label:'状态',options:['全部状态','启用','停用']}];
  return [{key:'keyword',label:'关键词',placeholder:`搜索${tab||page}`},{key:'status',label:'状态',options:['全部状态','启用','停用']}];
}

function moduleFormDefinition(surface,page,tab) {
  const key=`${surface}:${page}:${tab}`;
  const definitions={
    'platform:merchants:':[{label:'商家账号',placeholder:'请输入商家登录账号'},{label:'登录密码',type:'password',placeholder:'请输入初始登录密码'}],
    'platform:shops:':[{label:'所属商家',type:'select',options:['mch001','mch002','mch003']},{label:'店铺名称',placeholder:'请输入店铺名称'},{label:'店铺编号',placeholder:'字母、数字与短横线'},{label:'平台佣金比例',placeholder:'例如 5%'},{label:'结算周期',type:'select',options:['T+7','T+15','T+30']},{label:'平台状态',type:'select',options:['正常营业','暂停营业']}],
    'platform:categories:':[{label:'上级分类',type:'select',options:['顶级分类','数码家电','耳机音响']},{label:'分类名称',placeholder:'请输入分类名称'},{label:'分类编码',placeholder:'请输入分类编码'},{label:'分类图片',type:'upload'},{label:'排序',type:'number',placeholder:'请输入排序值'},{label:'备注',type:'textarea',wide:true,placeholder:'选填分类说明'}],
    'platform:points-mall:商品分类':[{label:'分类名称',placeholder:'请输入分类名称'},{label:'父分类',type:'select',options:['顶级分类','实物好礼','优惠券']},{label:'排序',type:'number'},{label:'状态',type:'select',options:['启用','停用']}],
    'platform:points-mall:积分商品':[{label:'商品名称',placeholder:'请输入积分商品名称'},{label:'商品分类',type:'select',options:['实物好礼','优惠券','会员权益']},{label:'商品类型',type:'select',options:['实物商品','优惠券','虚拟权益']},{label:'积分价格',type:'number'},{label:'兑换库存',type:'number'},{label:'状态',type:'select',options:['上架','下架']},{label:'商品说明',type:'textarea',wide:true}],
    'platform:seckill:活动管理':[{label:'活动名称'},{label:'报名时间',type:'date'},{label:'活动时间',type:'date'},{label:'活动说明',type:'textarea',wide:true},{label:'启用活动',type:'select',options:['启用','停用']}],
    'platform:seckill:场次管理':[{label:'所属活动',type:'select',options:['秋日数码秒杀','晚八点居家场']},{label:'场次名称'},{label:'开始时间',type:'date'},{label:'结束时间',type:'date'}],
    'platform:groupbuy:活动管理':[{label:'活动名称'},{label:'报名时间',type:'date'},{label:'活动时间',type:'date'},{label:'成团人数',type:'number'},{label:'最大开团数',type:'number',placeholder:'-1 表示不限'},{label:'活动说明',type:'textarea',wide:true}],
    'platform:content:Banner':[{label:'Banner 标题'},{label:'Banner 图片',type:'upload'},{label:'跳转类型',type:'select',options:['不跳转','商品详情','活动页面','自定义路径']},{label:'跳转目标'},{label:'排序',type:'number'},{label:'状态',type:'select',options:['启用','停用']}],
    'platform:content:快捷入口':[{label:'入口名称'},{label:'入口图标',type:'upload'},{label:'跳转目标',type:'select',options:['秒杀活动','拼团活动','积分商城','店铺列表']},{label:'排序',type:'number'},{label:'状态',type:'select',options:['启用','停用']}],
    'merchant:categories:':[{label:'上级分类',type:'select',options:['顶级分类','数码影音','耳机音响']},{label:'分类名称'},{label:'分类编码'},{label:'排序',type:'number'},{label:'备注',type:'textarea',wide:true}],
    'merchant:specs:规格定义':[{label:'规格名称'},{label:'规格编码'},{label:'排序',type:'number'},{label:'状态',type:'select',options:['启用','停用']}],
    'merchant:specs:规格选项':[{label:'所属规格',type:'select',options:['颜色','容量','重量']},{label:'选项名称'},{label:'选项编码'},{label:'排序',type:'number'}],
    'merchant:banner:':[{label:'轮播标题'},{label:'轮播图片',type:'upload'},{label:'跳转类型',type:'select',options:['商品详情','商品列表','不跳转']},{label:'跳转目标'},{label:'排序',type:'number'},{label:'状态',type:'select',options:['启用','停用']}],
    'merchant:coupons:优惠券':[{label:'优惠券名称'},{label:'优惠券类型',type:'select',options:['满减券','折扣券','无门槛券']},{label:'优惠金额或折扣',type:'number'},{label:'使用门槛',type:'number'},{label:'总数量',type:'number'},{label:'每人限领',type:'number'},{label:'使用范围',type:'select',options:['店铺通用','指定商品','指定分类']},{label:'有效时间',type:'date'},{label:'支持拼团',type:'select',options:['是','否']},{label:'积分兑换',type:'select',options:['不支持','支持']}],
    'merchant:shipping:物流公司':[{label:'物流公司名称'},{label:'物流公司编码'},{label:'是否常用',type:'select',options:['是','否']},{label:'状态',type:'select',options:['启用','停用']}],
    'merchant:shipping:发货地址':[{label:'地址名称'},{label:'联系人'},{label:'联系电话'},{label:'详细地址',type:'textarea',wide:true},{label:'设为默认',type:'select',options:['是','否']}]
  };
  return definitions[key] || [{label:'名称',placeholder:`请输入${tab||page}名称`},{label:'状态',type:'select',options:['启用','停用']},{label:'备注',type:'textarea',wide:true,placeholder:'选填业务说明'}];
}

function moduleDetailMode(surface,page,tab) {
  if(surface==='platform'&&tab.includes('审核')) return 'audit';
  if(surface==='platform'&&page==='feedback') return 'feedback';
  if(surface==='merchant'&&page==='reviews') return 'reply';
  if(['merchants','shops','categories','points','points-mall','content','specs','banner','shipping','coupons'].includes(page)) return 'edit';
  return 'readonly';
}

function moduleRowAction(surface,page,tab) {
  const mode=moduleDetailMode(surface,page,tab);
  return mode==='audit'?'审核':mode==='feedback'?'处理':mode==='reply'?'回复':mode==='edit'?'编辑':'查看';
}

function ModuleField({ field }) {
  if(field.type==='select') return <label className={field.wide?'wide':''}><span>{field.label}</span><select defaultValue={field.options?.[0]}>{field.options?.map((option)=><option key={option}>{option}</option>)}</select></label>;
  if(field.type==='textarea') return <label className={field.wide?'wide':''}><span>{field.label}</span><textarea placeholder={field.placeholder||`请输入${field.label}`}></textarea></label>;
  if(field.type==='upload') return <label className={field.wide?'wide':''}><span>{field.label}</span><button className="upload-placeholder"><AdminIcon name="image" size={17}/>上传文件</button></label>;
  return <label className={field.wide?'wide':''}><span>{field.label}</span><input type={field.type||'text'} placeholder={field.placeholder||`请输入${field.label}`}/></label>;
}

function AdminModulePage({ surface, page, route, navigate, notify }) {
  const config = moduleDefinition(surface,page);
  const requestedTab = routeParam(route,'tab');
  const routeMemberId = page === 'points' ? routeParam(route,'memberId') : '';
  const [tab,setTab] = useState(requestedTab || config?.tabs?.[0] || '');
  const [filters,setFilters] = useState({});
  const [selected,setSelected] = useState(null);
  const [creating,setCreating] = useState(false);
  const [rowMenu,setRowMenu] = useState(null);
  useEffect(()=>{
    setTab(requestedTab || config?.tabs?.[0] || '');
    setFilters(requestedTab === '积分流水' && routeMemberId ? { keyword: routeMemberId } : {});
    setSelected(null);
    setCreating(false);
    setRowMenu(null);
  },[surface,page,requestedTab,routeMemberId]);
  if (!config) return null;
  const view = moduleTabView(surface,page,tab,{columns:config.columns,rows:config.rows});
  const filterDefinitions=moduleFilterDefinition(surface,page,tab);
  const keyword=String(filters.keyword||'').trim();
  const status=String(filters.status||'');
  const rows = view.rows.filter((row) => (!keyword||row.join('').includes(keyword))&&(!status||status.startsWith('全部')||row.join('').includes(status)));
  const action = moduleAction(surface,page,tab,config.action);
  const formFields=moduleFormDefinition(surface,page,tab);
  const detailMode=moduleDetailMode(surface,page,tab);
  const rowAction=moduleRowAction(surface,page,tab);
  const updateFilter=(key,value)=>setFilters({...filters,[key]:value});
  const finishDetail=(message)=>{setSelected(null);notify(message);};
  return <section className="admin-page" data-screen-label={`${surface}-${config.title}-${tab||'列表'}`}>
    {config.tabs&&<div className="module-tabs">{config.tabs.map((item)=><button key={item} className={tab===item?'active':''} onClick={()=>{setFilters({});navigate(`${surface}/${page}?tab=${encodeURIComponent(item)}`);}}>{item}</button>)}</div>}
    <FilterBar onReset={()=>setFilters({})} onSearch={()=>notify(`找到 ${rows.length} 条记录`)} actions={action&&<button className="business-primary" onClick={()=>setCreating(true)}><AdminIcon name="plus" size={17}/>{action}</button>}>
      {filterDefinitions.map((field)=><label key={field.key}><span>{field.label}</span>{field.options?<select value={filters[field.key]||field.options[0]} onChange={(event)=>updateFilter(field.key,event.target.value)}>{field.options.map((option)=><option key={option}>{option}</option>)}</select>:<input value={filters[field.key]||''} onChange={(event)=>updateFilter(field.key,event.target.value)} placeholder={field.placeholder}/>}</label>)}
    </FilterBar>
    <div className="admin-table-frame"><table data-align-table="t19" className="admin-table"><thead><tr>{view.columns.map((col)=><th key={col}>{col}</th>)}<th className="actions">操作</th></tr></thead><tbody>{rows.map((row,index)=><tr key={index}>{row.map((cell,cellIndex)=>{const value=String(cell);const column=view.columns[cellIndex];const amount=value.includes('¥')||value.startsWith('+')||value.startsWith('-');const content=isStatusCell(cell)?<StatusTicket tone={semanticStatusTone(value)}>{cell}</StatusTicket>:isCopyableAdminColumn(column)?<CopyableText value={value} label={column} notify={notify}/>:cell;return <td className={amount?'amount-cell':''} key={cellIndex}><div className="cb">{content}</div></td>;})}<td className="actions"><div className="cb"><button className="table-link" onClick={()=>setSelected(row)}>{rowAction}</button><RowActionMenu open={rowMenu===index} onToggle={()=>setRowMenu(rowMenu===index?null:index)}>{detailMode!=='edit'&&<button onClick={()=>{setSelected(row);setRowMenu(null);}}>{rowAction}记录</button>}{detailMode==='audit'&&<button onClick={()=>{setSelected(row);setRowMenu(null);}}>进入审核</button>}<button onClick={()=>{setRowMenu(null);notify('操作日志抽屉已打开');}}>查看操作记录</button></RowActionMenu></div></td></tr>)}</tbody></table>{!rows.length&&<div className="admin-empty"><AdminIcon name="filter" size={28}/><b>没有匹配的数据</b><span>调整筛选条件后重试</span></div>}<AdminPagination count={rows.length}/></div>
    {selected&&<div className="drawer-mask" onClick={()=>setSelected(null)}><aside className="admin-drawer module-detail-drawer" onClick={(e)=>e.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">{detailMode==='audit'?'AUDIT':'DETAIL'}</span><h2>{tab||config.title}{rowAction}</h2></div><button onClick={()=>setSelected(null)}>×</button></div><div className="description-grid">{view.columns.map((col,index)=><div className={index===0?'wide':''} key={col}><span>{col}</span><b>{selected[index]}</b></div>)}</div>{detailMode==='edit'&&<div className="module-edit-grid">{formFields.map((field)=><ModuleField key={field.label} field={field}/>)}</div>}{detailMode==='audit'&&<label className="dialog-field"><span>审核备注 <small>驳回时必填</small></span><textarea placeholder="填写本次审核说明"></textarea></label>}{detailMode==='feedback'&&<><label className="dialog-field"><span>处理状态</span><select defaultValue="处理中"><option>处理中</option><option>已解决</option></select></label><label className="dialog-field"><span>平台回复</span><textarea placeholder="填写给会员的处理回复"></textarea></label></>}{detailMode==='reply'&&<label className="dialog-field"><span>回复内容</span><textarea placeholder="输入对会员的回复"></textarea></label>}<div className="drawer-actions"><button onClick={()=>setSelected(null)}>关闭</button>{detailMode==='audit'&&<><button className="danger" onClick={()=>finishDetail('申请已驳回')}>驳回</button><button className="primary" onClick={()=>finishDetail('审核已通过')}>通过</button></>}{detailMode==='feedback'&&<button className="primary" onClick={()=>finishDetail('反馈处理结果已保存')}>保存处理结果</button>}{detailMode==='reply'&&<button className="primary" onClick={()=>finishDetail('回复已发表')}>发表回复</button>}{detailMode==='edit'&&<button className="primary" onClick={()=>finishDetail('修改已保存')}>保存修改</button>}</div></aside></div>}
    {creating&&<div className="dialog-mask" onClick={()=>setCreating(false)}><section className="admin-dialog module-form-dialog" onClick={(e)=>e.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">CREATE</span><h2>{action}</h2></div><button onClick={()=>setCreating(false)}>×</button></div><div className="module-form-grid">{formFields.map((field)=><ModuleField key={field.label} field={field}/>)}</div><div className="dialog-actions"><button onClick={()=>setCreating(false)}>取消</button><button className="primary" onClick={()=>{setCreating(false);notify(`${action}已保存`);}}>确认保存</button></div></section></div>}
  </section>;
}

const PLATFORM_CATEGORY_TREE = [
  { id:'cat-digital', name:'数码家电', code:'digital', level:1, sort:1, remark:'平台一级分类', time:'2026-10-28 09:12', children:[
    { id:'cat-audio', name:'耳机音响', code:'audio', level:2, sort:1, remark:'数码家电子分类', time:'2026-10-28 09:16', children:[
      { id:'cat-headphone', name:'头戴耳机', code:'headphone', level:3, sort:1, remark:'三级分类', time:'2026-10-28 09:20' },
      { id:'cat-in-ear', name:'入耳耳机', code:'in-ear', level:3, sort:2, remark:'', time:'2026-10-28 09:22' }
    ] },
    { id:'cat-phone', name:'手机通讯', code:'phone', level:2, sort:2, remark:'', time:'2026-10-28 09:26', children:[
      { id:'cat-smartphone', name:'智能手机', code:'smartphone', level:3, sort:1, remark:'', time:'2026-10-28 09:28' },
      { id:'cat-feature-phone', name:'老人手机', code:'feature-phone', level:3, sort:2, remark:'仅保留大字大声机型', time:'2026-10-28 09:30' }
    ] },
    { id:'cat-smarthome', name:'智能家居', code:'smarthome', level:2, sort:3, remark:'', time:'2026-10-28 09:34' }
  ] },
  { id:'cat-fashion', name:'服饰鞋包', code:'fashion', level:1, sort:2, remark:'', time:'2026-10-28 10:02', children:[
    { id:'cat-men', name:'男装', code:'men', level:2, sort:1, remark:'', time:'2026-10-28 10:05' },
    { id:'cat-women', name:'女装', code:'women', level:2, sort:2, remark:'', time:'2026-10-28 10:07' },
    { id:'cat-shoes', name:'鞋靴', code:'shoes', level:2, sort:3, remark:'', time:'2026-10-28 10:09' }
  ] },
  { id:'cat-food', name:'食品生鲜', code:'food', level:1, sort:3, remark:'需冷链配送', time:'2026-10-28 10:20', children:[
    { id:'cat-snack', name:'休闲零食', code:'snack', level:2, sort:1, remark:'', time:'2026-10-28 10:22' },
    { id:'cat-fresh', name:'生鲜果蔬', code:'fresh', level:2, sort:2, remark:'48 小时内发货', time:'2026-10-28 10:24' }
  ] },
  { id:'cat-home', name:'家居日用', code:'home', level:1, sort:4, remark:'', time:'2026-10-28 10:40', children:[
    { id:'cat-bedding', name:'家纺床品', code:'bedding', level:2, sort:1, remark:'', time:'2026-10-28 10:42' },
    { id:'cat-kitchen', name:'厨房用品', code:'kitchen', level:2, sort:2, remark:'', time:'2026-10-28 10:44' }
  ] }
];

const platformCategoryTotal = (nodes) => nodes.reduce((total,node)=>total+1+(node.children?platformCategoryTotal(node.children):0),0);

const platformCategoryIds = (nodes,ids=[]) => { nodes.forEach((node)=>{ ids.push(node.id); if(node.children) platformCategoryIds(node.children,ids); }); return ids; };

const filterPlatformCategoryTree = (nodes,keyword) => {
  if (!keyword) return nodes;
  const matched = [];
  nodes.forEach((node)=>{
    const hit = `${node.name}${node.code}`.toLowerCase().includes(keyword);
    const children = node.children ? filterPlatformCategoryTree(node.children,keyword) : [];
    if (hit) matched.push({...node});
    else if (children.length) matched.push({...node,children});
  });
  return matched;
};

const platformCategoryParentName = (nodes,id,parent=null) => {
  for (const node of nodes) {
    if (node.id===id) return parent ? parent.name : '一级分类';
    if (node.children) {
      const found = platformCategoryParentName(node.children,id,node);
      if (found) return found;
    }
  }
  return '';
};

function AdminPlatformCategories({ notify }) {
  const [expanded,setExpanded] = useState(()=>new Set(PLATFORM_CATEGORY_TREE.map((node)=>node.id)));
  const [draftKeyword,setDraftKeyword] = useState('');
  const [keyword,setKeyword] = useState('');
  const [selected,setSelected] = useState([]);
  const [dialog,setDialog] = useState(null);
  const total = useMemo(()=>platformCategoryTotal(PLATFORM_CATEGORY_TREE),[]);
  const tree = useMemo(()=>filterPlatformCategoryTree(PLATFORM_CATEGORY_TREE,keyword),[keyword]);
  const searching = Boolean(keyword);
  const rows = [];
  const collect = (nodes,depth)=>nodes.forEach((node)=>{
    const hasChildren = Boolean(node.children&&node.children.length);
    const open = searching || expanded.has(node.id);
    rows.push({node,depth,hasChildren,open});
    if (hasChildren&&open) collect(node.children,depth+1);
  });
  collect(tree,0);
  const visibleIds = rows.map((row)=>row.node.id);
  const allSelected = visibleIds.length>0 && visibleIds.every((id)=>selected.includes(id));
  const toggle = (id)=>setExpanded((current)=>{ const next=new Set(current); if(next.has(id)) next.delete(id); else next.add(id); return next; });
  const toggleRow = (id)=>setSelected((current)=>current.includes(id)?current.filter((item)=>item!==id):[...current,id]);
  const rootCount = PLATFORM_CATEGORY_TREE.length;
  const dialogTitle = !dialog ? '' : dialog.mode==='edit' ? '编辑平台分类' : dialog.mode==='child' ? `新增「${dialog.node.name}」的子分类` : '新增平台分类';
  const dialogParent = !dialog ? '' : dialog.mode==='child' ? dialog.node.name : dialog.mode==='edit' ? platformCategoryParentName(PLATFORM_CATEGORY_TREE,dialog.node.id) : '一级分类';
  return <section className="admin-page platform-category-page" data-screen-label="平台后台-平台分类">
    <FilterBar onReset={()=>{setDraftKeyword('');setKeyword('');setSelected([]);}} onSearch={()=>{setKeyword(draftKeyword.trim().toLowerCase());setSelected([]);}} actions={<>
      <button className="business-danger" disabled={!selected.length} onClick={()=>{notify(`已删除 ${selected.length} 个平台分类`);setSelected([]);}}>{selected.length?`批量删除 (${selected.length})`:'批量删除'}</button>
      <button className="business-primary" onClick={()=>setDialog({mode:'create'})}><AdminIcon name="plus" size={17}/>新增平台分类</button>
    </>}>
      <label><span>分类名称</span><input value={draftKeyword} onChange={(event)=>setDraftKeyword(event.target.value)} placeholder="请输入平台分类名称或编码"/></label>
    </FilterBar>
    <div className="admin-table-frame">
      <div className="table-toolbar">
        <div><span className="shelf-mark"></span><b>平台分类树</b><small className="ptc-count">共 {total} 个分类 · 一级 {rootCount} 个{searching?` · 命中 ${tree.length} 个一级节点`:''}</small></div>
        <div className="ptc-tools">
          <button onClick={()=>setExpanded(new Set(platformCategoryIds(PLATFORM_CATEGORY_TREE)))}>展开全部</button>
          <button onClick={()=>setExpanded(new Set())}>折叠全部</button>
        </div>
      </div>
      <table data-align-table="t20" className="admin-table ptc-table">
        <thead><tr>
          <th className="ptc-check"><input type="checkbox" checked={allSelected} onChange={()=>setSelected(allSelected?[]:visibleIds)}/></th>
          <th className="ptc-name-head">平台分类名称</th>
          <th>分类图片</th>
          <th>分类编码</th>
          <th>排序</th>
          <th>备注</th>
          <th>更新时间</th>
          <th className="actions">操作</th>
        </tr></thead>
        <tbody>{rows.map(({node,depth,hasChildren,open})=><tr key={node.id}>
          <td className="ptc-check"><div className="cb"><input type="checkbox" checked={selected.includes(node.id)} onChange={()=>toggleRow(node.id)}/></div></td>
          <td className="ptc-name-cell"><div className="cb"><div className="ptc-cell" style={{paddingLeft:depth*18}}>
            <button className={`ptc-caret${open?' open':''}${hasChildren?'':' leaf'}`} aria-label="展开或折叠" onClick={()=>hasChildren&&toggle(node.id)}></button>
            <span className={`ptc-name${node.level===1?' is-root':''}`}>{node.name}</span>
            {hasChildren&&<em className="ptc-chip">{node.children.length}</em>}
          </div></div></td>
          <td><div className="cb"><span className={`ptc-thumb t${node.level}`}>{node.name.slice(0,1)}</span></div></td>
          <td><div className="cb">{node.code}</div></td>
          <td><div className="cb">{node.sort}</div></td>
          <td className="ptc-muted"><div className="cb">{node.remark||'—'}</div></td>
          <td className="ptc-muted"><div className="cb">{node.time}</div></td>
          <td className="actions ptc-actions"><div className="cb">
            <button className="table-link" onClick={()=>setDialog({mode:'edit',node})}>编辑</button>
            <button className="table-link" onClick={()=>setDialog({mode:'child',node})}>新增子分类</button>
            <button className="table-link danger" onClick={()=>notify(`已删除「${node.name}」`)}>删除</button>
          </div></td>
        </tr>)}</tbody>
      </table>
      {!rows.length&&<div className="admin-empty"><AdminIcon name="filter" size={28}/><b>没有匹配的平台分类</b><span>调整分类名称或编码后重试</span></div>}
    </div>
    {dialog&&<div className="dialog-mask" onClick={()=>setDialog(null)}><section className="admin-dialog" onClick={(event)=>event.stopPropagation()}>
      <div className="dialog-head"><div><span className="drawer-kicker">{dialog.mode==='edit'?'EDIT':'CREATE'}</span><h2>{dialogTitle}</h2></div><button onClick={()=>setDialog(null)}>×</button></div>
      <div className="module-form-grid">
        <label className="dialog-field"><span>上级分类</span><input value={dialogParent} disabled/></label>
        <label className="dialog-field"><span>分类名称</span><input defaultValue={dialog.mode==='edit'?dialog.node.name:''} placeholder="请输入平台分类名称"/></label>
        <label className="dialog-field"><span>分类编码</span><input defaultValue={dialog.mode==='edit'?dialog.node.code:''} placeholder="请输入分类编码"/></label>
        <label className="dialog-field"><span>排序</span><input type="number" defaultValue={dialog.mode==='edit'?dialog.node.sort:1}/></label>
      </div>
      <label className="dialog-field ptc-remark"><span>备注</span><textarea rows="2" defaultValue={dialog.mode==='edit'?dialog.node.remark:''} placeholder="选填，最多 255 字"></textarea></label>
      <p className="ptc-dialog-tip">树形一次展示全量分类，不提供分页；默认折叠，展开状态按层级维护。</p>
      <div className="dialog-actions"><button onClick={()=>setDialog(null)}>取消</button><button className="primary" onClick={()=>{setDialog(null);notify('平台分类已保存');}}>保存</button></div>
    </section></div>}
  </section>;
}

const HOME_BANNERS_INITIAL = [
  { id:'hb-1', art:'headphone', tone:'forest', kicker:'本周精选', caption:'秋日声学上新', title:'安静，留给喜欢的声音', target:'/pages/product/list?scene=new', sort:1, isEnabled:true, createdAt:'2026-11-01 09:12:08', updatedAt:'2026-11-02 10:18:32' },
  { id:'hb-2', art:'chair', tone:'clay', kicker:'居家焕新', caption:'居家生活专场', title:'把松弛感，搬进日常每个角落', target:'/pages/product/list?category=home', sort:2, isEnabled:true, createdAt:'2026-10-28 14:20:11', updatedAt:'2026-10-30 09:05:24' },
  { id:'hb-3', art:'serum', tone:'ocean', kicker:'积分有礼', caption:'积分商城上新', title:'积分商城好礼上新', target:'/pages/points-mall/list', sort:3, isEnabled:true, createdAt:'2026-10-24 16:02:45', updatedAt:'2026-10-24 16:02:45' },
  { id:'hb-4', art:'coffee', tone:'ink', kicker:'周末市集', caption:'周末好物折扣', title:'山野咖啡限时折扣', target:'', sort:4, isEnabled:false, createdAt:'2026-10-20 11:40:16', updatedAt:'2026-10-20 11:40:16' }
];

const HOME_QUICK_ACTIONS = {
  seckill:{ icon:'bolt', color:'#be412b', bg:'#f9ebe7' },
  group:{ icon:'users', color:'#24634f', bg:'#e8f2ed' },
  couponCenter:{ icon:'ticket', color:'#986019', bg:'#fbf0de' },
  coupons:{ icon:'ticket', color:'#986019', bg:'#fbf0de' },
  pointsMall:{ icon:'points', color:'#5b4a86', bg:'#efecf7' },
  shops:{ icon:'store', color:'#315c67', bg:'#e8eff2' },
  products:{ icon:'grid', color:'#65726a', bg:'#eef2ef' }
};

const HOME_QUICK_TARGET_ACTIONS = {
  '/pages/seckill/list':'seckill',
  '/pages/group-buy/list':'group',
  '/pages/coupons/center':'couponCenter',
  '/pages/coupons/list':'coupons',
  '/pages/points-mall/list':'pointsMall',
  '/pages/shop/list':'shops',
  '/pages/product/list':'products'
};

const HOME_QUICK_ICON_PRESETS = [
  { id:'qi-bolt', icon:'bolt', bg:'linear-gradient(135deg,#f0a05a,#c25532)' },
  { id:'qi-ticket', icon:'ticket', bg:'linear-gradient(135deg,#d8ae62,#a6742c)' },
  { id:'qi-points', icon:'points', bg:'linear-gradient(135deg,#9a86c8,#64518e)' },
  { id:'qi-store', icon:'store', bg:'linear-gradient(135deg,#6fa3b2,#376672)' },
  { id:'qi-users', icon:'users', bg:'linear-gradient(135deg,#6ea98e,#2d6b52)' },
  { id:'qi-spark', icon:'spark', bg:'linear-gradient(135deg,#93a196,#4c5f54)' }
];

const HOME_QUICK_ENTRIES_INITIAL = [
  { id:'qe-1', name:'限时秒杀', type:1, icon:'', target:'', sort:1, isEnabled:true, remark:'每天 10:00 开抢', updatedAt:'2026-10-28 09:12:10' },
  { id:'qe-2', name:'拼团活动', type:1, icon:'', target:'/pages/group-buy/list', sort:2, isEnabled:true, remark:'两人成团，好物更省', updatedAt:'2026-10-28 09:14:26' },
  { id:'qe-3', name:'积分商城', type:1, icon:'', target:'/pages/points-mall/list', sort:3, isEnabled:true, remark:'积分兑好礼', updatedAt:'2026-10-28 09:16:42' },
  { id:'qe-4', name:'领券中心', type:1, icon:'', target:'/pages/coupons/center', sort:4, isEnabled:true, remark:'', updatedAt:'2026-10-28 09:18:08' },
  { id:'qe-5', name:'店铺列表', type:1, icon:'qi-store', target:'/pages/shop/list', sort:5, isEnabled:true, remark:'精选店铺合集', updatedAt:'2026-10-29 15:40:22' },
  { id:'qe-6', name:'优惠券列表', type:1, icon:'', target:'/pages/coupons/list', sort:6, isEnabled:false, remark:'双 11 后开启', updatedAt:'2026-10-30 10:02:36' }
];

const homeOperationNow = () => {
  const date = new Date();
  const pad = (value) => String(value).padStart(2,'0');
  return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

const isAppPagePath = (value) => {
  const path = String(value||'').trim();
  return !path || path.startsWith('/pages/');
};

const homeQuickAction = (entry) => HOME_QUICK_TARGET_ACTIONS[String(entry.target||'').trim()] || (Number(entry.type)===2?'group':'seckill');

const homeQuickActionMeta = (entry) => HOME_QUICK_ACTIONS[homeQuickAction(entry)] || HOME_QUICK_ACTIONS.seckill;

const homeQuickPreset = (icon) => HOME_QUICK_ICON_PRESETS.find((preset)=>preset.id===icon) || null;

function HomeBannerArt({ banner }) {
  const alt = banner.caption||banner.title||'Banner 图片';
  return <span className={`ho-banner-art tone-${banner.tone||'forest'}${banner.imageUrl?' has-image':''}`}>
    {banner.imageUrl ? <img src={banner.imageUrl} alt={alt}/> : <>
      <span className="ho-banner-mark"></span>
      <span className="ho-banner-kicker">{banner.kicker||'首页 Banner'}</span>
      <b>{alt}</b>
      <ProductArt type={banner.art||'headphone'} bare/>
    </>}
  </span>;
}

function HomeQuickIcon({ entry, size = 38 }) {
  const preset = homeQuickPreset(entry.icon);
  if (preset) {
    return <span className="ho-quick-icon" style={{width:size,height:size,color:'#fff',background:preset.bg}}><AdminIcon name={preset.icon} size={Math.round(size*0.5)}/></span>;
  }
  const meta = homeQuickActionMeta(entry);
  return <span className="ho-quick-icon" style={{width:size,height:size,color:meta.color,background:meta.bg}}><AdminIcon name={meta.icon} size={Math.round(size*0.5)}/></span>;
}

function HomeRemoveConfirm({ title, description, count, onCancel, onConfirm }) {
  return <div className="dialog-mask" onClick={onCancel}><section className="admin-dialog ho-confirm-dialog" onClick={(event)=>event.stopPropagation()}>
    <div className="dialog-head"><div><span className="drawer-kicker">DELETE</span><h2>{title}</h2></div><button onClick={onCancel}>×</button></div>
    <div className="ho-danger-confirm"><AdminIcon name="warning" size={18}/><div><b>{count>1?`将删除选中的 ${count} 项内容`:'将删除这项内容'}</b><span>{description}</span><small>删除后不可恢复，App 首页将立即停止展示。</small></div></div>
    <div className="dialog-actions"><button onClick={onCancel}>取消</button><button className="danger" onClick={onConfirm}>确认删除</button></div>
  </section></div>;
}

function HomeBannerPanel({ banners, setBanners, notify, onPreview }) {
  const [draftKeyword,setDraftKeyword] = useState('');
  const [keyword,setKeyword] = useState('');
  const [draftStatus,setDraftStatus] = useState('全部状态');
  const [status,setStatus] = useState('全部状态');
  const [selected,setSelected] = useState([]);
  const [dialog,setDialog] = useState(null);
  const [removing,setRemoving] = useState(null);
  const fileInputRef = useRef(null);
  const enabledCount = banners.filter((banner)=>banner.isEnabled).length;
  const filtered = banners.filter((banner)=>(!keyword || String(banner.target||'').toLowerCase().includes(keyword)) && (status==='全部状态' || (status==='启用')===banner.isEnabled)).sort((a,b)=>Number(a.sort)-Number(b.sort)||String(b.id).localeCompare(String(a.id)));
  const visibleIds = filtered.map((banner)=>banner.id);
  const allSelected = visibleIds.length>0 && visibleIds.every((id)=>selected.includes(id));
  const toggleRow = (id)=>setSelected((current)=>current.includes(id)?current.filter((item)=>item!==id):[...current,id]);
  const openCreate = ()=>setDialog({ mode:'create', art:'', imageUrl:'', tone:'forest', kicker:'', caption:'', title:'', target:'', sort:Math.max(0,...banners.map((banner)=>Number(banner.sort)||0))+1, isEnabled:true, error:'' });
  const openEdit = (banner)=>setDialog({ mode:'edit', id:banner.id, art:banner.art, imageUrl:banner.imageUrl||'', tone:banner.tone, kicker:banner.kicker, caption:banner.caption, title:banner.title, target:banner.target||'', sort:banner.sort, isEnabled:banner.isEnabled, error:'' });
  const chooseImage = ()=>fileInputRef.current?.click();
  const handleImageFile = (event)=>{
    const file = event.target.files&&event.target.files[0];
    event.target.value = '';
    if (!file) return;
    if (!file.type||!file.type.startsWith('image/')) return notify('请选择图片文件');
    if (file.size>5*1024*1024) return notify('图片大小不能超过 5MB');
    const reader = new FileReader();
    reader.onload = ()=>setDialog((current)=>current?{...current,imageUrl:String(reader.result||''),error:''}:current);
    reader.readAsDataURL(file);
  };
  const applySearch = ()=>{
    const next = draftKeyword.trim().toLowerCase();
    const count = banners.filter((banner)=>(!next || String(banner.target||'').toLowerCase().includes(next)) && (draftStatus==='全部状态' || (draftStatus==='启用')===banner.isEnabled)).length;
    setKeyword(next);
    setStatus(draftStatus);
    setSelected([]);
    notify(`找到 ${count} 个 Banner`);
  };
  const resetSearch = ()=>{ setDraftKeyword(''); setKeyword(''); setDraftStatus('全部状态'); setStatus('全部状态'); setSelected([]); };
  const saveBanner = ()=>{
    const target = dialog.target.trim();
    const sort = Number(dialog.sort);
    if (dialog.mode==='create' && !dialog.art && !dialog.imageUrl) return setDialog({...dialog,error:'请选择一张 Banner 图片'});
    if (!sort || sort<1) return setDialog({...dialog,error:'排序必须大于等于 1'});
    if (!isAppPagePath(target)) return setDialog({...dialog,error:'跳转目标必须是 /pages/ 开头的 App 站内路径，留空表示不跳转'});
    const now = homeOperationNow();
    const payload = { art:dialog.art, imageUrl:dialog.imageUrl||'', tone:dialog.tone, kicker:dialog.kicker, caption:dialog.caption, title:dialog.title, target, sort, isEnabled:dialog.isEnabled, updatedAt:now };
    if (dialog.mode==='edit') {
      setBanners(banners.map((banner)=>banner.id===dialog.id?{...banner,...payload}:banner));
      notify('Banner 已保存');
    } else {
      setBanners([...banners,{ id:`hb-${Date.now()}`, ...payload, createdAt:now }]);
      notify('Banner 已新增');
    }
    setDialog(null);
  };
  const toggleStatus = (banner)=>{
    setBanners(banners.map((item)=>item.id===banner.id?{...item,isEnabled:!item.isEnabled,updatedAt:homeOperationNow()}:item));
    notify(`Banner 已${banner.isEnabled?'禁用':'启用'}`);
  };
  const removeBanners = ()=>{
    const ids = removing?.ids || [];
    if (!ids.length) return;
    setBanners(banners.filter((banner)=>!ids.includes(banner.id)));
    setSelected((current)=>current.filter((id)=>!ids.includes(id)));
    setRemoving(null);
    notify(`已删除 ${ids.length} 个 Banner`);
  };
  return <>
    <FilterBar onReset={resetSearch} onSearch={applySearch} actions={<>
      <button onClick={onPreview}><AdminIcon name="eye" size={16}/>预览效果</button>
      <button className="business-danger" disabled={!selected.length} onClick={()=>setRemoving({ids:selected})}>{selected.length?`批量删除 (${selected.length})`:'批量删除'}</button>
      <button className="business-primary" onClick={openCreate}><AdminIcon name="plus" size={17}/>新增 Banner</button>
    </>}>
      <label><span>跳转目标</span><input value={draftKeyword} onChange={(event)=>setDraftKeyword(event.target.value)} placeholder="搜索 /pages/ 站内路径" onKeyDown={(event)=>event.key==='Enter'&&applySearch()}/></label>
      <label><span>状态</span><select value={draftStatus} onChange={(event)=>setDraftStatus(event.target.value)}><option>全部状态</option><option>启用</option><option>禁用</option></select></label>
    </FilterBar>
    <div className="admin-table-frame">
      <div className="ho-table-intro">
        <span><AdminIcon name="eye" size={14}/>App 首屏全宽轮播已启用 Banner，按排序从小到大展示；建议保持 3–5 张，图片比例为 15:8。</span>
        <b>已启用 {enabledCount} / {banners.length}</b>
      </div>
      <table data-align-table="t21" className="admin-table ho-banner-table">
        <thead><tr>
          <th className="ho-check"><input type="checkbox" checked={allSelected} onChange={()=>setSelected(allSelected?[]:visibleIds)} aria-label="全选 Banner"/></th>
          <th>Banner 图片</th>
          <th>跳转目标</th>
          <th>排序</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>更新时间</th>
          <th className="actions">操作</th>
        </tr></thead>
        <tbody>{filtered.map((banner)=><tr key={banner.id}>
          <td className="ho-check"><div className="cb"><input type="checkbox" checked={selected.includes(banner.id)} onChange={()=>toggleRow(banner.id)} aria-label={`选择 ${banner.caption}`}/></div></td>
          <td><div className="cb"><div className="ho-banner-cell"><HomeBannerArt banner={banner}/></div></div></td>
          <td><div className="cb">{banner.target?<span className="ho-target"><i></i><span className="mono">{banner.target}</span></span>:<span className="ho-target-empty">不跳转</span>}</div></td>
          <td className="numeric"><div className="cb">{banner.sort}</div></td>
          <td><div className="cb"><span className={`ho-switch-cell${banner.isEnabled?' on':''}`}><button type="button" role="switch" aria-checked={banner.isEnabled} aria-label={`${banner.isEnabled?'禁用':'启用'}第 ${banner.sort} 张 Banner`} className={`ho-switch${banner.isEnabled?' on':''}`} onClick={()=>toggleStatus(banner)}><i></i></button><em>{banner.isEnabled?'启用':'禁用'}</em></span></div></td>
          <td><div className="cb"><div className="ho-time-cell">{banner.createdAt}</div></div></td>
          <td><div className="cb"><div className="ho-time-cell">{banner.updatedAt}</div></div></td>
          <td className="actions"><div className="cb"><button className="table-link" onClick={()=>openEdit(banner)}>编辑</button><button className="table-link danger-link" onClick={()=>setRemoving({ids:[banner.id]})}>删除</button></div></td>
        </tr>)}</tbody>
      </table>
      {!filtered.length&&<div className="admin-empty"><AdminIcon name="image" size={28}/><b>没有匹配的 Banner</b><span>调整跳转目标或状态后重试</span></div>}
      <AdminPagination count={filtered.length}/>
    </div>
    {dialog&&<div className="dialog-mask" onClick={()=>setDialog(null)}><section className="admin-dialog ho-banner-dialog" onClick={(event)=>event.stopPropagation()}>
      <div className="dialog-head"><div><span className="drawer-kicker">HOME BANNER</span><h2>{dialog.mode==='edit'?'编辑 Banner':'新增 Banner'}</h2></div><button onClick={()=>setDialog(null)}>×</button></div>
      <div className="ho-banner-dialog-grid">
        <div>
          <span className="ho-field-label">Banner 图片</span>
          {dialog.art||dialog.imageUrl
            ? <><HomeBannerArt banner={dialog}/><div className="ho-banner-upload-actions"><button type="button" className="ho-upload-btn" onClick={chooseImage}><AdminIcon name="image" size={15}/>{dialog.id?'选择新图片（可选）':'重新选择图片'}</button><small>{dialog.imageUrl?'已选择本地图片':'当前图片'}</small></div></>
            : <button type="button" className="ho-upload-empty" onClick={chooseImage}><AdminIcon name="image" size={22}/><b>点击选择 Banner 图片</b><small>建议 750 × 400（15:8），单张不超过 5MB</small></button>}
          <input ref={fileInputRef} type="file" accept="image/*" hidden onChange={handleImageFile}/>
          <small className="ho-field-tip">支持 JPG / PNG / GIF / BMP / WebP / SVG，建议 750 × 400（15:8），单张不超过 5MB；原型仅在本机预览，不会上传服务器。</small>
        </div>
        <div className="ho-banner-form">
          <label className="dialog-field"><span>跳转目标</span><input value={dialog.target} onChange={(event)=>setDialog({...dialog,target:event.target.value,error:''})} maxLength="500" placeholder="/pages/product/list"/></label>
          <small className="ho-field-tip">直接填写 App 实际页面路径（如 /pages/product/list，可带查询参数）；留空时点击 Banner 不跳转。</small>
          <div className="ho-banner-form-grid">
            <label className="dialog-field"><span>排序</span><input type="number" min="1" max="999" value={dialog.sort} onChange={(event)=>setDialog({...dialog,sort:event.target.value,error:''})}/><small className="ho-field-tip">数值越小越靠前，App 按此顺序轮播。</small></label>
            <div className="dialog-field"><span>状态</span><span className="ho-dialog-switch"><button type="button" role="switch" aria-checked={dialog.isEnabled} aria-label={dialog.isEnabled?'禁用 Banner':'启用 Banner'} className={`ho-switch${dialog.isEnabled?' on':''}`} onClick={()=>setDialog({...dialog,isEnabled:!dialog.isEnabled})}><i></i></button><em>{dialog.isEnabled?'启用':'禁用'}</em></span><small className="ho-field-tip">启用后才会在 App 首屏展示。</small></div>
          </div>
          {dialog.error&&<p className="ho-field-error">{dialog.error}</p>}
        </div>
      </div>
      <div className="dialog-actions"><button onClick={()=>setDialog(null)}>取消</button><button className="primary" onClick={saveBanner}>保存</button></div>
    </section></div>}
    {removing&&<HomeRemoveConfirm title="删除 Banner" description={removing.ids.length>1?`将删除 ${removing.ids.length} 张轮播图。`:'删除后该图片将从 App 首页轮播中移除。'} count={removing.ids.length} onCancel={()=>setRemoving(null)} onConfirm={removeBanners}/>}
  </>;
}

function HomeQuickEntryPanel({ entries, setEntries, notify, onPreview }) {
  const [draftName,setDraftName] = useState('');
  const [draftStatus,setDraftStatus] = useState('全部状态');
  const [filters,setFilters] = useState({ name:'', status:'全部状态' });
  const [selected,setSelected] = useState([]);
  const [dialog,setDialog] = useState(null);
  const [removing,setRemoving] = useState(null);
  const enabledCount = entries.filter((entry)=>entry.isEnabled).length;
  const filtered = entries.filter((entry)=>`${entry.name}`.toLowerCase().includes(filters.name.toLowerCase())&&(filters.status==='全部状态'||(filters.status==='启用')===entry.isEnabled)).sort((a,b)=>Number(a.sort)-Number(b.sort));
  const visibleIds = filtered.map((entry)=>entry.id);
  const allSelected = visibleIds.length>0 && visibleIds.every((id)=>selected.includes(id));
  const toggleRow = (id)=>setSelected((current)=>current.includes(id)?current.filter((item)=>item!==id):[...current,id]);
  const openCreate = ()=>setDialog({ mode:'create', name:'', type:1, icon:'', target:'', sort:Math.max(0,...entries.map((entry)=>Number(entry.sort)||0))+1, isEnabled:true, remark:'', error:'' });
  const openEdit = (entry)=>setDialog({ mode:'edit', id:entry.id, name:entry.name, type:entry.type, icon:entry.icon||'', target:entry.target||'', sort:entry.sort, isEnabled:entry.isEnabled, remark:entry.remark||'', error:'' });
  const applySearch = ()=>{
    const name = draftName.trim().toLowerCase();
    setFilters({ name, status:draftStatus });
    setSelected([]);
    notify(`找到 ${entries.filter((entry)=>`${entry.name}`.toLowerCase().includes(name)&&(draftStatus==='全部状态'||(draftStatus==='启用')===entry.isEnabled)).length} 个快捷入口`);
  };
  const resetSearch = ()=>{ setDraftName(''); setDraftStatus('全部状态'); setFilters({ name:'', status:'全部状态' }); setSelected([]); };
  const saveEntry = ()=>{
    const name = dialog.name.trim();
    const target = dialog.target.trim();
    if (!name) return setDialog({...dialog,error:'请输入入口名称'});
    if (!Number(dialog.sort) || Number(dialog.sort)<1) return setDialog({...dialog,error:'排序必须大于等于 1'});
    if (!isAppPagePath(target)) return setDialog({...dialog,error:'跳转目标必须是 /pages/ 开头的 App 站内路径，留空表示按内置动作跳转'});
    const now = homeOperationNow();
    const payload = { name, type:Number(dialog.type), icon:dialog.icon, target, sort:Number(dialog.sort), isEnabled:dialog.isEnabled, remark:dialog.remark.trim(), updatedAt:now };
    if (dialog.mode==='edit') {
      setEntries(entries.map((entry)=>entry.id===dialog.id?{...entry,...payload}:entry));
      notify('快捷入口已保存');
    } else {
      setEntries([...entries,{ id:`qe-${Date.now()}`, ...payload }]);
      notify('快捷入口已新增');
    }
    setDialog(null);
  };
  const toggleStatus = (entry)=>{
    setEntries(entries.map((item)=>item.id===entry.id?{...item,isEnabled:!item.isEnabled,updatedAt:homeOperationNow()}:item));
    notify(`入口「${entry.name}」已${entry.isEnabled?'禁用':'启用'}`);
  };
  const removeEntries = ()=>{
    const ids = removing?.ids || [];
    if (!ids.length) return;
    setEntries(entries.filter((entry)=>!ids.includes(entry.id)));
    setSelected((current)=>current.filter((id)=>!ids.includes(id)));
    setRemoving(null);
    notify(`已删除 ${ids.length} 个快捷入口`);
  };
  return <>
    <FilterBar onReset={resetSearch} onSearch={applySearch} actions={<>
      <button onClick={onPreview}><AdminIcon name="eye" size={16}/>预览效果</button>
      <button className="business-danger" disabled={!selected.length} onClick={()=>setRemoving({ids:selected})}>{selected.length?`批量删除 (${selected.length})`:'批量删除'}</button>
      <button className="business-primary" onClick={openCreate}><AdminIcon name="plus" size={17}/>新增入口</button>
    </>}>
      <label><span>入口名称</span><input value={draftName} onChange={(event)=>setDraftName(event.target.value)} placeholder="请输入入口名称" onKeyDown={(event)=>event.key==='Enter'&&applySearch()}/></label>
      <label><span>状态</span><select value={draftStatus} onChange={(event)=>setDraftStatus(event.target.value)}><option>全部状态</option><option>启用</option><option>禁用</option></select></label>
    </FilterBar>
    <div className="admin-table-frame">
      <div className={`ho-table-intro${enabledCount>10?' is-warn':''}`}>
        <span><AdminIcon name="grid" size={14}/>App 首页最多展示前 10 个已启用入口，超出部分不会展示；排序数值越小越靠前。</span>
        <b>{enabledCount>10?`已启用 ${enabledCount} · 超出 ${enabledCount-10}`:`已启用 ${enabledCount} / 10`}</b>
      </div>
      <table data-align-table="t22" className="admin-table ho-quick-table">
        <thead><tr>
          <th className="ho-check"><input type="checkbox" checked={allSelected} onChange={()=>setSelected(allSelected?[]:visibleIds)} aria-label="全选快捷入口"/></th>
          <th>入口</th>
          <th>跳转目标</th>
          <th>排序</th>
          <th>状态</th>
          <th>备注</th>
          <th>更新时间</th>
          <th className="actions">操作</th>
        </tr></thead>
        <tbody>{filtered.map((entry)=><tr key={entry.id}>
          <td className="ho-check"><div className="cb"><input type="checkbox" checked={selected.includes(entry.id)} onChange={()=>toggleRow(entry.id)} aria-label={`选择 ${entry.name}`}/></div></td>
          <td><div className="cb"><div className="ho-entry-cell"><HomeQuickIcon entry={entry}/><div><b>{entry.name}</b><small>{entry.icon?'自定义图标':'默认图标'}</small></div></div></div></td>
          <td><div className="cb">{entry.target?<span className="ho-target"><i></i><span className="mono">{entry.target}</span></span>:<span className="ho-target-empty">按内置动作跳转</span>}</div></td>
          <td className="numeric"><div className="cb">{entry.sort}</div></td>
          <td><div className="cb"><span className={`ho-switch-cell${entry.isEnabled?' on':''}`}><button type="button" role="switch" aria-checked={entry.isEnabled} aria-label={`${entry.isEnabled?'禁用':'启用'}${entry.name}`} className={`ho-switch${entry.isEnabled?' on':''}`} onClick={()=>toggleStatus(entry)}><i></i></button><em>{entry.isEnabled?'启用':'禁用'}</em></span></div></td>
          <td><div className="cb"><span className="ho-remark">{entry.remark||'—'}</span></div></td>
          <td><div className="cb"><div className="ho-time-cell">{entry.updatedAt}</div></div></td>
          <td className="actions"><div className="cb"><button className="table-link" onClick={()=>openEdit(entry)}>编辑</button><button className="table-link danger-link" onClick={()=>setRemoving({ids:[entry.id]})}>删除</button></div></td>
        </tr>)}</tbody>
      </table>
      {!filtered.length&&<div className="admin-empty"><AdminIcon name="grid" size={28}/><b>没有匹配的快捷入口</b><span>调整名称或状态后重试</span></div>}
      <AdminPagination count={filtered.length}/>
    </div>
    {dialog&&<div className="dialog-mask" onClick={()=>setDialog(null)}><section className="admin-dialog ho-quick-dialog" onClick={(event)=>event.stopPropagation()}>
      <div className="dialog-head"><div><span className="drawer-kicker">QUICK ENTRY</span><h2>{dialog.mode==='edit'?'编辑快捷入口':'新增快捷入口'}</h2></div><button onClick={()=>setDialog(null)}>×</button></div>
      <label className="dialog-field"><span>入口名称</span><input value={dialog.name} onChange={(event)=>setDialog({...dialog,name:event.target.value,error:''})} maxLength="32" placeholder="请输入入口名称"/></label>
      <div className="dialog-field"><span>入口图标 <small>选填</small></span>
        {dialog.icon&&<div className="ho-icon-field">
          <HomeQuickIcon entry={dialog} size={44}/>
          <div className="ho-icon-copy">
            <button type="button" onClick={()=>setDialog({...dialog,icon:'',error:''})}>移除图标</button>
            <small>已选择自定义图标</small>
          </div>
        </div>}
        <div className="ho-icon-picker" aria-label="预置图标，模拟上传">
          {HOME_QUICK_ICON_PRESETS.map((preset)=><button type="button" key={preset.id} className={`ho-icon-preset${dialog.icon===preset.id?' selected':''}`} style={{background:preset.bg}} onClick={()=>setDialog({...dialog,icon:preset.id,error:''})} aria-label={`选择预置图标 ${preset.id}`}><AdminIcon name={preset.icon} size={17}/></button>)}
        </div>
        <small className="ho-field-tip">原型以预置图标模拟图片上传；支持 JPG / PNG / GIF / BMP / WebP / SVG，单张不超过 2MB。</small>
      </div>
      <label className="dialog-field"><span>跳转目标 <small>选填</small></span><input value={dialog.target} onChange={(event)=>setDialog({...dialog,target:event.target.value,error:''})} maxLength="255" placeholder="/pages/product/list"/></label>
      <small className="ho-field-tip">直接填写 App 实际页面路径（如 /pages/product/list，可带查询参数）；需以 /pages/ 开头，留空时按内置动作跳转。</small>
      <div className="ho-quick-form-grid">
        <label className="dialog-field"><span>排序</span><input type="number" min="1" max="999" value={dialog.sort} onChange={(event)=>setDialog({...dialog,sort:event.target.value,error:''})}/><small className="ho-field-tip">数值越小越靠前，App 首页最多展示 10 个已启用入口。</small></label>
        <div className="dialog-field"><span>状态</span><span className="ho-dialog-switch"><button type="button" role="switch" aria-checked={dialog.isEnabled} aria-label={dialog.isEnabled?'禁用快捷入口':'启用快捷入口'} className={`ho-switch${dialog.isEnabled?' on':''}`} onClick={()=>setDialog({...dialog,isEnabled:!dialog.isEnabled})}><i></i></button><em>{dialog.isEnabled?'启用':'禁用'}</em></span></div>
      </div>
      <label className="dialog-field"><span>备注 <small>选填</small></span><textarea rows="3" maxLength="255" value={dialog.remark} onChange={(event)=>setDialog({...dialog,remark:event.target.value})} placeholder="补充入口用途或上线说明"/><small className="ho-counter">{dialog.remark.length}/255</small></label>
      {dialog.error&&<p className="ho-field-error">{dialog.error}</p>}
      <div className="dialog-actions"><button onClick={()=>setDialog(null)}>取消</button><button className="primary" onClick={saveEntry}>保存</button></div>
    </section></div>}
    {removing&&<HomeRemoveConfirm title="删除快捷入口" description={removing.ids.length>1?`将删除 ${removing.ids.length} 个首页入口。`:'删除后该入口将从 App 首页快捷区移除。'} count={removing.ids.length} onCancel={()=>setRemoving(null)} onConfirm={removeEntries}/>}
  </>;
}

function HomePreviewDrawer({ banners, entries, notify, onClose, onJumpTab }) {
  const [index,setIndex] = useState(0);
  const enabledBanners = banners.filter((banner)=>banner.isEnabled).sort((a,b)=>Number(a.sort)-Number(b.sort)||String(b.id).localeCompare(String(a.id)));
  const allEnabledEntries = entries.filter((entry)=>entry.isEnabled).sort((a,b)=>Number(a.sort)-Number(b.sort));
  const enabledEntries = allEnabledEntries.slice(0,10);
  const overflowCount = Math.max(allEnabledEntries.length-10,0);
  const rows = enabledEntries.length<=4
    ? [{ items:enabledEntries, maxCount:Math.max(enabledEntries.length,1) }]
    : (()=>{ const first = Math.ceil(enabledEntries.length/2); return [{ items:enabledEntries.slice(0,first), maxCount:first },{ items:enabledEntries.slice(first), maxCount:first }]; })();
  const activeBanner = enabledBanners[index] || enabledBanners[0] || null;
  useEffect(()=>{ if(index>=enabledBanners.length) setIndex(0); },[index,enabledBanners.length]);
  return <div className="drawer-mask" onClick={onClose}><aside className="admin-drawer ho-preview-drawer" onClick={(event)=>event.stopPropagation()}>
    <div className="drawer-head"><div><span className="drawer-kicker">APP HOME PREVIEW</span><h2>App 首屏预览</h2></div><button onClick={onClose}>×</button></div>
    <div className="ho-preview-note"><AdminIcon name="eye" size={16}/><span>当前展示 <b>{enabledBanners.length}</b> 张已启用 Banner（15:8 全宽轮播）和 <b>{enabledEntries.length}</b> 个快捷入口；Banner 与入口均按排序从小到大展示{overflowCount>0?`，另有 ${overflowCount} 个已启用入口超出前 10 个展示位`:''}。</span></div>
    <div className="ho-preview-phone">
      <div className="ho-phone-status"><span>9:41</span><span>···</span></div>
      <div className="ho-phone-topbar"><b>YEAH<span>MALL</span></b><span className="ho-phone-search"><AdminIcon name="search" size={14}/>搜索商品</span></div>
      {activeBanner?<div className="ho-phone-banner-wrap">
        <button type="button" className={`ho-phone-banner tone-${activeBanner.tone||'forest'}${activeBanner.imageUrl?' has-image':''}`} onClick={()=>activeBanner.target?notify(`Banner 将跳转至 ${activeBanner.target}`):notify('该 Banner 未配置跳转')}>
          {activeBanner.imageUrl
            ? <img className="ho-phone-banner-img" src={activeBanner.imageUrl} alt={activeBanner.caption||activeBanner.title||'Banner 图片'}/>
            : <><span className="ho-phone-banner-copy"><em>{activeBanner.kicker||'首页 Banner'}</em><strong>{activeBanner.title}</strong><span>查看详情 <AdminIcon name="chevron" size={12}/></span></span><ProductArt type={activeBanner.art||'headphone'} bare/></>}
        </button>
        <span className="ho-phone-dots">{enabledBanners.map((banner,dotIndex)=><button type="button" key={banner.id} className={dotIndex===index?'active':''} aria-label={`切换到第 ${dotIndex+1} 张 Banner`} onClick={(event)=>{event.stopPropagation();setIndex(dotIndex);}}></button>)}</span>
      </div>:<div className="ho-phone-banner-empty">暂无已启用 Banner</div>}
      {rows.length?<div className="ho-phone-quick">{rows.map((row,rowIndex)=><div className="ho-phone-quick-row" style={{gridTemplateColumns:`repeat(${row.maxCount},minmax(0,1fr))`}} key={rowIndex}>{row.items.map((entry)=><button type="button" key={entry.id} onClick={()=>notify(entry.target?`「${entry.name}」将跳转至 ${entry.target}`:`「${entry.name}」按内置动作跳转`)}><span className="ho-phone-quick-icon"><HomeQuickIcon entry={entry} size={20}/></span><span>{entry.name}</span></button>)}</div>)}</div>:<div className="ho-phone-quick-empty">暂无已启用的快捷入口</div>}
      <div className="ho-phone-placeholder"><i className="ho-phone-line"></i><b>精选好物</b><small>商品推荐不受首页运营配置影响</small></div>
    </div>
    <div className="drawer-actions ho-preview-actions"><button onClick={onClose}>关闭</button><button onClick={()=>onJumpTab('banner')}>配置 Banner</button><button className="primary" onClick={()=>onJumpTab('quick')}>配置快捷入口</button></div>
  </aside></div>;
}

function AdminHomeOperation({ route, notify }) {
  const requestedTab = routeParam(route,'tab');
  const [tab,setTab] = useState(requestedTab==='快捷入口'?'quick':'banner');
  const [banners,setBanners] = useState(HOME_BANNERS_INITIAL);
  const [entries,setEntries] = useState(HOME_QUICK_ENTRIES_INITIAL);
  const [preview,setPreview] = useState(false);
  useEffect(()=>{ setTab(requestedTab==='快捷入口'?'quick':'banner'); },[requestedTab]);
  const jumpTab = (next)=>{ setTab(next); setPreview(false); };
  return <section className="admin-page home-operation-page" data-screen-label={`平台后台首页运营-${tab}`}>
    <div className="module-tabs">
      <button className={tab==='banner'?'active':''} onClick={()=>setTab('banner')}>Banner</button>
      <button className={tab==='quick'?'active':''} onClick={()=>setTab('quick')}>快捷入口</button>
    </div>
    {tab==='banner'
      ? <HomeBannerPanel banners={banners} setBanners={setBanners} notify={notify} onPreview={()=>setPreview(true)}/>
      : <HomeQuickEntryPanel entries={entries} setEntries={setEntries} notify={notify} onPreview={()=>setPreview(true)}/>}
    {preview&&<HomePreviewDrawer banners={banners} entries={entries} notify={notify} onClose={()=>setPreview(false)} onJumpTab={jumpTab}/>}
  </section>;
}

const SECKILL_STATUS_ORDER = ['1','2','3','4','5','6','7'];
const SECKILL_STATUS_META = {
  '1': { label:'新建', tone:'neutral', action:'发布', next:'2', hint:'发布前会校验报名与活动时间段、场次时间范围及场次重叠情况。' },
  '2': { label:'已发布', tone:'info', action:'开始报名', next:'3', hint:'开始报名后，商家可在报名时间段内提交商品报名。' },
  '3': { label:'报名中', tone:'warning', action:'结束报名', next:'4', hint:'结束报名后活动进入等待开始状态，请确认场次与报名商品后开始活动。' },
  '4': { label:'报名结束', tone:'info', action:'开始活动', next:'5', hint:'开始活动后系统会准备秒杀库存与活动缓存，并校验审核通过的秒杀商品。' },
  '5': { label:'进行中', tone:'success', action:'结束活动', next:'6', hint:'结束活动后系统会写入活动状态并清理活动缓存，活动进入结束阶段。' },
  '6': { label:'已结束', tone:'neutral', action:'活动下架', next:'7', hint:'活动下架后会从进行中集合移除，App 端不再作为进行中活动展示。' },
  '7': { label:'已下架', tone:'neutral', action:'', next:'', hint:'' }
};
const SECKILL_FLOW = [
  { value:'1', label:'新建' },
  { value:'2', label:'发布' },
  { value:'3', label:'报名中' },
  { value:'4', label:'报名结束' },
  { value:'5', label:'进行中' },
  { value:'6', label:'结束' },
  { value:'7', label:'下架' }
];
const SECKILL_ACTIVITIES_INITIAL = [
  { id:'sa-1', name:'秋日数码秒杀', description:'耳机、音箱与智能穿戴限时直降，主推降噪耳机。', art:'headphone', applyBeginTime:'2026-10-20 10:00:00', applyEndTime:'2026-10-24 22:00:00', activityBeginTime:'2026-10-27 10:00:00', activityEndTime:'2026-10-27 12:00:00', status:'5', sort:1, enabled:true, sessionCount:3, remark:'活动缓存已准备，库存按审核通过商品锁定。', createTime:'2026-10-15 09:20:00', updateTime:'2026-10-27 09:58:12' },
  { id:'sa-2', name:'晚八点居家场', description:'居家好物晚八点场，覆盖休闲椅、香薰与床品。', art:'chair', applyBeginTime:'2026-11-14 10:00:00', applyEndTime:'2026-11-20 18:00:00', activityBeginTime:'2026-11-22 20:00:00', activityEndTime:'2026-11-22 22:00:00', status:'3', sort:2, enabled:true, sessionCount:2, remark:'', createTime:'2026-11-10 14:30:00', updateTime:'2026-11-16 18:06:40' },
  { id:'sa-3', name:'深秋护肤专场', description:'精华、面霜与身体护理组合，面向秋冬敏感肌。', art:'serum', applyBeginTime:'2026-11-19 10:00:00', applyEndTime:'2026-11-23 22:00:00', activityBeginTime:'2026-11-25 10:00:00', activityEndTime:'2026-11-25 22:00:00', status:'2', sort:3, enabled:true, sessionCount:2, remark:'', createTime:'2026-11-12 10:12:00', updateTime:'2026-11-17 09:20:05' },
  { id:'sa-4', name:'周末咖啡闪购', description:'挂耳咖啡与手冲器具周末限时组合。', art:'coffee', applyBeginTime:'2026-11-18 10:00:00', applyEndTime:'2026-11-20 22:00:00', activityBeginTime:'2026-11-22 10:00:00', activityEndTime:'2026-11-22 14:00:00', status:'1', sort:4, enabled:true, sessionCount:0, remark:'场次待配置，发布前需补充场次时间。', createTime:'2026-11-17 11:42:18', updateTime:'2026-11-17 11:42:18' },
  { id:'sa-5', name:'户外生活装备节', description:'户外休闲椅、便携家居与露营周边限时好价。', art:'chair', applyBeginTime:'2026-11-10 10:00:00', applyEndTime:'2026-11-15 22:00:00', activityBeginTime:'2026-11-18 10:00:00', activityEndTime:'2026-11-18 22:00:00', status:'4', sort:5, enabled:true, sessionCount:2, remark:'', createTime:'2026-11-05 09:08:00', updateTime:'2026-11-16 14:30:22' },
  { id:'sa-6', name:'双十一返场秒杀', description:'大促返场，保留活动历史数据用于复盘。', art:'headphone', applyBeginTime:'2026-10-28 10:00:00', applyEndTime:'2026-11-01 22:00:00', activityBeginTime:'2026-11-03 10:00:00', activityEndTime:'2026-11-03 22:00:00', status:'6', sort:6, enabled:false, sessionCount:3, remark:'', createTime:'2026-10-24 16:40:00', updateTime:'2026-11-04 08:10:02' },
  { id:'sa-7', name:'夏日清凉节', description:'夏季冰饮与清凉家居专题，已下架归档。', art:'coffee', applyBeginTime:'2026-08-01 10:00:00', applyEndTime:'2026-08-05 22:00:00', activityBeginTime:'2026-08-08 10:00:00', activityEndTime:'2026-08-08 22:00:00', status:'7', sort:7, enabled:false, sessionCount:1, remark:'', createTime:'2026-07-28 09:30:00', updateTime:'2026-08-09 10:05:36' },
  { id:'sa-8', name:'会员耳机专享场', description:'会员专属降噪耳机与音箱限量秒杀。', art:'headphone', applyBeginTime:'2026-11-01 10:00:00', applyEndTime:'2026-11-05 22:00:00', activityBeginTime:'2026-11-17 20:00:00', activityEndTime:'2026-11-17 22:00:00', status:'5', sort:8, enabled:true, sessionCount:2, remark:'', createTime:'2026-10-28 10:16:00', updateTime:'2026-11-17 12:08:52' }
];
const seckillShortTime = (value) => value ? `${value.slice(5,10)} ${value.slice(11,16)}` : '—';
const seckillFullRange = (start, end) => start && end ? `${start.slice(0,16)} 至 ${end.slice(0,16)}` : '待设置';
const seckillToInput = (value) => value ? value.slice(0,16).replace(' ','T') : '';
const seckillFromInput = (value) => value ? `${value.replace('T',' ')}:00`.slice(0,19) : '';
const seckillPublishIssues = (row) => {
  const issues = [];
  if (!row.applyBeginTime || !row.applyEndTime || !row.activityBeginTime || !row.activityEndTime) {
    issues.push('报名与活动时间段未完整设置');
  } else {
    if (row.applyBeginTime >= row.applyEndTime) issues.push('报名开始时间需早于结束时间');
    if (row.activityBeginTime >= row.activityEndTime) issues.push('活动开始时间需早于结束时间');
    if (row.applyEndTime > row.activityBeginTime) issues.push('报名截止时间需早于活动开始时间');
  }
  if (!row.sessionCount) issues.push('尚未配置活动场次');
  return issues;
};

function AdminSeckillActivity({ activities: rows, setActivities: setRows, navigate, notify }) {
  const [tab,setTab] = useState('all');
  const [filters,setFilters] = useState({ keyword:'', enabled:'全部状态' });
  const [applied,setApplied] = useState({ keyword:'', enabled:'全部状态' });
  const [detail,setDetail] = useState(null);
  const [editing,setEditing] = useState(null);
  const [errors,setErrors] = useState({});
  const [coverPreview,setCoverPreview] = useState('');
  const [menu,setMenu] = useState(null);
  const [transition,setTransition] = useState(null);
  const [deleting,setDeleting] = useState(null);
  const baseRows = [...rows].sort((a,b)=>a.sort-b.sort||String(a.id).localeCompare(String(b.id))).filter((row)=>{
    const keyword = applied.keyword.trim().toLowerCase();
    const keywordMatch = !keyword || `${row.name}${row.description}${row.remark}`.toLowerCase().includes(keyword);
    const enabledMatch = applied.enabled==='全部状态' || (applied.enabled==='启用' ? row.enabled : !row.enabled);
    return keywordMatch && enabledMatch;
  });
  const counts = { all: baseRows.length };
  SECKILL_STATUS_ORDER.forEach((status)=>{ counts[status] = baseRows.filter((row)=>row.status===status).length; });
  const filtered = baseRows.filter((row)=>tab==='all'||row.status===tab);
  const setupCount = rows.filter((row)=>row.status==='1'&&!row.sessionCount).length;
  const search = () => setApplied({ ...filters });
  const reset = () => {
    const empty = { keyword:'', enabled:'全部状态' };
    setFilters(empty);
    setApplied(empty);
    setTab('all');
  };
  const goSessions = (row) => {
    setMenu(null);
    setDetail(null);
    navigate(`platform/seckill?tab=${encodeURIComponent('场次管理')}&activityId=${row.id}`);
  };
  const goAudit = (row) => {
    setMenu(null);
    setDetail(null);
    navigate(`platform/seckill?tab=${encodeURIComponent('报名审核')}&activityId=${row.id}`);
  };
  const openCreate = () => {
    setMenu(null);
    setDetail(null);
    setErrors({});
    setCoverPreview('');
    setEditing({ id:'', name:'', description:'', coverImage:'', art:'coffee', applyBeginTime:'', applyEndTime:'', activityBeginTime:'', activityEndTime:'', sort:1, remark:'', status:'1', enabled:true, sessionCount:0, createTime:'', updateTime:'' });
  };
  const openEdit = (row) => {
    setMenu(null);
    setDetail(null);
    setErrors({});
    setCoverPreview(row.coverPreview || '');
    setEditing({ ...row });
  };
  const setField = (key,value) => setEditing((current)=>current ? { ...current,[key]:value } : current);
  const pickCover = (file) => {
    if (!file) return;
    setCoverPreview(URL.createObjectURL(file));
  };
  const save = () => {
    const nextErrors = {};
    if (!editing.name.trim()) nextErrors.name = '请输入活动名称';
    if (!editing.description.trim()) nextErrors.description = '请输入活动描述';
    if (!editing.sort || Number(editing.sort)<1) nextErrors.sort = '排序需大于等于 1';
    if (!editing.applyBeginTime || !editing.applyEndTime) nextErrors.applyTime = '请选择完整的报名开始和结束时间';
    else if (editing.applyBeginTime >= editing.applyEndTime) nextErrors.applyTime = '报名开始时间需早于结束时间';
    if (!editing.activityBeginTime || !editing.activityEndTime) nextErrors.activityTime = '请选择完整的活动开始和结束时间';
    else if (editing.activityBeginTime >= editing.activityEndTime) nextErrors.activityTime = '活动开始时间需早于结束时间';
    if (!nextErrors.applyTime && !nextErrors.activityTime && editing.applyEndTime > editing.activityBeginTime) {
      nextErrors.sequence = '报名截止时间需早于活动开始时间，请调整后再保存。';
    }
    if (Object.keys(nextErrors).length) {
      setErrors(nextErrors);
      return;
    }
    const stamp = '2026-11-17 15:36:20';
    if (editing.id) {
      const next = { ...editing, coverImage:editing.coverImage||'sample-cover', coverPreview:coverPreview||editing.coverPreview||'', updateTime:stamp };
      setRows(rows.map((row)=>row.id===editing.id?next:row));
      if (detail?.id===editing.id) setDetail(next);
      notify('活动信息已保存');
    } else {
      const next = { ...editing, id:`sa-${Date.now()}`, status:'1', enabled:true, sessionCount:0, coverImage:editing.coverImage||'sample-cover', coverPreview:coverPreview||'', createTime:stamp, updateTime:stamp };
      setRows([...rows,next]);
      notify('活动已创建，请配置场次后发布');
    }
    setEditing(null);
    setErrors({});
  };
  const toggleEnabled = (row) => {
    const nextEnabled = !row.enabled;
    const stamp = '2026-11-17 15:38:00';
    setRows(rows.map((item)=>item.id===row.id?{ ...item, enabled:nextEnabled, updateTime:stamp }:item));
    if (detail?.id===row.id) setDetail({ ...detail, enabled:nextEnabled, updateTime:stamp });
    notify(`活动已${nextEnabled?'启用':'停用'}`);
  };
  const openTransition = (row) => {
    const meta = SECKILL_STATUS_META[row.status];
    if (!meta?.action) return;
    setMenu(null);
    setTransition({ row, meta, issues:meta.next==='2' ? seckillPublishIssues(row) : [] });
  };
  const confirmTransition = () => {
    if (!transition || transition.issues.length) return;
    const stamp = '2026-11-17 15:40:12';
    const nextRow = { ...transition.row, status:transition.meta.next, updateTime:stamp };
    setRows(rows.map((item)=>item.id===nextRow.id?nextRow:item));
    if (detail?.id===nextRow.id) setDetail(nextRow);
    notify(`活动已${transition.meta.action}`);
    setTransition(null);
  };
  const confirmDelete = () => {
    if (!deleting) return;
    setRows(rows.filter((row)=>row.id!==deleting.id));
    if (detail?.id===deleting.id) setDetail(null);
    setDeleting(null);
    notify('活动已删除');
  };
  return (
    <section className="admin-page seckill-admin-page" data-screen-label="平台后台秒杀活动管理">
      <div className="sk-status-tabs">
        <button data-status="all" className={tab==='all'?'active':''} onClick={()=>setTab('all')}>全部<span className="sk-tab-count">{counts.all}</span></button>
        {SECKILL_STATUS_ORDER.map((status)=><button key={status} data-status={status} className={tab===status?'active':''} onClick={()=>setTab(status)}>{SECKILL_STATUS_META[status].label}<span className="sk-tab-count">{counts[status]}</span></button>)}
      </div>

      <FilterBar
        onReset={reset}
        onSearch={search}
        actions={<button className="business-primary" onClick={openCreate}><AdminIcon name="plus" size={17}/>新建活动</button>}
      >
        <label><span>活动名称</span><input value={filters.keyword} onChange={(event)=>setFilters({ ...filters,keyword:event.target.value })} onKeyDown={(event)=>event.key==='Enter'&&search()} placeholder="搜索活动名称或描述"/></label>
        <label><span>启用状态</span><select value={filters.enabled} onChange={(event)=>setFilters({ ...filters,enabled:event.target.value })}><option>全部状态</option><option>启用</option><option>停用</option></select></label>
      </FilterBar>

      <div className="admin-table-frame">
        <div className="sk-table-intro">
          <span><AdminIcon name="clock" size={15}/>发布前需配置至少 1 个场次；报名截止时间需早于活动开始时间。</span>
          <div className="sk-intro-right">
            {setupCount>0&&<span className="sk-setup-chip"><AdminIcon name="warning" size={12}/>{setupCount} 个待配置场次</span>}
            <b>共 {filtered.length} 个活动</b>
          </div>
        </div>
        <table data-align-table="t23" className="admin-table sk-activity-table">
          <thead><tr><th>活动信息</th><th>报名时间</th><th>活动时间</th><th>场次</th><th>状态</th><th className="numeric">排序</th><th>启用</th><th className="actions">操作</th></tr></thead>
          <tbody>{filtered.map((row)=>{
            const meta = SECKILL_STATUS_META[row.status] || SECKILL_STATUS_META['1'];
            return <tr key={row.id}>
              <td><div className="cb"><div className="sk-activity-cell">
                <span className="sk-cover-art">{row.coverPreview?<img src={row.coverPreview} alt="活动封面"/>:<ProductArt type={row.art||'coffee'}/>}</span>
                <span className="sk-activity-copy">
                  <button className="sk-name-link" title={row.name} onClick={()=>setDetail(row)}>{row.name}</button>
                  <small title={row.description}>{row.description||'暂无活动描述'}</small>
                </span>
              </div></div></td>
              <td><div className="cb"><div className="sk-when-cell" title={`${row.applyBeginTime} 至 ${row.applyEndTime}`}>
                {row.applyBeginTime&&row.applyEndTime?<><span>{seckillShortTime(row.applyBeginTime)}</span><i>→</i><span>{seckillShortTime(row.applyEndTime)}</span></>:<span className="sk-when-empty">待设置</span>}
              </div></div></td>
              <td><div className="cb"><div className="sk-when-cell" title={`${row.activityBeginTime} 至 ${row.activityEndTime}`}>
                {row.activityBeginTime&&row.activityEndTime?<><span>{seckillShortTime(row.activityBeginTime)}</span><i>→</i><span>{seckillShortTime(row.activityEndTime)}</span></>:<span className="sk-when-empty">待设置</span>}
              </div></div></td>
              <td><div className="cb"><button className={`sk-session-link${row.sessionCount?'':' is-empty'}`} onClick={()=>goSessions(row)}>{row.sessionCount?`${row.sessionCount} 场`:'未配置'}</button></div></td>
              <td><div className="cb"><StatusTicket tone={meta.tone}>{meta.label}</StatusTicket></div></td>
              <td className="numeric"><div className="cb">{row.sort}</div></td>
              <td><div className="cb"><label className="sk-switch"><input type="checkbox" checked={row.enabled} onChange={()=>toggleEnabled(row)}/><i></i><span>{row.enabled?'启用':'停用'}</span></label></div></td>
              <td className="actions"><div className="cb">
                {meta.action?<button className="table-link sk-next-link" onClick={()=>openTransition(row)}>{meta.action}</button>:<button className="table-link" onClick={()=>setDetail(row)}>详情</button>}
                <button className="table-link" onClick={()=>openEdit(row)}>编辑</button>
                <RowActionMenu open={menu===row.id} onToggle={()=>setMenu(menu===row.id?null:row.id)}>
                  <button onClick={()=>{setDetail(row);setMenu(null);}}>查看详情</button>
                  <button onClick={()=>goSessions(row)}>场次管理</button>
                  <button onClick={()=>goAudit(row)}>报名审核</button>
                  <button className="sk-danger-action" disabled={row.status!=='1'} title={row.status!=='1'?'仅新建状态的活动可删除':''} onClick={()=>{setMenu(null);setDeleting(row);}}>删除</button>
                </RowActionMenu>
              </div></td>
            </tr>;
          })}</tbody>
        </table>
        {!filtered.length&&<div className="admin-empty"><AdminIcon name="Calendar" size={28}/><b>没有匹配的秒杀活动</b><span>调整筛选条件，或新建一个活动</span></div>}
        <AdminPagination count={filtered.length}/>
      </div>

      {detail&&(()=>{
        const meta = SECKILL_STATUS_META[detail.status] || SECKILL_STATUS_META['1'];
        const currentIndex = SECKILL_FLOW.findIndex((step)=>step.value===detail.status);
        return <div className="drawer-mask" onClick={()=>setDetail(null)}><aside className="admin-drawer sk-detail-drawer" onClick={(event)=>event.stopPropagation()}>
          <div className="drawer-head"><div><span className="drawer-kicker">SECKILL ACTIVITY</span><h2>活动详情</h2></div><button onClick={()=>setDetail(null)}>×</button></div>
          <div className="sk-detail-hero">
            <span className="sk-detail-cover">{detail.coverPreview?<img src={detail.coverPreview} alt="活动封面"/>:<ProductArt type={detail.art||'coffee'}/>}</span>
            <div className="sk-detail-copy"><h3>{detail.name}</h3><StatusTicket tone={meta.tone}>{meta.label}</StatusTicket><p>{detail.description||'暂无活动描述'}</p></div>
          </div>
          <div className="sk-detail-times">
            <div><span>报名时间</span><b>{seckillFullRange(detail.applyBeginTime,detail.applyEndTime)}</b></div>
            <div><span>活动时间</span><b>{seckillFullRange(detail.activityBeginTime,detail.activityEndTime)}</b></div>
          </div>
          <div className="sk-detail-meta">
            <div><span>活动场次</span><b>{detail.sessionCount?`${detail.sessionCount} 个`:'未配置'}</b></div>
            <div><span>排序</span><b>{detail.sort}</b></div>
            <div><span>启用状态</span><b>{detail.enabled?'启用':'停用'}</b></div>
            <div><span>最近更新</span><b>{detail.updateTime?detail.updateTime.slice(0,19):'—'}</b></div>
          </div>
          <div className="drawer-section"><h3>活动进度</h3><div className="sk-flow">
            {SECKILL_FLOW.map((step,index)=><span key={step.value} className={`sk-flow-step${index<=currentIndex?' is-done':''}${index===currentIndex?' is-current':''}`}><i></i><small>{step.label}</small></span>)}
          </div></div>
          <div className="drawer-section"><h3>备注</h3><p className="sk-detail-remark">{detail.remark||'暂无备注'}</p></div>
          <div className="drawer-actions"><button onClick={()=>setDetail(null)}>关闭</button><button onClick={()=>goSessions(detail)}>场次管理</button><button className="primary" onClick={()=>openEdit(detail)}>编辑活动</button></div>
        </aside></div>;
      })()}

      {editing&&<div className="dialog-mask" onClick={()=>setEditing(null)}><section className="admin-dialog sk-activity-dialog" onClick={(event)=>event.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">SECKILL ACTIVITY</span><h2>{editing.id?'编辑活动':'新建活动'}</h2></div><button onClick={()=>setEditing(null)}>×</button></div>
        <div className="sk-dialog-note"><AdminIcon name="Lightning" size={16}/><span>发布前需配置活动场次，并确保报名截止时间早于活动开始时间；保存后可在列表按阶段推进活动状态。</span></div>
        <div className="sk-form-grid">
          <label className={`dialog-field${errors.name?' is-invalid':''}`}><span>活动名称 <em className="sk-required">*</em></span><input value={editing.name} maxLength="64" onChange={(event)=>setField('name',event.target.value)} placeholder="例如：秋日数码秒杀"/>{errors.name&&<small className="sk-field-error">{errors.name}</small>}</label>
          <label className={`dialog-field${errors.sort?' is-invalid':''}`}><span>排序 <em className="sk-required">*</em></span><input type="number" min="1" value={editing.sort} onChange={(event)=>setField('sort',Number(event.target.value))}/>{errors.sort&&<small className="sk-field-error">{errors.sort}</small>}</label>
          <label className={`dialog-field wide${errors.description?' is-invalid':''}`}><span>活动描述 <em className="sk-required">*</em></span><textarea rows="3" maxLength="500" value={editing.description} onChange={(event)=>setField('description',event.target.value)} placeholder="简要说明活动商品与玩法"/>{errors.description&&<small className="sk-field-error">{errors.description}</small>}</label>
          <div className="dialog-field wide"><span>活动封面 <em className="sk-required">*</em></span>
            <div className="sk-cover-editor">
              <span className="sk-cover-preview">{coverPreview?<img src={coverPreview} alt="活动封面预览"/>:<ProductArt type={editing.art||'coffee'}/>}</span>
              <span className="sk-cover-copy">
                <label className="sk-upload-btn"><AdminIcon name="image" size={16}/>{coverPreview?'更换封面':'选择封面'}<input type="file" accept="image/*" onChange={(event)=>{pickCover(event.target.files?.[0]);event.target.value='';}}/></label>
                <small>{coverPreview?'已选择本地图片，保存后用于活动列表与 App 展示。':'建议 640×320（2:1），支持 JPG / PNG / WebP，单张不超过 2MB；未上传时使用示意封面。'}</small>
              </span>
            </div>
          </div>
          <div className={`dialog-field wide${errors.applyTime?' is-invalid':''}`}><span>报名时间 <em className="sk-required">*</em></span>
            <div className="sk-time-inputs">
              <label><small>开始</small><input type="datetime-local" value={seckillToInput(editing.applyBeginTime)} onChange={(event)=>setField('applyBeginTime',seckillFromInput(event.target.value))}/></label>
              <i>→</i>
              <label><small>结束</small><input type="datetime-local" value={seckillToInput(editing.applyEndTime)} onChange={(event)=>setField('applyEndTime',seckillFromInput(event.target.value))}/></label>
            </div>
            {errors.applyTime&&<small className="sk-field-error">{errors.applyTime}</small>}
          </div>
          <div className={`dialog-field wide${errors.activityTime?' is-invalid':''}`}><span>活动时间 <em className="sk-required">*</em></span>
            <div className="sk-time-inputs">
              <label><small>开始</small><input type="datetime-local" value={seckillToInput(editing.activityBeginTime)} onChange={(event)=>setField('activityBeginTime',seckillFromInput(event.target.value))}/></label>
              <i>→</i>
              <label><small>结束</small><input type="datetime-local" value={seckillToInput(editing.activityEndTime)} onChange={(event)=>setField('activityEndTime',seckillFromInput(event.target.value))}/></label>
            </div>
            {errors.activityTime&&<small className="sk-field-error">{errors.activityTime}</small>}
          </div>
          <label className="dialog-field wide"><span>备注 <small>选填</small></span><textarea rows="2" maxLength="255" value={editing.remark} onChange={(event)=>setField('remark',event.target.value)} placeholder="补充运营说明或发布注意事项"/></label>
        </div>
        {errors.sequence&&<p className="sk-sequence-error"><AdminIcon name="warning" size={14}/>{errors.sequence}</p>}
        <div className="dialog-actions"><button onClick={()=>setEditing(null)}>取消</button><button className="primary" onClick={save}>{editing.id?'保存修改':'创建活动'}</button></div>
      </section></div>}

      {transition&&(()=>{
        const current = SECKILL_STATUS_META[transition.row.status] || SECKILL_STATUS_META['1'];
        const next = SECKILL_STATUS_META[transition.meta.next] || current;
        const isPublish = transition.meta.next==='2';
        const fail = transition.issues.length>0;
        const timeConfigured = Boolean(transition.row.applyBeginTime&&transition.row.applyEndTime&&transition.row.activityBeginTime&&transition.row.activityEndTime);
        const orderOk = timeConfigured && transition.row.applyBeginTime<transition.row.applyEndTime && transition.row.activityBeginTime<transition.row.activityEndTime && transition.row.applyEndTime<=transition.row.activityBeginTime;
        return <div className="dialog-mask" onClick={()=>setTransition(null)}><section className="admin-dialog sk-transition-dialog" onClick={(event)=>event.stopPropagation()}>
          <div className="dialog-head"><div><span className="drawer-kicker">SECKILL WORKFLOW</span><h2>{transition.meta.action}活动</h2></div><button onClick={()=>setTransition(null)}>×</button></div>
          <div className="sk-transition-context">
            <span className="sk-cover-art small">{transition.row.coverPreview?<img src={transition.row.coverPreview} alt="活动封面"/>:<ProductArt type={transition.row.art||'coffee'}/>}</span>
            <span className="sk-transition-copy"><b>{transition.row.name}</b><small>报名 {seckillShortTime(transition.row.applyBeginTime)} → {seckillShortTime(transition.row.applyEndTime)}</small><small>活动 {seckillShortTime(transition.row.activityBeginTime)} → {seckillShortTime(transition.row.activityEndTime)}</small></span>
            <span className="sk-transition-shift"><StatusTicket tone={current.tone}>{current.label}</StatusTicket><AdminIcon name="ArrowRight" size={14}/><StatusTicket tone={next.tone}>{next.label}</StatusTicket></span>
          </div>
          {isPublish&&<div className={`sk-check ${fail?'is-fail':'is-pass'}`}>
            <b><AdminIcon name={fail?'warning':'check'} size={14}/>{fail?'发布前检查未通过':'发布前检查通过'}</b>
            <ul className="sk-check-list">
              <li className={timeConfigured?'':'is-bad'}><i></i>报名与活动时间段已配置</li>
              <li className={orderOk?'':'is-bad'}><i></i>报名截止时间早于活动开始时间</li>
              <li className={transition.row.sessionCount?'':'is-bad'}><i></i>已配置活动场次{transition.row.sessionCount?`（${transition.row.sessionCount} 个）`:''}</li>
            </ul>
          </div>}
          <p className="sk-transition-hint">{transition.meta.hint}</p>
          <div className="dialog-actions">
            <button onClick={()=>setTransition(null)}>取消</button>
            {isPublish&&fail&&<button onClick={()=>goSessions(transition.row)}>前往场次管理</button>}
            <button className="primary" disabled={fail} onClick={confirmTransition}>确认{transition.meta.action}</button>
          </div>
        </section></div>;
      })()}

      {deleting&&<div className="dialog-mask" onClick={()=>setDeleting(null)}><section className="admin-dialog sk-delete-dialog" onClick={(event)=>event.stopPropagation()}>
        <div className="dialog-head"><div><span className="drawer-kicker">DELETE ACTIVITY</span><h2>删除活动</h2></div><button onClick={()=>setDeleting(null)}>×</button></div>
        <div className="danger-confirm"><AdminIcon name="warning" size={18}/><div><b>删除后不可恢复</b><span>仅【新建】状态的活动可以删除。删除后活动基础信息将被移除，后续需重新创建。</span><small>当前活动：{deleting.name}</small></div></div>
        <p className="sk-delete-context">如需保留活动记录，请改为停用或推进至活动下架。</p>
        <div className="dialog-actions"><button onClick={()=>setDeleting(null)}>取消</button><button className="danger" onClick={confirmDelete}>确认删除</button></div>
      </section></div>}
    </section>
  );
}

function AdminSeckillPage({ route, navigate, notify }) {
  const [activities,setActivities] = useState(SECKILL_ACTIVITIES_INITIAL);
  const [sessionsByActivity,setSessionsByActivity] = useState(()=>{
    const grouped = {};
    SECKILL_SESSIONS_INITIAL.forEach((item)=>{ if (!grouped[item.activityId]) grouped[item.activityId] = []; grouped[item.activityId].push(item); });
    return grouped;
  });
  const [applies,setApplies] = useState(SECKILL_APPLIES_INITIAL);
  const tab = routeParam(route,'tab') || '活动管理';
  if (tab === '场次管理') return <AdminSeckillSessions activities={activities} setActivities={setActivities} sessionsByActivity={sessionsByActivity} setSessionsByActivity={setSessionsByActivity} route={route} navigate={navigate} notify={notify}/>;
  if (tab === '报名审核') return <AdminSeckillAudits activities={activities} applies={applies} setApplies={setApplies} sessionsByActivity={sessionsByActivity} route={route} navigate={navigate} notify={notify}/>;
  if (tab !== '活动管理') return <AdminModulePage surface="platform" page="seckill" route={route} navigate={navigate} notify={notify}/>;
  return <AdminSeckillActivity activities={activities} setActivities={setActivities} navigate={navigate} notify={notify}/>;
}

function MerchantSettingsPage({ notify }) {
  const [open,setOpen] = useState(true);
  const [name,setName] = useState('山野生活店');
  const [intro,setIntro] = useState('挑选耐用、舒适、有生活气息的日常好物。');
  return <section className="admin-page merchant-settings-page" data-screen-label="商家后台店铺设置"><div className="settings-form-card"><div className="settings-section-title"><span className="shelf-mark"></span><div><b>店铺信息设置</b><small>资料仅作用于当前登录选择的店铺，保存后同步展示在 App 店铺页</small></div><StatusTicket tone={open?'success':'neutral'}>{open?'营业中':'已停业'}</StatusTicket></div><div className="settings-form-layout"><div className="settings-logo-panel"><div className="settings-logo-preview">野</div><button onClick={()=>notify('请选择店铺 Logo 图片')}>更换 Logo</button><small>建议 200 × 200，支持 JPG、PNG</small></div><div className="settings-form-grid"><label><span>店铺编号</span><input value="SHAN-YE" disabled/></label><label><span>店铺名称</span><input value={name} onChange={(e)=>setName(e.target.value)}/></label><label className="wide"><span>店铺简介</span><textarea maxLength="500" value={intro} onChange={(e)=>setIntro(e.target.value)}></textarea><small>{intro.length}/500</small></label><label className="business-switch"><span><b>营业状态</b><small>停业后商品仍可浏览，但用户不能提交新订单</small></span><input type="checkbox" checked={open} onChange={(e)=>setOpen(e.target.checked)}/></label><label><span>平台佣金</span><input value="5%" disabled/><small>由平台统一设置</small></label><label><span>结算周期</span><input value="订单完成后 7 天" disabled/><small>如有疑问请联系平台管理员</small></label></div></div><div className="settings-save-bar"><button onClick={()=>notify('店铺设置已恢复为加载值')}>重置</button><button className="primary" onClick={()=>notify('店铺设置已保存')}>保存设置</button></div></div></section>;
}

function FrameworkReferencePage({ route, notify }) {
  const [selected,setSelected] = useState(null);
  const view = routeParam(route,'view') || 'user';
  const definitions = {
    user:['用户管理',['用户名','昵称','组织','状态'],[['admin','平台管理员','总部','正常'],['operator','运营人员','运营中心','正常']]],
    org:['组织管理',['组织名称','组织编码','负责人','状态'],[['平台总部','headquarters','平台管理员','正常'],['运营中心','operation','运营人员','正常']]],
    position:['岗位管理',['岗位名称','岗位编码','排序','状态'],[['平台管理员','admin','1','正常'],['商城运营','mall_operator','2','正常']]],
    role:['角色管理',['角色名称','角色编码','用户数','状态'],[['超级管理员','super_admin','1','正常'],['商城运营','mall_operator','3','正常']]],
    resource:['资源管理',['资源名称','资源类型','路由','状态'],[['系统管理','目录','/system','正常'],['商城管理','目录','/mall','正常']]],
    dict:['字典管理',['字典名称','字典编码','数据项','状态'],[['订单状态','order_status','8','正常'],['启停状态','enable_status','2','正常']]],
    config:['系统参数',['参数名称','参数键','参数值','状态'],[['平台名称','system.name','Yeah Mall','正常'],['开放注册','member.register.enabled','是','正常']]],
    file:['文件管理',['文件名称','文件类型','文件大小','状态'],[['banner-autumn.jpg','image/jpeg','286 KB','正常'],['product-guide.pdf','application/pdf','1.2 MB','正常']]],
    storage:['存储管理',['存储名称','存储类型','使用情况','状态'],[['本地存储','LOCAL','2.8 GB','启用'],['对象存储','OSS','未配置','停用']]],
    log:['系统日志',['操作模块','操作用户','请求方式','状态'],[['订单管理','admin','GET','成功'],['商品管理','operator','POST','成功']]],
    area:['地区管理',['地区名称','地区编码','层级','状态'],[['浙江省','330000','省','正常'],['杭州市','330100','市','正常']]
    ]
  };
  const [title,columns,rows] = definitions[view] || definitions.user;
  return <section className="admin-page framework-reference-page" data-screen-label={`平台后台系统管理-${title}`}><div className="framework-preserved-note"><AdminIcon name="settings" size={18}/><span className="framework-note-copy"><b>{title}沿用 YeahBoot 页面能力</b><small>仅接入统一主题、紧凑密度和布局外壳，不改变框架接口、权限与交互契约。</small></span><StatusTicket>FRAMEWORK</StatusTicket></div><FilterBar onReset={()=>notify('筛选条件已重置')} onSearch={()=>notify(`找到 ${rows.length} 条记录`)}><label><span>关键词</span><input placeholder={`搜索${title}`}/></label><label><span>状态</span><select defaultValue="全部状态"><option>全部状态</option><option>正常</option><option>停用</option></select></label></FilterBar><div className="admin-table-frame"><table data-align-table="t24" className="admin-table"><thead><tr>{columns.map((col)=><th key={col}>{col}</th>)}<th className="actions">操作</th></tr></thead><tbody>{rows.map((row,index)=><tr key={index}>{row.map((cell,cellIndex)=><td key={`${cell}-${cellIndex}`}><div className="cb">{isCopyableAdminColumn(columns[cellIndex])?<CopyableText value={cell} label={columns[cellIndex]} notify={notify}/>:cell}</div></td>)}<td className="actions"><div className="cb"><button className="table-link" onClick={()=>setSelected(row)}>查看</button></div></td></tr>)}</tbody></table><AdminPagination count={rows.length}/></div>{selected&&<div className="drawer-mask" onClick={()=>setSelected(null)}><aside className="admin-drawer framework-detail-drawer" onClick={(event)=>event.stopPropagation()}><div className="drawer-head"><div><span className="drawer-kicker">YEAHBOOT DETAIL</span><h2>{title}详情</h2></div><button onClick={()=>setSelected(null)}>×</button></div><div className="description-grid">{columns.map((column,index)=><div className={index===0?'wide':''} key={column}><span>{column}</span><b>{isCopyableAdminColumn(column)?<CopyableText value={selected[index]} label={column} notify={notify}/>:selected[index]}</b></div>)}</div><div className="drawer-section"><h3>维护说明</h3><p className="framework-detail-note">该页面沿用 YeahBoot 的权限和数据契约，YeahMall 仅统一视觉主题与内容密度。</p></div><div className="drawer-actions"><button onClick={()=>setSelected(null)}>关闭</button><button className="primary" onClick={()=>{setSelected(null);notify(`${title}记录已更新`);}}>编辑记录</button></div></aside></div>}</section>;
}

function AdminSettlementPage({ surface, route, navigate, notify }) {
  const platform=surface==='platform';
  const tabs=platform?['结算台账','账户审核','支付通道']:['结算记录','收款账户','独立App支付'];
  const requested=routeParam(route,'tab');
  const active=tabs.includes(requested)?requested:tabs[0];
  const [keyword,setKeyword]=useState('');
  const [status,setStatus]=useState('全部状态');
  const [selected,setSelected]=useState([]);
  const [dialog,setDialog]=useState(null);
  useEffect(()=>{setKeyword('');setStatus('全部状态');setSelected([]);setDialog(null);},[active,surface]);
  const ledger=[
    ['YM202611020018','山野生活店','订单收入','¥0.01','¥0.00','+¥0.01','待结算','-'],
    ['YM202611010086','Yeah 数码生活馆','订单收入','¥299.00','¥14.95','+¥284.05','已结算','JS20261101001'],
    ['YM202610290032','白茶生活馆','退款冲减','¥139.00','¥0.00','-¥139.00','待结算','-']
  ];
  const filteredLedger=ledger.filter((row)=>(status==='全部状态'||row.includes(status))&&row.join('').includes(keyword));
  const accounts=[['1','山野生活店','银行卡','杭州山野生活有限公司','**** 8862','待审核','2026-11-01 15:20'],['2','Yeah 数码生活馆','支付宝','杭州悦数科技有限公司','pay***@mail.com','已通过','2026-10-28 09:36']];
  const channels=[['平台普通商户','平台主通道','平台普通商户直收','platform','启用'],['服务商主通道','平台主通道','服务商代子商户收款','platform','停用'],['山野独立 App','山野生活店','独立店铺普通商户直收','shop-app','启用']];
  const changeTab=(tab)=>navigate(`${surface}/settlement?tab=${encodeURIComponent(tab)}`);
  const renderLedger=()=> <><FilterBar onReset={()=>{setKeyword('');setStatus('全部状态');}} onSearch={()=>notify(`找到 ${filteredLedger.length} 条结算记录`)} actions={platform&&<button className="business-primary" disabled={!selected.length} onClick={()=>setDialog({type:'settle'})}><AdminIcon name="check" size={16}/>确认线下打款</button>}><label><span>{platform?'店铺或订单':'订单编号'}</span><input value={keyword} onChange={(e)=>setKeyword(e.target.value)} placeholder={platform?'店铺 ID、名称或订单号':'请输入订单编号'}/></label><label><span>结算状态</span><select value={status} onChange={(e)=>setStatus(e.target.value)}><option>全部状态</option><option>待结算</option><option>已结算</option><option>已取消</option></select></label><label><span>结算批次</span><input placeholder="请输入结算批次编号"/></label></FilterBar><div className="admin-table-frame"><table data-align-table="t25" className="admin-table"><thead><tr>{platform&&<th className="selection-cell"></th>}<th>订单编号</th>{platform&&<th>店铺</th>}<th>业务类型</th><th className="numeric">实际支付</th><th className="numeric">平台佣金</th><th className="numeric">结算变动</th><th>状态</th><th>结算批次</th></tr></thead><tbody>{filteredLedger.map((row,index)=><tr key={row[0]}>{platform&&<td className="selection-cell"><input type="checkbox" checked={selected.includes(index)} disabled={row[6]!=='待结算'} onChange={()=>setSelected(selected.includes(index)?selected.filter((item)=>item!==index):[...selected,index])}/></td>}<td><div className="cb"><b className="mono"><CopyableText value={row[0]} label="订单编号" notify={notify}/></b><small className="cell-sub">可结算 2026-11-03</small></div></td>{platform&&<td><div className="cb"><CopyableText value={row[1]} label="店铺名称" notify={notify}/></div></td>}<td><div className="cb">{row[2]}</div></td><td className="numeric"><div className="cb">{row[3]}</div></td><td className="numeric"><div className="cb">{row[4]}</div></td><td className={`numeric ${row[5].startsWith('-')?'negative-amount':'positive-amount'}`}><div className="cb">{row[5]}</div></td><td><div className="cb"><StatusTicket tone={row[6]==='已结算'?'success':'warning'}>{row[6]}</StatusTicket></div></td><td className="mono"><div className="cb">{row[7]==='-'?'-':<CopyableText value={row[7]} label="结算批次" notify={notify}/>}</div></td></tr>)}</tbody></table><AdminPagination count={filteredLedger.length}/></div></>;
  const renderAccounts=()=> <><FilterBar onReset={()=>{setKeyword('');setStatus('全部状态');}} onSearch={()=>notify(`找到 ${accounts.length} 个账户`)}><label><span>店铺</span><input value={keyword} onChange={(event)=>setKeyword(event.target.value)} placeholder="店铺 ID 或名称"/></label><label><span>审核状态</span><select value={status} onChange={(event)=>setStatus(event.target.value)}><option>全部状态</option><option>待审核</option><option>已通过</option><option>已拒绝</option></select></label></FilterBar><div className="admin-table-frame"><table data-align-table="t26" className="admin-table"><thead><tr><th>店铺</th><th>账户类型</th><th>收款名称</th><th>收款账号</th><th>审核状态</th><th>提交时间</th><th className="actions">操作</th></tr></thead><tbody>{accounts.filter((row)=>(status==='全部状态'||row[5]===status)&&(!keyword||row.join('').includes(keyword))).map((row)=><tr key={row[0]}><td><div className="cb"><CopyableText value={row[1]} label="店铺名称" notify={notify}/></div></td><td><div className="cb">{row[2]}</div></td><td><div className="cb"><CopyableText value={row[3]} label="收款名称" notify={notify}/></div></td><td className="mono"><div className="cb"><CopyableText value={row[4]} label="收款账号" notify={notify}/></div></td><td><div className="cb"><StatusTicket tone={row[5]==='已通过'?'success':'warning'}>{row[5]}</StatusTicket></div></td><td><div className="cb">{row[6]}</div></td><td className="actions"><div className="cb"><button className="table-link" onClick={()=>setDialog({type:'audit',row})}>{row[5]==='待审核'?'审核':'查看'}</button></div></td></tr>)}</tbody></table><AdminPagination count={accounts.length}/></div></>;
  const renderChannels=()=> <><div className="settlement-channel-actions"><button className="business-primary" onClick={()=>setDialog({type:'channel',mode:'平台普通商户直收'})}>新增平台普通商户通道</button><button onClick={()=>setDialog({type:'channel',mode:'服务商代子商户收款'})}>新增服务商主通道</button><button onClick={()=>setDialog({type:'channel',mode:'子商户路由'})}>新增子商户路由</button></div><div className="admin-table-frame"><table data-align-table="t27" className="admin-table"><thead><tr><th>通道名称</th><th>适用范围</th><th>收款模式</th><th>App 目标</th><th>状态</th><th className="actions">操作</th></tr></thead><tbody>{channels.map((row)=><tr key={row[0]}>{row.map((cell,index)=><td key={index}><div className="cb">{index===4?<StatusTicket tone={cell==='启用'?'success':'neutral'}>{cell}</StatusTicket>:[0,1,3].includes(index)?<CopyableText value={cell} label={index===0?'通道名称':index===1?'适用范围':'App目标'} notify={notify}/>:cell}</div></td>)}<td className="actions"><div className="cb"><button className="table-link" onClick={()=>setDialog({type:'channel',mode:row[2],editing:true})}>编辑</button></div></td></tr>)}</tbody></table><AdminPagination count={channels.length}/></div></>;
  const renderMerchantForm=(payment=false)=> <section className="settlement-editor"><div className="settlement-editor-head"><div><b>{payment?'独立 App 支付通道':'收款账户'}</b><span>{payment?'配置当前店铺独立 App 的微信支付信息':'平台线下结算将打款到审核通过的账户'}</span></div><StatusTicket tone={payment?'success':'warning'}>{payment?'已启用':'待审核'}</StatusTicket></div><div className="settlement-form-grid">{payment?<><label><span>通道名称</span><input defaultValue="山野独立 App 支付"/></label><label><span>App 目标</span><input defaultValue="shop-app"/></label><label><span>应用 AppID</span><input placeholder="请输入应用 AppID"/></label><label><span>商户号</span><input placeholder="请输入微信支付商户号"/></label><label><span>商户 API 证书序列号</span><input placeholder="请输入证书序列号"/></label><label><span>微信支付公钥 ID</span><input placeholder="请输入公钥 ID"/></label><label className="wide"><span>支付回调地址</span><input placeholder="请输入支付回调地址"/></label><label className="wide"><span>退款回调地址</span><input placeholder="请输入退款回调地址"/></label></>:<><label><span>账户类型</span><select defaultValue="银行卡"><option>银行卡</option><option>支付宝</option></select></label><label><span>收款名称</span><input defaultValue="杭州山野生活有限公司"/></label><label><span>收款账号</span><input defaultValue="6222 **** **** 8862"/></label><label><span>开户银行</span><input defaultValue="招商银行杭州分行"/></label></>}</div><div className="settlement-save-bar"><span>{payment?'敏感凭据仅在保存时提交，页面不回显明文':'审核通过前可继续修改账户信息'}</span><button onClick={()=>notify(payment?'支付配置已保存':'收款账户已提交审核')}>保存设置</button></div></section>;
  return <section className="admin-page settlement-admin-page" data-screen-label={`${platform?'平台':'商家'}结算管理-${active}`}><div className="module-tabs">{tabs.map((tab)=><button className={active===tab?'active':''} key={tab} onClick={()=>changeTab(tab)}>{tab}</button>)}</div>{platform?(active==='结算台账'?renderLedger():active==='账户审核'?renderAccounts():renderChannels()):(active==='结算记录'?renderLedger():renderMerchantForm(active==='独立App支付'))}{dialog?.type==='settle'&&<div className="dialog-mask" onClick={()=>setDialog(null)}><section className="admin-dialog settlement-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">OFFLINE SETTLEMENT</span><h2>确认线下打款</h2></div><button onClick={()=>setDialog(null)}>×</button></div><div className="settlement-summary"><span>本次结算 {selected.length} 条记录</span><b>净额 ¥284.06</b></div><div className="description-grid"><div><span>收款方式</span><b>银行卡</b></div><div><span>收款名称</span><b>杭州山野生活有限公司</b></div><div className="wide"><span>收款账号</span><b>6222 **** **** 8862</b></div></div><label className="dialog-field"><span>转账流水号</span><input placeholder="请输入银行或第三方转账流水号"/></label><label className="dialog-field"><span>打款凭证</span><button className="upload-placeholder"><AdminIcon name="image" size={18}/>上传图片</button></label><label className="dialog-field"><span>备注 <small>选填</small></span><textarea placeholder="补充本次结算说明"></textarea></label><div className="dialog-actions"><button onClick={()=>setDialog(null)}>取消</button><button className="primary" onClick={()=>{setDialog(null);notify('结算已确认');}}>确认已打款</button></div></section></div>}{dialog?.type==='audit'&&<div className="dialog-mask" onClick={()=>setDialog(null)}><section className="admin-dialog settlement-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">ACCOUNT AUDIT</span><h2>结算账户审核</h2></div><button onClick={()=>setDialog(null)}>×</button></div><div className="description-grid">{[['店铺',dialog.row[1]],['账户类型',dialog.row[2]],['收款名称',dialog.row[3]],['收款账号',dialog.row[4]]].map(([label,value])=><div key={label}><span>{label}</span><b>{value}</b></div>)}</div><label className="dialog-field"><span>审核备注</span><textarea placeholder="拒绝时必须填写审核原因"></textarea></label><div className="dialog-actions"><button onClick={()=>setDialog(null)}>关闭</button><button className="danger" onClick={()=>{setDialog(null);notify('账户已拒绝');}}>拒绝</button><button className="primary" onClick={()=>{setDialog(null);notify('账户审核通过');}}>通过</button></div></section></div>}{dialog?.type==='channel'&&<div className="dialog-mask" onClick={()=>setDialog(null)}><section className="admin-dialog channel-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">PAYMENT CHANNEL</span><h2>{dialog.editing?'编辑支付通道':'新增支付通道'}</h2></div><button onClick={()=>setDialog(null)}>×</button></div><div className="settlement-form-grid"><label><span>通道名称</span><input placeholder="请输入通道名称"/></label><label><span>收款模式</span><select defaultValue={dialog.mode}><option>平台普通商户直收</option><option>服务商代子商户收款</option><option>子商户路由</option></select></label><label><span>适用店铺</span><select><option>平台主通道</option><option>山野生活店</option></select></label><label><span>App 目标</span><input defaultValue="platform"/></label><label><span>应用 AppID</span><input placeholder="请输入应用 AppID"/></label><label><span>商户号</span><input placeholder="请输入商户号"/></label><label className="wide"><span>支付回调地址</span><input placeholder="请输入支付回调地址"/></label></div><div className="dialog-actions"><button onClick={()=>setDialog(null)}>取消</button><button className="primary" onClick={()=>{setDialog(null);notify('支付通道已保存');}}>保存</button></div></section></div>}</section>;
}

function AdminServicePage({ notify }) {
  const conversations=[
    {id:'cs-1',name:'周予安',preview:'请问订单今天可以发货吗？',time:'10:24'},
    {id:'cs-2',name:'林可',preview:'收到商品了，谢谢。',time:'昨天'},
    {id:'cs-3',name:'陈一禾',preview:'想咨询一下长续航版本。',time:'10/30'}
  ];
  const [active,setActive] = useState(conversations[0]);
  const [text,setText] = useState('');
  const [attachmentOpen,setAttachmentOpen] = useState(false);
  const [messages,setMessages] = useState([
    {side:'them',type:'text',content:'您好，请问订单今天可以发货吗？',time:'10:16'},
    {side:'me',type:'text',content:'可以，预计下午四点前交给快递。',time:'10:18'},
    {side:'them',type:'image',content:'商品图片',time:'10:19'},
    {side:'me',type:'file',content:'售后说明.pdf',meta:'1.2 MB',time:'10:21'}
  ]);
  const send=()=>{if(!text.trim())return;setMessages([...messages,{side:'me',type:'text',content:text.trim(),time:'刚刚'}]);setText('');notify('消息已发送');};
  const sendAttachment=(type)=>{setMessages([...messages,{side:'me',type,content:type==='image'?'客服图片':type==='video'?'商品演示视频.mp4':'商品说明.pdf',meta:type==='video'?'18.6 MB':'1.2 MB',time:'刚刚'}]);setAttachmentOpen(false);notify(`${type==='image'?'图片':type==='video'?'视频':'文件'}已发送`);};
  return <section className="admin-page service-admin-page" data-screen-label="商家客服消息"><div className="service-console"><aside><label><AdminIcon name="search" size={16}/><input placeholder="搜索用户昵称或消息内容"/></label>{conversations.map((item)=><button key={item.id} className={active.id===item.id?'active':''} onClick={()=>setActive(item)}><Avatar name={item.name}/><span><b>{item.name}</b><small>{item.preview}</small></span><time>{item.time}</time></button>)}</aside><main><header><Avatar name={active.name}/><div><b>{active.name}</b><span>会员 ID 211094 · 订单咨询</span></div><StatusTicket tone="success">会话中</StatusTicket><button onClick={()=>notify('会话已标记完成')}>结束会话</button></header><div className="service-chat">{messages.map((message,index)=><article className={`service-message ${message.side}`} key={index}><Avatar name={message.side==='me'?'野':active.name} size={32}/><span><small>{message.side==='me'?'山野生活店':active.name} · {message.time}</small><div className={`service-bubble type-${message.type}`}>{message.type==='image'?<ProductArt type="serum"/>:message.type==='video'?<><span className="service-file-icon"><AdminIcon name="video" size={18}/></span><span><b>{message.content}</b><small>{message.meta}</small></span></>:message.type==='file'?<><span className="service-file-icon"><AdminIcon name="file" size={18}/></span><span><b>{message.content}</b><small>{message.meta}</small></span></>:message.content}</div></span></article>)}</div><footer className="service-input-bar"><div className="service-attachment-wrap"><button className="service-attachment-button" onClick={()=>setAttachmentOpen(!attachmentOpen)}><AdminIcon name="plus" size={18}/></button>{attachmentOpen&&<div className="service-attachment-menu"><button onClick={()=>sendAttachment('image')}><AdminIcon name="image" size={17}/>图片</button><button onClick={()=>sendAttachment('video')}><AdminIcon name="video" size={17}/>视频</button><button onClick={()=>sendAttachment('file')}><AdminIcon name="file" size={17}/>文件</button></div>}</div><input value={text} onChange={(e)=>setText(e.target.value)} onKeyDown={(e)=>e.key==='Enter'&&send()} placeholder="输入回复内容"/><button className="service-send-button" onClick={send}>发送</button></footer></main></div></section>;
}

function adminRouteTitle(surface, route) {
  const page = routePath(route).split('/')[1] || 'overview';
  const names = {
    overview:'经营概览', merchants:'商家管理', shops:'店铺管理', members:'会员管理', reviews:'评价管理', points:'积分管理',
    'points-mall':'积分商城', seckill:'秒杀管理', groupbuy:'拼团管理', content:'首页运营', feedback:'意见反馈',
    products:'SPU 管理', sku:'SKU 管理', categories:surface==='platform'?'平台分类':'商品分类', specs:'规格管理', banner:'轮播图',
    orders:'订单管理', coupons:'优惠券管理', shipping:'物流设置', service:'客服消息', settings:'店铺设置', settlement:'结算管理', system:'系统管理'
  };
  return routeParam(route,'tab') || names[page] || '商城管理';
}

function AdminPrototype({ surface, route, navigate, notify }) {
  const page = routePath(route).split('/')[1] || 'overview';
  const [members, setMembers] = useState(MOCKS.members);
  const [orders, setOrders] = useState(MOCKS.orders);
  const [sidebarCollapsed,setSidebarCollapsed] = useState(false);
  const [pageTabs,setPageTabs] = useState([{route:`${surface}/overview`,title:'经营概览'}]);
  useEffect(()=>setPageTabs([{route:`${surface}/overview`,title:'经营概览'}]),[surface]);
  useEffect(()=>{
    setPageTabs((current)=>current.some((item)=>item.route===route) ? current : [...current,{route,title:adminRouteTitle(surface,route)}]);
  },[route,surface]);
  const closeTab=(target)=>{
    const index=pageTabs.findIndex((item)=>item.route===target);
    const next=pageTabs.filter((item)=>item.route!==target);
    setPageTabs(next.length?next:[{route:`${surface}/overview`,title:'经营概览'}]);
    if(target===route){
      const fallback=next[index] || next[index-1] || {route:`${surface}/overview`};
      navigate(fallback.route);
    }
  };
  const closeOtherTabs=()=>setPageTabs((current)=>current.filter((item)=>item.route===route));
  const closeAllTabs=()=>{setPageTabs([{route:`${surface}/overview`,title:'经营概览'}]);navigate(`${surface}/overview`);};
  let content;
  if (page === 'overview') content = <AdminOverview surface={surface} navigate={navigate}/>;
  else if (surface === 'platform' && page === 'merchants') content = <PlatformMerchants notify={notify}/>;
  else if (surface === 'platform' && page === 'shops') content = <PlatformShops notify={notify}/>;
  else if (surface === 'platform' && page === 'members') content = <AdminMembers members={members} setMembers={setMembers} notify={notify} navigate={navigate}/>;
  else if (surface === 'platform' && page === 'reviews') content = <AdminReviews notify={notify}/>;
  else if (surface === 'platform' && page === 'points') content = <AdminPoints route={route} notify={notify}/>;
  else if (surface === 'platform' && page === 'points-mall') content = <AdminPointsMall route={route} notify={notify}/>;
  else if (surface === 'platform' && page === 'content') content = <AdminHomeOperation route={route} notify={notify}/>;
  else if (surface === 'platform' && page === 'seckill') content = <AdminSeckillPage route={route} navigate={navigate} notify={notify}/>;
  else if (surface === 'platform' && page === 'feedback') content = <AdminFeedback notify={notify}/>;
  else if (surface === 'merchant' && page === 'orders') content = <AdminOrders orders={orders} setOrders={setOrders} notify={notify}/>;
  else if (surface === 'merchant' && page === 'products') content = <MerchantProducts notify={notify}/>;
  else if (surface === 'merchant' && page === 'service') content = <AdminServicePage notify={notify}/>;
  else if (surface === 'merchant' && page === 'settings') content = <MerchantSettingsPage notify={notify}/>;
  else if (page === 'settlement') content = <AdminSettlementPage surface={surface} route={route} navigate={navigate} notify={notify}/>;
  else if (surface === 'platform' && page === 'categories') content = <AdminPlatformCategories notify={notify}/>;
  else if (surface === 'platform' && page === 'system') content = <FrameworkReferencePage route={route} notify={notify}/>;
  else content = <AdminModulePage surface={surface} page={page} route={route} navigate={navigate} notify={notify}/>;
  return (
    <div className={`admin-shell admin-shell-light${sidebarCollapsed?' is-collapsed':''}`} data-screen-label={surface === 'platform' ? '平台后台' : '商家后台'}>
      <AdminSidebar surface={surface} route={route} navigate={navigate} collapsed={sidebarCollapsed}/>
      <div className="admin-workspace"><AdminTopbar surface={surface} page={page} route={route} navigate={navigate} notify={notify} collapsed={sidebarCollapsed} onToggleSidebar={()=>setSidebarCollapsed(!sidebarCollapsed)}/><AdminTabs tabs={pageTabs} route={route} navigate={navigate} onClose={closeTab} onCloseOthers={closeOtherTabs} onCloseAll={closeAllTabs}/>{content}</div>
    </div>
  );
}

function PrototypeUtilityDialog({ value, onClose, onFinish }) {
  const [selectedAsset,setSelectedAsset] = useState(0);
  const [selectedLogo,setSelectedLogo] = useState(0);
  const [selectedSpecs,setSelectedSpecs] = useState(['雾银','曜石黑','标准版','长续航版','128G','256G']);
  useEffect(()=>{setSelectedAsset(0);setSelectedLogo(0);setSelectedSpecs(['雾银','曜石黑','标准版','长续航版','128G','256G']);},[value]);
  if (!value) return null;
  const sku = value.includes('SKU 管理');
  const media = value.includes('图片') || value.includes('轮播');
  const spec = value.includes('规格选择器');
  const logo = value.includes('Logo');
  const log = value.includes('操作日志');
  const batch = value.includes('批量操作');
  const title = sku ? 'SKU 管理' : logo ? '选择店铺 Logo' : media ? '商品图片管理' : spec ? '选择商品规格' : log ? '操作记录' : batch ? '批量操作' : '业务操作';
  return <div className="dialog-mask" onClick={onClose}><section className="admin-dialog utility-dialog" onClick={(event)=>event.stopPropagation()}><div className="dialog-head"><div><span className="drawer-kicker">INTERACTIVE PROTOTYPE</span><h2>{title}</h2></div><button onClick={onClose}>×</button></div>
    {sku&&<><div className="dialog-hint"><AdminIcon name="box" size={17}/><span>规格、售价和库存均可在此维护。</span></div><table data-align-table="t28" className="mini-data-table"><thead><tr><th>规格组合</th><th>销售价</th><th>库存</th><th>状态</th></tr></thead><tbody><tr><td><div className="cb">雾银 / 标准版</div></td><td><div className="cb"><input defaultValue="299.00"/></div></td><td><div className="cb"><input defaultValue="18"/></div></td><td><div className="cb"><StatusTicket tone="success">启用</StatusTicket></div></td></tr><tr><td><div className="cb">曜石黑 / 长续航</div></td><td><div className="cb"><input defaultValue="369.00"/></div></td><td><div className="cb"><input defaultValue="12"/></div></td><td><div className="cb"><StatusTicket tone="success">启用</StatusTicket></div></td></tr></tbody></table></>}
    {media&&<><div className="asset-picker-grid">{['headphone','serum','coffee','chair'].map((art,index)=><button className={index===selectedAsset?'selected':''} key={art} onClick={()=>setSelectedAsset(index)}><ProductArt type={art}/><span>{index===0?'当前主图':`轮播图 ${index}`}</span>{index===selectedAsset&&<i><AdminIcon name="check" size={14}/></i>}</button>)}<button className="asset-upload-tile" onClick={()=>onFinish('新图片已加入资源列表')}><AdminIcon name="plus" size={22}/><span>上传新图片</span><small>JPG / PNG</small></button></div><p className="utility-tip">选择图片后可设置为主图，轮播图支持拖动排序。</p></>}
    {spec&&<><div className="spec-picker-list">{[['颜色',['雾银','曜石黑','奶油白']],['版本',['标准版','长续航版']],['容量',['128G','256G']]].map(([name,options])=><section key={name}><b>{name}</b><div>{options.map((option)=><button className={selectedSpecs.includes(option)?'selected':''} key={option} onClick={()=>setSelectedSpecs(selectedSpecs.includes(option)?selectedSpecs.filter((item)=>item!==option):[...selectedSpecs,option])}><i>{selectedSpecs.includes(option)?'✓':''}</i>{option}</button>)}</div></section>)}</div></>}
    {logo&&<><div className="logo-picker-grid">{['野','山','Y','店'].map((item,index)=><button className={index===selectedLogo?'selected':''} key={item} onClick={()=>setSelectedLogo(index)}><span>{item}</span><small>{index===0?'当前 Logo':'备选样式'}</small></button>)}<button className="logo-upload-tile" onClick={()=>onFinish('店铺 Logo 已上传')}><AdminIcon name="image" size={20}/><span>上传图片</span></button></div><p className="utility-tip">建议使用 200 × 200 的正方形图片。</p></>}
    {log&&<div className="operation-timeline"><div><i></i><span><b>记录已更新</b><small>平台管理员 · 2026-11-02 10:18</small></span></div><div><i></i><span><b>状态变更为启用</b><small>系统自动处理 · 2026-11-01 16:40</small></span></div><div><i></i><span><b>记录已创建</b><small>运营人员 · 2026-10-28 09:12</small></span></div></div>}
    {batch&&<div className="batch-action-panel"><p>选择要执行的批量操作</p><label><input type="checkbox" defaultChecked/>云感降噪头戴耳机</label><label><input type="checkbox" defaultChecked/>白茶屏障修护精华</label><label><input type="checkbox"/>山野拼配挂耳咖啡</label><select defaultValue="批量上架"><option>批量上架</option><option>批量下架</option><option>批量移动分类</option></select></div>}
    <div className="dialog-actions"><button onClick={onClose}>取消</button>{!log&&<button className="primary" onClick={()=>onFinish(`${title}已保存`)}>确认保存</button>}</div>
  </section></div>;
}

function PrototypeApp() {
  const [route, navigate] = usePrototypeRoute();
  const initialSurface = route.split('/')[0];
  const [surface, setSurface] = useState(['app','shop-app','platform','merchant'].includes(initialSurface) ? initialSurface : 'app');
  const [toast, setToast] = useState('');
  const [utilityDialog,setUtilityDialog] = useState('');
  useEffect(() => {
    const next = route.split('/')[0];
    if (['app','shop-app','platform','merchant'].includes(next)) setSurface(next);
  }, [route]);
  useEffect(() => {
    const handlePrototypeAction = (event) => {
      if (event.target.closest?.('.product-main-image')) setUtilityDialog('图片管理抽屉已打开');
      if (event.target.closest?.('.upload-placeholder')) setUtilityDialog('图片管理抽屉已打开');
      if (event.target.closest?.('.spec-chip-row button.active')) setUtilityDialog('规格选择器已打开');
    };
    document.addEventListener('click', handlePrototypeAction);
    return () => document.removeEventListener('click', handlePrototypeAction);
  }, []);
  const showToast = (message) => {
    setToast(message);
    window.clearTimeout(window.__yeahMallToastTimer);
    window.__yeahMallToastTimer = window.setTimeout(() => setToast(''), 2200);
  };
  const notify = (message) => {
    if (/(SKU 管理|图片管理抽屉|请选择轮播图片|规格选择器|店铺 Logo|操作日志|批量操作)/.test(message)) {
      setUtilityDialog(message);
      return;
    }
    showToast(message);
  };
  return (
    <div className={`prototype-root surface-${surface}`}>
      <PrototypeToolbar surface={surface} setSurface={setSurface} route={route} navigate={navigate}/>
      <div className="prototype-content">
        {surface === 'app' || surface === 'shop-app'
          ? <AppPrototype route={route} navigate={navigate} notify={notify} appMode={surface === 'shop-app' ? 'shop' : 'platform'}/>
          : <AdminPrototype surface={surface} route={route} navigate={navigate} notify={notify}/>}
      </div>
      <PrototypeUtilityDialog value={utilityDialog} onClose={()=>setUtilityDialog('')} onFinish={(message)=>{setUtilityDialog('');showToast(message);}}/>
      <Toast message={toast}/>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<PrototypeApp/>);
