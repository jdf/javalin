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

import java.util.LinkedList;
import java.util.Set;

import javalin.module.Module;

public class Rewriter extends ModuleStream
{
	private final ModuleStream moduleStream;
	private final Rules rules;
	private final LinkedList<Module> lookahead = new LinkedList<Module>();

	public Rewriter(final ModuleStream source, final Rules rules,
			final Set<Character> ignore)
	{
		super(rules, ignore);
		this.moduleStream = source;
		this.rules = rules;
	}

	@Override
	public void _consume()
	{
		if (lookahead.getFirst() == EOF)
			throw new IllegalStateException("You can't consume EOF.");
		lookahead.removeFirst();
	}

	@Override
	public Module current()
	{
		while (lookahead.size() == 0)
			fill();
		return lookahead.getFirst();
	}

	@Override
	protected Module getForward(final int n)
	{
		while (lookahead.size() < n + 1 && lookahead.getLast() != EOF)
			fill();
		return lookahead.size() < n + 1 ? EOF : lookahead.get(n);
	}

	private void fill()
	{
		final Module c = moduleStream.current();
		if (c == EOF)
		{
			lookahead.add(EOF);
			return;
		}
		lookahead.addAll(rules.apply(moduleStream));
		moduleStream.consume();
	}

}
