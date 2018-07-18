/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Statement;

/**
 *
 * @author Asus
 */
public class InstructionStatement extends Instruction
{
    private final Statement statement;
    
    public InstructionStatement(Statement statement)
    {
        this.statement = Objects.requireNonNull(statement);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.STATEMENT; }
    
    @Override
    public final void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates
    }

    @Override
    public final void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
