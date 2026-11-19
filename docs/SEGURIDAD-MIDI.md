# Seguridad y buenas prácticas MIDI

G-Major 2 Studio puede enviar SysEx que modifica presets o configuración.

- Guarda siempre un backup antes de editar.
- Comprueba MIDI IN y MIDI OUT antes de enviar datos.
- No restaures un archivo `.syx` si no conoces su procedencia.
- La acción `Preparar User` no pulsa `STORE`; la confirmación física queda en
  manos del usuario.
- Prueba primero con un preset User que puedas recuperar.
- No desconectes la interfaz mientras se está enviando un banco.
- Mantén `Send Tuner` en `Off` durante capturas y pruebas de configuración.

Los parámetros marcados como pendientes o dependientes de algoritmo no deben
considerarse una especificación oficial del firmware hasta validarlos con un
G-Major 2 real.
