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

package de.chdev.artools.loga.editor;


import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.ide.IDE;

import de.chdev.artools.loga.controller.ActlController;
import de.chdev.artools.loga.controller.ApiController;
import de.chdev.artools.loga.controller.EsclController;
import de.chdev.artools.loga.controller.FltrController;
import de.chdev.artools.loga.controller.ILogController;
import de.chdev.artools.loga.controller.MainController;
import de.chdev.artools.loga.controller.OperationController;
import de.chdev.artools.loga.controller.SqlController;
import de.chdev.artools.loga.controller.WflgController;
import de.chdev.artools.loga.lang.KeywordLoader;
import de.chdev.artools.loga.worker.ParseWorker;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class LogaEditor extends MultiPageEditorPart implements IResourceChangeListener{

	/** The text editor used in page 0. */
	private TextEditor textEditor;

	/** The text editor used in page 0. */
	private LogaTreeEditor treeEditor;

	/** The font chosen in page 1. */
	private Font font;

	/** The text widget used in page 2. */
	private StyledText text;
	
	/** The flag to show if the log tree is already created */
	private boolean treeCreated = false;


	/**
	 * Creates a multi-page editor example.
	 */
	public LogaEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage0() {
		try {
			textEditor = new LogaTextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, textEditor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
	}
	
	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
	void createPage1() {
		
		try {
			treeEditor = new LogaTreeEditor();
			int index = addPage(treeEditor, getEditorInput());
			setPageText(index, treeEditor.getTitle());
//			if (treeCreated==false) {
				Job buildTreeJob = new BuildTreeJob("Refresh Dependency Tree");
				buildTreeJob.schedule();
//				treeCreated=true;
//			}
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}

//		Composite composite = new Composite(getContainer(), SWT.NONE);
//		GridLayout layout = new GridLayout();
//		composite.setLayout(layout);
//		layout.numColumns = 2;
//
//		Button fontButton = new Button(composite, SWT.NONE);
//		GridData gd = new GridData(GridData.BEGINNING);
//		gd.horizontalSpan = 2;
//		fontButton.setLayoutData(gd);
//		fontButton.setText("Change Font...");
//		
//		fontButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent event) {
////				setFont();
//			}
//		});
//
//		int index = addPage(composite);
//		setPageText(index, "Properties");
	}
	/**
	 * Creates page 2 of the multi-page editor,
	 * which shows the sorted text.
	 */
	void createPage2() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		int index = addPage(composite);
		setPageText(index, "Preview");
	}
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException {
		
		
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
//		if (newPageIndex == 1 && treeCreated==false) {
//			Job buildTreeJob = new BuildTreeJob("Refresh Dependency Tree");
//			buildTreeJob.schedule();
//			treeCreated=true;
//		}
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
//		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
//			Display.getDefault().asyncExec(new Runnable(){
//				public void run(){
//					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
//					for (int i = 0; i<pages.length; i++){
//						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
//							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
//							pages[i].closeEditor(editorPart,true);
//						}
//					}
//				}            
//			});
//		}
	}

	class BuildTreeJob extends Job{

		public BuildTreeJob(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			// Autodetect keyword language
			KeywordLoader.initialize(new StringReader(((LogaEditorInput)getEditorInput()).getFileText()));
			
			 MainController mainController = new MainController();
			((LogaEditorInput)getEditorInput()).setMainController(mainController);
			
			OperationController operationController = new OperationController(
					mainController);
			ApiController apiController = new ApiController(
					KeywordLoader.getConfiguration(ApiController.PREFIX),
					mainController);
			ActlController actlController = new ActlController(
					KeywordLoader.getConfiguration(ActlController.PREFIX),
					mainController, operationController);
			FltrController fltrController = new FltrController(
					KeywordLoader.getConfiguration(FltrController.PREFIX),
					mainController, operationController);
			SqlController sqlController = new SqlController(
					KeywordLoader.getConfiguration(SqlController.PREFIX),
					mainController);
			EsclController esclController = new EsclController(
					KeywordLoader.getConfiguration(EsclController.PREFIX),
					mainController, operationController);
			WflgController wflgController = new WflgController();
			
			HashMap<String, ILogController> controllerMap = new HashMap<String, ILogController>();
			mainController.setControllerMap(controllerMap);
			
			controllerMap.put(ApiController.PREFIX, apiController);
			controllerMap.put(ActlController.PREFIX, actlController);
			controllerMap.put(FltrController.PREFIX, fltrController);
			controllerMap.put(SqlController.PREFIX, sqlController);
			controllerMap.put(WflgController.PREFIX, wflgController);
			controllerMap.put(EsclController.PREFIX, esclController);

			Reader reader = new StringReader(((LogaEditorInput)getEditorInput()).getFileText());
			ParseWorker parser = new ParseWorker(reader,mainController);
			parser.run();
			
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){

				@Override
				public void run() {
					((LogaTreeEditor)getEditor(1)).refreshData(((LogaEditorInput)getEditorInput()).getMainController().getLogElementList());					
				}
				
			});
			
			return Status.OK_STATUS;
		}
		
	}

}
