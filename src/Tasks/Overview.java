package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
    display="Overview",
    contentPath="XHTML/Overview.xhtml",
    controller=Overview.AssistantTaskController.class
)
public class Overview extends Task {
    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }
}
