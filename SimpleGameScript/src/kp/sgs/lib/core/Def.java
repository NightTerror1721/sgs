/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.util.Objects;
import kp.sgs.SGSGlobals;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.data.SGSUserdata;
import kp.sgs.data.SGSValue;
import kp.sgs.lib.SGSLibraryElement;

/**
 *
 * @author Asus
 */
public final class Def
{
    private Def() {}
    
    public static final SGSLibraryElement function(String name, DataType returnType, FunctionClosure function)
    {
        return new LibraryFunction(name, returnType, function);
    }
    public static final SGSLibraryElement function(String name, DataType returnType, VoidFunctionClosure function)
    {
        return new VoidLibraryFunction(name, returnType, function);
    }
    public static final SGSLibraryElement function(String name, FunctionClosure function)
    {
        return new LibraryFunction(name, DataType.ANY, function);
    }
    public static final SGSLibraryElement function(String name, VoidFunctionClosure function)
    {
        return new VoidLibraryFunction(name, DataType.ANY, function);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    private static final class LibraryFunction extends AbstractLibraryFunction
    {
        private final FunctionClosure closure;
        
        private LibraryFunction(String name, DataType retType, FunctionClosure closure)
        {
            super(name, retType);
            this.closure = Objects.requireNonNull(closure);
        }

        @Override
        public final SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { return closure.execute(globals, args); }
    }
    
    private static final class VoidLibraryFunction extends AbstractLibraryFunction
    {
        private final VoidFunctionClosure closure;
        
        private VoidLibraryFunction(String name, DataType retType, VoidFunctionClosure closure)
        {
            super(name, retType);
            this.closure = Objects.requireNonNull(closure);
        }

        @Override
        public final SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { closure.execute(globals, args); return SGSValue.UNDEFINED; }
    }
    
    private static abstract class AbstractLibraryFunction extends AbstractLibraryElement
    {
        private final DataType retType;
        
        public AbstractLibraryFunction(String name, DataType retType)
        {
            super(name);
            this.retType = Objects.requireNonNull(retType);
        }
        
        @Override
        public final DataType getReturnType() { return retType; }
        
        @Override
        public abstract SGSValue operatorCall(SGSGlobals globals, SGSValue[] args);
    }
    
    @FunctionalInterface
    public static interface FunctionClosure { SGSValue execute(SGSGlobals globals, SGSValue[] args); }
    
    @FunctionalInterface
    public static interface VoidFunctionClosure { void execute(SGSGlobals globals, SGSValue[] args); }
    
    private static abstract class AbstractLibraryElement extends SGSLibraryElement
    {
        private AbstractLibraryElement(String name) { super(name); }
        
        @Override
        public SGSValue toSGSValue(SGSGlobals globals) { return new LibraryElementWrapper(this, globals); }
        
        @Override
        public DataType getValueType() { return DataType.ANY; }

        @Override
        public DataType getReturnType() { return DataType.ANY; }

        @Override
        public SGSValue operatorGet(SGSValue index) { throw new UnsupportedOperationException(); }

        @Override
        public SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException(); }

        @Override
        public SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { throw new UnsupportedOperationException(); }

        @Override
        public SGSValue operatorReferenceGet() { throw new UnsupportedOperationException(); }
    }
    
    private static final class LibraryElementWrapper extends SGSUserdata
    {
        private final SGSLibraryElement element;
        private final SGSGlobals globals;
        
        private LibraryElementWrapper(SGSLibraryElement element, SGSGlobals globals) { this.element = element; this.globals = globals; }
        
        @Override
        public final SGSValue operatorGet(SGSValue index) { return element.operatorGet(index); }
        
        @Override
        public final SGSValue operatorGet(int index) { return element.operatorGet(index); }

        @Override
        public final SGSValue operatorGetProperty(String name) { return element.operatorGetProperty(name); }

        @Override
        public final SGSValue operatorCall(SGSValue[] args) { return element.operatorCall(globals, args); }
    }
}
