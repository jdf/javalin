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
package javalin.ops;

import javalin.Renderer;
import javalin.module.DynamicModule;

public abstract class Op
{
	public static Op NOOP = new Op("noop") {
		@Override
		public void execute(final Renderer renderer, final DynamicModule module)
		{
			// noop
		}
	};

	public static Op CHANGE_COLOR = new Op("changeColor") {
		@Override
		public void execute(Renderer renderer, DynamicModule module)
		{
			final int arity = module.arity();
			if (arity == 0)
				throw new IllegalStateException(
						"The change color operator must take from 1 to 4 args");
			double a = module.getParam(0).getValue();
			if (arity == 1)
			{
				renderer.setColor(a, a, a, 1);
				return;
			}
			double b = module.getParam(1).getValue();
			if (arity == 2)
			{
				renderer.setColor(a, a, a, b);
				return;
			}
			double c = module.getParam(2).getValue();
			if (arity == 3)
			{
				renderer.setColor(a, b, c, 1);
				return;
			}
			renderer.setColor(a, b, c, module.getParam(3).getValue());
		}
	};

	public static Op TURN_AROUND = new Op("turnAround") {
		@Override
		public void execute(final Renderer renderer, final DynamicModule module)
		{
			renderer.turnLeft(Math.PI);
		}
	};

	public static Op ELLIPSE = new Op("ellipse") {
		@Override
		public void execute(final Renderer renderer, final DynamicModule module)
		{
			if (module.arity() == 0)
				renderer.ellipse(1, 1);
			else if (module.arity() == 1)
				renderer.ellipse(module.getParam(0).getValue(), module.getParam(0)
						.getValue());
			else
				renderer.ellipse(module.getParam(0).getValue(), module.getParam(1)
						.getValue());
		}
	};
	public static Op MOVE = new Op("move") {
		@Override
		public void execute(final Renderer renderer, final DynamicModule module)
		{
			renderer.move(module.arity() == 0 ? 1 : module.getParam(0).getValue());
		}
	};
	public static Op DRAW = new Op("draw") {
		@Override
		public void execute(final Renderer renderer, final DynamicModule module)
		{
			renderer.draw(module.arity() == 0 ? 1 : module.getParam(0).getValue());
		}
	};

	private final String repr;

	public Op(final String repr)
	{
		this.repr = repr;
	}

	@Override
	public String toString()
	{
		return repr;
	}

	abstract public void execute(final Renderer renderer, final DynamicModule module);

}
