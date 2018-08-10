/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.exception;

/**
 *
 * @author Asus
 */
public class CompilerError extends Exception
{
    public CompilerError(String errorMessage) { super(errorMessage); }
    public CompilerError(String errorMessage, Throwable cause) { super(errorMessage, cause); }
}
