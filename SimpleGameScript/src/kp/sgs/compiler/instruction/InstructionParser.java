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
    
    public static final List<Instruction> parse(CodeReader source, ErrorList errors)
    {
        if(!source.hasNext())
            return Collections.emptyList();
        
        CodeParser codeParser = new CodeParser(null);
        LinkedList<Instruction> instructions = new LinkedList<>();
        Instruction last = null;
        while(source.hasNext())
        {
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
                    case DEF: return InstructionDeclaration.create(code.parseUntilScopeOrInlineAsList(source, Command.DEF, errors), DataType.ANY, false);
                    case GLOBAL: return InstructionDeclaration.create(code.parseUntilScopeOrInlineAsList(source, Command.DEF, errors), DataType.ANY, true);
                    case INCLUDE: return InstructionInclusion.create(code.parseInlineInstructionAsList(source, Command.INCLUDE, errors));
                    case IMPORT: return InstructionImportation.create(code.parseInlineInstructionAsList(source, Command.IMPORT, errors));
                    case IF: return InstructionCondition.create(code.parseUntilScopeOrInlineAsList(source, Command.IF, errors));
                    case ELSE: {
                        if(last == null || last.getInstructionId() != InstructionId.CONDITION)
                            throw new CompilerError("Expected \"if\" command before \"else\" command.");
                        ((InstructionCondition) last).setElseAction(code.parseUntilScopeOrInlineAsList(source, Command.ELSE, errors));
                        return null;
                    }
                    case WHILE: return InstructionLoopWhile.create(code.parseUntilScopeOrInlineAsList(source, Command.WHILE, errors));
                    case FOR: return InstructionLoopFor.create(code.parseUntilScopeOrInlineAsList(source, Command.FOR, errors));
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
                return InstructionDeclaration.create(code.parseUntilScopeOrInlineAsList(source, Command.DEF, errors), type, false);
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
            return InstructionDeclaration.create(codeList, type, false);
        }
        if(first.isCommand())
        {
            if(first == Command.DEF)
                return InstructionDeclaration.create(codeList, DataType.ANY, false);
            if(first == Command.GLOBAL)
                return InstructionDeclaration.create(codeList, DataType.ANY, true);
        }
        return new InstructionStatement(StatementParser.parse(codeList));
    }
}
