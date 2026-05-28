package Assistant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import star.common.Simulation;

public final class InputOutput {

    private InputOutput() {
    }

    public static boolean saveSimulation(Simulation sim) {
        String path = sim.getSessionPath();
        if (path == null || path.matches("")) {
            return saveAs(sim);
        }
        sim.saveState(resolvePath(sim, path));
        return true;
    }

    public static boolean saveAs(Simulation sim) {
        File file = saveFile(sim, "Save As...", ".sim", sim.getPresentationName());
        if (file == null) {
            return false;
        }
        sim.saveState(resolvePath(sim, file.toString()));
        return true;
    }

    public static boolean saveSummaryReport(Simulation sim) {
        File file = saveFile(sim, "Save summary file", ".html", sim.getPresentationName());
        if (file == null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("<!DOCTYPE html>");
            writer.newLine();
            writer.write("<html><head><meta charset=\"utf-8\"><title>");
            writer.write(escapeHtml(sim.getPresentationName()));
            writer.write("</title></head><body>");
            writer.newLine();
            writer.write("<h1>");
            writer.write(escapeHtml(sim.getPresentationName()));
            writer.write("</h1>");
            writer.newLine();
            writer.write("<p>Session path: ");
            writer.write(escapeHtml(sim.getSessionPath()));
            writer.write("</p>");
            writer.newLine();
            writer.write("</body></html>");
            writer.newLine();
            return true;
        } catch (IOException ex) {
            sim.println("[ERROR] Could not write summary report: " + ex.getMessage());
            return false;
        }
    }

    public static File saveFolder(Simulation sim, String dialogTitle, String preset) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(dialogTitle);
        setPreset(sim, fileChooser, preset);

        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            sim.println("[WARNING] Folder selection was aborted.");
            return null;
        }

        File folder = fileChooser.getSelectedFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static File saveFile(Simulation sim, String dialogTitle, String extension, String preset) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileExtensionFilter(extension));
        fileChooser.setDialogTitle(dialogTitle);
        setPreset(sim, fileChooser, preset);

        while (true) {
            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                sim.println("[WARNING] Saving " + extension + " file was aborted.");
                return null;
            }

            File selected = withExtension(fileChooser.getSelectedFile(), extension);
            if (selected.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(null,
                        "Overwrite '" + selected.getName() + "'?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                if (overwrite != JOptionPane.YES_OPTION) {
                    continue;
                }
            }
            return selected;
        }
    }

    public static File openFile(Simulation sim, String dialogTitle, String extension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileExtensionFilter(extension));
        fileChooser.setDialogTitle(dialogTitle);
        setPreset(sim, fileChooser, "");

        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            sim.println("[WARNING] Opening " + extension + " file was aborted.");
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    public static String resolvePath(Simulation sim, String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return file.toString();
        }
        return new File(sim.getSessionDir(), path).toString();
    }

    private static void setPreset(Simulation sim, JFileChooser fileChooser, String preset) {
        fileChooser.setCurrentDirectory(new File(sim.getSessionDir()));
        if (preset == null || preset.matches("")) {
            return;
        }

        File absolute = new File(preset);
        File relative = new File(sim.getSessionDir(), preset);
        if (absolute.getParentFile() != null && absolute.getParentFile().isDirectory()) {
            fileChooser.setCurrentDirectory(absolute.getParentFile());
            fileChooser.setSelectedFile(absolute);
        } else if (relative.getParentFile() != null && relative.getParentFile().isDirectory()) {
            fileChooser.setCurrentDirectory(relative.getParentFile());
            fileChooser.setSelectedFile(relative);
        } else {
            fileChooser.setSelectedFile(relative);
        }
    }

    private static File withExtension(File file, String extension) {
        if (extension == null || extension.matches("") || file.toString().endsWith(extension)) {
            return file;
        }
        return new File(file.toString() + extension);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
