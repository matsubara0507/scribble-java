package org.scribble.ext.f17.del;

import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDelBase;
import org.scribble.ext.f17.ast.AnnotUnaryPayloadElem;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.visit.wf.NameDisambiguator;

public class AnnotUnaryPayloadElemDel extends ScribDelBase
{
	@Override
	public void enterDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb) throws ScribbleException
	{
		AnnotUnaryPayloadElem<?> pe = (AnnotUnaryPayloadElem<?>) child;
		disamb.addParameter(pe.payvar.toName(), DataTypeKind.KIND);
	}
}
