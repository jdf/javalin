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

import javalin.Environment;

public class Predecessor extends AbstractModule<Character> implements IPredecessor
{
	public static final Predecessor ANY = new Predecessor('*') {
		@Override
		public boolean match(Module module, Environment env)
		{
			return true;
		}
	};

	public Predecessor(final char letter)
	{
		super(letter);
	}

	public boolean match(final Module iModule, final Environment env)
	{
		if (getLetter() != iModule.getLetter())
			return false;
		final DynamicModule module = (DynamicModule) iModule;
		if (arity() > 0 && module.arity() != arity())
			return false;
		for (int i = 0; i < arity(); i++)
			env.put(getParam(i), module.getParam(i).getValue());
		return true;
	}
}
