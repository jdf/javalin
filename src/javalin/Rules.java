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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javalin.module.Module;
import javalin.rules.Rule;

public class Rules
{
	private final List<Rule> rules = new ArrayList<Rule>();

	void add(final Rule rule)
	{
		rules.add(rule);
	}

	public List<Module> apply(final ModuleStream moduleStream)
	{
		if (moduleStream.current() == ModuleStream.EOF)
			throw new IllegalStateException("I can't apply any rules to EOF.");
		for (final Rule rule : rules)
		{
			final List<Module> replacement = rule.apply(moduleStream);
			if (replacement != null)
				return replacement;
		}
		return Collections.singletonList(moduleStream.current());
	}

	int getLookbehindSize()
	{
		int lb = 0;
		for (final Rule rule : rules)
			lb = Math.max(lb, rule.matcher.getLookbehindSize());
		return lb;
	}

	void check(final Set<Character> vocabulary)
	{
		for (final Rule r : rules)
			r.check(vocabulary);
	}

	public int size()
	{
		return rules.size();
	}

	@Override
	public String toString()
	{
		final StringBuilder retValue = new StringBuilder();
		retValue.append("Rules ( ").append(this.rules).append(" )");
		return retValue.toString();
	}

}
