package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Process",
    contentPath="XHTML/Process.xhtml",
    controller=Process.AssistantTaskController.class
)
public class Process extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
