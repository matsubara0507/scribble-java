package org.scribble.ext.f17.main;

import org.antlr.runtime.tree.CommonTree;

public class F17SyntaxException extends F17Exception
{
	private static final long serialVersionUID = 1L;

	public F17SyntaxException()
	{
		// TODO Auto-generated constructor stub
	}

	public F17SyntaxException(CommonTree blame, String arg0)
	{
		super(blame, arg0);
		// TODO Auto-generated constructor stub
	}

	public F17SyntaxException(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}
}
