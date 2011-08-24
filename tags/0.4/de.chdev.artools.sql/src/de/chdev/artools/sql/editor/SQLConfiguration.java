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
package de.chdev.artools.sql.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


/**
 * @author Christoph Heinig
 *
 */
public class SQLConfiguration extends SourceViewerConfiguration{
//	private XMLDoubleClickStrategy doubleClickStrategy;
	private ColorManager colorManager;
	private SQLScanner scanner;
	private SQLStringScanner stringScanner;
	private SQLCommentScanner commentScanner;

	public SQLConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			SQLPartitionScanner.SQL_COMMENT,
			SQLPartitionScanner.SQL_STRING};
	}
	
//	public ITextDoubleClickStrategy getDoubleClickStrategy(
//		ISourceViewer sourceViewer,
//		String contentType) {
//		if (doubleClickStrategy == null)
//			doubleClickStrategy = new XMLDoubleClickStrategy();
//		return doubleClickStrategy;
//	}

	protected SQLScanner getSQLScanner() {
		if (scanner == null) {
			scanner = new SQLScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(ISQLColorConstants.DEFAULT))));
		}
		return scanner;
	}

	protected SQLStringScanner getSQLStringScanner() {
		if (stringScanner == null) {
			stringScanner = new SQLStringScanner(colorManager);
			stringScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(ISQLColorConstants.SQL_STRING))));
		}
		return stringScanner;
	}

	protected SQLCommentScanner getSQLCommentScanner() {
		if (commentScanner == null) {
			commentScanner = new SQLCommentScanner(colorManager);
			commentScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(ISQLColorConstants.SQL_COMMENT))));
		}
		return commentScanner;
	}
	
//	protected SQLTagScanner getSQLTagScanner() {
//		if (tagScanner == null) {
//			tagScanner = new SQLTagScanner(colorManager);
//			tagScanner.setDefaultReturnToken(
//				new Token(
//					new TextAttribute(
//						colorManager.getColor(ISQLColorConstants.TAG))));
//		}
//		return tagScanner;
//	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr;
		dr = new DefaultDamagerRepairer(getSQLStringScanner());
		reconciler.setDamager(dr, SQLPartitionScanner.SQL_STRING);
		reconciler.setRepairer(dr, SQLPartitionScanner.SQL_STRING);

		dr = new DefaultDamagerRepairer(getSQLCommentScanner());
		reconciler.setDamager(dr, SQLPartitionScanner.SQL_COMMENT);
		reconciler.setRepairer(dr, SQLPartitionScanner.SQL_COMMENT);
		
		dr = new DefaultDamagerRepairer(getSQLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

//		NonRuleBasedDamagerRepairer ndr =
//			new NonRuleBasedDamagerRepairer(
//				new TextAttribute(
//					colorManager.getColor(ISQLColorConstants.SQL_COMMENT)));
//		reconciler.setDamager(ndr, SQLPartitionScanner.SQL_COMMENT);
//		reconciler.setRepairer(ndr, SQLPartitionScanner.SQL_COMMENT);

		return reconciler;
	}

}
