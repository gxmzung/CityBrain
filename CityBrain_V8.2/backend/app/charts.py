def bar_chart_svg(items, width=540, height=250, title=""):
    max_val = max([v for _, v in items], default=1)
    left = 140
    top = 34
    row_h = 42
    bar_max = width - left - 34
    parts = [f'<svg class="chart" width="100%" height="{height}" viewBox="0 0 {width} {height}" xmlns="http://www.w3.org/2000/svg">']
    parts.append(f'<text x="0" y="18" font-size="16" font-weight="800" fill="#16233A">{title}</text>')
    for i, (label, value) in enumerate(items):
        y = top + i * row_h
        bar_w = (value / max_val) * bar_max if max_val else 0
        safe_label = str(label).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        parts.append(f'<text x="0" y="{y+14}" font-size="12" fill="#687386">{safe_label}</text>')
        parts.append(f'<rect x="{left}" y="{y}" width="{bar_max}" height="18" rx="9" fill="#EEF1F6"/>')
        parts.append(f'<rect x="{left}" y="{y}" width="{bar_w}" height="18" rx="9" fill="#F59F3A"/>')
        parts.append(f'<text x="{left+bar_w+8}" y="{y+14}" font-size="12" font-weight="700" fill="#16233A">{value}</text>')
    parts.append("</svg>")
    return "".join(parts)
