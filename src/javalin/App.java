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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;

import javalin.draw.BoundingVolume;
import javalin.draw.Drawing;
import javalin.draw.DrawingGraphics;
import javalin.parsing.LSystemReader;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.antlr.runtime.RecognitionException;

public class App
{
	@SuppressWarnings("serial")
	static class ImgPanel extends JComponent
	{
		private BufferedImage img;

		public void setImage(final BufferedImage img)
		{
			if (img != this.img)
			{
				this.img = img;
				invalidate();
				repaint();
			}
		}

		@Override
		public Dimension getPreferredSize()
		{
			return img == null ? new Dimension(100, 100) : new Dimension(img.getWidth(),
					img.getHeight());
		}

		@Override
		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize()
		{
			return getPreferredSize();
		}

		@Override
		protected void paintComponent(final Graphics g)
		{
			g.drawImage(img, 0, 0, this);
		}
	}

	private static final double MAX_DIMENSION = 1000;

	private static BufferedImage dumpDrawing(final Drawing drawing, final Color color,
			final Color background) throws IOException
	{
		final BoundingVolume bounds = drawing.getBounds();
		final double scale = MAX_DIMENSION / bounds.maxDimension();
		final BufferedImage img = new BufferedImage(20 + (int) (scale * bounds.x
				.getLength()), 20 + (int) (scale * bounds.y.getLength()),
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) img.getGraphics();
		try
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

			g.setStroke(new BasicStroke((float) (1 / scale)));

			g.setColor(background);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.setColor(color);
			g.translate(10, 10);
			g.scale(scale, scale);
			g.translate(-bounds.x.min, -bounds.y.min);

			drawing.draw(new DrawingGraphics() {
				public void setColor(final float red, final float green,
						final float blue, final float alpha)
				{
					g.setColor(new Color(red, green, blue, alpha));
				}

				public void drawLine(final float fromx, final float fromy,
						final float fromz, final float tox, final float toy,
						final float toz)
				{
					g.draw(new Line2D.Double(fromx, fromy, tox, toy));
				}

				public void setLineWeight(final float weight)
				{
					g.setStroke(new BasicStroke((float) (weight / scale)));
				}

				public void drawPolygon(final float[] coords)
				{
					final GeneralPath p = new GeneralPath();
					for (int n = 0; n < coords.length / 3; n += 3)
						p.lineTo(coords[n], coords[n + 1]);
					g.fill(p);
				}

				public void drawPatch(final float[][][] controlPoints, final int gridSteps)
				{
					// TODO Auto-generated method stub

				}
			});
		}
		finally
		{
			g.dispose();
		}
		return img;
	}

	public static void main(final String[] args) throws RecognitionException, IOException
	{
		final String in = "petals";
		final int iters = 6;

		final LSystem lSystem = LSystemReader
				.read(new FileReader("samples/" + in + ".ls"));
		System.out.println("Generating " + in + "-" + iters + ": " + lSystem);
		final Drawing drawing = new Drawing();
		lSystem.generate(iters, drawing.getRenderer(), 10d, 20d, 30d, 40d, 50d);
		System.out.println("Dumping...");
		final BufferedImage img = dumpDrawing(drawing, Color.BLACK, Color.WHITE);
		System.out.println("Done.");
		//		ImageIO.write(img, "png", new File(in + "-" + iters + ".png"));

		final JFrame f = new JFrame(in + "-" + iters);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final ImgPanel p = new ImgPanel();
		f.getContentPane().add(p);
		f.setSize(img.getWidth(), img.getHeight() + 20);
		f.setVisible(true);
		p.setImage(img);
	}
}
