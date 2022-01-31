package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
    display="Run",
    contentPath="XHTML/Run.xhtml",
    controller=Run.AssistantTaskController.class
)
public class Run extends Task {
    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }
}
