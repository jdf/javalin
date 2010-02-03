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

import javalin.org.apache.commons.math.geometry.Rotation;
import javalin.org.apache.commons.math.geometry.Vector3D;

public class Turtle
{
	private static final Vector3D HEADING = Vector3D.plusJ; // up the Y axis

	public final Vector3D position;
	public final Rotation rotation;

	public Turtle()
	{
		this(new Vector3D(), new Rotation());
	}

	Turtle(final Vector3D position, final Rotation rotation)
	{
		this.position = position;
		this.rotation = rotation;
	}

	public Turtle copy()
	{
		return new Turtle(position, rotation);
	}

	public Turtle rotateX(final double angle)
	{
		return rotate(Vector3D.plusI, angle);
	}

	public Turtle rotateY(final double angle)
	{
		return rotate(Vector3D.plusJ, angle);
	}

	public Turtle rotateZ(final double angle)
	{
		return rotate(Vector3D.plusK, angle);
	}

	private Turtle rotate(final Vector3D axis, final double angle)
	{
		final Rotation rot = new Rotation(axis, angle);
		final Rotation newRotation = rotation.applyTo(rot);
		return new Turtle(position, newRotation);
	}

	public Turtle move(final double distance)
	{
		final Vector3D aStepInTheRightDirection = rotation.applyTo(HEADING);
		final Vector3D andTheRightSizeToo = aStepInTheRightDirection
				.scalarMultiply(distance);
		return new Turtle(position.add(andTheRightSizeToo), rotation);
	}

	@Override
	public String toString()
	{
		return "[turtle at " + position.getX() + "," + position.getY() + ","
				+ position.getZ() + " facing " + rotation + "]";
	}
}
