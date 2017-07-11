package org.scribble.ext.annot.parser.ast.global;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.annot.ast.AnnotAstFactory;
import org.scribble.ext.annot.ast.AnnotString;
import org.scribble.ext.annot.ast.ScribAnnot;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.parser.scribble.ScribParserUtil;
import org.scribble.parser.scribble.ast.global.AntlrGMessageTransfer;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class AnnotAntlrGMessageTransfer
{
	public static final int MESSAGE_CHILD_INDEX = 0;
	public static final int SOURCE_CHILD_INDEX = 1;
	//public static final int DESTINATION_CHILDREN_START_INDEX = 2;
	public static final int DESTINATION_CHILDREN_START_INDEX = 3;
	
	public static final int ANNOT_INDEX = 2;

	public static GMessageTransfer parseAnnotGMessageTransfer(ScribParser parser, CommonTree ct, AnnotAstFactory af) throws ScribParserException
	{
		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = AntlrGMessageTransfer.parseMessage(parser, getMessageChild(ct), af);
		List<RoleNode> dests = 
			getDestChildren(ct).stream().map(d -> AntlrSimpleName.toRoleNode(d, af)).collect(Collectors.toList());
		//return AstFactoryImpl.FACTORY.GMessageTransfer(ct, src, msg, dests);

		ScribAnnot annot = isEmptyAnnot(ct) ? null : parseAnnot(getAnnotChild(ct));
		/*if (annot != null)
		{
			MessageSigNode msn = (MessageSigNode) msg;  // FIXME: refactor properly
			msn.payloads.getElements().stream()
					.filter((p) -> p instanceof AnnotUnaryPayloadElem<?>)
					.forEach((p) -> 
							//((AnnotUnaryPayloadElemDel) p.del()).annot = annot );
							((AnnotUnaryPayloadElemDel) p.del()).setAnnot(annot) );  // Doesn't work: inlining pass discards dels
		}*/
		return af.GMessageTransfer(ct, src, msg, dests, annot);
	}

	public static CommonTree getMessageChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(MESSAGE_CHILD_INDEX);
	}

	public static CommonTree getSourceChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(SOURCE_CHILD_INDEX);
	}

	public static List<CommonTree> getDestChildren(CommonTree ct)
	{
		return ScribParserUtil.toCommonTreeList(ct.getChildren().subList(DESTINATION_CHILDREN_START_INDEX, ct.getChildCount()));
	}
	

	public static CommonTree getAnnotChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(ANNOT_INDEX);
	}
	
	protected static ScribAnnot parseAnnot(CommonTree ct)
	{
		String val = ct.getText();
		val = val.substring(val.indexOf('\"')+1, val.lastIndexOf('\"'));
		val = val.trim();
		return new AnnotString(val);
	}
	
	protected static boolean isEmptyAnnot(CommonTree ct)
	{
		//return ct.getText().equals("EMPTYANNOT");  // FIXME: refactor properly
		return getAnnotChild(ct).getText().equals("EMPTY_ANNOT");
	}
}
