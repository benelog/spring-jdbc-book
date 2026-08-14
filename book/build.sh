#!/usr/bin/env bash
# 원고를 HTML 한 파일로 렌더링한다. 필요: asciidoctor
set -euo pipefail
cd "$(dirname "$0")"
asciidoctor -o build/book.html book.adoc
mkdir -p build/images
cp -r images/. build/images/
echo "생성됨: $(pwd)/build/book.html"
