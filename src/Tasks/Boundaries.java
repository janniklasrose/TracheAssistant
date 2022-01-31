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
