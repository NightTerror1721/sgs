/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import kp.sgs.compiler.exception.CompilerError;

/**
 *
 * @author Asus
 */
public final class CodeFragmentList
{
    private final CodeFragment[] code;
    private final int sourceLine;
    
    public CodeFragmentList(int sourceLine, CodeFragment... code) { this.code = check(code); this.sourceLine = sourceLine; }
    public CodeFragmentList(int sourceLine, Collection<? extends CodeFragment> c) { this(sourceLine, c.toArray(new CodeFragment[c.size()])); }
    public CodeFragmentList(int sourceLine, CodeFragment[] code, int off, int len)
    {
        this.code = new CodeFragment[len];
        this.sourceLine = sourceLine;
        System.arraycopy(check(code), off, this.code, 0, len);
    }
    private CodeFragmentList(int sourceLine, CodeFragment[] code, boolean dummy) { this.code = code; this.sourceLine = sourceLine; }
    private CodeFragmentList(int sourceLine, CodeFragment[] code, int off, int len, boolean dummy)
    {
        this.code = new CodeFragment[len];
        this.sourceLine = sourceLine;
        System.arraycopy(code, off, this.code, 0, len);
    }
    
    public final CodeFragmentList copy() { return subList(0, code.length); }
    
    public final int length() { return code.length; }
    public final boolean isEmpty() { return code.length == 0; }
    
    public final void set(int index, CodeFragment code)
    {
        if(code == null)
            throw new NullPointerException();
        this.code[index] = code;
    }
    
    public final <US extends CodeFragment> US get(int index) { return (US) code[index]; }
    
    public final CodeFragmentList subList(int offset, int length) { return new CodeFragmentList(sourceLine, code, offset, length, false); }
    public final CodeFragmentList subList(int offset) { return subList(offset, code.length - offset); }
    
    public final CodeFragmentList concat(CodeFragmentList clist) { return concat(clist.code); }
    public final CodeFragmentList concat(Collection<? extends CodeFragment> c) { return concat(c.toArray(new CodeFragment[c.size()])); }
    public final CodeFragmentList concat(CodeFragment... code)
    {
        CodeFragment[] array = new CodeFragment[this.code.length + code.length];
        System.arraycopy(this.code, 0, array, 0, this.code.length);
        System.arraycopy(check(code), 0, array, this.code.length, code.length);
        return new CodeFragmentList(sourceLine, array, false);
    }
    public final CodeFragmentList concat(CodeFragment code)
    {
        if(code == null)
            throw new NullPointerException();
        CodeFragment[] array = new CodeFragment[this.code.length + 1];
        System.arraycopy(this.code, 0, array, 0, this.code.length);
        array[array.length - 1] = code;
        return new CodeFragmentList(sourceLine, array, false);
    }
    
    public final CodeFragmentList concatFirst(CodeFragmentList clist) { return concatFirst(clist.code); }
    public final CodeFragmentList concatFirst(Collection<? extends CodeFragment> c) { return concatFirst(c.toArray(new CodeFragment[c.size()])); }
    public final CodeFragmentList concatFirst(CodeFragment... code)
    {
        CodeFragment[] array = new CodeFragment[this.code.length + code.length];
        System.arraycopy(check(code), 0, array, 0, code.length);
        System.arraycopy(this.code, 0, array, code.length, this.code.length);
        return new CodeFragmentList(sourceLine, array, false);
    }
    public final CodeFragmentList concatFirst(CodeFragment code)
    {
        if(code == null)
            throw new NullPointerException();
        CodeFragment[] array = new CodeFragment[this.code.length + 1];
        System.arraycopy(this.code, 0, array, 1, this.code.length);
        array[0] = code;
        return new CodeFragmentList(sourceLine, array, false);
    }
    
    public final CodeFragmentList concatMiddle(int index, CodeFragmentList clist) { return concatMiddle(index, clist.code); }
    public final CodeFragmentList concatMiddle(int index, Collection<? extends CodeFragment> c) { return concatMiddle(index, c.toArray(new CodeFragment[c.size()])); }
    public final CodeFragmentList concatMiddle(int index, CodeFragment... code)
    {
        if(index < 0 || index > this.code.length)
            throw new IllegalArgumentException("Index out of range: " + index);
        if(index == 0)
            return concatFirst(code);
        else if(index == this.code.length)
            return concat(code);
        CodeFragment[] array = new CodeFragment[this.code.length + code.length];
        System.arraycopy(this.code, 0, array, 0, index);
        System.arraycopy(check(code), 0, array, index, code.length);
        System.arraycopy(this.code, index, array, index + code.length, this.code.length - index);
        return new CodeFragmentList(sourceLine, array, false);
    }
    public final CodeFragmentList concatMiddle(int index, CodeFragment code)
    {
        if(code == null)
            throw new NullPointerException();
        if(index < 0 || index > this.code.length)
            throw new IllegalArgumentException("Index out of range: " + index);
        if(index == 0)
            return concatFirst(code);
        else if(index == this.code.length)
            return concat(code);
        CodeFragment[] array = new CodeFragment[this.code.length + 1];
        System.arraycopy(this.code, 0, array, 0, index);
        System.arraycopy(this.code, index, array, index + 1, this.code.length - index);
        array[index] = code;
        return new CodeFragmentList(sourceLine, array, false);
    }
    
