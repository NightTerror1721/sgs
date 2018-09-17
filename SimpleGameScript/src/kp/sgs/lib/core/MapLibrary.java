/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import kp.sgs.data.SGSImmutableArray;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSMutableArray;
import kp.sgs.data.SGSMutableObject;
import kp.sgs.data.SGSString;
import kp.sgs.data.SGSUserdata;
import kp.sgs.data.SGSValue;
import static kp.sgs.data.SGSValue.FALSE;
import static kp.sgs.data.SGSValue.TRUE;
import kp.sgs.data.utils.SGSIterator;
import kp.sgs.data.utils.SpecialProperty;

/**
 *
 * @author Asus
 */
public final class MapLibrary extends DefaultCoreLibrary
{
    public static final MapLibrary LIB = new MapLibrary();
    
    public MapLibrary() {
        super("map", Def.customObjectClass("Map", (args) -> LibUtils.createLibraryClassInstance(BASE, new SGSMap(args))));
    }
    
    private static final SGSValue BASE = new SGSUserdata()
    {
        @Override
        public final SGSValue operatorGetProperty(String name)
        {
            switch(name)
            {
                default: return UNDEFINED;
                case "clear": return CLEAR;
                case "contains":
                case "containsKey": return CONTAINS_KEY;
                case "containsValue": return CONTAINS_VALUE;
                case SpecialProperty.CAST_ARRAY:
                case "entries": return ENTRIES;
                case SpecialProperty.OP_ITERATOR:
                case "entriesIterator": return ENTRIES_IT;
                case SpecialProperty.OP_GET:
                case "get": return GET;
                case "isEmpty": return IS_EMPTY;
                case "keys": return KEYS;
                case "keysIterator": return KEYS_IT;
                case SpecialProperty.OP_EQUALS:
                case "equals": return OP_EQUALS;
                case SpecialProperty.OP_NOT_EQUALS: return OP_NOT_EQUALS;
                case SpecialProperty.OP_LENGTH:
                case "size": return OP_LENGTH;
                case SpecialProperty.CAST_OBJECT: return OP_TO_OBJECT;
                case SpecialProperty.CAST_STRING:
                case "toString": return OP_TO_STRING;
                case SpecialProperty.OP_SET:
                case "put": return PUT;
                case "putAll": return PUT_ALL;
                case "remove": return REMOVE;
                case "values": return VALUES;
                case "valuesIterator": return VALUES_IT;
            }
        }
    };
    
    private static final class SGSMap extends SGSUserdata
    {
        public final Map<SGSValue, SGSValue> map;
        
        private SGSMap(SGSValue[] args)
        {
            if(args.length < 1)
                map = new HashMap<>();
            else
            {
                SGSValue arg = args[0];
                if(arg instanceof SGSMap)
                    map = new HashMap<>(((SGSMap) arg).map);
                else map = arg.toObject().map().entrySet().stream().collect(Collectors.toMap(e -> new SGSString(e.getKey()), e -> e.getValue()));
                
            }
        }
        
        @Override
        public final boolean equals(Object o)
        {
            return o instanceof SGSMap &&
                    ((SGSMap) o).map.equals(map);
        }

