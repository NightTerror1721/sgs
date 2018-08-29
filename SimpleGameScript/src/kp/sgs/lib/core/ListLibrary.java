/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.lib.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSString;
import kp.sgs.data.SGSUserdata;
import kp.sgs.data.SGSValue;
import static kp.sgs.data.SGSValue.FALSE;
import static kp.sgs.data.SGSValue.TRUE;
import static kp.sgs.data.SGSValue.UNDEFINED;
import kp.sgs.data.utils.SGSIterator;
import kp.sgs.data.utils.SpecialProperty;

/**
 *
 * @author Asus
 */
public final class ListLibrary extends DefaultCoreLibrary
{
    public static final ListLibrary LIB = new ListLibrary();
    
    public ListLibrary() {
        super("list", Def.objectClass("List", () -> LibUtils.createLibraryClassInstance(BASE, new SGSList(new LinkedList<>()))));
    }
    
    private static final SGSValue BASE = new SGSUserdata()
    {
        @Override
        public final SGSValue operatorGetProperty(String name)
        {
            switch(name)
            {
                default: return SGSValue.UNDEFINED;
                case "add": return ADD;
                case "addAll": return ADD_ALL;
                case "clear": return CLEAR;
                case "contains": return CONTAINS;
                case "get": return GET;
                case "indexOf": return INDEX_OF;
                case "isEmpty": return IS_EMPTY;
                case "lastIndexOf": return LAST_INDEX_OF;
                case "equals":
                case SpecialProperty.OP_EQUALS: return OP_EQUALS;
                case SpecialProperty.OP_ITERATOR: return OP_ITERATOR;
                case "size":
                case SpecialProperty.OP_LENGTH: return OP_LENGTH;
                case SpecialProperty.OP_NOT_EQUALS: return OP_NOT_EQUALS;
                case SpecialProperty.CAST_ARRAY: return OP_TO_ARRAY;
                case "toString":
                case SpecialProperty.CAST_STRING: return OP_TO_STRING;
                case "remove": return REMOVE;
                case "removeIdx": return REMOVE_IDX;
                case "set": return SET;
                case "sort": return SORT;
                case "subList": return SUB_LIST;
            }
        }
    };
    
    private static final class SGSList extends SGSUserdata
    {
        public final List<SGSValue> list;
        
        private SGSList(List<SGSValue> list) { this.list = list; }
        
        @Override
        public final boolean equals(Object o)
        {
            return o instanceof SGSList &&
                    ((SGSList) o).list.equals(list);
        }

        @Override
        public final int hashCode()
        {
            int hash = 3;
            hash = 37 * hash + Objects.hashCode(this.list);
            return hash;
        }
    }
    
    private static final SGSValue
            ADD = Def.method((args) -> {
                if(args.length > 2)
                {
                    LibUtils.<SGSList>self(args).list.add(args[1].toInt(), args[2]);
                    return TRUE;
                }
                else return LibUtils.<SGSList>self(args).list.add(args[1]) ? TRUE : FALSE;
            }),
            ADD_ALL = Def.method((args) -> {
                if(args.length > 2)
                    return LibUtils.<SGSList>self(args).list.addAll(args[1].toInt(), Arrays.asList(args[2].toArray().array())) ? TRUE : FALSE;
                else return LibUtils.<SGSList>self(args).list.addAll(Arrays.asList(args[1].toArray().array())) ? TRUE : FALSE;
            }),
            CLEAR = Def.method((args) -> { LibUtils.<SGSList>self(args).list.clear(); }),
            CONTAINS = Def.method((args) -> { return LibUtils.<SGSList>self(args).list.contains(args[1]) ? TRUE : FALSE; }),
            OP_EQUALS = Def.method((args) -> {
                SGSValue other = args[1].operatorGetProperty(LibUtils.DEFAULT_SELF_PROPERTY_NAME);
                if(other == null || !(other instanceof SGSList))
                    return FALSE;
                return LibUtils.<SGSList>self(args).list.equals(((SGSList) other).list) ? TRUE : FALSE;
            }),
            OP_NOT_EQUALS = Def.method((args) -> {
                SGSValue other = args[1].operatorGetProperty(LibUtils.DEFAULT_SELF_PROPERTY_NAME);
                if(other == null || !(other instanceof SGSList))
                    return TRUE;
                return LibUtils.<SGSList>self(args).list.equals(((SGSList) other).list) ? FALSE : TRUE;
            }),
            GET = Def.method((args) -> { return LibUtils.<SGSList>self(args).list.get(args[1].toInt()); }),
            INDEX_OF = Def.method((args) -> { return new SGSInteger(LibUtils.<SGSList>self(args).list.indexOf(args[1])); }),
            IS_EMPTY = Def.method((args) -> { return LibUtils.<SGSList>self(args).list.isEmpty() ? TRUE : FALSE; }),
            OP_ITERATOR = Def.method((args) -> { return new SGSIterator(LibUtils.<SGSList>self(args).list.iterator()); }),
            LAST_INDEX_OF = Def.method((args) -> { return new SGSInteger(LibUtils.<SGSList>self(args).list.lastIndexOf(args[1])); }),
            REMOVE = Def.method((args) -> { return LibUtils.<SGSList>self(args).list.remove(args[1]) ? TRUE : FALSE; }),
            REMOVE_IDX = Def.method((args) -> {
                SGSValue value = LibUtils.<SGSList>self(args).list.remove(args[1].toInt());
                return value == null ? UNDEFINED : value;
            }),
            SET = Def.method((args) -> {
                SGSValue value = LibUtils.<SGSList>self(args).list.set(args[1].toInt(), args[2]);
                return value == null ? UNDEFINED : value;
            }),
            OP_LENGTH = Def.method((args) -> { return new SGSInteger(LibUtils.<SGSList>self(args).list.size()); }),
            SORT = Def.method((args) -> { LibUtils.<SGSList>self(args).list.sort((o0, o1) -> args[1].operatorCall(new SGSValue[] { o0, o1 }).toInt()); }),
            SUB_LIST = Def.method((args) -> {
                if(args.length > 2)
                    return LibUtils.createLibraryClassInstance(BASE, new SGSList(LibUtils.<SGSList>self(args).list.subList(args[1].toInt(), args[2].toInt())));
                return LibUtils.createLibraryClassInstance(BASE, new SGSList(LibUtils.<SGSList>self(args).list.subList(0, args[1].toInt())));
            }),
            OP_TO_ARRAY = Def.method((args) -> { return SGSValue.valueOf(false, LibUtils.<SGSList>self(args).list); }),
            OP_TO_STRING = Def.method((args) -> { new SGSString(LibUtils.<SGSList>self(args).list.toString()); });
    
}
