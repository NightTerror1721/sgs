/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.opcode.OpcodeList.OpcodeLocation;
import kp.sgs.compiler.opcode.Opcodes;
import kp.sgs.compiler.parser.Operation;

/**
 *
 * @author Asus
 */
public class InstructionJumpPoint extends Instruction
{
    private static final InstructionJumpPoint BREAK = new InstructionJumpPoint(true);
    private static final InstructionJumpPoint CONTINUE = new InstructionJumpPoint(false);
    
    private final boolean isBreak;
    
    private InstructionJumpPoint(boolean isBreak)
    {
        this.isBreak = isBreak;
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.JUMP_POINT; }
    
    public static final InstructionJumpPoint create(boolean isBreak)
    {
        return isBreak ? BREAK : CONTINUE;
    }

    @Override
    public final void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        throw new CompilerError("Cannot compile jump instruction in constant mode");
    }

    @Override
    public final void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        OpcodeLocation jump = opcodes.append(Opcodes.goTo());
        if(isBreak)
            scope.registerBreakPoint(jump);
        else scope.registerContinuePoint(jump);
    }
    
}
