/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import kp.sgs.SGSConstants;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.Opcode;

/**
 *
 * @author Asus
 */
public final class RuntimeStack
{
    private int stackLen, stackUsed;
    private int allocatedVars;
    
    private void push(int amount) throws CompilerError
    {
        if(amount == 0)
            return;
        amount = amount < 0 ? -amount : amount;
        if(stackUsed + amount > stackLen)
            stackLen = stackUsed = stackUsed + amount;
        else stackUsed += amount;
        if(stackLen > SGSConstants.MAX_STACK_LENGTH)
            throw new CompilerError("Max stack length exceded");
    }
    //public final void push() throws CompilerError { push(1); }
    
    private void pop(int amount) throws CompilerError
    {
        if(amount == 0)
            return;
        amount = amount < 0 ? -amount : amount;
        stackUsed -= amount;
        if(stackUsed < 0)
            throw new CompilerError("Stack under zero not valid.");
    }
    public final void pop() throws CompilerError { pop(1); }
    
    public final void modify(Opcode op) throws CompilerError
    {
        int mod = op.getStackPush() - op.getStackPop();
        if(mod < 0)
            pop(-mod);
        else if(mod > 0)
            push(mod);
    }
    public final void modifyInverse(Opcode op) throws CompilerError
    {
        int mod = op.getStackPush() - op.getStackPop();
        if(mod > 0)
            pop(mod);
        else if(mod < 0)
            push(-mod);
    }
    
    public final int allocateVariable() throws CompilerError
    {
        allocatedVars++;
        if(allocatedVars > SGSConstants.MAX_VARS)
            throw new CompilerError("Max local variables exceded");
        return allocatedVars - 1;
    }
    
    
    public final int getVariableCount() { return allocatedVars; }
    public final int getMaxStackLength() { return stackLen; }
    public final boolean isStackEmpty() { return stackUsed <= 0; }
}
