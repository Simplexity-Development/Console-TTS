package simplexity.audio.virtualmic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import simplexity.console.Logging;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class PlatformAudioRouter {

    private static final Logger logger = LoggerFactory.getLogger(PlatformAudioRouter.class);

    public enum OS {
        WINDOWS, LINUX, MAC, UNKNOWN
    }

    private static final String[] WINDOWS_VIRTUAL_AUDIO_NAMES = {
            "cable input", "vb-audio", "virtual audio cable"
    };

    private static final String[] LINUX_VIRTUAL_AUDIO_NAMES = {
            "virtualmic", "null sink", "monitor"
    };

    private static final String[] MAC_VIRTUAL_AUDIO_NAMES = {
            "blackhole", "loopback audio"
    };

    public static OS getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return OS.WINDOWS;
        if (os.contains("mac")) return OS.MAC;
        if (os.contains("nux") || os.contains("nix")) return OS.LINUX;
        return OS.UNKNOWN;
    }

    public static Mixer getPreferredOutputMixer() {
        OS os = getOperatingSystem();
        String[] availableDevices;

        switch (os) {
            case WINDOWS -> availableDevices = WINDOWS_VIRTUAL_AUDIO_NAMES;
            case LINUX -> availableDevices = LINUX_VIRTUAL_AUDIO_NAMES;
            case MAC -> availableDevices = MAC_VIRTUAL_AUDIO_NAMES;
            default ->  availableDevices = new String[0];
        }

        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            String name = info.getName().toLowerCase();
            for (String audioName : availableDevices) {
                if (name.contains(audioName)) {
                    Logging.log(logger, "[Router] Found Virtual device: " + info.getName(), Level.INFO);
                    return AudioSystem.getMixer(info);
                }
            }
        }

        Logging.log(logger, "Unable to find a virtual device, using the default mixer.", Level.INFO);
        return AudioSystem.getMixer(null);
    }

    public static SourceDataLine getOutputLine(AudioFormat format) throws LineUnavailableException {
        Mixer mixer = getPreferredOutputMixer();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!mixer.isLineSupported(info)) {
            Logging.log(logger, "Preferred mixer does not support audio format " + format.toString() + " using system default.", Level.WARN);
            return AudioSystem.getSourceDataLine(format);
        }

        SourceDataLine line = (SourceDataLine) mixer.getLine(info);
        line.open(format);
        return line;
    }
}
