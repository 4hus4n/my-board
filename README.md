# My Board

A dark finger-drawing board for kids, made as one HTML page.

- Pen, rainbow pen and eraser, adjustable size, 40 colours plus a custom colour
- Full screen and child lock (hold the lock for 2 seconds to unlock)
- Save the drawing as a PNG
- Letter tracing: ABC, abc, 123, Thaana and Arabic, with handwriting lines,
  auto next letter when traced, and a speaker button
- Always laid out in landscape; an upright phone shows it sideways

## Arabic letter sounds

The page plays `sounds/arabic/<name>.mp3` when the file exists (alif, ba, ta, tha, jiim, hha, kha,
daal, thaal, ra, zay, siin, shiin, saad, daad, taa, thaa, ayn, ghayn, fa, qaf, kaf, lam, miim, nuun,
ha, waw, ya). Otherwise it uses the built-in voice. The `sounds/` folder is not kept in this repo;
add it on the server.

## Dhivehi font

Thaana letters use MV Aammu FK when `fonts/MV_AammuFK_Regular.ttf` is on the server,
otherwise Noto Sans Thaana. The `fonts/` folder is not kept in this repo; add it on the server.

## Android app

Every push to `main` builds the Android app on GitHub (see `.github/workflows/android.yml`) and
publishes it as `my-board.apk` on the **apk** release:
https://github.com/4hus4n/my-board/releases/latest/download/my-board.apk

The app is in `android/`: a full-screen, landscape WebView that loads `index.html` with the fonts
bundled (works offline). Pictures are saved to Pictures/My Board. Back is blocked while the child
lock is on. The signing key is `android/app/myboard.keystore`; keep it, because Android only installs
updates signed with the same key.
