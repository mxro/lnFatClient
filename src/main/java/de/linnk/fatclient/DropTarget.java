/**
 * 
 */
package de.linnk.fatclient;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JComponent;

public class DropTarget implements DropTargetListener {
    private final LinnkDocumentActions actions;

    public DropTarget( LinnkDocumentActions actions, JComponent comp){
        this.actions = actions;
        new java.awt.dnd.DropTarget(comp, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {


        if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          dtde.acceptDrop(dtde.getDropAction());
          if (dtde.getTransferable() != null) {
            try {
             this.actions.importPlainText(
                dtde.getTransferable().getTransferData(DataFlavor.stringFlavor).toString());
            } catch (UnsupportedFlavorException ex) {

            } catch (IOException ex) {

           }
         }



        }
    }
}