package org.scribble.ext.annot.parser.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GConnect;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.annot.ast.AnnotAstFactory;
import org.scribble.ext.annot.ast.AnnotString;
import org.scribble.ext.annot.ast.ScribAnnot;
import org.scribble.parser.scribble.ScribParser;
import org.scribble.parser.scribble.ast.global.AntlrGConnect;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

// Factor with AntlrGMessageTransfer?
public class AnnotAntlrGConnect
{
	public static final int MESSAGE_CHILD_INDEX = 2;
	public static final int SOURCE_CHILD_INDEX = 0;
	public static final int DESTINATION_CHILD_INDEX = 1;

	public static final int ANNOT_INDEX = 3;

	public static GConnect parseGConnect(ScribParser parser, CommonTree ct, AnnotAstFactory af) throws ScribParserException
	{
		RoleNode src = AntlrSimpleName.toRoleNode(getSourceChild(ct), af);
		MessageNode msg = AntlrGConnect.parseMessage(parser, getMessageChild(ct), af);
		RoleNode dest = AntlrSimpleName.toRoleNode(getDestinationChild(ct), af);
		//return AstFactoryImpl.FACTORY.GConnect(ct, src, msg, dest);
		////return AstFactoryImpl.FACTORY.GConnect(src, dest);

		// Cf. AntlrGMessageTransfer
		ScribAnnot annot = isEmptyAnnot(ct) ? null : parseAnnot(getAnnotChild(ct));
		/*if (annot != null)
		{
			MessageSigNode msn = (MessageSigNode) msg;  // FIXME: refactor properly
			msn.payloads.getElements().stream()
					.filter((p) -> p instanceof AnnotUnaryPayloadElem<?>)
					//.forEach((p) -> ((AnnotUnaryPayloadElemDel) p.del()).annot = annot);
					.forEach((p) -> ((AnnotUnaryPayloadElemDel) p.del()).setAnnot(annot));  // Doesn't work: inlining pass discards dels
		}*/
		return af.AnnotGConnect(ct, src, msg, dest, annot);
	}

	public static CommonTree getMessageChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(MESSAGE_CHILD_INDEX);
	}

	public static CommonTree getSourceChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(SOURCE_CHILD_INDEX);
	}

	public static CommonTree getDestinationChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(DESTINATION_CHILD_INDEX);
	}
	

	// Duplicated from AntlrGMessageTransfer
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
