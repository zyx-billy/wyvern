package wyvern.stdlib.support.backend;

import wyvern.stdlib.support.backend.nominalWyvern.NwBytecode;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class BytecodeWrapper {
    public static final BytecodeWrapper bytecode = new BytecodeWrapper();
    public Object loadBytecode(String filename) {
        NwBytecode.Bytecode bytecode = null;
        try {
            CodedInputStream inputStream = CodedInputStream.newInstance(new FileInputStream(filename));
            inputStream.setRecursionLimit(3000);
            bytecode =  NwBytecode.Bytecode.parseFrom(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytecode;
    }

    public BigInteger byteStringToInt(ByteString byteString) {
        byte[] b = byteString.toByteArray();

        return new BigInteger(b);
    }

    public List<Object> dynToList(Object object) {
        return (List<Object>) object;
    }
}
