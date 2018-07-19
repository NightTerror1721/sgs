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
import kp.sgs.SGSScript;
import kp.sgs.compiler.ScriptBuilder.Function;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
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

/**
 *
 * @author Asus
 */
public final class SGSCompiler
{
    private SGSCompiler() {}
    
    public static final SGSScript compile(InputStream input, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(input), props);
    }
    
    public static final SGSScript compile(Reader reader, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(reader), props);
    }
    
    public static final SGSScript compile(File file, CompilerProperties props) throws CompilerException, IOException
    {
        try(FileInputStream fis = new FileInputStream(file)) { return compile(new CodeReader(fis), props); }
    }
    
    private static SGSScript compile(CodeReader source, CompilerProperties props) throws CompilerException
    {
        ScriptBuilder builder = new ScriptBuilder(props);
        ErrorList errors = new ErrorList();
        List<Instruction> insts = InstructionParser.parse(source, errors);
        if(errors.hasErrors())
            throw new CompilerException(errors);
        
        LinkedList<Operation> functions = new LinkedList<>();
        try
        {
            for(Instruction inst : insts)
                inst.compileConstantPart(builder.getRootNamespace(), functions);
            
            for(Operation funcOp : functions)
                compileStaticFunction(builder.getRootNamespace(), funcOp);
        }
        catch(CompilerError ex) { throw CompilerException.single(ex); }
        
        return builder.buildScript();
    }
    
    private static void compileStaticFunction(NamespaceScope scope, Operation op) throws CompilerError
    {
        Identifier name = op.getOperand(0);
        if(name == null)
            throw new CompilerError("Expected valid identifier name for static function");
        Arguments pars = op.getOperand(1);
        NamespaceScope child = scope.createChildScope(true);
        StatementCompiler.compileNewFunctionParameters(child, pars);
        Scope funcScope = op.getOperand(2);
        Function func = scope.createFunction(name.toString());
        
        FunctionCompiler.compile(func, child, funcScope);
        if(child.hasInheritedIds())
            throw new CompilerError("Static function cannot have inherited elements.");
        scope.registerFunction(func);
    }
}
