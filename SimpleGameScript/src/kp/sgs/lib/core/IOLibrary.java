/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 *
 * @author Asus
 */
public final class IOLibrary extends DefaultCoreLibrary
{
    private static PrintWriter STDOUT = new PrintWriter(System.out);
    private static PrintWriter STDERR = new PrintWriter(System.err);
    private static InputStream STDIN = System.in;
    
    public static final IOLibrary LIB = new IOLibrary();
    
    private IOLibrary()
    {
        super("io",
                Def.function("print", (g, a) -> {
                    String text = a[0].toString();
                    System.arraycopy(a, 1, a, 0, a.length - 1);
                    STDOUT.format(text, a);
                    STDOUT.flush();
                })
        );
    }
    
    
}
