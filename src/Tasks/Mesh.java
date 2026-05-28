package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.InputOutput;
import Assistant.RemoteExecution;
import Assistant.WorkflowState;
import javax.swing.JOptionPane;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.base.neo.NeoObjectVector;
import star.common.FvRepresentation;
import star.common.Region;

@StarAssistantTask(
        display = "Mesh",
        contentPath = "XHTML/Mesh.xhtml",
        controller = Mesh.AssistantTaskController.class
)
public class Mesh extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        @Override
        protected void conditionUpdate() {
            FvRepresentation representation = WorkflowState.volumeMesh(sim);
            if (representation != null) {
                addToTaskLookup(representation);
                printMessage("Added Representation '" + representation.getPresentationName() + "' to lookup.");
            }
        }

        public void diagnoseMesh() {
            refresh();
            FvRepresentation volumeMesh = WorkflowState.volumeMesh(sim);
            Region airways = WorkflowState.airwaysRegion(sim);
            if (volumeMesh == null || airways == null) {
                printWarning("A volume mesh and the '" + WorkflowState.AIRWAYS_REGION + "' region are required for mesh diagnostics.");
                return;
            }
            volumeMesh.generateMeshReport(new NeoObjectVector(new Object[]{airways}));
            printMessage("Mesh diagnostics generated.");
        }

        public void runSurface() {
            refresh();
            if (!confirmLocalRun("surface mesh generation")) {
                printWarning("Surface mesh generation aborted.");
                return;
            }
            InputOutput.saveSimulation(sim);
            sim.getMeshPipelineController().generateSurfaceMesh();
            InputOutput.saveSimulation(sim);
            printMessage("Surface mesh created.");
        }

        public void runVolumeLocally() {
            refresh();
            if (!confirmLocalRun("volume mesh generation")) {
                printWarning("Volume mesh generation aborted.");
                return;
            }
            InputOutput.saveSimulation(sim);
            sim.getMeshPipelineController().generateVolumeMesh();
            InputOutput.saveSimulation(sim);
            conditionUpdate();
            printMessage("Volume mesh created.");
        }

        public void runVolumeRemotely() {
            refresh();
            RemoteExecution.runVolumeRemotely(this);
        }

        private boolean confirmLocalRun(String action) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "To avoid crashing " + action + ", do not interact with the simulation while it runs locally.",
                    "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            return choice == JOptionPane.OK_OPTION;
        }
    }
}
