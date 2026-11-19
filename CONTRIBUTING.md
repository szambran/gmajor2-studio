# Contribuir a G-Major 2 Studio

Las pruebas y mejoras son bienvenidas, especialmente con hardware G-Major 2
real y distintas interfaces USB-MIDI.

## Antes de probar

1. Haz un Bulk Dump completo y guarda una copia privada del archivo `.syx`.
2. Conecta MIDI OUT de la interfaz a MIDI IN del G-Major 2 y viceversa.
3. Desactiva `Send Tuner` durante las pruebas.
4. No pulses `STORE` salvo que quieras guardar físicamente un preset.

## Compilar

Se necesita Java 11 o posterior:

```bash
./run.sh
```

También se puede compilar sin iniciar la aplicación:

```bash
mkdir -p build
javac --release 11 -encoding UTF-8 -d build $(find src -name '*.java' -print)
```

## Informar de un problema

Incluye:

- sistema operativo y versión de Java;
- modelo de interfaz MIDI y puertos visibles;
- pasos exactos para reproducirlo;
- mensaje del Registro de la aplicación;
- si el problema ocurre al recibir, editar o enviar SysEx.

No adjuntes backups que contengan información personal sin revisarlos antes.
Un archivo `.syx` puede incluir nombres de presets creados por el usuario.
