#!/usr/bin/env bash
#
# Builds a signed release APK + AAB for the MyTube app.
#
#   ./scripts/build-release.sh              # release APK + AAB (bundle)
#   ./scripts/build-release.sh apk          # release APK only
#   ./scripts/build-release.sh aab          # release AAB only
#
# Signing:
#   * If KEYSTORE_PATH / STORE_PASSWORD / KEY_PASSWORD are already exported,
#     they are used as-is (the alias must be "upload").
#   * Otherwise a keystore is created at <repo root>/my-upload-key.jks with the
#     alias "upload" and passwords stored in the git-ignored file
#     <repo root>/release-keystore.env so the same key is reused next time.
#     KEEP THAT KEYSTORE SAFE - Play Store updates must be signed with the
#     exact same upload key.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

TASKS=("assembleRelease" "bundleRelease")
case "${1:-all}" in
  apk) TASKS=("assembleRelease") ;;
  aab) TASKS=("bundleRelease") ;;
  all) ;;
  *) echo "Usage: $0 [apk|aab]" >&2; exit 2 ;;
esac

# --- Java -----------------------------------------------------------------
if ! command -v java > /dev/null 2>&1; then
  echo "error: no JDK on PATH. AGP 9.1.1 needs JDK 17+ (JDK 21 recommended)." >&2
  exit 1
fi

# --- Android SDK ----------------------------------------------------------
if [ -z "${ANDROID_HOME:-}" ] && [ -z "${ANDROID_SDK_ROOT:-}" ]; then
  for candidate in "$HOME/Android/Sdk" "$HOME/Library/Android/sdk" "/usr/local/android-sdk"; do
    if [ -d "$candidate" ]; then export ANDROID_HOME="$candidate"; break; fi
  done
fi
SDK_DIR="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -n "$SDK_DIR" ]; then
  if [ ! -f "$ROOT_DIR/local.properties" ]; then
    echo "sdk.dir=$SDK_DIR" > "$ROOT_DIR/local.properties"
    echo "wrote local.properties (sdk.dir=$SDK_DIR)"
  fi
else
  echo "warning: ANDROID_HOME not set - relying on local.properties or an SDK on PATH." >&2
fi

# --- Signing --------------------------------------------------------------
KEYSTORE_FILE="${KEYSTORE_PATH:-$ROOT_DIR/my-upload-key.jks}"
ENV_FILE="$ROOT_DIR/release-keystore.env"

if [ -z "${STORE_PASSWORD:-}" ] && [ -f "$ENV_FILE" ]; then
  # shellcheck disable=SC1090
  set -a; . "$ENV_FILE"; set +a
fi

if [ ! -f "$KEYSTORE_FILE" ]; then
  STORE_PASSWORD="${STORE_PASSWORD:-$(openssl rand -hex 16)}"
  KEY_PASSWORD="${KEY_PASSWORD:-$(openssl rand -hex 16)}"
  keytool -genkeypair -v \
    -keystore "$KEYSTORE_FILE" \
    -alias upload \
    -keyalg RSA -keysize 2048 \
    -validity 10950 \
    -storepass "$STORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=MyTube, OU=Mobile, O=MyTube, L=Dhaka, S=Dhaka, C=BD"
  cat > "$ENV_FILE" <<EOF
KEYSTORE_PATH=$KEYSTORE_FILE
STORE_PASSWORD=$STORE_PASSWORD
KEY_PASSWORD=$KEY_PASSWORD
EOF
  chmod 600 "$ENV_FILE"
  echo "created keystore $KEYSTORE_FILE (alias: upload)"
  echo "passwords saved to $ENV_FILE - keep both files secret and back them up."
fi

export KEYSTORE_PATH="$KEYSTORE_FILE"
export STORE_PASSWORD KEY_PASSWORD

# --- Build ----------------------------------------------------------------
./gradlew --stacktrace "${TASKS[@]}"

echo
echo "Output:"
find app/build/outputs/apk/release -name '*.apk' 2>/dev/null | sed 's/^/  /'
find app/build/outputs/bundle/release -name '*.aab' 2>/dev/null | sed 's/^/  /'
