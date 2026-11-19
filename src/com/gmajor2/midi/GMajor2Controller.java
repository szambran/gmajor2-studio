package com.gmajor2.midi;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Control básico y transferencia SysEx para el TC Electronic G-Major 2. */
public class GMajor2Controller extends JFrame {
    private static final String VERSION = "1.0.0-beta";
    private static final String FECHA_VERSION = "19 de noviembre de 2026";
    private boolean idiomaIngles;
    // En el G-Major 2: banco 0 = fábrica y banco 1 = presets de usuario.
    private static final int BANCO_PRESETS_USUARIO = 1;
    // Paleta suave de equipo de audio: fondos claros, texto legible y familias
    // distinguibles sin convertir la vista ALL en una pared de colores fuertes.
    private static final Color COLOR_WAH_FILTER = new Color(246, 218, 174);
    private static final Color COLOR_DISTORTION = new Color(244, 190, 190);
    private static final Color COLOR_EQ = new Color(242, 229, 166);
    private static final Color COLOR_COMP_GATE = new Color(184, 225, 232);
    private static final Color COLOR_MOD = new Color(184, 214, 242);
    private static final Color COLOR_REVERB = new Color(220, 196, 234);
    private static final Color COLOR_PITCH = new Color(204, 199, 234);
    private static final Color COLOR_DELAY = new Color(191, 227, 201);
    private static final Color COLOR_PITCH_DELAY = new Color(218, 234, 174);
    private static final Color COLOR_GLOBAL = new Color(64, 64, 64); // gris oscuro general
    private static final Color COLOR_GLOBAL_CARD = new Color(100, 165, 100); // verde medio
    private static final Color COLOR_PANEL = new Color(78, 78, 78);
    private static final Color COLOR_TEXTO = new Color(55, 55, 55);
    private static final Color COLOR_BORDE = new Color(125, 135, 145);
    private static final Font FUENTE_NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font FUENTE_ETIQUETA = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Font FUENTE_TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    private final JComboBox<MidiDevice.Info> entrada = new JComboBox<>();
    private final JComboBox<MidiDevice.Info> salida = new JComboBox<>();
    private final JLabel estado = new JLabel("Sin conectar");
    private final JCheckBox sincronizacion = new JCheckBox("Sync en tiempo real", true);
    private final JTextArea registro = new JTextArea();
    private final DefaultListModel<String> listaPresets = new DefaultListModel<>();
    private final JList<String> presets = new JList<>(listaPresets);
    private final List<Integer> indicesPresetsOrdenados = new ArrayList<>();
    private final JTextField nombrePreset = new JTextField();
    private final JSpinner threshold = new JSpinner(new SpinnerNumberModel(0, -60, 0, 1));
    private final JSpinner maxDamping = new JSpinner(new SpinnerNumberModel(0, 0, 90, 1));
    private final JSpinner gateRelease = new JSpinner(new SpinnerNumberModel(3, 3, 200, 1));
    private final JSpinner compThreshold = new JSpinner(new SpinnerNumberModel(0, -40, 40, 1));
    private final JSpinner compGain = new JSpinner(new SpinnerNumberModel(0, -6, 6, 1));
    private final JSpinner eqGain = new JSpinner(new SpinnerNumberModel(0, -12, 12, 1));
    private final JSpinner filterMix = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner filterOut = new JSpinner(new SpinnerNumberModel(0, -100, 0, 1));
    private final JSpinner pitchMix = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner pitchOut = new JSpinner(new SpinnerNumberModel(0, -100, 0, 1));
    private final JSpinner modulationMix = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner modulationOut = new JSpinner(new SpinnerNumberModel(0, -100, 0, 1));
    private final JSpinner delayMix = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner delayOut = new JSpinner(new SpinnerNumberModel(0, -100, 0, 1));
    private final JSpinner delayTime = new JSpinner(new SpinnerNumberModel(0, 0, 1800, 1));
    private final JSpinner delayFeedback = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner reverbMix = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner reverbOut = new JSpinner(new SpinnerNumberModel(0, -100, 0, 1));
    private final JSpinner reverbDecay = new JSpinner(new SpinnerNumberModel(1, 1, 200, 1));
    private final JSpinner reverbPreDelay = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private JComboBox<String> killDryGlobal;
    private JComboBox<String> midiChannelGlobal;
    private JComboBox<String> midiProgramChangeGlobal;
    private JComboBox<String> midiClockGlobal;
    private JComboBox<String> sendTunerGlobal;
    private JSpinner sysexIdGlobal;
    private JComboBox<String> pedalTypeGlobal;
    private JComboBox<String> tunerModeGlobal;
    private JComboBox<String> tunerRangeGlobal;
    private JComboBox<String> tapMasterGlobal;
    private JComboBox<String> tapUnitGlobal;
    private JComboBox<String> fxMuteGlobal;
    private JComboBox<String> inputTypeGlobal;
    private JComboBox<String> outputRangeGlobal;
    private JComboBox<String> inputRangeGlobal;
    private JSpinner tunerRefGlobal;
    private JComboBox<String> clockGlobal;
    private JSpinner digiInGainGlobal;
    private JComboBox<String> ditherGlobal;
    private JComboBox<String> statusBitsGlobal;
    private JSpinner lcdAngleGlobal;
    private JComboBox<String> tapTempoMod;
    private JComboBox<String> mod1Assignment;
    private JComboBox<String> mod2Assignment;
    private JComboBox<String> mod3Assignment;
    private JComboBox<String> mod4Assignment;
    private JComboBox<String> routingGlobal;
    private JComboBox<String> routingLockGlobal;
    private JSpinner globalInLevelGlobal;
    private JSpinner globalOutLevelGlobal;
    private JTabbedPane pestañasBloques;
    private JTabbedPane editorCompletoBloques;
    private final java.util.Map<String, JComponent> controlesCompletos = new java.util.LinkedHashMap<>();
    private JLabel nombrePresetTodos;
    private JComboBox<String> selectorPresetTodos;
    private boolean actualizandoSelectorTodos;
    private byte[] presetCopiado;
    private final JLabel estadoEditor = new JLabel("Abre un backup SysEx para editarlo.");
    private final JLabel presetEnviado = new JLabel("Preset en el G-Major: sin seleccionar");
    private int indicePresetSeleccionado = -1;
    private int numeroPresetSeleccionado = -1;
    private final JSpinner canal = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1));
    private final JSpinner banco = new JSpinner(new SpinnerNumberModel(BANCO_PRESETS_USUARIO, 0, 127, 1));
    private final JSpinner programa = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
    private JSpinner destinoUser;
    private final JSpinner controlador = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
    private final JSpinner valor = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
    private Transmitter transmitter;
    private Receiver receiver;
    private MidiDevice inputDevice;
    private MidiDevice outputDevice;
    // El receptor MIDI escribe desde su propio hilo mientras la interfaz lee.
    // CopyOnWriteArrayList evita que la vista se quede en un estado inconsistente
    // durante la recepción de un Bulk Dump.
    private final List<byte[]> sysexRecibido = new CopyOnWriteArrayList<>();
    // Se conserva aparte porque el Bulk Dump de presets limpia su lista temporal.
    private final List<byte[]> setupRecibido = new CopyOnWriteArrayList<>();
    private final java.util.Map<Integer, Integer> valoresSetup = new java.util.concurrent.ConcurrentHashMap<>();

    public GMajor2Controller() {
        super("G-Major 2 Studio");
        UIManager.put("Label.font", FUENTE_NORMAL);
        UIManager.put("Button.font", FUENTE_ETIQUETA);
        UIManager.put("ComboBox.font", FUENTE_NORMAL);
        UIManager.put("Spinner.font", FUENTE_NORMAL);
        UIManager.put("TextField.font", FUENTE_NORMAL);
        UIManager.put("TextArea.font", new Font(Font.MONOSPACED, Font.PLAIN, 13));
        UIManager.put("List.font", FUENTE_NORMAL);
        UIManager.put("CheckBox.font", FUENTE_NORMAL);
        UIManager.put("Menu.font", FUENTE_ETIQUETA);
        UIManager.put("MenuItem.font", FUENTE_NORMAL);
        UIManager.put("TitledBorder.font", FUENTE_TITULO);
        UIManager.put("ToolTip.font", FUENTE_NORMAL);
        UIManager.put("Panel.font", FUENTE_NORMAL);
        UIManager.put("TabbedPane.selected", new Color(45, 105, 150));
        UIManager.put("TabbedPane.background", new Color(52, 66, 80));
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
        UIManager.put("ScrollBar.thumb", new Color(55, 105, 155));
        UIManager.put("ScrollBar.thumbDarkShadow", new Color(35, 65, 95));
        UIManager.put("ScrollBar.thumbHighlight", new Color(115, 165, 210));
        UIManager.put("ScrollBar.track", new Color(190, 195, 200));
        UIManager.put("ScrollBar.width", 18);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        setSize(1500, 850);
        construirInterfaz();
        buscarPuertos();
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(new EmptyBorder(12, 12, 12, 12));
        raiz.setBackground(COLOR_GLOBAL);
        setContentPane(raiz);
        setJMenuBar(crearBarraMenu());

        JPanel conexion = new JPanel(new GridBagLayout());
        conexion.setBackground(COLOR_PANEL);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; conexion.add(new JLabel("MIDI IN (G-Major → PC):"), c);
        c.gridx = 1; c.weightx = 1; conexion.add(entrada, c);
        c.gridx = 2; c.weightx = 0; conexion.add(new JLabel("MIDI OUT (PC → G-Major):"), c);
        c.gridx = 3; c.weightx = 1; conexion.add(salida, c);
        JButton conectar = new JButton("Conectar"); conectar.addActionListener(e -> conectar());
        c.gridx = 4; c.weightx = 0; conexion.add(conectar, c);
        c.gridx = 0; c.gridy = 1; c.gridwidth = 5; conexion.add(estado, c);
        c.gridy = 2; conexion.add(sincronizacion, c);
        sincronizacion.setToolTipText("Con Sync activo, la selección y los cambios se envían al G-Major 2.");
        sincronizacion.addActionListener(e -> {
            if (sincronizacion.isSelected()) iniciarSincronizacion();
            else log("Sync desactivado: los cambios se quedan en la biblioteca local.");
        });
        ponerTextoClaro(conexion);
        raiz.add(conexion, BorderLayout.NORTH);

        JPanel acciones = new JPanel(new BorderLayout(4, 4));
        acciones.setBackground(COLOR_GLOBAL);
        JPanel botonesAcciones = new JPanel(new GridLayout(0, 2, 3, 3));
        JButton recibir = new JButton("Recibir backup");
        recibir.setToolTipText("Pulsa este botón y después ejecuta Bulk Dump en el G-Major 2");
        recibir.addActionListener(e -> recibirBackup());
        JButton sincronizar = new JButton("Sync desde G-Major");
        sincronizar.setToolTipText("Prepara la recepción del banco; después ejecuta MIDI → Bulk Dump en la unidad");
        sincronizar.addActionListener(e -> {
            if (!sincronizacion.isSelected()) sincronizacion.setSelected(true);
            else iniciarSincronizacion();
        });
        JButton guardar = new JButton("Guardar backup");
        guardar.addActionListener(e -> guardarSysex());
        JButton abrir = new JButton("Abrir backup");
        abrir.addActionListener(e -> abrirBackup());
        JButton restaurar = new JButton("Restaurar backup"); restaurar.addActionListener(e -> restaurarBackup());
        JButton leerSetup = new JButton("Leer Setup real");
        leerSetup.setToolTipText("Solicita Global, MIDI, MOD y asignaciones sin modificar presets");
        leerSetup.addActionListener(e -> solicitarSetupReal());
        JButton refrescar = new JButton("Actualizar puertos"); refrescar.addActionListener(e -> buscarPuertos());
        botonesAcciones.add(recibir); botonesAcciones.add(sincronizar); botonesAcciones.add(guardar);
        botonesAcciones.add(abrir); botonesAcciones.add(restaurar); botonesAcciones.add(leerSetup);
        botonesAcciones.add(refrescar);

        JPanel midi = new JPanel(new GridBagLayout());
        midi.setBorder(BorderFactory.createTitledBorder("MIDI"));
        midi.setBackground(COLOR_GLOBAL);
        GridBagConstraints m = new GridBagConstraints(); m.insets = new Insets(2, 2, 2, 2); m.fill = GridBagConstraints.HORIZONTAL;
        m.gridx=0; m.gridy=0; midi.add(new JLabel("Canal"), m); m.gridx=1; midi.add(canal, m);
        JButton enviarPrograma = new JButton("Enviar banco + PC"); enviarPrograma.addActionListener(e -> enviarPrograma());
        m.gridx=0; m.gridy=1; m.gridwidth=2; midi.add(enviarPrograma, m);
        m.gridwidth=1; m.gridy=2; m.gridx=0; midi.add(new JLabel("Banco CC0"), m); m.gridx=1; midi.add(banco, m);
        m.gridy=3; m.gridx=0; midi.add(new JLabel("Programa"), m); m.gridx=1; midi.add(programa, m);
        JButton enviarCC = new JButton("Enviar CC"); enviarCC.addActionListener(e -> enviarCC());
        m.gridy=4; m.gridx=0; m.gridwidth=2; midi.add(enviarCC, m); m.gridwidth=1;
        m.gridy=5; m.gridx=0; midi.add(new JLabel("CC"), m); m.gridx=1; midi.add(controlador, m);
        m.gridy=6; m.gridx=0; midi.add(new JLabel("Valor CC"), m); m.gridx=1; midi.add(valor, m);
        m.gridy=7; m.gridx=0; m.gridwidth=2;
        JLabel avisoBanco = new JLabel("<html><body style='width:140px'>User: Program Bank = External<br>CC0=1 → User</body></html>");
        avisoBanco.setToolTipText("Con External, CC 0 = 0 selecciona Factory y CC 0 = 1 selecciona User.");
        midi.add(avisoBanco, m);
        JPanel menuSuperior = new JPanel(new BorderLayout(3, 3));
        menuSuperior.setOpaque(false);
        menuSuperior.add(botonesAcciones, BorderLayout.NORTH);
        menuSuperior.add(midi, BorderLayout.CENTER);
        acciones.remove(menuSuperior);
        acciones.add(midi, BorderLayout.NORTH);
        acciones.setBackground(COLOR_GLOBAL);
        acciones.setPreferredSize(new Dimension(226, 0));
        ponerTextoClaro(acciones);
        raiz.add(acciones, BorderLayout.WEST);

        registro.setEditable(false); registro.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        registro.setBackground(new Color(48, 48, 48));
        registro.setForeground(Color.WHITE);
        JTabbedPane pestañas = new JTabbedPane();
        // Las pestañas mantienen fondo claro para que sus títulos sigan siendo
        // legibles aunque el fondo general de la ventana sea gris oscuro.
        pestañas.setBackground(COLOR_PANEL);
        pestañas.setForeground(Color.WHITE);
        pestañas.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        pestañas.addTab(idioma("Registro", "Log"), new JScrollPane(registro));
        pestañas.addTab(idioma("Editor de presets", "Preset editor"), crearEditor());
        pestañas.addTab(idioma("Todos los parámetros", "All parameters"), crearEditorCompleto());
        pestañas.addTab(idioma("Setup global", "Global setup"), crearConfiguracionSistema());
        pestañas.addTab(idioma("Ayuda y créditos", "Help & credits"), crearModeloGMajor2());
        for (int i = 0; i < pestañas.getTabCount(); i++) {
            pestañas.setBackgroundAt(i, new Color(52, 66, 80));
            pestañas.setForegroundAt(i, Color.WHITE);
        }
        raiz.add(pestañas, BorderLayout.CENTER);
        log("Listo. Conecta una interfaz USB-MIDI y selecciona sus puertos.");
        if (idiomaIngles) traducirComponentes(raiz);
    }

    private String idioma(String español, String inglés) {
        return idiomaIngles ? inglés : español;
    }

    private void traducirComponentes(Container contenedor) {
        java.util.Map<String, String> traducciones = new java.util.HashMap<>();
        traducciones.put("Conectar", "Connect");
        traducciones.put("Recibir backup", "Receive backup");
        traducciones.put("Guardar backup", "Save backup");
        traducciones.put("Abrir backup", "Open backup");
        traducciones.put("Restaurar backup", "Restore backup");
        traducciones.put("Leer Setup real", "Read setup");
        traducciones.put("Actualizar puertos", "Refresh ports");
        traducciones.put("Sync desde G-Major", "Sync from G-Major");
        traducciones.put("Enviar banco + PC", "Send bank + PC");
        traducciones.put("Enviar CC", "Send CC");
        traducciones.put("Canal", "Channel");
        traducciones.put("Banco CC0", "CC0 bank");
        traducciones.put("Programa", "Program");
        traducciones.put("Valor CC", "CC value");
        traducciones.put("Opciones", "Options");
        traducciones.put("Ayuda", "Help");
        traducciones.put("Archivo", "File");
        traducciones.put("MIDI", "MIDI");
        traducciones.put("Copiar", "Copy");
        traducciones.put("Pegar", "Paste");
        traducciones.put("Aplicar", "Apply");
        traducciones.put("Aplicar nombre", "Apply name");
        traducciones.put("Nombre del preset", "Preset name");
        traducciones.put("Preset:", "Preset:");
        traducciones.put("Sync en tiempo real", "Real-time sync");
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof JLabel) {
                JLabel etiqueta = (JLabel) componente;
                if (traducciones.containsKey(etiqueta.getText())) etiqueta.setText(traducciones.get(etiqueta.getText()));
            } else if (componente instanceof AbstractButton) {
                AbstractButton boton = (AbstractButton) componente;
                if (traducciones.containsKey(boton.getText())) boton.setText(traducciones.get(boton.getText()));
            }
            if (componente instanceof Container) traducirComponentes((Container) componente);
        }
    }

    private void buscarPuertos() {
        entrada.removeAllItems(); salida.removeAllItems();
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            try {
                MidiDevice d = MidiSystem.getMidiDevice(info);
                if (d.getMaxTransmitters() != 0) entrada.addItem(info);
                if (d.getMaxReceivers() != 0) salida.addItem(info);
            } catch (MidiUnavailableException ignored) { }
        }
        log("Puertos encontrados: IN " + entrada.getItemCount() + ", OUT " + salida.getItemCount());
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu(idioma("Archivo", "File"));
        JMenuItem abrir = new JMenuItem(idioma("Abrir backup SysEx...", "Open SysEx backup..."));
        abrir.addActionListener(e -> abrirBackup());
        JMenuItem guardar = new JMenuItem(idioma("Guardar backup SysEx...", "Save SysEx backup..."));
        guardar.addActionListener(e -> guardarSysex());
        JMenuItem restaurar = new JMenuItem(idioma("Restaurar backup en el G-Major 2...", "Restore backup to G-Major 2..."));
        restaurar.addActionListener(e -> restaurarBackup());
        JMenuItem salir = new JMenuItem(idioma("Salir", "Exit"));
        salir.addActionListener(e -> dispose());
        archivo.add(abrir); archivo.add(guardar); archivo.add(restaurar);
        archivo.addSeparator(); archivo.add(salir);

        JMenu midiMenu = new JMenu("MIDI");
        JMenuItem conectarMenu = new JMenuItem(idioma("Conectar", "Connect"));
        conectarMenu.addActionListener(e -> conectar());
        JMenuItem puertosMenu = new JMenuItem(idioma("Actualizar puertos", "Refresh ports"));
        puertosMenu.addActionListener(e -> buscarPuertos());
        JMenuItem setupMenu = new JMenuItem(idioma("Leer Setup real", "Read setup"));
        setupMenu.addActionListener(e -> solicitarSetupReal());
        JMenuItem recibirMenu = new JMenuItem(idioma("Recibir Bulk Dump / backup", "Receive Bulk Dump / backup"));
        recibirMenu.addActionListener(e -> recibirBackup());
        JMenuItem syncMenu = new JMenuItem(idioma("Preparar Sync desde G-Major 2", "Prepare sync from G-Major 2"));
        syncMenu.addActionListener(e -> {
            if (!sincronizacion.isSelected()) sincronizacion.setSelected(true);
            iniciarSincronizacion();
        });
        midiMenu.add(conectarMenu); midiMenu.add(puertosMenu);
        midiMenu.addSeparator(); midiMenu.add(setupMenu); midiMenu.add(recibirMenu); midiMenu.add(syncMenu);

        JMenu opciones = new JMenu(idioma("Opciones", "Options"));
        JCheckBoxMenuItem sync = new JCheckBoxMenuItem(idioma("Sync en tiempo real", "Real-time sync"), sincronizacion.isSelected());
        sync.addActionListener(e -> {
            sincronizacion.setSelected(sync.isSelected());
            if (sync.isSelected()) iniciarSincronizacion();
            else log("Sync desactivado: los cambios se quedan en la biblioteca local.");
        });
        opciones.add(sync);
        JMenu idioma = new JMenu(idioma("Idioma / Language", "Language / Idioma"));
        JRadioButtonMenuItem español = new JRadioButtonMenuItem("Español", !idiomaIngles);
        JRadioButtonMenuItem inglés = new JRadioButtonMenuItem("English", idiomaIngles);
        ButtonGroup idiomas = new ButtonGroup();
        idiomas.add(español); idiomas.add(inglés);
        español.addActionListener(e -> cambiarIdioma(false));
        inglés.addActionListener(e -> cambiarIdioma(true));
        idioma.add(español); idioma.add(inglés);
        opciones.add(idioma);

        JMenu ayuda = new JMenu(idioma("Ayuda", "Help"));
         JMenuItem acerca = new JMenuItem(idioma("Acerca de G-Major 2 Studio", "About G-Major 2 Studio"));
        acerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Editor SysEx y MIDI para TC Electronic G-Major 2\n" +
                "Los backups se conservan sin modificar los presets originales.",
                idioma("Acerca de", "About"), JOptionPane.INFORMATION_MESSAGE));
        ayuda.add(acerca);

        barra.add(archivo); barra.add(midiMenu); barra.add(opciones); barra.add(ayuda);
        return barra;
    }

    private void cambiarIdioma(boolean inglés) {
        traducirRegistro(inglés);
        idiomaIngles = inglés;
        construirInterfaz();
        buscarPuertos();
        revalidate();
        repaint();
    }

    private void traducirRegistro(boolean inglés) {
        if (!inglés) return;
        String[] líneas = registro.getText().split("\\R", -1);
        StringBuilder resultado = new StringBuilder();
        for (String línea : líneas) {
            resultado.append(traducirMensajeRegistro(línea)).append('\n');
        }
        registro.setText(resultado.toString());
        registro.setCaretPosition(registro.getDocument().getLength());
    }

    private String traducirMensajeRegistro(String mensaje) {
        String[][] traducciones = {
                {"Listo. Conecta una interfaz USB-MIDI y selecciona sus puertos.", "Ready. Connect a USB-MIDI interface and select its ports."},
                {"Puertos encontrados: IN ", "Ports found: IN "},
                {"Conectado", "Connected"},
                {"MIDI conectado.", "MIDI connected."},
                {"Error MIDI: ", "MIDI error: "},
                {"Backup abierto: ", "Backup opened: "},
                {"Error abriendo backup: ", "Error opening backup: "},
                {"Setup recibido: ", "Setup received: "},
                {"MOD recibido: ", "MOD received: "},
                {"Preset seleccionado en la aplicación: ", "Preset selected in application: "},
                {"Preset de usuario ", "User preset "},
                {" seleccionado (banco ", " selected (bank "},
                {"No se envió el preset", "Preset was not sent"},
                {"conecta primero MIDI OUT", "connect MIDI OUT first"},
                {"Leer Setup necesita MIDI OUT conectado.", "Read setup requires MIDI OUT connected."},
                {"Sync necesita MIDI IN y MIDI OUT conectados.", "Sync requires MIDI IN and MIDI OUT connected."},
                {"Sync preparado. Ejecuta MIDI → Bulk Dump → ENTER en el G-Major 2.", "Sync ready. Run MIDI → Bulk Dump → ENTER on the G-Major 2."},
                {"Presets cargados desde el G-Major 2.", "Presets loaded from the G-Major 2."},
                {"No hay datos SysEx recibidos para guardar.", "There is no received SysEx data to save."},
                {"Backup guardado", "Backup saved"},
                {"Error guardando: ", "Error saving: "},
                {"Backup enviado.", "Backup sent."},
                {"Error restaurando: ", "Error restoring: "},
                {"SysEx enviado", "SysEx sent"},
                {"SysEx recibido", "SysEx received"},
                {"Bloque Global/MIDI recibido", "Global/MIDI block received"},
                {"Bloque MOD recibido", "MOD block received"},
                {"Error enviando SysEx: ", "Error sending SysEx: "}
        };
        String traducido = mensaje;
        for (String[] par : traducciones) traducido = traducido.replace(par[0], par[1]);
        return traducido;
    }

    private JPanel crearEditor() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setBackground(new Color(235, 238, 242));
        JPanel barraSuperior = new JPanel(new BorderLayout(6, 4));
        barraSuperior.add(crearBotonesBloques(), BorderLayout.CENTER);
        JPanel portapapeles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 2));
        portapapeles.setBorder(crearBordeTitulo("Preset"));
        JButton copiarArriba = new JButton("Copiar");
        copiarArriba.setToolTipText("Copiar el preset seleccionado");
        copiarArriba.addActionListener(e -> copiarPreset());
        JButton pegarArriba = new JButton("Pegar");
        pegarArriba.setToolTipText("Pegar sobre el preset seleccionado");
        pegarArriba.addActionListener(e -> pegarPreset());
        portapapeles.add(copiarArriba);
        portapapeles.add(pegarArriba);
        barraSuperior.add(portapapeles, BorderLayout.SOUTH);
        panel.add(barraSuperior, BorderLayout.NORTH);
        presets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        presets.setFont(FUENTE_NORMAL);
        presets.setBackground(Color.WHITE);
        presets.setSelectionBackground(new Color(74, 125, 168));
        presets.setSelectionForeground(Color.WHITE);
        presets.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarPreset();
        });
        JPanel detalles = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; c.weightx = 1; detalles.add(new JLabel("Nombre del preset"), c);
        c.gridy = 1; detalles.add(nombrePreset, c);
        JButton cambiar = new JButton("Aplicar nombre"); cambiar.addActionListener(e -> cambiarNombre());
        c.gridy = 2; detalles.add(cambiar, c);
        JButton enviar = new JButton("Enviar preset al G-Major 2");
        enviar.setToolTipText("Envía solo el preset seleccionado, sin restaurar todo el backup");
        enviar.addActionListener(e -> enviarPresetSeleccionado());
        c.gridy = 3; detalles.add(enviar, c);
        c.gridy = 4; detalles.add(presetEnviado, c);
        c.gridy = 5; detalles.add(estadoEditor, c);
        destinoUser = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton enviarUser = new JButton("Preparar User");
        enviarUser.setToolTipText("Envía una copia a la posición User elegida; confirma STORE en el G-Major 2 para conservarla");
        enviarUser.addActionListener(e -> prepararPresetUser());
        c.gridy = 6; detalles.add(new JLabel("Destino User (1–100)"), c);
        c.gridy = 7; detalles.add(destinoUser, c);
        c.gridy = 8; detalles.add(enviarUser, c);
        JPanel editorDerecho = new JPanel(new BorderLayout(6, 6));
        JPanel parametros = crearPanelParametros();
        parametros.setPreferredSize(new Dimension(520, 300));
        editorDerecho.add(parametros, BorderLayout.CENTER);
        editorDerecho.add(detalles, BorderLayout.SOUTH);
        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(presets), editorDerecho);
        divisor.setResizeWeight(0.25);
        divisor.setDividerLocation(260);
        panel.add(divisor, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearEditorCompleto() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel encabezado = new JPanel(new BorderLayout(8, 0));
        encabezado.setOpaque(false);
        JLabel tituloGeneral = new JLabel("TODOS LOS PARÁMETROS — G-MAJOR 2");
        tituloGeneral.setFont(FUENTE_TITULO);
        tituloGeneral.setForeground(COLOR_TEXTO);
        nombrePresetTodos = new JLabel("Preset actual: ninguno");
        nombrePresetTodos.setHorizontalAlignment(SwingConstants.RIGHT);
        selectorPresetTodos = new JComboBox<>();
        selectorPresetTodos.setPreferredSize(new Dimension(190, 26));
        selectorPresetTodos.setToolTipText("Selecciona directamente otro preset para editarlo");
        selectorPresetTodos.addActionListener(e -> {
            if (actualizandoSelectorTodos) return;
            int indice = selectorPresetTodos.getSelectedIndex();
            if (indice >= 0 && indice < listaPresets.size()) presets.setSelectedIndex(indice);
        });
        JButton copiar = new JButton("Copiar");
        copiar.setToolTipText("Copiar el preset seleccionado a la memoria de la aplicación");
        copiar.addActionListener(e -> copiarPreset());
        JButton pegar = new JButton("Pegar");
        pegar.setToolTipText("Pegar el preset copiado sobre el preset seleccionado");
        pegar.addActionListener(e -> pegarPreset());
        JPanel accionesPreset = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        accionesPreset.add(new JLabel("Preset:"));
        accionesPreset.add(selectorPresetTodos);
        accionesPreset.add(copiar);
        accionesPreset.add(pegar);
        encabezado.add(tituloGeneral, BorderLayout.WEST);
        encabezado.add(accionesPreset, BorderLayout.CENTER);
        encabezado.add(nombrePresetTodos, BorderLayout.EAST);

        JPanel columnas = new JPanel(new GridLayout(1, 9, 3, 0));
        // Mismo orden de bloques que utiliza el preset del G-Major 2.
        String[] nombres = {"COMPRESSOR", "FILTER", "PITCH", "MOD", "DELAY", "REVERB", "GATE", "EQ", "PRESET"};
        Color[] colores = {COLOR_COMP_GATE, COLOR_WAH_FILTER, COLOR_PITCH, COLOR_MOD,
                COLOR_DELAY, COLOR_REVERB, COLOR_COMP_GATE, COLOR_EQ, COLOR_PITCH_DELAY};
        for (int i = 0; i < nombres.length; i++) {
            String bloque = nombres[i];
            JPanel contenido = new JPanel();
            contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
            contenido.setBorder(new EmptyBorder(5, 5, 5, 5));
            contenido.setBackground(colores[i]);
            // Las columnas conservan un ancho legible; el JScrollPane permite
            // desplazarse horizontalmente cuando no caben todas en la ventana.
            contenido.setMinimumSize(new Dimension(220, 0));
            contenido.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
            JLabel titulo = new JLabel(bloque);
            titulo.setFont(FUENTE_TITULO);
            titulo.setForeground(COLOR_TEXTO);
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            contenido.add(titulo);
            List<GMajor2ParameterModel.Definition> definiciones = GMajor2ParameterModel.forBlock(bloque);
            // El estado On/Off queda siempre arriba y se identifica de un vistazo.
            for (GMajor2ParameterModel.Definition definition : definiciones) {
                if (definition.key.endsWith(".enabled")) agregarControlCompleto(contenido, definition);
            }
            for (GMajor2ParameterModel.Definition definition : definiciones) {
                if (!definition.key.endsWith(".enabled")) agregarControlCompleto(contenido, definition);
            }
            JButton aplicarBloque = new JButton("Aplicar");
            aplicarBloque.setAlignmentX(Component.LEFT_ALIGNMENT);
            aplicarBloque.setPreferredSize(new Dimension(190, 32));
            aplicarBloque.setMaximumSize(new Dimension(190, 32));
            aplicarBloque.setToolTipText("Aplicar los valores de " + bloque + " al preset y al G-Major 2");
            aplicarBloque.addActionListener(e -> aplicarBloqueCompleto(bloque));
            contenido.add(Box.createVerticalStrut(5));
            contenido.add(aplicarBloque);
            columnas.add(contenido);
        }
        Dimension anchoColumnas = columnas.getPreferredSize();
        anchoColumnas.width = Math.max(anchoColumnas.width, nombres.length * 220);
        columnas.setPreferredSize(anchoColumnas);
        panel.add(encabezado, BorderLayout.NORTH);
        JScrollPane desplazamiento = new JScrollPane(columnas,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        desplazamiento.setWheelScrollingEnabled(true);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(28);
        desplazamiento.getHorizontalScrollBar().setUnitIncrement(28);
        panel.add(desplazamiento, BorderLayout.CENTER);
        return panel;
    }

    private void agregarControlCompleto(JPanel contenido,
                                        GMajor2ParameterModel.Definition definition) {
        String texto = definition.label + (definition.unit.isEmpty() ? "" : " (" + definition.unit + ")");
        JLabel etiqueta = new JLabel("<html><body style='width:190px'>" + texto + "</body></html>");
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        etiqueta.setFont(FUENTE_ETIQUETA);
        etiqueta.setForeground(COLOR_TEXTO);
        etiqueta.setToolTipText(texto);
        contenido.add(etiqueta);
        JComponent control;
        if (definition.isChoice()) {
            control = new JComboBox<>(definition.options.toArray(new String[0]));
        } else {
            int maximo = definition.key.endsWith(".type") ? 127 : definition.maximum;
            control = new JSpinner(new SpinnerNumberModel(definition.minimum,
                    definition.minimum, maximo, 1));
        }
        control.setPreferredSize(new Dimension(180, 30));
        control.setMaximumSize(new Dimension(180, 30));
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (!(control instanceof JButton)) control.setBackground(Color.WHITE);
        controlesCompletos.put(definition.key, control);
        contenido.add(Box.createVerticalStrut(2));
        contenido.add(control);
        contenido.add(Box.createVerticalStrut(7));
    }

    private void cargarEditorCompleto(byte[] mensaje) {
        for (GMajor2ParameterModel.Definition definition : GMajor2ParameterModel.all()) {
            JComponent control = controlesCompletos.get(definition.key);
            if (control == null) continue;
            int valor = leerEntero24(mensaje, definition.offset);
            if (control instanceof JSpinner) {
                int maximo = definition.key.endsWith(".type") ? 127 : definition.maximum;
                int limitado = Math.max(definition.minimum, Math.min(maximo, valor));
                ((JSpinner) control).setValue(limitado);
            } else if (control instanceof JComboBox) {
                int indice = valor - definition.optionOffset;
                indice = Math.max(0, Math.min(definition.options.size() - 1, indice));
                ((JComboBox<?>) control).setSelectedIndex(indice);
            }
        }
    }

    private void aplicarBloqueCompleto(String bloqueActivo) {
        if (indicePresetSeleccionado < 0) {
            estadoEditor.setText("Selecciona primero un preset.");
            return;
        }
        byte[] mensaje = sysexRecibido.get(indicePresetSeleccionado);
        java.util.Set<Integer> offsetsEscritos = new java.util.HashSet<>();
        for (GMajor2ParameterModel.Definition definition : GMajor2ParameterModel.forBlock(bloqueActivo)) {
            // Los algoritmos comparten algunos offsets físicos. No se escribe
            // el mismo offset dos veces desde controles de algoritmos distintos.
            if (!offsetsEscritos.add(definition.offset)) continue;
            JComponent control = controlesCompletos.get(definition.key);
            int valor;
            if (control instanceof JSpinner) valor = (int) ((JSpinner) control).getValue();
            else valor = ((JComboBox<?>) control).getSelectedIndex() + definition.optionOffset;
            escribirEntero24(mensaje, definition.offset, valor);
        }
        recalcularChecksum(mensaje);
        enviarPresetSeleccionado();
        estadoEditor.setText("Todos los parámetros del preset fueron aplicados y enviados.");
    }

    private JPanel crearPanelParametros() {
        pestañasBloques = new JTabbedPane();
        pestañasBloques.addTab("FILTER", crearTarjeta("FILTER", COLOR_WAH_FILTER,
                fila("Mix (%)", filterMix), fila("Out Level (dB)", filterOut)));
        pestañasBloques.addTab("COMP", crearTarjeta("COMPRESSOR / GATE", COLOR_COMP_GATE,
                fila("Threshold (dB)", compThreshold), fila("Gain (dB)", compGain)));
        pestañasBloques.addTab("PITCH", crearTarjeta("PITCH", COLOR_PITCH,
                fila("Mix (%)", pitchMix), fila("Out Level (dB)", pitchOut)));
        pestañasBloques.addTab("MOD", crearTarjeta("MODULATION", COLOR_MOD,
                fila("Mix (%)", modulationMix), fila("Out Level (dB)", modulationOut)));
        pestañasBloques.addTab("DELAY", crearTarjeta("DELAY", COLOR_DELAY,
                fila("Delay time (ms)", delayTime), fila("Feedback (%)", delayFeedback),
                fila("Mix (%)", delayMix), fila("Out Level (dB)", delayOut)));
        pestañasBloques.addTab("REVERB", crearTarjeta("REVERB", COLOR_REVERB,
                fila("Decay (0.1 s)", reverbDecay), fila("Pre-delay (ms)", reverbPreDelay),
                fila("Mix (%)", reverbMix), fila("Out Level (dB)", reverbOut)));
        pestañasBloques.addTab("GATE", crearTarjeta("GATE", COLOR_COMP_GATE,
                fila("Threshold (dB)", threshold),
                fila("Max Damping (dB)", maxDamping),
                fila("Release (dB/s)", gateRelease)));
        pestañasBloques.addTab("EQ", crearTarjeta("EQ", COLOR_EQ,
                fila("Global Gain (dB)", eqGain)));
        JButton aplicar = new JButton("Aplicar parámetros visibles");
        aplicar.addActionListener(e -> aplicarParametrosBasicos());
        JPanel resultado = new JPanel(new BorderLayout(4, 4));
        resultado.add(pestañasBloques, BorderLayout.CENTER);
        resultado.add(aplicar, BorderLayout.SOUTH);
        return resultado;
    }

    private JPanel crearTarjeta(String titulo, Color color, JPanel... filas) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(crearBordeTitulo(titulo));
        tarjeta.setBackground(color);
        for (JPanel fila : filas) {
            fila.setOpaque(false);
            tarjeta.add(fila);
        }
        return tarjeta;
    }

    private JPanel fila(String etiqueta, JComponent control) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setBorder(new EmptyBorder(3, 4, 5, 4));
        if (!etiqueta.isEmpty()) {
            JLabel texto = new JLabel("<html><body style='width:150px'>" + etiqueta + "</body></html>");
            texto.setAlignmentX(Component.LEFT_ALIGNMENT);
            texto.setFont(FUENTE_ETIQUETA);
            texto.setForeground(COLOR_TEXTO);
            fila.add(texto);
        }
        control.setPreferredSize(control instanceof JButton
                ? new Dimension(160, 29) : new Dimension(110, 29));
        control.setMaximumSize(control instanceof JButton
                ? new Dimension(160, 29) : new Dimension(160, 29));
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (!(control instanceof JButton)) control.setBackground(Color.WHITE);
        fila.add(control);
        return fila;
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        TitledBorder borde = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE), titulo);
        borde.setTitleFont(FUENTE_TITULO);
        borde.setTitleColor(COLOR_TEXTO);
        return borde;
    }

    private JPanel crearBotonesBloques() {
        JPanel bloques = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        bloques.setBorder(BorderFactory.createTitledBorder("Bloques del G-Major 2"));
        String[] nombres = {"COMP", "FILTER", "PITCH", "MOD", "DELAY", "REVERB", "GATE", "EQ"};
        Color[] colores = {
                COLOR_WAH_FILTER, COLOR_COMP_GATE, COLOR_PITCH, COLOR_MOD,
                COLOR_DELAY, COLOR_REVERB, COLOR_COMP_GATE, COLOR_EQ
        };
        for (int i = 0; i < nombres.length; i++) {
            String nombre = nombres[i];
            JButton boton = new JButton(nombre);
            boton.setBackground(colores[i]);
            boton.setOpaque(true);
            boton.setForeground(Color.BLACK);
            boton.setFont(FUENTE_ETIQUETA);
            boton.setMargin(new Insets(5, 10, 5, 10));
            boton.setBorderPainted(true);
            boton.setToolTipText("Editor de " + nombre + " (se incorporará al modelo G-Major 2)");
            final int indiceBloque = i;
            boton.addActionListener(e -> {
                if (pestañasBloques != null) pestañasBloques.setSelectedIndex(indiceBloque);
                estadoEditor.setText("Bloque " + nombre + " seleccionado. Ajusta sus parámetros y pulsa Aplicar.");
            });
            bloques.add(boton);
        }
        return bloques;
    }

    private JPanel crearConfiguracionSistema() {
        JPanel sistema = new JPanel(new GridLayout(1, 4, 8, 0));
        sistema.setPreferredSize(new Dimension(1420, 560));
        sistema.setBackground(COLOR_GLOBAL);
        killDryGlobal = new JComboBox<>(new String[]{"Off", "On"});
        String[] canalesMIDI = {"Off", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "Omni"};
        midiChannelGlobal = new JComboBox<>(canalesMIDI);
        midiProgramChangeGlobal = new JComboBox<>(new String[]{"Off", "On"});
        midiClockGlobal = new JComboBox<>(new String[]{"Off", "On"});
        sendTunerGlobal = new JComboBox<>(new String[]{"Off", "On"});
        sysexIdGlobal = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
        midiChannelGlobal.setSelectedItem("1");
        midiProgramChangeGlobal.setSelectedItem("On");
        midiClockGlobal.setSelectedItem("On");
        sysexIdGlobal.setValue(1);
        JButton aplicarKillDry = new JButton("Aplicar Kill Dry");
        aplicarKillDry.setToolTipText("Envía el valor de Kill Dry al G-Major 2");
        aplicarKillDry.addActionListener(e -> enviarKillDry());
        pedalTypeGlobal = new JComboBox<>(new String[]{"Momentary", "Alternate", "Exp/Vol"});
        tunerModeGlobal = new JComboBox<>(new String[]{"Coarse", "Fine Tune"});
        tunerRangeGlobal = new JComboBox<>(new String[]{"Bass", "Guitar", "7-string Guitar"});
        tunerRangeGlobal.setSelectedItem("Guitar");
        tapMasterGlobal = new JComboBox<>(new String[]{"Preset", "Tap"});
        tapUnitGlobal = new JComboBox<>(new String[]{"BPM", "ms"});
        tapUnitGlobal.setSelectedItem("ms");
        fxMuteGlobal = new JComboBox<>(new String[]{"Soft", "Hard"});
        inputTypeGlobal = new JComboBox<>(new String[]{"Analog", "Digital"});
        outputRangeGlobal = new JComboBox<>(new String[]{"2 dBu", "8 dBu", "14 dBu", "20 dBu"});
        outputRangeGlobal.setSelectedItem("20 dBu");
        inputRangeGlobal = new JComboBox<>(new String[]{"Consumer", "Professional"});
        tunerRefGlobal = new JSpinner(new SpinnerNumberModel(440, 420, 460, 1));
        clockGlobal = new JComboBox<>(new String[]{"44.1 kHz", "48 kHz", "Digital"});
        clockGlobal.setSelectedItem("48 kHz");
        digiInGainGlobal = new JSpinner(new SpinnerNumberModel(0, -12, 12, 1));
        ditherGlobal = new JComboBox<>(new String[]{"Off", "20", "16", "8"});
        statusBitsGlobal = new JComboBox<>(new String[]{"AES/EBU", "S/PDIF"});
        statusBitsGlobal.setSelectedItem("S/PDIF");
        lcdAngleGlobal = new JSpinner(new SpinnerNumberModel(30, 0, 60, 1));
        tapTempoMod = crearSelectorCC(84);
        mod1Assignment = crearSelectorCC(14);
        mod2Assignment = crearSelectorCC(15);
        mod3Assignment = crearSelectorCC(15);
        mod4Assignment = crearSelectorCC(15);
        mod2Assignment.setSelectedIndex(0);
        mod3Assignment.setSelectedIndex(0);
        mod4Assignment.setSelectedIndex(0);
        JButton aplicarValidados = new JButton("Aplicar Setup");
        aplicarValidados.setToolTipText("Envía únicamente Pedal Type y Kill Dry, ya comprobados");
        aplicarValidados.addActionListener(e -> enviarValoresValidados());
        JPanel global = crearTarjetaSistema("GLOBAL", COLOR_GLOBAL_CARD,
                fila("MOD Master", new JComboBox<>(new String[]{"Preset", "Mod"})),
                fila("Kill Dry", killDryGlobal),
                fila("Pedal Type", pedalTypeGlobal),
                 fila("Tuner Ref (Hz)", tunerRefGlobal),
                 fila("Tuner Mode", tunerModeGlobal),
                 fila("Tuner Range", tunerRangeGlobal),
                 fila("Tap Master", tapMasterGlobal),
                 fila("Tap Unit", tapUnitGlobal),
                 fila("FX Mute", fxMuteGlobal),
                 fila("Input Type", inputTypeGlobal),
                 fila("Input Range", inputRangeGlobal),
                 fila("Output Range", outputRangeGlobal),
                 fila("Clock", clockGlobal),
                 fila("Digi In Gain (dB)", digiInGainGlobal),
                 fila("Dither", ditherGlobal),
                 fila("Status Bits", statusBitsGlobal),
                 fila("LCD Angle", lcdAngleGlobal),
                 fila("", aplicarKillDry),
                 fila("", aplicarValidados));
        sistema.add(global);
        sistema.add(crearTarjetaSistema("MIDI", COLOR_MOD,
                fila("MIDI Channel", midiChannelGlobal),
                fila("Program Change", midiProgramChangeGlobal),
                fila("Program Bank", new JComboBox<>(new String[]{"External", "Factory", "User", "Custom"})),
                fila("SysEx ID", sysexIdGlobal),
                fila("MIDI Clock", midiClockGlobal),
                fila("Send Tuner", sendTunerGlobal),
                 fila("MIDI Mapping", new JComboBox<>(new String[]{"Factory/User", "Custom"}))));
        sistema.add(crearTarjetaSistema("MODIFIERS", COLOR_WAH_FILTER,
                fila("MOD 1", mod1Assignment),
                fila("MOD 2", mod2Assignment),
                fila("MOD 3", mod3Assignment),
                fila("MOD 4", mod4Assignment),
                fila("Tap Tempo", tapTempoMod),
                fila("Bypass All", new JComboBox<>(new String[]{"Off", "On"})),
                fila("Tuner Mute", new JComboBox<>(new String[]{"Off", "On"})),
                 fila("Global In Level", selectorCCOff()),
                 fila("Preset Out Level", crearSelectorCC(14)),
                 fila("PresetBoost", selectorCCOff()),
                 fila("Global Out Level", selectorCCOff()),
                 fila("Comp On/Off", selectorCCOff()),
                 fila("Filter On/Off", crearSelectorCC(80)),
                 fila("Pitch On/Off", crearSelectorCC(83)),
                 fila("Cho/Flg On/Off", crearSelectorCC(81)),
                 fila("Delay On/Off", crearSelectorCC(82)),
                 fila("Reverb On/Off", crearSelectorCC(81)),
                 fila("Noise Gate On/Off", selectorCCOff()),
                 fila("EQ On/Off", selectorCCOff())));
        sistema.add(crearTarjetaLevelsRouting());
        JPanel resultado = new JPanel(new BorderLayout(8, 8));
        resultado.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane desplazamiento = new JScrollPane(sistema);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        resultado.add(desplazamiento, BorderLayout.CENTER);
        JTextArea ayuda = new JTextArea(
                "Los cambios globales afectan a toda la unidad. Usa «Leer Setup real» para cargar los valores del G-Major 2.");
        ayuda.setLineWrap(false); ayuda.setEditable(false); ayuda.setRows(1);
        ayuda.setFont(FUENTE_NORMAL);
        ayuda.setForeground(COLOR_TEXTO);
        ayuda.setBackground(new Color(245, 246, 248));
        ayuda.setBorder(new EmptyBorder(7, 9, 7, 9));
        resultado.add(new JScrollPane(ayuda), BorderLayout.SOUTH);
        return resultado;
    }

    private JPanel crearTarjetaLevelsRouting() {
        routingGlobal = new JComboBox<>(new String[]{"Serial", "Semi Parallel", "Serial 2", "Parallel"});
        routingGlobal.setSelectedItem("Semi Parallel");
        routingLockGlobal = new JComboBox<>(new String[]{"Off", "On"});
        routingLockGlobal.setSelectedItem("On");
        globalInLevelGlobal = new JSpinner(new SpinnerNumberModel(0, -100, 20, 1));
        globalOutLevelGlobal = new JSpinner(new SpinnerNumberModel(0, -100, 20, 1));
        return crearTarjetaSistema("LEVELS / ROUTING", COLOR_EQ,
                fila("Global In (dB)", globalInLevelGlobal),
                fila("Global Out (dB)", globalOutLevelGlobal),
                fila("Routing", routingGlobal),
                fila("Routing Lock", routingLockGlobal),
                fila("Preset Output (dB)", new JSpinner(new SpinnerNumberModel(-6, -100, 20, 1))),
                fila("Dry Signal", new JComboBox<>(new String[]{"On", "Off"})),
                fila("Relay 1", new JComboBox<>(new String[]{"Open", "Closed"})),
                fila("Relay 2", new JComboBox<>(new String[]{"Open", "Closed"})),
                fila("Relay 1 Mode", new JComboBox<>(new String[]{"Preset", "Global"})),
                fila("Relay 2 Mode", new JComboBox<>(new String[]{"Preset", "Global"})));
    }

    private void enviarKillDry() {
        if (outputDevice == null) {
            log("Kill Dry no enviado: conecta primero el MIDI OUT.");
            return;
        }
        int valorKillDry = killDryGlobal.getSelectedIndex();
        byte[] datos = {(byte) 0xF0, 0x00, 0x20, 0x1F, 0x01, 0x66, 0x32,
                0x48, 0x01, (byte) valorKillDry, 0x00, 0x00, 0x00, (byte) 0xF7};
        try {
            SysexMessage mensaje = new SysexMessage();
            mensaje.setMessage(datos, datos.length);
            outputDevice.getReceiver().send(mensaje, -1);
            log("→ Kill Dry enviado: " + (valorKillDry == 1 ? "On" : "Off"));
        } catch (InvalidMidiDataException | MidiUnavailableException ex) {
            log("Error enviando Kill Dry: " + ex.getMessage());
        }
    }

    private void enviarValoresValidados() {
        if (outputDevice == null) {
            log("Valores validados no enviados: conecta primero el MIDI OUT.");
            return;
        }
        try {
            enviarSysexCorto(0x32, 0x23, 0x01, pedalTypeGlobal.getSelectedIndex() + 1, 0, 0, 0);
            enviarSysexCorto(0x32, 0x25, 0x01, tunerModeGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x26, 0x01, tunerRangeGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x29, 0x01, tapMasterGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x2A, 0x01, tapUnitGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x2C, 0x01, fxMuteGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x17, 0x01, inputTypeGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x1E, 0x01, outputRangeGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x1C, 0x01, inputRangeGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x1A, 0x01, ditherGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x18, 0x01, clockGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x1B, 0x01, statusBitsGlobal.getSelectedIndex(), 0, 0, 0);
            int digiGain = (int) digiInGainGlobal.getValue();
            enviarSysexCorto(0x32, 0x19, 0x01, digiGain & 0x7F, 0, 0, 0);
            int tunerRef = (int) tunerRefGlobal.getValue();
            enviarSysexCorto(0x32, 0x24, 0x01, tunerRef & 0x7F, (tunerRef >> 7) & 0x7F, 0, 0);
            enviarSysexCorto(0x32, 0x2D, 0x01, (int) lcdAngleGlobal.getValue(), 0, 0, 0);
            if (routingGlobal != null) enviarSysexCorto(0x32, 0x2F, 0x01, routingGlobal.getSelectedIndex(), 0, 0, 0);
            if (routingLockGlobal != null) enviarSysexCorto(0x32, 0x30, 0x01, routingLockGlobal.getSelectedIndex(), 0, 0, 0);
            if (globalInLevelGlobal != null) enviarNivelGlobal(0x46, (int) globalInLevelGlobal.getValue());
            if (globalOutLevelGlobal != null) enviarNivelGlobal(0x47, (int) globalOutLevelGlobal.getValue());
            enviarSysexCorto(0x32, 0x4C, 0x01, killDryGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x28, 0x01, sendTunerGlobal.getSelectedIndex(), 0, 0, 0);
            int canalSetup = midiChannelGlobal.getSelectedIndex() == 0 ? 17
                    : (midiChannelGlobal.getSelectedIndex() == 17 ? 16 : midiChannelGlobal.getSelectedIndex() - 1);
            enviarSysexCorto(0x32, 0x27, 0x01, canalSetup, 0, 0, 0);
            enviarSysexCorto(0x32, 0x20, 0x01, midiProgramChangeGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x2B, 0x01, midiClockGlobal.getSelectedIndex(), 0, 0, 0);
            enviarSysexCorto(0x32, 0x22, 0x01, (int) sysexIdGlobal.getValue(), 0, 0, 0);
            int ccTapTempo = leerCC(tapTempoMod);
            if (ccTapTempo >= 0) enviarSysexCorto(0x35, 0x01, ccTapTempo + 3, 0, 0, 0);
            int ccMod1 = leerCC(mod1Assignment);
            if (ccMod1 >= 0) enviarSysexCorto(0x31, 0x01, ccMod1 + 3, 0, 0, 0);
            int ccMod2 = leerCC(mod2Assignment);
            if (ccMod2 >= 0) enviarSysexCorto(0x32, 0x01, ccMod2 + 3, 0, 0, 0);
            int ccMod3 = leerCC(mod3Assignment);
            if (ccMod3 >= 0) enviarSysexCorto(0x33, 0x01, ccMod3 + 3, 0, 0, 0);
            int ccMod4 = leerCC(mod4Assignment);
            if (ccMod4 >= 0) enviarSysexCorto(0x34, 0x01, ccMod4 + 3, 0, 0, 0);
            log("→ Valores validados enviados: Setup y Tap Tempo.");
        } catch (Exception ex) {
            log("Error enviando valores validados: " + ex.getMessage());
        }
    }

    private JComboBox<String> crearSelectorCC(int valorInicial) {
        JComboBox<String> selector = new JComboBox<>();
        selector.addItem("Off");
        for (int i = 0; i <= 127; i++) selector.addItem("CC " + i);
        selector.setSelectedItem("CC " + valorInicial);
        return selector;
    }

    private int leerCC(JComboBox<String> selector) {
        if (selector == null || selector.getSelectedItem() == null) return -1;
        String texto = selector.getSelectedItem().toString();
        if (!texto.startsWith("CC ")) return -1;
        try { return Integer.parseInt(texto.substring(3)); }
        catch (NumberFormatException ex) { return -1; }
    }

    private JComboBox<String> selectorCCOff() {
        JComboBox<String> selector = crearSelectorCC(0);
        selector.setSelectedIndex(0);
        return selector;
    }

    private void enviarNivelGlobal(int parametro, int valor) throws Exception {
        int codificado = valor < 0 ? (1 << 28) + valor : valor;
        enviarSysexCorto(0x32, parametro, 0x01,
                codificado & 0x7F, (codificado >> 7) & 0x7F,
                (codificado >> 14) & 0x7F, (codificado >> 21) & 0x7F);
    }

    private JPanel crearLevelsRouting() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel niveles = new JPanel(new GridLayout(1, 3, 8, 8));
        routingGlobal = new JComboBox<>(new String[]{"Serial", "Semi Parallel", "Serial 2", "Parallel"});
        routingGlobal.setSelectedItem("Semi Parallel");
        routingLockGlobal = new JComboBox<>(new String[]{"Off", "On"});
        routingLockGlobal.setSelectedItem("On");
        globalInLevelGlobal = new JSpinner(new SpinnerNumberModel(0, -100, 20, 1));
        globalOutLevelGlobal = new JSpinner(new SpinnerNumberModel(0, -100, 20, 1));
        JSpinner presetOutput = new JSpinner(new SpinnerNumberModel(-6, -100, 20, 1));
        niveles.add(crearTarjetaSistema("INPUT / OUTPUT", COLOR_EQ,
                fila("Global In Level (dB)", globalInLevelGlobal),
                fila("Global Out Level (dB)", globalOutLevelGlobal),
                fila("Preset In Level (dB)", new JSpinner(new SpinnerNumberModel(0, -100, 20, 1))),
                fila("Preset Out Level (dB)", new JSpinner(new SpinnerNumberModel(0, -100, 20, 1))),
                fila("Kill Dry", new JComboBox<>(new String[]{"Off", "On"}))));
        niveles.add(crearTarjetaSistema("ROUTING", COLOR_DELAY,
                fila("Routing", routingGlobal),
                fila("Routing Lock", routingLockGlobal),
                fila("Dry Signal", new JComboBox<>(new String[]{"On", "Off"})),
                fila("Preset Output Level (dB)", presetOutput)));
        niveles.add(crearTarjetaSistema("RELAY 1 + 2", COLOR_PITCH_DELAY,
                fila("Relay 1", new JComboBox<>(new String[]{"Open", "Closed"})),
                fila("Relay 2", new JComboBox<>(new String[]{"Open", "Closed"})),
                fila("Relay 1 Mode", new JComboBox<>(new String[]{"Preset", "Global"})),
                fila("Relay 2 Mode", new JComboBox<>(new String[]{"Preset", "Global"}))));
        panel.add(niveles, BorderLayout.NORTH);
        JTextArea ayuda = new JTextArea(
                "Levels/Routing reúne los niveles Global In/Out, Preset In/Out, " +
                "Kill Dry, las cuatro routings documentadas (Serial, Serial 2, " +
                "Semi Parallel y Parallel), Routing Lock y los dos relés. Estos " +
                "parámetros necesitan el formato SysEx global/preset específico " +
                "del G-Major 2 antes de escribirlos en el equipo.");
        ayuda.setLineWrap(true); ayuda.setWrapStyleWord(true); ayuda.setEditable(false);
        panel.add(ayuda, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTarjetaSistema(String titulo, Color color, JPanel... filas) {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(color);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 5, 3, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        for (int i = 0; i < filas.length; i++) {
            JPanel fila = filas[i];
            fila.setOpaque(false);
            c.gridx = i % 2;
            c.gridy = i / 2;
            tarjeta.add(fila, c);
        }
        c.gridx = 0; c.gridy = (filas.length + 1) / 2;
        c.gridwidth = 2; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        tarjeta.add(Box.createGlue(), c);
        tarjeta.setBorder(crearBordeTitulo(titulo));
        ponerTextoOscuro(tarjeta);
        return tarjeta;
    }

    private void ponerTextoOscuro(Container contenedor) {
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof JLabel) componente.setForeground(Color.DARK_GRAY);
            if (componente instanceof Container) ponerTextoOscuro((Container) componente);
        }
    }

    private void ponerTextoClaro(Container contenedor) {
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof JLabel) componente.setForeground(Color.WHITE);
            if (componente instanceof Container) ponerTextoClaro((Container) componente);
        }
    }

    /**
     * Catálogo visible de los algoritmos del G-Major 2. No se usan los índices
     * de FreeMajor para escribir tipos: algunos pertenecen al G-Major 1.
     */
    private JPanel crearModeloGMajor2() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setBackground(new Color(235, 238, 242));

        JLabel titulo = new JLabel("G-MAJOR 2 STUDIO");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titulo.setForeground(new Color(35, 70, 105));
        JLabel subtitulo = new JLabel(idioma("Editor de presets, configuración global y herramientas MIDI para TC Electronic G-Major 2",
                "Preset editor, global setup and MIDI tools for the TC Electronic G-Major 2"));
        subtitulo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        subtitulo.setForeground(new Color(55, 55, 55));
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(subtitulo);
        panel.add(encabezado, BorderLayout.NORTH);

        JTextArea texto = new JTextArea(
            "AYUDA RÁPIDA\n\n" +
            "1. Conecta la interfaz USB-MIDI y selecciona el mismo dispositivo en MIDI IN y MIDI OUT.\n" +
            "2. Pulsa «Conectar» y utiliza «Leer Setup real» para consultar Global, MIDI, MOD y Levels/Routing.\n" +
            "3. Para leer presets sin sobrescribirlos, pulsa «Recibir backup» y después ejecuta Bulk Dump en el G-Major 2.\n" +
            "4. Abre un archivo .syx para editar una copia local. No se pulsa Store durante las pruebas.\n" +
            "5. Para presets de usuario se utiliza CC0 = 1 y después el Program Change.\n\n" +
            "El banco User muestra siempre las 100 posiciones. El G-Major 2 omite del Bulk Dump las posiciones vacías; " +
            "por eso aparecen marcadas como «vacío / no recibido».\n\n" +
            "QUÉ ENCONTRARÁS EN LAS PESTAÑAS\n\n" +
            "• Editor de presets: selección y edición del preset recibido.\n" +
            "• Todos los parámetros: vista completa organizada por bloques de efectos.\n" +
            "• Setup global: parámetros generales, MIDI, MOD y niveles/routing.\n" +
            "• Registro: actividad MIDI y avisos de la aplicación.\n\n" +
            "PROTOCOLO Y SEGURIDAD\n\n" +
            "La aplicación conserva los mensajes SysEx recibidos y permite guardar un backup antes de editar. " +
            "Los valores mostrados como referencia no se escriben en el equipo hasta que se ejecuta una acción MIDI.\n\n" +
            "COMPATIBILIDAD\n\n" +
            "Desarrollado y probado en Linux Debian 13. Al estar escrito en Java Swing y usar " +
            "javax.sound.midi, también puede funcionar en Windows con JDK 11 o posterior y un " +
            "controlador USB-MIDI reconocido por el sistema. En Linux se utiliza ALSA-MIDI; el " +
            "archivo run.sh es específico de Linux.\n\n" +
            "IDIOMA\n\n" +
            "La aplicación está disponible en Español e English. Puedes cambiarlo desde " +
            "Opciones → Idioma / Language.\n\n" +
            "CRÉDITOS\n\n" +
            "G-Major 2 Studio\n" +
            "Versión " + VERSION + " · " + FECHA_VERSION + "\n" +
            "Desarrollado para Sergio Zambrano-Martínez\n" +
            "Herramienta de escritorio para edición y gestión MIDI del TC Electronic G-Major 2.\n" +
            "Código: licencia MIT recomendada para facilitar su uso y reutilización."
        );
        if (idiomaIngles) texto.setText(
            "QUICK HELP\n\n" +
            "1. Connect the USB-MIDI interface and select the same device for MIDI IN and MIDI OUT.\n" +
            "2. Press Connect and use Read setup to query Global, MIDI, MOD and Levels/Routing.\n" +
            "3. To read presets safely, press Receive backup and then run Bulk Dump on the G-Major 2.\n" +
            "4. Open a .syx file to edit a local copy. Do not press Store during testing.\n" +
            "5. User presets use CC0 = 1 followed by Program Change.\n\n" +
            "The User bank always shows all 100 positions. The G-Major 2 omits empty positions from a Bulk Dump; " +
            "they are shown as empty / not received.\n\n" +
            "TABS\n\n" +
            "• Preset editor: select and edit a received preset.\n" +
            "• All parameters: complete view organized by effect blocks.\n" +
            "• Global setup: general, MIDI, MOD and levels/routing settings.\n" +
            "• Log: MIDI activity and application messages.\n\n" +
            "PROTOCOL AND SAFETY\n\n" +
            "Received SysEx messages are preserved so you can save a backup before editing. " +
            "Reference values are not written to the unit until a MIDI action is executed.\n\n" +
            "COMPATIBILITY\n\n" +
            "Developed and tested on Linux Debian 13. Because it uses Java Swing and javax.sound.midi, " +
            "it can also run on Windows with JDK 11 or later and a recognized USB-MIDI driver.\n\n" +
            "LANGUAGE\n\n" +
            "The application is available in Español and English. Change it from " +
            "Options → Language / Idioma.\n\n" +
            "CREDITS\n\n" +
            "G-Major 2 Studio\n" +
            "Version " + VERSION + " · " + FECHA_VERSION + "\n" +
            "Developed for Sergio Zambrano-Martínez\n" +
            "MIT license recommended for sharing and reuse."
        );
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        texto.setForeground(new Color(35, 35, 35));
        texto.setBackground(Color.WHITE);
        texto.setBorder(new EmptyBorder(14, 16, 14, 16));
        JScrollPane desplazamiento = new JScrollPane(texto,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        desplazamiento.setWheelScrollingEnabled(true);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(30);
        panel.add(desplazamiento, BorderLayout.CENTER);
        return panel;
    }

    private void abrirBackup() {
        JFileChooser f = new JFileChooser();
        f.setFileFilter(new FileNameExtensionFilter("SysEx (*.syx)", "syx"));
        if (f.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            byte[] datos = Files.readAllBytes(f.getSelectedFile().toPath());
            cargarMensajesSysex(datos);
            log("Backup abierto: " + f.getSelectedFile());
        } catch (IOException ex) { log("Error abriendo backup: " + ex.getMessage()); }
    }

    private void cargarMensajesSysex(byte[] datos) throws IOException {
        sysexRecibido.clear();
        setupRecibido.clear();
        int inicio = -1;
        for (int i = 0; i < datos.length; i++) {
            int valor = datos[i] & 255;
            if (valor == 0xf0) inicio = i;
            if (valor == 0xf7 && inicio >= 0) {
                byte[] mensaje = new byte[i - inicio + 1];
                System.arraycopy(datos, inicio, mensaje, 0, mensaje.length);
                sysexRecibido.add(mensaje);
                if (esMensajeSetup(mensaje)) setupRecibido.add(mensaje);
                inicio = -1;
            }
        }
        actualizarListaPresets();
        if (cantidadPresetsRecibidos() == 0) throw new IOException("no contiene presets SysEx de 615 bytes");
        estadoEditor.setText(cantidadPresetsRecibidos() + " presets recibidos; se muestran las 100 posiciones User.");
    }

    private void actualizarListaPresets() {
        listaPresets.clear();
        indicesPresetsOrdenados.clear();
        indicePresetSeleccionado = -1; numeroPresetSeleccionado = -1;
        java.util.Map<Integer, Integer> mensajesPorNumero = new java.util.HashMap<>();
        for (int i = 0; i < sysexRecibido.size(); i++) {
            if (sysexRecibido.get(i).length == 615) mensajesPorNumero.put(numeroPreset(sysexRecibido.get(i)), i);
        }
        // El G-Major 2 omite las posiciones User vacías durante el Bulk Dump.
        // La lista mantiene siempre visibles las 100 posiciones del banco.
        for (int numero = 1; numero <= 100; numero++) {
            Integer indice = mensajesPorNumero.get(numero);
            indicesPresetsOrdenados.add(indice == null ? -1 : indice);
            if (indice == null) {
                listaPresets.addElement(String.format("User %3d  —  [vacío / no recibido]", numero));
            } else {
                byte[] mensaje = sysexRecibido.get(indice);
                String nombre = new String(mensaje, 9, 20, StandardCharsets.US_ASCII).trim();
                listaPresets.addElement(String.format("User %3d  —  %s  [PC %d]", numero, nombre, numero - 1));
            }
        }
        actualizarSelectorPresets();
        aplicarConfiguracionRecibida();
    }

    private int cantidadPresetsRecibidos() {
        int cantidad = 0;
        for (byte[] mensaje : sysexRecibido) if (mensaje.length == 615) cantidad++;
        return cantidad;
    }

    /** Carga únicamente los parámetros globales que ya han sido identificados.
     * Los demás mensajes de Setup se conservan, pero no se interpretan todavía. */
    private void aplicarConfiguracionRecibida() {
        for (byte[] mensaje : sysexRecibido) {
            actualizarKillDryDesdeMensaje(mensaje, false);
        }
        for (byte[] mensaje : setupRecibido) {
            actualizarKillDryDesdeMensaje(mensaje, false);
        }
    }

    private void actualizarKillDryDesdeMensaje(byte[] mensaje, boolean registrar) {
        if (mensaje.length >= 14 && (mensaje[6] & 0x7F) == 0x32
                && ((mensaje[7] & 0x7F) == 0x46 || (mensaje[7] & 0x7F) == 0x47)) {
            int parametroNivel = mensaje[7] & 0x7F;
            int codificado = (mensaje[9] & 0x7F) | ((mensaje[10] & 0x7F) << 7)
                    | ((mensaje[11] & 0x7F) << 14) | ((mensaje[12] & 0x7F) << 21);
            if (codificado >= (1 << 27)) codificado -= (1 << 28);
            final int nivel = codificado;
            SwingUtilities.invokeLater(() -> {
                JSpinner control = parametroNivel == 0x46 ? globalInLevelGlobal : globalOutLevelGlobal;
                if (control != null && nivel >= -100 && nivel <= 20) {
                    control.setValue(nivel);
                    if (registrar) log("Setup recibido: " + (parametroNivel == 0x46 ? "Global In" : "Global Out") + " Level = " + nivel + " dB");
                }
            });
            return;
        }
        if (mensaje.length != 14) return;
        int comando = mensaje[6] & 0x7F;
        if (comando == 0x35 ||
                ((comando >= 0x31 && comando <= 0x34) && (mensaje[7] & 0x7F) == 1)) {
            int campo = mensaje[7] & 0x7F;
            int ccCodificado = mensaje[8] & 0x7F;
            if (campo == 1 && tapTempoMod != null && ccCodificado >= 3 && ccCodificado <= 130) {
                final int cc = ccCodificado - 3;
                SwingUtilities.invokeLater(() -> {
                    if (comando == 0x35) {
                        tapTempoMod.setSelectedItem("CC " + cc);
                        if (registrar) log("MOD recibido: Tap Tempo = CC " + cc);
                    } else if (comando == 0x31) {
                        mod1Assignment.setSelectedItem("CC " + cc);
                        if (registrar) log("MOD recibido: MOD 1 = CC " + cc);
                    } else if (comando == 0x32) {
                        mod2Assignment.setSelectedItem("CC " + cc);
                        if (registrar) log("MOD recibido: MOD 2 = CC " + cc);
                    } else if (comando == 0x33) {
                        mod3Assignment.setSelectedItem("CC " + cc);
                        if (registrar) log("MOD recibido: MOD 3 = CC " + cc);
                    } else {
                        mod4Assignment.setSelectedItem("CC " + cc);
                        if (registrar) log("MOD recibido: MOD 4 = CC " + cc);
                    }
                });
            }
            return;
        }
        if (comando != 0x32) return;
        int parametro = mensaje[7] & 0x7F;
        int valor = mensaje[9] & 0x7F;
        int valorExtendido = valor;
        if (parametro == 0x24 && mensaje.length > 10) valorExtendido |= (mensaje[10] & 0x7F) << 7;
        final int valorRecibido = valorExtendido;
        valoresSetup.put(parametro, valor);
        SwingUtilities.invokeLater(() -> {
            if (parametro == 0x4C && killDryGlobal != null && valor <= 1) {
                killDryGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Kill Dry = " + (valor == 1 ? "On" : "Off"));
            } else if (parametro == 0x27 && midiChannelGlobal != null && valor <= 16) {
                midiChannelGlobal.setSelectedIndex(valor == 16 ? 17 : valor + 1);
                if (registrar) log("Setup recibido: MIDI Channel = " + valor);
            } else if (parametro == 0x20 && midiProgramChangeGlobal != null && valor <= 1) {
                midiProgramChangeGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Program Change = " + (valor == 1 ? "On" : "Off"));
            } else if (parametro == 0x2B && midiClockGlobal != null && valor <= 1) {
                midiClockGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: MIDI Clock = " + (valor == 1 ? "On" : "Off"));
            } else if (parametro == 0x28 && sendTunerGlobal != null && valor <= 1) {
                sendTunerGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Send Tuner = " + (valor == 1 ? "On" : "Off"));
            } else if (parametro == 0x22 && sysexIdGlobal != null && valor <= 127) {
                sysexIdGlobal.setValue(valor);
                if (registrar) log("Setup recibido: SysEx ID = " + valor);
            } else if (parametro == 0x23 && pedalTypeGlobal != null && valor >= 1 && valor <= 3) {
                pedalTypeGlobal.setSelectedIndex(valor - 1);
                if (registrar) log("Setup recibido: Pedal Type = " + pedalTypeGlobal.getItemAt(valor - 1));
            } else if (parametro == 0x25 && tunerModeGlobal != null && valor <= 1) {
                tunerModeGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Tuner Mode = " + tunerModeGlobal.getItemAt(valor));
            } else if (parametro == 0x26 && tunerRangeGlobal != null && valor <= 2) {
                tunerRangeGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Tuner Range = " + tunerRangeGlobal.getItemAt(valor));
            } else if (parametro == 0x29 && tapMasterGlobal != null && valor <= 1) {
                tapMasterGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Tap Master = " + tapMasterGlobal.getItemAt(valor));
            } else if (parametro == 0x2A && tapUnitGlobal != null && valor <= 1) {
                tapUnitGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Tap Unit = " + tapUnitGlobal.getItemAt(valor));
            } else if (parametro == 0x2C && fxMuteGlobal != null && valor <= 1) {
                fxMuteGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: FX Mute = " + fxMuteGlobal.getItemAt(valor));
            } else if (parametro == 0x17 && inputTypeGlobal != null && valor <= 1) {
                inputTypeGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Input Type = " + inputTypeGlobal.getItemAt(valor));
            } else if (parametro == 0x1E && outputRangeGlobal != null && valor <= 3) {
                outputRangeGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Output Range = " + outputRangeGlobal.getItemAt(valor));
            } else if (parametro == 0x1C && inputRangeGlobal != null && valor <= 1) {
                inputRangeGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Input Range = " + inputRangeGlobal.getItemAt(valor));
            } else if (parametro == 0x1A && ditherGlobal != null && valor <= 3) {
                ditherGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Dither = " + ditherGlobal.getItemAt(valor));
            } else if (parametro == 0x18 && clockGlobal != null && valor <= 2) {
                clockGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Clock = " + clockGlobal.getItemAt(valor));
            } else if (parametro == 0x1B && statusBitsGlobal != null && valor <= 1) {
                statusBitsGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Status Bits = " + statusBitsGlobal.getItemAt(valor));
            } else if (parametro == 0x19 && digiInGainGlobal != null) {
                int gain = valor > 63 ? valor - 128 : valor;
                if (gain >= -12 && gain <= 12) {
                    digiInGainGlobal.setValue(gain);
                    if (registrar) log("Setup recibido: Digi In Gain = " + gain + " dB");
                }
            } else if (parametro == 0x24 && tunerRefGlobal != null && valorRecibido >= 420 && valorRecibido <= 460) {
                tunerRefGlobal.setValue(valorRecibido);
                if (registrar) log("Setup recibido: Tuner Ref = " + valorRecibido + " Hz");
            } else if (parametro == 0x2D && lcdAngleGlobal != null && valor <= 60) {
                lcdAngleGlobal.setValue(valor);
                if (registrar) log("Setup recibido: LCD Angle = " + valor);
            } else if (parametro == 0x2F && routingGlobal != null && valor <= 3) {
                routingGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Routing = " + routingGlobal.getItemAt(valor));
            } else if (parametro == 0x30 && routingLockGlobal != null && valor <= 1) {
                routingLockGlobal.setSelectedIndex(valor);
                if (registrar) log("Setup recibido: Routing Lock = " + routingLockGlobal.getItemAt(valor));
            }
        });
    }

    private void actualizarSelectorPresets() {
        if (selectorPresetTodos == null) return;
        actualizandoSelectorTodos = true;
        selectorPresetTodos.removeAllItems();
        for (int i = 0; i < listaPresets.size(); i++) selectorPresetTodos.addItem(listaPresets.get(i));
        selectorPresetTodos.setSelectedIndex(-1);
        actualizandoSelectorTodos = false;
    }

    private void seleccionarPreset() {
        int seleccionado = presets.getSelectedIndex();
        if (seleccionado < 0) return;
        indicePresetSeleccionado = buscarIndiceMensaje(seleccionado);
        // Al limpiar o reconstruir el modelo de la lista, Swing puede emitir
        // una selección válida visualmente pero sin mensaje SysEx asociado.
        if (indicePresetSeleccionado < 0 || indicePresetSeleccionado >= sysexRecibido.size()) {
            indicePresetSeleccionado = -1;
            numeroPresetSeleccionado = -1;
            nombrePreset.setText("");
            actualizarNombrePresetTodos();
            presetEnviado.setText("Sin preset User seleccionado");
            estadoEditor.setText("User " + (seleccionado + 1) + " está vacío o no fue recibido en el backup.");
            return;
        }
        byte[] mensaje = sysexRecibido.get(indicePresetSeleccionado);
        if (mensaje.length < 615) {
            indicePresetSeleccionado = -1;
            numeroPresetSeleccionado = -1;
            return;
        }
        numeroPresetSeleccionado = numeroPreset(mensaje);
        log("Preset seleccionado en la aplicación: " + numeroPresetSeleccionado
                + " (lista " + seleccionado + ")");
        if (selectorPresetTodos != null) {
            actualizandoSelectorTodos = true;
            selectorPresetTodos.setSelectedIndex(seleccionado);
            actualizandoSelectorTodos = false;
        }
        nombrePreset.setText(new String(mensaje, 9, 20, StandardCharsets.US_ASCII).trim());
        actualizarNombrePresetTodos();
        threshold.setValue(leerEntero24(mensaje, 552));
        maxDamping.setValue(leerEntero24(mensaje, 556));
        gateRelease.setValue(leerEntero24(mensaje, 560));
        compThreshold.setValue(leerEntero24(mensaje, 164));
        compGain.setValue(leerEntero24(mensaje, 180));
        eqGain.setValue(leerEntero24(mensaje, 572));
        filterMix.setValue(leerEntero24(mensaje, 280));
        filterOut.setValue(leerEntero24(mensaje, 284));
        pitchMix.setValue(leerEntero24(mensaje, 340));
        pitchOut.setValue(leerEntero24(mensaje, 344));
        modulationMix.setValue(leerEntero24(mensaje, 396));
        modulationOut.setValue(leerEntero24(mensaje, 400));
        delayMix.setValue(leerEntero24(mensaje, 472));
        delayOut.setValue(leerEntero24(mensaje, 476));
        delayTime.setValue(leerEntero24(mensaje, 424));
        delayFeedback.setValue(leerEntero24(mensaje, 440));
        reverbMix.setValue(leerEntero24(mensaje, 532));
        reverbOut.setValue(leerEntero24(mensaje, 536));
        reverbDecay.setValue(leerEntero24(mensaje, 488));
        reverbPreDelay.setValue(leerEntero24(mensaje, 492));
        // La vista ALL se construye una sola vez. Hay que copiar aquí los
        // valores del preset recién seleccionado; de lo contrario conserva los
        // valores iniciales de los controles y parece que el editor está vacío.
        cargarEditorCompleto(mensaje);
        estadoEditor.setText("Preset seleccionado. Puedes editar sus parámetros en las pestañas de bloques.");
        if (sincronizacion.isSelected()) {
            enviarCambioPrograma(numeroPresetSeleccionado);
            presetEnviado.setText("MIDI User: CC0=1 + PC " + numeroPresetSeleccionado);
        } else {
            presetEnviado.setText("Sync desactivado: selección local");
        }
    }

    private void copiarPreset() {
        if (indicePresetSeleccionado < 0) {
            estadoEditor.setText("Selecciona primero un preset para copiarlo.");
            return;
        }
        presetCopiado = sysexRecibido.get(indicePresetSeleccionado).clone();
        estadoEditor.setText("Preset " + numeroPresetSeleccionado + " copiado. Selecciona otro y pulsa Pegar.");
        log("Preset " + numeroPresetSeleccionado + " copiado en la memoria temporal.");
    }

    private void prepararPresetUser() {
        if (indicePresetSeleccionado < 0) {
            estadoEditor.setText("Selecciona primero un preset de origen.");
            return;
        }
        int destino = (int) destinoUser.getValue();
        int confirmar = JOptionPane.showConfirmDialog(this,
                "Se preparará una copia del preset actual para User " + destino + ".\n\n" +
                "La aplicación no pulsa STORE. Confirma STORE físicamente en el G-Major 2 para conservarla.",
                "Preparar preset User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (confirmar != JOptionPane.OK_OPTION) return;

        byte[] copia = sysexRecibido.get(indicePresetSeleccionado).clone();
        int codigo = destino + 100;
        copia[7] = (byte) (codigo & 0x7F);
        copia[8] = (byte) ((copia[8] & 0x7E) | ((codigo >> 7) & 1));
        recalcularChecksum(copia);

        int indiceExistente = -1;
        for (int i = 0; i < sysexRecibido.size(); i++) {
            byte[] mensaje = sysexRecibido.get(i);
            if (mensaje.length == 615 && numeroPreset(mensaje) == destino) {
                indiceExistente = i;
                break;
            }
        }
        if (indiceExistente >= 0) sysexRecibido.set(indiceExistente, copia);
        else {
            sysexRecibido.add(copia);
            indiceExistente = sysexRecibido.size() - 1;
        }
        actualizarListaPresets();
        int posicion = posicionListaParaMensaje(indiceExistente);
        if (posicion >= 0) presets.setSelectedIndex(posicion);
        estadoEditor.setText("Copia preparada para User " + destino + ". Usa STORE en el G-Major 2 para guardarla.");
        log("Preset preparado para User " + destino + ". STORE debe confirmarse en el equipo.");
        if (sincronizacion.isSelected() && outputDevice != null && outputDevice.isOpen()) {
            try {
                enviarMensajesSysex(copia);
                new Timer(900, e -> {
                    ((Timer) e.getSource()).stop();
                    enviarCambioPrograma(destino);
                }).start();
            } catch (Exception ex) {
                log("No se pudo enviar el preset User: " + ex.getMessage());
            }
        }
    }

    private void pegarPreset() {
        if (indicePresetSeleccionado < 0) {
            estadoEditor.setText("Selecciona primero el destino del pegado.");
            return;
        }
        if (presetCopiado == null || presetCopiado.length != 615) {
            estadoEditor.setText("No hay ningún preset copiado.");
            return;
        }
        byte[] destino = sysexRecibido.get(indicePresetSeleccionado);
        byte byteSlotBajo = destino[7];
        byte byteSlotAlto = destino[8];
        System.arraycopy(presetCopiado, 1, destino, 1, 612);
        // El destino conserva su número de preset; el nombre y los parámetros
        // sí se copian completos desde el origen.
        destino[7] = byteSlotBajo;
        destino[8] = byteSlotAlto;
        recalcularChecksum(destino);
        actualizarFilaPreset(indicePresetSeleccionado, destino);
        seleccionarPreset();
        enviarPresetSeleccionado();
        estadoEditor.setText("Preset pegado sobre el destino y enviado.");
        log("Preset copiado pegado sobre User " + numeroPresetSeleccionado + ".");
    }

    private void actualizarFilaPreset(int indiceMensaje, byte[] mensaje) {
        int posicion = posicionListaParaMensaje(indiceMensaje);
        if (posicion >= 0) {
            listaPresets.set(posicion, String.format("Preset %3d  —  %s",
                    numeroPreset(mensaje), new String(mensaje, 9, 20, StandardCharsets.US_ASCII).trim()));
        }
    }

    private int numeroPreset(byte[] mensaje) {
        return (((mensaje[8] & 1) << 7) | (mensaje[7] & 127)) - 100;
    }

    private void enviarCambioPrograma(int numeroPreset) {
        if (numeroPreset < 1 || numeroPreset > 128) {
            log("No se envió el preset: número fuera de rango (" + numeroPreset + ").");
            return;
        }
        if (outputDevice == null || !outputDevice.isOpen()) {
            log("No se envió el preset " + numeroPreset + ": conecta primero MIDI OUT.");
            return;
        }
        int ch = (int) canal.getValue() - 1;
        new Thread(() -> {
            try {
                Receiver destino = outputDevice.getReceiver();
                ShortMessage bancoMSB = new ShortMessage(ShortMessage.CONTROL_CHANGE, ch, 0, BANCO_PRESETS_USUARIO);
                ShortMessage programaSeleccionado = new ShortMessage(ShortMessage.PROGRAM_CHANGE, ch, numeroPreset - 1, 0);
                destino.send(bancoMSB, -1);
                log("→ Bank Select CC 0=" + BANCO_PRESETS_USUARIO);
                Thread.sleep(500);
                destino.send(programaSeleccionado, -1);
                log("→ Program Change=" + (numeroPreset - 1) + "; preset User " + numeroPreset);
                log("Preset de usuario " + numeroPreset + " seleccionado (banco " + BANCO_PRESETS_USUARIO + ").");
            } catch (Exception ex) { log("No se pudo seleccionar el preset " + numeroPreset + ": " + ex.getMessage()); }
        }, "seleccion-preset").start();
    }

    private int buscarIndiceMensaje(int posicionLista) {
        if (posicionLista < 0 || posicionLista >= indicesPresetsOrdenados.size()) return -1;
        return indicesPresetsOrdenados.get(posicionLista);
    }

    private void cambiarNombre() {
        if (indicePresetSeleccionado < 0) { estadoEditor.setText("Selecciona primero un preset."); return; }
        byte[] mensaje = sysexRecibido.get(indicePresetSeleccionado);
        byte[] nombre = nombrePreset.getText().getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < 20; i++) mensaje[9 + i] = (byte) (i < nombre.length ? nombre[i] : ' ');
        recalcularChecksum(mensaje);
        int posicionLista = posicionListaParaMensaje(indicePresetSeleccionado);
        if (posicionLista >= 0) {
            listaPresets.set(posicionLista, String.format("Preset %3d  —  %s",
                    numeroPreset(mensaje), new String(mensaje, 9, 20, StandardCharsets.US_ASCII).trim()));
        }
        actualizarNombrePresetTodos();
        enviarPresetSeleccionado();
        estadoEditor.setText("Nombre cambiado y checksum recalculado. Usa 'Guardar datos recibidos'.");
    }

    private void actualizarNombrePresetTodos() {
        if (nombrePresetTodos == null) return;
        String nombre = nombrePreset.getText().trim();
        if (indicePresetSeleccionado < 0 || nombre.isEmpty()) {
            nombrePresetTodos.setText("Preset actual: ninguno");
        } else {
            nombrePresetTodos.setText("Preset " + numeroPresetSeleccionado + ": " + nombre);
        }
    }

    private int posicionListaParaMensaje(int indiceMensaje) {
        return indicesPresetsOrdenados.indexOf(indiceMensaje);
    }

    private void cambiarThreshold() {
        if (indicePresetSeleccionado < 0) { estadoEditor.setText("Selecciona primero un preset."); return; }
        byte[] mensaje = sysexRecibido.get(indicePresetSeleccionado);
        int valor = (int) threshold.getValue();
        escribirEntero24(mensaje, 552, valor);
        recalcularChecksum(mensaje);
        enviarPresetSeleccionado();
        estadoEditor.setText("Threshold cambiado a " + valor + " dB y checksum recalculado.");
    }

    private void aplicarParametrosBasicos() {
        if (indicePresetSeleccionado < 0) {
            estadoEditor.setText("Selecciona primero un preset.");
            return;
        }
        byte[] mensaje = sysexRecibido.get(indicePresetSeleccionado);
        escribirEntero24(mensaje, 552, (int) threshold.getValue());
        escribirEntero24(mensaje, 556, (int) maxDamping.getValue());
        escribirEntero24(mensaje, 560, (int) gateRelease.getValue());
        escribirEntero24(mensaje, 164, (int) compThreshold.getValue());
        escribirEntero24(mensaje, 180, (int) compGain.getValue());
        escribirEntero24(mensaje, 572, (int) eqGain.getValue());
        escribirEntero24(mensaje, 280, (int) filterMix.getValue());
        escribirEntero24(mensaje, 284, (int) filterOut.getValue());
        escribirEntero24(mensaje, 340, (int) pitchMix.getValue());
        escribirEntero24(mensaje, 344, (int) pitchOut.getValue());
        escribirEntero24(mensaje, 396, (int) modulationMix.getValue());
        escribirEntero24(mensaje, 400, (int) modulationOut.getValue());
        escribirEntero24(mensaje, 472, (int) delayMix.getValue());
        escribirEntero24(mensaje, 476, (int) delayOut.getValue());
        escribirEntero24(mensaje, 424, (int) delayTime.getValue());
        escribirEntero24(mensaje, 440, (int) delayFeedback.getValue());
        escribirEntero24(mensaje, 532, (int) reverbMix.getValue());
        escribirEntero24(mensaje, 536, (int) reverbOut.getValue());
        escribirEntero24(mensaje, 488, (int) reverbDecay.getValue());
        escribirEntero24(mensaje, 492, (int) reverbPreDelay.getValue());
        recalcularChecksum(mensaje);
        enviarPresetSeleccionado();
        estadoEditor.setText("Gate, Compressor y EQ aplicados; checksum recalculado.");
    }

    private void enviarPresetSeleccionado() {
        if (indicePresetSeleccionado < 0) {
            estadoEditor.setText("Selecciona primero un preset.");
            return;
        }
        if (!sincronizacion.isSelected()) {
            presetEnviado.setText("Sync desactivado: cambio guardado solo localmente");
            return;
        }
        try {
            enviarMensajesSysex(sysexRecibido.get(indicePresetSeleccionado));
            presetEnviado.setText("Preset " + numeroPresetSeleccionado + " enviado en tiempo real");
            int preset = numeroPresetSeleccionado;
            new Thread(() -> {
                try {
                    // El G-Major necesita terminar de escribir el preset antes de recuperarlo.
                    Thread.sleep(800);
                    enviarCambioPrograma(preset);
                    log("Preset " + preset + " recuperado para actualizar la pantalla.");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }, "recuperar-preset-editado").start();
        } catch (Exception ex) {
            estadoEditor.setText("No se pudo enviar: " + ex.getMessage());
        }
    }

    private int leerEntero24(byte[] mensaje, int offsetRaw) {
        int valor = 0;
        for (int i = 0; i < 4; i++) valor |= (mensaje[1 + offsetRaw + i] & 127) << (7 * i);
        if ((valor & (1 << 23)) != 0) valor |= 0xff000000;
        return valor;
    }

    private void escribirEntero24(byte[] mensaje, int offsetRaw, int valor) {
        int codificado = valor >= 0 ? valor : ((valor & 0x7fffff) | (1 << 23));
        for (int i = 0; i < 4; i++) mensaje[1 + offsetRaw + i] = (byte) ((codificado >> (7 * i)) & 127);
    }

    private void recalcularChecksum(byte[] mensaje) {
        int checksum = 0;
        for (int i = 29; i <= 612; i++) checksum = (checksum + (mensaje[i] & 127)) & 127;
        mensaje[613] = (byte) checksum;
    }

    private void conectar() {
        cerrarMIDI();
        try {
            if (entrada.getSelectedItem() != null) {
                inputDevice = MidiSystem.getMidiDevice((MidiDevice.Info) entrada.getSelectedItem());
                inputDevice.open(); receiver = new Receptor(); transmitter = inputDevice.getTransmitter(); transmitter.setReceiver(receiver);
            }
            if (salida.getSelectedItem() != null) { outputDevice = MidiSystem.getMidiDevice((MidiDevice.Info) salida.getSelectedItem()); outputDevice.open(); }
            estado.setText("Conectado"); log("MIDI conectado.");
            solicitarSetupReal();
            if (sincronizacion.isSelected()) iniciarSincronizacion();
        } catch (Exception ex) { log("Error MIDI: " + ex.getMessage()); cerrarMIDI(); }
    }

    private void enviarPrograma() {
        try {
            int ch=(int) canal.getValue()-1;
            enviar(new ShortMessage(ShortMessage.CONTROL_CHANGE,ch,0,(int)banco.getValue()));
            enviar(new ShortMessage(ShortMessage.PROGRAM_CHANGE,ch,(int)programa.getValue(),0));
        } catch (InvalidMidiDataException e) { log("Mensaje MIDI no válido: "+e.getMessage()); }
    }
    private void enviarCC() {
        try { enviar(new ShortMessage(ShortMessage.CONTROL_CHANGE,(int)canal.getValue()-1,(int)controlador.getValue(),(int)valor.getValue())); }
        catch (InvalidMidiDataException e) { log("Mensaje MIDI no válido: "+e.getMessage()); }
    }
    private void enviar(MidiMessage msg) { try { if(outputDevice==null) throw new Exception("no hay MIDI OUT conectado"); outputDevice.getReceiver().send(msg,-1); log("→ " + describe(msg)); } catch(Exception e){ log("No enviado: "+e.getMessage()); } }

    private void recibirBackup() {
        iniciarSincronizacion();
    }

    /** Solicita los bloques de configuración separados del Bulk Dump de presets. */
    private void solicitarSetupReal() {
        if (outputDevice == null) {
            log("Leer Setup necesita MIDI OUT conectado.");
            return;
        }
        int[] comandos = {0x30, 0x34};
        int[] parametros = {0x17, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x20, 0x22, 0x23,
                0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2F, 0x30, 0x46, 0x47, 0x4C};
        setupRecibido.clear();
        new Thread(() -> {
            try {
                for (int comando : comandos) enviarSysexCorto(comando, 0x00);
                for (int parametro : parametros) enviarSysexCorto(0x47, parametro, 0x01);
                log("→ Solicitud de Setup enviada: Global/MIDI/MOD");
            } catch (Exception ex) {
                log("Error solicitando Setup: " + ex.getMessage());
            }
        }, "solicitud-setup").start();
    }

    private void enviarSysexCorto(int... datos) throws Exception {
        // Cabecera de 6 bytes + datos + F7. No debe quedar ningún 00 extra
        // entre el último dato y el final del mensaje SysEx.
        byte[] bytes = new byte[datos.length + 7];
        bytes[0] = (byte) 0xF0;
        bytes[1] = 0x00; bytes[2] = 0x20; bytes[3] = 0x1F;
        bytes[4] = 0x01; bytes[5] = 0x66;
        for (int i = 0; i < datos.length; i++) bytes[6 + i] = (byte) datos[i];
        bytes[bytes.length - 1] = (byte) 0xF7;
        SysexMessage mensaje = new SysexMessage();
        mensaje.setMessage(bytes, bytes.length);
        outputDevice.getReceiver().send(mensaje, -1);
        Thread.sleep(100);
    }

    private void iniciarSincronizacion() {
        if (inputDevice == null || outputDevice == null) {
            log("Sync necesita MIDI IN y MIDI OUT conectados.");
            return;
        }
        sysexRecibido.clear();
        estado.setText("Sync: esperando Bulk Dump");
        // Se muestra después de las respuestas automáticas de Setup para que
        // sea la última instrucción visible antes de iniciar el Bulk Dump.
        new Timer(5000, e -> {
            ((Timer) e.getSource()).stop();
            log("Sync preparado. Ejecuta MIDI → Bulk Dump → ENTER en el G-Major 2.");
        }).start();
        new Timer(60000, e -> {
            ((Timer) e.getSource()).stop();
            if (sysexRecibido.isEmpty()) {
                log("Sync terminado sin datos SysEx.");
                return;
            }
            actualizarListaPresets();
            estadoEditor.setText(cantidadPresetsRecibidos() + " presets recibidos; se muestran las 100 posiciones User.");
            estado.setText("Sync activo");
            log("Presets cargados desde el G-Major 2. No se guardó ningún backup automáticamente.");
        }).start();
    }
    private void guardarSysex() {
        if (sysexRecibido.isEmpty() && setupRecibido.isEmpty()) { log("No hay datos SysEx recibidos para guardar."); return; }
        JFileChooser f=new JFileChooser(); f.setSelectedFile(new File("gmajor2-prueba.syx"));
        f.setFileFilter(new FileNameExtensionFilter("SysEx (*.syx)","syx"));
        if(f.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) try {
            ByteArrayOutputStream b=new ByteArrayOutputStream();
            for(byte[] x:sysexRecibido) b.write(x);
            for(byte[] x:setupRecibido) if (!contieneMensaje(sysexRecibido, x)) b.write(x);
            Files.write(f.getSelectedFile().toPath(),b.toByteArray());
            log("Backup guardado con presets y Setup: "+f.getSelectedFile());
        } catch(IOException ex){log("Error guardando: "+ex.getMessage());}
    }

    private boolean contieneMensaje(List<byte[]> mensajes, byte[] buscado) {
        for (byte[] mensaje : mensajes) if (Arrays.equals(mensaje, buscado)) return true;
        return false;
    }
    private void restaurarBackup() {
        JFileChooser f=new JFileChooser(); f.setFileFilter(new FileNameExtensionFilter("SysEx (*.syx)","syx"));
        if(f.showOpenDialog(this)==JFileChooser.APPROVE_OPTION && JOptionPane.showConfirmDialog(this,
                "El backup reemplazará los presets del G-Major 2. ¿Continuar?", "Confirmar restauración",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
            try { byte[] data=Files.readAllBytes(f.getSelectedFile().toPath()); enviarMensajesSysex(data); log("Backup enviado. Espera a que termine antes de apagar el equipo."); } catch(Exception ex){log("Error restaurando: "+ex.getMessage());}
    }

    private void enviarMensajesSysex(byte[] datos) throws Exception {
        if (outputDevice == null) throw new Exception("no hay MIDI OUT conectado");
        List<byte[]> mensajes = new ArrayList<>();
        int inicio = -1;
        for (int i = 0; i < datos.length; i++) {
            int byteActual = datos[i] & 255;
            if (byteActual == 0xf0) inicio = i;
            if (byteActual == 0xf7 && inicio >= 0) {
                byte[] mensaje = new byte[i - inicio + 1];
                System.arraycopy(datos, inicio, mensaje, 0, mensaje.length);
                mensajes.add(mensaje); inicio = -1;
            }
        }
        if (mensajes.isEmpty()) throw new Exception("el archivo no contiene mensajes SysEx completos");
        new Thread(() -> {
            try {
                Receiver destino = outputDevice.getReceiver();
                for (byte[] bytes : mensajes) {
                    SysexMessage mensaje = new SysexMessage();
                    mensaje.setMessage(bytes, bytes.length);
                    destino.send(mensaje, -1);
                    log("→ SysEx enviado (" + bytes.length + " bytes)");
                    Thread.sleep(100);
                }
                destino.close();
            } catch (Exception ex) { log("Error enviando SysEx: " + ex.getMessage()); }
        }, "envio-sysex").start();
    }

    private class Receptor implements Receiver {
        public void send(MidiMessage message, long time) {
            if (message instanceof SysexMessage) {
                byte[] x = message.getMessage().clone();
                sysexRecibido.add(x);
                if (esMensajeSetup(x)) setupRecibido.add(x);
                // Los valores se aplican a los controles, pero no se repiten
                // uno por uno en el registro: la vista Global/MIDI/MOD es su
                // representación principal.
                actualizarKillDryDesdeMensaje(x, false);
                if (x.length == 615) {
                    log("← SysEx recibido (615 bytes)");
                    // Mostrar cada preset en cuanto llega. El equipo puede
                    // enviar Setup antes o durante el Bulk Dump; no hay que
                    // esperar al temporizador de 60 segundos para actualizar
                    // la biblioteca visible.
                    SwingUtilities.invokeLater(() -> {
                        actualizarListaPresets();
                        int cantidad = 0;
                        for (byte[] recibido : sysexRecibido) {
                            if (recibido.length == 615) cantidad++;
                        }
                        estadoEditor.setText(cantidad + " presets recibidos desde el G-Major 2.");
                    });
                } else {
                    int comando = x.length > 6 ? x[6] & 0x7F : -1;
                    if (x.length == 209 && comando == 0x31) {
                        log("← Bloque Global/MIDI recibido (209 bytes)");
                    } else if (x.length == 181 && comando == 0x35) {
                        log("← Bloque MOD recibido (181 bytes)");
                    } else if (!esMensajeSetup(x)) {
                        log("← SysEx auxiliar recibido (" + x.length + " bytes)");
                    }
                }
            } else {
                log("← " + describe(message));
            }
        }
        public void close() {}
    }

    private boolean esMensajeSetup(byte[] mensaje) {
        if (mensaje.length < 8) return false;
        int comando = mensaje[6] & 0x7F;
        return comando == 0x31 || comando == 0x35 || comando == 0x32;
    }
    private String describe(MidiMessage m){ byte[] b=m.getMessage(); StringBuilder s=new StringBuilder(); for(int i=0;i<m.getLength();i++)s.append(String.format("%02X ",b[i]&255)); return s.toString().trim(); }
    private void log(String s){
        String mensaje = idiomaIngles ? traducirMensajeRegistro(s) : s;
        SwingUtilities.invokeLater(()->registro.append(mensaje+"\n"));
    }
    private void cerrarMIDI(){ try{if(transmitter!=null)transmitter.close();if(inputDevice!=null)inputDevice.close();if(outputDevice!=null)outputDevice.close();}catch(Exception ignored){} transmitter=null;inputDevice=null;outputDevice=null; }
    public static void main(String[] args){ SwingUtilities.invokeLater(()->new GMajor2Controller().setVisible(true)); }
}
