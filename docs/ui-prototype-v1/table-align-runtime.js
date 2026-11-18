/* 表格列容器归一化（原型端，与真实端 utils/table-align.ts 同一套规则）：
   同列所有数据行共用「本列实际内容最大宽度」的容器，容器在列内居中，容器内内容左对齐。
   宽度必须按当前真实渲染内容算，写死的样例宽度在内容更短时会让整列看起来偏左。 */
(function () {
  'use strict'

  var applying = false
  var frame = 0
  var retryTimer = 0
  var waitStart = 0
  var signatures = new WeakMap()
  var watched = new WeakSet()
  var resizeObserver = null
  var WAIT_LIMIT = 1500

  function collect() {
    var targets = []
    var tables = document.querySelectorAll('table[data-align-table]')
    for (var i = 0; i < tables.length; i += 1) {
      var tbody = tables[i].querySelector('tbody')
      if (tbody) targets.push({ scope: tables[i], tbody: tbody })
    }
    return targets
  }

  function isTypeColumn(th, td, cell) {
    var cls = (th ? th.className : '') + ' ' + td.className
    if (/selection-cell|--selection|--expand|--index|__expand-column/.test(cls)) return true
    var text = (cell.textContent || '').trim()
    return !text && !!cell.querySelector('input[type="checkbox"], input[type="radio"], .el-table__expand-icon')
  }

  /* 单元格内容可用宽度（avail）与盒模型 padding+border（box）。 */
  function measure(cell) {
    var cs = getComputedStyle(cell)
    var padL = parseFloat(cs.paddingLeft) || 0
    var padR = parseFloat(cs.paddingRight) || 0
    var borL = parseFloat(cs.borderLeftWidth) || 0
    var borR = parseFloat(cs.borderRightWidth) || 0
    var rect = cell.getBoundingClientRect()
    var left = rect.left + borL + padL
    var right = rect.right - borR - padR
    return {
      avail: Math.max(0, right - left),
      box: padL + padR + borL + borR,
    }
  }

  /* 视觉原子计数：一段文本/一张图/一个标签/一个开关/一个按钮 = 一个原子；
     上下堆叠（名称+描述、日期+时间）或横向多控件 = 多个原子；纯装饰（空圆点/占位）不计。
     CopyableText 是单个文本按钮，自然算一个原子。只需判断是否 >=2，提前退出。 */
  var ATOMIC_TAGS = { IMG:1, SVG:1, CANVAS:1, VIDEO:1, AUDIO:1, INPUT:1, SELECT:1, TEXTAREA:1, BUTTON:1, IFRAME:1, OBJECT:1, EMBED:1, PICTURE:1 }
  var ATOMIC_SELECTOR = '.avatar,.status-ticket,.merchant-account-status,.copyable-value,.copyable-text,.el-avatar,.el-image,.el-switch,.el-tag,.el-checkbox,.el-radio,.el-icon,.el-button'
  var MEDIA_SELECTOR = 'img,svg,canvas,video,input,button,select,textarea,.avatar,.el-avatar,.el-image,.el-switch,.el-checkbox,.el-radio,.el-icon'

  function isAtomic(el) {
    return !!ATOMIC_TAGS[el.tagName.toUpperCase()] || el.matches(ATOMIC_SELECTOR)
  }
  function isHiddenEl(el) {
    var cs = getComputedStyle(el)
    return cs.display === 'none' || cs.visibility === 'hidden' || cs.position === 'fixed'
  }
  function isEmptyDecoration(el) {
    if ((el.textContent || '').trim()) return false
    return !el.querySelector(MEDIA_SELECTOR)
  }
  function countAtoms(node) {
    var atoms = 0
    var children = node.childNodes
    for (var i = 0; i < children.length; i += 1) {
      var child = children[i]
      if (child.nodeType === Node.TEXT_NODE) {
        if ((child.textContent || '').trim()) atoms += 1
        continue
      }
      if (child.nodeType !== Node.ELEMENT_NODE) continue
      if (isHiddenEl(child)) continue
      if (isAtomic(child)) atoms += 1
      else if (!isEmptyDecoration(child)) atoms += countAtoms(child)
      if (atoms >= 2) return atoms
    }
    return atoms
  }

  /* 单行自然文本宽度（不受表格布局/换行影响），判断单元素列内容是否超长需截断。 */
  var textProbe = null
  function naturalTextWidth(cell) {
    var text = (cell.textContent || '').trim()
    if (!text) return 0
    if (!textProbe) {
      textProbe = document.createElement('span')
      textProbe.style.position = 'absolute'
      textProbe.style.visibility = 'hidden'
      textProbe.style.pointerEvents = 'none'
      textProbe.style.left = '-99999px'
      textProbe.style.top = '0'
      textProbe.style.whiteSpace = 'nowrap'
      textProbe.style.width = 'auto'
      textProbe.style.maxWidth = 'none'
      document.body.appendChild(textProbe)
    }
    var cs = getComputedStyle(cell)
    textProbe.style.fontSize = cs.fontSize
    textProbe.style.fontFamily = cs.fontFamily
    textProbe.style.fontWeight = cs.fontWeight
    textProbe.style.fontStyle = cs.fontStyle
    textProbe.style.letterSpacing = cs.letterSpacing
    textProbe.style.fontVariantNumeric = cs.fontVariantNumeric
    textProbe.style.textTransform = cs.textTransform
    textProbe.textContent = text
    return textProbe.getBoundingClientRect().width
  }

  /* 多元素列内容的自然宽度：克隆单元格内容到离屏探针，放开块级子元素的固定宽/max-width，
     让 flex 复合块收缩到内容真正需要的宽度。渲染态 extent 会被 max-width:100% 压到≈列内容宽，
     窄列里永远填满而无法居中；自然宽才能算出可居中的容器宽。 */
  var blockProbe = null
  function naturalContentWidth(cell) {
    if (!blockProbe) {
      blockProbe = document.createElement('div')
      blockProbe.style.position = 'absolute'
      blockProbe.style.visibility = 'hidden'
      blockProbe.style.pointerEvents = 'none'
      blockProbe.style.left = '-99999px'
      blockProbe.style.top = '0'
      blockProbe.style.whiteSpace = 'nowrap'
      blockProbe.style.width = 'auto'
      blockProbe.style.maxWidth = 'none'
      document.body.appendChild(blockProbe)
    }
    var cs = getComputedStyle(cell)
    blockProbe.style.fontSize = cs.fontSize
    blockProbe.style.fontFamily = cs.fontFamily
    blockProbe.style.fontWeight = cs.fontWeight
    blockProbe.style.fontStyle = cs.fontStyle
    blockProbe.style.letterSpacing = cs.letterSpacing
    blockProbe.style.fontVariantNumeric = cs.fontVariantNumeric
    blockProbe.style.textTransform = cs.textTransform
    blockProbe.style.lineHeight = cs.lineHeight
    blockProbe.innerHTML = ''
    var has = false
    var children = cell.childNodes
    for (var i = 0; i < children.length; i += 1) {
      var child = children[i]
      if (child.nodeType === Node.TEXT_NODE && !(child.textContent || '').trim()) continue
      var clone = child.cloneNode(true)
      if (clone.nodeType === Node.ELEMENT_NODE) {
        clone.style.width = 'auto'
        clone.style.maxWidth = 'none'
      }
      blockProbe.appendChild(clone)
      has = true
    }
    return has ? blockProbe.getBoundingClientRect().width : 0
  }

  function normalize(target) {
    var scope = target.scope
    var tbody = target.tbody
    var all = tbody.children
    var rows = []
    for (var i = 0; i < all.length; i += 1) {
      if (all[i].tagName === 'TR' && all[i].getClientRects().length) rows.push(all[i])
    }
    if (!rows.length) return

    var columnCount = rows[0].children.length
    var headers = scope.querySelectorAll('thead th')
    var columns = []
    var typeColumn = []
    var singleColumn = []
    for (var k = 0; k < columnCount; k += 1) {
      columns.push([])
      typeColumn.push(false)
      singleColumn.push(false)
    }

    for (var r = 0; r < rows.length; r += 1) {
      var row = rows[r]
      if (row.children.length !== columnCount) continue
      var rowRect = row.getBoundingClientRect()
      if (rowRect.width <= 0 || rowRect.height <= 0) continue
      for (var c = 0; c < columnCount; c += 1) {
        var td = row.children[c]
        if (td.tagName !== 'TD') continue
        var cell = td.querySelector(':scope > .cb')
        if (!cell) continue
        columns[c].push(cell)
        if (!typeColumn[c]) typeColumn[c] = isTypeColumn(headers[c] || null, td, cell)
      }
    }

    /* 列分类：本列任意一行 >=2 个视觉原子 → 多元素列；否则单元素列（纯居中，超长截断）。 */
    for (var s = 0; s < columnCount; s += 1) {
      if (typeColumn[s] || !columns[s].length) continue
      var multi = false
      for (var t = 0; t < columns[s].length; t += 1) {
        if (countAtoms(columns[s][t]) >= 2) { multi = true; break }
      }
      singleColumn[s] = !multi
    }

    /* 测量前必须先放开宽度：带着上一次的固定宽去量会收缩反馈成越来越小的假值。 */
    for (var a = 0; a < columnCount; a += 1) {
      if (typeColumn[a]) continue
      for (var b = 0; b < columns[a].length; b += 1) {
        if (columns[a][b].style.width !== 'auto') columns[a][b].style.width = 'auto'
      }
    }
    void tbody.offsetWidth

    /* 多元素列：同列共用本列内容自然最大宽度的容器，容器列内居中、内容左对齐。 */
    var widths = new Array(columnCount).fill(null)
    for (var m = 0; m < columnCount; m += 1) {
      if (typeColumn[m] || singleColumn[m] || !columns[m].length) continue
      var natural = 0
      var avail = Infinity
      var box = 0
      for (var n = 0; n < columns[m].length; n += 1) {
        var mm = measure(columns[m][n])
        var nat = naturalContentWidth(columns[m][n])
        if (nat > natural) natural = nat
        if (mm.avail < avail) avail = mm.avail
        box = mm.box
      }
      if (!isFinite(avail)) continue
      var content = Math.min(Math.ceil(natural), avail)
      widths[m] = content >= avail - 0.5 ? 'auto' : content + box + 'px'
    }

    /* 单元素列：放得下 → 纯居中；放不下 → 截断省略号 + title 完整内容 + 左对齐。 */
    for (var u = 0; u < columnCount; u += 1) {
      if (typeColumn[u] || !singleColumn[u] || !columns[u].length) continue
      var sAvail = Infinity
      var sBox = 0
      for (var v = 0; v < columns[u].length; v += 1) {
        var sm = measure(columns[u][v])
        if (sm.avail < sAvail) sAvail = sm.avail
        sBox = sm.box
      }
      if (!isFinite(sAvail)) continue
      for (var w = 0; w < columns[u].length; w += 1) {
        var sc = columns[u][w]
        if (naturalTextWidth(sc) > sAvail + 1) {
          sc.setAttribute('data-ym-align', 'trunc')
          sc.style.width = Math.floor(sAvail) + sBox + 'px'
          var full = (sc.textContent || '').trim()
          if (full) { sc.setAttribute('title', full); sc.setAttribute('data-ym-title', '1') }
        } else {
          sc.setAttribute('data-ym-align', 'center')
          sc.style.width = 'auto'
          if (sc.getAttribute('data-ym-title') === '1') { sc.removeAttribute('title'); sc.removeAttribute('data-ym-title') }
        }
      }
    }

    for (var p = 0; p < columnCount; p += 1) {
      if (typeColumn[p] || singleColumn[p]) continue
      var width = widths[p] || 'auto'
      for (var q = 0; q < columns[p].length; q += 1) {
        if (columns[p][q].style.width !== width) columns[p][q].style.width = width
        if (columns[p][q].getAttribute('data-ym-align') !== 'multi') columns[p][q].setAttribute('data-ym-align', 'multi')
        if (columns[p][q].getAttribute('data-ym-title') === '1') { columns[p][q].removeAttribute('title'); columns[p][q].removeAttribute('data-ym-title') }
      }
    }
  }

  /* 过渡/动画未走完时，computed style 仍是起始值（例如 font-weight 700→400），
     此刻量到的文字宽度偏大，会把容器定宽成错误的值，必须等动画结束再量。
     但只改 opacity/颜色的动画（loading 遮罩淡入）不影响文字尺寸，
     等它会让遮罩下的行一直停在未归一化的满宽状态。 */
  var NON_GEOMETRIC = {
    opacity: 1, color: 1, backgroundColor: 1,
    borderTopColor: 1, borderRightColor: 1, borderBottomColor: 1, borderLeftColor: 1,
    boxShadow: 1, outlineColor: 1, fill: 1, stroke: 1, filter: 1, backdropFilter: 1
  }
  var KEYFRAME_META = { offset: 1, computedOffset: 1, easing: 1, composite: 1 }

  function changesGeometry(anim) {
    var effect = anim.effect
    if (!effect || typeof effect.getKeyframes !== 'function') return true
    var frames = effect.getKeyframes()
    for (var i = 0; i < frames.length; i += 1) {
      var keys = Object.keys(frames[i])
      for (var j = 0; j < keys.length; j += 1) {
        if (KEYFRAME_META[keys[j]]) continue
        if (!NON_GEOMETRIC[keys[j]]) return true
      }
    }
    return false
  }

  function pendingAnimations(scope) {
    if (typeof document.getAnimations !== 'function') return []
    var out = []
    var list = document.getAnimations()
    for (var i = 0; i < list.length; i += 1) {
      var anim = list[i]
      var effect = anim.effect
      var target = effect && effect.target
      if (!target || target.nodeType !== 1) continue
      if (!scope.contains(target) && !target.contains(scope)) continue
      if (anim.playState !== 'running') continue
      var timing = effect.getComputedTiming ? effect.getComputedTiming() : null
      if (timing && !isFinite(timing.endTime)) continue
      if (!changesGeometry(anim)) continue
      out.push(anim)
    }
    return out
  }

  function signature(target) {
    var first = target.tbody.children[0]
    var cols = []
    if (first) {
      for (var i = 0; i < first.children.length; i += 1) {
        cols.push(first.children[i].getBoundingClientRect().width.toFixed(1))
      }
    }
    var text = []
    var rows = target.tbody.children
    for (var j = 0; j < rows.length; j += 1) text.push(rows[j].textContent || '')
    return target.scope.getBoundingClientRect().width.toFixed(1) + '|' + cols.join(',') + '|' + text.join('\u0001')
  }

  function watch(el) {
    if (watched.has(el) || !resizeObserver) return
    watched.add(el)
    resizeObserver.observe(el)
  }

  function run(force) {
    applying = true
    var pending = []
    var waiting = []
    try {
      var targets = collect()
      for (var i = 0; i < targets.length; i += 1) {
        watch(targets[i].scope)
        watch(targets[i].tbody)
        var anims = force ? [] : pendingAnimations(targets[i].scope)
        if (anims.length && (!waitStart || performance.now() - waitStart < WAIT_LIMIT)) {
          if (!waitStart) waitStart = performance.now()
          waiting = waiting.concat(anims)
          continue
        }
        var sig = signature(targets[i])
        if (!force && signatures.get(targets[i].tbody) === sig) continue
        normalize(targets[i])
        signatures.set(targets[i].tbody, sig)
      }
    } finally {
      pending = observer.takeRecords()
      applying = false
      if (pending.length) schedule()
    }
    if (waiting.length) {
      Promise.all(waiting.map(function (a) { return a.finished })).then(schedule, schedule)
      if (!retryTimer) {
        retryTimer = setTimeout(function () {
          retryTimer = 0
          schedule()
        }, 120)
      }
    } else {
      waitStart = 0
    }
  }

  function schedule() {
    if (applying || frame) return
    frame = requestAnimationFrame(function () {
      frame = 0
      run()
    })
  }

  var observer = new MutationObserver(schedule)
  observer.observe(document.body, { childList: true, subtree: true, characterData: true })
  resizeObserver = new ResizeObserver(schedule)
  window.addEventListener('resize', schedule)
  if (document.fonts && document.fonts.ready) document.fonts.ready.then(schedule)
  schedule()

  window.__ymTableAlign = { run: run, schedule: schedule }
})()