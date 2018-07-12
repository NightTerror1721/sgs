/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.Objects;
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
    
}
