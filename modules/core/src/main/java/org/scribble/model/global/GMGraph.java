package org.scribble.model.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.scribble.model.MPrettyPrint;
import org.scribble.model.global.actions.GMAction;

public class GMGraph implements MPrettyPrint
{
	public final GMState init;
	public Map<Integer, GMState> states; // State ID -> GMState

	private Map<Integer, Set<Integer>> reach; // State ID -> reachable states (not
																						// reflexive)
	private Set<Set<Integer>> termSets;

	public GMGraph(Map<Integer, GMState> states, GMState init)
	{
		this.init = init;
		this.states = Collections.unmodifiableMap(states);
		this.reach = getReachabilityMap();
	}

	public Set<Set<Integer>> getTerminalSets()
	{
		if (this.termSets != null)
		{
			return this.termSets;
		}

		Set<Set<Integer>> termSets = new HashSet<>();
		Set<Set<Integer>> checked = new HashSet<>();
		for (Integer i : reach.keySet())
		{
			GMState s = this.states.get(i);
			Set<Integer> rs = this.reach.get(s.id);
			if (!checked.contains(rs) && rs.contains(s.id))
			{
				checked.add(rs);
				if (isTerminalSetMember(s))
				{
					termSets.add(rs);
				}
			}
		}
		this.termSets = Collections.unmodifiableSet(termSets);
		return this.termSets;
	}

	private boolean isTerminalSetMember(GMState s)
	{
		Set<Integer> rs = this.reach.get(s.id);
		Set<Integer> tmp = new HashSet<>(rs);
		tmp.remove(s.id);
		for (Integer r : tmp)
		{
			if (!this.reach.containsKey(r) || !this.reach.get(r).equals(rs))
			{
				return false;
			}
		}
		return true;
	}

	// Pre: reach.get(start).contains(end) // FIXME: will return null if initial
	// state is error
	public List<GMAction> getTrace(GMState start, GMState end)
	{
		SortedMap<Integer, Set<Integer>> candidates = new TreeMap<>();
		Set<Integer> dis0 = new HashSet<Integer>();
		dis0.add(start.id);
		candidates.put(0, dis0);

		Set<Integer> seen = new HashSet<>();
		seen.add(start.id);

		return getTraceAux(new LinkedList<>(), seen, candidates, end);
	}

	// Djikstra's
	private List<GMAction> getTraceAux(List<GMAction> trace, Set<Integer> seen,
			SortedMap<Integer, Set<Integer>> candidates, GMState end)
	{
		Integer dis = candidates.keySet().iterator().next();
		Set<Integer> cs = candidates.get(dis);
		Iterator<Integer> it = cs.iterator();
		Integer currid = it.next();
		it.remove();
		if (cs.isEmpty())
		{
			candidates.remove(dis);
		}

		GMState curr = this.states.get(currid);
		Iterator<GMAction> as = curr.getAllActions().iterator();
		Iterator<GMState> ss = curr.getAllSuccessors().iterator();
		while (as.hasNext())
		{
			GMAction a = as.next();
			GMState s = ss.next();
			if (s.id == end.id)
			{
				trace.add(a);
				return trace;
			}

			if (!seen.contains(s.id) && this.reach.containsKey(s.id)
					&& this.reach.get(s.id).contains(end.id))
			{
				seen.add(s.id);
				Set<Integer> tmp1 = candidates.get(dis + 1);
				if (tmp1 == null)
				{
					tmp1 = new HashSet<>();
					candidates.put(dis + 1, tmp1);
				}
				tmp1.add(s.id);
				List<GMAction> tmp2 = new LinkedList<>(trace);
				tmp2.add(a);
				List<GMAction> res = getTraceAux(tmp2, seen, candidates, end);
				if (res != null)
				{
					return res;
				}
			}
		}
		return null;
	}

	// Not reflexive
	public Map<Integer, Set<Integer>> getReachabilityMap()
	{
		if (this.reach != null)
		{
			return this.reach;
		}

		Map<Integer, Integer> idToIndex = new HashMap<>(); // state ID -> array
																												// index
		Map<Integer, Integer> indexToId = new HashMap<>(); // array index -> state
																												// ID
		int i = 0;
		for (GMState s : this.states.values())
		{
			idToIndex.put(s.id, i);
			indexToId.put(i, s.id);
			i++;
		}
		this.reach = getReachabilityAux(idToIndex, indexToId);

		return this.reach;
	}

	private Map<Integer, Set<Integer>> getReachabilityAux(
			Map<Integer, Integer> idToIndex, Map<Integer, Integer> indexToId)
	{
		int size = idToIndex.keySet().size();
		boolean[][] reach = new boolean[size][size];

		for (Integer s1id : idToIndex.keySet())
		{
			for (GMState s2 : this.states.get(s1id).getAllSuccessors())
			{
				reach[idToIndex.get(s1id)][idToIndex.get(s2.id)] = true;
			}
		}

		for (boolean again = true; again;)
		{
			again = false;
			for (int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
				{
					if (reach[i][j])
					{
						for (int k = 0; k < size; k++)
						{
							if (reach[j][k] && !reach[i][k])
							{
								reach[i][k] = true;
								again = true;
							}
						}
					}
				}
			}
		}

		Map<Integer, Set<Integer>> res = new HashMap<>();
		for (int i = 0; i < size; i++)
		{
			Set<Integer> tmp = res.get(indexToId.get(i));
			for (int j = 0; j < size; j++)
			{
				if (reach[i][j])
				{
					if (tmp == null)
					{
						tmp = new HashSet<>();
						res.put(indexToId.get(i), tmp);
					}
					tmp.add(indexToId.get(j));
				}
			}
		}

		return Collections.unmodifiableMap(res);
	}

	@Override
	public String toDot()
	{
		return this.init.toDot();
	}

	@Override
	public String toAut()
	{
		return this.init.toAut();
	}

	@Override
	public String toString()
	{
		return this.init.toString();
	}
}