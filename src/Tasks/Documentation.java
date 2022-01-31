package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Documentation",
        contentPath = "XHTML/Documentation.xhtml",
        controller = Documentation.AssistantTaskController.class
)
public class Documentation extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}
