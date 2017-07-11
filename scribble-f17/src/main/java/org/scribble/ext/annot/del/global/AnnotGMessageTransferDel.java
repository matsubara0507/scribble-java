package org.scribble.ext.annot.del.global;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.ScribNode;
import org.scribble.del.global.GMessageTransferDel;
import org.scribble.ext.annot.ast.AnnotScribDel;
import org.scribble.ext.annot.ast.AnnotUnaryPayloadElem;
import org.scribble.ext.annot.ast.ScribAnnot;
import org.scribble.ext.annot.ast.global.AnnotGMessageTransfer;
import org.scribble.ext.annot.del.AnnotUnaryPayloadElemDel;
import org.scribble.ext.annot.visit.context.AnnotSetter;
import org.scribble.main.ScribbleException;

public class AnnotGMessageTransferDel extends GMessageTransferDel implements AnnotScribDel
{
	public AnnotGMessageTransferDel()
	{
		
	}

	/*@Override
	public ScribNode leaveF17Parsing(ScribNode parent, ScribNode child, F17Parser parser, ScribNode visited) throws ScribbleException
	{
		F17ParserEnv env = parser.peekEnv();
		if (env.isUnguarded())
		{
			parser.popEnv();
			parser.pushEnv(new F17ParserEnv());  // Maybe make "setGuarded" method
		}
		return super.leaveF17Parsing(parent, child, parser, visited);
	}*/

	@Override
	public ScribNode leaveAnnotSetting(ScribNode parent, ScribNode child, AnnotSetter rem, ScribNode visited) throws ScribbleException
	{
		AnnotGMessageTransfer gmt = (AnnotGMessageTransfer) visited;
		ScribAnnot annot = gmt.annot;
		if (annot != null)
		{
			MessageSigNode msn = (MessageSigNode) gmt.msg;  // FIXME: refactor properly
			msn.payloads.getElements().stream()
					.filter((p) -> p instanceof AnnotUnaryPayloadElem<?>)
					.forEach((p) -> 
							//((AnnotUnaryPayloadElemDel) p.del()).annot = annot );
							((AnnotUnaryPayloadElemDel) p.del()).setAnnot(annot) );
		}
		return gmt;
	}
}
