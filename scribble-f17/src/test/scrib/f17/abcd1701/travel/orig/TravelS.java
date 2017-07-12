package f17.abcd1701.travel.orig;

import static f17.abcd1701.travel.orig.Travel.Travel.Travel.C;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.S;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.confirm;

import java.io.IOException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.MPSTEndpoint;

import f17.abcd1701.travel.orig.Travel.Travel.Travel;
import f17.abcd1701.travel.orig.Travel.Travel.channels.S.EndSocket;
import f17.abcd1701.travel.orig.Travel.Travel.channels.S.Travel_S_1;
import f17.abcd1701.travel.orig.Travel.Travel.channels.S.Travel_S_1_Handler;
import f17.abcd1701.travel.orig.Travel.Travel.channels.S.Travel_S_2;
import f17.abcd1701.travel.orig.Travel.Travel.ops._1;
import f17.abcd1701.travel.orig.Travel.Travel.ops.pay;
import f17.abcd1701.travel.orig.Travel.Travel.ops.reject;
import f17.abcd1701.travel.orig.Travel.Travel.roles.S;

public class TravelS
{
	public static void main(String[] args) throws Exception
	{
		try (ScribServerSocket ss_C = new SocketChannelServer(9999))
		{
			while (true)
			{
				Travel travel = new Travel();
				try (MPSTEndpoint<Travel, S> se = new MPSTEndpoint<>(travel, S, new ObjectStreamFormatter()))
				{
					se.accept(ss_C, C);

					new Travel_S_1(se).branch(C, new TravelS1Handler());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
	
class TravelS1Handler implements Travel_S_1_Handler
{
	@Override
	public void receive(Travel_S_1 s1, _1 op) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		s1.branch(C, this);
	}

	@Override
	public void receive(Travel_S_2 s2, pay op, Buf<String> b) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		s2.send(C, confirm, 1234);           System.out.println("Done: " + b.val);
	}

	@Override
	public void receive(EndSocket end, reject op) throws ScribbleRuntimeException, IOException, ClassNotFoundException
	{
		System.out.println("Done: ");
	}
}
