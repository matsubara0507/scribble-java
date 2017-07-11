package org.scribble.ext.annot.del.name;

import org.scribble.ast.MessageTransfer;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.ScribNode;
import org.scribble.ast.context.ModuleContext;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.del.name.AmbigNameNodeDel;
import org.scribble.ext.annot.visit.AnnotNameDisambiguator;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.sesstype.kind.SigKind;
import org.scribble.sesstype.name.AmbigName;
import org.scribble.visit.wf.NameDisambiguator;

public class AnnotAmbigNameNodeDel extends AmbigNameNodeDel
{
	public AnnotAmbigNameNodeDel()
	{

	}

	@Override
	public ScribNode leaveDisambiguation(ScribNode parent, ScribNode child, NameDisambiguator disamb, ScribNode visited) throws ScribbleException
	{
		ModuleContext mcontext = disamb.getModuleContext();
		AmbigNameNode ann = (AmbigNameNode) visited;
		AmbigName name = ann.toName();
		// By well-formedness (checked later), payload type and parameter names are distinct
		// FIXME: are conflicts checked elsewhere?
		if (mcontext.isDataTypeVisible(name.toDataType()))
		{
			if (parent instanceof MessageTransfer<?>)  // FIXME HACK: MessageTransfer assumes MessageNode (cast in visitChildren), so this needs to be caught here  // FIXME: other similar cases?
			{
				throw new ScribbleException(ann.getSource(), "Invalid occurrence of data type: " + parent);
			}
			return disamb.job.af.QualifiedNameNode(ann.getSource(), DataTypeKind.KIND, name.getElements());
		}
		else if (mcontext.isMessageSigNameVisible(name.toMessageSigName()))
		{
			if (parent instanceof PayloadElem)  // FIXME HACK
			{
				throw new ScribbleException(ann.getSource(), "Invalid occurrence of message signature name: " + parent);
			}
			return disamb.job.af.QualifiedNameNode(ann.getSource(), SigKind.KIND, name.getElements());
		}

		else if (((AnnotNameDisambiguator) disamb).isBoundPayloadVar(name.toDataType()))
		{
			return disamb.job.af.SimpleNameNode(ann.getSource(), DataTypeKind.KIND, name.toString());
		}
			
		else if (((AnnotNameDisambiguator) disamb).isBoundParameter(name))
		{
			return disamb.job.af.NonRoleParamNode(ann.getSource(), disamb.getParameterKind(name), name.toString());
		}
		throw new ScribbleException(ann.getSource(), "Cannot disambiguate name: " + name);
	}
}
