package Assistant;

import javax.swing.JOptionPane;
import star.common.Simulation;

public final class OutputMessages {

    private OutputMessages() {
    }

    public static void printMessage(Simulation sim, String message) {
        sim.println(message);
    }

    public static void printWarning(Simulation sim, String warning) {
        sim.println("[WARNING] " + warning);
    }

    public static void printError(Simulation sim, String error) {
        sim.println("[ERROR] " + error);
        JOptionPane.showMessageDialog(null, error, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
