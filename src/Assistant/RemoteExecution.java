package Assistant;

public class RemoteExecution {
    
        //TODO: improve bsub.starccmp to include a -mesh, -run, -meshrun option that executes batch mode accordingly
        //TODO: watch for name of option --temp  in bsub
        public void runVolumeRemotely() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------
            String simName = sim.getPresentationName();
            if (simName.contains(" ")) {
                printWarning("Simulation name contains SPACES! Please rename. Using UNDERSCORE for bsub command.");
                simName = simName.replaceAll(" ", "_");
            }
            String java = " --java_file=Mesher.java";
            switch (JOptionPane.showConfirmDialog(null, "Do you need me to create the Mesher.java file?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null)) {
                case JOptionPane.YES_OPTION:
                    try {
                        File file = saveFile("Where to put the macro?", ".java", "Mesher");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                            bw.write("public class Mesher extends star.common.StarMacro {" + System.lineSeparator());
                            bw.write("  @Override public void execute(){" + System.lineSeparator());
                            bw.write("    getActiveSimulation().getMeshPipelineController().generateVolumeMesh();" + System.lineSeparator());
                            bw.write("    /*" + System.lineSeparator());
                            bw.write("    // room for more custom code" + System.lineSeparator());
                            bw.write("    */" + System.lineSeparator());
                            bw.write("  }" + System.lineSeparator());
                            bw.write("}" + System.lineSeparator());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
            String version;
            Object[] versions = {"10.06.009", "10.06.009-r8", "11.04.010", "11.04.010-r8"};
            Object selected_version = JOptionPane.showInputDialog(null, "Please select the version", "Version", JOptionPane.QUESTION_MESSAGE, null, versions, versions[0]);
            if (selected_version == null) {
                version = " --version=10.06.009";
            } else {
                version = " --version=" + selected_version;
            }
            String input = " --input=" + simName;
            String queue = " --queue=mc_all_ser";
            String cpu = " --cpus=" + 1;
            String mem = " --mem=" + promptUserForInput("Memory for serial meshing [MB]", 48000);
            String temp = " --temp=" + promptUserForInput("Storage space [MB]", 50000);
            String jobname = " --jobname=" + promptUserForInput("Job name", "Mesher_" + simName);
            String project = " --PROJECT_NAME=A320NEO_EYV";
            String fairshare = " --fairshare_group=ug_sy_env";
            printMessage("Use the following command to submit the batch job (first fill in the placeholders):");
            printMessage("\nbsub.starccmp" + version + jobname + input + queue + cpu + mem + temp + project + fairshare + java + "\n");

            // ----------------------------------------------------------------------------------
            printMessage("Volume mesh job ready for batch submission!");

        }

        //TODO: improve bsub.starccmp to include a -mesh, -run, -meshrun option that executes batch mode accordingly
        //TODO: watch for name of option --temp  in bsub
        //TODO: add check for table/plot/scene names, anything that could be written out (remove all characters that could lead to error, such as '&' or ':')
        public void createBsub() {

            sim = getActiveSimulation();

            // ----------------------------------------------------------------------------------

            String simName = sim.getPresentationName();
            if (simName.contains(" ")){
                printWarning("Simulation name contains SPACES! Please rename. Using UNDERSCORE for bsub command.");
                simName = simName.replaceAll(" ", "_");
            }
            String java = "";
            switch (JOptionPane.showConfirmDialog(null, "Do you need me to create the Run.java file?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null)) {
                case JOptionPane.YES_OPTION:
                    try {
                        File file = saveFile("Where to put the macro?", ".java", "Run");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                            bw.write("public class Run extends star.common.StarMacro {" + System.lineSeparator());
                            bw.write("  @Override public void execute(){" + System.lineSeparator());
                            bw.write("    getActiveSimulation().getSimulationIterator().run();" + System.lineSeparator());
                            bw.write("    /*" + System.lineSeparator());
                            bw.write("    // room for more custom code" + System.lineSeparator());
                            bw.write("    */" + System.lineSeparator());
                            bw.write("  }" + System.lineSeparator());
                            bw.write("}" + System.lineSeparator());
                        }
                    } catch (IOException e) {
                        printError("Brace for IOException error!");
                        throw new RuntimeException(e);
                    }
                    java = " --java_file=Run.java";
                    printMessage("\n"
                            + "public class Run extends star.common.StarMacro {"
                            + "  @Override public void execute(){"
                            + "    getActiveSimulation().getSimulationIterator().run();"
                            + "    /*"
                            + "    // room for more custom code"
                            + "    */"
                            + "  }"
                            + "\n");
                    break;
            }
            String version;
            Object[] versions = {"10.06.009", "10.06.009-r8", "11.04.010", "11.04.010-r8"};
            Object selected_version = JOptionPane.showInputDialog(null, "Please select the version", "Version", JOptionPane.QUESTION_MESSAGE, null, versions, versions[0]);
            if (selected_version == null) {
                version = " --version=10.06.009";
            } else {
                version = " --version=" + selected_version;
            }
            String input = " --input=" + simName;
            String queue = " --queue=mc_all_par";
            long cellCount = ((FvRepresentation) sim.getRepresentationManager().getObject("Volume Mesh")).getCellCount();
            printMessage("Cell count = " + cellCount);
            int cpus = promptUserForInput("How many CPUs do you want to use? (Recommended: enter multiple of 24)", 24*((int)Math.max(1.0, Math.ceil(Math.ceil(cellCount/10000.0)/24.0))));
            String cpu = " --cpus=" + cpus;
            int mem_per_cpu = promptUserForInput("Memory (per core) for parallel run [MB]", 1000*(int)Math.max(1.0, Math.ceil((3000.0/1000000.0)*cellCount/cpus/1000.0)));
            String mem = " --mem=" + mem_per_cpu;
            String temp = " --temp=" + promptUserForInput("Storage space [MB]", 50000);
            String jobname = " --jobname=" + promptUserForInput("Job name", "Run_"+simName);
            String project = " --PROJECT_NAME=A320NEO_EYV";
            String fairshare = " --fairshare_group=ug_sy_env";
            printMessage("Use the following command to submit the batch job (first fill in the placeholders):");
            printMessage("\nbsub.starccmp"+version+jobname+input+queue+cpu+mem+temp+project+fairshare+java+"\n");

            // ----------------------------------------------------------------------------------

            printMessage("Bsub command created!");

        }

}
