/*
   Copyright 2011 Christoph Heinig

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 * 
 */
package de.chdev.artools.loga.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;


/**
 * @author Christoph Heinig
 *
 */
public class LogaDocumentProvider extends AbstractDocumentProvider{
    
    private boolean modifyAllowed = false;
    
    // This marker is only needed to check if the document is saved correctly
    private boolean saved = false;

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createAnnotationModel(java.lang.Object)
     */
    @Override
    protected IAnnotationModel createAnnotationModel(Object element)
	    throws CoreException {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#createDocument(java.lang.Object)
     */
    @Override
    protected IDocument createDocument(Object element) throws CoreException {
	// TODO Auto-generated method stub
	Document document = new Document();
	
	document.set(((LogaEditorInput)element).getFileText());
//	if (document != null) {
//		IDocumentPartitioner partitioner =
//			new FastPartitioner(
//				new SQLPartitionScanner(),
//				new String[] {
//					SQLPartitionScanner.SQL_COMMENT,
//					SQLPartitionScanner.SQL_STRING});
//		partitioner.connect(document);
//		document.setDocumentPartitioner(partitioner);
//	}
	return document;
    }

//    /* (non-Javadoc)
//     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#doSaveDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
//     */
//    @Override
//    protected void doSaveDocument(IProgressMonitor monitor, Object element,
//	    IDocument document, boolean overwrite) throws CoreException {
//	setSaved(false); // mark the document as not saved as a workaround
//	File file = ((LogaEditorInput)element).getFile();
//	if (file == null){
//	    FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.SAVE);
//	    String filename = fileDialog.open();
//	    if (filename!=null){
//		file = new File(filename);
//	    }
//	}
//	
//	if (file != null){
//	    try {
//		FileWriter fileWriter = new FileWriter(file);
//		fileWriter.write(document.get());
//		fileWriter.flush();
//		fileWriter.close();
////		((LogaEditorInput)element).setFile(file);
//		setSaved(true); // mark the document as saved as a workaround
//	    } catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }
//	}
//    }
    

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#getOperationRunner(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#isModifiable(java.lang.Object)
     */
    @Override
    public boolean isModifiable(Object element) {
        return modifyAllowed;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDocumentProvider#isReadOnly(java.lang.Object)
     */
    @Override
    public boolean isReadOnly(Object element) {
        return !modifyAllowed;
    }
    
    public void setModifyAllowed(boolean modifyAllowed){
	this.modifyAllowed=modifyAllowed;
    }

    /**
     * This method is only for internal use. It can be used to check if the save process finished correct.
     * @param saved the saved to set
     */
    private void setSaved(boolean saved) {
	this.saved = saved;
    }

    /**
     * This method can be used to check if the last save action has completed successfully.
     * It will return true if the document was saved and false otherwise.
     * @return the saved
     */
    public boolean isSaved() {
	return saved;
    }

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		// TODO Auto-generated method stub
		
	}
    
    
}
