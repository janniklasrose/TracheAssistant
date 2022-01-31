package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Run",
    contentPath="XHTML/Run.xhtml",
    controller=Run.AssistantTaskController.class
)
public class Run extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
