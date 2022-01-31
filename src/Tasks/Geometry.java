package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Geometry",
    contentPath="XHTML/Geometry.xhtml",
    controller=Geometry.AssistantTaskController.class
)
public class Geometry extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
