/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.util.Map;
import java.util.regex.Pattern;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.data.SGSFloat;
import kp.sgs.data.SGSImmutableArray;
import kp.sgs.data.SGSImmutableObject;
import kp.sgs.data.SGSImmutableValue;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSString;
import kp.sgs.data.SGSValue;
import kp.sgs.data.SGSValue.Type;
import kp.sgs.data.SGSValue.Type.ImmutableType;

/**
 *
 * @author Asus
 */
public class Literal extends Statement
{
    public static final Literal UNDEFINED = new Literal(SGSValue.UNDEFINED);
    public static final Literal ZERO = new Literal(SGSValue.ZERO);
    public static final Literal ONE = new Literal(SGSValue.ONE);
    public static final Literal MINUSONE = new Literal(SGSValue.MINUSONE);
    public static final Literal TRUE = new Literal(SGSValue.TRUE);
    public static final Literal FALSE = new Literal(SGSValue.FALSE);
    
    private final SGSImmutableValue value;
    
    private Literal(SGSImmutableValue value)
    {
        this.value = value;
    }
    
    public final Literal fixDecimals()
    {
        if(value.isFloat())
        {
            double num = value.toDouble();
            if(num == ((double)((int) num)))
                return valueOf((int) num);
        }
        return this;
    }
    
    public final SGSImmutableValue getSGSValue() { return value; }
    
