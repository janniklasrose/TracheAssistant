package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
    display="Processing",
    contentPath="XHTML/Processing.xhtml",
    controller=Processing.AssistantTaskController.class
)
public class Processing extends Task {
    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }
}
