/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

/**
 *
 * @author Asus
 */
public abstract class SGSImmutableValue extends SGSValue
{
    SGSImmutableValue() {}
    
    @Override public final SGSImmutableValue operatorConst() { return this; }
    
    @Override public final void operatorSet(SGSValue index, SGSValue value) { throw new UnsupportedOperationException("const value cannot use operatorSet"); }
    @Override public final void operatorSet(int index, SGSValue value) { throw new UnsupportedOperationException("const value cannot use operatorSet"); }
    
    @Override public final void operatorSetProperty(String name, SGSValue value) { throw new UnsupportedOperationException("const value cannot use operatorSetProperty"); }
    
    @Override public final void operatorReferenceSet(SGSValue value) { throw new UnsupportedOperationException("const value cannot use operatorPointerSet"); }
}
