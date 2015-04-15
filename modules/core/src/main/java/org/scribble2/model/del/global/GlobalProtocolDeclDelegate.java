package org.scribble2.model.del.global;

import java.util.Map;
import java.util.Set;

import org.scribble2.model.ModelFactoryImpl;
import org.scribble2.model.ModelNode;
import org.scribble2.model.Module;
import org.scribble2.model.ParameterDeclList;
import org.scribble2.model.RoleDeclList;
import org.scribble2.model.del.ModuleDelegate;
import org.scribble2.model.del.ProtocolDeclDelegate;
import org.scribble2.model.global.GlobalProtocolDecl;
import org.scribble2.model.local.LocalProtocolDecl;
import org.scribble2.model.local.LocalProtocolDefinition;
import org.scribble2.model.local.LocalProtocolHeader;
import org.scribble2.model.name.simple.SimpleProtocolNameNode;
import org.scribble2.model.visit.JobContext;
import org.scribble2.model.visit.Projector;
import org.scribble2.model.visit.env.ProjectionEnv;
import org.scribble2.sesstype.name.ProtocolName;
import org.scribble2.sesstype.name.Role;
import org.scribble2.util.ScribbleException;

public class GlobalProtocolDeclDelegate extends ProtocolDeclDelegate
{
	public GlobalProtocolDeclDelegate()
	{

	}
	
	/*protected GlobalProtocolDeclDelegate(Map<Role, Map<ProtocolName, Set<Role>>> dependencies)
	{
		super(dependencies);
	}

	@Override
	protected GlobalProtocolDeclDelegate reconstruct(Map<Role, Map<ProtocolName, Set<Role>>> dependencies)
	{
		return new GlobalProtocolDeclDelegate(dependencies);
	}

	@Override
	public GlobalProtocolDeclDelegate setDependencies(Map<Role, Map<ProtocolName, Set<Role>>> dependencies)
	{
		return (GlobalProtocolDeclDelegate) super.setDependencies(dependencies);
	}*/

	@Override
	protected GlobalProtocolDeclDelegate copy()
	{
		return new GlobalProtocolDeclDelegate();
	}

	/*@Override
	public Projector enterProjection(ModelNode parent, ModelNode child, Projector proj) throws ScribbleException
	{
		/*JobContext jc = proj.getJobContext();
		Module main = jc.getMainModule();

		ProtocolName gpn = ((GlobalProtocolDecl) child).getFullProtocolName(main);
		Role self = proj.peekSelf();

		//proj.addProtocolDependency(gpn, self, gpn, self);
		proj.addProtocolDependency(gpn, self);* /

		return proj;
	}*/
	
	@Override
	public GlobalProtocolDecl leaveProjection(ModelNode parent, ModelNode child, Projector proj, ModelNode visited) throws ScribbleException
	{
		JobContext jc = proj.getJobContext();
		Module main = jc.getMainModule();
		ProtocolName gpn = ((GlobalProtocolDecl) child).getFullProtocolName(main);
		
		Role self = proj.peekSelf();
		GlobalProtocolDecl gpd = (GlobalProtocolDecl) visited;

		LocalProtocolDecl lpd = project(proj, gpd);
		//Map<ProtocolName, Set<Role>> deps = proj.getProtocolDependencies();
		Map<ProtocolName, Set<Role>> deps = ((GlobalProtocolDeclDelegate) gpd.del()).getProtocolDependencies().get(self);
		//Module projected = projectIntoModule(proj, gpd);
		Module projected = ((ModuleDelegate) main.del()).createModuleForProjection(proj, main, lpd, deps);  // FIXME: projection should always use the main module?
		// store projections in projector? in context? do earlier with context building? (but subprotocol pattern not available there)* /
		//dependencies.put(self, deps);

		proj.addProjection(gpn, self, projected);
		
		//System.out.println("P: " + self + ":\n" + projected + "\n");
		
		//.. add all dependencies to projection set (needs to be done after all modules have been visited) .. FIXME: dependencies need to be the sigs for the argument positions, not just the name -- or maybe ok to not, just be conservative
		//.. maybe just record all projections in one big store for reachability checking, projection set for a specific global protocol can be worked out form dependencies?
		
		//proj.popSelf();

		return gpd;
	}

