package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Run",
        contentPath = "XHTML/Run.xhtml",
        controller = Run.AssistantTaskController.class
)
public class Run extends Task {

    public Run() {
        //setPreconditions();
        //setPostconditions();
    }

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        public void initializeSolution() {
            refresh();
            sim.getSolution().initializeSolution();
            printMessage("Solution has been successfully initialized!");
        }

    }

}

// Pre: ConditionManager.MeshPresent_Cabin
// Post: ConditionManager.SolutionPresent_Initial

//
import star.common.Simulation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

import javax.swing.JOptionPane;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.common.FvRepresentation;

class ADD {

        public void updateConditions() {
            if (ConditionManager.satisfiesCondition_SolutionPresent_Initial(sim.getSolution())){
                addToTaskLookup(sim.getSolution());
                printMessage("Added Solution to Lookup!");
            }
        }
        public void runDirectly(){

            refresh();

            // ----------------------------------------------------------------------------------

            switch (JOptionPane.showConfirmDialog(null, "To avoid crashing the solver execution, do not interact with the simulation!", "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null)) {
                case JOptionPane.YES_OPTION:
                    break;
                default:
                    switch (JOptionPane.showConfirmDialog(null, "Do you want to execute the whole solver in batch?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null)) {
                        case JOptionPane.YES_OPTION:
                            createBsub();
                            return;
                        default:
                            printWarning("Running the simulation ABORT-ed by user!");
                            return;
                    }
            }

            save();
            sim.getSimulationIterator().run();
            save();

            // ----------------------------------------------------------------------------------

            printMessage("Run finished!");

        }
}
}
