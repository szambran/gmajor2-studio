#!/usr/bin/env sh
set -eu
APP="GMajor2-MIDI.AppDir"
rm -rf "$APP"
mkdir -p "$APP/usr/bin" "$APP/usr/share/applications" "$APP/usr/share/icons/hicolor/256x256/apps"
mkdir -p build
javac -encoding UTF-8 -d build src/com/gmajor2/midi/GMajor2Controller.java
cp -r build "$APP/usr/"
cat > "$APP/usr/bin/gmajor2-midi" <<'EOF'
#!/usr/bin/env sh
exec java -cp "$(dirname "$0")/../build" com.gmajor2.midi.GMajor2Controller
EOF
chmod +x "$APP/usr/bin/gmajor2-midi"
cat > "$APP/gmajor2-midi.desktop" <<'EOF'
[Desktop Entry]
Name=G-Major 2 MIDI
Comment=Backup y control MIDI del TC Electronic G-Major 2
Exec=gmajor2-midi
Type=Application
Categories=AudioVideo;Audio;Midi;
Terminal=false
EOF
cp "$APP/gmajor2-midi.desktop" "$APP/usr/share/applications/"
if ! command -v appimagetool >/dev/null 2>&1; then
  echo "Falta appimagetool. AppDir creada en $APP" >&2
  exit 1
fi
appimagetool "$APP" GMajor2-MIDI-x86_64.AppImage
