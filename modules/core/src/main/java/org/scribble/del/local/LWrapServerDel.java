package org.scribble.del.local;

import org.scribble.ast.ScribNode;
import org.scribble.ast.local.LWrapServer;
import org.scribble.del.ConnectionActionDel;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.actions.LMWrapServer;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.context.EndpointGraphBuilder;
import org.scribble.visit.context.ProjectedChoiceSubjectFixer;
import org.scribble.visit.context.UnguardedChoiceDoProjectionChecker;
import org.scribble.visit.env.UnguardedChoiceDoEnv;

public class LWrapServerDel extends ConnectionActionDel implements LSimpleInteractionNodeDel
{
	@Override
	public LWrapServer leaveEndpointGraphBuilding(ScribNode parent, ScribNode child, EndpointGraphBuilder builder, ScribNode visited) throws ScribbleException
	{
		LWrapServer la = (LWrapServer) visited;
		Role peer = la.src.toName();
		builder.builder.addEdge(builder.builder.getEntry(), new LMWrapServer(peer), builder.builder.getExit());
		return (LWrapServer) super.leaveEndpointGraphBuilding(parent, child, builder, la);
	}

	@Override
	public void enterProjectedChoiceSubjectFixing(ScribNode parent, ScribNode child, ProjectedChoiceSubjectFixer fixer)
	{
		fixer.setChoiceSubject(((LWrapServer) child).src.toName());
	}

	@Override
	public void enterUnguardedChoiceDoProjectionCheck(ScribNode parent, ScribNode child, UnguardedChoiceDoProjectionChecker checker) throws ScribbleException
	{
		super.enterUnguardedChoiceDoProjectionCheck(parent, child, checker);
		LWrapServer la = (LWrapServer) child;
		UnguardedChoiceDoEnv env = checker.popEnv();
		env = env.setChoiceSubject(la.src.toName());
		checker.pushEnv(env);
	}
}
