/*
 * FileDragDropHandler.java
 *
 * Created on September 18, 2010, 3:19:23 PM
 */
package com.hccs.forms.components;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class FileDragDropHandler extends DropTargetAdapter {

    FileDragDropCallback callBack;

    public FileDragDropHandler() {
    }

    public void addCallback(FileDragDropCallback callback) {
        this.callBack = callback;
    }

    /**
     * Set the target component where the listener will run.
     *
     * @param targetComp
     */
    public void setTargetComponent(Component targetComp) {
        DropTarget target = new DropTarget(targetComp, this);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        Transferable trans = dtde.getTransferable();
        List<File> files;
        try {
            files = (List) trans.getTransferData(DataFlavor.javaFileListFlavor);
            if (!callBack.validate(files.toArray(new File[files.size()]))) {
                dtde.rejectDrag();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        boolean gotData = false;

        Transferable trans = dtde.getTransferable();
        try {
            if (trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> files = (List) trans.getTransferData(DataFlavor.javaFileListFlavor);
                callBack.processFiles(files.toArray(new File[files.size()]));
            }
            gotData = true;
        } catch (Exception e) {
        } finally {
            dtde.dropComplete(gotData);
        }
    }
}
