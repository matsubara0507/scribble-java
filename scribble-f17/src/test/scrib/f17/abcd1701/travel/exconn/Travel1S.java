package f17.abcd1701.travel.exconn;

import static f17.abcd1701.travel.exconn.Travel1.Travel.Travel.C;
import static f17.abcd1701.travel.exconn.Travel1.Travel.Travel.S;
import static f17.abcd1701.travel.exconn.Travel1.Travel.Travel.confirm;
import static f17.abcd1701.travel.exconn.Travel1.Travel.Travel.pay;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.scribsock.ScribServerSocket;
import org.scribble.runtime.net.scribsock.SocketChannelServer;
import org.scribble.runtime.net.session.ExplicitEndpoint;

import f17.abcd1701.travel.exconn.Travel1.Travel.Travel;
import f17.abcd1701.travel.exconn.Travel1.Travel.channels.S.Travel_S_1;
import f17.abcd1701.travel.exconn.Travel1.Travel.roles.S;

public class Travel1S
{
	public static void main(String[] args) throws IOException, ScribbleRuntimeException, ExecutionException, InterruptedException
	{
		try (ScribServerSocket ss_C = new SocketChannelServer(9999))
		{
			while (true)
			{
				Travel travel = new Travel();
				try (ExplicitEndpoint<Travel, S> se = new ExplicitEndpoint<>(travel, S, new ObjectStreamFormatter()))
				{
					Buf<String> b = new Buf<>();

					new Travel_S_1(se)
					  .accept(C, ss_C)
					  .receive(C, pay, b)
					  .send(C, confirm, 1234);                 System.out.println("Done: " + b.val);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
