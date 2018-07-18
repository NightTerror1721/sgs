/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.StatementCompiler;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Literal;
import kp.sgs.compiler.parser.Mutable;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;
import kp.sgs.compiler.parser.Stopchar;

/**
 *
 * @author Asus
 */
public class InstructionDeclaration extends Instruction
{
    private final Operation[] assignments;
    private final DataType type;
    
    private InstructionDeclaration(Operation[] assignments, DataType type)
    {
        this.assignments = Objects.requireNonNull(assignments);
        this.type = Objects.requireNonNull(type);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.DECLARATION; }
    
    public static final InstructionDeclaration create(CodeFragmentList list, DataType type) throws CompilerError
    {
        CodeFragmentList[] parts = list.split(Stopchar.COMMA);
        if(parts == null || (parts.length == 1 && parts[0].isEmpty()))
            throw new CompilerError("Invalid empty \"def\" declaration");
        
        Operation[] assignments = new Operation[parts.length];
        for(int i=0;i<parts.length;i++)
        {
            Operation op = StatementParser.tryParseAssignmentNewFunction(list);
            if(op == null)
            {
                Statement part = StatementParser.parse(list);
                if(!part.isOperation())
                    throw new CompilerError("Expected valid declaration but found: " + part);
                op = (Operation) part;
            }
            if(!op.isAssignment() && !op.isNewFunction())
                throw new CompilerError("Expected valid declaration but found: " + op);
            assignments[i] = op;
        }
        
        return new InstructionDeclaration(assignments, type);
    }

    @Override
    public void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        for(Operation assignment : assignments)
        {
            if(assignment.isAssignment())
            {
                Statement left = assignment.getOperand(0);
                if(!left.isIdentifier())
                    throw new CompilerError("Invalid left part of assignation operator in declaration. Required valid identifier, but found: " + left);
                Statement right = assignment.getOperand(1);
                if(right.isLiteral())
                    scope.createConstant(left.toString(), (Literal) right);
                else if(right.isMutable())
                {
                    Mutable m = (Mutable) right;
                    if(!m.isMutableLiteral())
                        throw new CompilerError("Invalid right part of assignation operator in constant declaration. Required valid literal, but found: " + right);
                    scope.createConstant(left.toString(), m.generateLiteral());
                }
                else throw new CompilerError("Invalid right part of assignation operator in constant declaration. Required valid literal, but found: " + right);
            }
            else if(assignment.isNewFunction())
            {
                functions.add(assignment);
            }
            else throw new IllegalStateException();
        }
    }

    @Override
    public void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        for(Operation assignment : assignments)
        {
            if(assignment.isAssignment())
            {
                Statement left = assignment.getOperand(0);
                if(!left.isIdentifier())
                    throw new CompilerError("Invalid left part of assignation operator in declaration. Required valid identifier, but found: " + left);
                scope.createLocalVariable(left.toString(), type);
                StatementCompiler.compile(scope, opcodes, left, false);
            }
            else if(assignment.isNewFunction())
            {
                StatementCompiler.compile(scope, opcodes, assignment, false);
            }
            else throw new IllegalStateException();
        }
    }

    
}
