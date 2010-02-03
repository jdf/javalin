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

import java.text.DecimalFormat;

import javalin.DoubleValue;

public class DynamicModule extends AbstractModule<DoubleValue> implements Module
{
	public DynamicModule(final char letter)
	{
		super(letter);
	}

	private static final DecimalFormat F = new DecimalFormat("0.00");

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getLetter());
		if (arity() > 0)
		{
			sb.append('(');
			for (int i = 0; i < arity(); i++)
			{
				if (i > 0)
					sb.append(",");
				sb.append(F.format(getParam(i)));
			}
			sb.append(')');
		}
		return sb.toString();
	}
}
