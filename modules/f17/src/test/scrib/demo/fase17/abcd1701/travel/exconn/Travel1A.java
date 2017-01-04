package demo.fase17.abcd1701.travel.exconn;

import static demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel.A;
import static demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel.C;
import static demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel.quote;

import java.io.IOException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.scribsock.ScribServerSocket;
import org.scribble.net.scribsock.SocketChannelServer;
import org.scribble.net.session.ExplicitEndpoint;

import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.channels.A.EndSocket;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.channels.A.Travel_A_1;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.channels.A.Travel_A_2_Handler;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.channels.A.Travel_A_3;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.ops.accpt;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.ops.query;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.ops.reject;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.roles.A;

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
				try (ExplicitEndpoint<Travel, A> se = new ExplicitEndpoint<>(booking, A, new ObjectStreamFormatter()))
				{
					new Travel_A_1(se)
					  .accept(C, ss_C)
					  .branch(C, new Travel1AHandler(INITIAL));
				}
			}
		}
	}
}

class Travel1AHandler implements Travel_A_2_Handler
{	
	private int q;

	public Travel1AHandler(int q)
	{
		this.q = q;
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

	@Override
	public void receive(EndSocket schan, reject op) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		System.out.println("Rejected: ");
	}
}
