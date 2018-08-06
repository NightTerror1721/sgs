/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data.utils;

import java.util.Iterator;
import java.util.Objects;
import kp.sgs.data.SGSFunction;
import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public class SGSIterator extends SGSFunction
{
    private final Iterator<SGSValue> it;
    
    public SGSIterator(Iterator<SGSValue> it)
    {
        this.it = Objects.requireNonNull(it);
    }
    public SGSIterator(Iterable<SGSValue> it)
    {
        this.it = it.iterator();
    }

    @Override
    public SGSValue operatorCall(SGSValue[] args)
    {
        return it.hasNext() ? it.next() : UNDEFINED;
    }
    
    
    public static final SGSIterator scalarIterator(final SGSValue value)
    {
        return new SGSIterator(new Iterator<SGSValue>()
        {
            private boolean end = false;
            
            @Override public boolean hasNext() { return !end; }

            @Override public SGSValue next() { end = true; return value; }
        });
    }
}
