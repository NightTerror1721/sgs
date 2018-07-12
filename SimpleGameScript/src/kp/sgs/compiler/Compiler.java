/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import java.io.InputStream;
import java.util.List;
import kp.sgs.SGSScript;
import kp.sgs.compiler.exception.CompilerException;
import kp.sgs.compiler.exception.ErrorList;
import kp.sgs.compiler.parser.CodeParser;
import kp.sgs.compiler.parser.CodeReader;
import kp.sgs.compiler.instruction.Instruction;
import kp.sgs.compiler.instruction.InstructionParser;

/**
 *
 * @author Asus
 */
public final class Compiler
{
    private Compiler() {}
    
    public static final SGSScript compile(InputStream input, CompilerProperties props) throws CompilerException
    {
        return compile(new CodeReader(input), props);
    }
    
    private static SGSScript compile(CodeReader source, CompilerProperties props) throws CompilerException
    {
        ScriptBuilder builder = new ScriptBuilder(props);
        CodeParser codeParser = new CodeParser();
        ErrorList errors = new ErrorList();
        List<Instruction> insts = InstructionParser.parse(source, codeParser, errors);
        if(errors.hasErrors())
            throw new CompilerException(errors);
    }
}
