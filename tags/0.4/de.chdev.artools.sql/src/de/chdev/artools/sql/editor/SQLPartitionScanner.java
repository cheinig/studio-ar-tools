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

import org.eclipse.jface.text.rules.*;

public class SQLPartitionScanner extends RuleBasedPartitionScanner {
	public final static String SQL_COMMENT = "__sql_comment";
	public final static String SQL_STRING = "__sql_string";

	public SQLPartitionScanner() {

		IToken sqlComment = new Token(SQL_COMMENT);
		IToken sqlString = new Token(SQL_STRING);

		IPredicateRule[] rules = new IPredicateRule[3];

		rules[0] = new MultiLineRule("/*", "*/", sqlComment);
		rules[1] = new SingleLineRule("--", "\n", sqlComment,'\\',true);
		rules[2] = new MultiLineRule("'", "'", sqlString);

		setPredicateRules(rules);
	}
}
