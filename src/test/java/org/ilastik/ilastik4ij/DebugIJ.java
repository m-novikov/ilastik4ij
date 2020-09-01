package org.ilastik.ilastik4ij;

import net.imagej.ImageJ;
import org.ilastik.ilastik4ij.ui.IlastikImportCommand;
import org.ilastik.ilastik4ij.ui.IlastikPixelClassificationCommand;

public class DebugIJ {

    public static void main(String[] args) {
        ImageJ ij = new ImageJ();
        ij.launch(args);
        ij.command().run(IlastikPixelClassificationCommand.class, true);
    }

}
