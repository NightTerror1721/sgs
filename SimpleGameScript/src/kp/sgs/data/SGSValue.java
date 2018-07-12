/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import kp.sgs.SGSConstants;
import kp.sgs.SGSGlobals;

/**
 *
 * @author Asus
 */
public abstract class SGSValue
{
    SGSValue() {}
    
    public abstract int getDataType();
    
    public final boolean isUndefined() { return getDataType() == Type.UNDEFINED; }
    public final boolean isInteger() { return getDataType() == Type.INTEGER; }
    public final boolean isFloat() { return getDataType() == Type.FLOAT; }
    public final boolean isString() { return getDataType() == Type.STRING; }
    public final boolean isFunction() { return getDataType() == Type.FUNCTION; }
    public final boolean isReference() { return getDataType() == Type.REFERENCE; }
    public final boolean isArray() { return getDataType() == Type.ARRAY; }
    public final boolean isObject() { return getDataType() == Type.OBJECT; }
    public final boolean isConstArray() { return getDataType() == Type.CONST_ARRAY; }
    public final boolean isConstObject() { return getDataType() == Type.CONST_OBJECT; }
    
    
    public abstract int toInt();
    public abstract long toLong();
    public abstract float toFloat();
    public abstract double toDouble();
    public abstract boolean toBoolean();
    @Override public abstract String toString();
    public abstract SGSArray toArray();
    public abstract SGSObject toObject();
    
    
    
    
    
    /* Common operators */
    public abstract SGSValue operatorEquals(SGSValue value);
    public abstract SGSValue operatorNotEquals(SGSValue value);
    public abstract SGSValue operatorGreater(SGSValue value);
    public abstract SGSValue operatorSmaller(SGSValue value);
    public abstract SGSValue operatorGreaterEquals(SGSValue value);
    public abstract SGSValue operatorSmallerEquals(SGSValue value);
    public abstract SGSValue operatorNegate();
    public abstract SGSValue operatorConcat(SGSValue value);
    public abstract int      operatorLength();
    public abstract SGSImmutableValue operatorConst();
    
    
    /* Math operators */
    public abstract SGSValue operatorPlus(SGSValue value);
    public abstract SGSValue operatorMinus(SGSValue value);
    public abstract SGSValue operatorMultiply(SGSValue value);
    public abstract SGSValue operatorDivide(SGSValue value);
    public abstract SGSValue operatorRemainder(SGSValue value);
    public abstract SGSValue operatorIncrease();
    public abstract SGSValue operatorDecrease();
    public abstract SGSValue operatorNegative();
    
    
    /* Bit operators */
    public abstract SGSValue operatorBitwiseShiftLeft(SGSValue value);
    public abstract SGSValue operatorBitwiseShiftRight(SGSValue value);
    public abstract SGSValue operatorBitwiseAnd(SGSValue value);
    public abstract SGSValue operatorBitwiseOr(SGSValue value);
    public abstract SGSValue operatorBitwiseXor(SGSValue value);
    public abstract SGSValue operatorBitwiseNot();
    
    
    /* Array operators */
    public abstract SGSValue operatorGet(SGSValue index);
    public abstract SGSValue operatorSet(SGSValue index, SGSValue value);
    public          SGSValue operatorGet(int index) { return operatorGet(new SGSInteger(index)); }
    public          SGSValue operatorSet(int index, SGSValue value) { return operatorSet(new SGSInteger(index), value); }
    
    
    /* Object operators */
    public abstract SGSValue operatorGetProperty(String name);
    public abstract SGSValue operatorSetProperty(String name, SGSValue value);
    public abstract SGSValue operatorCall(SGSGlobals globals, SGSValue[] args);
    public final    SGSValue operatorCall(SGSGlobals globals) { return operatorCall(globals, SGSConstants.EMPTY_ARGS); }
    
    
    /* Pointer operators */
    public abstract SGSValue operatorReferenceGet();
    public abstract SGSValue operatorReferenceSet(SGSValue value);
    
    
    /* Iterator operators */
    public abstract SGSValue operatorIterator();
    
    
    
    /* Java Functions */
    @Override public abstract boolean equals(Object o);
    @Override public abstract int hashCode();
    
    protected final int superHashCode() { return super.hashCode(); }
    
    public final SGSValue operatorTypedEquals(SGSValue value)
    {
        if(value.getDataType() != value.getDataType())
            return FALSE;
        return operatorEquals(value);
    }
    public final SGSValue operatorTypedNotEquals(SGSValue value)
    {
        if(value.getDataType() != value.getDataType())
            return TRUE;
        return operatorNotEquals(value);
    }
    
    
    
    /* Immutable Constant values */
    public static final SGSImmutableValue ZERO = new SGSInteger(0);
    public static final SGSImmutableValue ONE = new SGSInteger(1);
    public static final SGSImmutableValue MINUSONE = new SGSInteger(-1);
    public static final SGSImmutableValue TRUE = ONE;
    public static final SGSImmutableValue FALSE = ZERO;
    public static final SGSImmutableValue EMPTY_ARRAY = new SGSImmutableArray(new SGSValue[0]);
    public static final SGSImmutableValue EMPTY_OBJECT = new SGSImmutableObject(Collections.emptyMap());
    public static final SGSImmutableValue UNDEFINED = new SGSUndefined();
    
    
    
    /* Value Of */
    public static final SGSValue valueOf(SGSValue value) { return value; }
    
