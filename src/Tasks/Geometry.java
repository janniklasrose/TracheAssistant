package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Geometry",
        contentPath = "XHTML/Geometry.xhtml",
        controller = Geometry.AssistantTaskController.class
)
public class Geometry extends Task {

    public Geometry() {
        //setPreconditions(Collections.singleton(ConditionManager.Condition()));
        //setPostconditions(Collections.singleton(ConditionManager.PartPresent_Cabin()));
    }

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        @Override
        protected void conditionUpdate() {
            // Add only matching
            for (GeometryPart part : sim.getGeometryPartManager().getParts()) {
                if (ConditionManager.satisfiesCondition_PartPresent_Cabin(part)) {
                    addToTaskLookup(part);
                    print("Added [Geometry]Part '" + part.getPresentationName() + "' to Lookup!");
                }
            }
        }

    }

}

//
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JOptionPane;

import star.assistant.Task;
import star.meshing.PartImportManager;
import star.assistant.annotation.StarAssistantTask;
import star.common.CompositePart;
import star.common.GeometryPart;
import star.meshing.SimpleShapePart;
import star.vis.Displayer;
import star.vis.PartDisplayer;
import star.vis.Scene;

class ADD {

        //SERIOUS BUG! gives horribly cryptic feedback if unsupported file type is selected
        //TODO: better error handling, if import action is safe to use, remove save() operation before import!
        public void importSurfaceMesh() {
            refresh();

            // ----------------------------------------------------------------------------------

            // Open
            File f = openFile("Open mesh file", "");
            if (f == null) {
                return;
            }

            // Import
            try {
                save();
                sim.get(PartImportManager.class).importCadPart(f.toString(), "AllEdges", 30.0, 2, false, 1.0E-5, false, false);
            } catch (Exception e) {
                printWarning("The following error occured while importing the selected part:");
                printMessage(e.toString());
                throw new RuntimeException(e);
                //return;
            }

            // ----------------------------------------------------------------------------------

            selectAndExpandNodeExclusive(sim.getGeometryPartManager());
            print("Surface Mesh imported!");

        }

        // ---------- Action ----------

        public void createGeometryScene() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            // Create a geometry scene
            Source.MacroHelper.Base helper = new Source.MacroHelper.Base(sim);
            String sceneName = "Geometry_all";
            Scene scene;
            if (!sim.getSceneManager().has(sceneName)) {
                scene = helper.createScene(sceneName);
            } else {
                switch (JOptionPane.showConfirmDialog(null, "Scene '" + sceneName + "' already exists! Should I keep the existing Displayers (if any) by hiding them?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null)) {
                    case JOptionPane.YES_OPTION:
                        scene = helper.createScene(sceneName);
                        break;
                    case JOptionPane.NO_OPTION:
                        scene = helper.createScene_Empty(sceneName);
                        break;
                    default:
                        print("Scene creation ABORT-ed by the user!");
                        return;
                }
            }
            float[] color_float = Color.GRAY.getColorComponents(new float[3]);
            double[] color = new double[color_float.length];
            int i = 0;
            for (float color_i : color_float) {
                color[i++] = color_i;
            }
            Displayer oldDisp = scene.getDisplayerManager().hasObject("Geometry");
            if (oldDisp != null) {
                oldDisp.setPresentationName("Geometry (old)");
            }
            PartDisplayer disp = helper.createDisplayer_Geometry(scene, "Geometry", 0.5, color, true, helper.putObjects(helper.putSurfaces()));
            helper.applyRepresentation(disp, "Geometry");
            scene.open(true);
            scene.resetCamera();

            // ----------------------------------------------------------------------------------

            selectAndExpandNodeExclusive(scene);
            print("Geometry Scene created! Please set the View yourself.");

        }

        // ---------- Action ----------

        //BUG! see block comment below (regarding updating part name in lookup)
        //TODO: improve renaming
        public void selectCabin() {
            refresh();

            // ----------------------------------------------------------------------------------

            // Pick part
            Collection<GeometryPart> parts = sim.getGeometryPartManager().getParts();
            ArrayList<Object> names_parts = new ArrayList(parts.size());
            int defaultIndex = 0;
            int counter = 0;
            boolean cabinPartExists = false;
            for (GeometryPart part_i : parts) {
                if ((part_i instanceof CompositePart) || (part_i instanceof SimpleShapePart)) {
                    if (part_i.getPresentationName().matches(ConditionManager.PARTCABIN_NAME)) {
                        part_i.setPresentationName(ConditionManager.PARTCABIN_NAME + "_other");
                    }
                    continue; // composite parts and simple shape parts parts are skipped
                }
                names_parts.add(part_i.getPresentationName());
                if (part_i.getPresentationName().matches(ConditionManager.PARTCABIN_NAME)) {
                    defaultIndex = counter;
                    cabinPartExists = true;
                }
                counter++;
            }
            if (names_parts.isEmpty()) {
                printWarning("No Part exists!");
                return;
            }
            Object name_selectedPart = JOptionPane.showInputDialog(null, "Please select the Part to be named '" + ConditionManager.PARTCABIN_NAME + "':", "selectCabin", JOptionPane.QUESTION_MESSAGE, null, names_parts.toArray(), names_parts.get(defaultIndex));
            if (name_selectedPart == null) {
                printWarning("Selection of Part was ABORT-ed by user!");
                return;
            }

            // Rename & re-organise Lookup
            GeometryPart cabinPart = sim.getGeometryPartManager().getPart((String) name_selectedPart);
            if (cabinPartExists) {
                if (((String) name_selectedPart).matches((String) names_parts.get(defaultIndex))) {
                    // existing part selected
                } else {
                    printError("BUG: This choice cannot be resolved currently. Please manually rename the old and new parts involved regarding Part '" + ConditionManager.PARTCABIN_NAME + "' before updating the task!"); //TODO: resolve this issue
                    /*
                    GeometryPart oldCabinPart = sim.getGeometryPartManager().getPart((String) names_parts.get(defaultIndex));
                    removeFromTaskLookup(oldCabinPart);
                    addToTaskLookup(cabinPart);
                    oldCabinPart.setPresentationName("_TEMP_" + oldCabinPart.getPresentationName());
                    cabinPart.setPresentationName(ConditionManager.PARTCABIN_NAME);
                    oldCabinPart.setPresentationName(ConditionManager.PARTCABIN_NAME + " (old)");
                    removeFromTaskLookup(oldCabinPart);
                    addToTaskLookup(cabinPart);
                    cabinPart.setPresentationName("_TEMP_TEMP_" + ConditionManager.PARTCABIN_NAME);
                    cabinPart.setPresentationName(ConditionManager.PARTCABIN_NAME);
                     */
                }
            } else {
                cabinPart.setPresentationName(ConditionManager.PARTCABIN_NAME);
                addToTaskLookup(cabinPart);
            }

            // ----------------------------------------------------------------------------------

            selectAndExpandNodeExclusive(cabinPart);
            print("Cabin Part selected!");

        }
}
