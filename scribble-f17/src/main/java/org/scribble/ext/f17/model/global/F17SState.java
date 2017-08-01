package org.scribble.ext.f17.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ext.annot.ast.AnnotString;
import org.scribble.ext.annot.sesstype.name.AnnotPayloadType;
import org.scribble.ext.annot.sesstype.name.AnnotType;
import org.scribble.ext.annot.sesstype.name.PayloadVar;
import org.scribble.model.MPrettyState;
import org.scribble.model.MState;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.EStateKind;
import org.scribble.model.endpoint.actions.EAccept;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.endpoint.actions.EConnect;
import org.scribble.model.endpoint.actions.EDisconnect;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.actions.SAction;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.name.Op;
import org.scribble.sesstype.name.PayloadType;
import org.scribble.sesstype.name.Role;

// Not extending SState -- not reusing SConfig, SBuffers, etc
// FSM version of F17Session
// Wait-for errors?
public class F17SState extends MPrettyState<Void, SAction, F17SState, Global>
{
	private static final F17EBot BOT = new F17EBot();
	
	// Cf. SState.config
	private final Map<Role, EState> P;
	private final Map<Role, Map<Role, ESend>> Q;  // null value means connected and empty -- dest -> src -> msg
	
	private final Map<Role, Map<Role, PayloadVar>> ports;  // Server -> Client -> port
	private final Map<Role, Set<PayloadVar>> owned;
	
	private final Set<Role> subjs = new HashSet<>();  // Hacky: mostly because EState has no self

	public F17SState(Map<Role, EState> P, boolean explicit)
	{
		//this(P, makeQ(P.keySet(), explicit ? BOT : null));
		this(P, makeQ(P.keySet(), explicit ? BOT : null),
				makePorts(P.keySet()), makeOwned(P.keySet()));
	}

	//protected F17SState(Map<Role, EState> P, Map<Role, Map<Role, ESend>> Q)
	protected F17SState(Map<Role, EState> P, Map<Role, Map<Role, ESend>> Q,
			Map<Role, Map<Role, PayloadVar>> ports, Map<Role, Set<PayloadVar>> owners)
	{
		super(Collections.emptySet());
		this.P = Collections.unmodifiableMap(P);
		this.Q = Collections.unmodifiableMap(Q);
		
		this.ports = Collections.unmodifiableMap(ports);
		this.owned = Collections.unmodifiableMap(owners);
	}
	
	public void addSubject(Role subj)
	{
		this.subjs.add(subj);
	}
	
	public Set<Role> getSubjects()
	{
		return Collections.unmodifiableSet(this.subjs);
	}

	public Map<Role, EState> getP()
	{
		return this.P;
	}
	
	public Map<Role, Map<Role, ESend>> getQ()
	{
		return this.Q;
	}

	public boolean isConnectionError()
	{
		return this.P.entrySet().stream().anyMatch((e) -> 
			e.getValue().getActions().stream().anyMatch((a) ->
				(a.isConnect() || a.isAccept()) && isConnected(e.getKey(), a.peer)   // FIXMEL isConnected is direction sensitive

						// FIXME: check for pending port, if so then port is used -- need to extend an AnnotEConnect type with ScribAnnot (cf. AnnotPayloadType)

		));
	}

