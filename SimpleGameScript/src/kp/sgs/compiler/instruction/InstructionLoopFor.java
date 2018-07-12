/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.Objects;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.parser.CodeFragment;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.CommandArguments;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Identifier;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Scope;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;
import kp.sgs.compiler.parser.Stopchar;

/**
 *
 * @author Asus
 */
public abstract class InstructionLoopFor extends Instruction
{
    final DataType type; //null == global variable
    final Statement action;
    
    private InstructionLoopFor(DataType type, Statement action)
    {
        this.type = type;
        this.action = Objects.requireNonNull(action);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.LOOP_FOR; }
    
    public static final Instruction create(CodeFragmentList list) throws CompilerError
    {
        if(list.length() < 2)
            throw new CompilerError("Malformed \"for\" command. Expected valid statement and scope after command.");
        CodeFragment frag;
        
        frag = list.get(0);
        if(!frag.isCommandArguments())
            throw new CompilerError("Malformed \"for\" command. Expected valid statement and scope after command. But found: " + frag);
        CommandArguments args = (CommandArguments) frag;
        
        frag = list.get(1);
        Statement action = frag.isScope() ? (Scope) frag : StatementParser.parse(list.subList(1));
        
        switch (args.getArgumentCount())
        {
            case 3: return createNumeric(args.getArgument(0), args.getArgument(1), args.getArgument(2), action);
            case 1: {
                if(args.getArgument(0).isEmpty())
                    throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}. But found: " + frag);
                CodeFragmentList[] parts = args.getArgument(0).split(Stopchar.TWO_POINTS);
                if(parts.length != 2)
                    throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}. But found: " + frag);
                return createGeneric(parts[0], parts[1], action);
            }
            default: throw new CompilerError("Malformed \"for\" command. Expected valid statement and scope after command. But found: " + frag);
        }
    }
    
    
    
    
    private static final NumericFor createNumeric(CodeFragmentList initsList, CodeFragmentList conditionList, CodeFragmentList endsList, Statement action) throws CompilerError
    {
        /* Inits and type */
        DataType type;
        Operation[] inits;
        if(initsList != null && !initsList.isEmpty())
        {
            if(DataType.isValid(initsList.get(0)))
            {
                type = DataType.parse(initsList.get(0));
                initsList = initsList.subList(1);
            }
            else type = null;
            
            CodeFragmentList[] parts = initsList.split(Stopchar.COMMA);
            if(parts == null || parts.length < 1 || (parts.length == 0 && parts[0].isEmpty()))
            {
                if(type == null)
                    throw new CompilerError("Malformed numeric \"for\" command. Expected for(<type|def>? <varname>*;<conditions>;<ends>*){<scope>}.");
                inits = null;
            }
            else
            {
                inits = new Operation[parts.length];
                for(int i=0;i<inits.length;i++)
                {
                    Statement stat = StatementParser.parse(parts[i]);
                    if(!stat.isOperation() || !((Operation) stat).isAssignment())
                        throw new CompilerError("Malformed numeric \"for\" command. Expected for(<type|def>? <varname>*;<conditions>;<ends>*){<scope>}.");
                    inits[i] = (Operation) stat;
                }
            }
        }
        else { inits = null; type = null; }
        
        /* Condition */
        Statement condition;
        if(conditionList != null && !conditionList.isEmpty())
        {
            condition = StatementParser.parse(conditionList);
        }
        else condition = null;
        
        /* Ends */
        Statement[] ends;
        if(endsList != null && !endsList.isEmpty())
        {
            CodeFragmentList[] parts = initsList.split(Stopchar.COMMA);
            if(parts == null || parts.length < 1 || (parts.length == 0 && parts[0].isEmpty()))
            {
                if(type == null)
                    throw new CompilerError("Malformed numeric \"for\" command. Expected for(<type|def>? <varname>*;<conditions>;<ends>*){<scope>}.");
                ends = null;
            }
            else
            {
                ends = new Statement[parts.length];
                for(int i=0;i<ends.length;i++)
                {
                    ends[i] = StatementParser.parse(parts[i]);
                }
            }
        }
        else ends = null;
        
        return new NumericFor(type, inits, condition, ends, action);
    }
    
    private static final GenericFor createGeneric(CodeFragmentList varnameList, CodeFragmentList iterableList, Statement action) throws CompilerError
    {
        DataType type;
        Identifier varname;
        switch(varnameList.length())
        {
            default: throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}.");
            case 1: {
                if(!varnameList.get(0).isIdentifier())
                    throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}.");
                type = null;
                varname = varnameList.get(0);
            } break;
            case 2: {
                type = DataType.parse(varnameList.get(1));
                if(!varnameList.get(1).isIdentifier())
                    throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}.");
                varname = varnameList.get(1);
            } break;
        }
        Statement iteratorGetter = StatementParser.parse(iterableList);
        
        return new GenericFor(type, varname, iteratorGetter, action);
    }
    
    private static final class NumericFor extends InstructionLoopFor
    {
        private final Operation[] inits;
        private final Statement condition;
        private final Statement[] ends;
        
        private NumericFor(DataType type, Operation[] inits, Statement condition, Statement[] ends, Statement action)
        {
            super(type, action);
            this.inits = inits == null ? new Operation[0] : inits;
            this.condition = condition;
            this.ends = ends == null ? new Statement[0] : ends;
        }
    }
    
    
    
    private static final class GenericFor extends InstructionLoopFor
    {
        private final Identifier varname;
        private final Statement iteratorGetter;
        
        private GenericFor(DataType type, Identifier varname, Statement iteratorGetter, Statement action)
        {
            super(type, action);
            this.varname = Objects.requireNonNull(varname);
            this.iteratorGetter = Objects.requireNonNull(iteratorGetter);
        }
    }
}
