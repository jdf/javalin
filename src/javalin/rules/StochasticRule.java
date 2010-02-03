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
package javalin.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javalin.Environment;
import javalin.LSystem;
import javalin.Matcher;
import javalin.module.Module;
import javalin.module.Successor;

public class StochasticRule extends Rule
{
	static class Production
	{
		protected final float probability;
		protected final List<Successor> replacement;

		public Production(final float probability, final List<Successor> replacement)
		{
			this.probability = probability;
			this.replacement = replacement;
		}
	}

	private final List<Production> productions = new ArrayList<Production>();
	private final Random random;

	public StochasticRule(final LSystem lSystem, final Matcher matcher)
	{
		super(lSystem, matcher);
		random = new Random(System.currentTimeMillis());
	}

	public void addProduction(final float probability, final List<Successor> replacement)
	{
		productions.add(new Production(probability, replacement));
		float sum = 0;
		for (final Production p : productions)
			sum += p.probability;
		if (sum > 1.001)
			throw new IllegalStateException("Probabilities for " + matcher
					+ " rule add up to " + sum);
	}

	@Override
	public List<Module> apply(final Environment env)
	{
		final double d = random.nextDouble();
		double sum = 0;
		for (final Production p : productions)
		{
			sum += p.probability;
			if (d < sum)
			{
				final List<Module> result = new ArrayList<Module>(p.replacement.size());
				for (final Successor successor : p.replacement)
					result.add(successor.evaluate(env));
				return result;
			}
		}
		throw new IllegalStateException("None of the stochastic rules matched for d == "
				+ d);
	}

	@Override
	public void check(final Set<Character> vocabulary)
	{
		double sum = 0;
		for (final Production p : productions)
			sum += p.probability;
		if (sum > 1.001)
			throw new IllegalStateException("Probabilities for " + matcher
					+ " rule add up to more than 1.");
		if (sum < .999)
			throw new IllegalStateException("Probabilities for " + matcher
					+ " rule add up to less than 1.");
	}

}
