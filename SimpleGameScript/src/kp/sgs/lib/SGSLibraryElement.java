/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib;

import kp.sgs.SGSConstants;
import kp.sgs.SGSGlobals;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSMutableObject;
import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public abstract class SGSLibraryElement
{
    final String name;
    SGSLibrary lib;
    
    public SGSLibraryElement(String name)
    {
        if(name == null)
            throw new NullPointerException();
        if(name.isEmpty())
            throw new IllegalArgumentException("Library name cannot be empty");
        this.name = name;
    }
    
    public final SGSLibrary getLibrary() { return lib; }
    public final String getElementName() { return name; }
    
    public abstract DataType getValueType();
    public abstract DataType getReturnType();
    
    public abstract SGSValue toSGSValue(SGSGlobals globals);
    
    public abstract SGSValue operatorGet(SGSValue index);
    public          SGSValue operatorGet(int index) { return operatorGet(new SGSInteger(index)); }
    
    public abstract SGSValue operatorGetProperty(String name);
    
    public abstract SGSValue operatorCall(SGSGlobals globals, SGSValue[] args);
    public          SGSValue operatorCall(SGSGlobals globals) { return operatorCall(globals, SGSConstants.EMPTY_ARGS); }
    
    public abstract SGSValue operatorReferenceGet();
    
    public abstract SGSMutableObject constructor(SGSValue[] args);
}
