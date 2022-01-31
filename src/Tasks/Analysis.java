package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Analysis",
    contentPath="XHTML/Analysis.xhtml",
    controller=Analysis.AssistantTaskController.class
)
public class Analysis extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
