/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.Objects;
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
    
}
