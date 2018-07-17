/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.util.Objects;

/**
 *
 * @author Asus
 */
public final class Varargs extends Statement
{
    private final Identifier name;
    
    public Varargs(Identifier name) { this.name = Objects.requireNonNull(name); }
    
    public final Identifier getName() { return name; }
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.VARARGS; }
    
}
