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
package javalin.draw;

import java.text.DecimalFormat;

import javalin.org.apache.commons.math.geometry.Vector3D;


/**
 * Immutable
 * @author jdf
 *
 */
public class BoundingVolume
{
	/**
	 * Immutable
	 * @author jdf
	 */
	public static class Range
	{
		public final double min;
		public final double max;

		Range()
		{
			min = Double.MAX_VALUE;
			max = -Double.MAX_VALUE;
		}

		private Range(final double min, final double max)
		{
			this.min = min;
			this.max = max;
		}

		Range with(final double value)
		{
			return new Range(value < min ? value : min, value > max ? value : max);
		}

		public double getCenter()
		{
			return min + ((max - min) / 2.0);
		}

		public double getLength()
		{
			return max - min;
		}

		private static final DecimalFormat F = new DecimalFormat("0.0");

		@Override
		public String toString()
		{
			return "[" + F.format(min) + "," + F.format(max) + "]";
		}
	}

	public final Range x, y, z;

	public BoundingVolume()
	{
		this(new Range(), new Range(), new Range());
	}

	private BoundingVolume(final Range x, final Range y, final Range z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BoundingVolume with(final Vector3D v)
	{
		return with(v.getX(), v.getY(), v.getZ());
	}

	public BoundingVolume with(final double xv, final double yv, final double zv)
	{
		return new BoundingVolume(x.with(xv), y.with(yv), z.with(zv));
	}

	public double maxDimension()
	{
		double max = x.getLength();
		max = Math.max(max, y.getLength());
		max = Math.max(max, z.getLength());
		return max;
	}

	public Vector3D getCenter()
	{
		return new Vector3D(x.getCenter(), y.getCenter(), z.getCenter());
	}

	public double boundingSphereRadius()
	{
		final double m = maxDimension();
		return Math.sqrt(2 * m * m);
	}

	@Override
	public String toString()
	{
		return "x:" + x + ", y:" + y + ", z:" + z;
	}
}
