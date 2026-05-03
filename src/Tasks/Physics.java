package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Physics",
        contentPath = "XHTML/Physics.xhtml",
        controller = Physics.AssistantTaskController.class
)
public class Physics extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}

//
class ADD {
        //setPreconditions(Collections.singleton(ConditionManager.RegionPresent_Cabin()));
        //setPostconditions(Collections.singleton(ConditionManager.PhysicsPresent_Air()));
        public void update() {
            for (PhysicsContinuum physics : sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class)) {
                if (ConditionManager.satisfiesCondition_PhysicsPresent_Air(physics)) {
                    addToTaskLookup(physics);
                    printMessage("Added Physics[Continuum] '" + physics.getPresentationName() + "' to Lookup!");
                }
            }
        }
    }
}

import star.common.Model;
import star.common.ModelManager;
import star.common.PhysicsContinuum;

    // Fail-safe method for switching on or off a physics model
    public <T extends Model> void switchModel_Physics(PhysicsContinuum physics, Class<T> type, boolean on) {
        ModelManager modelManager = physics.getModelManager();
        Model model = modelManager.hasObjectOfClass(type.getCanonicalName());
        if ((on) && (model == null)) {
            physics.enable(type);
        } else if ((!on) && (model != null)) {
            physics.disableModel(model);
}
}
