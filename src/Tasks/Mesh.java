package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
    display="Mesh",
    contentPath="XHTML/Mesh.xhtml",
    controller=Mesh.AssistantTaskController.class
)
public class Mesh extends Task {
    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }
}
