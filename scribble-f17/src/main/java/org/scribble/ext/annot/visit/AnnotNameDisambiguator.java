package org.scribble.ext.annot.visit;

import java.util.HashSet;
import java.util.Set;

import org.scribble.main.Job;
import org.scribble.sesstype.kind.DataTypeKind;
import org.scribble.sesstype.name.Name;
import org.scribble.visit.wf.NameDisambiguator;

public class AnnotNameDisambiguator extends NameDisambiguator
{
	private Set<String> payvars = new HashSet<>();
	
	public AnnotNameDisambiguator(Job job)
	{
		super(job);
	}

	public void addPayloadVar(Name<DataTypeKind> pv)
	{
		this.payvars.add(pv.toString());
	}
	
	public boolean isBoundPayloadVar(Name<DataTypeKind> name)
	{
		return this.payvars.contains(name.toString());
	}
}
