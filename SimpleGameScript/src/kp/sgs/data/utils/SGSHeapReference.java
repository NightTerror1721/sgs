/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data.utils;

import kp.sgs.data.SGSReference;
import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public final class SGSHeapReference extends SGSReference
{
    public SGSValue value = UNDEFINED;
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { return value == null ? UNDEFINED : value; }
    @Override public final void     operatorReferenceSet(SGSValue value) { this.value = value; }
}
