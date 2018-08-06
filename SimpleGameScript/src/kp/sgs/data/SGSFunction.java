/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import static kp.sgs.data.SGSValue.FALSE;
import static kp.sgs.data.SGSValue.TRUE;

/**
 *
 * @author Asus
 */
public abstract class SGSFunction extends SGSImmutableValue
{
    @Override
    public final int getDataType() { return Type.FUNCTION; }

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
    public final String toString() { return "function::" + superHashCode(); }

    @Override
    public final SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public final SGSObject toObject() { return SGSObject.of(true, this); }
    
    
    /* Comparison operators */
    @Override public SGSValue operatorEquals(SGSValue value) { return this == value ? TRUE : FALSE; }
    @Override public SGSValue operatorNotEquals(SGSValue value) { return this != value ? TRUE : FALSE; }
    @Override public final SGSValue operatorGreater(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorGreater"); }
    @Override public final SGSValue operatorSmaller(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorSmaller"); }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorGreaterEquals"); }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorSmallerEquals"); }
    @Override public final SGSValue operatorNegate() { return TRUE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(toString().concat(value.toString())); }
    @Override public final int      operatorLength() { return 1; }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorPlus"); }
    @Override public final SGSValue operatorMinus(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorMinus"); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorMultiply"); }
    @Override public final SGSValue operatorDivide(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorDivide"); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorRemainder"); }
    @Override public final SGSValue operatorIncrease() { throw new UnsupportedOperationException("Function cannot use operatorIncrease"); }
    @Override public final SGSValue operatorDecrease() { throw new UnsupportedOperationException("Function cannot use operatorDecrease"); }
    @Override public final SGSValue operatorNegative() { throw new UnsupportedOperationException("Function cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorShiftLeft"); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorShiftRight"); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorLogicAnd"); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorLogicOr"); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { throw new UnsupportedOperationException("Function cannot use operatorLogicXor"); }
    @Override public final SGSValue operatorBitwiseNot() { throw new UnsupportedOperationException("Function cannot use operatorLogicNot"); }
    

    /* Array operators */
    @Override public SGSValue operatorGet(SGSValue index) { throw new UnsupportedOperationException("Function cannot use operatorGet"); }
    
    
    /* Object operators */
    @Override public SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("Function cannot use operatorGetProperty"); }
    
    
    @Override public abstract SGSValue operatorCall(SGSValue[] args);
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Function cannot use operatorPointerGet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return this; }

    @Override
    public boolean equals(Object o) { return o == this || o instanceof SGSFunction; }

    @Override
    public int hashCode() { return superHashCode(); }
}
