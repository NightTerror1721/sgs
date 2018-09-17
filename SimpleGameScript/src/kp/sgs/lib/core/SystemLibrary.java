/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import kp.sgs.compiler.parser.DataType;
import kp.sgs.data.SGSFloat;
import kp.sgs.data.SGSString;

/**
 *
 * @author Asus
 */
public class SystemLibrary extends DefaultCoreLibrary
{
    public static final SystemLibrary LIB = new SystemLibrary();
    
    private SystemLibrary()
    {
        super("system",
                Def.function("CurrentTimeMillis", DataType.FLOAT, (g, args) -> { return new SGSFloat(System.currentTimeMillis()); }),
                Def.function("CurrentUserDir", DataType.STRING, (g, args) -> { return new SGSString(System.getProperty("user.dir")); }),
                Def.function("SetGlobal", (g, args) -> { g.setGlobalValue(args[0].toString(), args[1]); }),
                Def.function("GetGlobal", (g, args) -> { return g.getGlobalValue(args[0].toString()); }),
                Def.function("SetProperty", (g, args) -> { args[0].operatorSetProperty(args[1].toString(), args[2]); }),
                Def.function("GetProperty", (g, args) -> { return args[0].operatorGetProperty(args[1].toString()); }));
    }
    
    
    
}
