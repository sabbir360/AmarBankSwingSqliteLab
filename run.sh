#!/usr/bin/env bash
# Compile and run AmarBank on macOS/Linux (and Git Bash on Windows).
# Downloads the SQLite JDBC driver into lib/ if it is missing, then
# compiles with javac and runs with java. No build tool required.
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src/main/java"
LIB_DIR="$PROJECT_DIR/lib"
OUT_DIR="$PROJECT_DIR/out"
MAIN_CLASS="com.amarbank.Main"

SQLITE_JDBC_VERSION="3.36.0"
SQLITE_JDBC_URL="https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/${SQLITE_JDBC_VERSION}/sqlite-jdbc-${SQLITE_JDBC_VERSION}.jar"

# The xerial sqlite-jdbc jar bundles native libraries for every OS/arch,
# so a single jar works on macOS, Linux and Windows. Keeping one fixed path
# also lets the committed IntelliJ module reference it on any OS.
JAR="$LIB_DIR/sqlite-jdbc.jar"

classpath_separator() {
  case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) echo ";" ;;
    *) echo ":" ;;
  esac
}

download_jdbc_jar() {
  local jar_path="$1"
  mkdir -p "$(dirname "$jar_path")"
  if command -v curl >/dev/null 2>&1; then
    echo "Downloading SQLite JDBC ${SQLITE_JDBC_VERSION}..."
    curl -fsSL "$SQLITE_JDBC_URL" -o "$jar_path"
  elif command -v wget >/dev/null 2>&1; then
    echo "Downloading SQLite JDBC ${SQLITE_JDBC_VERSION}..."
    wget -q "$SQLITE_JDBC_URL" -O "$jar_path"
  else
    echo "Neither curl nor wget is available to download: $SQLITE_JDBC_URL" >&2
    exit 1
  fi
}

if [ ! -f "$JAR" ]; then
  download_jdbc_jar "$JAR"
else
  echo "Using SQLite JDBC: $JAR"
fi

CP_SEP="$(classpath_separator)"
mkdir -p "$OUT_DIR" "$PROJECT_DIR/db_file"

echo "Compiling..."
SOURCES_FILE="$(mktemp)"
find "$SRC_DIR" -name "*.java" > "$SOURCES_FILE"
javac -cp "$JAR" -d "$OUT_DIR" "@$SOURCES_FILE"
rm -f "$SOURCES_FILE"

echo "Running..."
cd "$PROJECT_DIR"
java --enable-native-access=ALL-UNNAMED -cp "${OUT_DIR}${CP_SEP}${JAR}" "$MAIN_CLASS"
