package wyvern.tools.typedAST.core.declarations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.compiler.MetadataInnerBinding;
import wyvern.tools.typedAST.core.binding.typechecking.LateNameBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.AbstractTreeWritable;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;

/** Represents a declaration of a structural type.
 * 
 * A thin wrapper for a TypeDeclaration (but note that I think TypeDeclaration is *only*
 * used as part of this class)
 * 
 * @author aldrich
 *
 */
public class TypeVarDecl extends Declaration {
	private final String name;
	private final EnvironmentExtender body;
	private final FileLocation fileLocation;
	private final Reference<Optional<TypedAST>> metadata;
	private final Reference<Value> metadataObj;
	private TaggedInfo taggedInfo = null;
	private boolean resourceFlag = false;
    private final String defaultSelfName = "this";
    private String activeSelfName;
    private IExpr metadataExp = null;

	public TypeVarDecl(String name, DeclSequence body, TaggedInfo taggedInfo, TypedAST metadata, FileLocation fileLocation, boolean isResource, String selfName ) {
		this.metadata = new Reference<Optional<TypedAST>>(Optional.ofNullable(metadata));
		this.name = name;
		this.metadataObj = new Reference<>(new Obj(EvaluationEnvironment.EMPTY, null));
		this.body = new TypeDeclaration(name, body, this.metadataObj, taggedInfo, fileLocation);
		this.fileLocation = fileLocation;
		this.taggedInfo = taggedInfo;
		this.resourceFlag = isResource;
        this.activeSelfName = selfName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String,TypedAST> out = new HashMap<>();
		out.put("body", body);
		if (metadata.get().isPresent())
			out.put("metadata", metadata.get().get());
		return out;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
	    throw new RuntimeException("deprecated I think");
		/*metadata.set(Optional.ofNullable(newChildren.get("metadata")));
		return new TypeVarDecl(name, (EnvironmentExtender)newChildren.get("body"), metadata, metadataObj, fileLocation);*/
	}

    @Override
	public FileLocation getLocation() {
		return fileLocation;
	}

	/*@Override
	public Expression generateIL(GenContext ctx) {
		return body.generateIL(ctx);
	}*/

    private String getSelfName() {
        String s = defaultSelfName;
        if (this.activeSelfName != null && this.activeSelfName.length() != 0) {
            s = this.activeSelfName;
        }
        return s;
    }
    
    private StructuralType computeInternalILType(GenContext ctx) {
    	TypeDeclaration td = (TypeDeclaration) this.body;
		GenContext localCtx = ctx.extend(getSelfName(), new Variable(getSelfName()), null);
		return new StructuralType(getSelfName(), td.genDeclTypeSeq(localCtx), this.resourceFlag);
	}
	
	@Override
	public DeclType genILType(GenContext ctx) {
		StructuralType type = computeInternalILType(ctx);
		return new ConcreteTypeMember(getName(), type, getMetadata(ctx));
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		return computeInternalDecl(thisContext);
	}
	
	private IExpr getMetadata(GenContext ctx) {
		if (metadata.get().isPresent()) {
			if (metadataExp == null)
				metadataExp = ((ExpressionAST)metadata.get().get()).generateIL(ctx, null, null);
			return metadataExp;
		} else {
			return null;
		}
	}

	private wyvern.target.corewyvernIL.decl.Declaration computeInternalDecl(GenContext ctx) {
		StructuralType type = computeInternalILType(ctx);
		return new wyvern.target.corewyvernIL.decl.TypeDeclaration(getName(), type, getMetadata(ctx), getLocation());
	}
	
	public boolean isResource() {
		return this.resourceFlag;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		return computeInternalDecl(ctx);
	}
	
	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		wyvern.target.corewyvernIL.decl.Declaration decl = computeInternalDecl(tlc.getContext());
		DeclType dt = genILType(tlc.getContext());
		tlc.addModuleDecl(decl,dt);
	}
}
