/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

/**
 *
 * @author Asus
 */
public abstract class Statement extends CodeFragment
{
    @Override
    public final boolean isValidOperand() { return true; }
    
    @Override
    public final boolean isStatement() { return true; }
}
