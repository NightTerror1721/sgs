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
import kp.sgs.compiler.parser.Identifier;
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
    private final DeclarationEntry[] assignments;
    private final DataType type;
    private final DeclarationType decType;
    
    private InstructionDeclaration(DeclarationEntry[] assignments, DataType type, DeclarationType decType)
    {
        this.assignments = Objects.requireNonNull(assignments);
        this.type = Objects.requireNonNull(type);
        this.decType = Objects.requireNonNull(decType);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.DECLARATION; }
    
    public static final InstructionDeclaration create(CodeFragmentList list, DataType type, DeclarationType decType) throws CompilerError
    {
        CodeFragmentList[] parts = list.split(Stopchar.COMMA);
        if(parts == null || (parts.length == 1 && parts[0].isEmpty()))
            throw new CompilerError("Invalid empty \"def\" declaration");
        
        DeclarationEntry[] assignments = new DeclarationEntry[parts.length];
        for(int i=0;i<parts.length;i++)
        {
            if(parts[i].length() == 1 && parts[i].get(0).isIdentifier())
            {
                assignments[i] = new IdentifierEntry((Identifier) parts[i].get(0));
            }
            else
            {
                Operation op = StatementParser.tryParseAssignmentNewFunction(parts[i]);
                if(op == null)
                {
                    Statement part = StatementParser.parse(list);
                    if(!part.isOperation())
                        throw new CompilerError("Expected valid declaration but found: " + part);
                    op = (Operation) part;
                }
                if(!op.isAssignment() && !op.isNewFunction())
                    throw new CompilerError("Expected valid declaration but found: " + op);
                assignments[i] = new OperationEntry(op);
            }
        }
        
        return new InstructionDeclaration(assignments, type, decType);
    }

    @Override
    public void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        for(DeclarationEntry entry : assignments)
        {
            if(entry.isIdentifier())
            {
                if(decType != DeclarationType.GLOBAL)
                    throw new CompilerError("Cannot declare uninitiated constants");
                Identifier identifier = entry.getIdentifier();
                scope.createGlobalVariable(identifier.toString());
            }
            else
            {
                if(decType == DeclarationType.GLOBAL)
                    throw new CompilerError("Cannot assign any value to global variable in constant time");
                Operation assignment = entry.getOperation();
                if(assignment.isAssignment())
                {
                    compileConstantDeclaration(scope, assignment);
                }
                else if(assignment.isNewFunction())
                {
                    functions.add(assignment);
                }
                else throw new IllegalStateException();
            }
        }
    }
    
    private static void compileConstantDeclaration(NamespaceScope scope, Operation assignment) throws CompilerError
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

    @Override
    public void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        for(DeclarationEntry entry : assignments)
        {
            if(entry.isIdentifier())
            {
                Identifier identifier = entry.getIdentifier();
                switch(decType)
                {
                    default: throw new IllegalStateException();
                    case NORMAL: scope.createLocalVariable(identifier.toString(), type); break;
                    case GLOBAL: scope.createGlobalVariable(identifier.toString()); break;
                    case CONSTANT: throw new CompilerError("Cannot declare uninitiated constants");
                }
            }
            else
            {
                Operation assignment = entry.getOperation();
                if(assignment.isAssignment())
                {
                    if(decType == DeclarationType.CONSTANT)
                        compileConstantDeclaration(scope, assignment);
                    else
                    {
                        Statement left = assignment.getOperand(0);
                        if(!left.isIdentifier())
                            throw new CompilerError("Invalid left part of assignation operator in declaration. Required valid identifier, but found: " + left);
                        if(decType == DeclarationType.GLOBAL)
                            scope.createGlobalVariable(left.toString());
                        else scope.createLocalVariable(left.toString(), type);
                        StatementCompiler.compile(scope, opcodes, assignment, false);
                    }
                }
                else if(assignment.isNewFunction())
                {
                    if(decType != DeclarationType.NORMAL)
                        throw new CompilerError("Cannot declarate functions in global or constant statement");
                    StatementCompiler.compile(scope, opcodes, assignment, false);
                }
                else throw new IllegalStateException();
            }
        }
    }
    
    private static abstract class DeclarationEntry
    {
        public abstract boolean isIdentifier();
        
        public Identifier getIdentifier() { throw new UnsupportedOperationException(); }
        public Operation getOperation() { throw new UnsupportedOperationException(); }
    }
    
    private static final class IdentifierEntry extends DeclarationEntry
    {
        private final Identifier id;
        
        private IdentifierEntry(Identifier id) { this.id = id; }
        
        @Override public final boolean isIdentifier() { return true; }
        
        @Override public final Identifier getIdentifier() { return id; }
    }
    
    private static final class OperationEntry extends DeclarationEntry
    {
        private final Operation op;
        
        private OperationEntry(Operation op) { this.op = op; }
        
        @Override public final boolean isIdentifier() { return false; }
        
        @Override public final Operation getOperation() { return op; }
    }
    
    public enum DeclarationType { NORMAL, GLOBAL, CONSTANT }
}
