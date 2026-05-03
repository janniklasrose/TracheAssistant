package Assistant;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import star.common.Simulation;

// Input/Output, like saving and loading etc

public class InputOutput {

    public boolean saveSimulation(Simulation sim) {
        // Save
        if (!saveAs()) {
            return false;
        }
        return true;
    }

    public boolean saveSummaryReport(Simulation sim) {
        // Save
        File f = saveFile("Save summary file", ".html", sim.getPresentationName());
        if (f == null) {
            return false;
        }
        new SimulationSummaryReporter().report(sim, resolvePath(f.toString()));
        return true
    }

}








    public boolean saveAs() {
        File f = saveFile("Save As...", ".sim", sim.getSessionPath());
        if (f == null) {
            return false;
        }
        sim.saveState(resolvePath(f.toString()));
        return true;
    }

    public void save() {
        String path = sim.getSessionPath();
        if (path.matches("")) {
            if (!saveAs()) {
                return;
            }
        }
        sim.saveState(resolvePath(path));
    }

    public File saveFolder(String dialogTitle) {
        return saveFolder(dialogTitle, sim.getSessionDir());
    }

    //TODO: setSelectedFile() does not accept folder?
    public File saveFolder(String dialogTitle, String preset) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(dialogTitle);
        if (!preset.matches("")) {
            File pre1 = new File(preset); // absolute path given
            File pre2 = new File(sim.getSessionDir() + File.separator + preset); // relative path given
            if (pre1.getParentFile() != null && pre1.getParentFile().isDirectory()) {
                fileChooser.setCurrentDirectory(pre1.getParentFile());
                fileChooser.setSelectedFile(pre1);
            } else if (pre2.getParentFile() != null && pre2.getParentFile().isDirectory()) {
                fileChooser.setCurrentDirectory(pre2.getParentFile());
                fileChooser.setSelectedFile(pre2);
            } else {
                fileChooser.setCurrentDirectory(new File(sim.getSessionDir()));
            }
        } else {
            fileChooser.setCurrentDirectory(new File(sim.getSessionDir()));
        }
        File selectedFolder;
        switch (fileChooser.showSaveDialog(null)) {
            case JFileChooser.APPROVE_OPTION:
                selectedFolder = fileChooser.getSelectedFile();
                if (!selectedFolder.exists()) {
                    selectedFolder.mkdirs();
                }
                break;
            default:
                printWarning("Saving the folder was ABORT-ed by the user!");
                selectedFolder = null;
                break;
        }
        return selectedFolder;
    }

    public File saveFile(String dialogTitle, String extension) {
        return saveFile(dialogTitle, extension, "");
    }

    public File saveFile(String dialogTitle, String extension, String preset) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(new FileFilter_withExtension(extension));
        fileChooser.setDialogTitle(dialogTitle);
        if (!preset.matches("")) {
            if (!preset.endsWith(extension)) {
                preset = preset + extension;
            }
            File pre1 = new File(preset); // absolute path given
            File pre2 = new File(sim.getSessionDir() + File.separator + preset); // relative path given
            if (pre1.getParent() != null && pre1.getParentFile().isDirectory()) {
                fileChooser.setSelectedFile(pre1);
            } else if (pre2.getParent() != null && pre2.getParentFile().isDirectory()) {
                fileChooser.setSelectedFile(pre2);
            } else {
                fileChooser.setCurrentDirectory(new File(sim.getSessionDir()));
            }
        } else {
            fileChooser.setCurrentDirectory(new File(sim.getSessionDir()));
        }
        boolean tryagain = true;
        File selectedFile = null;
        while (tryagain) {
            switch (fileChooser.showSaveDialog(null)) {
                case JFileChooser.APPROVE_OPTION:
                    selectedFile = fileChooser.getSelectedFile();
                    String savePath = selectedFile.toString();
                    if (!savePath.endsWith(extension)) {
                        selectedFile = new File(savePath + extension);
                    }
                    if (selectedFile.isDirectory()) {
                        printWarning("You are trying to overwrite a directory! Pick again.");
                        selectedFile = null;
                        tryagain = true;
                        break;
                    }
                    if (selectedFile.exists()) {
                        printWarning("File '" + savePath + "' already exists!");
                        switch (JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite the file?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null)) {
                            case JOptionPane.YES_OPTION:
                                tryagain = false;
                                break;
                            default:
                                selectedFile = null;
                                tryagain = true;
                                break;
                        }
                    } else {
                        tryagain = false;
                    }
                    break;
                default:
                    printWarning("Saving the " + extension + " file was ABORT-ed by the user!");
                    selectedFile = null;
                    tryagain = false;
                    break;
            }
        }
        return selectedFile;
    }

    public File openFile(String dialogTitle, String extension) {
        return openFile(dialogTitle, extension, "");
    }

    public File openFile(String dialogTitle, String extension, String preset) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileFilter_withExtension(extension));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle(dialogTitle);
        if (!preset.matches("")) {
            if (!preset.endsWith(extension)) {
                preset = preset + extension;
            }
            File pre1 = new File(preset); // absolute path given
            File pre2 = new File(sim.getSessionDir() + File.separator + preset); // relative path given
            if (pre1.getParent() != null && pre1.getParentFile().isDirectory()) {
                fileChooser.setSelectedFile(pre1);
            } else if (pre2.getParent() != null && pre2.getParentFile().isDirectory()) {
                fileChooser.setSelectedFile(pre2);
            } else {
                fileChooser.setCurrentDirectory(new File(sim.getSessionDir()));
            }
        } else {
            fileChooser.setCurrentDirectory(new File(sim.getSessionDir()));
        }
        boolean tryagain = true;
        File selectedFile = null;
        while (tryagain) {
            switch (fileChooser.showOpenDialog(null)) {
                case JFileChooser.APPROVE_OPTION:
                    selectedFile = fileChooser.getSelectedFile();
                    String savePath = selectedFile.toString();
                    if (!savePath.endsWith(extension)) {
                        selectedFile = new File(savePath + extension);
                    }
                    if (!selectedFile.exists()) {
                        printWarning("File '" + savePath + "' does not exist!");
                        selectedFile = null;
                        tryagain = true;
                    } else {
                        tryagain = false;
                    }
                    break;
                default:
                    printWarning("Opening the " + extension + " file was ABORT-ed by the user!");
                    selectedFile = null;
                    tryagain = false;
                    break;
            }
        }
        return selectedFile;
    }
