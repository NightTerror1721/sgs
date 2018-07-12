/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import kp.sgs.SGSGlobals;
import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public class SGSUserdata extends SGSValue
{
    public SGSUserdata() {}
    
    @Override
    public int getDataType() { return Type.OBJECT; }

    @Override
    public int toInt() { throw new UnsupportedOperationException("Object cannot use toJavaInt"); }

    @Override
    public long toLong() { throw new UnsupportedOperationException("Object cannot use toJavaLong"); }

    @Override
    public float toFloat() { throw new UnsupportedOperationException("Object cannot use toJavaFloat"); }

    @Override
    public double toDouble() { throw new UnsupportedOperationException("Object cannot use toJavaDouble"); }

    @Override
    public boolean toBoolean() { return true; }

    @Override
    public String toString() { return "object::" + Integer.toHexString(superHashCode()); }

    @Override
    public SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public SGSObject toObject() { return SGSObject.of(true, this); }
    
    
    /* Comparison operators */
    @Override public SGSValue operatorEquals(SGSValue value) { return this == value ? TRUE : FALSE; }
    @Override public SGSValue operatorNotEquals(SGSValue value) { return this != value ? TRUE : FALSE; }
    @Override public SGSValue operatorGreater(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorGreater"); }
    @Override public SGSValue operatorSmaller(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorSmaller"); }
    @Override public SGSValue operatorGreaterEquals(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorGreaterEquals"); }
    @Override public SGSValue operatorSmallerEquals(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorSmallerEquals"); }
    @Override public SGSValue operatorNegate() { return FALSE; }
    @Override public SGSValue operatorConcat(SGSValue value) { return new SGSString(toString().concat(value.toString())); }
    @Override public int      operatorLength() { return 1; }
    
    
    /* Math operators */
    @Override public SGSValue operatorPlus(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorPlus"); }
    @Override public SGSValue operatorMinus(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorMinus"); }
    @Override public SGSValue operatorMultiply(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorMultiply"); }
    @Override public SGSValue operatorDivide(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorDivide"); }
    @Override public SGSValue operatorRemainder(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorRemainder"); }
    @Override public SGSValue operatorIncrease() { throw new UnsupportedOperationException("Object cannot use operatorIncrease"); }
    @Override public SGSValue operatorDecrease() { throw new UnsupportedOperationException("Object cannot use operatorDecrease"); }
    @Override public SGSValue operatorNegative() { throw new UnsupportedOperationException("Object cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public SGSValue operatorBitwiseShiftLeft(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorShiftLeft"); }
    @Override public SGSValue operatorBitwiseShiftRight(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorShiftRight"); }
    @Override public SGSValue operatorBitwiseAnd(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorLogicAnd"); }
    @Override public SGSValue operatorBitwiseOr(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorLogicOr"); }
    @Override public SGSValue operatorBitwiseXor(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorLogicXor"); }
    @Override public SGSValue operatorBitwiseNot() { throw new UnsupportedOperationException("Object cannot use operatorLogicNot"); }
    
    
    /* Array operators */
    @Override public SGSValue operatorGet(SGSValue index) { throw new UnsupportedOperationException("Object cannot use operatorGet"); }
    @Override public SGSValue operatorSet(SGSValue index, SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorSet"); }
    
    
    /* Object operators */
    @Override public SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("Object cannot use operatorGetProperty"); }
    @Override public SGSValue operatorSetProperty(String name, SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorSetProperty"); }
    @Override public SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { throw new UnsupportedOperationException("Object cannot use operatorCall"); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Userdata cannot use operatorPointerGet"); }
    @Override public final SGSValue operatorReferenceSet(SGSValue value) { throw new UnsupportedOperationException("Userdata cannot use operatorPointerSet"); }
    
    
    /* Iterator operators */
    @Override public SGSValue operatorIterator() { return SGSIterator.scalarIterator(this); }
    
    
    @Override
    public boolean equals(Object o) { return o == this; }

    @Override
    public int hashCode() { return superHashCode(); }

    @Override
    public final SGSImmutableValue operatorConst() { throw new UnsupportedOperationException("Userdefined cannot use operatorConst"); }
}
