package f17.travel.port;

import static f17.travel.port.Travel2.Travel.Travel.A;
import static f17.travel.port.Travel2.Travel.Travel.C;
import static f17.travel.port.Travel2.Travel.Travel.S;
import static f17.travel.port.Travel2.Travel.Travel.ack;
import static f17.travel.port.Travel2.Travel.Travel.port;
import static f17.travel.port.Travel2.Travel.Travel.quote;

import java.io.IOException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.ExplicitEndpoint;
import org.scribble.runtime.net.session.SocketChannelEndpoint;

import f17.travel.port.Travel2.Travel.Travel;
import f17.travel.port.Travel2.Travel.channels.A.EndSocket;
import f17.travel.port.Travel2.Travel.channels.A.Travel_A_1;
import f17.travel.port.Travel2.Travel.channels.A.Travel_A_2_Handler;
import f17.travel.port.Travel2.Travel.channels.A.Travel_A_3;
import f17.travel.port.Travel2.Travel.channels.A.Travel_A_4;
import f17.travel.port.Travel2.Travel.ops.accpt;
import f17.travel.port.Travel2.Travel.ops.query;
import f17.travel.port.Travel2.Travel.ops.reject;
import f17.travel.port.Travel2.Travel.roles.A;

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
					//run(new Travel_A_1(se).accept(C, ss_C), INITIAL);
					new Travel_A_1(se).accept(C, ss_C).branch(C, new Travel2AHandler(INITIAL));
				}
			}
		}
	}
	
	/*private static void run(Travel_A_2 s2, int q) throws Exception
	{
		Buf<Integer> b = new Buf<>();
		Travel_A_2_Cases s2cases = s2.branch(Travel.C);
		switch (s2cases.op)
		{
			case query:
				run(s2cases.receive(query).send(C, quote, q -= 100), q);
				break;
			case reject:
				s2cases.receive(reject);
				System.out.println("Rejected: ");
			case accpt:
				s2cases.receive(accpt)
				.connect(S, SocketChannelEndpoint::new, "localhost", 9999)
				.receive(S, port, b)
				.send(C, port, b.val)
				.receive(C, ack, b);
				System.out.println("Accepted: " + b.val);
		}
	}*/
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
			.receive(C, ack, b);
		System.out.println("Accepted: " + b.val);
	}

	@Override
	public void receive(EndSocket schan, reject op) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		System.out.println("Rejected: ");
	}
}
