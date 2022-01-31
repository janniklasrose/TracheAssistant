package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Hello, World!",
        contentPath = "XHTML/HelloWorld.xhtml",
        controller = HelloWorld.AssistantTaskController.class
)
public class HelloWorld extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        public void sayhello() {
            this.refresh();
            notifyUser("Hello, World!");
        }

    }

}
