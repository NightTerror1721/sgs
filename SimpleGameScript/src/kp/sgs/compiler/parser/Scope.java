/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.instruction.Instruction;

/**
 *
 * @author Asus
 */
public final class Scope extends Statement implements Iterable<Instruction>
{
    public static final Scope EMPTY_SCOPE = new Scope(new Instruction[0]);
    
    private final List<Instruction> instructions;
    
    public Scope(List<Instruction> instructions)
    {
        this.instructions = Objects.requireNonNull(instructions);
    }
    public Scope(Instruction... instructions)
    {
        this.instructions = Arrays.asList(instructions);
    }
    
    public final boolean isEmpty() { return instructions.isEmpty(); }
    
    public final int getInstructionCount() { return instructions.size(); }
    
    public final Instruction getInstruction(int index) { return instructions.get(index); }
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.SCOPE; }

    @Override
    public final Iterator<Instruction> iterator() { return instructions.iterator(); }

    @Override
    public final String toString()
    {
        if(instructions.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for(Instruction inst : instructions)
            sb.append(inst.getInstructionId()).append('\n');
        return sb.append('}').toString();
    }
}