	// Error of opening a port when already connected or another port is still open
	public boolean isPortOpenError()
	{
		for (Entry<Role, EState> e : this.P.entrySet())
		{
			for (EAction a : e.getValue().getActions())
			{
				if (a.isSend())
				{
					for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
							.filter(x -> x instanceof AnnotType)::iterator)
					{
						if (pt instanceof AnnotPayloadType<?>)
						{
							// FIXME: factor out annot parsing
							AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
							String annot = ((AnnotString) apt.annot).val;
							String key = annot.substring(0, annot.indexOf("="));
							String val = annot.substring(annot.indexOf("=")+1,annot.length());
							if (key.equals("open"))
							{
								Role portRole = new Role(val);
								// FIXME: generalise
								if (isConnected(e.getKey(), portRole) || isPendingConnected(e.getKey(), portRole))
								{
									return true;
								}
							}
							else
							{
								throw new RuntimeException("[f17] TODO: " + a);
							}
						}
						else if (pt instanceof PayloadVar)
						{
							
						}
						else
						{
							throw new RuntimeException("[f17] TODO: " + a);
						}
					}
				}
			}
		}
		return false;
	}

	// Error of trying to send a PayloadVar that is not "owned" by the sender
	public boolean isPortOwnershipError()
	{
		for (Entry<Role, EState> e : this.P.entrySet())
		{
			for (EAction a : e.getValue().getActions())
			{
				if (a.isSend())
				{
					for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
							.filter(x -> x instanceof AnnotType)::iterator)
					{
						if (pt instanceof AnnotPayloadType<?>)
						{
							
						}
						else if (pt instanceof PayloadVar)
						{
							if (!this.owned.get(e.getKey()).contains((PayloadVar) pt))
							{
								return true;
							}
						}
						else
						{
							throw new RuntimeException("[f17] TODO: " + a);
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isDisconnectedError()
	{
		return this.P.entrySet().stream().anyMatch((e) -> 
			e.getValue().getActions().stream().anyMatch((a) ->
				a.isDisconnect() && this.Q.get(e.getKey()).get(a.peer) != null
		));
	}

	public boolean isUnconnectedError()
	{
		return this.P.entrySet().stream().anyMatch((e) -> 
			e.getValue().getActions().stream().anyMatch((a) ->
				(a.isSend() || a.isReceive()) && !isConnected(e.getKey(), a.peer)  // FIXME: isConnected is direction sensitive
		));
	}

	public boolean isSynchronisationError()
	{
		return this.P.entrySet().stream().anyMatch((e) -> 
			e.getValue().getActions().stream().anyMatch((a) ->
				{
					EState peer;
					return a.isConnect() && (peer = this.P.get(a.peer)).getStateKind() == EStateKind.ACCEPT
							&& (peer.getActions().iterator().next().peer.equals(e.getKey()))  // E.g. A->>B.B->>C.A->>C
							&& !(peer.getActions().contains(a.toDual(e.getKey())));
				}
		));
	}

	// FIXME: iterate on P, not Q
	public boolean isReceptionError()
	{
		return this.Q.entrySet().stream().anyMatch((e1) ->
				e1.getValue().entrySet().stream().anyMatch((e2) ->
					{
						EState s;
						return hasMessage(e1.getKey(), e2.getKey())
								&& ((s = this.P.get(e1.getKey())).getStateKind() == EStateKind.UNARY_INPUT
										|| s.getStateKind() == EStateKind.POLY_INPUT)
								&& (s.getActions().iterator().next().peer.equals(e2.getKey()))  // E.g., A->B.B->C.A->C
								&& !s.getActions().contains(e2.getValue().toDual(e2.getKey()));
					}
				));
	}

	public boolean isUnfinishedRoleError(Map<Role, EState> E0)
	{
		return this.isTerminal() &&
				this.P.entrySet().stream().anyMatch((e) -> isActive(e.getValue(), E0.get(e.getKey()).id));
	}

	public boolean isOrphanError(Map<Role, EState> E0)
	{
		/*return this.P.entrySet().stream().anyMatch((e) -> isInactive(e.getValue(), E0.get(e.getKey()).id)
				&& (this.P.keySet().stream().anyMatch((r) -> hasMessage(e.getKey(), r))));*/
		return this.P.entrySet().stream().anyMatch((e) -> isInactive(e.getValue(), E0.get(e.getKey()).id)
				&& (this.P.keySet().stream().anyMatch((r) -> hasMessage(e.getKey(), r))
						//|| !this.owned.get(e.getKey()).isEmpty()  
						
								// FIXME: need AnnotEConnect to consume owned properly

				));
	}
	
	public Map<Role, List<EAction>> getFireable()
	{
		Map<Role, List<EAction>> res = new HashMap<>();
		for (Entry<Role, EState> e : this.P.entrySet())
		{
			Role self = e.getKey();
			EState s = e.getValue();
			res.put(self, new LinkedList<>());
			for (EAction a : s.getActions())
			{
				if (a.isSend())
				{
					ESend es = (ESend) a;
					if (isConnected(self, es.peer) && this.Q.get(es.peer).get(self) == null)
					{

						boolean ok = true;
						for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
								.filter((x) -> x instanceof AnnotType)::iterator)
						{
							if (pt instanceof AnnotPayloadType<?>)
							{
								AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
								String annot = ((AnnotString) apt.annot).val;
								String key = annot.substring(0, annot.indexOf("="));
								String val = annot.substring(annot.indexOf("=")+1,annot.length());
								if (key.equals("open"))
								{
									Role portRole = new Role(val);
									if (isConnected(self, portRole) || isPendingConnected(self, portRole))
									{
										ok = false;
										break;
									}
								}
								else
								{
									throw new RuntimeException("[f17] TODO: " + a);
								}
							}
							else if (pt instanceof PayloadVar)  // Check linear ownership of port
							{
								if (!this.owned.get(self).contains(pt))
								{
									ok = false;
									break;
								}
							}
							else
							{
								throw new RuntimeException("[f17] TODO: " + pt);
							}
						}
						if (ok)
						{	
							res.get(self).add(es);
						}

					}
				}
				else if (a.isReceive())
				{
					EReceive er = (EReceive) a;
					ESend m = this.Q.get(self).get(er.peer);
					if (m != null && er.toDual(self).equals(m))  //&& !(m instanceof F17EBot)
					{
						res.get(self).add(er);
					}
				}
				else if (a.isConnect())
				{
					EConnect lo = (EConnect) a;
					if (this.Q.get(self).get(lo.peer) instanceof F17EBot      // FIXME: !isConnected
							&& this.Q.get(lo.peer).get(self) instanceof F17EBot)
					{
						EState plt = this.P.get(lo.peer);
						if (plt.getActions().contains(lo.toDual(self)))
						{

							boolean ok = true;
							for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
									.filter((x) -> x instanceof AnnotType)::iterator)
							{
								if (pt instanceof AnnotPayloadType<?>)
								{
									AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
									String annot = ((AnnotString) apt.annot).val;
									String key = annot.substring(0, annot.indexOf("="));
									String val = annot.substring(annot.indexOf("=")+1,annot.length());
									if (key.equals("port"))
									{
										Role portRole = new Role(val);
										if (isConnected(self, portRole) || isPendingConnected(self, portRole))
										{
											ok = false;
											break;
										}
										if (!val.equals(lo.peer.toString()))
										{
											ok = false;
											break;
										}
									}
									else  // TODO: connect-with-message could also be open-annot
									{
										throw new RuntimeException("[f17] TODO: " + a);
									}
								}
								else if (pt instanceof PayloadVar)  // Check linear ownership of port
								{
									if (!this.owned.get(self).contains(pt) || !pt.equals(this.ports.get(lo.peer).get(self)))
									{
										ok = false;
										break;
									}
								}
								else
								{
									throw new RuntimeException("[f17] TODO: " + a);
								}
							}
							if (ok)
							{	
								res.get(self).add(lo);
							}

						}
					}
				}
				else if (a.isAccept())
				{
					EAccept la = (EAccept) a;
					if (this.Q.get(self).get(la.peer) instanceof F17EBot      // FIXME: !isConnected
							&& this.Q.get(la.peer).get(self) instanceof F17EBot)
					{
						EState plt = this.P.get(la.peer);
						if (plt.getActions().contains(la.toDual(self)))
						{
							res.get(self).add(la);
						}
					}
				}
				else if (a.isDisconnect())
				{
					EDisconnect ld = (EDisconnect) a;
					if (!(this.Q.get(self).get(ld.peer) instanceof F17EBot)  // FIXME: isConnected
							&& this.Q.get(self).get(ld.peer) == null)
					{
						res.get(self).add(ld);
					}
				}
				else
				{
					throw new RuntimeException("[f17] Shouldn't get in here: " + a);
				}
			}
		}
		return res;
	}
	
	public F17SState fire(Role self, EAction a)  // Deterministic
	{
		Map<Role, EState> P = new HashMap<>(this.P);
		Map<Role, Map<Role, ESend>> Q = copyQ(this.Q);
		Map<Role, Map<Role, PayloadVar>> ports = copyPorts(this.ports);
		Map<Role, Set<PayloadVar>> owned = copyOwned(this.owned);
		EState succ = P.get(self).getSuccessor(a);

		if (a.isSend())
		{
			ESend es = (ESend) a;
			P.put(self, succ);
			Q.get(es.peer).put(self, es);
			
			for (PayloadType<?> pt : (Iterable<PayloadType<?>>) a.payload.elems.stream()
					.filter((x) -> x instanceof AnnotType)::iterator)
			{
				if (pt instanceof AnnotPayloadType<?>)
				{
					AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
					String annot = ((AnnotString) apt.annot).val;
					String key = annot.substring(0, annot.indexOf("="));
					String val = annot.substring(annot.indexOf("=")+1,annot.length());
					if (key.equals("open"))
					{
						Role portRole = new Role(val);
						ports.get(self).put(portRole, apt.var);
						owned.get(es.peer).add(apt.var);
					}
					else
					{
						throw new RuntimeException("[f17] TODO: " + a);
					}
				}
				else if (pt instanceof PayloadVar)
				{
					PayloadVar pv = (PayloadVar) pt;
					owned.get(self).remove(pv);
					owned.get(es.peer).add(pv);
				}
				else
				{
					throw new RuntimeException("[f17] TODO: " + a);
				}
			}

		}
		else if (a.isReceive())
		{
			EReceive lr = (EReceive) a;
			P.put(self, succ);
			Q.get(self).put(lr.peer, null);
		}
		else if (a.isDisconnect())
		{
			EDisconnect ld = (EDisconnect) a;
			P.put(self, succ);
			Q.get(self).put(ld.peer, BOT);
		}
		else
		{
			throw new RuntimeException("[f17] Shouldn't get in here: " + a);
		}
		return new F17SState(P, Q, ports, owned);
	}

	// "Synchronous version" of fire
	public F17SState sync(Role r1, EAction a1, Role r2, EAction a2)
	{
		Map<Role, EState> P = new HashMap<>(this.P);
		Map<Role, Map<Role, ESend>> Q = copyQ(this.Q);
		Map<Role, Map<Role, PayloadVar>> ports = copyPorts(this.ports);
		Map<Role, Set<PayloadVar>> owned = copyOwned(this.owned);
		EState succ1 = P.get(r1).getSuccessor(a1);
		EState succ2 = P.get(r2).getSuccessor(a2);

		if ((a1.isConnect() && a2.isAccept())
				|| (a1.isAccept() && a2.isConnect()))
		{
			P.put(r1, succ1);
			P.put(r2, succ2);
			Q.get(r1).put(r2, null);
			Q.get(r2).put(r1, null);

			Role cself = a1.isConnect() ? r1 : r2;
			Role cpeer = a1.isConnect() ? r2 : r1;
			EConnect ec = (EConnect) (a1.isConnect() ? a1 : a2);
			
			//if (...)  // FIXME: check if port pending, if so then correct port used -- need AnnotEConnect type -- cf., isConnectionError
			{
				ports.get(cpeer).put(cself, null); // HACK FIXME: incorrect without proper checks
				//owned.get(cself).clear();  // Hack doesn't work: will clear others' pending ports
			}
			
			for (PayloadType<?> pt : (Iterable<PayloadType<?>>) ec.payload.elems.stream()
					.filter((x) -> x instanceof AnnotType)::iterator)
			{
				if (pt instanceof AnnotPayloadType<?>)
				{
					AnnotPayloadType<?> apt = (AnnotPayloadType<?>) pt;
					String annot = ((AnnotString) apt.annot).val;
					String key = annot.substring(0, annot.indexOf("="));
					String val = annot.substring(annot.indexOf("=")+1,annot.length());
					if (key.equals("open"))  // Duplicated from fire/isSend -- opening+passing a port as part of connect
					{
						Role portRole = new Role(val);
						ports.get(cself).put(portRole, apt.var);
						owned.get(cpeer).add(apt.var);
					}
					else
					{
						throw new RuntimeException("[f17] TODO: " + ec);
					}
				}
				else if (pt instanceof PayloadVar)
				{
					PayloadVar pv = (PayloadVar) pt;
					owned.get(cself).remove(pv);
					owned.get(cpeer).add(pv);
				}
				else
				{
					throw new RuntimeException("[f17] TODO: " + ec);
				}
			}
		}
		else
		{
			throw new RuntimeException("[f17] Shouldn't get in here: " + a1 + ", " + a2);
		}
		return new F17SState(P, Q, ports, owned);
	}
	
	@Override
	protected String getNodeLabel()
	{
		String lab = "(" + this.P + ", " + this.Q + ", " + this.ports + ")";
		//return "label=\"" + this.id + ":" + lab.substring(1, lab.length() - 1) + "\"";
		return "label=\"" + this.id + ":" + lab + "\"";
	}
	
	@Override
	public final int hashCode()
	{
		int hash = 79;
		hash = 31 * hash + this.P.hashCode();
		hash = 31 * hash + this.Q.hashCode();
		hash = 31 * hash + this.ports.hashCode();
		return hash;
	}

	// Not using id, cf. ModelState -- FIXME? use a factory pattern that associates unique states and ids? -- use id for hash, and make a separate "semantic equals"
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof F17SState))
		{
			return false;
		}
		F17SState them = (F17SState) o;
		return them.canEquals(this) && this.P.equals(them.P) && this.Q.equals(them.Q)
				&& this.ports.equals(them.ports);
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof F17SState;
	}
	
	private static Map<Role, Map<Role, ESend>> makeQ(Set<Role> rs, ESend init)
	{
		/*return rs.stream().collect(Collectors.toMap((r) -> r, (r) ->
			rs.stream().filter((x) -> !x.equals(r))
				.collect(Collectors.toMap((x) -> x, (x) -> init))  // Doesn't work? (NPE)
		));*/
		Map<Role, Map<Role, ESend>> res = new HashMap<>();
		for (Role r : rs)
		{
			HashMap<Role, ESend> tmp = new HashMap<>();
			for (Role rr : rs)
			{
				if (!rr.equals(r))
				{
					tmp.put(rr, init);
				}
			}
			res.put(r, tmp);
		}
		return res;
	}
	
	private static Map<Role, Map<Role, ESend>> copyQ(Map<Role, Map<Role, ESend>> Q)
	{
		Map<Role, Map<Role, ESend>> copy = new HashMap<>();
		for (Role r : Q.keySet())
		{
			copy.put(r, new HashMap<>(Q.get(r)));
		}
		return copy;
	}

	// FIXME: factor with makeQ
	private static Map<Role, Map<Role, PayloadVar>> makePorts(Set<Role> rs)
	{
		Map<Role, Map<Role, PayloadVar>> res = new HashMap<>();
		for (Role r : rs)
		{
			HashMap<Role, PayloadVar> tmp = new HashMap<>();
			for (Role rr : rs)
			{
				if (!rr.equals(r))
				{
					tmp.put(rr, null);
				}
			}
			res.put(r, tmp);
		}
		return res;
	}
	
	private static Map<Role, Map<Role, PayloadVar>> copyPorts(Map<Role, Map<Role, PayloadVar>> ports)
	{
		Map<Role, Map<Role, PayloadVar>> copy = new HashMap<>();
		for (Role r : ports.keySet())
		{
			copy.put(r, new HashMap<>(ports.get(r)));
		}
		return copy;
	}

	private static Map<Role, Set<PayloadVar>> makeOwned(Set<Role> rs)
	{
		return rs.stream().collect(Collectors.toMap((r) -> r, (r) -> new HashSet<>()));
	}
	
	private static Map<Role, Set<PayloadVar>> copyOwned(Map<Role, Set<PayloadVar>> owned)
	{
		return owned.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey(), (e) -> new HashSet<>(e.getValue())));
	}
	
	// Direction sensitive (not symmetric)
	private boolean isConnected(Role r1, Role r2)  // N.B. is more like the "input buffer" at r1 for r2 -- not the actual "connection from r1 to r2"
	{
		return !(this.Q.get(r1).get(r2) instanceof F17EBot);
	}

	private boolean isPendingConnected(Role r1, Role r2)
	{
		return (this.ports.get(r1).get(r2) != null) || (this.ports.get(r2).get(r1) != null);
	}
	
	private boolean hasMessage(Role self, Role peer)
	{
		ESend m = this.Q.get(self).get(peer);
		return m != null && !(m instanceof F17EBot);
	}

	@Override
	public void addEdge(SAction a, F17SState s)  // Visibility hack (for F17SModelBuilder.build)
	{
		super.addEdge(a, s);
	}
	
	// isActive(SState, Role) becomes isActive(EState)
	public static boolean isActive(EState s, int init)
	{
		return !isInactive(s, init);
	}
	
	private static boolean isInactive(EState s, int init)
	{
		return s.isTerminal() || (s.id == init && s.getStateKind() == EStateKind.ACCEPT);
	}
}


class F17EBot extends ESend
{
	public F17EBot()
	{
		super(null, Role.EMPTY_ROLE, Op.EMPTY_OPERATOR, Payload.EMPTY_PAYLOAD);  // null ef OK?
	}

	@Override
	public EReceive toDual(Role self)
	{
		throw new RuntimeException("Shouldn't get in here: " + this);
		//return this;
	}
	
	@Override
	public boolean isSend()
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return "#";
	} 

	@Override
	public int hashCode()
	{
		int hash = 2273;
		hash = 31 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof F17EBot))
		{
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public boolean canEqual(Object o)  // FIXME: rename canEquals
	{
		return o instanceof F17EBot;
	}
}
