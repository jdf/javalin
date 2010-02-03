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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javalin.Renderer;
import javalin.org.apache.commons.math.geometry.Vector3D;


public class Drawing
{
	private static enum DrawingOp
	{
		MOVE_TO, LINE_TO, LINEWIDTH, ELLIPSE, SETCOLOR, START_POLY, END_POLY, VERTEX, //
		INIT_PATCH, DRAW_PATCH, CONTROL_POINT
	}

	private static final boolean debug = false;

	private final List<DrawingOp> drawing = new ArrayList<DrawingOp>(1000000);
	private final FloatList args = new FloatList(12000000);
	private final Renderer renderer = new DrawingRenderer();

	private BoundingVolume bounds = new BoundingVolume();

	private static final void log(final Object msg)
	{
		System.err.println("Drawing: " + msg);
	}

	public Drawing()
	{
		moveTo(new Vector3D());
	}

	public Renderer getRenderer()
	{
		return renderer;
	}

	private void checkBounds(final double x, final double y, final double z)
	{
		bounds = bounds.with(x, y, z);
	}

	private void moveTo(final Vector3D pos)
	{
		moveWithOp(pos, DrawingOp.MOVE_TO);
	}

	private void lineTo(final Vector3D pos)
	{
		moveWithOp(pos, DrawingOp.LINE_TO);
	}

	private void moveWithOp(final Vector3D pos, final DrawingOp op)
	{
		final float x = (float) pos.getX();
		final float y = (float) pos.getY();
		final float z = (float) pos.getZ();
		checkBounds(x, y, z);
		drawing.add(op);
		args.add(x);
		args.add(y);
		args.add(z);
	}

	public BoundingVolume getBounds()
	{
		return bounds;
	}

	public void draw(final DrawingGraphics dg)
	{
		float[] pos = new float[] { 0, 0, 0 };
		final Stack<FloatList> polygonStack = new Stack<FloatList>();
		FloatList polygon = null;
		Map<Integer, float[][][]> patches = null;

		int argIndex = 0;
		final Iterator<DrawingOp> opIter = drawing.iterator();
		while (opIter.hasNext())
		{
			final DrawingOp op = opIter.next();

			switch (op)
			{
			case LINE_TO:
				float x = args.get(argIndex++);
				float y = args.get(argIndex++);
				float z = args.get(argIndex++);
				dg.drawLine(pos[0], pos[1], pos[2], x, y, z);

				pos[0] = x;
				pos[1] = y;
				pos[2] = z;
				break;
			case MOVE_TO:
				pos[0] = args.get(argIndex++);
				pos[1] = args.get(argIndex++);
				pos[2] = args.get(argIndex++);
				break;
			case SETCOLOR:
				dg.setColor(args.get(argIndex++), args.get(argIndex++), args
						.get(argIndex++), args.get(argIndex++));
				break;
			case START_POLY:
				if (polygon != null)
					polygonStack.push(polygon);
				polygon = new FloatList(64);
				break;
			case VERTEX:
				if (polygon == null)
					throw new IllegalStateException(
							"You can't create a vertex before a polygon is begun.");
				polygon.add(pos[0], pos[1], pos[2]);
				break;
			case END_POLY:
				if (polygon == null)
					throw new IllegalStateException(
							"You can't end a polygon before a polygon is begun.");
				dg.drawPolygon(polygon.toArray());
				polygon = polygonStack.size() > 0 ? polygonStack.pop() : null;
				break;

			case INIT_PATCH:
				int patchIndex = (int) args.get(argIndex++);
				if (patches == null)
					patches = new HashMap<Integer, float[][][]>();
				float[][][] patch;
				if (patches.containsKey(patchIndex))
					patch = patches.get(patchIndex);
				else
				{
					patch = new float[4][4][3];
					patches.put(patchIndex, patch);
				}
				for (int row = 0; row < 4; row++)
					for (int col = 0; col < 4; col++)
						for (int d = 0; d < 3; d++)
							patch[row][col][d] = 0;
				break;
			case CONTROL_POINT:
				patchIndex = (int) args.get(argIndex++);
				patch = patches.get(patchIndex);
				if (patch == null)
					throw new IllegalStateException("Patch " + patchIndex
							+ " has not been initialized.");
				int row = (int) args.get(argIndex++);
				int col = (int) args.get(argIndex++);
				patch[row][col][0] = args.get(argIndex++);
				patch[row][col][1] = args.get(argIndex++);
				patch[row][col][2] = args.get(argIndex++);
				break;
			case DRAW_PATCH:
				patchIndex = (int) args.get(argIndex++);
				patch = patches.get(patchIndex);
				if (patch == null)
					throw new IllegalStateException("You can't draw patch " + patchIndex
							+ " before it is initialized.");
				final int gridSteps = (int) args.get(argIndex++);
				dg.drawPatch(patch, gridSteps);
				break;
			}
		}
		if (polygon != null || polygonStack.size() > 0)
			throw new IllegalStateException("Unfinished polygon.");
	}

