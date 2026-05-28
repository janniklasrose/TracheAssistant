package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.Monitors;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Analysis",
        contentPath = "XHTML/Analysis.xhtml",
        controller = Analysis.AssistantTaskController.class
)
public class Analysis extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        public void setConvergence() {
            refresh();
            Monitors.setConvergence(this);
        }

        public void setResources() {
            refresh();
            Monitors.setResources(this);
        }
    }
}
