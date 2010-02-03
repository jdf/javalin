package javalin.ops;

import javalin.Renderer;
import javalin.module.DynamicModule;

abstract class RotationOp extends Op
{
	private final double defaultAngle;

	public RotationOp(final String repr, final double defaultAngle)
	{
		super(repr);
		this.defaultAngle = defaultAngle;
	}

	@Override
	public void execute(final Renderer renderer, final DynamicModule module)
	{
		final double angle = module.arity() == 0 ? defaultAngle : 2 * Math.PI
				* module.getParam(0).getValue() / 360;
		rotate(renderer, angle);
	}

	abstract protected void rotate(Renderer renderer, double angle);
}
