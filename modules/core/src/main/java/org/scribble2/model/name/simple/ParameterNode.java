package org.scribble2.model.name.simple;

import org.scribble2.model.MessageNode;
import org.scribble2.sesstype.Argument;
import org.scribble2.sesstype.Message;
import org.scribble2.sesstype.kind.DataTypeKind;
import org.scribble2.sesstype.kind.Kind;
import org.scribble2.sesstype.kind.SigKind;
import org.scribble2.sesstype.name.DataType;
import org.scribble2.sesstype.name.MessageSigName;
import org.scribble2.sesstype.name.Name;

//public class ParameterNode extends SimpleNameNode<Parameter> implements PayloadElementNameNode, MessageNode//, ArgumentInstantiation//, PayloadTypeOrParameterNode
//public class ParameterNode<K extends Kind> extends SimpleNameNode<Name<K>, K> implements PayloadElementNameNode, MessageNode//, ArgumentInstantiation//, PayloadTypeOrParameterNode
public class ParameterNode<K extends Kind> extends SimpleNameNode<Name<K>, K> implements MessageNode//ArgumentNode //, ArgumentInstantiation//, PayloadTypeOrParameterNode
{
	// FIXME: maybe shouldn't be kinded, as just AST node?  or do name/kind disambiguation -- maybe disamb not needed, AmbiguousNameNode --disamb--> kinded Parameter
	
	public final K kind;
	
	//public ParameterNode(String identifier)//, Kind kind)
	public ParameterNode(K kind, String identifier)//, Kind kind)
	{
		super(identifier);
		this.kind = kind;
	}

	/*@Override
	protected ParameterNode reconstruct(String identifier)
	{
		ModelDel del = del();  // Default delegate assigned in ModelFactoryImpl for all simple names
		ParameterNode pn = new ParameterNode(identifier);
		pn = (ParameterNode) pn.del(del);
		return pn;
	}*/

	@Override
	protected ParameterNode<K> copy()
	{
		//return new ParameterNode<>(this.identifier);
		return new ParameterNode<>(this.kind, this.identifier);
	}
	
	/*// Only useful for MessageSignatureDecls -- FIXME: integrate sig decls properly
	@Override
	public ParameterNode leaveProjection(Projector proj) //throws ScribbleException
	{
		ParameterNode projection = new ParameterNode(null, toName().toString(), this.kind);
		this.setEnv(new ProjectionEnv(proj.getJobContext(), proj.getModuleContext(), projection));
		return this;
	}
	
	@Override
	public ArgumentNode substitute(Substitutor subs)
	{
		return subs.getArgumentSubstitution(toName());
	}*/
	
	@Override
	//public Parameter toName()
	public Name<K> toName()
	{
		//return new Parameter(null, this.identifier);
		if (this.kind.equals(SigKind.KIND))
		{
			return Kind.castName(kind, new MessageSigName(this.identifier));
		}
		else if (this.kind.equals(DataTypeKind.KIND))
		{
			return Kind.castName(kind, new DataType(this.identifier));
		}
		else
		{
			throw new RuntimeException("Shouldn't get in here: " + this.kind);
		}
	}

	@Override
	public boolean isMessageSignatureNode()
	{
		return false;
	}

	@Override
	public boolean isPayloadTypeNode()
	{
		return false;
	}

	@Override
	public boolean isParameterNode()
	{
		return true;
	}

	/*//@Override
	public PayloadType toPayloadTypeOrParameter()
	{
		//if (this.kind != Kind.TYPE)
		{
			throw new RuntimeException("Not a type-kind parameter: " + this);
		}
		//return toName();
	}*/
	
	/*@Override
	public Operator getOperator()
	{
		return new Operator(toString());
	}*/

	@Override
	//public Parameter toArgument()
	//public Name<K> toArgument()
	public Argument<? extends Kind> toArgument()
	{
		//return toName();
		throw new RuntimeException("TODO: " + this);
	}

	//@Override
	//public Parameter toMessage()
	//public Name<K> toMessage()
	public Message toMessage()
	{
		if (!this.kind.equals(SigKind.KIND))
		{
			throw new RuntimeException("Shouldn't get in here: " + this);
		}
		return (Message) toName();
	}

	/*@Override
	public Argument<? extends Kind> toArgument(Scope scope)
	{
		// TODO Auto-generated method stub
		return null;
	}*/

	/*@Override
	public boolean isAmbiguousNode()
	{
		return false;
	}*/
}
