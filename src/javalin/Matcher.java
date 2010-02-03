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
package javalin;

import java.util.List;

import javalin.expr.BooleanExpression;
import javalin.module.ConstantModule;
import javalin.module.IPredecessor;
import javalin.module.Module;

public class Matcher
{
	private final IPredecessor strictPredecessor;
	private final List<IPredecessor> left;
	private final List<IPredecessor> right;
	private final BooleanExpression cond;

	public Matcher(final List<IPredecessor> left, final IPredecessor strictPredecessor,
			final List<IPredecessor> right, final BooleanExpression cond)
	{
		this.left = left;
		this.strictPredecessor = strictPredecessor;
		this.right = right;
		this.cond = cond;
	}

	public int getLookbehindSize()
	{
		return left == null ? 0 : left.size();
	}

	public boolean matches(final ModuleStream stream, final Environment env)
	{
		if (!strictPredecessor.match(stream.current(), env))
			return false;
		if (left != null) // lookbehind
			for (int i = 1; i <= left.size(); i++)
				if (!left.get(left.size() - i).match(stream.lookbehind(i), env))
					return false;
		if (right != null)
		{
			int lookahead = 1;
			int index = 0;

			while (index < right.size())
			{
				Module laModule = stream.lookahead(lookahead);
				final IPredecessor matchModule = right.get(index);
				if (matchModule == ConstantModule.POP || laModule == ConstantModule.PUSH)
				{
					while (laModule != ModuleStream.EOF && laModule != ConstantModule.POP)
						laModule = stream.lookahead(++lookahead);
					if (laModule == ModuleStream.EOF)
						return false;
				}
				else if (matchModule.match(laModule, env))
				{
					index++;
				}
				else
				{
					return false;
				}

				lookahead++;
			}
		}

		return cond == null || cond.eval(env);
	}

	@Override
	public String toString()
	{
		return (left == null ? "" : left + " < ") + strictPredecessor
				+ (right == null ? "" : " > " + right)
				+ (cond == null ? "" : " : " + cond);
	}
}
