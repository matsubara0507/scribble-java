package org.scribble.codegen.statetype.go;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.scribble.codegen.statetype.STStateChanAPIBuilder;
import org.scribble.codegen.statetype.STSendActionBuilder;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;

public class GSTSendActionBuilder extends STSendActionBuilder
{

	@Override
	public String getSTActionName(STStateChanAPIBuilder api, EAction a)
	{
		return "Send_" + a.peer + "_" + a.mid;
	}

	@Override
	public String buildArgs(EAction a)
	{
		return IntStream.range(0, a.payload.elems.size()) 
					.mapToObj(i -> "arg" + i + " " + a.payload.elems.get(i)).collect(Collectors.joining(", "));
	}

	@Override
	public String buildBody(STStateChanAPIBuilder api, EState curr, EAction a, EState succ)
	{
		return 
				  "role" + api.role + "." + a.peer + "<- " + a.mid + "\n"  // FIXME: factor out with branch state builder (and use lower+_)
				+ IntStream.range(0, a.payload.elems.size())
				           .mapToObj(i -> "role" + api.role + "." + a.peer + "<- arg" + i).collect(Collectors.joining("\n")) + "\n"
				+ "return " + buildReturn(null, api, succ) + "{}";
	}
}