    public final CodeFragmentList wrapBetween(CodeFragmentList before, CodeFragmentList after) { return wrapBetween(before.code, after.code); }
    public final CodeFragmentList wrapBetween(CodeFragment before, CodeFragment after)
    {
        if(before == null)
            throw new NullPointerException();
        if(after == null)
            throw new NullPointerException();
        CodeFragment[] array = new CodeFragment[code.length + 2];
        System.arraycopy(code, 0, array, 1, code.length);
        array[0] = before;
        array[array.length - 1] = after;
        return new CodeFragmentList(sourceLine, array, false);
    }
    public final CodeFragmentList wrapBetween(CodeFragment[] before, CodeFragment[] after)
    {
        CodeFragment[] array = new CodeFragment[before.length + code.length + after.length];
        System.arraycopy(check(before), 0, array, 0, before.length);
        System.arraycopy(code, 0, array, before.length, code.length);
        System.arraycopy(check(after), 0, array, before.length + array.length, after.length);
        return new CodeFragmentList(sourceLine, array, false);
    }
    
    public final CodeFragmentList extract(CodeFragment from, CodeFragment to)
    {
        boolean init = false;
        int offset = -1, len = -1, idx = -1;
        for(CodeFragment c : code)
        {
            idx++;
            if(!init)
            {
                if(!c.equals(from))
                    continue;
                init = true;
                offset = idx;
                continue;
            }
            if(c.equals(to))
                break;
            len++;
        }
        return subList(offset, len);
    }
    
    public final int count(CodeFragment codePart)
    {
        int count = 0;
        for(CodeFragment c : code)
            if(c.equals(codePart))
                count++;
        return count;
    }
    
    public final boolean has(CodeFragment codePart)
    {
        for(CodeFragment cp : code)
            if(cp.equals(codePart))
                return true;
        return false;
    }
    
    public final int count(CodeFragmentType type)
    {
        int count = 0;
        for(CodeFragment c : code)
            if(c.getFragmentType() == type)
                count++;
        return count;
    }
    
    public final int indexOf(CodeFragment code)
    {
        for(int i=0;i<this.code.length;i++)
            if(this.code[i].equals(code))
                return i;
        return -1;
    }
    
    public final int indexOf(CodeFragmentType type)
    {
        for(int i=0;i<code.length;i++)
            if(code[i].getFragmentType() == type)
                return i;
        return -1;
    }
    
    public final CodeFragmentList[] split(CodeFragment separator) { return split(separator, -1); }
    public final CodeFragmentList[] split(CodeFragment separator, int limit)
    {
        if(code.length == 0)
            return new CodeFragmentList[] { this };
        if(limit == 1)
            return new CodeFragmentList[] { copy() };
        LinkedList<CodeFragmentList> parts = new LinkedList<>();
        limit = limit < 1 ? -1 : limit;
        int i, off;
        for(i=0, off=0;i<code.length;i++)
            if(code[i].equals(separator) && limit != 0)
            {
                parts.add(subList(off, i - off));
                off = i + 1;
                limit--;
            }
        if(i > off)
            parts.add(subList(off, i - off));
        return parts.toArray(new CodeFragmentList[parts.size()]);
    }
    
    final Pointer counter() { return new Pointer(); }
    final Pointer counter(int initialValue) { return new Pointer(initialValue); }
    
    @Override
    public final String toString() { return Arrays.toString(code); }
    
    private static CodeFragment[] check(CodeFragment[] code)
    {
        for(CodeFragment c : code)
            if(c == null)
                throw new NullPointerException();
        return code;
    }
    
    private static final CodeFragment[] EMPTY_ARRAY = {};
    public static final CodeFragmentList empty(int sourceLine) { return new CodeFragmentList(sourceLine, EMPTY_ARRAY); }
    
    public static final <IT, OT> OT[] mapArray(IT[] input, Mapper<IT, OT> mapper, OT[] output) throws CompilerError
    {
        int end = input.length > output.length ? output.length : input.length;
        for(int i=0;i<end;i++)
            output[i] = mapper.map(input[i]);
        return output;
    }
    
    public static final <IT, OT> OT[] mapArray(int offset, IT[] input, Mapper<IT, OT> mapper, OT[] output) throws CompilerError
    {
        int end = input.length > output.length ? output.length : input.length;
        for(int i=offset;i<end;i++)
            output[i] = mapper.map(input[i]);
        return output;
    }
    
    @FunctionalInterface
    public interface Mapper<I, O> { O map(I input) throws CompilerError; }
    
    
    final class Pointer
    {
        private int value;
        private final int limit;
        
        private Pointer(int initialValue)
        {
            this.value = initialValue;
            this.limit = code.length;
        }
        private Pointer() { this(0); }
        
        public final CodeFragmentList list() { return CodeFragmentList.this; }
        public final int increase(int times) { return value += times; }
        public final int increase() { return increase(1); }
        public final int decrease(int times) { return value -= times; }
        public final int decrease() { return decrease(1); }
        public final int value() { return value; }
        public final int finish() { return value = limit; }
        public final boolean end() { return value >= limit; }
        
        public final CodeFragment listValue() { return code[value]; }
    }
}
