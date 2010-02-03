package javalin.expr;

import java.util.Map;

import javalin.DoubleValue;

public class SampleDouble implements DoubleExpression
{
	private final DoubleValue v = new DoubleValue.ConstantDoubleValue(42);

	public double eval(final Map<String, Double> bindings)
	{
		return v.getValue();
	}
}
