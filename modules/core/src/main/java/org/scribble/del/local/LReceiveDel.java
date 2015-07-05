package org.scribble.del.local;

import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.MessageSigNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LReceive;
import org.scribble.del.MessageTransferDel;
import org.scribble.main.ScribbleException;
import org.scribble.model.local.Receive;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.FsmBuilder;
import org.scribble.visit.InlinedProtocolUnfolder;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.env.UnfoldingEnv;


public class LReceiveDel extends MessageTransferDel implements LSimpleInteractionNodeDel
{
	@Override
	public LReceive leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner builder, ScribNode visited) throws ScribbleException
	{
		LReceive gmt = (LReceive) visited;
		LReceive inlined = AstFactoryImpl.FACTORY.LReceive(gmt.src, gmt.msg, gmt.dests);  // FIXME: clone
		builder.pushEnv(builder.popEnv().setTranslation(inlined));
		return (LReceive) super.leaveProtocolInlining(parent, child, builder, gmt);
	}

	@Override
	public void enterInlinedProtocolUnfolding(ScribNode parent, ScribNode child, InlinedProtocolUnfolder unf) throws ScribbleException
	{
		UnfoldingEnv env = unf.popEnv();
		env = env.noUnfold();
		unf.pushEnv(env);
	}

	/*@Override
	public LReceive leaveReachabilityCheck(ScribNode parent, ScribNode child, ReachabilityChecker checker, ScribNode visited) throws ScribbleException
	{
		return (LReceive) LSimpleInteractionNodeDel.super.leaveReachabilityCheck(parent, child, checker, visited);
	}*/

	@Override
	public LReceive leaveFsmBuilder(ScribNode parent, ScribNode child, FsmBuilder conv, ScribNode visited)
	{
		LReceive lr = (LReceive) visited;
		Role peer = lr.src.toName();
		MessageId mid = lr.msg.toMessage().getId();
		Payload payload =
				(lr.msg.isMessageSigNode())  // Hacky?
					? ((MessageSigNode) lr.msg).payloads.toPayload()
					: Payload.EMPTY_PAYLOAD;
		conv.builder.addEdge(conv.builder.getEntry(), new Receive(peer, mid, payload), conv.builder.getExit());
		return (LReceive) super.leaveFsmBuilder(parent, child, conv, lr);
	}
}
