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

package de.chdev.artools.loga.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import de.chdev.artools.loga.model.ControlLogElement;
import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.ServerLogElement;

public class TreeComposite extends Composite{

	private TreeColumn nameColumn;
	private Tree tree;
	private TreeColumn lineColumn;
	private TreeColumn timeColumn;
	private TreeViewer treeViewer;


	public TreeComposite(Composite parent, int style) {
		
		super(parent, style);
		this.setLayout(new FillLayout());
		setTree(new Tree(this,SWT.None));
		getTree().setHeaderVisible(true);
		nameColumn = new TreeColumn(getTree(), SWT.NONE);
	    nameColumn.setText("Name");
	    nameColumn.setWidth(400);
		lineColumn = new TreeColumn(getTree(), SWT.NONE);
	    lineColumn.setText("Linenumber");
	    lineColumn.setWidth(50);
		timeColumn = new TreeColumn(getTree(), SWT.NONE);
	    timeColumn.setText("Duration");
	    timeColumn.setWidth(100);
		getTree().setVisible(true);
		
		setTreeViewer(new TreeViewer(getTree()));
		getTreeViewer().setContentProvider(new LogTreeContentProvider());
		getTreeViewer().setLabelProvider(new LogTreeLabelProvider());
	}

	public void fillTable(LinkedList<LogElement> logElementList){

//		Map<LogElement, TreeItem> logItemMap = new HashMap<LogElement, TreeItem>();
//		
//		for (LogElement log : logElementList){
//			
//			TreeItem item;
//			if (log.getParentLogElement()!=null && logItemMap.containsKey(log.getParentLogElement())){
//				item = new TreeItem(logItemMap.get(log.getParentLogElement()), SWT.NONE);
//			} else {
//				item = new TreeItem(tree, SWT.NONE);
//			}
//			
//			// Build tree row
//			Long startTimestamp;
//			Long endTimestamp;
//			String duration="";
//			if (log instanceof ServerLogElement){
//				startTimestamp = ((ServerLogElement) log).getStartTimestamp();
//				endTimestamp = ((ServerLogElement) log).getEndTimestamp();
//				if (startTimestamp!=null && endTimestamp != null){
//					duration=(endTimestamp - startTimestamp) + " ms";
//				}				
//			} else if (log instanceof ControlLogElement){
//				startTimestamp = ((ControlLogElement) log).getStartTimestamp();
//				endTimestamp = ((ControlLogElement) log).getEndTimestamp();
//				if (log.getElementAlias().equalsIgnoreCase("ACTL")){
//					if (startTimestamp!=null && endTimestamp != null){
//						duration=(endTimestamp - startTimestamp)/1000 + " s";
//					}
//				} else {
//					if (startTimestamp!=null && endTimestamp != null){
//						duration=(endTimestamp - startTimestamp) + " ms";
//					}
//				}
//			}
//			
//			String[] rowValues = new String[]{log.getName(),log.getStartLineNumber()+"",duration};
//			
//			
//			item.setText(rowValues);
//			logItemMap.put(log, item);
//		}
		
//		LogElement rootElement = new LogElement();
//		rootElement.setName("ROOT");
//		
//		for (LogElement logElement : logElementList){
//			if (logElement.getParentLogElement()==null){
//				logElement.setParentLogElement(rootElement);
//			}
//		}
		
		getTreeViewer().setInput(logElementList);
		getTreeViewer().refresh();
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	public Tree getTree() {
		return tree;
	}

	public void setTree(Tree tree) {
		this.tree = tree;
	}
}
