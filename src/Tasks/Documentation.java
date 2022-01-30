package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Documentation",
    contentPath="XHTML/Documentation.xhtml",
    controller=Documentation.AssistantTaskController.class
)
public class Documentation extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
