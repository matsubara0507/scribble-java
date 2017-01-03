package org.scribble.ext.f17.ast;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.UnaryPayloadElem;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.ext.f17.ast.name.simple.PayloadVarNode;
import org.scribble.sesstype.kind.PayloadTypeKind;


public interface AnnotAstFactory extends AstFactory
{
	<K extends PayloadTypeKind> UnaryPayloadElem<K> UnaryPayloadElem(CommonTree source, PayloadElemNameNode<K> name);
	<K extends PayloadTypeKind> AnnotUnaryPayloadElem<K> AnnotUnaryPayloadElem(CommonTree source, PayloadVarNode payvar, PayloadElemNameNode<K> name);

	/*GMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests);
	GConnect GConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest);
	GDisconnect GDisconnect(CommonTree source, RoleNode src, RoleNode dest);
	
	LSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests);
	LReceive LReceive(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests);
	LConnect LConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest);
	LAccept LAccept(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest);*/
}
