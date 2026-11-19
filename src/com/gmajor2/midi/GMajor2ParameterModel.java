package com.gmajor2.midi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Modelo de parámetros del formato de preset SysEx del G-Major 2. */
public final class GMajor2ParameterModel {
    public static final class Definition {
        public final String block;
        public final String key;
        public final String label;
        public final int offset;
        public final int minimum;
        public final int maximum;
        public final String unit;
        /** Desplazamiento del índice SysEx para rangos discretos del firmware. */
        public final int optionOffset;
        public final List<String> options;

        Definition(String block, String key, String label, int offset, int minimum,
                   int maximum, String unit, String... options) {
            this.block = block; this.key = key; this.label = label; this.offset = offset;
            this.minimum = minimum; this.maximum = maximum; this.unit = unit;
            this.optionOffset = 0;
            this.options = Collections.unmodifiableList(Arrays.asList(options));
        }
        Definition(String block, String key, String label, int offset, int minimum,
                   int maximum, String unit, int optionOffset, String... options) {
            this.block = block; this.key = key; this.label = label; this.offset = offset;
            this.minimum = minimum; this.maximum = maximum; this.unit = unit;
            this.optionOffset = optionOffset;
            this.options = Collections.unmodifiableList(Arrays.asList(options));
        }
        public boolean isChoice() { return !options.isEmpty(); }
    }

    private static final List<Definition> DEFINICIONES = new ArrayList<>();

