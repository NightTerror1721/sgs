/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Asus
 */
public final class SGSImmutableObject extends SGSImmutableValue implements SGSObject
{
    private final SGSMutableObject object;
    
    public SGSImmutableObject(Map<String, ? extends SGSValue> properties)
    {
        if(properties == null)
            throw new NullPointerException();
        this.object = new SGSMutableObject(Collections.unmodifiableMap(properties));
    }
    
    @Override
    public final int getDataType() { return Type.CONST_OBJECT; }

    @Override
    public final int toInt() { return object.toInt(); }

    @Override
    public final long toLong() { return object.toLong(); }

    @Override
    public final float toFloat() { return object.toFloat(); }

    @Override
    public final double toDouble() { return object.toDouble(); }

    @Override
    public final boolean toBoolean() { return object.toBoolean(); }

    @Override
    public final String toString() { return object.toString(); }

    @Override
    public final SGSArray toArray() { return object.toArray(); }
    
    @Override
    public final SGSObject toObject() { return this; }
    
    
    /* Comparison operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return object.operatorEquals(value); }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return object.operatorNotEquals(value); }
    @Override public final SGSValue operatorGreater(SGSValue value) { return object.operatorGreater(value); }
    @Override public final SGSValue operatorSmaller(SGSValue value) { return object.operatorSmaller(value); }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { return object.operatorGreaterEquals(value); }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { return object.operatorSmallerEquals(value); }
    @Override public final SGSValue operatorNegate() { return object.operatorNegate(); }
    @Override public final SGSValue operatorConcat(SGSValue value) { return object.operatorConcat(value); }
    @Override public final int      operatorLength() { return object.operatorLength(); }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { return object.operatorPlus(value); }
    @Override public final SGSValue operatorMinus(SGSValue value) { return object.operatorMinus(value); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { return object.operatorMultiply(value); }
    @Override public final SGSValue operatorDivide(SGSValue value) { return object.operatorDivide(value); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { return object.operatorRemainder(value); }
    @Override public final SGSValue operatorIncrease() { return object.operatorIncrease(); }
    @Override public final SGSValue operatorDecrease() { return object.operatorDecrease(); }
    @Override public final SGSValue operatorNegative() { return object.operatorNegative(); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { return object.operatorBitwiseShiftLeft(value); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { return object.operatorBitwiseShiftRight(value); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { return object.operatorBitwiseAnd(value); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { return object.operatorBitwiseOr(value); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { return object.operatorBitwiseXor(value); }
    @Override public final SGSValue operatorBitwiseNot() { return object.operatorBitwiseNot(); }
    

    /* Array operators */
    
    @Override public final SGSValue operatorGet(SGSValue index) { return object.operatorGet(index); }
    
    
    /* Object operators */
    @Override public final SGSValue operatorGetProperty(String name) { return object.operatorGetProperty(name); }
    @Override public final SGSValue operatorCall(SGSValue[] args) { return object.operatorCall(args); }
    @Override public final void     constructor(SGSValue object, SGSValue[] args) { object.constructor(object); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Object cannot use operatorPointerGet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return object.operatorIterator(); }
    
    @Override
    public final boolean equals(Object o)
    {
        return o instanceof SGSImmutableObject && object.equals(((SGSImmutableObject) o).object);
    }

    @Override
    public final int hashCode() { return object.hashCode(); }
    

    @Override
    public final boolean isMutable() { return false; }

    @Override
    public final int objectSize() { return object.objectSize(); }
    
    @Override
    public final boolean objectIsEmpty() { return object.objectIsEmpty(); }
    
    @Override
    public final boolean objectHasProperty(String name) { return object.objectHasProperty(name); }

    @Override
    public final SGSValue objectGetProperty(String name) { return object.objectGetProperty(name); }

    @Override
    public final SGSValue objectSetProperty(String name, SGSValue value)
    {
        throw new UnsupportedOperationException("Cannot modify properties in const object.");
    }
    
    @Override
    public final Map<String, SGSValue> map() { return object.map(); }
    
    @Override
    public final SGSValue toSGSValue() { return this; }

    @Override
    public final Iterator<Map.Entry<String, SGSValue>> iterator() { return object.iterator(); }
    
    @Override
    public final SGSValue getGlobalValue(String name) { return object.getGlobalValue(name); }

    @Override
    public final SGSValue setGlobalValue(String name, SGSValue value)
    {
        throw new UnsupportedOperationException("const Object cannot support modify global values");
    }

    @Override
    public final boolean hasGlobalValue(String name) { return object.hasGlobalValue(name); }

    @Override
    public final SGSValue objectGetBase() { return object.objectGetBase(); }
}
