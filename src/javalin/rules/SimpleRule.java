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
import java.util.Set;

import javalin.Environment;
import javalin.LSystem;
import javalin.Matcher;
import javalin.module.Module;
import javalin.module.Successor;

public class SimpleRule extends Rule
{
	private final List<Successor> replacement;

	public SimpleRule(final LSystem lSystem, final Matcher matcher,
			final List<Successor> replacement)
	{
		super(lSystem, matcher);
		this.replacement = replacement;
	}

	@Override
	public List<Module> apply(final Environment environment)
	{
		final List<Module> result = new ArrayList<Module>(replacement.size());
		for (final Successor successor : replacement)
			result.add(successor.evaluate(environment));
		return result;
	}

	@Override
	public void check(final Set<Character> vocabulary)
	{
	}

	@Override
	public String toString()
	{
		return matcher + " -> " + replacement + "";
	}
}
