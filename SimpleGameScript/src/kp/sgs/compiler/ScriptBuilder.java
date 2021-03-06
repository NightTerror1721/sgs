/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kp.sgs.SGSGlobals;
import kp.sgs.SGSScript;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.opcode.OpcodeList.OpcodeLocation;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Literal;
import kp.sgs.data.SGSImmutableValue;
import kp.sgs.lib.SGSLibrary;
import kp.sgs.lib.SGSLibraryElement;
import kp.sgs.lib.SGSLibraryRepository;

/**
 *
 * @author Asus
 */
public final class ScriptBuilder
{
    private final HashMap<SGSImmutableValue, Constant> literals = new HashMap<>();
    private final HashMap<String, LibraryElement> usedLibElements = new HashMap<>();
    private final HashMap<String, Integer> identifiers = new HashMap<>();
    private final LinkedList<Constant> constants = new LinkedList<>();
    private final LinkedList<Function> functions = new LinkedList<>();
    private final LinkedList<LibraryElement> libelements = new LinkedList<>();
    private final NamespaceScope rootNamespace;
    private final String mainFunctionName;
    private final SGSLibraryRepository allLibs;
    private final SGSLibraryRepository libs;
    private final HashSet<File> included = new HashSet<>();
    private final List<File> dirs;
    
    public ScriptBuilder(CompilerProperties props)
    {
        this.rootNamespace = new NamespaceScope(null, -1, NamespaceScopeType.ROOT);
        //this.stack = new RuntimeStack();
        this.mainFunctionName = props.getFunctionName();
        this.allLibs = props.getLibraryRepository();
        this.libs = new SGSLibraryRepository();
        this.dirs = props.getDirectories();
        
        functions.add(new Function(mainFunctionName, 0)); //main function
    }
    
    public final NamespaceScope getRootNamespace() { return rootNamespace; }
    
    public final Function getMainFunction() { return functions.getFirst(); }
    
    public final String getMainFunctionName() { return mainFunctionName; }
    
    public final List<Constant> getConstants() { return Collections.unmodifiableList(constants); }
    
    public final List<Function> getFunctions() { return Collections.unmodifiableList(functions); }
    
    public final List<LibraryElement> getLibraryElements() { return Collections.unmodifiableList(libelements); }
    
    private Constant createConstant(String name, Literal literal)
    {
        int index = constants.size();
        Constant constant = new Constant(name, index, literal);
        constants.add(constant);
        return constant;
    }
    
    private Function createFunction(String name)
    {
        if(name != null && name.equals(mainFunctionName))
            return functions.getFirst();
        int index = functions.size();
        Function func = new Function(name == null ? "" : name, index);
        functions.add(func);
        return func;
    }
    
    public final Constant registerLiteral(Literal literal)
    {
        SGSImmutableValue value = literal.getSGSValue();
        Constant c = literals.getOrDefault(value, null);
        if(c == null)
            literals.put(value, c = createConstant("", literal));
        return c;
    }
    
    public final int registerIdentifier(String identifier)
    {
        Integer index = identifiers.getOrDefault(identifier, null);
        if(index == null)
            identifiers.put(identifier, index = identifiers.size());
        return index;
    }
    
    public final LibraryElement findLibraryElement(String name)
    {
        LibraryElement e = usedLibElements.getOrDefault(name, null);
        if(e != null)
            return e;
        for(SGSLibrary lib : libs)
            if(lib.hasLibraryElement(name))
            {
                SGSLibraryElement libe = lib.getLibraryElement(name);
                if(libe != null)
                {
                    e = new LibraryElement(name, libelements.size(), libe);
                    libelements.add(e);
                    usedLibElements.put(name, e);
                    return e;
                }
            }
        return null;
    }
    
    private File findFileToInclude(String path) throws CompilerError
    {
        for(File dir : dirs)
        {
            File file = new File(dir.getAbsolutePath() + File.separator + path);
            if(file.exists() && file.isFile())
                return file;
        }
        throw new CompilerError("Script file \"" + path + "\" not found.");
    }
    
    private void includeScript(String path) throws CompilerError
    {
        File file = findFileToInclude(path);
        if(included.contains(file))
            return;
        included.add(file);
        SGSCompiler.includeScript(file, this);
    }
    
