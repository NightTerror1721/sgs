/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import kp.sgs.SGSGlobals;
import kp.sgs.SGSScript;
import kp.sgs.compiler.ScriptBuilder.Function;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.ScriptBuilder.NamespaceScopeType;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.exception.CompilerException;
import kp.sgs.compiler.exception.ErrorList;
import kp.sgs.compiler.instruction.Instruction;
import kp.sgs.compiler.instruction.InstructionParser;
import kp.sgs.compiler.parser.Arguments;
import kp.sgs.compiler.parser.CodeReader;
import kp.sgs.compiler.parser.Identifier;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Scope;
import kp.sgs.compiler.parser.Varargs;

/**
 *
 * @author Asus
 */
public final class SGSCompiler
{
    private SGSCompiler() {}
    
    public static final SGSScript compile(InputStream input, SGSGlobals globals, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(input), globals, props, false);
    }
    
    public static final SGSScript compile(Reader reader, SGSGlobals globals, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(reader), globals, props, false);
    }
    
    public static final SGSScript compile(File file, SGSGlobals globals, CompilerProperties props) throws CompilerException, IOException
    {
        try(FileInputStream fis = new FileInputStream(file)) { return compile(new CodeReader(fis), globals, props, false); }
    }
    
    public static final SGSScript compile(String code, SGSGlobals globals, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(code), globals, props, false);
    }
    
    public static final SGSScript compileInline(InputStream input, SGSGlobals globals, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(input), globals, props, true);
    }
    
    public static final SGSScript compileInline(Reader reader, SGSGlobals globals, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(reader), globals, props, true);
    }
    
    public static final SGSScript compileInline(File file, SGSGlobals globals, CompilerProperties props) throws CompilerException, IOException
    {
        try(FileInputStream fis = new FileInputStream(file)) { return compile(new CodeReader(fis), globals, props, false); }
    }
    
    public static final SGSScript compileInline(String inlineScriptCode, SGSGlobals globals, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(inlineScriptCode), globals, props, true);
    }
    
    private static SGSScript compile(CodeReader source, SGSGlobals globals, CompilerProperties props, boolean inlineScript) throws CompilerException
    {
        ScriptBuilder builder = new ScriptBuilder(props);
        ErrorList errors = new ErrorList();
        
        if(inlineScript)
            compileInlineScript(source, builder, errors);
        else compile(source, builder, errors);
        
        return builder.buildScript(globals);
    }
    
    private static void compile(CodeReader source, ScriptBuilder builder, ErrorList errors) throws CompilerException
    {
        source = CommentDeleter.removeComments(source);
        List<Instruction> insts = InstructionParser.parse(source, errors, false);
        if(errors.hasErrors())
            throw new CompilerException(errors);
        
        LinkedList<Operation> functions = new LinkedList<>();
        try
        {
            for(Instruction inst : insts)
                inst.compileConstantPart(builder.getRootNamespace(), functions);
            
            functions.stream().map(op -> new StaticFunction(builder.getRootNamespace(), op)).collect(Collectors.toList())
                     .stream().forEach(StaticFunction::compile);
        }
        catch(CompilerError ex) { throw CompilerException.single(ex); }
        catch(NullPointerException ex) { ex.printStackTrace(System.err); throw CompilerException.single(new CompilerError("NULL POINTER EXCEPTION")); }
        catch(RuntimeException ex) { throw CompilerException.single((CompilerError) ex.getCause()); }
    }
    
    private static void compileInlineScript(CodeReader source, ScriptBuilder builder, ErrorList errors) throws CompilerException
    {
        source = CommentDeleter.removeComments(source);
        List<Instruction> insts = InstructionParser.parse(source, errors, false);
        if(errors.hasErrors())
            throw new CompilerException(errors);
        
        try
        {
            Arguments args = Arguments.valueOf(new Varargs(Identifier.valueOf("args")));
            Operation op = Operation.newFunction(Identifier.valueOf(builder.getMainFunctionName()), args, new Scope(insts));
            
            StaticFunction function = new StaticFunction(builder.getRootNamespace(), op);
            function.compile();
        }
        catch(CompilerError ex) { throw CompilerException.single(ex); }
        catch(NullPointerException ex) { ex.printStackTrace(System.err); throw CompilerException.single(new CompilerError("NULL POINTER EXCEPTION")); }
        catch(RuntimeException ex) { throw CompilerException.single((CompilerError) ex.getCause()); }
    }
    
    static final void includeScript(File file, ScriptBuilder builder) throws CompilerError
    {
        try(FileInputStream fis = new FileInputStream(file))
        {
            CodeReader source = new CodeReader(fis);
            ErrorList errors = new ErrorList();
            compile(source, builder, errors);
        }
        catch(IOException ex) { throw new CompilerError("Error in file: " + file, ex); }
        catch(CompilerException ex)
        {
            throw new CompilerError("Errors in file " + file + ": " + ex.getMessage());
        }
    }
    
    private static final class StaticFunction
    {
        private final NamespaceScope scope;
        private final Function function;
        private final Arguments pars;
        private final Scope instScope;
        
        private StaticFunction(NamespaceScope scope, Operation operation)
        {
            try
            {
                this.scope = scope;
                Identifier name = operation.getOperand(0);
                if(name == null)
                    throw new CompilerError("Expected valid identifier name for static function");
                this.pars = operation.getOperand(1);
                this.instScope = operation.getOperand(2);
                this.function = scope.createFunction(name.toString());
                scope.registerFunction(function);
            }
            catch(CompilerError ex) { throw new RuntimeException(ex); }
        }
        
        private void compile()
        {
            try
            {
                NamespaceScope child = scope.createChildScope(NamespaceScopeType.FUNCTION);
                StatementCompiler.compileNewFunctionParameters(child, pars);
                FunctionCompiler.compile(function, child, instScope);
                if(child.hasInheritedIds())
                    throw new CompilerError("Static function cannot have inherited elements.");
            }
            catch(CompilerError ex) { throw new RuntimeException(ex); }
        }
    }
}
