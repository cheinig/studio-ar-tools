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