        @Override
        public final int hashCode()
        {
            int hash = 3;
            hash = 37 * hash + Objects.hashCode(this.map);
            return hash;
        }
    }
    
    
    private static final SGSValue
            CLEAR = Def.method((args) -> { LibUtils.<SGSMap>self(args).map.clear(); }),
            CONTAINS_KEY = Def.method((args) -> {
                return LibUtils.<SGSMap>self(args).map.containsKey(args[1]) ? TRUE : FALSE;
            }),
            CONTAINS_VALUE = Def.method((args) -> {
                return LibUtils.<SGSMap>self(args).map.containsValue(args[1]) ? TRUE : FALSE;
            }),
            ENTRIES = Def.method((args) -> {
                return new SGSMutableArray(LibUtils.<SGSMap>self(args).map.entrySet()
                        .stream().map(e -> new SGSMutableArray(new SGSValue[]{ e.getKey(), e.getValue() })).toArray(size -> new SGSValue[size]));
            }),
            ENTRIES_IT = Def.method((args) -> {
                return new SGSIterator(new Iterator<SGSValue>()
                {
                    private final Iterator<Map.Entry<SGSValue, SGSValue>> it = LibUtils.<SGSMap>self(args).map.entrySet().iterator();
                    private final SGSValue[] array = new SGSValue[2];
                    private final SGSImmutableArray result = new SGSImmutableArray(array);
                    
                    @Override
                    public final boolean hasNext() { return it.hasNext(); }

                    @Override
                    public final SGSValue next()
                    {
                        Map.Entry<SGSValue, SGSValue> e = it.next();
                        if(e == null)
                            return SGSValue.UNDEFINED;
                        array[0] = e.getKey();
                        array[1] = e.getValue();
                        return result;
                    }
                });
            }),
            OP_EQUALS = Def.method((args) -> {
                SGSValue other = args[1].operatorGetProperty(LibUtils.DEFAULT_SELF_PROPERTY_NAME);
                if(other == null || !(other instanceof SGSMap))
                    return FALSE;
                return LibUtils.<SGSMap>self(args).map.equals(((SGSMap) other).map) ? TRUE : FALSE;
            }),
            OP_NOT_EQUALS = Def.method((args) -> {
                SGSValue other = args[1].operatorGetProperty(LibUtils.DEFAULT_SELF_PROPERTY_NAME);
                if(other == null || !(other instanceof SGSMap))
                    return TRUE;
                return LibUtils.<SGSMap>self(args).map.equals(((SGSMap) other).map) ? FALSE : TRUE;
            }),
            GET = Def.method((args) -> {
                if(args.length > 2)
                    return LibUtils.<SGSMap>self(args).map.getOrDefault(args[1], args[2]);
                return LibUtils.<SGSMap>self(args).map.get(args[1]);
            }),
            IS_EMPTY = Def.method((args)-> { return LibUtils.<SGSMap>self(args).map.isEmpty() ? TRUE : FALSE; }),
            KEYS = Def.method((args) -> { return SGSValue.valueOf(false, LibUtils.<SGSMap>self(args).map.keySet()); }),
            KEYS_IT = Def.method((args) -> { return new SGSIterator(LibUtils.<SGSMap>self(args).map.keySet()); }),
            PUT = Def.method((args) -> {
                SGSValue value = LibUtils.<SGSMap>self(args).map.put(args[1], args[2]);
                return value == null ? SGSValue.UNDEFINED : value;
            }),
            PUT_ALL = Def.method((args) -> {
                SGSValue value = args[1].operatorGetProperty(LibUtils.DEFAULT_SELF_PROPERTY_NAME);
                if(value != null && value instanceof SGSMap)
                    LibUtils.<SGSMap>self(args).map.putAll(((SGSMap) value).map);
            }),
            REMOVE = Def.method((args) -> {
                SGSValue value = LibUtils.<SGSMap>self(args).map.remove(args[1]);
                return value == null ? SGSValue.UNDEFINED : value;
            }),
            OP_LENGTH = Def.method((args) -> { return new SGSInteger(LibUtils.<SGSMap>self(args).map.size()); }),
            OP_TO_OBJECT = Def.method((args) -> {
                return new SGSMutableObject(LibUtils.<SGSMap>self(args).map.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue())));
            }),
            OP_TO_STRING = Def.method((args) -> { return new SGSString(LibUtils.<SGSMap>self(args).map.toString()); }),
            VALUES = Def.method((args) -> { return SGSValue.valueOf(false, LibUtils.<SGSMap>self(args).map.values()); }),
            VALUES_IT = Def.method((args) -> { return new SGSIterator(LibUtils.<SGSMap>self(args).map.values()); });
}
