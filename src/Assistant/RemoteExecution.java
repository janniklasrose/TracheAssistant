package Assistant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import star.common.FvRepresentation;

public final class RemoteExecution {

    private static final String[] STAR_VERSIONS = {
        "10.06.009", "10.06.009-r8", "11.04.010", "11.04.010-r8", "16.04.012"
    };

    private RemoteExecution() {
    }

    public static void runVolumeRemotely(ExtendedFunctionTaskController controller) {
        String simName = sanitizedSimulationName(controller);
        String javaOption = "";
        int createMacro = JOptionPane.showConfirmDialog(null,
                "Create Mesher.java macro file?", "Remote meshing",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (createMacro == JOptionPane.YES_OPTION && writeMacro(controller, "Mesher",
                "getActiveSimulation().getMeshPipelineController().generateVolumeMesh();")) {
            javaOption = " --java_file=Mesher.java";
        }

        String command = "bsub.starccmp"
                + versionOption(controller)
                + " --jobname=" + controller.promptString("Job name", "Mesher_" + simName)
                + " --input=" + simName
                + " --queue=mc_all_ser"
                + " --cpus=1"
                + " --mem=" + controller.promptInt("Memory for serial meshing [MB]", 48000)
                + " --temp=" + controller.promptInt("Storage space [MB]", 50000)
                + " --PROJECT_NAME=A320NEO_EYV"
                + " --fairshare_group=ug_sy_env"
                + javaOption;
        controller.printMessage("Use the following command to submit the batch job:");
        controller.printMessage(command);
    }

    public static void createBsub(ExtendedFunctionTaskController controller) {
        String simName = sanitizedSimulationName(controller);
        String javaOption = "";
        int createMacro = JOptionPane.showConfirmDialog(null,
                "Create Run.java macro file?", "Remote run",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (createMacro == JOptionPane.YES_OPTION && writeMacro(controller, "Run",
                "getActiveSimulation().getSimulationIterator().run();")) {
            javaOption = " --java_file=Run.java";
        }

        long cellCount = 0;
        FvRepresentation volumeMesh = WorkflowState.volumeMesh(controller.sim);
        if (volumeMesh != null) {
            cellCount = volumeMesh.getCellCount();
        }
        int defaultCpus = 24 * ((int) Math.max(1.0, Math.ceil(Math.ceil(cellCount / 10000.0) / 24.0)));
        int cpus = controller.promptInt("How many CPUs do you want to use?", defaultCpus);
        int defaultMemory = 1000 * (int) Math.max(1.0,
                Math.ceil((3000.0 / 1000000.0) * Math.max(cellCount, 1) / Math.max(cpus, 1) / 1000.0));

        String command = "bsub.starccmp"
                + versionOption(controller)
                + " --jobname=" + controller.promptString("Job name", "Run_" + simName)
                + " --input=" + simName
                + " --queue=mc_all_par"
                + " --cpus=" + cpus
                + " --mem=" + controller.promptInt("Memory per core [MB]", defaultMemory)
                + " --temp=" + controller.promptInt("Storage space [MB]", 50000)
                + " --PROJECT_NAME=A320NEO_EYV"
                + " --fairshare_group=ug_sy_env"
                + javaOption;
        controller.printMessage("Cell count = " + cellCount);
        controller.printMessage("Use the following command to submit the batch job:");
        controller.printMessage(command);
    }

    private static String versionOption(ExtendedFunctionTaskController controller) {
        return " --version=" + controller.promptOption("Version", "Please select the STAR-CCM+ version",
                STAR_VERSIONS, STAR_VERSIONS[0]);
    }

    private static String sanitizedSimulationName(ExtendedFunctionTaskController controller) {
        String simName = controller.sim.getPresentationName();
        if (simName.contains(" ")) {
            controller.printWarning("Simulation name contains spaces; using underscores in the batch command.");
            simName = simName.replaceAll(" ", "_");
        }
        return simName;
    }

    private static boolean writeMacro(ExtendedFunctionTaskController controller, String className, String bodyLine) {
        File file = InputOutput.saveFile(controller.sim, "Where to put the macro?", ".java", className);
        if (file == null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("public class " + className + " extends star.common.StarMacro {");
            writer.newLine();
            writer.write("  @Override public void execute() {");
            writer.newLine();
            writer.write("    " + bodyLine);
            writer.newLine();
            writer.write("  }");
            writer.newLine();
            writer.write("}");
            writer.newLine();
            return true;
        } catch (IOException ex) {
            controller.printWarning("Could not write macro file: " + ex.getMessage());
            return false;
        }
    }
}
