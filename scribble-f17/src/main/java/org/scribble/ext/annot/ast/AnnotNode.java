package org.scribble.ext.annot.ast;

public interface AnnotNode
{
	default boolean isAnnotated()
	{
		return false;
	}

	ScribAnnot getAnnotation();
}
