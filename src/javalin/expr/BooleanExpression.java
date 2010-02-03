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
package javalin.expr;

import java.util.Map;

public interface BooleanExpression
{
	public static final BooleanExpression ALWAYS_TRUE = new BooleanExpression() {
		public boolean eval(Map<String, Double> bindings)
		{
			return true;
		}

		@Override
		public String toString()
		{
			return "*";
		}
	};

	boolean eval(final Map<String, Double> bindings);
}
