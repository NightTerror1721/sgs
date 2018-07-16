/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib;

/**
 *
 * @author Asus
 */
public class DefaultSGSLibrary extends AbstractSGSLibrary
{
    public DefaultSGSLibrary(String name)
    {
        super(name);
    }
    
    public final DefaultSGSLibrary putLibraryElement(SGSLibraryElement e)
    {
        SGSLibrary.linkElementToLibrary(e, this);
        elements.put(e.name, e);
        return this;
    }
    
    public final SGSLibraryElement removeLibraryElement(String name)
    {
        SGSLibraryElement e = elements.remove(name);
        if(e != null)
            SGSLibrary.unlinkElementToLibrary(e);
        return e;
    }
}
