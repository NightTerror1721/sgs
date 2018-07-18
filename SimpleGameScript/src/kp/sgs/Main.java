/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs;

import java.io.File;
import java.io.IOException;
import kp.sgs.compiler.CompilerProperties;
import kp.sgs.compiler.SGSCompiler;
import kp.sgs.compiler.exception.CompilerException;
import kp.sgs.compiler.opcode.OpcodeParser;
import kp.sgs.lib.SGSLibraryRepository;

/**
 *
 * @author Asus
 */
public final class Main
{
    public static void main(String[] args) throws CompilerException, IOException
    {
        CompilerProperties props = new CompilerProperties();
        SGSLibraryRepository rep = new SGSLibraryRepository();
        props.setLibraryRepository(rep);
        
        SGSScript script = SGSCompiler.compile(new File("test.sgs"), props);
        OpcodeParser.parseTo(script, new File("opcodes.txt"));
    }
}
