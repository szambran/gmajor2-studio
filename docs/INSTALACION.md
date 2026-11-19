# Instalación y primeros pasos

## Linux

### JAR

Instala Java 11 o posterior y ejecuta:

```bash
java -jar GMajor2Studio-1.0.0-beta.jar
```

O compila desde el código:

```bash
./run.sh
```

### Aplicación autocontenida

El directorio `G-Major-2-Studio/` incluye un runtime Java y no necesita una
instalación de Java adicional. Ejecuta:

```bash
./G-Major-2-Studio/bin/G-Major-2-Studio
```

## Windows

Instala Java 11 o posterior y ejecuta el JAR con doble clic o desde una
terminal. La interfaz MIDI debe aparecer como dispositivo compatible con
`javax.sound.midi`.

## Conexión MIDI

El G-Major 2 no tiene USB propio. Usa una interfaz MIDI con dos cables:

- interfaz MIDI OUT → G-Major 2 MIDI IN;
- G-Major 2 MIDI OUT → interfaz MIDI IN.

En la aplicación selecciona la misma interfaz en MIDI IN y MIDI OUT, pulsa
`Conectar` y usa `Recibir backup` antes de ejecutar `MIDI → Bulk Dump → ENTER`
en el equipo.
