package f17.paper.travel.orig;

import static f17.paper.travel.orig.Travel1.Travel.Travel.A;
import static f17.paper.travel.orig.Travel1.Travel.Travel.C;
import static f17.paper.travel.orig.Travel1.Travel.Travel.quote;

import java.io.IOException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.ExplicitEndpoint;

import f17.paper.travel.orig.Travel1.Travel.Travel;
import f17.paper.travel.orig.Travel1.Travel.channels.A.EndSocket;
import f17.paper.travel.orig.Travel1.Travel.channels.A.Travel_A_1;
import f17.paper.travel.orig.Travel1.Travel.channels.A.Travel_A_2_Handler;
import f17.paper.travel.orig.Travel1.Travel.channels.A.Travel_A_3;
import f17.paper.travel.orig.Travel1.Travel.ops.accpt;
import f17.paper.travel.orig.Travel1.Travel.ops.query;
import f17.paper.travel.orig.Travel1.Travel.ops.reject;
import f17.paper.travel.orig.Travel1.Travel.roles.A;

public class Travel1A
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
					//run(new Travel_A_1(se).accept(C, ss_b.C));
					new Travel_A_1(se).accept(C, ss_C).branch(C, new Travel1AHandler(INITIAL));
				}
			}
		}
	}

	/*private static void run(Travel_A_2 s2) throws Exception
	{
		int q = INITIAL;
		Buf<Integer> b = new Buf<>();
		Travel_A_2_Cases s2cases;
		X: while (true)
		{
			s2cases = s2.branch(Travel.C);
			switch (s2cases.op)
			{
				case query:
					s2 = s2cases.receive(query)
								 .send(C, quote, q -= 100);
					break;
				case reject:
					s2cases.receive(reject);
					System.out.println("Rejected: ");
					break X;
				case accpt:
					s2cases.receive(accpt, b);
					System.out.println("Accepted: " + b.val);
					break X;
			}
		}
	}*/
}

class Travel1AHandler implements Travel_A_2_Handler
{	
	private int q;

	public Travel1AHandler(int q)
	{
		this.q = q;
	}

	@Override
	public void receive(EndSocket schan, reject op) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		System.out.println("Rejected: ");
	}

	@Override
	public void receive(Travel_A_3 s3, query op, Buf<String> b) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		s3.send(C, quote, this.q -= 100).branch(C, this);
	}

	@Override
	public void receive(EndSocket end, accpt op, Buf<Integer> b) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		System.out.println("Accepted: " + b.val);
	}
}
