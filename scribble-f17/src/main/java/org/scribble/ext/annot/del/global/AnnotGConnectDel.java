package org.scribble.ext.annot.del.global;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.ScribNode;
import org.scribble.del.global.GConnectDel;
import org.scribble.ext.annot.ast.AnnotScribDel;
import org.scribble.ext.annot.ast.AnnotUnaryPayloadElem;
import org.scribble.ext.annot.ast.ScribAnnot;
import org.scribble.ext.annot.ast.global.AnnotGConnect;
import org.scribble.ext.annot.del.AnnotUnaryPayloadElemDel;
import org.scribble.ext.annot.visit.context.AnnotSetter;
import org.scribble.main.ScribbleException;

public class AnnotGConnectDel extends GConnectDel implements AnnotScribDel
{
	public AnnotGConnectDel()
	{
		
	}

	// Duplicated from GMessageTransferDel
	@Override
	public ScribNode leaveAnnotSetting(ScribNode parent, ScribNode child, AnnotSetter rem, ScribNode visited) throws ScribbleException
	{
		AnnotGConnect gc = (AnnotGConnect) visited;
		ScribAnnot annot = gc.annot;
		if (annot != null)
		{
			MessageSigNode msn = (MessageSigNode) gc.msg;  // FIXME: refactor properly
			msn.payloads.getElements().stream()
					.filter(p -> p instanceof AnnotUnaryPayloadElem<?>)
					.forEach(p -> 
							//((AnnotUnaryPayloadElemDel) p.del()).annot = annot );
							((AnnotUnaryPayloadElemDel) p.del()).setAnnot(annot) );
		}
		return gc;
	}
}
