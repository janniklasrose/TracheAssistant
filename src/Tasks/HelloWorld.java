package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Hello, World!",
    contentPath="XHTML/HelloWorld.xhtml",
    controller=HelloWorld.AssistantTaskController.class
)
public class HelloWorld extends Task {
    public class AssistantTaskController extends FunctionTaskController {
        public void sayhello() {
            notifyUser("Hello, World!");
        }
    }
}
