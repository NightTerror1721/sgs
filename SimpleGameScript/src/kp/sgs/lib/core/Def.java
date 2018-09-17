/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import kp.sgs.SGSGlobals;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.data.SGSFunction;
import kp.sgs.data.SGSImmutableObject;
import kp.sgs.data.SGSMutableObject;
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
    
    public static final SGSValue method(MethodClosure method)
    {
        return new Method(method);
    }
    public static final SGSValue method(VoidMethodClosure method)
    {
        return new VoidMethod(method);
    }
    
    
    public static final SGSLibraryElement objectClass(String name, SGSValue clazz)
    {
        return new LibraryClass(name, clazz);
    }
    
    public static final SGSLibraryElement objectClass(String name, Map<String, SGSValue> classElements)
    {
        return new LibraryClass(name, new SGSImmutableObject(classElements));
    }
    
    public static final SGSLibraryElement objectClass(String name, final InnerLibraryClass classElements)
    {
        return new LibraryClass(name, new SGSUserdata()
        {
            @Override
            public final SGSValue operatorGetProperty(String name)
            {
                SGSValue value;
                return (value = classElements.getProperty(name)) == null ? SGSValue.UNDEFINED : value;
            }
        });
    }
    
    public static final SGSLibraryElement customObjectClass(String name, Function<SGSValue[], SGSMutableObject> customObjectCreator)
    {
        return new CustomLibraryClass(name, customObjectCreator);
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
    
    @FunctionalInterface
    public static interface InnerLibraryClass { SGSValue getProperty(String name); }
    
    private static final class LibraryClass extends AbstractLibraryElement
    {
        private final SGSValue base;
        public LibraryClass(String name, SGSValue base)
        {
            super(name);
            this.base = Objects.requireNonNull(base);
        }
        
        @Override
        public final SGSMutableObject constructor(SGSValue[] args) { return new SGSMutableObject(base); }
    }
    private static final class CustomLibraryClass extends AbstractLibraryElement
    {
        private final Function<SGSValue[], SGSMutableObject> creator;
        public CustomLibraryClass(String name, Function<SGSValue[], SGSMutableObject> creator)
        {
            super(name);
            this.creator = Objects.requireNonNull(creator);
        }
        
        @Override
        public final SGSMutableObject constructor(SGSValue[] args) { return creator.apply(args); }
    }
    
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
        
        @Override
        public SGSMutableObject constructor(SGSValue[] args) { return new SGSMutableObject(); }
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
    
    private static final class Method extends SGSFunction
    {
        private final MethodClosure function;
        
        private Method(MethodClosure function)
        {
            if(function == null)
                throw new NullPointerException();
            this.function = function;
        }
        
        @Override
        public SGSValue operatorCall(SGSValue[] args) { return function.execute(args); }
    }
    
    private static final class VoidMethod extends SGSFunction
    {
        private final VoidMethodClosure function;
        
        private VoidMethod(VoidMethodClosure function)
        {
            if(function == null)
                throw new NullPointerException();
            this.function = function;
        }
        
        @Override
        public SGSValue operatorCall(SGSValue[] args)
        {
            function.execute(args);
            return SGSValue.UNDEFINED;
        }
    }
    
    @FunctionalInterface
    public static interface MethodClosure { SGSValue execute(SGSValue[] args); }
    
    @FunctionalInterface
    public static interface VoidMethodClosure { void execute(SGSValue[] args); }
}
