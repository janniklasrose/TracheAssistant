package Assistant;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileExtensionFilter extends FileFilter {

    private final String extension; // final value is set in the constructor

    public FileExtensionFilter() {
        this.extension = "";
    }

    public FileExtensionFilter(String ext) {
        String prefix = (ext.startsWith(".") || ext.matches("")) ? "" : ".";
        this.extension = prefix + ext;
    }

    @Override
    public boolean accept(File file) {
        return extension.matches("")
                || file.isDirectory() // allow dirs to be able to navigate
                || (file.isFile() // if it's a file, check for extension:
                && file.getName().toLowerCase().endsWith(this.extension));
    }

    @Override
    public String getDescription() {
        String prefix = this.extension.matches("") ? "" : "*";
        return prefix + this.extension;
    }

}
