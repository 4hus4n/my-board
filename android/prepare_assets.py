"""Copy the web app into the Android assets, using the bundled fonts instead of Google Fonts."""
import pathlib, re
root = pathlib.Path(__file__).resolve().parent.parent
page = (root / 'index.html').read_text(encoding='utf-8')
page = re.sub(r'<link rel="preconnect"[^>]*>\s*', '', page)
page, n = re.subn(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^>]*>',
                  '<link rel="stylesheet" href="fonts/fonts.css">', page)
assert n == 1, 'Google Fonts link not found in index.html'
out = root / 'android' / 'app' / 'src' / 'main' / 'assets' / 'index.html'
out.write_text(page, encoding='utf-8')
print('wrote', out, len(page), 'bytes')
