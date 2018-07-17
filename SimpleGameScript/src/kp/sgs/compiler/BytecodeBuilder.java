/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import kp.sgs.SGSConstants;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.parser.DataType;

/**
 *
 * @author Asus
 */
public final class BytecodeBuilder
{
    private BytecodeBuilder() {}
    
    public static final byte[] buildFunction(int stackSize, int varsCount, DataType returnType, OpcodeList opcodes)
    {
        if(stackSize < 0 || stackSize > 0xff)
            throw new IllegalArgumentException("Stack size overflow");
        if(varsCount < 0 || varsCount > 0xff)
            throw new IllegalArgumentException("Variable count overflow");
        if(returnType == null)
            throw new NullPointerException();
        if(opcodes == null)
            throw new NullPointerException();
        
        int opcodeLen = opcodes.buildBytePositions();
        byte[] bytecode = new byte[SGSConstants.CODE_INIT + opcodeLen];
        bytecode[SGSConstants.CODE_STACK_LEN] = (byte) (stackSize & 0xff);
        bytecode[SGSConstants.CODE_VARS_LEN] = (byte) (varsCount & 0xff);
        bytecode[SGSConstants.CODE_RETURN_TYPE] = (byte) (returnType.getTypeId() & 0xff);
        opcodes.buildBytecodes(bytecode, SGSConstants.CODE_INIT);
        
        return bytecode;
    }
    public static final byte[] buildFunction(NamespaceScope scope, DataType returnType, OpcodeList opcodes)
    {
        return buildFunction(scope.getRuntimeStack().getMaxStackLength(), scope.getRuntimeStack().getVariableCount(), returnType, opcodes);
    }
}
