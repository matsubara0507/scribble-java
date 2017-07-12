package f17.abcd1701.travel.orig;

import static f17.abcd1701.travel.orig.Travel.Travel.Travel.A;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.C;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.quote;

import java.io.IOException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.MPSTEndpoint;

import f17.abcd1701.travel.orig.Travel.Travel.Travel;
import f17.abcd1701.travel.orig.Travel.Travel.channels.A.EndSocket;
import f17.abcd1701.travel.orig.Travel.Travel.channels.A.Travel_A_1;
import f17.abcd1701.travel.orig.Travel.Travel.channels.A.Travel_A_1_Handler;
import f17.abcd1701.travel.orig.Travel.Travel.channels.A.Travel_A_2;
import f17.abcd1701.travel.orig.Travel.Travel.ops.accpt;
import f17.abcd1701.travel.orig.Travel.Travel.ops.query;
import f17.abcd1701.travel.orig.Travel.Travel.ops.reject;
import f17.abcd1701.travel.orig.Travel.Travel.roles.A;

public class TravelA
{
	private static final int INITIAL = 1000;
	
	public static void main(String[] args) throws Exception
	{
		try (ScribServerSocket ss_C = new SocketChannelServer(8888))
		{
			while (true)
			{
				Travel booking = new Travel();
				try (MPSTEndpoint<Travel, A> se
						= new MPSTEndpoint<>(booking, A, new ObjectStreamFormatter()))
				{
					se.accept(ss_C, C);

					new Travel_A_1(se).branch(C, new TravelAHandler(INITIAL));
				}
			}
		}
	}
}

class TravelAHandler implements Travel_A_1_Handler
{	
	private int q;

	public TravelAHandler(int q)
	{
		this.q = q;
	}

	@Override
	public void receive(Travel_A_2 s2, query op, Buf<String> b) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		s2.send(C, quote, this.q -= 100).branch(C, this);
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
