package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "License",
        contentPath = "XHTML/License.xhtml",
        controller = License.AssistantTaskController.class
)
public class License extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}
