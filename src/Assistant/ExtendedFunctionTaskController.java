package Assistant;

import javax.swing.JOptionPane;
import star.assistant.ui.FunctionTaskController;
import star.base.neo.NamedObject;
import star.common.Simulation;

/**
 * Extends the STAR-CCM+ {@link FunctionTaskController} to provide common
 * functionality.
 */
public class ExtendedFunctionTaskController extends FunctionTaskController {

    /**
     * Allows for interaction with an open STAR-CCM+ simulation.
     */
    public Simulation sim;

    /**
     * Refreshes {@link #sim}.
     */
    public void refresh() {
        sim = getActiveSimulation();
    }

    public void print(String message) {
        if (sim == null) {
            refresh();
        }
        sim.println(message);
    }

    public void printMessage(String message) {
        printInfo(message);
    }

    public void printInfo(String message) {
        print(message);
    }

    public void printDebug(String message) {
        message = "[DEBUG] " + message;
        print(message);
    }

    public void printWarning(String message) {
        message = "[WARNING] " + message;
        print(message);
    }

    public void printError(String message) {
        message = "[ERROR] " + message;
        print(message);
    }

    public int promptInt(String message, int defaultValue) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, message, defaultValue);
            if (input == null) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                printWarning("Expected an integer value for '" + message + "'.");
            }
        }
    }

    public double promptDouble(String message, double defaultValue) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, message, defaultValue);
            if (input == null) {
                return defaultValue;
            }
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException ex) {
                printWarning("Expected a numeric value for '" + message + "'.");
            }
        }
    }

    public String promptString(String message, String defaultValue) {
        String input = JOptionPane.showInputDialog(null, message, defaultValue);
        if (input == null || input.trim().isEmpty()) {
            return defaultValue;
        }
        return input.trim();
    }

    public String promptOption(String title, String message, String[] options, String defaultValue) {
        Object selected = JOptionPane.showInputDialog(null, message, title,
                JOptionPane.QUESTION_MESSAGE, null, options, defaultValue);
        if (selected == null) {
            return defaultValue;
        }
        return selected.toString();
    }

    protected void conditionUpdate() {
        // do nothing, but @Override this
    }

    public void forceConditionUpdate() {
        refresh();
        printDebug("PREVIOUS STATE:"); printLookup();
        conditionUpdate();
        printDebug("UPDATED STATE:"); printLookup();
        printInfo("Updated");
    }

    @Override // improve readability of the printed lookup
    public void printLookup() {
        // super.printLookup: Debugging function that will print the contents of the lookup to the output window.
        String lookupContents = "{ ";
        for (NamedObject object_i : lookupObjects(NamedObject.class)) {
            lookupContents += object_i.getClass().getCanonicalName()
                    + ":'" + object_i.getPresentationName() + "'";
        }
        if (lookupContents.length() > 2) {
            lookupContents = lookupContents.substring(0, lookupContents.length() - 2);
        }
        lookupContents += " }";
        sim.println("Lookup Contents for TaskController := " + lookupContents);
    }

}
