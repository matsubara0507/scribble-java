package abcd1701.travel.port;

import static abcd1701.travel.port.Travel2.Travel.Travel.A;
import static abcd1701.travel.port.Travel2.Travel.Travel.C;
import static abcd1701.travel.port.Travel2.Travel.Travel.S;
import static abcd1701.travel.port.Travel2.Travel.Travel.ack;
import static abcd1701.travel.port.Travel2.Travel.Travel.port;
import static abcd1701.travel.port.Travel2.Travel.Travel.quote;

import java.io.IOException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.ExplicitEndpoint;
import org.scribble.runtime.net.session.SocketChannelEndpoint;

import abcd1701.travel.port.Travel2.Travel.Travel;
import abcd1701.travel.port.Travel2.Travel.channels.A.EndSocket;
import abcd1701.travel.port.Travel2.Travel.channels.A.Travel_A_1;
import abcd1701.travel.port.Travel2.Travel.channels.A.Travel_A_2_Handler;
import abcd1701.travel.port.Travel2.Travel.channels.A.Travel_A_3;
import abcd1701.travel.port.Travel2.Travel.channels.A.Travel_A_4;
import abcd1701.travel.port.Travel2.Travel.ops.accpt;
import abcd1701.travel.port.Travel2.Travel.ops.query;
import abcd1701.travel.port.Travel2.Travel.ops.reject;
import abcd1701.travel.port.Travel2.Travel.roles.A;

public class Travel2A
{
	private static final int INITIAL = 1000;

	public static void main(String[] args) throws Exception
	{
		try (ScribServerSocket ss_C = new SocketChannelServer(8888))
		{
			while (true)
			{
				Travel booking = new Travel();
				try (ExplicitEndpoint<Travel, A> se
						= new ExplicitEndpoint<>(booking, A, new ObjectStreamFormatter()))
				{
					new Travel_A_1(se)
						.accept(C, ss_C)
						.branch(C, new Travel2AHandler(INITIAL));
				}
			}
		}
	}
}

class Travel2AHandler implements Travel_A_2_Handler
{	
	private int q;

	public Travel2AHandler(int q)
	{
		this.q = q;
	}

	@Override
	public void receive(Travel_A_3 s3, query op, Buf<String> b) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		s3.send(C, quote, this.q -= 100).branch(C, this);
	}

	@Override
	public void receive(Travel_A_4 s4, accpt op) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		Buf<Integer> b = new Buf<>();
		s4.connect(S, SocketChannelEndpoint::new, "localhost", 9999)
			.receive(S, port, b)
			.send(C, port, b.val)
			.receive(C, ack, b);                 System.out.println("Accepted: " + b.val);
	}

	@Override
	public void receive(EndSocket schan, reject op) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		System.out.println("Rejected: ");
	}
}
