package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Physics",
    contentPath="XHTML/Physics.xhtml",
    controller=Physics.AssistantTaskController.class
)
public class Physics extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
