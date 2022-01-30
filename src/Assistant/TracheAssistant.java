package Assistant;

import Tasks.Overview;
import Tasks.Documentation;
import Tasks.License;

import java.util.ArrayList;
import java.util.List;
import star.assistant.SimulationAssistant;
import star.assistant.Task;
import star.assistant.annotation.StarAssistant;

@StarAssistant(
    display="TracheAssistant",
    desc="STAR-CCM+ Simulation Assistant for airway CFD simulations in human tracheas"
)
public final class TracheAssistant extends SimulationAssistant {
    public TracheAssistant () {
        List<Task> tasks = new ArrayList<>();
        // Welcome
        tasks.add(new Overview());
        // Core tasks
        // Documentation
        tasks.add(new Documentation());
        tasks.add(new License());
        setOutline(tasks);
    }
}
