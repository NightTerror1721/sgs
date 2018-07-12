/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

/**
 *
 * @author Asus
 */
public abstract class CodeFragment
{
    public abstract CodeFragmentType getFragmentType();
    public abstract boolean isValidOperand();
    
    public final boolean is(CodeFragmentType type0, CodeFragmentType type1)
    {
        CodeFragmentType c = getFragmentType();
        return c == type0 || c == type1;
    }
    public final boolean is(CodeFragmentType type0, CodeFragmentType type1, CodeFragmentType type2)
    {
        CodeFragmentType c = getFragmentType();
        return c == type0 || c == type1 || c == type2;
    }
    public final boolean is(CodeFragmentType... types)
    {
        CodeFragmentType c = getFragmentType();
        for(int i=0;i<types.length;i++)
            if(c == types[i])
                return true;
        return false;
    }
    
    public boolean isStatement() { return false; }
    
    public final boolean isIdentifier() { return getFragmentType() == CodeFragmentType.IDENTIFIER; }
    public final boolean isLiteral() { return getFragmentType() == CodeFragmentType.LITERAL; }
    public final boolean isMutable() { return getFragmentType() == CodeFragmentType.MUTABLE; }
    public final boolean isOperator() { return getFragmentType() == CodeFragmentType.OPERATOR; }
    public final boolean isOperation() { return getFragmentType() == CodeFragmentType.OPERATION; }
    public final boolean isStopchar() { return getFragmentType() == CodeFragmentType.STOPCHAR; }
    public final boolean isArguments() { return getFragmentType() == CodeFragmentType.ARGUMENTS; }
    public final boolean isScope() { return getFragmentType() == CodeFragmentType.SCOPE; }
    public final boolean isDataType() { return getFragmentType() == CodeFragmentType.DATA_TYPE; }
    public final boolean isCommandArguments() { return getFragmentType() == CodeFragmentType.COMMAND_ARGUMENTS; }
    public final boolean isCommand() { return getFragmentType() == CodeFragmentType.COMMAND; }
}
