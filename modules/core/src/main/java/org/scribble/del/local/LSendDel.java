package org.scribble.del.local;

import java.util.List;

import org.scribble.ast.MessageSigNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LSend;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.MessageTransferDel;
import org.scribble.model.local.Send;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.EndpointGraphBuilder;
import org.scribble.visit.ProjectedChoiceSubjectFixer;

public class LSendDel extends MessageTransferDel implements LSimpleInteractionNodeDel
{
	@Override
	public LSend leaveGraphBuilding(ScribNode parent, ScribNode child, EndpointGraphBuilder graph, ScribNode visited)
	{
		LSend ls = (LSend) visited;
		List<RoleNode> dests = ls.getDestinations();
		if (dests.size() > 1)
		{
			throw new RuntimeException("TODO: " + ls);
		}
		Role peer = dests.get(0).toName();
		MessageId<?> mid = ls.msg.toMessage().getId();
		Payload payload = ls.msg.isMessageSigNode()  // Hacky?
					? ((MessageSigNode) ls.msg).payloads.toPayload()
					: Payload.EMPTY_PAYLOAD;
		graph.builder.addEdge(graph.builder.getEntry(), new Send(peer, mid, payload), graph.builder.getExit());
		return (LSend) super.leaveGraphBuilding(parent, child, graph, ls);
	}

	// Could make a LMessageTransferDel to factor this out with LReceiveDel
	@Override
	public void enterProjectedChoiceSubjectFixing(ScribNode parent, ScribNode child, ProjectedChoiceSubjectFixer fixer)
	{
		fixer.setChoiceSubject(((LSend) child).src.toName());
	}
}