package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Analysis",
        contentPath = "XHTML/Analysis.xhtml",
        controller = Analysis.AssistantTaskController.class
)
public class Analysis extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}
