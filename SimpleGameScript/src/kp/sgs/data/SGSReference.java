/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import static kp.sgs.data.SGSValue.FALSE;
import static kp.sgs.data.SGSValue.TRUE;
import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public abstract class SGSReference extends SGSValue
{
    @Override
    public final int getDataType() { return Type.REFERENCE; }
    
    @Override
    public final int toInt() { return superHashCode(); }

    @Override
    public final long toLong() { return superHashCode(); }

    @Override
    public final float toFloat() { return superHashCode(); }

    @Override
    public final double toDouble() { return superHashCode(); }

    @Override
    public final boolean toBoolean() { return true; }

    @Override
    public final String toString() { return "pointer::" + Integer.toHexString(superHashCode()); }

    @Override
    public final SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public final SGSObject toObject() { return SGSObject.of(true, this); }
    
    
    /* Comparison operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return this == value ? TRUE : FALSE; }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return this != value ? TRUE : FALSE; }
    @Override public final SGSValue operatorGreater(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorGreater"); }
    @Override public final SGSValue operatorSmaller(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorSmaller"); }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorGreaterEquals"); }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorSmallerEquals"); }
    @Override public final SGSValue operatorNegate() { return FALSE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(toString().concat(value.toString())); }
    @Override public final int      operatorLength() { return 1; }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorPlus"); }
    @Override public final SGSValue operatorMinus(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorMinus"); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorMultiply"); }
    @Override public final SGSValue operatorDivide(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorDivide"); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorRemainder"); }
    @Override public final SGSValue operatorIncrease() { throw new UnsupportedOperationException("Pointer cannot use operatorIncrease"); }
    @Override public final SGSValue operatorDecrease() { throw new UnsupportedOperationException("Pointer cannot use operatorDecrease"); }
    @Override public final SGSValue operatorNegative() { throw new UnsupportedOperationException("Pointer cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorShiftLeft"); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorShiftRight"); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorLogicAnd"); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorLogicOr"); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorLogicXor"); }
    @Override public final SGSValue operatorBitwiseNot() { throw new UnsupportedOperationException("Pointer cannot use operatorLogicNot"); }
    
    
    /* Array operators */
    @Override public final SGSValue operatorGet(SGSValue index) { throw new UnsupportedOperationException("Pointer cannot use operatorGet"); }
    @Override public final void     operatorSet(SGSValue index, SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorSet"); }
    
    
    /* Object operators */
    @Override public final SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("Pointer cannot use operatorGetProperty"); }
    @Override public final void     operatorSetProperty(String name, SGSValue value) { throw new UnsupportedOperationException("Pointer cannot use operatorSetProperty"); }
    @Override public final SGSValue operatorCall(SGSValue[] args) { throw new UnsupportedOperationException("Pointer cannot use operatorCall"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return SGSIterator.scalarIterator(this); }
    
    
    /* Pointer operators */
    @Override public abstract SGSValue operatorReferenceGet();
    @Override public abstract void     operatorReferenceSet(SGSValue value);
    
    
    @Override
    public final boolean equals(Object o) { return o == this; }

    @Override
    public final int hashCode() { return superHashCode(); }

    @Override
    public final SGSImmutableValue operatorConst() { throw new UnsupportedOperationException("Pointer cannot use operatorConst"); }
}

