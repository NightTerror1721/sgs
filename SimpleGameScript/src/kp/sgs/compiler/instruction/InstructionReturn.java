/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.Mutable;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;
import kp.sgs.compiler.parser.Stopchar;

/**
 *
 * @author Asus
 */
public final class InstructionReturn extends Instruction
{
    private final Statement value;
    
    private InstructionReturn(Statement value) { this.value = value; }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.RETURN; }
    
    
    public static final InstructionReturn create(CodeFragmentList list) throws CompilerError
    {
        if(list == null || list.isEmpty())
            return new InstructionReturn(null);
        
        CodeFragmentList[] parts = list.split(Stopchar.COMMA);
        if(parts == null || parts.length == 0 || (parts.length == 1 && parts[0].isEmpty()))
            return new InstructionReturn(null);
        
        if(parts.length == 1)
            return new InstructionReturn(StatementParser.parse(parts[0]));
        
        return new InstructionReturn(Mutable.array(parts));
    }
}
