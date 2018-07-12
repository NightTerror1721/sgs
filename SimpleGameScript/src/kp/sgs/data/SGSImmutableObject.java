/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import kp.sgs.SGSGlobals;
import static kp.sgs.data.SGSValue.FALSE;
import static kp.sgs.data.SGSValue.TRUE;
import static kp.sgs.data.SGSValue.UNDEFINED;
import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public final class SGSImmutableObject extends SGSImmutableValue implements SGSObject
{
    private final Map<String, SGSValue> properties;
    
    public SGSImmutableObject(Map<String, ? extends SGSValue> properties)
    {
        if(properties == null)
            throw new NullPointerException();
        this.properties = Collections.unmodifiableMap(properties);
    }
    
    @Override
    public final int getDataType() { return Type.CONST_OBJECT; }

    @Override
    public final int toInt() { return properties.size(); }

    @Override
    public final long toLong() { return properties.size(); }

    @Override
    public final float toFloat() { return properties.size(); }

    @Override
    public final double toDouble() { return properties.size(); }

    @Override
    public final boolean toBoolean() { return !properties.isEmpty(); }

    @Override
    public final String toString() { return SGSMutableObject.objectToString(this); }

    @Override
    public final SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public final SGSObject toObject() { return this; }
    
    
    /* Comparison operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return properties.equals(value.toObject().map()) ? TRUE : FALSE; }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return properties.equals(value.toObject().map()) ? FALSE : TRUE; }
    @Override public final SGSValue operatorGreater(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorGreater"); }
    @Override public final SGSValue operatorSmaller(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorSmaller"); }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorGreaterEquals"); }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorSmallerEquals"); }
    @Override public final SGSValue operatorNegate() { return properties.isEmpty() ? TRUE : FALSE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(toString().concat(value.toString())); }
    @Override public final int      operatorLength() { return properties.size(); }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorPlus"); }
    @Override public final SGSValue operatorMinus(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorMinus"); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorMultiply"); }
    @Override public final SGSValue operatorDivide(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorDivide"); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorRemainder"); }
    @Override public final SGSValue operatorIncrease() { throw new UnsupportedOperationException("Object cannot use operatorIncrease"); }
    @Override public final SGSValue operatorDecrease() { throw new UnsupportedOperationException("Object cannot use operatorDecrease"); }
    @Override public final SGSValue operatorNegative() { throw new UnsupportedOperationException("Object cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorShiftLeft"); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorShiftRight"); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorLogicAnd"); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorLogicOr"); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorLogicXor"); }
    @Override public final SGSValue operatorBitwiseNot() { throw new UnsupportedOperationException("Object cannot use operatorLogicNot"); }
    

    /* Array operators */
    @Override public SGSValue operatorGet(SGSValue index) { return properties.getOrDefault(index.toString(), UNDEFINED); }
    
    
    /* Object operators */
    @Override public SGSValue operatorGetProperty(String name) { return properties.getOrDefault(name, UNDEFINED); }
    @Override public SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { throw new UnsupportedOperationException("Object cannot use operatorCall"); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Object cannot use operatorPointerGet"); }
    @Override public final SGSValue operatorReferenceSet(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorPointerSet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return new SGSIterator(properties.values()); }
    
    @Override
    public final boolean equals(Object o)
    {
        return o instanceof SGSImmutableObject && properties.equals(((SGSImmutableObject) o).properties);
    }

    @Override
    public final int hashCode() { return properties.hashCode(); }
    

    @Override
    public final boolean isMutable() { return false; }

    @Override
    public final int objectSize() { return properties.size(); }
    
    @Override
    public final boolean objectIsEmpty() { return properties.isEmpty(); }
    
    @Override
    public final boolean objectHasProperty(String name) { return properties.containsKey(name); }

    @Override
    public final SGSValue objectGetProperty(String name) { return properties.getOrDefault(name, UNDEFINED); }

    @Override
    public final SGSValue objectSetProperty(String name, SGSValue value)
    {
        if(value == null)
            throw new NullPointerException();
        properties.put(name, value);
        return value;
    }
    
    @Override
    public final Map<String, SGSValue> map() { return properties; }
    
    @Override
    public final SGSValue toSGSValue() { return this; }

    @Override
    public final Iterator<Map.Entry<String, SGSValue>> iterator() { return properties.entrySet().iterator(); }
}
