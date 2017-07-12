package org.scribble.ext.annot.ast;
		
//gmt = del(gmt, new GMessageTransferDel());

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.MessageNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.name.NameNode;
import org.scribble.ast.name.PayloadElemNameNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.annot.ast.global.AnnotGConnect;
import org.scribble.ext.annot.ast.global.AnnotGMessageTransfer;
import org.scribble.ext.annot.ast.name.simple.PayloadVarNode;
import org.scribble.ext.annot.del.AnnotUnaryPayloadElemDel;
import org.scribble.ext.annot.del.global.AnnotGConnectDel;
import org.scribble.ext.annot.del.global.AnnotGMessageTransferDel;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.sesstype.kind.Kind;
import org.scribble.sesstype.kind.PayloadTypeKind;


public class AnnotAstFactoryImpl extends AstFactoryImpl implements AnnotAstFactory
{
	
	// "Overriding" existing node creation

	@Override  // No, still used as previously for non annotated payloads (AnnotAntlrPayloadElemList.parsePayloadElem default)
	public <K extends PayloadTypeKind> AnnotUnaryPayloadElem<K> UnaryPayloadElem(CommonTree source, PayloadElemNameNode<K> name)
	{
		AnnotUnaryPayloadElem<K> pe = new AnnotUnaryPayloadElem<>(source, name);  // Maybe unnecessary, super is fine
		pe = del(pe, createDefaultDelegate());
		//pe = del(pe, new AnnotUnaryPayloadElemDel());
		return pe;
	}

	@Override
	public GMessageTransfer GMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		/*AnnotGMessageTransfer gmt = new AnnotGMessageTransfer(source, src, msg, dests);  // Maybe unnecessary, super is fine
		gmt = del(gmt, new GMessageTransferDel());
		return gmt;*/
		throw new RuntimeException("[f17] Shouldn't get in here: " + source);
	}

	@Override
	public AnnotGConnect GConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest)
	{
		/*AnnotGConnect gc = new AnnotGConnect(source, src, msg, dest);  // Maybe unnecessary, super is fine
		gc = del(gc, new GConnectDel());
		return gc;*/
		throw new RuntimeException("[f17] Shouldn't get in here: " + source);
	}
	
	
	// New node creation

	@Override
	public AnnotGMessageTransfer AnnotGMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests, ScribAnnot annot)
	{
		AnnotGMessageTransfer gmt = new AnnotGMessageTransfer(source, src, msg, dests, annot);
		gmt = del(gmt, new AnnotGMessageTransferDel());
		return gmt;
	}

	@Override
	public AnnotGConnect AnnotGConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, ScribAnnot annot)
	{
		AnnotGConnect gc = new AnnotGConnect(source, src, msg, dest, annot);
		//gc = del(gc, new GConnectDel());
		gc = del(gc, new AnnotGConnectDel());
		return gc;
	}

	@Override
	public <K extends PayloadTypeKind> AnnotUnaryPayloadElem<K> AnnotUnaryPayloadElem(CommonTree source, PayloadVarNode payvar, PayloadElemNameNode<K> name)
	{
		AnnotUnaryPayloadElem<K> pe = new AnnotUnaryPayloadElem<>(source, payvar, name);
		//pe = del(pe, createDefaultDelegate());
		pe = del(pe, new AnnotUnaryPayloadElemDel());
		return pe;
	}
	
	// Duplicated from AstFactoryImpl
	@Override
	public <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier)
	{
		NameNode<? extends Kind> snn = null;
		
		// Default del
		if (kind.equals(DataTypeKind.KIND))  // No conflict with super? (regular simple data type name nodes?)
		{
			snn = new PayloadVarNode(source, identifier);
			return castNameNode(kind, del(snn, createDefaultDelegate()));
		}
		else
		{
			return super.SimpleNameNode(source, kind, identifier);
		}
	}
	
	
	

	/*@Override
	public GConnect GConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest)
	//public GConnect GConnect(RoleNode src, RoleNode dest)
	{
		GConnect gc = new GConnect(source, src, msg, dest);
		//GConnect gc = new GConnect(src, dest);
		gc = del(gc, new GConnectDel());
		return gc;
	}

	@Override
	public GDisconnect GDisconnect(CommonTree source, RoleNode src, RoleNode dest)
	{
		GDisconnect gc = new GDisconnect(source, src, dest);
		gc = del(gc, new GDisconnectDel());
		return gc;
	}
	@Override
	public LSend LSend(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		LSend ls = new LSend(source, src, msg, dests);
		ls = del(ls, new LSendDel());
		return ls;
	}

	@Override
	public LReceive LReceive(CommonTree source, RoleNode src, MessageNode msg, List<RoleNode> dests)
	{
		LReceive ls = new LReceive(source, src, msg, dests);
		ls = del(ls, new LReceiveDel());
		return ls;
	}
	
	@Override
	public LConnect LConnect(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest)
	//public LConnect LConnect(RoleNode src, RoleNode dest)
	{
		LConnect lc = new LConnect(source, src, msg, dest);
		//LConnect lc = new LConnect(src, dest);
		lc = del(lc, new LConnectDel());
		return lc;
	}

	@Override
	public LAccept LAccept(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest)
	//public LAccept LAccept(RoleNode src, RoleNode dest)
	{
		LAccept la = new LAccept(source, src, msg, dest);
		//LAccept la = new LAccept(src, dest);
		la = del(la, new LAcceptDel());
		return la;
	}

	@Override
	public LDisconnect LDisconnect(CommonTree source, RoleNode self, RoleNode peer)
	{
		LDisconnect lc = new LDisconnect(source, self, peer);
		lc = del(lc, new LDisconnectDel());
		return lc;
	}*/
}
