package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Processing",
    contentPath="XHTML/Processing.xhtml",
    controller=Processing.AssistantTaskController.class
)
public class Processing extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
