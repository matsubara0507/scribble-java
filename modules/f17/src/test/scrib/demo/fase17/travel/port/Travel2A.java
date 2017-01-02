package demo.fase17.travel.port;

import static demo.fase17.travel.port.Travel2.Travel.Travel.C;
import static demo.fase17.travel.port.Travel2.Travel.Travel.S;
import static demo.fase17.travel.port.Travel2.Travel.Travel.accpt;
import static demo.fase17.travel.port.Travel2.Travel.Travel.ack;
import static demo.fase17.travel.port.Travel2.Travel.Travel.port;
import static demo.fase17.travel.port.Travel2.Travel.Travel.query;
import static demo.fase17.travel.port.Travel2.Travel.Travel.quote;
import static demo.fase17.travel.port.Travel2.Travel.Travel.reject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.scribsock.ScribServerSocket;
import org.scribble.net.scribsock.SocketChannelServer;
import org.scribble.net.session.ExplicitEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import demo.fase17.travel.port.Travel2.Travel.Travel;
import demo.fase17.travel.port.Travel2.Travel.channels.A.Travel_A_1;
import demo.fase17.travel.port.Travel2.Travel.channels.A.Travel_A_2;
import demo.fase17.travel.port.Travel2.Travel.channels.A.Travel_A_2_Cases;
import demo.fase17.travel.port.Travel2.Travel.roles.A;

public class Travel2A
{
	public static void main(String[] args) throws IOException, ScribbleRuntimeException, ExecutionException, InterruptedException
	{
		try (ScribServerSocket ss_C = new SocketChannelServer(8888))
		{
			while (true)
			{
				int q = 1000;

				Travel booking = new Travel();
				Buf<Integer> b = new Buf<>();
				try (ExplicitEndpoint<Travel, A> se
						= new ExplicitEndpoint<>(booking, Travel.A, new ObjectStreamFormatter()))
				{
					Travel_A_2 s2 = new Travel_A_1(se).accept(C, ss_C);
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
								s2cases.receive(accpt)
								.connect(S, SocketChannelEndpoint::new, "localhost", 9999)
								.receive(S, port, b)
								.send(C, port, b.val)
								.receive(C, ack, b);
								System.out.println("Accepted: " + b.val);
								break X;
						}
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
