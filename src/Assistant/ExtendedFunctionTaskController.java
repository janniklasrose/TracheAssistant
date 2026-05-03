package Assistant;

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
        sim.println(message);
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
