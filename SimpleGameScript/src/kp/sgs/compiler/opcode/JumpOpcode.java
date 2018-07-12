/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.opcode;

import kp.sgs.compiler.opcode.OpcodeList.OpcodeLocation;

/**
 *
 * @author Asus
 */
public class JumpOpcode extends Opcode
{
    private final int opcode16;
    private OpcodeLocation target;
    
    JumpOpcode(int opcode, int opcode16, int stackPop, OpcodeLocation target)
    {
        super(opcode, 3, 0, stackPop);
        this.opcode16 = opcode16;
        this.target = target;
    }
    JumpOpcode(int opcode, int opcode16, int stackPop) { this(opcode, opcode16, stackPop, null); }
    
    @Override public final boolean isJumpOpcode() { return true; }
    @Override public final OpcodeLocation getJumpTargetLocation() { return target; }
    @Override public final void setJumpTargetLocation(OpcodeLocation target) { this.target = target; }
    
    @Override
    public final void build(byte[] bytecode, int offset)
    {
        if(target == null)
            throw new IllegalStateException("Invalid jump instruction");
        int index = target.getFirstByte();
        bytecode[offset] = (byte) (index > 0xff ? opcode16 : opcode);
        bytecode[offset + 1] = (byte) (index & 0xff);
        bytecode[offset + 2] = (byte) ((index >>> 8) & 0xff);
    }
}
