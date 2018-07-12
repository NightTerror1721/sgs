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
public class Arguments extends Statement implements Iterable<Statement>
{
    private final Statement[] arguments;
    
    private Arguments(Statement[] args)
    {
        if(args == null)
            throw new NullPointerException();
        this.arguments = args;
    }
    
    public final int getArgumentCount() { return arguments.length; }
    public final Statement getArgument(int index) { return arguments[index]; }
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.ARGUMENTS; }

    @Override
    public final Iterator<Statement> iterator()
    {
        return new Iterator<Statement>()
        {
            private int it = 0;
            @Override public final boolean hasNext() { return it < arguments.length; }
            @Override public final Statement next() { return arguments[it++]; }
        };
    }
    
    
    private static final Arguments EMPTY = new Arguments(new Statement[0]);
    
    public static final Arguments valueOf() { return EMPTY; }
    public static final Arguments valueOf(Statement... args) { return new Arguments(args); }
    
    public static final Arguments valueOf(CodeFragmentList argsList) throws CompilerError
    {
        if(argsList.isEmpty())
            return EMPTY;
        CodeFragmentList[] uargs = argsList.split(Stopchar.COMMA);
        Statement[] args = new Statement[uargs.length];
        for(int i = 0; i < args.length; i++)
            args[i] = StatementParser.parse(uargs[i]);
        return new Arguments(args);
    }
}
