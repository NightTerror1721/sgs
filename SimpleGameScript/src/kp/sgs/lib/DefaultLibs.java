/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib;

import kp.sgs.lib.core.ArrayLibrary;
import kp.sgs.lib.core.IOLibrary;
import kp.sgs.lib.core.ListLibrary;
import kp.sgs.lib.core.MapLibrary;
import kp.sgs.lib.core.StringLibrary;
import kp.sgs.lib.core.SystemLibrary;

/**
 *
 * @author Asus
 */
public final class DefaultLibs
{
    public static final SGSLibrary SYSTEM = SystemLibrary.LIB;
    public static final SGSLibrary IO = IOLibrary.LIB;
    public static final SGSLibrary STRINGS = StringLibrary.LIB;
    public static final SGSLibrary ARRAYS = ArrayLibrary.LIB;
    public static final SGSLibrary LIST = ListLibrary.LIB;
    public static final SGSLibrary MAP = MapLibrary.LIB;
    
    public static final void registerAllDefaultLibs(SGSLibraryRepository repository)
    {
        repository.registerLibrary(SYSTEM);
        repository.registerLibrary(IO);
        repository.registerLibrary(STRINGS);
        repository.registerLibrary(ARRAYS);
        repository.registerLibrary(LIST);
        repository.registerLibrary(MAP);
    }
}
