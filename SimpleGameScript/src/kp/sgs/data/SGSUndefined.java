/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Iterator;
import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public final class SGSUndefined extends SGSImmutableValue
{
    SGSUndefined() {}
    
    @Override
    public final int getDataType() { return Type.UNDEFINED; }

    @Override
    public final int toInt() { throw new UnsupportedOperationException("Undefined cannot use toJavaInt"); }

    @Override
    public final long toLong() { throw new UnsupportedOperationException("Undefined cannot use toJavaLong"); }

    @Override
    public final float toFloat() { throw new UnsupportedOperationException("Undefined cannot use toJavaFloat"); }

    @Override
    public final double toDouble() { throw new UnsupportedOperationException("Undefined cannot use toJavaDouble"); }

    @Override
    public final boolean toBoolean() { return false; }

    @Override
    public final String toString() { return "undefined"; }

    @Override
    public final SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public final SGSObject toObject() { return SGSObject.of(true, this); }
    
    
    /* Comparison operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return FALSE; }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return TRUE; }
    @Override public final SGSValue operatorGreater(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorGreater"); }
    @Override public final SGSValue operatorSmaller(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorSmaller"); }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorGreaterEquals"); }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorSmallerEquals"); }
    @Override public final SGSValue operatorNegate() { return TRUE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString("undefined".concat(value.toString())); }
    @Override public final int      operatorLength() { return 0; }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorPlus"); }
    @Override public final SGSValue operatorMinus(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorMinus"); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorMultiply"); }
    @Override public final SGSValue operatorDivide(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorDivide"); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorRemainder"); }
    @Override public final SGSValue operatorIncrease() { throw new UnsupportedOperationException("Undefined cannot use operatorIncrease"); }
    @Override public final SGSValue operatorDecrease() { throw new UnsupportedOperationException("Undefined cannot use operatorDecrease"); }
    @Override public final SGSValue operatorNegative() { throw new UnsupportedOperationException("Undefined cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorShiftLeft"); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorShiftRight"); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorLogicAnd"); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorLogicOr"); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { throw new UnsupportedOperationException("Undefined cannot use operatorLogicXor"); }
    @Override public final SGSValue operatorBitwiseNot() { throw new UnsupportedOperationException("Undefined cannot use operatorLogicNot"); }
    

    /* Array operators */
    @Override public SGSValue operatorGet(SGSValue index) { throw new UnsupportedOperationException("Undefined cannot use operatorGet"); }
    
    
    /* Object operators */
    @Override public SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("Undefined cannot use operatorGetProperty"); }
    @Override public SGSValue operatorCall(SGSValue[] args) { throw new UnsupportedOperationException("Undefined cannot use operatorCall"); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Undefined cannot use operatorPointerGet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator()
    {
        return new SGSIterator(new Iterator<SGSValue>()
        {
            @Override public boolean hasNext() { return false; }
            @Override public SGSValue next() { return SGSUndefined.this; }
        });
    }

    @Override
    public final boolean equals(Object o) { return o == this || o instanceof SGSUndefined; }

    @Override
    public final int hashCode() { return 0; }
    
    
}
