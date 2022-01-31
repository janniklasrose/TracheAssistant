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

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}
