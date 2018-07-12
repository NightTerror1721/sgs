/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.Objects;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.parser.CodeFragment;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.CommandArguments;
import kp.sgs.compiler.parser.Scope;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;

/**
 *
 * @author Asus
 */
public class InstructionLoopWhile extends Instruction
{
    private final Statement condition;
    private final Statement action;
    
    private InstructionLoopWhile(Statement condition, Statement action)
    {
        this.condition = Objects.requireNonNull(condition);
        this.action = Objects.requireNonNull(action);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.LOOP_WHILE; }
    
    public static final InstructionLoopWhile create(CodeFragmentList list) throws CompilerError
    {
        if(list.length() < 2)
            throw new CompilerError("Malformed \"while\" command. Expected valid statement and scope after command.");
        Statement condition, action;
        CodeFragment frag;
        
        frag = list.get(0);
        if(!frag.isCommandArguments())
            throw new CompilerError("Malformed \"while\" command. Expected valid statement and scope after command. But found: " + frag);
        CommandArguments args = (CommandArguments) frag;
        if(args.getArgumentCount() != 1)
            throw new CompilerError("Malformed \"while\" command. Expected valid statement after command. But found: " + args);
        condition = StatementParser.parse(args.getArgument(0));
        
        frag = list.get(1);
        action = frag.isScope() ? (Scope) frag : StatementParser.parse(list.subList(1));
        
        return new InstructionLoopWhile(condition, action);
    }
    
}