    static {
        // Orden del preset SysEx del G-Major 2: Compressor, Filter, Pitch,
        // Modulation, Delay, Reverb, Gate y EQ.
        add("GATE", "gate.mode", "Mode", 548, 0, 2, "", "Off", "Hard", "Soft");
        add("GATE", "gate.threshold", "Threshold", 552, -60, 0, "dB");
        add("GATE", "gate.damping", "Max Damping", 556, 0, 90, "dB");
        add("GATE", "gate.release", "Release", 560, 3, 200, "dB/s");
        add("GATE", "gate.enabled", "Effect On", 608, 0, 1, "", "On", "Off");
        add("COMPRESSOR", "comp.threshold", "Threshold", 164, -30, 0, "dB");
        add("COMPRESSOR", "comp.ratio", "Ratio", 168, 0, 15, "", "Off", "1.12:1", "1.25:1", "1.40:1", "1.60:1", "1.80:1", "2:1", "2.5:1", "3.2:1", "4:1", "5.6:1", "8:1", "16:1", "32:1", "64:1", "Inf:1");
        addIndexed("COMPRESSOR", "comp.attack", "Attack", 172, 3, 23, "ms", 3,
                "1.0", "1.4", "2.0", "3.0", "5.0", "7.0", "10", "14", "20", "30", "50", "70", "100", "140", "200", "300", "500", "700", "1000", "1400", "2000");
        addIndexed("COMPRESSOR", "comp.release", "Release", 176, 3, 23, "ms", 3,
                "1.0", "1.4", "2.0", "3.0", "5.0", "7.0", "10", "14", "20", "30", "50", "70", "100", "140", "200", "300", "500", "700", "1000", "1400", "2000");
        add("COMPRESSOR", "comp.gain", "Gain", 180, -6, 6, "dB");
        add("COMPRESSOR", "comp.enabled", "Effect On", 224, 0, 1, "", "On", "Off");
        add("EQ", "eq.band1.frequency", "Band 1 Frequency [index]", 568, 25, 215, "");
        add("EQ", "eq.band1.gain", "Band 1 Gain", 572, -12, 12, "dB");
        addIndexed("EQ", "eq.band1.width", "Band 1 Width", 576, 3, 16, "oct", 3,
                "0.2", "0.25", "0.32", "0.4", "0.5", "0.63", "0.8", "1.0", "1.25", "1.6", "2.0", "2.5", "3.2", "4.0");
        add("EQ", "eq.band2.frequency", "Band 2 Frequency [index]", 580, 25, 215, "");
        add("EQ", "eq.band2.gain", "Band 2 Gain", 584, -12, 12, "dB");
        addIndexed("EQ", "eq.band2.width", "Band 2 Width", 588, 3, 16, "oct", 3,
                "0.2", "0.25", "0.32", "0.4", "0.5", "0.63", "0.8", "1.0", "1.25", "1.6", "2.0", "2.5", "3.2", "4.0");
        add("EQ", "eq.band3.frequency", "Band 3 Frequency [index]", 592, 25, 215, "");
        add("EQ", "eq.band3.gain", "Band 3 Gain", 596, -12, 12, "dB");
        addIndexed("EQ", "eq.band3.width", "Band 3 Width", 600, 3, 16, "oct", 3,
                "0.2", "0.25", "0.32", "0.4", "0.5", "0.63", "0.8", "1.0", "1.25", "1.6", "2.0", "2.5", "3.2", "4.0");
        // En EQ el firmware usa la polaridad inversa de los demás bloques.
        add("EQ", "eq.enabled", "Effect On", 564, 0, 1, "", "Off", "On");

        // Filter, Pitch y Modulation. Los índices de elección son los del modelo G-Major 2.
        add("FILTER", "filter.type", "Algorithm", 228, 0, 6, "",
                "Wahwah", "Touch Wah", "Auto Wah", "Resonance Filter",
                "Touch Resonance Filter", "Tremolo", "Panner");
        add("FILTER", "filter.order", "Filter Order", 232, 0, 1, "", "2nd", "4th");
        add("FILTER", "filter.sensitivity", "Sensitivity", 236, 0, 10, "");
        add("FILTER", "filter.response", "Response", 240, 0, 2, "", "Slow", "Medium", "Fast");
        add("FILTER", "filter.freqMax", "Auto Resonance Freq. Max [index]", 252, 0, 100, "");
        add("FILTER", "filter.hiCut", "Hi Cut [index]", 272, 0, 60, "");
        add("FILTER", "filter.resonance", "Resonance", 276, 0, 100, "%");
        add("FILTER", "filter.speed", "Speed [index]", 244, 0, 200, "");
        add("FILTER", "filter.depth", "Depth", 248, 0, 100, "%");
        add("FILTER", "filter.feedback", "Phaser Feedback / Tremolo LFO Pulse Width", 260, -100, 100, "%");
        add("FILTER", "filter.range", "Phaser Range", 264, 0, 1, "", "Low", "High");
        add("FILTER", "filter.phaseReverse", "Phaser Phase Reverse", 268, 0, 1, "", "On", "Off");
        add("FILTER", "filter.tremoloType", "Tremolo Type", 256, 0, 1, "", "Soft", "Hard");
        add("FILTER", "filter.enabled", "Effect On", 288, 0, 1, "", "On", "Off");
        add("FILTER", "filter.mix", "Mix", 280, 0, 100, "%");
        add("FILTER", "filter.out", "Out Level", 284, -100, 0, "dB");
        add("PITCH", "pitch.type", "Algorithm", 292, 0, 4, "",
                "Detune", "Whammy", "Octaver", "Pitch Shifter", "Intelligent Pitch Shifter");
        add("PITCH", "pitch.voice1", "Shifter Voice 1", 296, -2400, 2400, "cents");
        add("PITCH", "pitch.voice2", "Shifter Voice 2", 300, -2400, 2400, "cents");
        add("PITCH", "pitch.direction", "Direction", 332, 0, 1, "", "Down", "Up");
        add("PITCH", "pitch.octave", "Octave Range", 336, 0, 2, "", "1 Oct", "2 Oct", "3 Oct");
        add("PITCH", "pitch.pan1", "Shifter Pan 1", 304, -50, 50, "");
        add("PITCH", "pitch.pan2", "Shifter Pan 2", 308, -50, 50, "");
        add("PITCH", "pitch.delay1", "Delay 1", 312, 0, 350, "ms");
        add("PITCH", "pitch.delay2", "Delay 2", 316, 0, 350, "ms");
        add("PITCH", "pitch.feedback1", "Feedback 1", 320, 0, 100, "%");
        add("PITCH", "pitch.feedback2", "Feedback 2", 324, 0, 100, "%");
        add("PITCH", "pitch.level1", "Level 1", 328, -100, 0, "%");
        add("PITCH", "pitch.level2", "Level 2", 332, -100, 0, "%");
        add("PITCH", "pitch.whammyPitch", "Whammy Pitch", 328, 0, 100, "%");
        add("PITCH", "pitch.mix", "Mix", 340, 0, 100, "%");
        add("PITCH", "pitch.out", "Out Level", 344, -100, 0, "dB");
        add("PITCH", "pitch.enabled", "Effect On", 352, 0, 1, "", "On", "Off");
        add("MOD", "mod.type", "Algorithm", 356, 0, 12, "",
                "Classic Chorus", "Advanced Chorus", "Tri-Chorus Normal", "Tri-Chorus Asymmetric",
                "Classic Flanger", "Advanced Flanger", "Through Zero Flanger Negative",
                "Through Zero Flanger Positive", "Vibrato", "Vintage Phaser", "Smooth Phaser",
                "Vintage Univibe", "Modern Univibe");
        add("MOD", "mod.speed", "Speed [index]", 360, 0, 200, "");
        add("MOD", "mod.depth", "Depth", 364, 0, 100, "%");
        add("MOD", "mod.tempo", "Tempo", 368, 0, 16, "");
        add("MOD", "mod.hiCut", "Hi Cut [index]", 372, 0, 60, "");
        add("MOD", "mod.feedback", "Feedback", 376, -100, 100, "%");
        add("MOD", "mod.delay", "Delay", 384, 1, 500, "/10 ms");
        add("MOD", "mod.goldenRatio", "Golden Ratio", 388, 0, 1, "", "On", "Off");
        add("MOD", "mod.phaseReverse", "Phase Reverse", 392, 0, 1, "", "On", "Off");
        add("MOD", "mod.feedbackHiCut", "Feedback Hi Cut [index]", 380, 0, 60, "");
        add("MOD", "mod.mix", "Mix", 396, 0, 100, "%");
        add("MOD", "mod.out", "Out Level", 400, -100, 0, "dB");
        add("MOD", "mod.enabled", "Effect On", 416, 0, 1, "", "On", "Off");

        // Delay, Reverb y parámetros generales contenidos en cada preset.
        add("DELAY", "delay.type", "Algorithm", 420, 0, 3, "", "Ping Pong", "Dynamic", "Dual", "Reverse");
        add("DELAY", "delay.time1", "Delay Time 1", 424, 0, 1800, "ms");
        add("DELAY", "delay.time2", "Dual Delay Time 2", 428, 0, 1800, "ms");
        add("DELAY", "delay.tempo", "Tempo", 432, 0, 16, "", "Ignored", "1", "1/2D", "1/2", "1/2T", "1/4D", "1/4", "1/4T", "1/8D", "1/8", "1/8T", "1/16D", "1/16", "1/16T", "1/32D", "1/32", "1/32T");
        add("DELAY", "delay.width", "Ping Pong Width", 436, 0, 100, "%");
        add("DELAY", "delay.tempo2", "Dual Tempo 2", 436, 0, 16, "");
        add("DELAY", "delay.feedback1", "Feedback 1", 440, 0, 100, "%");
        add("DELAY", "delay.feedback2", "Feedback 2", 444, 0, 100, "%");
        add("DELAY", "delay.fbHiCut", "Feedback Hi Cut [index]", 448, 0, 60, "");
        add("DELAY", "delay.fbLoCut", "Feedback Lo Cut [index]", 452, 0, 40, "");
        add("DELAY", "delay.offset", "Dynamic Offset / Dual Pan 1", 456, -200, 200, "ms");
        add("DELAY", "delay.sensitivity", "Dynamic Sensitivity / Dual Pan 2", 460, -50, 0, "dB");
        add("DELAY", "delay.pan1", "Dual Pan 1 (alternative)", 456, -50, 50, "");
        add("DELAY", "delay.pan2", "Dual Pan 2 (alternative)", 460, -50, 50, "");
        add("DELAY", "delay.damping", "Dynamic Damping", 464, 0, 100, "%");
        add("DELAY", "delay.release", "Dynamic Release [index]", 468, 11, 21, "");
        add("DELAY", "delay.mix", "Mix", 472, 0, 100, "%");
        add("DELAY", "delay.out", "Out Level", 476, -100, 0, "dB");
        add("DELAY", "delay.enabled", "Effect On", 480, 0, 1, "", "On", "Off");
        add("REVERB", "reverb.type", "Algorithm", 484, 0, 3, "", "Spring", "Hall", "Room", "Plate");
        add("REVERB", "reverb.decay", "Decay", 488, 1, 200, "0.1 s");
        add("REVERB", "reverb.predelay", "Pre-Delay", 492, 0, 100, "ms");
        add("REVERB", "reverb.shape", "Shape", 496, 0, 2, "", "Round", "Curved", "Square");
        add("REVERB", "reverb.size", "Size", 500, 0, 7, "",
                "Box", "Tiny", "Small", "Medium", "Large", "Ex Large", "Grand", "Huge");
        add("REVERB", "reverb.hiColor", "Hi Color", 504, 0, 6, "",
                "Wool", "Warm", "Real", "Clear", "Bright", "Crisp", "Grand");
        add("REVERB", "reverb.hiFactor", "Hi Factor", 508, -25, 25, "");
        add("REVERB", "reverb.loColor", "Lo Color", 512, 0, 6, "",
                "Thick", "Round", "Real", "Light", "Tight", "Thin", "No Base");
        add("REVERB", "reverb.loFactor", "Lo Factor", 516, -25, 25, "");
        add("REVERB", "reverb.roomLevel", "Room Level", 520, 0, 100, "%");
        add("REVERB", "reverb.reverbLevel", "Reverb Level", 524, 0, 100, "%");
        add("REVERB", "reverb.diffuse", "Diffuse", 528, -25, 25, "");
        add("REVERB", "reverb.mix", "Mix", 532, 0, 100, "%");
        add("REVERB", "reverb.out", "Out Level", 536, -100, 0, "dB");
        add("REVERB", "reverb.enabled", "Effect On", 544, 0, 1, "", "On", "Off");
        add("PRESET", "preset.tapTempo", "Tap Tempo", 160, 100, 3000, "ms");
    }

    private static void add(String block, String key, String label, int offset, int min,
                            int max, String unit, String... options) {
        DEFINICIONES.add(new Definition(block, key, label, offset, min, max, unit, options));
    }

    private static void addIndexed(String block, String key, String label, int offset, int min,
                                   int max, String unit, int optionOffset, String... options) {
        DEFINICIONES.add(new Definition(block, key, label, offset, min, max, unit,
                optionOffset, options));
    }

    public static List<Definition> all() { return Collections.unmodifiableList(DEFINICIONES); }

    public static List<Definition> forBlock(String block) {
        List<Definition> result = new ArrayList<>();
        for (Definition definition : DEFINICIONES) if (definition.block.equals(block)) result.add(definition);
        return result;
    }
}
