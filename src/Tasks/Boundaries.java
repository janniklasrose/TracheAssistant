package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Boundaries",
    contentPath="XHTML/Boundaries.xhtml",
    controller=Boundaries.AssistantTaskController.class
)
public class Boundaries extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