	private class DrawingRenderer implements Renderer
	{
		private final Stack<Turtle> stack = new Stack<Turtle>();
		private Turtle turtle = new Turtle();

		public void setColor(final double red, final double green, final double blue,
				final double alpha)
		{
			drawing.add(DrawingOp.SETCOLOR);
			args.add((float) red);
			args.add((float) green);
			args.add((float) blue);
			args.add((float) alpha);
		}

		public void setLineWidth(final double width)
		{
			drawing.add(DrawingOp.LINEWIDTH);
			args.add((float) width);
		}

		public void push()
		{
			if (debug)
				log("push");
			stack.push(turtle.copy());
		}

		public void pop()
		{
			if (debug)
				log("pop");
			turtle = stack.pop();
			moveTo(turtle.position);
		}

		public void draw(final double length)
		{
			if (debug)
				log("draw " + length);
			turtle = turtle.move(length);
			lineTo(turtle.position);
		}

		public void move(final double length)
		{
			if (debug)
				log("move " + length);
			turtle = turtle.move(length);
			moveTo(turtle.position);
		}

		public void turnLeft(final double angle)
		{
			if (debug)
				log("turn left " + angle);
			turtle = turtle.rotateZ(-angle);
		}

		public void turnRight(final double angle)
		{
			if (debug)
				log("turn right " + angle);
			turtle = turtle.rotateZ(angle);
		}

		public void pitchDown(final double angle)
		{
			if (debug)
				log("pitch down " + angle);
			turtle = turtle.rotateX(angle);
		}

		public void pitchUp(final double angle)
		{
			if (debug)
				log("pitch up " + angle);
			turtle = turtle.rotateX(-angle);
		}

		public void rollLeft(final double angle)
		{
			if (debug)
				log("roll left " + angle);
			turtle = turtle.rotateY(angle);
		}

		public void rollRight(final double angle)
		{
			if (debug)
				log("roll right " + angle);
			turtle = turtle.rotateY(-angle);
		}

		public void ellipse(final double width, final double height)
		{
			drawing.add(DrawingOp.ELLIPSE);
			args.add((float) width);
			args.add((float) height);
		}

		public void endPoly()
		{
			drawing.add(DrawingOp.END_POLY);
		}

		public void startPoly()
		{
			drawing.add(DrawingOp.START_POLY);
		}

		public void vertex()
		{
			drawing.add(DrawingOp.VERTEX);
		}

		public void controlPoint(final int patchIndex, final int row, final int col)
		{
			drawing.add(DrawingOp.CONTROL_POINT);
			args.add(patchIndex);
			args.add(row);
			args.add(col);
			final float x = (float) turtle.position.getX();
			final float y = (float) turtle.position.getY();
			final float z = (float) turtle.position.getZ();
			checkBounds(x, y, z);
			args.add(x);
			args.add(y);
			args.add(z);
		}

		public void drawPatch(final int patchIndex, final int gridSteps)
		{
			drawing.add(DrawingOp.DRAW_PATCH);
			args.add(patchIndex);
			args.add(gridSteps);
		}

		public void initPatch(final int patchIndex)
		{
			drawing.add(DrawingOp.INIT_PATCH);
			args.add(patchIndex);
		}

	}

}
