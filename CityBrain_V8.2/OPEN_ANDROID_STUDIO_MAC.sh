#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
open -a "Android Studio" "$ROOT_DIR/android"
