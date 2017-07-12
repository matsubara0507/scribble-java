package f17.travel.orig;

import static f17.travel.orig.Travel1.Travel.Travel.A;
import static f17.travel.orig.Travel1.Travel.Travel.C;
import static f17.travel.orig.Travel1.Travel.Travel.S;
import static f17.travel.orig.Travel1.Travel.Travel.accpt;
import static f17.travel.orig.Travel1.Travel.Travel.confirm;
import static f17.travel.orig.Travel1.Travel.Travel.pay;
import static f17.travel.orig.Travel1.Travel.Travel.query;
import static f17.travel.orig.Travel1.Travel.Travel.quote;

import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.session.ExplicitEndpoint;
import org.scribble.runtime.net.session.SocketChannelEndpoint;

import f17.travel.orig.Travel1.Travel.Travel;
import f17.travel.orig.Travel1.Travel.channels.C.Travel_C_1;
import f17.travel.orig.Travel1.Travel.channels.C.Travel_C_2;
import f17.travel.orig.Travel1.Travel.roles.C;

public class Travel1C
{
	private static final String[] queries = new String[] { "aaa", "bbb", "ccc", "ddd" };
	
	public static void main(String[] args) throws Exception
	{
		Travel travel = new Travel();
		try (ExplicitEndpoint<Travel, C> se
				= new ExplicitEndpoint<>(travel, C, new ObjectStreamFormatter()))
		{
			Buf<Integer> b = new Buf<>();
			Travel_C_2 C2 = new Travel_C_1(se).connect(A, SocketChannelEndpoint::new, "localhost", 8888);
			for (int i = 0; i < queries.length; i++)
			{
				C2 = C2.send(A, query, queries[0]) .receive(A, quote, b);
			}
				C2.connect(S, SocketChannelEndpoint::new, "localhost", 9999)
					.send(S, pay, "...")
					.receive(S, confirm, b)
					.send(A, accpt, b.val);

			System.out.println("Done: " + b.val);
		}
	}
}
