package org.scribble.ext.annot.visit.context;

import org.scribble.ast.ScribNode;
import org.scribble.ext.annot.ast.AnnotScribDel;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.visit.NoEnvInlinedProtocolVisitor;

public class AnnotSetter extends NoEnvInlinedProtocolVisitor
{
	public AnnotSetter(Job job)
	{
		super(job);
	}
	
	@Override
	public void inlinedEnter(ScribNode parent, ScribNode child) throws ScribbleException
	{
		super.inlinedEnter(parent, child);
		((AnnotScribDel) child.del()).enterAnnotSetting(parent, child, this);
	}
	
	@Override
	public ScribNode inlinedLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		visited = ((AnnotScribDel) visited.del()).leaveAnnotSetting(parent, child, this, visited);
		return super.inlinedLeave(parent, child, visited);
	}
}
