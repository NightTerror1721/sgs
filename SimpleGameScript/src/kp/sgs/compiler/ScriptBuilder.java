/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kp.sgs.SGSScript;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Literal;
import kp.sgs.data.SGSImmutableValue;

/**
 *
 * @author Asus
 */
public final class ScriptBuilder
{
    private final HashMap<SGSImmutableValue, Constant> literals = new HashMap<>();
    private final HashMap<String, Integer> identifiers = new HashMap<>();
    private final LinkedList<Constant> constants = new LinkedList<>();
    private final LinkedList<Function> functions = new LinkedList<>();
    private final NamespaceScope rootNamespace;
    private final RuntimeStack stack;
    private final String mainFunctionName;
    
    public ScriptBuilder(CompilerProperties props)
    {
        this.rootNamespace = new NamespaceScope(null, false);
        this.stack = new RuntimeStack();
        this.mainFunctionName = props.getFunctionName();
        
        functions.add(new Function(mainFunctionName, 0)); //main function
    }
    
    public final RuntimeStack getRuntimeStack() { return stack; }
    
    public final NamespaceScope getRootNamespace() { return rootNamespace; }
    
    public final Function getMainFunction() { return functions.getFirst(); }
    
    public final String getMainFunctionName() { return mainFunctionName; }
    
    public final List<Constant> getConstants() { return Collections.unmodifiableList(constants); }
    
    public final List<Function> getFunctions() { return Collections.unmodifiableList(functions); }
    
    private Constant createConstant(String name, Literal literal)
    {
        int index = constants.size();
        Constant constant = new Constant(name, index, literal);
        constants.add(constant);
        return constant;
    }
    
    private Function createFunction(String name)
    {
        int index = functions.size();
        Function func = new Function(name, index);
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
    
    public final SGSScript buildScript()
    {
        SGSImmutableValue[] cnsts = new SGSImmutableValue[constants.size()];
        for(Constant c : constants)
            cnsts[c.getIndex()] = c.value.getSGSValue();
        
        String[] ids = new String[identifiers.size()];
        for(Map.Entry<String, Integer> e : identifiers.entrySet())
            ids[e.getValue()] = e.getKey();
        
        byte[][] funcs = new byte[functions.size()][];
        for(Function f : functions)
            funcs[f.getIndex()] = f.bytecode;
        
        return new SGSScript(cnsts, ids, funcs);
    }
    
    
    public final class NamespaceScope
    {
        private final NamespaceScope parent;
        private final HashMap<String, NamespaceIdentifier> ids = new HashMap<>();
        private final LinkedList<LocalVariable> inherithedIds;
        private int argCount = 0;
        
        private NamespaceScope(NamespaceScope parent, boolean functionScope)
        {
            this.parent = parent;
            this.inherithedIds = functionScope ? new LinkedList<>() : null;
        }
        
        public final ScriptBuilder getScriptBuilder() { return ScriptBuilder.this; }
        
        public final boolean isFunctionScope() { return inherithedIds != null; }
        
        public final int getArgumentCount() { return argCount; }
        
        public final RuntimeStack getRuntimeStack() { return stack; }
        
        public final NamespaceScope createChildScope(boolean functionScope)
        {
            return new NamespaceScope(this, functionScope);
        }
        
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
        
        public final NamespaceIdentifier getIdentifier(String name)
        {
            NamespaceIdentifier id = ids.getOrDefault(name, null);
            if(id != null)
                return id;
            if(parent != null)
            {
                if(isFunctionScope() && id.needInherition())
                {
                    id = parent.getIdentifier(name);
                    inherithedIds.add((LocalVariable) id);
                    ids.put(id.getName(), id);
                    return id;
                }
                else return parent.getIdentifier(name);
            }
            return new GlobalVariable(name);
        }
        
        public final LocalVariable createLocalVariable(String name, DataType type) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            int index = stack.allocateVariable();
            LocalVariable var = new LocalVariable(name, index, type);
            ids.put(var.getName(), var);
            return var;
        }
        
        public final Constant createConstant(String name, Literal literal) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            return ScriptBuilder.this.createConstant(name, literal);
            
        }
        
        public final Constant registerLiteral(Literal literal) { return ScriptBuilder.this.registerLiteral(literal); }
        
        public final Argument createArgument(String name, DataType type) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            Argument arg = new Argument(name, argCount++, type);
            ids.put(arg.getName(), arg);
            return arg;
        }
        
        public final Function createFunction(String name) throws CompilerError
        {
            if(hasIdentifierInCurrentScope(name))
                throw new CompilerError("Identifier \"" + name + "\" already exists.");
            return ScriptBuilder.this.createFunction(name);
        }
        
        public final int registerIdentifier(String identifier) { return ScriptBuilder.this.registerIdentifier(identifier); }
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
        
        public final String getName() { return name; }
        public final int getIndex() { return index; }
        public DataType getType() { return DataType.ANY; }
        public DataType getReturnType() { return null; }
        public boolean isReassignable() { return true; }
        public boolean needInherition() { return false; }
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
        
        @Override public final DataType getReturnType() { return null; }
        @Override public final boolean isReassignable() { return false; }
        
        public final void setReturnType(DataType type) { this.returnType = Objects.requireNonNull(type); }
        public final void setBytecode(byte[] bytecode) { this.bytecode = Objects.requireNonNull(bytecode); }
    }
    
    
    public static enum IdentifierType
    {
        LOCAL_VARIABLE, GLOBAL_VARIABLE, CONSTANT, ARGUMENT, FUNCTION
    }
}