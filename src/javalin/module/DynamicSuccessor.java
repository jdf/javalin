/*
   Copyright 2008 Jonathan Feinberg

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package javalin.module;

import java.util.ArrayList;
import java.util.List;

import javalin.DoubleValue;
import javalin.Environment;
import javalin.expr.DoubleExpression;

/**
 * In evaluating a rule, a successor turns an environment into a
 * real-valued Module.
 * @author jdf
 *
 */
public class DynamicSuccessor implements Successor
{
	public final char letter;
	public List<DoubleExpression> params = null;

	public DynamicSuccessor(final char letter)
	{
		this.letter = letter;
	}

	public void addExpression(final DoubleExpression param)
	{
		if (params == null)
			params = new ArrayList<DoubleExpression>();
		params.add(param);
	}

	public Module evaluate(final Environment env)
	{
		final DynamicModule result = new DynamicModule(letter);
		if (params != null)
			for (final DoubleExpression expr : params)
				result.addParam(new DoubleValue.ConstantDoubleValue(expr.eval(env)));
		return result;
	}

	@Override
	public String toString()
	{
		return letter + (params == null ? "" : "(" + params + ")");
	}

}
