/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import kp.sgs.data.SGSArray;
import kp.sgs.data.SGSFloat;
import kp.sgs.data.SGSImmutableArray;
import kp.sgs.data.SGSImmutableObject;
import kp.sgs.data.SGSImmutableValue;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSObject;
import kp.sgs.data.SGSString;
import kp.sgs.data.SGSValue;
import kp.sgs.data.SGSValue.Type.ImmutableType;

/**
 *
 * @author Asus
 */
public final class SGSScriptIO
{
    private SGSScriptIO() {}
    
    public static final void write(SGSScript script, OutputStream out) throws IOException
    {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(SGSConstants.MAGIC_NUMBER);
        
        /* Constants */
        dos.writeInt(script.constants.length);
        for(SGSImmutableValue c : script.constants)
            writeValue(dos, c);
        
        /* Identifiers */
        dos.writeInt(script.identifiers.length);
        for(String id : script.identifiers)
            dos.writeUTF(id);
        
        /* Functions */
        dos.writeInt(script.functions.length);
        for(byte[] f : script.functions)
        {
            dos.writeInt(f.length);
            dos.write(f);
        }
    }
    public static final void write(SGSScript script, File file) throws IOException
    {
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)))
        {
            write(script, bos);
        }
    }
    
    
    public static final SGSScript read(InputStream in) throws IOException
    {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(in, 65536));
        if(dis.readInt() != SGSConstants.MAGIC_NUMBER)
            throw new IOException("Wrong " + SGSConstants.COMPILED_FILE_EXTENSION + " file format");
        
        /* Constants */
        SGSImmutableValue[] constants = new SGSImmutableValue[dis.readInt()];
        for(int i=0;i<constants.length;i++)
            constants[i] = readValue(dis);
        
        /* Identifiers */
        String[] identifiers = new String[dis.readInt()];
        for(int i=0;i<identifiers.length;i++)
            identifiers[i] = dis.readUTF();
        
        /* Functions */
        byte[][] functions = new byte[dis.readInt()][];
        for(int i=0;i<functions.length;i++)
        {
            functions[i] = new byte[dis.readInt()];
            dis.read(functions[i]);
        }
        
        return new SGSScript(constants, identifiers, functions);
    }
    public static final SGSScript read(File file) throws IOException
    {
        try(FileInputStream fis = new FileInputStream(file))
        {
            return read(fis);
        }
    }
    
    
    private static void writeValue(DataOutputStream dos, SGSValue value) throws IOException
    {
        dos.writeByte(value.getDataType());
        switch(value.getDataType())
        {
            case ImmutableType.UNDEFINED: break;
            case ImmutableType.INTEGER: dos.writeInt(value.toInt()); break;
            case ImmutableType.FLOAT: dos.writeDouble(value.toDouble()); break;
            case ImmutableType.STRING: dos.writeUTF(value.toString()); break;
            case ImmutableType.CONST_ARRAY: writeArray(dos, value.toArray()); break;
            case ImmutableType.CONST_OBJECT: writeObject(dos, value.toObject()); break;
            default: throw new IllegalArgumentException("Expected a immutable value in script constant pool");
        }
    }
    
    private static void writeArray(DataOutputStream dos, SGSArray array) throws IOException
    {
        if(array.isMutable())
            throw new IllegalArgumentException("Expected a const array in script constant pool");
        dos.writeInt(array.arrayLength());
        for(SGSValue value : array)
            writeValue(dos, value);
    }
    
    private static void writeObject(DataOutputStream dos, SGSObject object) throws IOException
    {
        if(object.isMutable())
            throw new IllegalArgumentException("Expected a const object in script constant pool");
        dos.writeInt(object.objectSize());
        for(Map.Entry<String, SGSValue> e : object)
        {
            dos.writeUTF(e.getKey());
            writeValue(dos, e.getValue());
        }
    }
    
    
    private static SGSImmutableValue readValue(DataInputStream dis) throws IOException
    {
        switch(dis.readInt())
        {
            case ImmutableType.UNDEFINED: return SGSValue.UNDEFINED;
            case ImmutableType.INTEGER:return new SGSInteger(dis.readInt());
            case ImmutableType.FLOAT: return new SGSFloat(dis.readDouble());
            case ImmutableType.STRING: return new SGSString(dis.readUTF());
            case ImmutableType.CONST_ARRAY: return readArray(dis);
            case ImmutableType.CONST_OBJECT: return readObject(dis);
            default: throw new IllegalArgumentException("Expected a immutable value in script constant pool");
        }
    }
    
    private static SGSImmutableValue readArray(DataInputStream dis) throws IOException
    {
        SGSValue[] array = new SGSValue[dis.readInt()];
        for(int i=0;i<array.length;i++)
            array[i] = readValue(dis);
        return new SGSImmutableArray(array);
    }
    
    private static SGSImmutableValue readObject(DataInputStream dis) throws IOException
    {
        int len = dis.readInt();
        HashMap<String, SGSValue> map = new HashMap<>(len);
        for(int i=0;i<len;i++)
        {
            String name = dis.readUTF();
            SGSValue value = readValue(dis);
            map.put(name, value);
        }
        return new SGSImmutableObject(map);
    }
}
