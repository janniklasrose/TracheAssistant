package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.InputOutput;
import Assistant.WorkflowState;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.common.FvRepresentation;
import star.common.GeometryPart;
import star.common.PhysicsContinuum;
import star.common.Region;

@StarAssistantTask(
        display = "Overview",
        contentPath = "XHTML/Overview.xhtml",
        controller = Overview.AssistantTaskController.class
)
public class Overview extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        public void saveSimulation() {
            refresh();
            if (InputOutput.saveSimulation(sim)) {
                printMessage("Simulation saved.");
            }
        }

        public void createSummary() {
            refresh();
            if (InputOutput.saveSummaryReport(sim)) {
                printMessage("Summary report saved.");
            }
        }

        @Override
        protected void conditionUpdate() {
            updateAllConditions();
        }

        public void updateAllConditions() {
            refresh();

            for (GeometryPart part : sim.getGeometryPartManager().getParts()) {
                if (WorkflowState.isTracheaPart(part)) {
                    addToTaskLookup(part);
                    print("Added GeometryPart '" + part.getPresentationName() + "' to lookup.");
                }
            }
            for (Region region : sim.getRegionManager().getRegions()) {
                if (WorkflowState.isAirwaysRegion(region)) {
                    addToTaskLookup(region);
                    print("Added Region '" + region.getPresentationName() + "' to lookup.");
                }
            }
            for (PhysicsContinuum physics : sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class)) {
                if (WorkflowState.isAirPhysics(physics)) {
                    addToTaskLookup(physics);
                    print("Added PhysicsContinuum '" + physics.getPresentationName() + "' to lookup.");
                }
            }
            for (FvRepresentation representation : sim.getRepresentationManager().getObjectsOf(FvRepresentation.class)) {
                if (WorkflowState.isVolumeMesh(representation)) {
                    addToTaskLookup(representation);
                    print("Added Representation '" + representation.getPresentationName() + "' to lookup.");
                }
            }
            if (WorkflowState.isInitialSolution(sim.getSolution())) {
                addToTaskLookup(sim.getSolution());
                print("Added Solution to lookup.");
            }
        }

        public void updateAll() {
            updateAllConditions();
        }
    }
}
