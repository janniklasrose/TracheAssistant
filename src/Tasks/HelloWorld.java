package Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Hello, World!",
    contentPath="XHTML/HelloWorld.xhtml",
    controller=HelloWorld.HelloWorldController.class
)
public class HelloWorld extends Task {
    public class HelloWorldController extends FunctionTaskController {
        public void sayhello() {
            notifyUser("Hello, World!");
        }
    }
}
