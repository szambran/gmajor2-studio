# Historial de cambios

## 1.0.0-beta — 19 de noviembre de 2026

Primera beta pública de G-Major 2 Studio.

- Editor y biblioteca SysEx para TC Electronic G-Major 2.
- Recepción y guardado de Bulk Dump.
- Restauración manual de archivos `.syx`.
- Lista ordenada de las 100 posiciones User.
- Preparación de un preset para una posición User concreta sin ejecutar `STORE`.
- Editor de nombre y parámetros de preset.
- Vista completa por bloques: Compressor, Filter, Pitch, MOD, Delay, Reverb,
  Gate, EQ y Preset.
- Setup Global, MIDI, MOD y Levels/Routing.
- Interfaz en español e inglés.
- Compatible con Java 11 o posterior.
- Probado en Debian 13 con interfaz UMC404HD.

### Limitaciones conocidas

- Algunos parámetros específicos de algoritmo y modificadores requieren
  validación adicional con hardware real.
- El equipo no envía posiciones User vacías durante el Bulk Dump; la aplicación
  las muestra como vacías/no recibidas.
- El paquete Linux autocontenido se genera en Linux; Windows requiere ejecutar
  el JAR con Java 11 o posterior.
