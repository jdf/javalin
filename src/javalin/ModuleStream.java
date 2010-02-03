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

import java.util.Set;

import javalin.module.Module;
import javalin.module.DynamicModule;

abstract public class ModuleStream
{
	public static final Module EOF = new DynamicModule('\0');

	private final Lookbehind lookbehind;
	private final Set<Character> ignore;

	public ModuleStream(final Rules rules, final Set<Character> ignore)
	{
		final int lookbehindSize = rules.getLookbehindSize();
		lookbehind = lookbehindSize == 0 ? null : new Lookbehind(lookbehindSize, ignore);
		this.ignore = ignore;
	}

	abstract public Module current();

	public Module lookahead(final int n)
	{
		int counted = 0;
		int posForward = 1;
		Module m = getForward(posForward);
		while (m != EOF)
		{
			if (!ignore.contains(m.getLetter()))
			{
				counted++;
				if (counted == n)
					return m;
			}
			m = getForward(++posForward);
		}
		return EOF;

	}

	abstract protected Module getForward(int n);

	public Module lookbehind(final int n)
	{
		if (lookbehind == null)
			throw new IllegalStateException("Lookbehind called when lookbehind size is 0");
		return lookbehind.get(n);
	}

	public void consume()
	{
		if (lookbehind != null)
			lookbehind.put(current());
		_consume();
	}

	abstract protected void _consume();
}
