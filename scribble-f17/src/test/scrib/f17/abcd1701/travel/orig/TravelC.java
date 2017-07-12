package f17.abcd1701.travel.orig;

import static f17.abcd1701.travel.orig.Travel.Travel.Travel.A;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.C;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.S;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel._1;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.accpt;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.confirm;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.pay;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.query;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.quote;
import static f17.abcd1701.travel.orig.Travel.Travel.Travel.reject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.ObjectStreamFormatter;
import org.scribble.runtime.net.session.MPSTEndpoint;
import org.scribble.runtime.net.session.SocketChannelEndpoint;

import f17.abcd1701.travel.orig.Travel.Travel.Travel;
import f17.abcd1701.travel.orig.Travel.Travel.channels.C.Travel_C_1;
import f17.abcd1701.travel.orig.Travel.Travel.roles.C;

public class TravelC
{
	private static final int MAX = 500;
	private static final List<String> QUERIES
			= IntStream.range(97, 122).mapToObj((i) -> new Character((char) i).toString()).collect(Collectors.toList());
	
	public static void main(String[] args) throws Exception
	{
		Travel travel = new Travel();
		try (MPSTEndpoint<Travel, C> se
				= new MPSTEndpoint<>(travel, C, new ObjectStreamFormatter()))
		{
			se.connect(A, SocketChannelEndpoint::new, "localhost", 8888);
			se.connect(S, SocketChannelEndpoint::new, "localhost", 9999);

			run(new Travel_C_1(se), QUERIES);
		}
	}

	private static void run(Travel_C_1 s1, List<String> qs) throws Exception
	{
		if (qs.isEmpty())
		{                                       System.out.println("Rejecting: ");
			s1.send(A, reject);
		}	
		else
		{                                       System.out.println("Querying: " + qs.get(0));
			Buf<Integer> b = new Buf<>();
			s1 = s1.send(A, query, qs.get(0))
			       .receive(A, quote, b)
			       .send(S, _1);                  System.out.println("Quoted: " + b.val);

			if (b.val > MAX)
			{
				run(s1, qs.subList(1, qs.size()));
			}
			else
			{                                     System.out.println("Accepting: ");
				s1.send(S, pay, "...")
					.receive(S, confirm, b)
					.send(A, accpt, b.val);           System.out.println("Done: " + b.val);
			}
		}
	}	
}
