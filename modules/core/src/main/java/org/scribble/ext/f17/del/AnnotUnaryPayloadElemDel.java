package org.scribble.ext.f17.del;

import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDelBase;
import org.scribble.ext.f17.ast.AnnotUnaryPayloadElem;
import org.scribble.ext.f17.sesstype.name.PayloadVar;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.visit.wf.NameDisambiguator;

public class AnnotUnaryPayloadElemDel extends ScribDelBase
{
	@Override
	public void enterDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb) throws ScribbleException
	{
		// N.B. payload params counted as protodecl params, so cannot shadow those
		AnnotUnaryPayloadElem<?> pe = (AnnotUnaryPayloadElem<?>) child;
		PayloadVar pv = pe.payvar.toName();
		if (disamb.isBoundParameter(pv))
		{
			throw new ScribbleException("[f17] Duplicate payload var: " + pv);  // FIXME: probably doesn't check across protodecls
		}
		disamb.addParameter(pv, DataTypeKind.KIND);
	}
}
