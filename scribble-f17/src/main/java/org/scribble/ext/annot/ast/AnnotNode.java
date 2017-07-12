package org.scribble.ext.annot.ast;

// A node that is a "regular" node with an "annotation"
// i.e., an annotated interaction node (i.e., @"..."), or an annotated payload elem (e.g.., x: T) -- the former node may contain some of the latter
// Note: PayloadVarNode (e.g., x) is *not* an AnnotNode
public interface AnnotNode
{
	default boolean isAnnotated()
	{
		return false;
	}

	ScribAnnot getAnnotation();
}
