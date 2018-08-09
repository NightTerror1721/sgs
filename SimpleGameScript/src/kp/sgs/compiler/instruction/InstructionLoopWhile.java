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
    
    @Override
    public final void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        throw new CompilerError("Cannot compile while loop in constant mode");
    }

    @Override
    public final void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        OpcodeLocation loopStart = opcodes.getBottomLocation();
        OpcodeLocation condFalse = StatementCompiler.compileDefaultIf(scope, opcodes, condition);
        NamespaceScope child = scope.createChildScope(NamespaceScopeType.LOOP);
        StatementCompiler.compileScope(child, opcodes, action);
        opcodes.append(Opcodes.goTo(loopStart));
        opcodes.setJumpOpcodeLocationToBottom(condFalse);
        child.setBreakPointLocations(opcodes, opcodes.getBottomLocation());
        child.setContinuePointLocations(opcodes, loopStart);
    }
}
