package org.scribble.net.scribsock;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.ScribMessage;
import org.scribble.net.session.SessionEndpoint;
import org.scribble.sesstype.name.Role;

public abstract class ReceiveSocket<R extends Role> extends LinearSocket<R>
{
	private CompletableFuture<ScribMessage> fut;

	protected ReceiveSocket(SessionEndpoint<R> se)
	{
		super(se);
	}

	protected ScribMessage readScribMessage(Role peer) throws ClassNotFoundException, IOException, ScribbleRuntimeException, InterruptedException, ExecutionException
	{
		//ScribMessage m = this.ep.smf.readMessage(this.ep.getSocketEndpoint(role).dis);
		ScribMessage m = getFuture(peer).get();  // The Future converts the RuntimeScribbleException wrapper for the underlying IOException into an ExecutionException

		//System.out.println("Read: " + m);

		return m;
	}
	
	protected boolean isDone(Role peer)
	{
		//return !this.ep.getInputQueues().isEmpty(peer);
		return (this.fut == null) || (this.fut.isDone());
	}
	
	protected CompletableFuture<ScribMessage> getFuture(Role peer) throws ScribbleRuntimeException
	{
		use();
		//this.fut = this.ep.getInputQueues().getFuture(peer);
		return this.se.getChannelEndpoint(peer).getFuture();
	}
}
