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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.chdev.artools.loga.model.ControlLogElement;
import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.ServerLogElement;

public class LogTreeLabelProvider extends LabelProvider implements ITableLabelProvider{

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		// Calculate duration
		Long startTimestamp;
		Long endTimestamp;
		String duration="";
		if (element instanceof ServerLogElement){
			startTimestamp = ((ServerLogElement) element).getStartTimestamp();
			endTimestamp = ((ServerLogElement) element).getEndTimestamp();
			if (startTimestamp!=null && endTimestamp != null){
				duration=(endTimestamp - startTimestamp) + " ms";
			}				
		} else if (element instanceof ControlLogElement){
			startTimestamp = ((ControlLogElement) element).getStartTimestamp();
			endTimestamp = ((ControlLogElement) element).getEndTimestamp();
			if (((ControlLogElement)element).getElementAlias().equalsIgnoreCase("ACTL")){
				if (startTimestamp!=null && endTimestamp != null){
					duration=(endTimestamp - startTimestamp)/1000 + " s";
				}
			} else {
				if (startTimestamp!=null && endTimestamp != null){
					duration=(endTimestamp - startTimestamp) + " ms";
				}
			}
		}
		
		switch (columnIndex){
		case 0: return ((LogElement)element).getName();
		case 1: return ((LogElement)element).getStartLineNumber()+"";
		case 2: return duration;
		}
		return ((LogElement)element).getName();
	}

}
