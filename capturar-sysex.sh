#!/usr/bin/env sh
set -eu
PUERTO="${1:-hw:2,0,0}"
SALIDA="${2:-gmajor2-prueba.syx}"
echo "Escuchando $PUERTO durante 60 segundos."
echo "Ahora ejecuta MIDI > Bulk Dump > ENTER en el G-Major 2."
amidi --port "$PUERTO" --dump --timeout 60 > "$SALIDA"
echo "Archivo guardado en: $SALIDA"
wc -c "$SALIDA"
