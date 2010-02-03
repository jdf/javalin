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

import java.util.ArrayList;
import java.util.List;

abstract class AbstractModule<T>
{
	private final char letter;
	public List<T> params = null;

	protected AbstractModule(final char letter)
	{
		this.letter = letter;
	}

	public void addParam(final T param)
	{
		if (params == null)
			params = new ArrayList<T>();
		params.add(param);
	}

	public int arity()
	{
		return params == null ? 0 : params.size();
	}

	public T getParam(final int i)
	{
		return params.get(i);
	}

	public char getLetter()
	{
		return letter;
	}

	@Override
	public String toString()
	{
		return getLetter() + (params == null ? "" : "(" + params + ")");
	}

}
