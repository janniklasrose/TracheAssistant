package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Mesh",
    contentPath="XHTML/Mesh.xhtml",
    controller=Mesh.AssistantTaskController.class
)
public class Mesh extends Task {
    public class AssistantTaskController extends FunctionTaskController {
    }
}
