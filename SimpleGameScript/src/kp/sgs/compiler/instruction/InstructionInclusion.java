/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.ScriptBuilder;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.Literal;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;

/**
 *
 * @author Asus
 */
public final class InstructionInclusion extends Instruction
{
    private final String path;
    
    private InstructionInclusion(String path)
    {
        this.path = Objects.requireNonNull(path);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.INCLUSION; }
    
    public static final InstructionInclusion create(CodeFragmentList list) throws CompilerError
    {
        Statement stat = StatementParser.parse(list);
        if(!stat.isLiteral())
            throw new CompilerError("Expected valid string literal for \"include\" command.");
        return new InstructionInclusion(((Literal) stat).getSGSValue().toString());
    }
    
    @Override
    public final void compileConstantPart(ScriptBuilder.NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates
    }

    @Override
    public final void compileFunctionPart(ScriptBuilder.NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
