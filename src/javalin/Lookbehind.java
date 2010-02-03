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

import static javalin.ModuleStream.EOF;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

import javalin.module.ConstantModule;
import javalin.module.Module;
import javalin.module.DynamicModule;

public class Lookbehind
{
	private final int size;
	private Module[] lookbehind;
	private final Set<Character> ignore;
	private final Stack<Module[]> stack = new Stack<Module[]>();

	public Lookbehind(final int size, final Set<Character> ignore)
	{
		this.size = size;
		this.ignore = ignore;
		lookbehind = makeLookbehind();
	}

	private DynamicModule[] makeLookbehind()
	{
		final DynamicModule[] buf = new DynamicModule[size];
		Arrays.fill(buf, EOF);
		return buf;
	}

	Module get(final int n)
	{
		if (n < 1)
			throw new IllegalArgumentException("Lookbehind expects an argument >= 1");
		if (n > size)
			throw new IllegalArgumentException("Attempt to retrieve lookbehind " + n
					+ " with lookbehind size of " + lookbehind.length);
		return lookbehind[lookbehind.length - n];
	}

	void put(final Module module)
	{
		if (module == ConstantModule.PUSH)
		{
			stack.push(lookbehind);
			lookbehind = makeLookbehind();
		}
		else if (module == ConstantModule.POP)
		{
			lookbehind = stack.pop();
		}
		else if (!ignore.contains(module.getLetter()))
		{
			for (int i = 0; i < lookbehind.length - 1; i++)
				lookbehind[i] = lookbehind[i + 1];
			lookbehind[lookbehind.length - 1] = module;
		}
	}
}
