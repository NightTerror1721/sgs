/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.util.Arrays;
import kp.sgs.data.SGSArray;
import kp.sgs.data.SGSImmutableArray;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSMutableArray;
import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public final class ArrayLibrary extends DefaultCoreLibrary
{
    public static final ArrayLibrary LIB = new ArrayLibrary();

    private ArrayLibrary()
    {
        super("arrays",
                Def.function("arraynew", (g, args) -> { return new SGSMutableArray(args[0].toInt()); }),
                Def.function("arrayconst", (g, args) -> { return new SGSImmutableArray(args[0].toArray().array()); }),
                Def.function("arrayget", (g, args) -> { return args[0].toArray().arrayGet(args[1].toInt()); }),
                Def.function("arrayset", (g, args) -> { return args[0].toArray().arraySet(args[1].toInt(), args[2]); }),
                Def.function("arraylength", (g, args) -> { return new SGSInteger(args[0].toArray().arrayLength()); }),
                Def.function("subarray", (g, args) -> {
                    if(args.length < 2)
                        return new SGSMutableArray(Arrays.copyOfRange(args[0].toArray().array(), 0, args[1].toInt()));
                    int offset = args[1].toInt();
                    int len = args[2].toInt() - offset;
                    return new SGSMutableArray(Arrays.copyOfRange(args[0].toArray().array(), offset, len));
                }),
                Def.function("arrayconcat", (g, args) -> {
                    if(args.length < 2)
                        throw new RuntimeException("Expected 2 or more arrays");
                    if(args.length == 2)
                    {
                        SGSArray a0 = args[0].toArray();
                        SGSArray a1 = args[1].toArray();
                        SGSValue[] a = new SGSValue[a0.arrayLength() + a1.arrayLength()];
                        System.arraycopy(a0.array(), 0, a, 0, a0.arrayLength());
                        System.arraycopy(a1.array(), 0, a, a0.arrayLength(), a1.arrayLength());
                        return new SGSMutableArray(a);
                    }
                    SGSArray[] as = new SGSArray[args.length];
                    int maxLen = 0;
                    for(int i=0;i<args.length;i++)
                    {
                        as[i] = args[i].toArray();
                        maxLen += as[i].arrayLength();
                    }
                    SGSValue[] a = new SGSValue[maxLen];
                    for(int i=0;i<as.length;i++)
                    {
                        int offset = i == 0 ? 0 : as[i - 1].arrayLength();
                        int len = as[i].arrayLength();
                        System.arraycopy(as[i].array(), 0, a, offset, len);
                    }
                    return new SGSMutableArray(a);
                })
        );
    }
    
    
}
