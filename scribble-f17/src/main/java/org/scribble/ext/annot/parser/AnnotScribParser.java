/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.annot.parser;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.ScribNode;
import org.scribble.ext.annot.ast.AnnotAstFactory;
import org.scribble.ext.annot.parser.ast.AnnotAntlrPayloadElemList;
import org.scribble.ext.annot.parser.ast.global.AnnotAntlrGConnect;
import org.scribble.ext.annot.parser.ast.global.AnnotAntlrGMessageTransfer;
import org.scribble.parser.scribble.AntlrConstants;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.util.ScribParserException;

public class AnnotScribParser extends ScribParser
{
	// FIXME: refactor pattern (cf. AntlrConstants) -- cannot extend existing node type enum though
	//public static final String ANNOT_GLOBALMESSAGETRANSFER_NODE_TYPE = "ASSRTGLOBALMESSAGETRANSFER";
	public static final String ANNOT_ANNOTPAYLOADELEM_NODE_TYPE = "ANNOTPAYLOADELEMENT";
	
	//public final AssrtAssertParser ap = AssrtAssertParser.getInstance();

	public AnnotScribParser()
	{

	}

	@Override
	public ScribNode parse(CommonTree ct, AstFactory af) throws ScribParserException
	{
		ScribParser.checkForAntlrErrors(ct);
		
		AnnotAstFactory aaf = (AnnotAstFactory) af;
		String type = ct.getToken().getText();  // Duplicated from ScribParserUtil.getAntlrNodeType  // FIXME: factor out with AssrtAntlrPayloadElemList.parsePayloadElem
		switch (type)
		{
			// N.B. will "override" base payload parsing in super -- FIXME: hacky
			// AssrtAntlrPayloadElemList used to parse both annotated and non-annotated payload elems -- i.e., original AntlrPayloadElemList is now redundant
			case AntlrConstants.PAYLOAD_NODE_TYPE:               return AnnotAntlrPayloadElemList.parsePayloadElemList(this, ct, aaf);
			
			// AnnotScribble.g modifies original GLOBALMESSAGETRANSFER category with extra annot EXTIDENTIFIER
			case AntlrConstants.GLOBALMESSAGETRANSFER_NODE_TYPE: return AnnotAntlrGMessageTransfer.parseAnnotGMessageTransfer(this, ct, aaf);
			case AntlrConstants.GLOBALCONNECT_NODE_TYPE:         return AnnotAntlrGConnect.parseAnnotGConnect(this, ct, aaf);

			default: return super.parse(ct, af);
		}
	}
}
