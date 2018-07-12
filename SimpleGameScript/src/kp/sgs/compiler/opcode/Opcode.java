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
public class Opcode
{
    protected int opcode;
    protected int length;
    protected int stackPush;
    protected int stackPop;
    
    public Opcode(int opcode, int length, int stackPush, int stackPop)
    {
        this.opcode = opcode;
        this.length = length;
        this.stackPush = stackPush;
        this.stackPop = stackPop;
    }
    protected Opcode() { this( -1, 1, 0, 0); }
    
    public int getOpcode() { return opcode; }
    
    public int getByteCount() { return length; }
    
    public int getStackPush() { return stackPush; }
    public int getStackPop() { return stackPop; }
    
    public boolean isJumpOpcode() { return false; }
    public OpcodeLocation getJumpTargetLocation() { throw new UnsupportedOperationException(); }
    public void setJumpTargetLocation(OpcodeLocation target) { throw new UnsupportedOperationException(); }
    
    public void build(byte[] bytecode, int offset)
    {
        bytecode[offset] = (byte) (opcode & 0xff);
    }
}
