/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.io.EOFException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.exception.ErrorList;
import kp.sgs.compiler.instruction.Instruction;
import kp.sgs.compiler.instruction.InstructionParser;

/**
 *
 * @author Asus
 */
public final class CodeParser
{
    private final CodeQueue accumulated = new CodeQueue();
    
    public final CodeFragment parseFragment(CodeReader source, CodeFragment last, boolean isFinishValid, ErrorList errors) throws CompilerError
    {
        if(!accumulated.isEmpty())
            return accumulated.dequeue();
        CodeFragmentBuilder builder = new CodeFragmentBuilder(source, accumulated, isFinishValid);
        
        try
        {
            for(;;)
            {
                char c = source.next();
                
                main_switch:
                switch(c)
                {
                    case '\t':
                    case '\r':
                    case ' ': {
                        if(builder.flush())
                            return accumulated.dequeue();
                    } break;
                    
                    case ';': {
                        builder.flush();
                        return accumulated.enqret(Stopchar.SEMICOLON);
                    }
                    
                    case '(': {
                        builder.flush();
                        CodeReader scopeSource = extractScope(source, '(', ')');
                        CodeFragmentList list = parseSubStatement(scopeSource, errors);
                        if(last == null || (!last.isValidOperand() && last != Command.DEF)) //Parenthesis
                        {
                            if(list.length() == 1 && list.get(0).isDataType())
                                return accumulated.enqret(list.<DataType>get(0).getCastOperator());
                            return accumulated.enqret(StatementParser.parse(list));
                        }
                        else if(last.isCommand() && last != Command.DEF) //Command arguments
                            return accumulated.enqret(CommandArguments.valueOf(list));
                        else return accumulated.enqret(Arguments.valueOf(list)); //Arguments
                    }
                    
                    case ')': throw new CompilerError("Unexpected end of parenthesis ')'");
                    
                    case '[': {
                        builder.flush();
                        CodeReader scopeSource = extractScope(source, '[', ']');
                        CodeFragmentList list = parseSubStatement(scopeSource, errors);
                        if(last == null || !last.isValidOperand()) //Literal Array
                            return accumulated.enqret(Mutable.array(list));
                        else return accumulated.enqueue(Operator.ARRAY_GET).enqret(StatementParser.parse(list));
                    }
                    
                    case ']': throw new CompilerError("Unexpected end of square parenthesis ']'");
                    
                    case '{': {
                        builder.flush();
                        CodeReader scopeSource = extractScope(source, '{', '}');
                        if(last == null || !last.isValidOperand()) // Object
                        {
                            CodeFragmentList list = parseSubStatement(scopeSource, errors);
                            return accumulated.enqret(Mutable.object(list));
                        }
                        else // Scope
                        {
                            Scope scope = parseScope(scopeSource, errors);
                            return accumulated.enqret(scope);
                        }
                    }
                    
                    case '}': throw new CompilerError("Unexpected end of scope parenthesis '}'");
                    
                    case '\'':
                    case '\"': {
                        builder.flush();
                        
                        final char base = c;
                        builder.disableFinish();
                        for(;;)
                        {
                            c = source.next();
                            if(c == base)
                                break;
                            if(c == '\\')
                            {
                                c = source.next();
                                switch(c)
                                {
                                    case 'n': builder.append('\n'); break;
                                    case 'r': builder.append('\r'); break;
                                    case 't': builder.append('\t'); break;
                                    case 'u': {
                                        if(!source.canPeek(4))
                                            throw new CompilerError("Invalid unicode scape");
                                        String hexCode = new String(source.nextArray(4));
                                        builder.append(HexadecimalDecoder.decodeUnicode(hexCode));
                                    } break;
                                    case '\\': builder.append('\\'); break;
                                    case '\'': builder.append('\''); break;
                                    case '\"': builder.append('\"'); break;
                                }
                                continue;
                            }
                            builder.append(c);
                        }
                        builder.enableFinish();
                        String value = builder.toString();
                        builder.clear();
                        if(base == '\'')
                        {
                            if(value.length() != 1)
                                throw new CompilerError("Invalid char literal: \'" + value + "\'");
                            return accumulated.enqret(Literal.valueOf(value.charAt(0)));
                        }
                        else return accumulated.enqret(Literal.valueOf(value));
                    }
                    
                    case ',': {
                        builder.flush();
                        return accumulated.enqret(Stopchar.COMMA);
                    }
                    
                    case ':': {
                        builder.flush();
                        return accumulated.enqret(Stopchar.TWO_POINTS);
                    }
                    
                    case '?': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: ?");
                        return accumulated.enqret(Operator.TERNARY_CONDITIONAL);
                    }
                    
                    case '|': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: |");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.BITWISE_OR);
                                source.move(-1);
                            } break;
                            case '|': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: |");
                                accumulated.enqueue(Operator.LOGICAL_OR);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: |");
                                accumulated.enqueue(Operator.ASSIFNMENT_BITWISE_OR);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '&': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: &");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                if(last != null && last.isValidOperand())
                                    accumulated.enqueue(Operator.ADDRESS_OF);
                                else accumulated.enqueue(Operator.BITWISE_AND);
                                source.move(-1);
                            } break;
                            case '&': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: &");
                                accumulated.enqueue(Operator.LOGICAL_AND);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: &");
                                accumulated.enqueue(Operator.ASSIFNMENT_BITWISE_AND);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '^': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: ^");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.BITWISE_XOR);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: ^");
                                accumulated.enqueue(Operator.ASSIFNMENT_BITWISE_XOR);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '.': {
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: .");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                source.move(-1);
                                if(!builder.isEmpty() && isInteger(builder.toString()))
                                {
                                    builder.append('.');
                                    break main_switch;
                                }
                                builder.flush();
                                accumulated.enqueue(Operator.PROPERTY_GET);
                            } break;
                            case '.': {
                                builder.flush();
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: .");
                                c = source.next();
                                switch(c)
                                {
                                    default: {
                                        accumulated.enqueue(Operator.CONCAT);
                                        source.move(-1);
                                    } break;
                                    case '.': {
                                        if(last == null || !last.isIdentifier())
                                            throw new CompilerError("Expected a valid identifier before '...'");
                                        accumulated.enqueue(Stopchar.THREE_POINTS);
                                    } break;
                                }
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '!': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: !");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.NOT);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: =");
                                c = source.next();
                                switch(c)
                                {
                                    default: {
                                        accumulated.enqueue(Operator.NOT_EQUALS);
                                        source.move(-1);
                                    } break;
                                    case '=': {
                                        if(!source.canPeek(1))
                                            throw new CompilerError("Unexpected character: =");
                                        accumulated.enqueue(Operator.TYPED_NOT_EQUALS);
                                    } break;
                                }
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '=': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: =");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.ASSIGNMENT);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: =");
                                c = source.next();
                                switch(c)
                                {
                                    default: {
                                        accumulated.enqueue(Operator.EQUALS);
                                        source.move(-1);
                                    } break;
                                    case '=': {
                                        if(!source.canPeek(1))
                                            throw new CompilerError("Unexpected character: =");
                                        accumulated.enqueue(Operator.TYPED_EQUALS);
                                    } break;
                                }
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '>': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: >");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.GREATER_THAN);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: >");
                                accumulated.enqueue(Operator.GREATER_EQUALS_THAN);
                            } break;
                            case '>': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: >");
                                accumulated.enqueue(Operator.BITWISE_LEFT_SHIFT);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '<': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: <");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.SMALLER_THAN);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: <");
                                accumulated.enqueue(Operator.SMALLER_EQUALS_THAN);
                            } break;
                            case '>': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: <");
                                accumulated.enqueue(Operator.BITWISE_RIGHT_SHIFT);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '-': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: -");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                source.move(-1);
                                if(last != null && last.isValidOperand())
                                {
                                    if(Character.isDigit(c))
                                    {
                                        builder.append('-');
                                        break main_switch;
                                    }
                                    accumulated.enqueue(Operator.UNARY_MINUS);
                                }
                                else accumulated.enqueue(Operator.SUBTRACTION);
                            } break;
                            case '-': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: -");
                                if(last == null || !last.isValidOperand())
                                    accumulated.enqueue(Operator.PREFIX_DECREMENT);
                                else accumulated.enqueue(Operator.SUFIX_DECREMENT);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: -");
                                accumulated.enqueue(Operator.ASSIGNMENT_SUBTRACTION);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '+': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: +");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                source.move(-1);
                                if(last != null && last.isValidOperand())
                                {
                                    if(Character.isDigit(c))
                                        break main_switch;
                                    accumulated.enqueue(Operator.UNARY_PLUS);
                                }
                                else accumulated.enqueue(Operator.ADDITION);
                            } break;
                            case '+': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: +");
                                if(last == null || !last.isValidOperand())
                                    accumulated.enqueue(Operator.PREFIX_INCREMENT);
                                else accumulated.enqueue(Operator.SUFIX_INCREMENT);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: +");
                                accumulated.enqueue(Operator.ASSIGNMENT_ADDITION);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '%': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: %");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.REMAINDER);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: %");
                                accumulated.enqueue(Operator.ASSIGNMENT_REMAINDER);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '/': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: /");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                accumulated.enqueue(Operator.DIVISION);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: /");
                                accumulated.enqueue(Operator.ASSIGNMENT_DIVISION);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '*': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: *");
                        c = source.next();
                        switch(c)
                        {
                            default: {
                                if(last != null && (last.isValidOperand() || last == Operator.INDIRECTION))
                                    accumulated.enqret(Operator.INDIRECTION);
                                else accumulated.enqueue(Operator.MULTIPLICATION);
                                source.move(-1);
                            } break;
                            case '=': {
                                if(!source.canPeek(1))
                                    throw new CompilerError("Unexpected character: *");
                                accumulated.enqueue(Operator.ASSIGNMENT_MULTIPLICATION);
                            } break;
                        }
                        return accumulated.dequeue();
                    }
                    
                    case '~': {
                        builder.flush();
                        if(!source.canPeek(1))
                            throw new CompilerError("Unexpected character: ~");
                        return accumulated.enqret(Operator.BITWISE_NOT);
                    }
                    
                    
                    default: {
                        builder.append(c);
                    } break;
                }
            }
            
            /*if(!builder.isEmpty())
                throw new CompilerError("Unexpected End of File");*/
        }
        catch(EOFException ex)
        {
            if(!builder.canFinish())
                throw new CompilerError("Unexpected End of File");
            return builder.isEmpty() ? null : builder.decode();
        }
    }
    
    public final Statement parseInlineInstruction(CodeReader source, ErrorList errors, CodeFragment... preFragments) throws CompilerError
    {
        LinkedList<CodeFragment> frags = new LinkedList<>();
        if(preFragments != null && preFragments.length > 0)
            frags.addAll(Arrays.asList(preFragments));
        CodeFragment frag;
        int firstLine = source.getCurrentLine();
        while((frag = parseFragment(source, frags.isEmpty() ? null : frags.getLast(), true, errors)) != null)
        {
            if(frag == Stopchar.SEMICOLON)
                break;
            frags.add(frag);
        }
        CodeFragmentList list = frags.isEmpty()
                ? CodeFragmentList.empty(firstLine)
                : new CodeFragmentList(firstLine, frags);
        return StatementParser.parse(list);
    }
    
    public final CodeFragmentList parseInlineInstructionAsList(CodeReader source, Command last, ErrorList errors) throws CompilerError
    {
        LinkedList<CodeFragment> frags = new LinkedList<>();
        CodeFragment frag;
        int firstLine = source.getCurrentLine();
        while((frag = parseFragment(source, frags.isEmpty() ? last : frags.getLast(), true, errors)) != null)
        {
            if(frag == Stopchar.SEMICOLON)
                break;
            frags.add(frag);
        }
        return frags.isEmpty()
                ? CodeFragmentList.empty(firstLine)
                : new CodeFragmentList(firstLine, frags);
    }
    
    public final CodeFragmentList parseUntilScopeAsList(CodeReader source, Command last, ErrorList errors) throws CompilerError
    {
        LinkedList<CodeFragment> frags = new LinkedList<>();
        CodeFragment frag;
        int firstLine = source.getCurrentLine();
        while((frag = parseFragment(source, frags.isEmpty() ? last : frags.getLast(), true, errors)) != null)
        {
            frags.add(frag);
            if(frag.isScope())
                break;
        }
        return frags.isEmpty()
                ? CodeFragmentList.empty(firstLine)
                : new CodeFragmentList(firstLine, frags);
    }
    
    public final CodeFragmentList parseUntilScopeOrInlineAsList(CodeReader source, Command last, ErrorList errors) throws CompilerError
    {
        LinkedList<CodeFragment> frags = new LinkedList<>();
        CodeFragment frag;
        int firstLine = source.getCurrentLine();
        while((frag = parseFragment(source, frags.isEmpty() ? last : frags.getLast(), true, errors)) != null)
        {
            if(frag == Stopchar.SEMICOLON)
                break;
            frags.add(frag);
            if(frag.isScope())
                break;
        }
        return frags.isEmpty()
                ? CodeFragmentList.empty(firstLine)
                : new CodeFragmentList(firstLine, frags);
    }
    
    private CodeFragmentList parseSubStatement(CodeReader source, ErrorList errors) throws CompilerError
    {
        LinkedList<CodeFragment> frags = new LinkedList<>();
        CodeFragment frag;
        int firstLine = source.getCurrentLine();
        while((frag = parseFragment(source, frags.isEmpty() ? null : frags.getLast(), true, errors)) != null)
            frags.add(frag);
        return frags.isEmpty()
                ? CodeFragmentList.empty(firstLine)
                : new CodeFragmentList(firstLine, frags);
    }
    
    private Scope parseScope(CodeReader source, ErrorList errors) throws CompilerError
    {
        List<Instruction> instrs = InstructionParser.parse(source, this, errors);
        return new Scope(instrs);
    }
    
    private static void skipUntil(CodeReader source, char end, boolean isEndOfFileValid) throws EOFException
    {
        try
        {
            for(;;)
            {
                char c = source.next();
                if(c == end)
                    return;
            }
        }
        catch(EOFException ex)
        {
            if(!isEndOfFileValid)
                throw ex;
        }
    }
    
    private static CodeReader extractScope(CodeReader source, char cstart, char cend) throws CompilerError
    {
        int startIndex = source.getCurrentIndex();
        int scope = 0;
        try
        {
            for(;;)
            {
                char c = source.next();
                if(c == cstart)
                    scope++;
                else if(c == cend)
                {
                    if(scope == 0)
                        return source.subpart(startIndex, source.getCurrentIndex() - 1);
                    scope--;
                }
            }
        }
        catch(EOFException ex) { throw new CompilerError("Char " + cstart + " is not a valid char"); }
    }
    
    private static boolean isInteger(String str)
    {
        for(char c : str.toCharArray())
            if(!Character.isDigit(c))
                return false;
        return true;
    }
    
    
    
    
    private static final class CodeFragmentBuilder
    {
        private final CodeReader source;
        private final CodeQueue queue;
        private final StringBuilder sb = new StringBuilder(8);
        private final boolean canFinish;
        private boolean finishEnabled = true;
        
        private CodeFragmentBuilder(CodeReader source, CodeQueue queue, boolean canFinish)
        {
            this.source = Objects.requireNonNull(source);
            this.queue = Objects.requireNonNull(queue);
            this.canFinish = canFinish;
        }
        
        public final int length() { return sb.length(); }
        public final boolean isEmpty() { return sb.length() < 1; }
        public final void clear() { sb.delete(0, sb.length()); }
        
        public final void enableFinish() { finishEnabled = true; }
        public final void disableFinish() { finishEnabled = false; }
        
        public final boolean canFinish() { return canFinish && finishEnabled; }
        
        public final CodeFragmentBuilder append(byte value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(short value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(int value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(long value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(float value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(double value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(boolean value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(char value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(String value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(char[] value) { sb.append(value); return this; }
        public final CodeFragmentBuilder append(Object value) { sb.append(value); return this; }
        
        public final boolean flush() throws CompilerError, EOFException
        {
            if(isEmpty())
                return !queue.isEmpty();
            CodeFragment frag = decode();
            clear();
            queue.enqueue(frag);
            return true;
        }
        
        public final CodeFragment decode() throws CompilerError
        {
            if(isEmpty())
                throw new IllegalStateException();
            String str;
            switch(str = sb.toString())
            {
                case "0": case "0.0": return Literal.ZERO;
                case "1": case "1.0": return Literal.ONE;
                case "-1": case "-1.0": return Literal.MINUSONE;
                case "true": return Literal.TRUE;
                case "false": return Literal.FALSE;
                case "int": return DataType.INTEGER;
                case "float": return DataType.FLOAT;
                case "string": return DataType.STRING;
                case "array": return DataType.ARRAY;
                case "object": return DataType.OBJECT;
                case "length": return Operator.LENGTH;
                case "isdef": return Operator.ISDEF;
                case "typeid": return Operator.TYPEID;
                case "iterator": return Operator.ITERATOR;
                case "def": return Command.DEF;
                case "include": return Command.INCLUDE;
                case "if": return Command.IF;
                case "else": return Command.ELSE;
                case "for": return Command.FOR;
                case "while": return Command.WHILE;
                case "return": return Command.RETURN;
            }
            CodeFragment frag = Literal.decodeNumber(str);
            if(frag != null)
                throw new CompilerError("Invalid token: " + str);
            return frag;
        }
        
        @Override
        public final String toString() { return sb.toString(); }
    }
    
    private static final class CodeQueue
    {
        private final LinkedList<CodeFragment> accumulated = new LinkedList<>();
        
        public final CodeQueue enqueue(CodeFragment frag) { accumulated.add(frag); return this; }
        
        public final CodeFragment dequeue() { return accumulated.removeFirst(); }
        
        public final boolean isEmpty() { return accumulated.isEmpty(); }
        
        public final CodeFragment enqret(CodeFragment frag)
        {
            if(accumulated.isEmpty())
                return frag;
            CodeFragment first = accumulated.removeFirst();
            accumulated.add(frag);
            return first;
        }
    }
}