package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.Workflow;
import java.util.Collections;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Hello, World!",
        contentPath = "XHTML/HelloWorld.xhtml",
        controller = HelloWorld.AssistantTaskController.class
)
public class HelloWorld extends Task {

    public HelloWorld() {
        setPreconditionDescription("Pre");
        setPreconditions(Collections.singleton(Workflow.Condition()));
        setPostconditionDescription("Post");
        setPostconditions(Collections.singleton(Workflow.Condition()));
    }

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        public void sayhello() {
            this.refresh();
            notifyUser("Hello, World!");
        }

    }

}
