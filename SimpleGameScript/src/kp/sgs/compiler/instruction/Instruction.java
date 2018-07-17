/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.opcode.OpcodeList;

/**
 *
 * @author Asus
 */
public abstract class Instruction
{
    public abstract InstructionId getInstructionId();
    
    public abstract void compileConstantPart(NamespaceScope scope, OpcodeList opcodes);
    public abstract void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes);
}