    public final SGSScript buildScript(SGSGlobals globals)
    {
        if(functions.getFirst().bytecode == null) //Main function not found
            FunctionCompiler.createEmptyFunction(functions.getFirst());
        
        SGSImmutableValue[] cnsts = new SGSImmutableValue[constants.size()];
        for(Constant c : constants)
            cnsts[c.getIndex()] = c.value.getSGSValue();
        
        String[] ids = new String[identifiers.size()];
        for(Map.Entry<String, Integer> e : identifiers.entrySet())
            ids[e.getValue()] = e.getKey();
        
        byte[][] funcs = new byte[functions.size()][];
        for(Function f : functions)
            funcs[f.getIndex()] = f.bytecode;
        
        SGSLibraryElement[] libels = new SGSLibraryElement[libelements.size()];
        for(LibraryElement e : libelements)
            libels[e.getIndex()] = e.element;
        
        return new SGSScript(globals, cnsts, ids, funcs, libels);
    }
    
    
    public final class NamespaceScope
    {
        private final NamespaceScope parent;
        private final NamespaceScopeType type;
        private final HashMap<String, NamespaceIdentifier> ids = new HashMap<>();
        private final LinkedList<LocalVariable> inherithedIds;
        private final LinkedList<OpcodeLocation> breakPoints;
        private final LinkedList<OpcodeLocation> continuePoints;
        private final RuntimeStack stack;
        private int lastLocalIndex;
        private int argCount = 0;
        private LocalVariable varargs = null;
        
        private NamespaceScope(NamespaceScope parent, int lastLocalIndex, NamespaceScopeType type)
        {
            if(type == null)
                throw new NullPointerException();
            this.parent = type == NamespaceScopeType.ROOT ? null : parent;
            this.type = type;
            this.inherithedIds = type == NamespaceScopeType.FUNCTION ? new LinkedList<>() : null;
            this.lastLocalIndex = type == NamespaceScopeType.FUNCTION ? -1 : lastLocalIndex;
            if(type == NamespaceScopeType.LOOP)
            {
                breakPoints = new LinkedList<>();
                continuePoints = new LinkedList<>();
            }
            else
            {
                breakPoints = null;
                continuePoints = null;
            }
            if(parent == null)
                stack = null;
            else stack = type == NamespaceScopeType.FUNCTION ? new RuntimeStack() : parent.stack;
        }
        
        public final ScriptBuilder getScriptBuilder() { return ScriptBuilder.this; }
        
        public final boolean isRootScope() { return type == NamespaceScopeType.ROOT; }
        public final boolean isLoopScope() { return type == NamespaceScopeType.LOOP; }
        public final boolean isFunctionScope() { return type == NamespaceScopeType.FUNCTION; }
        
        public final int getArgumentCount() { return argCount; }
        
        public final RuntimeStack getRuntimeStack() { return stack; }
        
        public final NamespaceScope createChildScope(NamespaceScopeType scopeType)
        {
            return new NamespaceScope(this, lastLocalIndex, scopeType);
        }
        
        public final boolean hasVarargs() { return varargs != null; }
        public final LocalVariable getVarargs() { return varargs; }
        
        public final void clear()
        {
            ids.clear();
            inherithedIds.clear();
        }
        
        
        public final boolean hasIdentifierInCurrentScope(String name)
        {
            return ids.containsKey(name);
        }
        
        public final boolean hasIdentifier(String name)
        {
            if(ids.containsKey(name))
                return true;
            return parent != null ? parent.hasIdentifier(name) : false;
        }
        
        public final boolean hasInheritedIds() { return inherithedIds != null && !inherithedIds.isEmpty(); }
        
        public final int getInheritedIdCount() { return inherithedIds == null ? 0 : inherithedIds.size(); }
        
        public final Iterable<LocalVariable> getInheritedIds() { return inherithedIds; }
        
        public final NamespaceIdentifier getIdentifier(String name) throws CompilerError
        {
            NamespaceIdentifier id = ids.getOrDefault(name, null);
            if(id != null)
                return id;
            if(parent != null)
            {
                id = parent.getIdentifier(name);
                if(isFunctionScope() && id.needInherition())
                {
                    inherithedIds.add((LocalVariable) id);
                    ids.put(id.getName(), id);
                }
                return id;
            }
            if((id = findLibraryElement(name)) != null)
                return id;
            throw new CompilerError("Identifier \"" + name + "\" not found.");
        }
        
        public final LocalVariable createLocalVariable(String name, DataType type) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            lastLocalIndex = stack.allocateVariable(lastLocalIndex);
            LocalVariable var = new LocalVariable(name, lastLocalIndex, type);
            ids.put(var.getName(), var);
            return var;
        }
        
