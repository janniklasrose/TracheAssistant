package Assistant;

import star.assistant.ui.FunctionTaskController;
import star.common.Simulation;

/**
 * Extends the STAR-CCM+ {@link FunctionTaskController} to provide common functionality.
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

}
