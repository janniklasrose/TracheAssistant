package Tasks;

import Assistant.ExtendedFunctionTaskController;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;

@StarAssistantTask(
        display = "Analysis",
        contentPath = "XHTML/Analysis.xhtml",
        controller = Analysis.AssistantTaskController.class
)
public class Analysis extends Task {

    public class AssistantTaskController extends ExtendedFunctionTaskController {
    }

}

//
import java.util.Collections;
import star.common.Region;
class ADD {
    public Analysis() {
        //setPreconditions(Collections.singleton(ConditionManager.MeshPresent_Cabin()));
        //setPostconditions(Collections.singleton(ConditionManager.Condition()));
    }
        //TODO: change hardcoded values to interactive (long-term: import post-processing settings from file)
        //TODO: extend Source.MacroHelper.Post to apply full standard
        //TODO: better integrate stopping criterion for physical time with monitor trigger
        public void setAll() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            Region cabinRegion = sim.getRegionManager().getRegion(ConditionManager.REGIONCABIN_NAME);
            Source.MacroHelper.Post helperPost = new Source.MacroHelper.Post(sim, cabinRegion);

            helperPost.makeCustomTools(true, true, true, true, true);

            helperPost.makeFieldFunctions(true, true, true, true);

            helperPost.makeScenes_Iso();
            helperPost.makeScenes_SectionPlaneX(new double[]{17.75, 18.56, 19.38, 20.19}, new String[]{"row17", "row18", "row19", "row20"});
            helperPost.makeScenes_SectionPlaneY(new double[]{-1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5}, new String[]{"paxA", "paxB", "paxC", "aisle", "paxD", "paxE", "paxF"});
            helperPost.makeScenes_SectionPlaneZ(new double[]{-0.1, 0.7, 1.7}, new String[]{"paxFeet", "paxHeads", "crewHeads"});
            helperPost.makeScenes_Streamlines();
            helperPost.makeScenes_VolumeRender();
            helperPost.makeScenes_SurfaceTemps();
            helperPost.makePlots_Histograms(new double[]{17.8, 18.6, 19.4, 20.2}, 0.55, new double[]{-1.5, -1.0, -0.5, 0.5, 1.0, 1.5}, 0.15, new double[]{0.2}, 1.2);

            // ----------------------------------------------------------------------------------

            printMessage("Post-processing set successfully!");
}
}
//
