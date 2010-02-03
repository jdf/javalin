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

import java.util.List;
import java.util.Set;

import javalin.Environment;
import javalin.LSystem;
import javalin.Matcher;
import javalin.ModuleStream;
import javalin.module.Module;

abstract public class Rule
{
	public final Matcher matcher;
	private final Environment environment;

	protected Rule(final LSystem lSystem, final Matcher matcher)
	{
		environment = new Environment(lSystem);
		this.matcher = matcher;
	}

	public List<Module> apply(final ModuleStream source)
	{
		environment.clear();
		if (!matcher.matches(source, environment))
			return null;
		return apply(environment);
	}

	abstract protected List<Module> apply(final Environment environment);

	abstract public void check(final Set<Character> vocabulary);
}
