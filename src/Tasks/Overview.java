package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Overview",
    contentPath="XHTML/Overview.xhtml",
    controller=Overview.AssistantTaskController.class
)
public class Overview extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
