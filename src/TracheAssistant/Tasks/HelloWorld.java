package TracheAssistant.Tasks;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(
    display="Hello, World!",
    contentFragment="<ul><li><a href=\"staraction:sayhello\">Say Hello</a></li></ul>",
    controller=HelloWorld.HelloWorldController.class
)
public class HelloWorld extends Task {
    public class HelloWorldController extends FunctionTaskController {
        public void sayhello() {
            notifyUser("Hello, World!");
        }
    }
}
