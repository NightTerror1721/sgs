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
public final class Command extends CodeFragment
{
    private final CommandId id;
    
    private Command(CommandId id)
    {
        if(id == null)
            throw new NullPointerException();
        this.id = id;
    }
    
    public final CommandId getCommandId() { return id; }
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.COMMAND; }

    @Override
    public final boolean isValidOperand() { return false; }
    
    
    public static final Command
            DEF = new Command(CommandId.DEF),
            INCLUDE = new Command(CommandId.INCLUDE),
            IF = new Command(CommandId.IF),
            ELSE = new Command(CommandId.ELSE),
            FOR = new Command(CommandId.FOR),
            WHILE = new Command(CommandId.WHILE),
            RETURN = new Command(CommandId.RETURN);
    
}
