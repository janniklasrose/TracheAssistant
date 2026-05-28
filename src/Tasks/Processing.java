package Tasks;

import Assistant.ExtendedFunctionTaskController;
import Assistant.InputOutput;
import Assistant.Monitors;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JOptionPane;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.common.StarPlot;
import star.vis.Scene;

@StarAssistantTask(
        display = "Processing",
        contentPath = "XHTML/Processing.xhtml",
        controller = Processing.AssistantTaskController.class
)
public class Processing extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {

        public void exportScenes() {
            refresh();
            File folder = InputOutput.saveFolder(sim, "Folder for scenes", "scenes");
            if (folder == null) {
                return;
            }

            JList<String> sceneList = new JList<>(sceneNames());
            JOptionPane.showMessageDialog(null, sceneList, "Scenes to export", JOptionPane.QUESTION_MESSAGE);
            if (sceneList.getSelectedValuesList().isEmpty()) {
                printWarning("No scenes were selected.");
                return;
            }

            for (String name : sceneList.getSelectedValuesList()) {
                Scene scene = sim.getSceneManager().getObject(name);
                exportScene(scene, new File(folder, safeFileName(name) + ".png"));
            }
            printMessage("Scene export finished.");
        }

        public void exportPlots() {
            refresh();
            File folder = InputOutput.saveFolder(sim, "Folder for plots", "plots");
            if (folder == null) {
                return;
            }

            JList<String> plotList = new JList<>(plotNames());
            JOptionPane.showMessageDialog(null, plotList, "Plots to export", JOptionPane.QUESTION_MESSAGE);
            if (plotList.getSelectedValuesList().isEmpty()) {
                printWarning("No plots were selected.");
                return;
            }

            for (String name : plotList.getSelectedValuesList()) {
                StarPlot plot = sim.getPlotManager().getObject(name);
                plot.open();
                plot.encode(new File(folder, safeFileName(name) + ".png").toString(), "png");
                plot.close();
            }
            printMessage("Plot export finished.");
        }

        public void setAll() {
            refresh();
            Monitors.setConvergence(this);
            printMessage("Post-processing scene/report generation is WIP without the external MacroHelper library.");
        }

        private String[] sceneNames() {
            ArrayList<String> names = new ArrayList<>();
            for (Scene scene : sim.getSceneManager().getScenes()) {
                names.add(scene.getPresentationName());
            }
            return names.toArray(new String[0]);
        }

        private String[] plotNames() {
            ArrayList<String> names = new ArrayList<>();
            for (StarPlot plot : sim.getPlotManager().getPlots()) {
                names.add(plot.getPresentationName());
            }
            return names.toArray(new String[0]);
        }

        private void exportScene(Scene scene, File file) {
            scene.open(true);
            boolean exported = false;
            try {
                Method printAndWait = scene.getClass().getMethod("printAndWait", String.class, int.class, int.class);
                printAndWait.invoke(scene, file.toString(), 1, 1);
                exported = true;
            } catch (ReflectiveOperationException ex) {
                printWarning("Could not hardcopy scene '" + scene.getPresentationName()
                        + "'. Opened it instead for manual export.");
            }
            if (exported) {
                scene.close(true);
            }
        }

        private String safeFileName(String name) {
            return name.replaceAll("[^A-Za-z0-9._-]", "_");
        }
    }
}
