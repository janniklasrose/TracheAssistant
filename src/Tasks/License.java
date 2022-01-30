package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="License",
    contentPath="XHTML/License.xhtml",
    controller=License.AssistantTaskController.class
)
public class License extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
