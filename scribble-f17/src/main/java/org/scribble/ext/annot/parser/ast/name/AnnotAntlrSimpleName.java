package org.scribble.ext.annot.parser.ast.name;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ext.annot.ast.name.simple.PayloadVarNode;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.sesstype.kind.DataTypeKind;

public class AnnotAntlrSimpleName
{
	// FIXME: refactor properly
	public static PayloadVarNode toPayloadVarNode(CommonTree ct, AstFactory af)
	{
		//return (PayloadVarNode) AstFactoryImpl.FACTORY.SimpleNameNode(ct, PayloadVarKind.KIND, getName(ct));
		return (PayloadVarNode) af.SimpleNameNode(ct, DataTypeKind.KIND, AntlrSimpleName.getName(ct));
	}
}
