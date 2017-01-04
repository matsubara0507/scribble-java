package demo.fase17.abcd1701.travel.exconn;

import static demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel.C;
import static demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel.S;
import static demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel.confirm;
import static demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel.pay;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.scribsock.ScribServerSocket;
import org.scribble.net.scribsock.SocketChannelServer;
import org.scribble.net.session.ExplicitEndpoint;

import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.Travel;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.channels.S.Travel_S_1;
import demo.fase17.abcd1701.travel.exconn.Travel1.Travel.roles.S;

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
