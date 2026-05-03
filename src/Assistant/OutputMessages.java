package Assistant;

// Writing to the Output window

public class OutputMessages {

    public void printMessage(String message) {
        String msg;
        if (ConditionManager.CURRENT_TASK == TASK_ID) {
            msg = message;
        } else {
            ConditionManager.CURRENT_TASK = TASK_ID;
            msg = "\n---------- Task: 00 - Introduction ----------\n" + message;
        }
        printMessage(msg, false);
    }

    private void printMessage(String message, boolean dialog) {
        sim.println(message);
        if (dialog) {
            JOptionPane.showMessageDialog(null, message, "MESSAGE!", JOptionPane.INFORMATION_MESSAGE, null);
        }
    }

    public void printWarning(String warning) {
        printWarning(warning, false);
    }

    private void printWarning(String warning, boolean dialog) {
        String messageWithoutColor = "::: WARNING: " + warning + " :::";
        String messageWithColor = "<html><font color=#ff8000>" + messageWithoutColor + "</font></html>";
        printMessage(messageWithoutColor);
        if (dialog) {
            JOptionPane.showMessageDialog(null, messageWithColor, "WARNING!", JOptionPane.WARNING_MESSAGE, null);
        }
    }

    public void printError(String error) {
        printError(error, true);
    }

    private void printError(String error, boolean dialog) {
        String messageWithoutColor = "### ERROR: " + error + " ###";
        String messageWithColor = "<html><font color=#ff0000>" + messageWithoutColor + "</font></html>";
        printMessage(messageWithoutColor);
        if (dialog) {
            JOptionPane.showMessageDialog(null, messageWithColor, "ERROR!", JOptionPane.ERROR_MESSAGE, null);
        }
    }

}
