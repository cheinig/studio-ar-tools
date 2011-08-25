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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandAdapter;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import de.chdev.artools.loga.controller.ActlController;
import de.chdev.artools.loga.controller.ApiController;
import de.chdev.artools.loga.controller.EsclController;
import de.chdev.artools.loga.controller.FltrController;
import de.chdev.artools.loga.controller.ILogController;
import de.chdev.artools.loga.controller.MainController;
import de.chdev.artools.loga.controller.OperationController;
import de.chdev.artools.loga.controller.SqlController;
import de.chdev.artools.loga.controller.WflgController;
import de.chdev.artools.loga.gui.TreeComposite;
import de.chdev.artools.loga.handler.TreeContextOpenAction;
import de.chdev.artools.loga.handler.TreeContextToTextAction;
import de.chdev.artools.loga.lang.KeywordLoader;
import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.worker.ParseWorker;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class LogaTreeEditor extends EditorPart{
	
	public LogaTreeEditor() {
	}

	private Composite filterArea;
	private Composite treeArea;
	private Composite mainFrame;
	private ExpandBar configBar;
	private TreeComposite treeFrame;
	private Combo clientLanguageCombo;
	private Combo serverLanguageCombo;

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setInput(input);
		setSite(site);
		setPartName("Tree Editor");
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		
		mainFrame = new Composite(parent,SWT.None);
		GridLayout mainFrameLayout = new GridLayout(1,false);
		mainFrameLayout.marginHeight=0;
		mainFrameLayout.marginWidth=0;
		mainFrame.setLayout(mainFrameLayout);
		 
		
		configBar = new ExpandBar(mainFrame, SWT.V_SCROLL);
		configBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		configBar.setSpacing(2);
		
		filterArea = createFilterArea(configBar);
		treeArea = createTreeArea(mainFrame);
		
		ExpandItem configBarItem = new ExpandItem (configBar, SWT.NONE, 0);
		configBarItem.setText("Config");
		configBarItem.setHeight(filterArea.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		configBarItem.setControl(filterArea);
		configBar.addExpandListener(new ExpandAdapter(){
			
			@Override
			public void itemExpanded(ExpandEvent e) {
				// Set size of expand bar to expanded item
				GridData filterBarLayoutData = (GridData)((ExpandItem)e.item).getParent().getLayoutData();
				filterBarLayoutData.heightHint=((ExpandItem)e.item).getHeight()+((ExpandItem)e.item).getHeaderHeight()+4;
				mainFrame.layout(true);
				super.itemExpanded(e);
			}		
			
			@Override
			public void itemCollapsed(ExpandEvent e) {
				// Reduce size of expand bar to expanded item header
				GridData filterBarLayoutData = (GridData)((ExpandItem)e.item).getParent().getLayoutData();
				filterBarLayoutData.heightHint=((ExpandItem)e.item).getHeaderHeight()+4;
				mainFrame.layout(true);
				super.itemCollapsed(e);
			}
		});
	}
	
	private Composite createFilterArea(Composite parent){
		
		Composite configFrame = new Composite(parent, SWT.NONE);
		configFrame.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Group grpLanguageSettings = new Group(configFrame, SWT.NONE);
		grpLanguageSettings.setText("Language Settings");
		grpLanguageSettings.setLayout(new GridLayout(2, false));
		
		Label lblClientLanguage = new Label(grpLanguageSettings, SWT.NONE);
		lblClientLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClientLanguage.setText("Client Language");
		
		clientLanguageCombo = new Combo(grpLanguageSettings, SWT.READ_ONLY);
		clientLanguageCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean reload = MessageDialog.openQuestion(null, "Rebuild tree", "Should the current log tree rebuild with the new language setting?");
				KeywordLoader.setClientLanguage(((Combo)e.getSource()).getText());
				if (reload==true){
					Job treeJob = new BuildTreeJob("Rebuild tree with new language");
					treeJob.schedule();
				}
			}
		});
		clientLanguageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		clientLanguageCombo.setItems(KeywordLoader.getSupportedLanguages().toArray(new String[0]));
		clientLanguageCombo.add("AUTO",0);
		clientLanguageCombo.select(0);
		
		Label lblServerLanguage = new Label(grpLanguageSettings, SWT.NONE);
		lblServerLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerLanguage.setText("Server Language");
		
		serverLanguageCombo = new Combo(grpLanguageSettings, SWT.READ_ONLY);
		serverLanguageCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean reload = MessageDialog.openQuestion(null, "Rebuild tree", "Should the current log tree rebuild with the new language setting?");
				KeywordLoader.setServerLanguage(((Combo)e.getSource()).getText());
				if (reload==true){
					Job treeJob = new BuildTreeJob("Rebuild tree with new language");
					treeJob.schedule();
				}
			}
		});
		serverLanguageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		serverLanguageCombo.setItems(KeywordLoader.getSupportedLanguages().toArray(new String[0]));
		serverLanguageCombo.add("AUTO",0);
		serverLanguageCombo.select(0);
		
		Group grpFilterSettings = new Group(configFrame, SWT.NONE);
		grpFilterSettings.setText("Filter Settings");
		grpFilterSettings.setLayout(new GridLayout(2, false));
				
						Label labelHideDisabled = new Label(grpFilterSettings, SWT.None);
						labelHideDisabled.setText("Hide disabled items");
						Button btnHideDisabled = new Button(grpFilterSettings, SWT.CHECK);
						btnHideDisabled.setText("yes");
		
		return configFrame;

	}
	
	private Composite createTreeArea(Composite parent){
		treeFrame = new TreeComposite(parent, SWT.None);
		treeFrame.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		LinkedList<LogElement> initList = new LinkedList<LogElement>();
		LogElement initElement = new LogElement();
		initElement.setName("...loading...");
		initList.add(initElement);
		treeFrame.fillTable(initList);

		// Create context menu for tree table
		MenuManager popupMenuManager = new MenuManager();
		IMenuListener listener = new IMenuListener() { 
		public void menuAboutToShow(IMenuManager manager) { 
		    TreeSelection selection = (TreeSelection)treeFrame.getTreeViewer().getSelection();
		    if (selection != null && selection.getFirstElement() != null){
		    	LogElement logElement = (LogElement)selection.getFirstElement();
		    	manager.add(new TreeContextOpenAction(logElement));
		    	manager.add(new TreeContextToTextAction(logElement, (LogaEditor)getSite().getPage().getActiveEditor()));
		    }
		    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); 
		    } 
		}; 
		popupMenuManager.addMenuListener(listener); 
		popupMenuManager.setRemoveAllWhenShown(true); 
		Menu menu = popupMenuManager.createContextMenu(treeFrame.getTree());
		treeFrame.getTree().setMenu(menu);
		getSite().registerContextMenu("de.chdev.artools.loga.editor.treepopup", popupMenuManager, treeFrame.getTreeViewer());
		getSite().setSelectionProvider(treeFrame.getTreeViewer());
		
		
		return treeFrame;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	protected void refreshData(LinkedList<LogElement> logElementList){
		treeFrame.fillTable(logElementList);
		
		// also refresh the available language information
		List<String> supportedLanguages = KeywordLoader.getSupportedLanguages();
		supportedLanguages.add("AUTO");
		for (String lang : supportedLanguages){
			if (clientLanguageCombo.indexOf(lang) < 0){
				clientLanguageCombo.add(lang);
				serverLanguageCombo.add(lang);
			}
		}
		for (String item : clientLanguageCombo.getItems()){
			if (!supportedLanguages.contains(item)){
				clientLanguageCombo.remove(item);
				serverLanguageCombo.remove(item);
			}
		}
	}
	
	class BuildTreeJob extends Job{

		public BuildTreeJob(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
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
					refreshData(((LogaEditorInput)getEditorInput()).getMainController().getLogElementList());					
				}
				
			});
			
			return Status.OK_STATUS;
		}
	}
}

