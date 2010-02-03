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
package javalin;

public interface Renderer
{
	public void push();

	public void pop();

	public void startPoly();

	public void vertex();

	public void endPoly();

	public void initPatch(int patchIndex);

	public void controlPoint(int patchIndex, int row, int col);

	public void drawPatch(int patchIndex, int gridSteps);

	public void setLineWidth(final double width);

	public void setColor(final double red, final double green, final double blue,
			final double alpha);

	public void draw(final double length);

	public void move(final double length);

	public void turnLeft(final double angle);

	public void turnRight(final double angle);

	public void pitchUp(final double angle);

	public void pitchDown(final double angle);

	public void rollLeft(final double angle);

	public void rollRight(final double angle);

	public void ellipse(final double width, final double height);
}