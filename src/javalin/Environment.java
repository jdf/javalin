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

import java.util.HashMap;

@SuppressWarnings("serial")
public class Environment extends HashMap<String, Double>
{
	private final LSystem lSystem;

	public Environment(final LSystem system)
	{
		lSystem = system;
	}

	public void put(final char c, final double d)
	{
		put(String.valueOf(c), d);
	}

	@Override
	public Double get(final Object key)
	{
		final Double v = super.get(key);
		if (v != null)
			return v;
		if (lSystem.hasParam((String) key))
			return lSystem.getParam((String) key).getValue();
		return null;
	}
}
