package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
    display="Physics",
    contentPath="XHTML/Physics.xhtml",
    controller=Physics.AssistantTaskController.class
)
public class Physics extends Task {
    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }
}
