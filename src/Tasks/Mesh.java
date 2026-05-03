package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Mesh",
        contentPath = "XHTML/Mesh.xhtml",
        controller = Mesh.AssistantTaskController.class
)
public class Mesh extends Task {

    public Mesh() {
        //setPreconditions();
        //setPostconditions();
    }

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}

        // Pre: ConditionManager.RegionPresent_CabinWithBoundaries
        // Post: ConditionManager.MeshPresent_Cabin
//
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import javax.swing.JOptionPane;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.base.neo.NeoObjectVector;
import star.common.FvRepresentation;


class ADD {

        public void updateConditions() {
            for (FvRepresentation representation : sim.getRepresentationManager().getObjectsOf(FvRepresentation.class)) {
                if (ConditionManager.satisfiesCondition_MeshPresent_Cabin(representation)) {
                    addToTaskLookup(representation);
                    printMessage("Added Representation '" + representation.getPresentationName() + "' to Lookup!");
                }
            }
        }

        //TODO: add mesh quality checks, incl. adding thresholds and creating scenes
        public void diagnoseMesh() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            FvRepresentation volumeMesh = (FvRepresentation) sim.getRepresentationManager().hasObject("Volume Mesh");
            if (volumeMesh == null) {
                printWarning("You need to generate a volume mesh first before you can diagnose it!");
                return;
            }

            volumeMesh.generateMeshReport(new NeoObjectVector(new Object[]{sim.getRegionManager().getRegion(ConditionManager.REGIONCABIN_NAME)}));

            // ----------------------------------------------------------------------------------
            printMessage("Mesh analysed!");

        }

        public void runSurface() {

            sim = getSimulation();

            // ----------------------------------------------------------------------------------

            switch (JOptionPane.showConfirmDialog(null, "To avoid crashing the surface mesh generation, do not interact with the simulation!", "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null)) {
                case JOptionPane.YES_OPTION:
                    break;
                default:
                    switch (JOptionPane.showConfirmDialog(null, "Do you want to execute the whole mesh pipeline in batch?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null)) {
                        case JOptionPane.YES_OPTION:
                            runVolumeRemotely();
                            return;
                        default:
                            printWarning("Generating surface mesh ABORT-ed by user!");
                            return;
                    }
            }

            save();
            sim.getMeshPipelineController().generateSurfaceMesh();
            save();

            // ----------------------------------------------------------------------------------

            printMessage("Surface mesh created successfully!");

        }

        // ---------- Action ----------

        public void runVolumeLocally() {

            sim = getSimulation();

            // ----------------------------------------------------------------------------------

            switch (JOptionPane.showConfirmDialog(null, "To avoid crashing the volume mesh generation, do not interact with the simulation!", "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null)) {
                case JOptionPane.YES_OPTION:
                    break;
                default:
                    switch (JOptionPane.showConfirmDialog(null, "Do you want to execute the whole mesh in batch?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null)) {
                        case JOptionPane.YES_OPTION:
                            runVolumeRemotely();
                            return;
                        default:
                            printWarning("Generating volume mesh ABORT-ed by user!");
                            return;
                    }
            }

            save();
            sim.getMeshPipelineController().generateVolumeMesh();
            save();

            // ----------------------------------------------------------------------------------

            printMessage("Volume mesh created successfully!");
}
}
//
