package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.InvokableValue;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.TreeWriter;

public class StringConstant extends AbstractTypedAST implements InvokableValue {
	private String value;
	
	public StringConstant(String s) { this.value = s; }

	@Override
	public Type getType() {
		return Str.getInstance();
	}

	@Override
	public Type typecheck() {
		return getType();
	}

	@Override
	public Value evaluate(Environment env) {
		return this;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(this.value);
	}
	
	public String getValue() {
		return value;
	}
	

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		String operator = exp.getOperationName();
		if (!operator.equals("+")) {
			throw new RuntimeException("forgot to typecheck!");
		}
		
		Value argValue =  exp.getArgument().evaluate(env);
		if (argValue instanceof StringConstant) {
			return new StringConstant(this.value + ((StringConstant) argValue).value);
		} else	if (argValue instanceof IntegerConstant) {
			return new StringConstant(this.value + ((IntegerConstant) argValue).getValue());
		} else
		{
//			shoudn't get here.
			throw new RuntimeException("forgot to typecheck!");
		}
	}

}
