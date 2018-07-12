/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Asus
 */
public final class ErrorList implements Iterable<ErrorList.ErrorEntry>
{
    private final LinkedList<ErrorEntry> errors = new LinkedList<>();
    
    public final ErrorEntry addError(int startLine, int endLine, CompilerError cause)
    {
        ErrorEntry e = new ErrorEntry(startLine, endLine, cause);
        errors.add(e);
        return e;
    }
    
    public final boolean hasErrors() { return !errors.isEmpty(); }
    
    public final List<ErrorEntry> getAllErrors() { return Collections.unmodifiableList(new ArrayList<>(errors)); }
    
    public final int getErrorCount() { return errors.size(); }

    @Override
    public final Iterator<ErrorEntry> iterator() { return errors.iterator(); }
    
    public static final class ErrorEntry
    {
        private final int startLine;
        private final int endLine;
        private final CompilerError cause;
        
        private ErrorEntry(int startLine, int endLine, CompilerError cause)
        {
            this.startLine = startLine;
            this.endLine = endLine;
            this.cause = Objects.requireNonNull(cause);
        }
        
        public final int getStartLine() { return startLine; }
        public final int getEndLine() { return endLine; }
        public final CompilerError getCause() { return cause; }
    }
}
