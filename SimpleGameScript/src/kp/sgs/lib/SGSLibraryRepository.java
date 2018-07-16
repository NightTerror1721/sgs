/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Asus
 */
public final class SGSLibraryRepository implements Iterable<SGSLibrary>
{
    private final HashMap<String, SGSLibrary> libs = new HashMap<>();
    
    public final void registerLibrary(SGSLibrary lib)
    {
        if(libs.containsKey(lib.getLibraryName()))
            throw new IllegalArgumentException("Library " + lib.getLibraryName() + " has already exists");
        libs.put(lib.getLibraryName(), lib);
    }
    
    public final SGSLibrary getLibrary(String name)
    {
        return libs.getOrDefault(name, null);
    }
    
    public final boolean hasLibrary(String name) { return libs.containsKey(name); }
    
    public final SGSLibrary removeLibrary(String name)
    {
        return libs.remove(name);
    }

    @Override
    public final Iterator<SGSLibrary> iterator() { return libs.values().iterator(); }
}
