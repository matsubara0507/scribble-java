package org.scribble.model.global;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scribble.model.MState;
import org.scribble.model.PrettyMState;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.model.endpoint.actions.ESend;
import org.scribble.model.global.actions.GMAction;
import org.scribble.sesstype.kind.Global;
import org.scribble.sesstype.name.Role;

// FIXME? make a WFModel front-end class? (cf. EGraph)
// Only uses MState.id cosmetically, cf. MState equals/hash -- overrides equals/hash based on this.config (maybe extending MState is a bit misleading)
public class GMState extends PrettyMState<Void, GMAction, GMState, Global>
{
	public final GMConfig config;
	
	public GMState(GMConfig config)
	{
		super(Collections.emptySet());
		this.config = config;
	}
	
	// Based on config semantics, not "static" graph edges (cf., super.getAllActions) -- used to build global model graph
	public Map<Role, List<EAction>> getTakeable()
	{
		return this.config.getTakeable();
	}
	
	public List<GMConfig> take(Role r, EAction a)
	{
		return this.config.take(r, a);
	}

	// "Synchronous version" of take
	public List<GMConfig> sync(Role r1, EAction a1, Role r2, EAction a2)
	{
		return this.config.sync(r1, a1, r2, a2);
	}
	
	public GMStateErrors getErrors()
	{
		Map<Role, EReceive> stuck = this.config.getStuckMessages();
		Set<Set<Role>> waitfor = this.config.getWaitForErrors();
		//Set<Set<Role>> waitfor = Collections.emptySet();
		Map<Role, Set<ESend>> orphs = this.config.getOrphanMessages();
		Map<Role, EState> unfinished = this.config.getUnfinishedRoles();
		return new GMStateErrors(stuck, waitfor, orphs, unfinished);
	}
	
	// FIXME? doesn't use super.hashCode (cf., equals)
	@Override
	public final int hashCode()
	{
		int hash = 79;
		//int hash = super.hashCode();
		hash = 31 * hash + this.config.hashCode();
		return hash;
	}

	// FIXME? doesn't use this.id, cf. super.equals
	// Not using id, cf. ModelState -- FIXME? use a factory pattern that associates unique states and ids? -- use id for hash, and make a separate "semantic equals"
	// Care is needed if hashing, since mutable (OK to use immutable config -- cf., ModelState.id)
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof GMState))
		{
			return false;
		}
		return ((GMState) o).canEquals(this) && this.config.equals(((GMState) o).config);
	}

	@Override
	protected boolean canEquals(MState<?, ?, ?, ?> s)
	{
		return s instanceof GMState;
	}
	
	@Override
	public String toString()
	{
		return this.id + ":" + this.config.toString();
	}
}