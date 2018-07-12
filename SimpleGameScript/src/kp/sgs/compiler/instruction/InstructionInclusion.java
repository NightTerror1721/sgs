/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.Objects;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.Literal;
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
}
