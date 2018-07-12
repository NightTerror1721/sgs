/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs;

import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public interface SGSGlobals
{
    SGSValue getGlobalValue(String name);
    SGSValue setGlobalValue(String name, SGSValue value);
    
    default boolean hasGlobalValue(String name) { return getGlobalValue(name) != SGSValue.UNDEFINED; }
}
