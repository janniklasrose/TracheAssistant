package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.InputOutput;
import Assistant.WorkflowState;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JOptionPane;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.common.CompositePart;
import star.common.GeometryPart;
import star.meshing.PartImportManager;
import star.meshing.SimpleShapePart;
import star.vis.Scene;

@StarAssistantTask(
        display = "Geometry",
        contentPath = "XHTML/Geometry.xhtml",
        controller = Geometry.AssistantTaskController.class
)
public class Geometry extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        @Override
        protected void conditionUpdate() {
            for (GeometryPart part : sim.getGeometryPartManager().getParts()) {
                if (WorkflowState.isTracheaPart(part)) {
                    addToTaskLookup(part);
                    print("Added GeometryPart '" + part.getPresentationName() + "' to lookup.");
                }
            }
        }

        public void importSurfaceMesh() {
            refresh();
            File file = InputOutput.openFile(sim, "Open mesh file", "");
            if (file == null) {
                return;
            }

            try {
                InputOutput.saveSimulation(sim);
                sim.get(PartImportManager.class).importCadPart(file.toString(), "AllEdges",
                        30.0, 2, false, 1.0E-5, false, false);
                printMessage("Surface mesh imported.");
            } catch (RuntimeException ex) {
                printWarning("Could not import selected part: " + ex.getMessage());
            }
        }

        public void createGeometryScene() {
            refresh();
            String sceneName = "Geometry_all";
            Scene scene = sim.getSceneManager().hasObject(sceneName);
            if (scene == null) {
                scene = sim.getSceneManager().createScene("Geometry Scene", "Outline", "Geometry", 1);
                scene.setPresentationName(sceneName);
                scene.initializeAndWait();
            }
            scene.open(true);
            scene.resetCamera();
            selectAndExpandNodeExclusive(scene);
            printMessage("Geometry scene is ready. Adjust displayers and camera as needed.");
        }

        public void selectCabin() {
            refresh();
            Collection<GeometryPart> parts = sim.getGeometryPartManager().getParts();
            ArrayList<String> selectableNames = new ArrayList<>();
            String defaultName = null;

            for (GeometryPart part : parts) {
                if (part instanceof CompositePart || part instanceof SimpleShapePart) {
                    continue;
                }
                selectableNames.add(part.getPresentationName());
                if (WorkflowState.isTracheaPart(part)) {
                    defaultName = part.getPresentationName();
                }
            }

            if (selectableNames.isEmpty()) {
                printWarning("No eligible geometry part exists.");
                return;
            }
            if (defaultName == null) {
                defaultName = selectableNames.get(0);
            }

            Object selectedName = JOptionPane.showInputDialog(null,
                    "Select the part to be named '" + WorkflowState.TRACHEA_PART + "':",
                    "Select trachea", JOptionPane.QUESTION_MESSAGE, null,
                    selectableNames.toArray(), defaultName);
            if (selectedName == null) {
                printWarning("Part selection was aborted.");
                return;
            }

            GeometryPart oldTrachea = WorkflowState.tracheaPart(sim);
            GeometryPart newTrachea = sim.getGeometryPartManager().getPart(selectedName.toString());
            if (oldTrachea != null && oldTrachea != newTrachea) {
                oldTrachea.setPresentationName(WorkflowState.TRACHEA_PART + " (old)");
            }
            newTrachea.setPresentationName(WorkflowState.TRACHEA_PART);
            addToTaskLookup(newTrachea);
            selectAndExpandNodeExclusive(newTrachea);
            printMessage("Trachea part selected.");
        }
    }
}
