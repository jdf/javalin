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

public interface DrawingGraphics
{
	void drawLine(float fromx, float fromy, float fromz, float tox, float toy, float toz);

	void setColor(float red, float green, float blue, float alpha);

	void setLineWeight(float weight);

	// { vx0, vy0, vz0, vx1, vy1, vz1, ... , vxn, vyn, vzn }
	void drawPolygon(float[] coords);

	// float[row][col][x=0,y=1,z=2]
	void drawPatch(float[][][] controlPoints, int gridSteps);
}
