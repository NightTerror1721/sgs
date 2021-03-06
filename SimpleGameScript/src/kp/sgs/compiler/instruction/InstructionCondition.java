/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.ScriptBuilder.NamespaceScopeType;
import kp.sgs.compiler.StatementCompiler;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.opcode.OpcodeList.OpcodeLocation;
import kp.sgs.compiler.opcode.Opcodes;
import kp.sgs.compiler.parser.CodeFragment;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.CommandArguments;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Scope;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;

/**
 *
 * @author Asus
 */
public final class InstructionCondition extends Instruction
{
    private final Statement condition;
    private final Statement action;
    private Statement elseAction;
    
    private InstructionCondition(Statement condition, Statement action)
    {
        this.condition = Objects.requireNonNull(condition);
        this.action = Objects.requireNonNull(action);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.CONDITION; }
    
    public final void setElseAction(CodeFragmentList list) throws CompilerError
    {
        if(elseAction != null)
            throw new IllegalStateException();
        if(list.isEmpty())
            throw new CompilerError("Malformed \"else\" command. Expected valid statement after \"if\" command.");
        CodeFragment frag = list.get(0);
        this.elseAction = frag.isScope() ? (Scope) frag : StatementParser.parse(list);
    }
    
    public static final InstructionCondition create(CodeFragmentList list) throws CompilerError
    {
        if(list.length() < 2)
            throw new CompilerError("Malformed \"if\" command. Expected valid statement and scope after command.");
        Statement condition, action;
        CodeFragment frag;
        
        frag = list.get(0);
        if(!frag.isCommandArguments())
            throw new CompilerError("Malformed \"if\" command. Expected valid statement and scope after command. But found: " + frag);
        CommandArguments args = (CommandArguments) frag;
        if(args.getArgumentCount() != 1)
            throw new CompilerError("Malformed \"if\" command. Expected valid statement after command. But found: " + args);
        condition = StatementParser.parse(args.getArgument(0));
        
        frag = list.get(1);
        action = frag.isScope() ? (Scope) frag : StatementParser.parse(list.subList(1));
        
        return new InstructionCondition(condition, action);
    }

    @Override
    public final void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        throw new CompilerError("Cannot compile conditionals in constant mode");
    }

    @Override
    public final void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        OpcodeLocation elseLoc = StatementCompiler.compileDefaultIf(scope, opcodes, condition);
        StatementCompiler.compileScope(scope.createChildScope(NamespaceScopeType.NORMAL), opcodes, action);
        if(elseAction != null)
        {
            OpcodeLocation endIfLoc = opcodes.append(Opcodes.goTo());
            opcodes.setJumpOpcodeLocationToBottom(elseLoc);
            StatementCompiler.compileScope(scope.createChildScope(NamespaceScopeType.NORMAL), opcodes, elseAction);
            opcodes.setJumpOpcodeLocationToBottom(endIfLoc);
        }
        else opcodes.setJumpOpcodeLocationToBottom(elseLoc);
    }
}
