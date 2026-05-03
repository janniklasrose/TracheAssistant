package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Boundaries",
        contentPath = "XHTML/Boundaries.xhtml",
        controller = Boundaries.AssistantTaskController.class
)
public class Boundaries extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}

        // Pre A: ConditionManager.PhysicsPresent_Air
        // Post B: RegionPresent_CabinWithBoundaries
        // Pre B: ConditionManager.PartPresent_Cabin
        // Post B: RegionPresent_Cabin

//
import java.util.Collections;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.base.neo.NeoObjectVector;
import star.common.Boundary;
import star.common.GeometryPart;
import star.common.Region;

class ADD {
        public void forceConditionUpdate() {
            // A
            for (Region region : sim.getRegionManager().getRegions()) {
                if (ConditionManager.satisfiesCondition_RegionPresent_Cabin(region)) {
                    addToTaskLookup(region);
                    printMessage("Added Region '" + region.getPresentationName() + "' to Lookup!");
                }
            }
            // B
            for (Region region : sim.getRegionManager().getRegions()) {
                if (ConditionManager.satisfiesCondition_RegionPresent_CabinWithBoundaries(region)) {
                    addToTaskLookup(region);
                    printMessage("Added Region '" + region.getPresentationName() + "' to Lookup!");
                }
            }
        }

        //TODO: handle the case where there is already a mesh
        public void setCabinRegion() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            // Set region
            GeometryPart cabinPart = sim.getGeometryPartManager().getPart(ConditionManager.PARTCABIN_NAME); // must exist
            Region cabinRegion = sim.getRegionManager().hasObject(ConditionManager.REGIONCABIN_NAME);
            if (cabinRegion == null) {
                cabinRegion = sim.getRegionManager().createEmptyRegion();
            } else {
                for (GeometryPart assignedPart_i : cabinRegion.getPartGroup().getObjects()) {
                    if (assignedPart_i != cabinPart) {
                        printMessage("Part '" + assignedPart_i.getPresentationName() + "' removed from Region '" + ConditionManager.REGIONCABIN_NAME + "'!");
                        removeFromTaskLookup(assignedPart_i);
                    }
                }
                cabinRegion.getPartGroup().setObjects(cabinPart);
                Boundary boundary_default = cabinRegion.getBoundaryManager().getDefaultBoundary();
                for (Boundary boundary_i : cabinRegion.getBoundaryManager().getBoundaries()) {
                    if (boundary_i != boundary_default) {
                        printMessage("Boundary '" + boundary_i.getPresentationName() + "' removed from Region '" + ConditionManager.REGIONCABIN_NAME + "'!");
                        removeFromTaskLookup(boundary_i);
                        cabinRegion.getBoundaryManager().removeBoundaries(new NeoObjectVector(new Object[]{boundary_i}));
                    }
                }
            }
            cabinRegion.setPresentationName(ConditionManager.REGIONCABIN_NAME);
            cabinRegion.getPartGroup().add(cabinPart);
            cabinRegion.getBoundaryManager().getDefaultBoundary().setPresentationName("Default");
            addToTaskLookup(cabinRegion);

            // ----------------------------------------------------------------------------------

            selectAndExpandNodeExclusive(cabinRegion);
            printMessage("Cabin Region successfully assigned!");

        }
}
}
//
