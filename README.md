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
