package org.scribble.ext.annot.parser.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.PayloadElem;
import org.scribble.ast.PayloadElemList;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.ast.name.qualified.DataTypeNode;
import org.scribble.ast.name.simple.AmbigNameNode;
import org.scribble.ext.annot.ast.AnnotAstFactory;
import org.scribble.ext.annot.ast.name.simple.PayloadVarNode;
import org.scribble.ext.annot.parser.AnnotScribParser;
import org.scribble.ext.annot.parser.ast.name.AnnotAntlrSimpleName;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.parser.scribble.ScribParserUtil;
import org.scribble.parser.scribble.ast.AntlrPayloadElemList;
import org.scribble.parser.scribble.ast.name.AntlrAmbigName;
import org.scribble.parser.scribble.ast.name.AntlrQualifiedName;

public class AnnotAntlrPayloadElemList
{
	// Cf. AntlrNonRoleArgList
	public static PayloadElemList parsePayloadElemList(ScribParser parser, CommonTree ct, AnnotAstFactory af)
	{
		// As in AntlrNonRoleArgList, i.e. payloadelem (NonRoleArg) not directly parsed -- cf. rolearg and nonroleparamdecl, which are directly parsed (not consistent), due to amibgious names
		//List<PayloadElem> pes = getPayloadElements(ct).stream().map((pe) -> parsePayloadElem(pe)).collect(Collectors.toList());
		List<PayloadElem<?>> pes = AntlrPayloadElemList.getPayloadElements(ct).stream()
				.map(pe -> parsePayloadElem(pe, af)).collect(Collectors.toList());
		return af.PayloadElemList(ct, pes);
	}

	protected static PayloadElem<?> parsePayloadElem(CommonTree ct, AnnotAstFactory af)
	{
		String type = ct.getToken().getText();  // Duplicated from ScribParserUtil.getAntlrNodeType  // FIXME: factor out with AnnotScribParser.parse
		switch (type)
		{
			case AnnotScribParser.ANNOT_ANNOTPAYLOADELEM_NODE_TYPE:
			{
			PayloadVarNode pvn = AnnotAntlrSimpleName.toPayloadVarNode((CommonTree) ct.getChild(0), af);
			PayloadElemNameNode<?> penn = parsePayloadElemenNameNode((CommonTree) ct.getChild(1), af);
			return af.AnnotUnaryPayloadElem(ct, pvn, penn);
			}
			default: return AntlrPayloadElemList.parsePayloadElem(ct, af);
		}
	}

	private static PayloadElemNameNode<?> parsePayloadElemenNameNode(CommonTree ct, AstFactory af)
	{
		if (ct.getChildCount() > 1)
		{
			DataTypeNode dt = AntlrQualifiedName.toDataTypeNameNode(ct, af);
			////return AstFactoryImpl.FACTORY.PayloadElem(dt);
			//return AstFactoryImpl.FACTORY.UnaryPayloadElem(ct, dt);  // return and dt share same ct in this case
			return dt;
		}
		else
		{
			// Similarly to NonRoleArg: cannot syntactically distinguish right now between SimplePayloadTypeNode and ParameterNode
			AmbigNameNode an = AntlrAmbigName.toAmbigNameNode(ct, af);
			//return AstFactoryImpl.FACTORY.UnaryPayloadElem(ct, an);
			return an;
		}
	}

	public static final List<CommonTree> getPayloadElements(CommonTree ct)
	{
		return (ct.getChildCount() == 0)
				? Collections.emptyList()
				: ScribParserUtil.toCommonTreeList(ct.getChildren());
	}
}
