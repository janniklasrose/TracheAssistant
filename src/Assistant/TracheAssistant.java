package Assistant;

import Tasks.*; // wildcard avoids needing to change the code in 2 places
import java.util.ArrayList;
import java.util.List;
import star.assistant.SimulationAssistant;
import star.assistant.Task;
import star.assistant.annotation.StarAssistant;

@StarAssistant(
        display = "TracheAssistant",
        desc = "STAR-CCM+ Simulation Assistant for airway CFD simulations in human tracheas"
)
public final class TracheAssistant extends SimulationAssistant {

    public TracheAssistant() {

        // Initialise variable to hold all task instances
        List<Task> tasks = new ArrayList<>();

        // Welcome (first one is opened on launch)
        tasks.add(new Overview());

        // Core tasks
        tasks.add(new Geometry());
        tasks.add(new Physics());
        tasks.add(new Boundaries());
        tasks.add(new Mesh());
        tasks.add(new Analysis());
        tasks.add(new Run());
        tasks.add(new Processing());

        // Documentation
        tasks.add(new Documentation());
        tasks.add(new License());

        // Generate the assistant sidebar
        setOutline(tasks);

    }

}
