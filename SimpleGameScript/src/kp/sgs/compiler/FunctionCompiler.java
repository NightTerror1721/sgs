/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import java.util.List;
import kp.sgs.compiler.ScriptBuilder.Function;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.instruction.Instruction;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.opcode.Opcodes;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Scope;

/**
 *
 * @author Asus
 */
public final class FunctionCompiler
{
    private FunctionCompiler() {}
    
    public static final void compile(Function function, NamespaceScope scope, List<Instruction> instructions) throws CompilerError
    {
        OpcodeList opcodes = new OpcodeList(scope.getRuntimeStack());
        if(scope.hasVarargs())
            opcodes.append(Opcodes.argsToArray(scope.getVarargs().getIndex(), scope.getArgumentCount()));
        for(Instruction instruction : instructions)
            instruction.compileFunctionPart(scope, opcodes);
        opcodes.append(Opcodes.RETURN_NONE);
        
        byte[] bytecode =  BytecodeBuilder.buildFunction(scope, DataType.ANY, opcodes);
        function.setBytecode(bytecode);
        function.setReturnType(DataType.ANY);
    }
    
    public static final void compile(Function function, NamespaceScope scope, Scope funcScope) throws CompilerError
    {
        OpcodeList opcodes = new OpcodeList(scope.getRuntimeStack());
        if(scope.hasVarargs())
            opcodes.append(Opcodes.argsToArray(scope.getVarargs().getIndex(), scope.getArgumentCount()));
        for(Instruction instruction : funcScope)
            instruction.compileFunctionPart(scope, opcodes);
        opcodes.append(Opcodes.RETURN_NONE);
        
        byte[] bytecode =  BytecodeBuilder.buildFunction(scope, DataType.ANY, opcodes);
        function.setBytecode(bytecode);
        function.setReturnType(DataType.ANY);
    }
}
