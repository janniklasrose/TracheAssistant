package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Processing",
        contentPath = "XHTML/Processing.xhtml",
        controller = Processing.AssistantTaskController.class
)
public class Processing extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}

        // Pre: ConditionManager.SolutionPresent_Initial
        // Post: ConditionManager.Condition

//
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JList;
import javax.swing.JOptionPane;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.common.StarPlot;
import star.vis.Scene;

class ADD {

        //TODO: handle cases where existing files could get overwritten
        //TODO: handle cases where the presentationName has invalid characters
        public void exportScenes() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            // User selects folder
            File folder = saveFolder("Folder for scenes", "scenes");
            if (folder == null) {
                return;
            }

            // User chooses scenes to export
            ArrayList<String> names = new ArrayList(0);
            for (Scene scene_i : sim.getSceneManager().getScenes()) {
                names.add(scene_i.getPresentationName());
            }
            JList<String> taskList = new JList(names.toArray());
            JOptionPane.showMessageDialog(null, taskList, "Scenes to export", JOptionPane.QUESTION_MESSAGE);

            // Export scenes
            for (String name_selected : taskList.getSelectedValuesList()) {
                Scene selected = sim.getSceneManager().getObject(name_selected);
                selected.open(true);
                //selected.printAndWait(resolvePath(folder.toString() + File.separator + selected.toString() + ".png"));
                selected.close(true);
            }

            // ----------------------------------------------------------------------------------

            printMessage("Scenes exported!");

        }
        //TODO: handle cases where existing files could get overwritten
        //TODO: handle cases where the presentationName has invalid characters
        public void exportPlots() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            // User selects folder
            File folder = saveFolder("Folder for plots", "plots");
            if (folder == null) {
                return;
            }

            // User chooses plots to export
            ArrayList<String> names = new ArrayList(0);
            for (StarPlot plot_i : sim.getPlotManager().getPlots()) {
                names.add(plot_i.getPresentationName());
            }
            JList<String> taskList = new JList(names.toArray());

            // Export plots
            JOptionPane.showMessageDialog(null, taskList, "Plots to export", JOptionPane.QUESTION_MESSAGE);
            for (String name_selected : taskList.getSelectedValuesList()) {
                StarPlot selected = sim.getPlotManager().getObject(name_selected);
                selected.open();
                selected.encode(resolvePath(folder.toString() + File.separator + selected.toString() + ".png"), "png");
                selected.close();
            }

            // ----------------------------------------------------------------------------------

            printMessage("Plots exported!");

        }
}
}
