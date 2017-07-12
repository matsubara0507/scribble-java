package f17.abcd1701.travel.port;

import static f17.abcd1701.travel.port.Travel2.Travel.Travel.A;
import static f17.abcd1701.travel.port.Travel2.Travel.Travel.C;
import static f17.abcd1701.travel.port.Travel2.Travel.Travel.S;
import static f17.abcd1701.travel.port.Travel2.Travel.Travel.confirm;
import static f17.abcd1701.travel.port.Travel2.Travel.Travel.pay;
import static f17.abcd1701.travel.port.Travel2.Travel.Travel.port;

import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.ExplicitEndpoint;

import f17.abcd1701.travel.port.Travel2.Travel.Travel;
import f17.abcd1701.travel.port.Travel2.Travel.channels.S.Travel_S_1;
import f17.abcd1701.travel.port.Travel2.Travel.channels.S.Travel_S_2;
import f17.abcd1701.travel.port.Travel2.Travel.roles.S;

public class Travel2S
{
	public static void main(String[] args) throws Exception
	{
		try (ScribServerSocket ss_A = new SocketChannelServer(9999))
		{
			while (true)
			{
				Travel travel = new Travel();
				try (ExplicitEndpoint<Travel, S> se = new ExplicitEndpoint<>(travel, S, new ObjectStreamFormatter()))
				{
					Buf<String> b = new Buf<>();
					Travel_S_2 s2 = new Travel_S_1(se).accept(A, ss_A);
					int fresh = 7777;
					try (ScribServerSocket ss_C = new SocketChannelServer(fresh))
					{                                  System.out.println("Opened: " + fresh);
						s2.send(A, port, fresh)
							.accept(C, ss_C)
							.receive(C, pay, b)
							.send(C, confirm, 1234);       System.out.println("Done: " + b.val);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
