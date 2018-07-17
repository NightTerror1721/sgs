/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.opcode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import kp.sgs.compiler.RuntimeStack;
import kp.sgs.compiler.exception.CompilerError;

/**
 *
 * @author Asus
 */
public final class OpcodeList implements Iterable<OpcodeList.OpcodeLocation>
{
    private final RuntimeStack stack;
    private final OpcodeLocation top = new OpcodeLocation();
    private OpcodeLocation bottom = new OpcodeLocation();
    private int size = 0;
    
    public OpcodeList(RuntimeStack stack)
    {
        this.stack = Objects.requireNonNull(stack);
        top.next = bottom;
        bottom.previous = top;
    }

    public final int size() { return size; }

    public final boolean isEmpty() { return top.next == bottom; }

    public final boolean contains(Opcode o)
    {
        if(isEmpty())
            return false;
        for(OpcodeLocation it : this)
            if(it.opcode.equals(o))
                return true;
        return false;
    }

    public final Iterator<OpcodeLocation> iterator()
    {
        return new Iterator<OpcodeLocation>()
        {
            private OpcodeLocation it = top;
            
            @Override public final boolean hasNext() { return it == bottom; }
            @Override public final OpcodeLocation next() { return it = it.next(); }
        };
    }
    public final void forEachLocation(Consumer<OpcodeLocation> consumer)
    {
        for(OpcodeLocation it : this)
            consumer.accept(it);
    }

    public final OpcodeLocation[] toArray()
    {
        OpcodeLocation[] array = new OpcodeLocation[size];
        int count = 0;
        for(OpcodeLocation it : this)
            array[count++] = it;
        return array;
    }
    
    public final OpcodeLocation append(Opcode e) throws CompilerError
    {
        OpcodeLocation last = bottom;
        bottom = new OpcodeLocation();
        last.opcode = e;
        last.next = bottom;
        bottom.previous = last;
        stack.modify(e);
        size++;
        return last;
    }
    
    public final OpcodeLocation append(Opcode... opcodes) throws CompilerError
    {
        OpcodeLocation loc = bottom.previous;
        for(int i=0;i<opcodes.length;i++)
            loc = append(opcodes[i]);
        return loc;
    }

    public final OpcodeLocation append(Collection<? extends Opcode> c) throws CompilerError
    {
        OpcodeLocation loc = bottom.previous;
        for(Opcode op : c)
            loc = append(op);
        return loc;
    }
    
    public final OpcodeLocation insert(OpcodeLocation loc, Opcode element) throws CompilerError
    {
        loc.checkParentList(this);
        if(loc == top)
            if(isEmpty())
                return append(element);
            else throw new IllegalArgumentException("Cannot replace header opcode location.");
        if(loc == bottom)
            return append(element);
        OpcodeLocation newloc = new OpcodeLocation(element);
        newloc.previous = loc.previous;
        newloc.next = loc;
        newloc.previous.next = newloc;
        loc.previous = newloc;
        stack.modify(element);
        size++;
        return newloc;
    }
    public final OpcodeLocation insertBefore(OpcodeLocation loc, Opcode element) throws CompilerError { return insert(loc.previous, element); }
    public final OpcodeLocation insertAfter(OpcodeLocation loc, Opcode element) throws CompilerError { return insert(loc.next, element); }
    
    public final OpcodeLocation insert(OpcodeLocation loc, Opcode... opcodes) throws CompilerError
    {
        for(int i=0;i<opcodes.length;i++)
            loc = insert(loc, opcodes[i]);
        return loc;
    }
    public final OpcodeLocation insertBefore(OpcodeLocation loc, Opcode... opcodes) throws CompilerError { return insert(loc.previous, opcodes); }
    public final OpcodeLocation insertAfter(OpcodeLocation loc, Opcode... opcodes) throws CompilerError { return insert(loc.next, opcodes); }

    public final OpcodeLocation insert(OpcodeLocation loc, Collection<? extends Opcode> c) throws CompilerError
    {
        loc.checkParentList(this);
        for(Opcode op : c)
            loc = insert(loc, op);
        return loc;
    }
    public final OpcodeLocation insertBefore(OpcodeLocation loc, Collection<? extends Opcode> c) throws CompilerError { return insert(loc.previous, c); }
    public final OpcodeLocation insertAfter(OpcodeLocation loc, Collection<? extends Opcode> c) throws CompilerError { return insert(loc.next, c); }
    
    public final void setJumpOpcodeLocationTarget(OpcodeLocation loc, OpcodeLocation jumpTarget)
    {
        Opcode op = get(loc);
        op.setJumpTargetLocation(jumpTarget);
    }
    public final void setJumpOpcodeLocationToBottom(OpcodeLocation loc)
    {
        Opcode op = get(loc);
        op.setJumpTargetLocation(bottom);
    }

    public final void clear()
    {
        OpcodeLocation loc = top.next;
        while(loc != bottom)
        {
            loc.previous = null;
            loc.opcode = null;
            loc = loc.next;
            loc.previous.next = null;
        }
        bottom.previous = null;
        bottom = new OpcodeLocation();
        top.next = bottom;
        bottom.previous = top;
        size = 0;
    }

    public final Opcode get(OpcodeLocation loc)
    {
        loc.checkParentList(this);
        if(loc == top || loc == bottom)
            throw new IllegalArgumentException("Cannot get opcode from top or bottom opcode location.");
        return loc.opcode;
    }
    
    public final OpcodeLocation getLastLocation() { return bottom.previous; }

    public final OpcodeLocation set(OpcodeLocation loc, Opcode element) throws CompilerError
    {
        loc.checkParentList(this);
        if(loc == top || loc == bottom)
            throw new IllegalArgumentException("Cannot set top or bottom opcode location.");
        if(element == null)
            throw new NullPointerException();
        stack.modifyInverse(loc.opcode);
        loc.opcode = element;
        stack.modify(element);
        return loc;
    }
    
    public final int buildBytePositions()
    {
        int count = 0;
        for(OpcodeLocation loc : this)
        {
            loc.firstByte = count;
            count += loc.opcode.getByteCount();
        }
        return count;
    }
    
    public final void buildBytecodes(byte[] bytecode, int offset)
    {
        for(OpcodeLocation loc : this)
        {
            loc.opcode.build(bytecode, offset);
            offset += loc.firstByte;
        }
    }
    
    
    
    public final class OpcodeLocation
    {
        private Opcode opcode;
        private OpcodeLocation previous;
        private OpcodeLocation next;
        private int firstByte = -1;
        
        private OpcodeLocation(Opcode opcode)
        {
            if(opcode == null)
                throw new NullPointerException();
            this.opcode = opcode;
        }
        
        private OpcodeLocation() { this.opcode = null; } //top or bottom
        
        public final boolean isTop() { return this == top; }
        public final boolean isBottom() { return this == bottom; }
        
        public final Opcode getOpcode() { return opcode; }
        
        public final boolean hasPrevious() { return previous != null; }
        public final boolean hasNext() { return next != null; }
        
        public final OpcodeLocation previous() { return previous; }
        public final OpcodeLocation next() { return next; }
        
        public final int getFirstByte() { return firstByte; }
        
        private void checkParentList(OpcodeList list)
        {
            if(list != OpcodeList.this)
                throw new IllegalStateException();
        }
    }
}
