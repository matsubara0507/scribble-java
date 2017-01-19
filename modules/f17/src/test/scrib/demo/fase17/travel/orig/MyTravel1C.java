package demo.fase17.travel.orig;

import static demo.fase17.travel.orig.Travel1.Travel.Travel.A;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.C;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.S;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.accpt;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.confirm;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.pay;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.query;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.quote;
import static demo.fase17.travel.orig.Travel1.Travel.Travel.reject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.session.ExplicitEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import demo.fase17.travel.orig.Travel1.Travel.Travel;
import demo.fase17.travel.orig.Travel1.Travel.channels.C.Travel_C_1;
import demo.fase17.travel.orig.Travel1.Travel.channels.C.Travel_C_2;
import demo.fase17.travel.orig.Travel1.Travel.roles.C;

public class MyTravel1C
{
	private static final int MAX = 500;
	private static final List<String> QUERIES = IntStream.range(97, 122).mapToObj((i) -> new Character((char) i).toString()).collect(Collectors.toList());
	
	public static void main(String[] args) throws Exception
	{
		Travel travel = new Travel();
		try (ExplicitEndpoint<Travel, C> se
				= new ExplicitEndpoint<>(travel, C, new ObjectStreamFormatter()))
		{
			run(new Travel_C_1(se).connect(A, SocketChannelEndpoint::new, "localhost", 8888),
					QUERIES);
		}
	}

	private static void run(Travel_C_2 s2, List<String> qs) throws Exception
	{
		if (qs.isEmpty())
		{
			System.out.println("Rejecting: ");
			s2.send(A, reject);
		}	
		else
		{
			System.out.println("Querying: " + qs.get(0));
			Buf<Integer> b = new Buf<>();
			s2 = s2.send(A, query, qs.get(0))
			       .receive(A, quote, b);
			System.out.println("Quoted: " + b.val);
			if (b.val > MAX)
			{
				run(s2, qs.subList(1, qs.size()));
			}
			else
			{
				System.out.println("Accepting: ");
				s2.connect(S, SocketChannelEndpoint::new, "localhost", 9999)
					.send(S, pay, "...")
					.receive(S, confirm, b)
					.send(A, accpt, b.val);
				System.out.println("Done: " + b.val);
			}
		}
	}	

	/*private static void run(Travel_C_2 s2) throws Exception
	{
		Buf<Integer> b = new Buf<>();
		for (int i = 0; ; i++)
		{
			if (i >= QUERIES.size())
			{
				System.out.println("Rejecting: ");
				s2.send(A, reject);
				break;
			}
			System.out.println("Querying: " + QUERIES.get(i));
			s2 = s2.send(A, query, QUERIES.get(i))
						 .receive(A, quote, b);
			System.out.println("Quoted: " + b.val);
			if (b.val <= MAX)
			{
				System.out.println("Accepting: ");
				s2.connect(S, SocketChannelEndpoint::new, "localhost", 9999)
					.send(S, pay, "...")
					.receive(S, confirm, b)
					.send(A, accpt, b.val);
				break;
			}
		}
		System.out.println("Done: " + b.val);
	}*/
}
