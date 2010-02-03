package javalin.module;

import javalin.Environment;
import javalin.Renderer;

abstract public class ConstantModule implements Module, Successor, IPredecessor
{
	public static final ConstantModule PUSH = new ConstantModule('[') {
		@Override
		public void execute(Renderer renderer)
		{
			renderer.push();
		}
	};
	public static final ConstantModule POP = new ConstantModule(']') {
		@Override
		public void execute(Renderer renderer)
		{
			renderer.pop();
		}
	};
	public static final ConstantModule START_POLY = new ConstantModule('{') {
		@Override
		public void execute(Renderer renderer)
		{
			renderer.startPoly();
		}
	};
	public static final ConstantModule END_POLY = new ConstantModule('}') {
		@Override
		public void execute(Renderer renderer)
		{
			renderer.endPoly();
		}
	};
	public static final ConstantModule VERTEX = new ConstantModule('.') {
		@Override
		public void execute(Renderer renderer)
		{
			renderer.vertex();
		}
	};
	public static final ConstantModule TURN_AROUND = new ConstantModule('|') {
		@Override
		public void execute(final Renderer renderer)
		{
			renderer.turnLeft(Math.PI);
		}
	};

	public static ConstantModule createPatchInitModule(final int patchIndex)
	{
		return new ConstantModule('\uF000') {
			@Override
			public void execute(final Renderer renderer)
			{
				renderer.initPatch(patchIndex);
			}

			@Override
			public String toString()
			{
				return "@PS(" + patchIndex + ")";
			}
		};
	}

	public static ConstantModule createPatchControlModule(final int patchIndex,
			final int row, final int col)
	{
		return new ConstantModule('\uF001') {
			@Override
			public void execute(final Renderer renderer)
			{
				renderer.controlPoint(patchIndex, row, col);
			}

			@Override
			public String toString()
			{
				return "@PC(" + patchIndex + "," + row + "," + col + ")";
			}
		};
	}

	public static ConstantModule createPatchDrawModule(final int patchIndex,
			final int gridSteps)
	{
		return new ConstantModule('\uF001') {
			@Override
			public void execute(final Renderer renderer)
			{
				renderer.drawPatch(patchIndex, gridSteps);
			}

			@Override
			public String toString()
			{
				return "@PD(" + patchIndex + "," + gridSteps + ")";
			}
		};
	}

	private final char letter;

	public ConstantModule(final char letter)
	{
		this.letter = letter;
	}

	public char getLetter()
	{
		return letter;
	}

	public final Module evaluate(final Environment env)
	{
		return this;
	}

	public boolean match(final Module module, final Environment env)
	{
		return this == module;
	}

	@Override
	public String toString()
	{
		return String.valueOf(letter);
	}

	abstract public void execute(final Renderer renderer);
}