    public final Literal operatorUnaryPlus() { return this; }
    public final Literal operatorUnaryMinus()
    {
        switch(value.getDataType())
        {
            case Type.INTEGER: return valueOf(-value.toInt());
            case Type.FLOAT: return valueOf(-value.toDouble());
            default: return this;
        }
    }
    public final Literal operatorNot() { return valueOf(value.toBoolean()); }
    public final Literal operatorBitwiseNot()
    {
        switch(value.getDataType())
        {
            case Type.INTEGER: return valueOf(~value.toInt());
            case Type.FLOAT: return valueOf(~value.toLong());
            default: return this;
        }
    }
    public final Literal operatorCastInt() { return valueOf(value.toInt()); }
    public final Literal operatorCastFloat() { return valueOf(value.toDouble()); }
    public final Literal operatorCastString() { return valueOf(value.toString()); }
    public final Literal operatorCastArray() { return valueOf(value.toArray().toSGSValue()); }
    public final Literal operatorCastObject() { return valueOf(value.toObject().toSGSValue()); }
    public final Literal operatorLength() { return valueOf(value.operatorLength()); }
    public final Literal operatorIsdef() { return valueOf(!value.isUndefined()); }
    public final Literal operatorIsundef() { return valueOf(value.isUndefined()); }
    public final Literal operatorTypeid() { return valueOf(value.getDataType()); }
    public final Literal operatorMultiply(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() * other.value.toInt());
                case Type.FLOAT: return valueOf(value.toDouble() * other.value.toDouble());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toDouble() * other.value.toDouble());
            }
        }
    }
    public final Literal operatorDivision(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() / other.value.toInt());
                case Type.FLOAT: return valueOf(value.toDouble() / other.value.toDouble());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toDouble() / other.value.toDouble());
            }
        }
    }
    public final Literal operatorRemainder(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() % other.value.toInt());
                case Type.FLOAT: return valueOf(value.toLong() % other.value.toLong());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toLong() % other.value.toLong());
            }
        }
    }
    public final Literal operatorPlus(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() + other.value.toInt());
                case Type.FLOAT: return valueOf(value.toDouble() + other.value.toDouble());
                case Type.STRING: return valueOf(value.toString().concat(other.value.toString()));
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toDouble() + other.value.toDouble());
                case Type.STRING: return valueOf(value.toString().concat(other.value.toString()));
            }
            case Type.STRING: return valueOf(value.toString().concat(other.value.toString()));
        }
    }
    public final Literal operatorMinus(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() - other.value.toInt());
                case Type.FLOAT: return valueOf(value.toDouble() - other.value.toDouble());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toDouble() - other.value.toDouble());
            }
        }
    }
    public final Literal operatorBitwiseShiftLeft(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() << other.value.toInt());
                case Type.FLOAT: return valueOf(value.toLong() << other.value.toLong());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toLong() << other.value.toLong());
            }
        }
    }
    public final Literal operatorBitwiseShiftRight(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() >>> other.value.toInt());
                case Type.FLOAT: return valueOf(value.toLong() >>> other.value.toLong());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toLong() >>> other.value.toLong());
            }
        }
    }
    public final Literal operatorEquals(Literal other) { return valueOf(value.operatorEquals(other.value)); }
    public final Literal operatorNotEquals(Literal other) { return valueOf(value.operatorNotEquals(other.value)); }
    public final Literal operatorGreater(Literal other) { return valueOf(value.operatorGreater(other.value)); }
    public final Literal operatorSmaller(Literal other) { return valueOf(value.operatorSmaller(other.value)); }
    public final Literal operatorGreaterEquals(Literal other) { return valueOf(value.operatorGreaterEquals(other.value)); }
    public final Literal operatorSmallerEquals(Literal other) { return valueOf(value.operatorSmallerEquals(other.value)); }
    public final Literal operatorTypedEquals(Literal other) { return valueOf(value.operatorTypedEquals(other.value)); }
    public final Literal operatorTypedNotEquals(Literal other) { return valueOf(value.operatorTypedNotEquals(other.value)); }
    public final Literal operatorBitwiseAnd(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() & other.value.toInt());
                case Type.FLOAT: return valueOf(value.toLong() & other.value.toLong());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toLong() & other.value.toLong());
            }
        }
    }
    public final Literal operatorBitwiseXor(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() ^ other.value.toInt());
                case Type.FLOAT: return valueOf(value.toLong() ^ other.value.toLong());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toLong() ^ other.value.toLong());
            }
        }
    }
    public final Literal operatorBitwiseOr(Literal other)
    {
        switch(value.getDataType())
        {
            default:
            case Type.INTEGER: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER: return valueOf(value.toInt() | other.value.toInt());
                case Type.FLOAT: return valueOf(value.toLong() | other.value.toLong());
            }
            case Type.FLOAT: switch(other.value.getDataType())
            {
                default:
                case Type.INTEGER:
                case Type.FLOAT: return valueOf(value.toLong() | other.value.toLong());
            }
        }
    }
    public final Literal operatorConcat(Literal other) { return valueOf(value.toString().concat(other.value.toString())); }
    
    public final LiteralType getLiteralType() { return LiteralType.decode(value.getDataType()); }

    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.LITERAL; }
    
    public static final Literal valueOf(int value) { return new Literal(new SGSInteger(value)); }
    public static final Literal valueOf(long value) { return new Literal(new SGSFloat(value)); }
    
    public static final Literal valueOf(float value) { return new Literal(new SGSFloat(value)); }
    public static final Literal valueOf(double value) { return new Literal(new SGSFloat(value)); }
    
    public static final Literal valueOf(boolean value) { return value ? TRUE : FALSE; }
    
    public static final Literal valueOf(char value) { return new Literal(new SGSInteger(value)); }
    public static final Literal valueOf(String value) { return new Literal(new SGSString(value)); }
    
    public static final Literal valueOf(SGSImmutableValue[] value) { return new Literal(new SGSImmutableArray(value)); }
    
    public static final Literal valueOf(Map<String, SGSImmutableValue> value) { return new Literal(new SGSImmutableObject(value)); }
    
    public static final Literal valueOf(SGSImmutableValue value) { return new Literal(value); }
    
    private static Literal valueOf(SGSValue value)
    {
        return valueOf(value instanceof SGSImmutableValue ? (SGSImmutableValue) value : SGSValue.UNDEFINED);
    }
    
    
    private static final Pattern INTEGER_P = Pattern.compile("(0|0[xX])?[0-9]+[bBsSlL]?[uU]?");
    private static final Pattern FLOAT_P = Pattern.compile("[0-9]+(\\.[0-9]+)?[fFdD]?");
    private static final int BYTES_INT = 4;
    private static final int BYTES_LONG = 8;
    
    
    public static final Literal decodeNumber(String str) throws CompilerError
    {
        if(INTEGER_P.matcher(str).matches())
            return decodeInteger(str);
        if(FLOAT_P.matcher(str).matches())
            return decodeFloat(str);
        return null;
    }
    
    private static int base(String str)
    {
        if(str.length() <= 1)
            return 10;
        char c = str.charAt(0);
        if(c != '0')
            return 10;
        c = str.charAt(1);
        return c == 'x' || c == 'X' ? 16 : 8;
    }
    private static int integerLen(String str)
    {
        char c = str.charAt(str.length() - 1);
        switch(c)
        {
            case 'l': case 'L': return BYTES_LONG;
            default: return BYTES_INT;
        }
    }
    private static Literal decodeInteger(String str) throws CompilerError
    {
        int base = base(str);
        int bytes = integerLen(str);
        int start = base == 8 ? 1 : base == 16 ? 2 : 0;
        int end = str.length() - (bytes != BYTES_INT ? 1 : 0);
        str = str.substring(start, end);
        
        try
        {
            switch(bytes)
            {
                case BYTES_LONG: return valueOf(Long.parseLong(str, base));
                default: return valueOf(Integer.parseInt(str, base));
            }
        }
        catch(NumberFormatException ex)
        {
            throw new CompilerError("Invalid Integer literal: " + ex);
        }
    }
    
    private static Literal decodeFloat(String str)
    {
        return valueOf(Double.parseDouble(str));
    }
    
    
    public enum LiteralType
    {
        UNDEFINED,
        INTEGER,
        FLOAT,
        STRING,
        CONST_ARRAY,
        CONST_OBJECT;
        
        private static LiteralType decode(int type)
        {
            switch(type)
            {
                case ImmutableType.UNDEFINED: return UNDEFINED;
                case ImmutableType.INTEGER: return INTEGER;
                case ImmutableType.FLOAT: return FLOAT;
                case ImmutableType.STRING: return STRING;
                case ImmutableType.CONST_ARRAY: return CONST_ARRAY;
                case ImmutableType.CONST_OBJECT: return CONST_OBJECT;
                default: throw new IllegalStateException();
            }
        }
        
        public final DataType getDataType()
        {
            switch(this)
            {
                case UNDEFINED: return DataType.ANY;
                case INTEGER: return DataType.INTEGER;
                case FLOAT: return DataType.FLOAT;
                case STRING: return DataType.STRING;
                case CONST_ARRAY: return DataType.ARRAY;
                case CONST_OBJECT: return DataType.OBJECT;
                default: return DataType.ANY;
            }
        }
    }
}
