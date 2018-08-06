/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Objects;
import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public final class SGSString extends SGSImmutableValue
{
    public final String value;
    
    public SGSString(String value)
    {
        if(value == null)
            throw new NullPointerException();
        this.value = value;
    }
    
    @Override
    public final int getDataType() { return Type.STRING; }

    @Override
    public final int toInt() { return Integer.decode(value); }

    @Override
    public final long toLong() { return Long.decode(value); }

    @Override
    public final float toFloat() { return Float.parseFloat(value); }

    @Override
    public final double toDouble() { return Double.parseDouble(value); }

    @Override
    public final boolean toBoolean() { return !value.isEmpty(); }

    @Override
    public final String toString() { return value; }

    @Override
    public final SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public final SGSObject toObject() { return SGSObject.of(true, this); }
    
    
    /* Comparison operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return this.value.equals(value.toString()) ? TRUE : FALSE; }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return this.value.equals(value.toString()) ? FALSE : TRUE; }
    @Override public final SGSValue operatorGreater(SGSValue value) { return this.value.compareTo(value.toString()) > 0 ? TRUE : FALSE; }
    @Override public final SGSValue operatorSmaller(SGSValue value) { return this.value.compareTo(value.toString()) < 0 ? TRUE : FALSE; }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { return this.value.compareTo(value.toString()) >= 0 ? TRUE : FALSE; }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { return this.value.compareTo(value.toString()) <= 0 ? TRUE : FALSE; }
    @Override public final SGSValue operatorNegate() { return this.value.isEmpty() ? TRUE : FALSE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(this.value.concat(value.toString())); }
    @Override public final int      operatorLength() { return value.length(); }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorPlus"); }
    @Override public final SGSValue operatorMinus(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorMinus"); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorMultiply"); }
    @Override public final SGSValue operatorDivide(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorDivide"); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorRemainder"); }
    @Override public final SGSValue operatorIncrease() { throw new UnsupportedOperationException("String cannot use operatorIncrease"); }
    @Override public final SGSValue operatorDecrease() { throw new UnsupportedOperationException("String cannot use operatorDecrease"); }
    @Override public final SGSValue operatorNegative() { throw new UnsupportedOperationException("String cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorShiftLeft"); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorShiftRight"); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorLogicAnd"); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorLogicOr"); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { throw new UnsupportedOperationException("String cannot use operatorLogicXor"); }
    @Override public final SGSValue operatorBitwiseNot() { throw new UnsupportedOperationException("String cannot use operatorLogicNot"); }
    
    
    /* Array operators */
    @Override public final SGSValue operatorGet(SGSValue index) { return new SGSInteger(value.charAt(index.toInt())); }
    
    
    /* Object operators */
    @Override public final SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("String cannot use operatorGetProperty"); }
    @Override public final SGSValue operatorCall(SGSValue[] args) { throw new UnsupportedOperationException("String cannot use operatorCall"); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("String cannot use operatorPointerGet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return SGSIterator.scalarIterator(this); }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof SGSString && value.equals(((SGSString) o).value);
    }

    @Override
    public final int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
