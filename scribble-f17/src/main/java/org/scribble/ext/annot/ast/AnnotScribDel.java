package org.scribble.ext.annot.ast;

import org.scribble.ast.ScribNode;
import org.scribble.del.ScribDel;
import org.scribble.ext.annot.visit.context.AnnotSetter;
import org.scribble.main.ScribbleException;

public interface AnnotScribDel extends ScribDel
{
	default void enterAnnotSetting(ScribNode parent, ScribNode child, AnnotSetter rem)
	{
		
	}

	default ScribNode leaveAnnotSetting(ScribNode parent, ScribNode child, AnnotSetter rem, ScribNode visited) throws ScribbleException
	{
		return visited;
	}
}
