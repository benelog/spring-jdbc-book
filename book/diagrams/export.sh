#!/usr/bin/env bash
# book/diagrams/*.drawio 를 book/images/*.png 로 내보낸다.
# draw.io 데스크톱(snap: drawio)과 xvfb-run 이 필요하다. snap 샌드박스 때문에 /tmp 경로는 읽지 못한다.
set -euo pipefail
cd "$(dirname "$0")"
for f in *.drawio; do
  n="${f%.drawio}"
  xvfb-run -a drawio --no-sandbox --disable-gpu -x -f png -s 2 -b 12 -o "../images/$n.png" "$f" 2>/dev/null | grep -e '->' || true
done
