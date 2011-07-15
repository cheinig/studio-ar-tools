package de.chdev.artools.loga.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.chdev.artools.loga.model.LogElement;

public class LogTreeContentProvider implements ITreeContentProvider{

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<LogElement> rootList = new ArrayList<LogElement>();
		
		LinkedList<LogElement> logElementList = (LinkedList<LogElement>)inputElement;
		for (LogElement logElement : logElementList){
			if (logElement.getParentLogElement()==null){
				rootList.add(logElement);
			}
		}
		return rootList.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		ArrayList<LogElement> childLogElementList = ((LogElement)parentElement).getChildLogElementList();
		return childLogElementList.toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((LogElement)element).getParentLogElement();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (((LogElement)element).getChildLogElementList().isEmpty()){
			return false;
		} else {
			return true;
		}
	}

}
