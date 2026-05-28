package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.WorkflowState;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.base.neo.NeoObjectVector;
import star.common.Boundary;
import star.common.GeometryPart;
import star.common.Region;

@StarAssistantTask(
        display = "Boundaries",
        contentPath = "XHTML/Boundaries.xhtml",
        controller = Boundaries.AssistantTaskController.class
)
public class Boundaries extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        @Override
        protected void conditionUpdate() {
            Region region = WorkflowState.airwaysRegion(sim);
            if (region != null) {
                addToTaskLookup(region);
                printMessage("Added Region '" + region.getPresentationName() + "' to lookup.");
            }
        }

        public void setCabinRegion() {
            refresh();
            GeometryPart tracheaPart = WorkflowState.tracheaPart(sim);
            if (tracheaPart == null) {
                printWarning("Select a '" + WorkflowState.TRACHEA_PART + "' part before creating the region.");
                return;
            }

            Region airwaysRegion = WorkflowState.airwaysRegion(sim);
            if (airwaysRegion == null) {
                airwaysRegion = sim.getRegionManager().createEmptyRegion();
            } else {
                Boundary defaultBoundary = airwaysRegion.getBoundaryManager().getDefaultBoundary();
                for (Boundary boundary : airwaysRegion.getBoundaryManager().getBoundaries()) {
                    if (boundary != defaultBoundary) {
                        airwaysRegion.getBoundaryManager().removeBoundaries(new NeoObjectVector(new Object[]{boundary}));
                    }
                }
            }

            airwaysRegion.setPresentationName(WorkflowState.AIRWAYS_REGION);
            airwaysRegion.getPartGroup().setObjects(tracheaPart);
            airwaysRegion.getBoundaryManager().getDefaultBoundary().setPresentationName("Default");
            addToTaskLookup(airwaysRegion);
            selectAndExpandNodeExclusive(airwaysRegion);
            printMessage("Airways region assigned.");
        }
    }
}
