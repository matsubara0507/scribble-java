package org.scribble.ext.f17.main;

import java.util.Map;

import org.scribble.ast.AstFactory;
import org.scribble.ast.Module;
import org.scribble.ext.annot.visit.AnnotNameDisambiguator;
import org.scribble.main.Job;
import org.scribble.main.ScribbleException;
import org.scribble.model.endpoint.EModelFactory;
import org.scribble.model.global.SModelFactory;
import org.scribble.sesstype.name.ModuleName;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.context.ModuleContextBuilder;
import org.scribble.visit.context.Projector;
import org.scribble.visit.context.ProtocolDeclContextBuilder;
import org.scribble.visit.util.RoleCollector;
import org.scribble.visit.wf.DelegationProtocolRefChecker;
import org.scribble.visit.wf.ExplicitCorrelationChecker;

public class F17Job extends Job
{
	public F17Job(boolean debug, Map<ModuleName, Module> parsed, ModuleName main,
			boolean useOldWF, boolean noLiveness, boolean minEfsm, boolean fair, boolean noLocalChoiceSubjectCheck,
			boolean noAcceptCorrelationCheck, boolean noValidation,
			AstFactory af, EModelFactory ef, SModelFactory sf)
	{
		super(debug, parsed, main, useOldWF, noLiveness, minEfsm, fair, noLocalChoiceSubjectCheck, noAcceptCorrelationCheck, noValidation,
				af, ef, sf);
	}
	
	@Override
	public void runContextBuildingPasses() throws ScribbleException
	{
		runVisitorPassOnAllModules(ModuleContextBuilder.class);  // Always done first (even if other contexts are built later) so that following passes can use ModuleContextVisitor

		//runVisitorPassOnAllModules(NameDisambiguator.class);  // Includes validating names used in subprotocol calls..
		runVisitorPassOnAllModules(AnnotNameDisambiguator.class);

		runVisitorPassOnAllModules(ProtocolDeclContextBuilder.class);   //..which this pass depends on.  This pass basically builds protocol dependency info
		runVisitorPassOnAllModules(DelegationProtocolRefChecker.class);  // Must come after ProtocolDeclContextBuilder
		runVisitorPassOnAllModules(RoleCollector.class);  // Actually, this is the second part of protocoldecl context building
		runVisitorPassOnAllModules(ProtocolDefInliner.class);
	}

	// No projection unfolding pass -- not needed(?) for f17 syntax
	public void runF17ProjectionPasses() throws ScribbleException
	{
		runVisitorPassOnAllModules(Projector.class);
		runProjectionContextBuildingPasses();
		if (!this.noAcceptCorrelationCheck)
		{
			runVisitorPassOnParsedModules(ExplicitCorrelationChecker.class);
		}
	}
}
