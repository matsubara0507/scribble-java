package org.scribble2.model;

import org.scribble2.model.name.simple.SimpleProtocolNameNode;
import org.scribble2.model.visit.ModelVisitor;
import org.scribble2.util.ScribbleException;


// TODO: parameterize on global/local role decl list
public abstract class ProtocolHeader extends ModelNodeBase
{
	public final SimpleProtocolNameNode name;
	public final RoleDeclList roledecls;
	public final ParameterDeclList paramdecls;

	protected ProtocolHeader(SimpleProtocolNameNode name, RoleDeclList roledecls, ParameterDeclList paramdecls)
	{
		this.name = name;
		this.roledecls = roledecls;
		this.paramdecls = paramdecls;
	}
	
	protected abstract ProtocolHeader reconstruct(SimpleProtocolNameNode name, RoleDeclList rdl, ParameterDeclList pdl);
	
	@Override
	public ProtocolHeader visitChildren(ModelVisitor nv) throws ScribbleException
	{
		RoleDeclList rdl = (RoleDeclList) visitChild(this.roledecls, nv);
		ParameterDeclList pdl = (ParameterDeclList) visitChild(this.paramdecls, nv);
		return reconstruct(this.name, rdl, pdl);
	}

	public boolean isParameterDeclListEmpty()
	{
		return this.paramdecls.isEmpty();
	}

	@Override
	public String toString()
	{
		String s = Constants.PROTOCOL_KW + " " + this.name;
		if (!isParameterDeclListEmpty())
		{
			s += this.paramdecls;
		}
		return s + this.roledecls;
	}
}
