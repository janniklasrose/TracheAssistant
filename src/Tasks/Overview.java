package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.InputOutput;
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
            InputOutput.saveSimulation(sim);
            printMessage("Simulation saved!");
        }

        public void createSummary() {
            refresh();
            InputOutput.saveSummaryReport(sim);
            printMessage("Summary saved!");
        }

        public void updateAllConditions() {
            refresh();

            for (GeometryPart part : sim.getGeometryPartManager().getParts()) {
                if (ConditionManager.satisfiesCondition_PartPresent_Cabin(part)) {
                    addToTaskLookup(part);
                    print("Added GeometryPart '" + part.getPresentationName() + "' to Lookup!");
                }
            }
            for (Region region : sim.getRegionManager().getRegions()) {
                if (ConditionManager.satisfiesCondition_RegionPresent_Cabin(region)) {
                    addToTaskLookup(region);
                    print("Added Region '" + region.getPresentationName() + "' to Lookup!");
                }
            }
            for (PhysicsContinuum physics : sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class)) {
                if (ConditionManager.satisfiesCondition_PhysicsPresent_Air(physics)) {
                    addToTaskLookup(physics);
                    print("Added PhysicsContinuum '" + physics.getPresentationName() + "' to Lookup!");
                }
            }
            for (Region region : sim.getRegionManager().getRegions()) {
                if (ConditionManager.satisfiesCondition_RegionPresent_CabinWithBoundaries(region)) {
                    addToTaskLookup(region);
                    print("Added Region '" + region.getPresentationName() + "' to Lookup!");
                }
            }
            for (FvRepresentation representation : sim.getRepresentationManager().getObjectsOf(FvRepresentation.class)) {
                if (ConditionManager.satisfiesCondition_MeshPresent_Cabin(representation)) {
                    addToTaskLookup(representation);
                    print("Added Representation '" + representation.getPresentationName() + "' to Lookup!");
                }
            }
            if (ConditionManager.satisfiesCondition_SolutionPresent_Initial(sim.getSolution())) {
                addToTaskLookup(sim.getSolution());
                print("Added Solution to Lookup!");
            }
        }

        public void updateAll() {
            updateAllConditions();
        }

    }

}
