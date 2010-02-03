package javalin;


public interface DoubleValue
{
	double getValue();

	public static class ConstantDoubleValue implements DoubleValue
	{
		private final double value;

		public ConstantDoubleValue(final double value)
		{
			this.value = value;
		}

		public double getValue()
		{
			return value;
		}
	}

}
