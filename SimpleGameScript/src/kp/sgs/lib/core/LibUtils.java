/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.util.HashMap;
import kp.sgs.data.SGSMutableObject;
import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public final class LibUtils
{
    private LibUtils() {}
    
    public static final String DEFAULT_SELF_PROPERTY_NAME = "__SELF";
    
    public static final <T extends SGSValue> T self(String selfPropertyName, SGSValue[] args)
    {
        return (T) args[0].operatorGetProperty(selfPropertyName);
    }
    
    public static final <T extends SGSValue> T self(SGSValue[] args)
    {
        return (T) args[0].operatorGetProperty(DEFAULT_SELF_PROPERTY_NAME);
    }
    
    public static final SGSMutableObject createLibraryClassInstance(SGSValue base, String selfPropertyName, SGSValue selfObject)
    {
        HashMap<String, SGSValue> map = new HashMap<>();
        map.put(selfPropertyName, selfObject);
        return new SGSMutableObject(map, base);
    }
    
    public static final SGSMutableObject createLibraryClassInstance(SGSValue base, SGSValue selfObject)
    {
        HashMap<String, SGSValue> map = new HashMap<>();
        map.put(DEFAULT_SELF_PROPERTY_NAME, selfObject);
        return new SGSMutableObject(map, base);
    }
}
