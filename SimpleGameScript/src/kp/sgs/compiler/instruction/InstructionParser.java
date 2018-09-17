/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.exception.ErrorList;
import kp.sgs.compiler.instruction.InstructionDeclaration.DeclarationType;
import kp.sgs.compiler.parser.CodeFragment;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.CodeParser;
import kp.sgs.compiler.parser.CodeReader;
import kp.sgs.compiler.parser.Command;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Scope;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;

/**
 *
 * @author Asus
 */
public final class InstructionParser
{
    private InstructionParser() {}
    
    public static final List<Instruction> parse(CodeReader source, ErrorList errors, boolean singleInstruction)
    {
        if(!source.hasNext())
            return Collections.emptyList();
        
        CodeParser codeParser = new CodeParser(null);
        LinkedList<Instruction> instructions = new LinkedList<>();
        Instruction last = null;
        while(source.hasNext())
        {
            if(singleInstruction && last != null)
                break;
            last = parseInstruction(source, codeParser, last, errors);
            if(last == null)
                continue;
            instructions.add(last);
        }
        return instructions;
    }
    
    private static Instruction parseInstruction(CodeReader source, CodeParser code, Instruction last, ErrorList errors)
    {
        int startLine = source.getCurrentLine();
        try
        {
            code.setLast(null);
            CodeFragment first = code.parseFragment(source, true, errors);
            if(first == null)
                return null;

            if(first.isCommand()) // Commands
            {
                switch(((Command) first).getCommandId())
                {
                    default: throw new IllegalStateException();
                    case DEF: return InstructionDeclaration.create(code.parseUntilScopeOrInlineAsList(source, Command.DEF, errors), DataType.ANY, DeclarationType.NORMAL);
                    case GLOBAL: return InstructionDeclaration.create(code.parseUntilScopeOrInlineAsList(source, Command.DEF, errors), DataType.ANY, DeclarationType.GLOBAL);
                    case CONST: return InstructionDeclaration.create(code.parseUntilScopeOrInlineAsList(source, Command.DEF, errors), DataType.ANY, DeclarationType.CONSTANT);
                    case INCLUDE: return InstructionInclusion.create(code.parseInlineInstructionAsList(source, Command.INCLUDE, errors));
                    case IMPORT: return InstructionImportation.create(code.parseInlineInstructionAsList(source, Command.IMPORT, errors));
                    case IF: return InstructionCondition.create(code.parseCommandArgsAndScope(source, Command.IF, errors));
                    case ELSE: {
                        if(last == null || last.getInstructionId() != InstructionId.CONDITION)
                            throw new CompilerError("Expected \"if\" command before \"else\" command.");
                        ((InstructionCondition) last).setElseAction(code.parseCommandScope(source, Command.ELSE, errors));
                        return null;
                    }
                    case WHILE: return InstructionLoopWhile.create(code.parseCommandArgsAndScope(source, Command.WHILE, errors));
                    case FOR: return InstructionLoopFor.create(code.parseCommandArgsAndScope(source, Command.FOR, errors));
                    case BREAK:
                        code.findEmptyInlineInstruction(source, Command.BREAK, errors);
                        return InstructionJumpPoint.create(true);
                    case CONTINUE:
                        code.findEmptyInlineInstruction(source, Command.CONTINUE, errors);
                        return InstructionJumpPoint.create(false);
                    case RETURN: return InstructionReturn.create(code.parseInlineInstructionAsList(source, Command.RETURN, errors));
                }
            }
            else if(first.isScope()) // Multi-Statement
            {
                Scope scope = (Scope) first;
                return new InstructionScope(scope);
            }
            else if(first.isDataType()) // <type> <identifier>
            {
                DataType type = (DataType) first;
                return InstructionDeclaration.create(code.parseUntilScopeOrInlineAsList(source, Command.DEF, errors), type, DeclarationType.NORMAL);
            }
            else // Statement
            {
                Statement statement = code.parseInlineInstruction(source, errors, first);
                return new InstructionStatement(statement);
            }
        }
        catch(CompilerError ex)
        {
            int endLine = source.getCurrentLine();
            errors.addError(startLine, endLine, ex);
            return null;
        }
    }
    
    public static final Instruction parseDeclarationInstructions(CodeFragmentList codeList) throws CompilerError
    {
        CodeFragment first = codeList.get(0);
        if(first.isDataType())
        {
            DataType type = (DataType) first;
            return InstructionDeclaration.create(codeList.subList(1), type, DeclarationType.NORMAL);
        }
        if(first.isCommand())
        {
            if(first == Command.DEF)
                return InstructionDeclaration.create(codeList.subList(1), DataType.ANY, DeclarationType.NORMAL);
            if(first == Command.GLOBAL)
                return InstructionDeclaration.create(codeList.subList(1), DataType.ANY, DeclarationType.GLOBAL);
            if(first == Command.CONST)
                return InstructionDeclaration.create(codeList.subList(1), DataType.ANY, DeclarationType.CONSTANT);
        }
        return new InstructionStatement(StatementParser.parse(codeList));
    }
    
    /*private static Scope createSingleInstructionScope(CodeFragmentList list) throws CompilerError
    {
        if(list.isEmpty())
            return Scope.EMPTY_SCOPE;
        CodeFragment first = list.get(0);
        if(first == null)
            return Scope.EMPTY_SCOPE;

        if(first.isCommand()) // Commands
        {
            switch(((Command) first).getCommandId())
            {
                default: throw new IllegalStateException();
                //case DEF: return InstructionDeclaration.create(list.subList(1), DataType.ANY, false);
                //case GLOBAL: return InstructionDeclaration.create(list.subList(1), DataType.ANY, true);
                //case INCLUDE: return InstructionInclusion.create(list.subList(1));
                //case IMPORT: return InstructionImportation.create(list.subList(1));
                case IF: return InstructionCondition.create(code.parseUntilScopeOrInlineAsList(source, Command.IF, errors));
                case ELSE: {
                    if(last == null || last.getInstructionId() != InstructionId.CONDITION)
                        throw new CompilerError("Expected \"if\" command before \"else\" command.");
                    ((InstructionCondition) last).setElseAction(code.parseUntilScopeOrInlineAsList(source, Command.ELSE, errors));
                    return null;
                }
                case WHILE: return InstructionLoopWhile.create(code.parseUntilScopeOrInlineAsList(source, Command.WHILE, errors));
                case FOR: return InstructionLoopFor.create(code.parseUntilScopeOrInlineAsList(source, Command.FOR, errors));
                case BREAK:
                    code.findEmptyInlineInstruction(source, Command.BREAK, errors);
                    return InstructionJumpPoint.create(true);
                case CONTINUE:
                    code.findEmptyInlineInstruction(source, Command.CONTINUE, errors);
                    return InstructionJumpPoint.create(false);
                case RETURN: return InstructionReturn.create(code.parseInlineInstructionAsList(source, Command.RETURN, errors));
            }
        }
        else if(first.isScope()) // Multi-Statement
        {
            Scope scope = (Scope) first;
            return new Scope(new InstructionScope(scope));
        }
        else if(first.isDataType()) // <type> <identifier>
        {
            DataType type = (DataType) first;
            return new Scope(InstructionDeclaration.create(list.subList(1), type, false));
        }
        else // Statement
        {
            Statement statement = StatementParser.parse(list);
            return new Scope(new InstructionStatement(statement));
        }
    }*/
}
