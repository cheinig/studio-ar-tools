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
		tree = new Tree(this,SWT.None);
		tree.setHeaderVisible(true);
		nameColumn = new TreeColumn(tree, SWT.NONE);
	    nameColumn.setText("Name");
	    nameColumn.setWidth(400);
		lineColumn = new TreeColumn(tree, SWT.NONE);
	    lineColumn.setText("Linenumber");
	    lineColumn.setWidth(50);
		timeColumn = new TreeColumn(tree, SWT.NONE);
	    timeColumn.setText("Duration");
	    timeColumn.setWidth(100);
		tree.setVisible(true);
		
		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(new LogTreeContentProvider());
		treeViewer.setLabelProvider(new LogTreeLabelProvider());
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
		
		treeViewer.setInput(logElementList);
		treeViewer.refresh();
	}
}