	/*// Projector uses this to "override" the base SubprotocolVisitor visitChildrenInSubprotocols pattern
	public GlobalProtocolDecl visitOverrideForProjection(Projector proj, Module parent, GlobalProtocolDecl child) throws ScribbleException
	{
			//ModelNode visited = child.visitChildrenInSubprotocols(spv);  visitForProjection
		//ModuleDelegate md = getModuleDelegate();
		/*JobContext jc = proj.getJobContext();
		Module main = jc.getMainModule();

		ProtocolName gpn = child.getFullProtocolName(main);* /
		Map<Role, Map<ProtocolName, Set<Role>>> dependencies = new HashMap<>();
		for (Role self : child.header.roledecls.getRoles())
		{
			//..HERE:   // FIXME: make into a enter/leave or push/pop pattern

			proj.pushSelf(self);
			//proj.clearProtocolDependencies();

			proj = (Projector) proj.enter(parent, child);
			//GlobalProtocolDecl gpd = (GlobalProtocolDecl)
			GlobalProtocolDecl visited = child.visitChildrenInSubprotocols(proj);  // enter/leave around visitChildren for this GlobalProtocolDecl done above -- cf. SubprotocolVisitor.visit
			visited = leave(parent, child, proj, visited);
			// projection will not change original global protocol
			
			dependencies.put(self, proj.getProtocolDependencies());
			
			//System.out.println("P: " + self + ":\n" + projected + "\n");
			
			//.. add all dependencies to projection set (needs to be done after all modules have been visited) .. FIXME: dependencies need to be the sigs for the argument positions, not just the name -- or maybe ok to not, just be conservative
			//.. maybe just record all projections in one big store for reachability checking, projection set for a specific global protocol can be worked out form dependencies?
			
			proj.popSelf();
		}
		//return this;
		//ProtocolDeclContext pdcontext = new ProtocolDeclContext(dependencies);
		GlobalProtocolDeclDelegate del = ((GlobalProtocolDeclDelegate) child.del()).setDependencies(dependencies);  // FIXME: move to leaveProjection in GlobalProtocolDecl
			
		//System.out.println("c: " + this.name + ", " + pdcontext.getProtocolDependencies());

		//return reconstruct(this.name, this.roledecls, this.paramdecls, this.def, pdcontext, getEnv());
		return (GlobalProtocolDecl) child.del(del);  // del setter needs to be done here (access to collected dependencies) -- envLeave uses this new del (including Env setting)
		
		// projected modules added to context delegate in (main) module delegate leaveProjection
	}*/
	
	private LocalProtocolDecl project(Projector proj, GlobalProtocolDecl gpd)
	{
		Role self = proj.peekSelf();
		//Module projected = projectToModule(proj, (LocalProtocolDefinition) ((ProjectionEnv) gpd.def.del().getEnv()).getProjection());
		LocalProtocolDefinition def = (LocalProtocolDefinition) ((ProjectionEnv) gpd.def.del().env()).getProjection();
		//SimpleProtocolNameNode pn = proj.makeProjectedLocalName(gpd.header.name.toName(), self);
		SimpleProtocolNameNode pn = proj.makeProjectedLocalName(gpd.header.name.toCompoundName(), self);
		
		// FIXME: move to delegate? -- maybe fully integrate into projection pass
		RoleDeclList roledecls = gpd.header.roledecls.project(self);
		ParameterDeclList paramdecls = gpd.header.paramdecls.project(self);//peekSelf());
		LocalProtocolHeader lph = ModelFactoryImpl.FACTORY.LocalProtocolHeader(pn, roledecls, paramdecls);
		//LocalProtocolDefinition def = (LocalProtocolDefinition) ((ProjectionEnv) gpd.def.del().getEnv()).getProjection();
		LocalProtocolDecl projected = ModelFactoryImpl.FACTORY.LocalProtocolDecl(lph, def);

		////pdcontext = new ProtocolDeclContext(getContext(), self, proj.getProtocolDependencies());  // FIXME: move dependency building to after initial context building (or integrate into it?)
		//Map<ProtocolName, Set<Role>> deps = proj.getProtocolDependencies();
		//dependencies.put(self, deps);
		return projected;
	}
}
