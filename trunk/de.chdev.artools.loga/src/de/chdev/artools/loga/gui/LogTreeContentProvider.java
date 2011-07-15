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
