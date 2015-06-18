package org.scribble.ast;

import org.scribble.ast.name.NameNode;
import org.scribble.ast.visit.AstVisitor;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.Kind;
import org.scribble.sesstype.name.Role;



// Names that are declared in a protocol header (roles and parameters -- not the protocol name though)
//public interface HeaderParameterDecl extends ModelNode, NameDecl //implements NameDeclaration
//public abstract class HeaderParamDecl<T extends SimpleNameNode<T2>, T2 extends SimpleName> extends NameDeclNode<T, T2> //implements NameDeclaration
//public abstract class HeaderParamDecl<T2 extends Name<K>, K extends Kind> extends NameDeclNode<T2, K> //implements NameDeclaration
public abstract class HeaderParamDecl<K extends Kind> extends NameDeclNode<K> //implements NameDeclaration
{
	//public final T name;
	//public final NameNode<T2, K> name;

	//protected HeaderParamDecl(T name)
	//protected HeaderParamDecl(NameNode<T2, K> name)
	protected HeaderParamDecl(NameNode<K> name)
	{
		//this.name = name;
		super(name);
	}

	//protected abstract HeaderParamDecl<T, T2> reconstruct(T namenode);
	//protected abstract HeaderParamDecl<T2, K> reconstruct(NameNode<T2, K> namenode);
	protected abstract HeaderParamDecl<K> reconstruct(NameNode<K> namenode);
	
	@Override
	//public HeaderParamDecl<T, T2> visitChildren(ModelVisitor nv) throws ScribbleException
	//public HeaderParamDecl<T2, K> visitChildren(ModelVisitor nv) throws ScribbleException
	public HeaderParamDecl<K> visitChildren(AstVisitor nv) throws ScribbleException
	{
		//SimpleNameNode name = (SimpleNameNode) visitChild(this.name, nv);
		//T name = visitChildWithClassCheck(this, this.name, nv);
		//NameNode<T2, K> name = visitChildWithClassCheck(this, this.name, nv);
		NameNode<K> name = visitChildWithClassCheck(this, this.name, nv);
		return reconstruct(name);
	}
	
	//public abstract NameDeclNode<T, T2> project(Role self);
	//public abstract HeaderParamDecl<T2, K> project(Role self);
	public abstract HeaderParamDecl<K> project(Role self);

	/*@Override
	//public SimpleName toName()
	public T2 getName()
	{
		return this.name.getName();
	}*/
	
	public abstract String getKeyword();
	
	@Override
	public String toString()
	{
		//return toName().toString();  // FIXME: need declaration kind keyword
		return getKeyword() + " " + getDeclName().toString();
	}
}

/*public class HeaderParameterDecl<T extends SimpleNameNode> extends ModelNodeBase //implements NameDeclaration
{
	//public final Kind kind;
	public final T name;

	//public NameDecl(Token t, Kind kind, T namenode)
	public HeaderParameterDecl(T namenode)
	{
		//this.kind = kind;
		this.name = namenode;
	}
	
	/*@Override
	public NameDecl disambiguate(PayloadTypeOrParameterDisambiguator disamb) throws ScribbleException
	{
		disamb.getEnv().params.addParameter(this.name.toName(), this.kind);
		return (NameDecl) super.disambiguate(disamb);
	}*/

	/*@Override
	public NameDecl<T> visitChildren(NodeVisitor nv) throws ScribbleException
	{
		T name = visitChildWithClassCheck(this, this.name, nv);
		return new NameDecl<T>(this.ct, this.kind, name);
	}* /

	@Override
	public String toString()
	{
		//return this.kind + " " + this.name;
		return this.name.toString();
	}
}*/