    public static final SGSValue valueOf(byte value) { return new SGSInteger(value); }
    public static final SGSValue valueOf(short value) { return new SGSInteger(value); }
    public static final SGSValue valueOf(int value) { return new SGSInteger(value); }
    public static final SGSValue valueOf(long value) { return new SGSFloat(value); }
    public static final SGSValue valueOf(float value) { return new SGSFloat(value); }
    public static final SGSValue valueOf(double value) { return new SGSFloat(value); }
    public static final SGSValue valueOf(boolean value) { return value ? TRUE : FALSE; }
    public static final SGSValue valueOf(char value) { return new SGSString(String.valueOf(value)); }
    
    public static final SGSValue valueOf(Byte value) { return new SGSInteger(value); }
    public static final SGSValue valueOf(Short value) { return new SGSInteger(value); }
    public static final SGSValue valueOf(Integer value) { return new SGSInteger(value); }
    public static final SGSValue valueOf(Long value) { return new SGSFloat(value); }
    public static final SGSValue valueOf(Float value) { return new SGSFloat(value); }
    public static final SGSValue valueOf(Double value) { return new SGSFloat(value); }
    public static final SGSValue valueOf(Boolean value) { return value ? TRUE : FALSE; }
    public static final SGSValue valueOf(Character value) { return new SGSString(value.toString()); }
    
    public static final SGSValue valueOf(String value) { return new SGSString(value); }
    
    public static final SGSValue valueOf(boolean immutable, SGSValue[] value) { return immutable ? new SGSImmutableArray(value) : new SGSMutableArray(value); }
    public static final SGSValue valueOf(boolean immutable, byte[] value)
    {
        SGSValue[] array = new SGSValue[value.length];
        for(int i=0;i<value.length;i++)
            array[i] = new SGSInteger(value[i]);
        return valueOf(immutable, array);
    }
    public static final SGSValue valueOf(boolean immutable, short[] value)
    {
        SGSValue[] array = new SGSValue[value.length];
        for(int i=0;i<value.length;i++)
            array[i] = new SGSInteger(value[i]);
        return valueOf(immutable, array);
    }
    public static final SGSValue valueOf(boolean immutable, int[] value)
    {
        return valueOf(immutable, Arrays.stream(value).mapToObj(SGSInteger::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, long[] value)
    {
        return valueOf(immutable, Arrays.stream(value).mapToObj(SGSFloat::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, float[] value)
    {
        SGSValue[] array = new SGSValue[value.length];
        for(int i=0;i<value.length;i++)
            array[i] = new SGSFloat(value[i]);
        return valueOf(immutable, array);
    }
    public static final SGSValue valueOf(boolean immutable, double[] value)
    {
        return valueOf(immutable, Arrays.stream(value).mapToObj(SGSFloat::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, boolean[] value)
    {
        SGSValue[] array = new SGSValue[value.length];
        for(int i=0;i<value.length;i++)
            array[i] = value[i] ? TRUE : FALSE;
        return valueOf(immutable, array);
    }
    public static final SGSValue valueOf(boolean immutable, char[] value)
    {
        SGSValue[] array = new SGSValue[value.length];
        for(int i=0;i<value.length;i++)
            array[i] = new SGSString(String.valueOf(value[i]));
        return valueOf(immutable, array);
    }
    
    public static final SGSValue valueOf(boolean immutable, Byte[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSInteger::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, Short[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSInteger::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, Integer[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSInteger::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, Long[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSFloat::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, Float[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSFloat::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, Double[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSFloat::new).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, Boolean[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSValue::valueOf).toArray(SGSValue[]::new));
    }
    public static final SGSValue valueOf(boolean immutable, Character[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSValue::valueOf).toArray(SGSValue[]::new));
    }
    
    public static final SGSValue valueOf(boolean immutable, String[] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSString::new).toArray(SGSValue[]::new));
    }
    
    public static final SGSValue valueOf(boolean immutable, SGSValue[][] value)
    {
        return valueOf(immutable, Arrays.stream(value).map(SGSMutableArray::new).toArray(SGSValue[]::new));
    }
    
    public static final SGSValue valueOf(boolean immutable, Collection<SGSValue> value)
    {
        return valueOf(immutable, value.stream().toArray(SGSValue[]::new));
    }
    
    public static final SGSValue valueOf(SGSArray value) { return (SGSValue) value; }
    
    public static final SGSValue valueOf(boolean immutable, Map<String, SGSValue> value) { return immutable ? new SGSImmutableObject(value) : new SGSMutableObject(value); }
    
    public static final SGSValue valueOf(SGSObject value) { return (SGSValue) value; }
    
    
    public interface Type
    {
        int UNDEFINED       = 0x0;
        int INTEGER         = 0x1;
        int FLOAT           = 0x2;
        int STRING          = 0x3;
        int FUNCTION        = 0x4;
        int REFERENCE         = 0x5;
        int ARRAY           = 0x6;
        int OBJECT          = 0x7;
        int CONST_ARRAY     = 0x8;
        int CONST_OBJECT    = 0x9;
        int USERDATA        = 0xA;
        
        interface MutableType
        {
            int REFERENCE   = Type.REFERENCE;
            int ARRAY       = Type.ARRAY;
            int OBJECT      = Type.OBJECT;
            int USERDATA    = Type.USERDATA;
        }
        
        interface ImmutableType
        {
            int UNDEFINED       = Type.UNDEFINED;
            int INTEGER         = Type.INTEGER;
            int FLOAT           = Type.FLOAT;
            int STRING          = Type.STRING;
            int FUNCTION        = Type.FUNCTION;
            int CONST_ARRAY     = Type.CONST_ARRAY;
            int CONST_OBJECT    = Type.CONST_OBJECT;
        }
    }
    
    private static int LAST_VALID_TYPE = Type.USERDATA + 1;
    public static final int generateTypeId() { return LAST_VALID_TYPE++; }
}
