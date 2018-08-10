/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.util.StringJoiner;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.data.SGSArray;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSString;
import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public class StringLibrary extends DefaultCoreLibrary
{
    public static final StringLibrary LIB = new StringLibrary();
    
    private StringLibrary()
    {
        super("strings", 
                Def.function("strcat", DataType.STRING, (g, args) -> {
                    if(args.length > 2)
                        return new SGSString(args[0].toString().concat(args[1].toString()).substring(0, args[2].toInt()));
                    return new SGSString(args[0].toString().concat(args[1].toString()));
                }),
                
                Def.function("stridxof", DataType.INTEGER, (g, args) -> {
                    return new SGSInteger(args[0].toString().indexOf(args[1].toInt()));
                }),
                
                Def.function("strlastidxof", DataType.INTEGER, (g, args) -> {
                    return new SGSInteger(args[0].toString().lastIndexOf(args[1].toInt()));
                }),
                
                Def.function("strcmp", DataType.INTEGER, (g, args) -> {
                    return new SGSInteger(args[0].toString().compareTo(args[1].toString()));
                }),
                
                Def.function("substr", DataType.STRING, (g, args) -> {
                    if(args.length > 2)
                        return new SGSString(args[0].toString().substring(args[1].toInt(), args[2].toInt()));
                    return new SGSString(args[0].toString().substring(args[1].toInt()));
                }),
                
                Def.function("strmatch", DataType.INTEGER, (g, args) -> {
                    return args[0].toString().matches(args[1].toString()) ? SGSValue.TRUE : SGSValue.FALSE;
                }),
                
                Def.function("strsplit", DataType.ARRAY, (g, args) -> {
                    return SGSValue.valueOf(false, args[0].toString().split(args[1].toString()));
                }),
                
                Def.function("tolowercase", DataType.ANY, (g, args) -> {
                    if(args[0].isInteger() || args[0].isFloat())
                        return new SGSInteger(Character.toLowerCase(args[0].toInt()));
                    return new SGSString(args[0].toString().toLowerCase());
                }),
                
                Def.function("touppercase", DataType.ANY, (g, args) -> {
                    if(args[0].isInteger() || args[0].isFloat())
                        return new SGSInteger(Character.toUpperCase(args[0].toInt()));
                    return new SGSString(args[0].toString().toUpperCase());
                }),
                
                Def.function("strstarts", DataType.INTEGER, (g, args) -> {
                    return args[0].toString().startsWith(args[1].toString()) ? SGSValue.TRUE : SGSValue.FALSE;
                }),
                
                Def.function("strends", DataType.INTEGER, (g, args) -> {
                    return args[0].toString().endsWith(args[1].toString()) ? SGSValue.TRUE : SGSValue.FALSE;
                }),
                
                Def.function("strcontains", DataType.INTEGER, (g, args) -> {
                    return args[0].toString().contains(args[1].toString()) ? SGSValue.TRUE : SGSValue.FALSE;
                }),
                
                Def.function("strjoin", DataType.STRING, (g, args) -> {
                    StringJoiner joiner = new StringJoiner(args[0].toString());
                    SGSArray array = args[1].toArray();
                    for(SGSValue value : array)
                        joiner.add(value.toString());
                    return new SGSString(joiner.toString());
                }));
    }
    
}
