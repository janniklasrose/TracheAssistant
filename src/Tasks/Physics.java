package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.PhysicsModels;
import Assistant.WorkflowState;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.common.PhysicsContinuum;

@StarAssistantTask(
        display = "Physics",
        contentPath = "XHTML/Physics.xhtml",
        controller = Physics.AssistantTaskController.class
)
public class Physics extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        @Override
        protected void conditionUpdate() {
            PhysicsContinuum physics = WorkflowState.airPhysics(sim);
            if (physics != null) {
                addToTaskLookup(physics);
                printMessage("Added PhysicsContinuum '" + physics.getPresentationName() + "' to lookup.");
            }
        }

        public void setPhysicsContinuum() {
            refresh();
            PhysicsContinuum physics = PhysicsModels.setPhysicsContinuum(this);
            if (physics != null) {
                addToTaskLookup(physics);
            }
        }

        public void setRANSke() {
            refresh();
            PhysicsContinuum physics = PhysicsModels.selectPhysics(this);
            if (physics != null && PhysicsModels.setRANSke(this, physics)) {
                addToTaskLookup(physics);
            }
        }

        public void setRANSkw() {
            refresh();
            PhysicsContinuum physics = PhysicsModels.selectPhysics(this);
            if (physics != null && PhysicsModels.setRANSkw(this, physics)) {
                addToTaskLookup(physics);
            }
        }

        public void setIsothermal() {
            refresh();
            PhysicsContinuum physics = PhysicsModels.selectPhysics(this);
            if (physics != null && PhysicsModels.setIsothermal(this, physics)) {
                addToTaskLookup(physics);
            }
        }

        public void setNonisothermal() {
            refresh();
            PhysicsContinuum physics = PhysicsModels.selectPhysics(this);
            if (physics != null && PhysicsModels.setNonisothermal(this, physics)) {
                addToTaskLookup(physics);
            }
        }

        public void setSteady() {
            refresh();
            PhysicsContinuum physics = PhysicsModels.selectPhysics(this);
            if (physics != null && PhysicsModels.setSteady(this, physics)) {
                addToTaskLookup(physics);
            }
        }

        public void setUnsteady() {
            refresh();
            PhysicsContinuum physics = PhysicsModels.selectPhysics(this);
            if (physics != null && PhysicsModels.setUnsteady(this, physics)) {
                addToTaskLookup(physics);
            }
        }
    }
}