        public final LocalVariable createHiddenLocalVariable(String name, DataType type) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            lastLocalIndex = stack.allocateVariable(lastLocalIndex);
            LocalVariable var = new LocalVariable(name, lastLocalIndex, type);
            return var;
        }
        
        public final GlobalVariable createGlobalVariable(String name) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            GlobalVariable var = new GlobalVariable(name);
            ids.put(var.getName(), var);
            return var;
        }
        
        public final Constant createConstant(String name, Literal literal) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            Constant c = ScriptBuilder.this.createConstant(name, literal);
            if(c != null)
                ids.put(name, c);
            return c;
        }
        
        public final Constant registerLiteral(Literal literal) { return ScriptBuilder.this.registerLiteral(literal); }
        
        public final Argument createArgument(String name, DataType type) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            if(varargs != null)
                throw new CompilerError("Cannot be arguments after varargs.");
            Argument arg = new Argument(name, argCount++, type);
            ids.put(arg.getName(), arg);
            return arg;
        }
        public final LocalVariable argumentToLocal(Argument arg) throws CompilerError
        {
            if(ids.getOrDefault(arg.getName(), null) == arg)
            {
                lastLocalIndex = stack.allocateVariable(lastLocalIndex);
                LocalVariable var = new LocalVariable(arg.getName(), lastLocalIndex, arg.getType());
                ids.put(arg.getName(), var);
                return var;
            }
            else if(parent != null)
                return parent.argumentToLocal(arg);
            else throw new IllegalStateException();
        }
        
        public final LocalVariable createVarArgument(String name) throws CompilerError
        {
            if(varargs != null)
                throw new CompilerError("Cannot be more than one varargs.");
            return varargs = createLocalVariable(name, DataType.ANY);
        }
        
        public final Function createFunction(String name) throws CompilerError
        {
            return ScriptBuilder.this.createFunction(name);
        }
        
        public final void registerFunction(Function function) throws CompilerError
        {
            if(function.getName().isEmpty())
                throw new IllegalStateException();
            if(hasIdentifierInCurrentScope(function.getName()))
                throw new CompilerError("Identifier \"" + function.getName() + "\" already exists.");
            ids.put(function.getName(), function);
        }
        
        public final NamespaceIdentifier registerClosure(Function function) throws CompilerError
        {
            if(function.getName().isEmpty())
                throw new IllegalStateException();
            return !hasIdentifierInCurrentScope(function.getName())
                    ? createLocalVariable(function.getName(), DataType.ANY)
                    : getIdentifier(function.getName());
        }
        
        public final int registerIdentifier(String identifier) { return ScriptBuilder.this.registerIdentifier(identifier); }
        
        public final void importLibrary(String identifier) throws CompilerError
        {
            if(allLibs == null || !allLibs.hasLibrary(identifier))
                throw new CompilerError("Library " + identifier + " not found");
            if(libs.hasLibrary(identifier))
                return;
            libs.registerLibrary(allLibs.getLibrary(identifier));
        }
        
        public final void includeScript(String path) throws CompilerError
        {
            ScriptBuilder.this.includeScript(path);
        }
        
        public final void registerBreakPoint(OpcodeLocation loc) throws CompilerError
        {
            if(breakPoints == null)
            {
                if(parent == null)
                    throw new CompilerError("Cannot use break in non loop scope");
                if(type == NamespaceScopeType.FUNCTION || type == NamespaceScopeType.ROOT)
                    throw new CompilerError("Cannot use break in non loop scope");
                parent.registerBreakPoint(loc);
            }
            else breakPoints.add(loc);
        }
        public final boolean hasBreakPoints() { return breakPoints != null && !breakPoints.isEmpty(); }
        public final List<OpcodeLocation> getBreakPoints() { return breakPoints; }
        public final void setBreakPointLocations(OpcodeList opcodes, OpcodeLocation endLoc)
        {
            if(breakPoints != null)
                for(OpcodeLocation loc : breakPoints)
                    opcodes.setJumpOpcodeLocationTarget(loc, endLoc);
        }
        
        public final void registerContinuePoint(OpcodeLocation loc) throws CompilerError
        {
            if(continuePoints == null)
            {
                if(parent == null)
                    throw new CompilerError("Cannot use continue in non loop scope");
                if(type == NamespaceScopeType.FUNCTION || type == NamespaceScopeType.ROOT)
                    throw new CompilerError("Cannot use continue in non loop scope");
                parent.registerContinuePoint(loc);
            }
            else continuePoints.add(loc);
        }
        public final boolean hasContinuePoints() { return continuePoints != null && !continuePoints.isEmpty(); }
        public final List<OpcodeLocation> getContinuePoints() { return continuePoints; }
        public final void setContinuePointLocations(OpcodeList opcodes, OpcodeLocation startLoc)
        {
            if(continuePoints != null)
                for(OpcodeLocation loc : continuePoints)
                    opcodes.setJumpOpcodeLocationTarget(loc, startLoc);
        }
    }
    
    
    public abstract class NamespaceIdentifier
    {
        private final String name;
        private final int index;
        
        private NamespaceIdentifier(String name, int index)
        {
            this.name = Objects.requireNonNull(name);
            this.index = index;
        }
        
        public abstract IdentifierType getIdentifierType();
        
        public final boolean isLocalVariable() { return getIdentifierType() == IdentifierType.LOCAL_VARIABLE; }
        public final boolean isGlobalVariable() { return getIdentifierType() == IdentifierType.GLOBAL_VARIABLE; }
        public final boolean isFunction() { return getIdentifierType() == IdentifierType.FUNCTION; }
        public final boolean isConstant() { return getIdentifierType() == IdentifierType.CONSTANT; }
        public final boolean isArgument() { return getIdentifierType() == IdentifierType.ARGUMENT; }
        public final boolean isLibraryElement() { return getIdentifierType() == IdentifierType.LIBRARY_ELEMENT; }
        
        public final String getName() { return name; }
        public final int getIndex() { return index; }
        public DataType getType() { return DataType.ANY; }
        public DataType getReturnType() { return null; }
        public boolean isReassignable() { return true; }
        public boolean needInherition() { return false; }
        public SGSLibraryElement getLibraryElement() { return null; }
    }
    
    public class LocalVariable extends NamespaceIdentifier
    {
        private final DataType type;
        
        private LocalVariable(String name, int index, DataType type)
        {
            super(name, index);
            this.type = Objects.requireNonNull(type);
        }
        
        @Override public IdentifierType getIdentifierType() { return IdentifierType.LOCAL_VARIABLE; }
        
        @Override public final DataType getType() { return type; }
        @Override public final boolean needInherition() { return true; }
    }
    
    public final class GlobalVariable extends NamespaceIdentifier
    {
        
        private GlobalVariable(String name)
        {
            super(name, registerIdentifier(name));
        }
        
        @Override public final IdentifierType getIdentifierType() { return IdentifierType.GLOBAL_VARIABLE; }
    }
    
    public final class Constant extends NamespaceIdentifier
    {
        private final DataType type;
        private final Literal value;
        
        private Constant(String name, int index, Literal value)
        {
            super(name, index);
            this.value = Objects.requireNonNull(value);
            this.type = value.getLiteralType().getDataType();
        }
        
        @Override public final IdentifierType getIdentifierType() { return IdentifierType.CONSTANT; }
        
        @Override public final DataType getType() { return type; }
        @Override public final boolean isReassignable() { return false; }
        
        public final Literal getLiteralValue() { return value; }
    }
    
    public final class Argument extends LocalVariable
    {
        private Argument(String name, int index, DataType type)
        {
            super(name, index, type);
        }
        
        @Override public final IdentifierType getIdentifierType() { return IdentifierType.ARGUMENT; }
    }
    
    public final class Function extends NamespaceIdentifier
    {
        private DataType returnType;
        private byte[] bytecode;
        
        private Function(String name, int index)
        {
            super(name, index);
            this.returnType = DataType.ANY;
            this.bytecode = null;
        }
        
        @Override public final IdentifierType getIdentifierType() { return IdentifierType.FUNCTION; }
        
        @Override public final DataType getReturnType() { return returnType; }
        @Override public final boolean isReassignable() { return false; }
        
        public final void setReturnType(DataType type) { this.returnType = Objects.requireNonNull(type); }
        public final void setBytecode(byte[] bytecode) { this.bytecode = Objects.requireNonNull(bytecode); }
    }
    
    public final class LibraryElement extends NamespaceIdentifier
    {
        private final SGSLibraryElement element;

        public LibraryElement(String name, int index, SGSLibraryElement element)
        {
            super(name, index);
            this.element = Objects.requireNonNull(element);
        }
        
        @Override
        public final IdentifierType getIdentifierType() { return IdentifierType.LIBRARY_ELEMENT; }
        
        @Override public DataType getType() { DataType t; return (t = element.getValueType()) == null ? DataType.ANY : t; }
        @Override public DataType getReturnType() { DataType t; return (t = element.getReturnType()) == null ? DataType.ANY : t; }
        @Override public final boolean isReassignable() { return false; }
        @Override public SGSLibraryElement getLibraryElement() { return null; }
    }
    
    
    public static enum IdentifierType
    {
        LOCAL_VARIABLE, GLOBAL_VARIABLE, CONSTANT, ARGUMENT, FUNCTION, LIBRARY_ELEMENT
    }
    
    public static enum NamespaceScopeType
    {
        ROOT,
        NORMAL,
        LOOP,
        FUNCTION;
    }
}
