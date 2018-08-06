/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public final class SGSFloat extends SGSImmutableValue
{
    public final double value;
    
    public SGSFloat(double value) { this.value = value; }
    
    @Override
    public final int getDataType() { return Type.FLOAT; }

    @Override
    public final int toInt() { return (int) value; }

    @Override
    public final long toLong() { return (long) value; }

    @Override
    public final float toFloat() { return (float) value; }

    @Override
    public final double toDouble() { return value; }

    @Override
    public final boolean toBoolean() { return value != 0d; }

    @Override
    public final String toString() { return String.valueOf(value); }

    @Override
    public final SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public final SGSObject toObject() { return SGSObject.of(true, this); }
    
    
    
    /* Common operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return this.value == value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return this.value != value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorGreater(SGSValue value) { return this.value > value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorSmaller(SGSValue value) { return this.value < value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { return this.value >= value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { return this.value <= value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorNegate() { return this.value != 0 ? TRUE : FALSE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(String.valueOf(this.value).concat(value.toString())); }
    @Override public final int      operatorLength() { return 1; }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { return new SGSFloat(this.value + value.toDouble()); }
    @Override public final SGSValue operatorMinus(SGSValue value) { return new SGSFloat(this.value - value.toDouble()); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { return new SGSFloat(this.value * value.toDouble()); }
    @Override public final SGSValue operatorDivide(SGSValue value) { return new SGSFloat(this.value / value.toDouble()); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { return new SGSInteger((int) (this.value % value.toDouble())); }
    @Override public final SGSValue operatorIncrease() { return new SGSInteger((int) this.value + 1); }
    @Override public final SGSValue operatorDecrease() { return new SGSInteger((int) this.value - 1); }
    @Override public final SGSValue operatorNegative() { return new SGSFloat(-this.value); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { return new SGSInteger((int) this.value << value.toInt()); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { return new SGSInteger((int) this.value >>> value.toInt()); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { return new SGSInteger((int) this.value & value.toInt()); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { return new SGSInteger((int) this.value | value.toInt()); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { return new SGSInteger((int) this.value ^ value.toInt()); }
    @Override public final SGSValue operatorBitwiseNot() { return new SGSInteger(~((int) this.value)); }
    
    
    /* Array operators */
    @Override public final SGSValue operatorGet(SGSValue index) { throw new UnsupportedOperationException("Float cannot use operatorGet"); }
    
    
    /* Object operators */
    @Override public final SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("Float cannot use operatorGetProperty"); }
    @Override public final SGSValue operatorCall(SGSValue[] args) { throw new UnsupportedOperationException("Float cannot use operatorCall"); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Float cannot use operatorPointerGet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return SGSIterator.scalarIterator(this); }
    
    
    @Override
    public final boolean equals(Object o)
    {
        return o instanceof SGSImmutableValue && value == ((SGSImmutableValue) o).toDouble();
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + ((int) this.value);
        return hash;
    }
}
