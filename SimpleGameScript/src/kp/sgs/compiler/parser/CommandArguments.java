/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.util.Iterator;
import kp.sgs.compiler.exception.CompilerError;

/**
 *
 * @author Asus
 */
public class CommandArguments extends CodeFragment implements Iterable<CodeFragmentList>
{
    private final CodeFragmentList[] arguments;
    
    private CommandArguments(CodeFragmentList[] args)
    {
        if(args == null)
            throw new NullPointerException();
        this.arguments = args;
    }
    
    public final int getArgumentCount() { return arguments.length; }
    public final CodeFragmentList getArgument(int index) { return arguments[index]; }
    
    @Override
    public final Iterator<CodeFragmentList> iterator()
    {
        return new Iterator<CodeFragmentList>()
        {
            private int it = 0;
            @Override public final boolean hasNext() { return it < arguments.length; }
            @Override public final CodeFragmentList next() { return arguments[it++]; }
        };
    }
    
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.COMMAND_ARGUMENTS; }

    @Override
    public final boolean isValidOperand() { return false; }
    
    
    private static final CommandArguments EMPTY = new CommandArguments(new CodeFragmentList[0]);
    
    public static final CommandArguments valueOf(CodeFragmentList argsList) throws CompilerError
    {
        if(argsList.isEmpty())
            return EMPTY;
        CodeFragmentList[] args = argsList.split(Stopchar.SEMICOLON);
        return new CommandArguments(args);
    }
    
}
