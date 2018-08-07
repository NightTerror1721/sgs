/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.StatementCompiler;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Scope;

/**
 *
 * @author Asus
 */
public final class InstructionScope extends Instruction
{
    private final Scope scope;
    
    public InstructionScope(Scope scope)
    {
        this.scope = Objects.requireNonNull(scope);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.SCOPE; }
    
    @Override
    public final void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        for(Instruction inst : this.scope)
            inst.compileConstantPart(scope, functions);
    }

    @Override
    public final void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        StatementCompiler.compileScope(scope.createChildScope(false), opcodes, this.scope);
    }
}
