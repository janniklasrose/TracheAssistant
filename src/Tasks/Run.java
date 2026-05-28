package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.InputOutput;
import Assistant.RemoteExecution;
import Assistant.WorkflowState;
import javax.swing.JOptionPane;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Run",
        contentPath = "XHTML/Run.xhtml",
        controller = Run.AssistantTaskController.class
)
public class Run extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        @Override
        protected void conditionUpdate() {
            if (WorkflowState.isInitialSolution(sim.getSolution())) {
                addToTaskLookup(sim.getSolution());
                printMessage("Added Solution to lookup.");
            }
        }

        public void initializeSolution() {
            refresh();
            sim.getSolution().initializeSolution();
            addToTaskLookup(sim.getSolution());
            printMessage("Solution initialized.");
        }

        public void runDirectly() {
            refresh();
            int choice = JOptionPane.showConfirmDialog(null,
                    "To avoid crashing the solver execution, do not interact with the simulation while it runs locally.",
                    "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                printWarning("Local run aborted.");
                return;
            }

            InputOutput.saveSimulation(sim);
            sim.getSimulationIterator().run();
            InputOutput.saveSimulation(sim);
            conditionUpdate();
            printMessage("Run finished.");
        }

        public void createBsub() {
            refresh();
            RemoteExecution.createBsub(this);
        }
    }
}
