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

package de.chdev.artools.sql.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;

public class SQLScanner extends RuleBasedScanner {

	public SQLScanner(ColorManager manager) {

		// Initialize the different token types
		IToken defaultToken = new Token(new TextAttribute(
				manager.getColor(ISQLColorConstants.DEFAULT)));
		IToken keywordToken = new Token(new TextAttribute(
				manager.getColor(ISQLColorConstants.SQL_KEYWORD), null,
				SWT.BOLD));
		IToken reservedToken = new Token(new TextAttribute(
				manager.getColor(ISQLColorConstants.SQL_RESERVED)));

		// Define keyword rule
		List<String> keywords = new ArrayList<String>();
		keywords.add("select");
		keywords.add("from");
		keywords.add("where");
		keywords.add("join");
		keywords.add("on");
		keywords.add("full");
		keywords.add("left");
		keywords.add("right");
		keywords.add("inner");
		keywords.add("outer");
		keywords.add("group");
		keywords.add("by");
		keywords.add("order");
		keywords.add("having");
		keywords.add("as");
		keywords.add("update");
		keywords.add("delete");
		keywords.add("is");
		keywords.add("set");
		keywords.add("create");
		keywords.add("not");

		List<String> reserved = new ArrayList<String>();
		reserved.add("and");
		reserved.add("or");
		reserved.add("null");
		reserved.add("like");

		WordRule wordRule = new WordRule(new SQLKeywordDetector(),
				defaultToken, true);
		for (String keyword : keywords) {
			wordRule.addWord(keyword, keywordToken);
		}
		for (String word : reserved) {
			wordRule.addWord(word, reservedToken);
		}

		IRule[] rules = new IRule[1];
		// Add rule for keywords
		rules[0] = wordRule;

		setRules(rules);
	}
}
