/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import kp.sgs.lib.AbstractSGSLibrary;
import kp.sgs.lib.SGSLibrary;
import kp.sgs.lib.SGSLibraryElement;

/**
 *
 * @author Asus
 */
class DefaultCoreLibrary extends AbstractSGSLibrary
{
    DefaultCoreLibrary(String name, SGSLibraryElement... els) { super(name); initialPut(els); }
    
    private void initialPut(SGSLibraryElement... els)
    {
        for(SGSLibraryElement e : els)
        {
            SGSLibrary.linkElementToLibrary(e, this);
            elements.put(e.getElementName(), e);
        }
    }
}
