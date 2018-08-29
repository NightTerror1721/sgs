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
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.exception.CompilerException;
import kp.sgs.compiler.opcode.OpcodeParser;
import kp.sgs.data.SGSMutableObject;
import kp.sgs.lib.DefaultLibs;
import kp.sgs.lib.SGSLibraryRepository;

/**
 *
 * @author Asus
 */
public final class Main
{
    public static void main(String[] args) throws CompilerException, IOException, CompilerError
    {
        CompilerProperties props = new CompilerProperties();
        SGSLibraryRepository rep = new SGSLibraryRepository();
        rep.registerLibrary(DefaultLibs.IO);
        props.setLibraryRepository(rep);
        props.addDirectories(System.getProperty("user.dir"));
        
        SGSScript script = SGSCompiler.compile(new File("libtests.sgs"), new SGSMutableObject(), props);
        OpcodeParser.parseTo(script, new File("opcodes.txt"));
        
        double t1 = System.nanoTime();
        script.execute();
        double t2 = System.nanoTime();
        System.out.println("Executed in " + ((t2 - t1) / 1000000) + " ms.");
        
        t1 = System.nanoTime();
        script.execute();
        t2 = System.nanoTime();
        System.out.println("Executed in " + ((t2 - t1) / 1000000) + " ms.");
    }
}
