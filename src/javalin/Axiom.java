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
import java.util.Set;

import javalin.module.Module;

public class Axiom extends ModuleStream
{
	private final List<Module> source;
	private int index = 0;

	public Axiom(final List<Module> source, final Rules rules,
			final Set<Character> ignore)
	{
		super(rules, ignore);
		this.source = source;
	}

	@Override
	protected Module getForward(final int n)
	{
		return index + n >= source.size() ? EOF : source.get(index + n);
	}

	@Override
	public Module current()
	{
		return index >= source.size() ? EOF : source.get(index);
	}

	@Override
	protected void _consume()
	{
		index++;
	}

	@Override
	public String toString()
	{
		return "[Axiom \"" + source.subList(0, index) + "<"
				+ source.subList(index, index + 1) + ">"
				+ (index < source.size() ? source.subList(index + 1, source.size()) : "")
				+ "\"]";
	}
}
