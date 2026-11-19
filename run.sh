#!/usr/bin/env sh
set -eu
mkdir -p build
javac --release 11 -encoding UTF-8 -d build $(find src -name '*.java' -print)
exec java -cp build com.gmajor2.midi.GMajor2Controller
