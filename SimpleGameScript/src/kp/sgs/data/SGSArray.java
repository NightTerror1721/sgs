/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Asus
 */
public interface SGSArray extends Iterable<SGSValue>
{
    boolean isMutable();
    
    int arrayLength();
    
    SGSValue arrayGet(int index);
    
    SGSValue arraySet(int index, SGSValue value);
    
    SGSValue[] array();
    
    SGSValue toSGSValue();
    
    static SGSArray of(boolean immutable, SGSValue... values)
    {
        return immutable ? new SGSImmutableArray(values) : new SGSMutableArray(values);
    }
    
    static SGSArray of(boolean immutable, Collection<SGSValue> values)
    {
        return immutable
                ? new SGSImmutableArray(values.toArray(new SGSValue[values.size()]))
                : new SGSMutableArray(values.toArray(new SGSValue[values.size()]));
    }
    
    static SGSArray of(boolean immutable, Map<String, SGSValue> map)
    {
        SGSValue[] array = new SGSValue[map.size()];
        int count = 0;
        for(SGSValue value : map.values())
            array[count++] = value;
        return immutable ? new SGSImmutableArray(array) : new SGSMutableArray(array);
    }
    
    static SGSArray of(boolean immutable, SGSObject object)
    {
        SGSValue[] array = new SGSValue[object.objectSize()];
        int count = 0;
        for(Map.Entry<String, SGSValue> e : object)
            array[count++] = e.getValue();
        return immutable ? new SGSImmutableArray(array) : new SGSMutableArray(array);
    }
}
