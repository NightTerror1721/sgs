/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kp.sgs.SGSGlobals;

/**
 *
 * @author Asus
 */
public interface SGSObject extends Iterable<Map.Entry<String, SGSValue>>, SGSGlobals
{
    boolean isMutable();
    
    int objectSize();
    
    boolean objectIsEmpty();
    
    boolean objectHasProperty(String name);
    
    SGSValue objectGetProperty(String name);
    
    SGSValue objectSetProperty(String name, SGSValue value);
    
    SGSValue objectGetBase();
    
    SGSValue toSGSValue();
    
    Map<String, SGSValue> map();
    
    static SGSObject of(boolean immutable, SGSValue scalar)
    {
        HashMap<String, SGSValue> map = new HashMap<>();
        map.put("scalar", scalar);
        return immutable ? new SGSImmutableObject(map) : new SGSMutableObject(map);
    }
    
    static SGSObject of(boolean immutable, SGSValue... values)
    {
        HashMap<String, SGSValue> map = new HashMap<>();
        for(int i=0;i<values.length;i++)
            map.put(String.valueOf(i), values[i]);
        return immutable ? new SGSImmutableObject(map) : new SGSMutableObject(map);
    }
    
    static SGSObject of(boolean immutable, Collection<SGSValue> values)
    {
        HashMap<String, SGSValue> map = new HashMap<>();
        int count = 0;
        for(SGSValue value : values)
            map.put(String.valueOf(count++), value);
        return immutable ? new SGSImmutableObject(map) : new SGSMutableObject(map);
    }
    
    static SGSObject of(boolean immutable, SGSArray array)
    {
        HashMap<String, SGSValue> map = new HashMap<>();
        int count = 0;
        for(SGSValue value : array)
            map.put(String.valueOf(count++), value);
        return immutable ? new SGSImmutableObject(map) : new SGSMutableObject(map);
    }
    
    static SGSObject of(boolean immutable, Map<String, SGSValue> map)
    {
        if(map == null)
            throw new NullPointerException();
        return immutable ? new SGSImmutableObject(map) : new SGSMutableObject(map);
    }
}
