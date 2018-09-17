/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;

/**
 *
 * @author Asus
 */
public final class InstructionImportation extends Instruction
{
    private final String libname;
    
    private InstructionImportation(String libname) { this.libname = libname; }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.IMPORTATION; }
    
    public static final InstructionImportation create(CodeFragmentList list) throws CompilerError
    {
        Statement statement = StatementParser.parse(list);
        if(statement.isIdentifier() || statement.isLiteral())
            return new InstructionImportation(statement.toString());
        throw new CompilerError("Expected valid identifier or string literal for import command. But found: " + statement);
    }

    @Override
    public void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
    {
        scope.importLibrary(libname);
    }

    @Override
    public void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
    {
        scope.importLibrary(libname);
        //throw new CompilerError("Cannot compile \"import\" command in function source code");
    }
    
}
