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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javalin.module.ConstantModule;
import javalin.module.DynamicModule;
import javalin.module.Module;
import javalin.ops.Op;
import javalin.ops.PitchDownOp;
import javalin.ops.PitchUpOp;
import javalin.ops.RollLeftOp;
import javalin.ops.RollRightOp;
import javalin.ops.TurnLeftOp;
import javalin.ops.TurnRightOp;
import javalin.rules.Rule;

public class LSystem
{
	private static final boolean debug = false;

	private final List<Module> axiom = new ArrayList<Module>();
	private double angle = Double.NaN;
	private final Rules rules = new Rules();
	private final Set<Character> ignore = new HashSet<Character>();
	private final Map<Character, Op> actions = new HashMap<Character, Op>();
	private final Map<String, DoubleValue> formalParams = new LinkedHashMap<String, DoubleValue>();
	private final List<Double> actualParams = new ArrayList<Double>();

	public LSystem()
	{
		actions.put('#', Op.CHANGE_COLOR);
		actions.put(';', Op.CHANGE_COLOR);
	}

	public void generate(final int iterations, final Renderer renderer,
			final Double... args)
	{
		if (args.length < formalParams.size())
			throw new IllegalArgumentException("Expected " + formalParams.size()
					+ " arguments, got " + args.length);

		for (final Double d : args)
			actualParams.add(d);

		final Set<Character> safeIgnore = Collections.unmodifiableSet(ignore);
		ModuleStream source = new Axiom(axiom, rules, safeIgnore);
		for (int i = 0; i < iterations; i++)
			source = new Rewriter(source, rules, safeIgnore);
		Module iModule;
		int charsGenerated = 0;
		while ((iModule = source.current()) != ModuleStream.EOF)
		{
			if (debug)
			{
				charsGenerated++;
				System.err.print(iModule + " ");
			}
			if (iModule instanceof ConstantModule)
			{
				((ConstantModule) iModule).execute(renderer);
				if (debug)
					System.err.println();
			}
			else
			{
				final DynamicModule module = (DynamicModule) iModule;
				Op op = actions.get(module.getLetter());
				if (op == null)
				{
					op = Op.NOOP;
					actions.put(module.getLetter(), op);
				}
				if (debug)
				{
					System.err.println(op);
				}
				op.execute(renderer, module);
			}
			source.consume();
		}
		if (debug)
			System.err.println("\n" + charsGenerated + " characters generated");
	}

	void addParam(final String param)
	{
		if (formalParams.containsKey(param))
			throw new IllegalArgumentException("Duplicate param " + param);
		formalParams.put(param, new Parameter(formalParams.size()));
	}

	boolean hasParam(final String param)
	{
		return formalParams.containsKey(param);
	}

	DoubleValue getParam(final String param)
	{
		if (!formalParams.containsKey(param))
			throw new IllegalArgumentException("No such param: " + param);
		return formalParams.get(param);
	}

	void setIgnore(final String ignore)
	{
		for (int i = 0; i < ignore.length(); i++)
			this.ignore.add(ignore.charAt(i));
	}

	void addRule(final Rule rule)
	{
		if (debug)
			System.err.println(rule);
		rules.add(rule);
	}

	void addAction(final char c, final Op action)
	{
		if (debug)
			System.err.println(c + " -> " + action);
		if (actions.put(c, action) != null)
			throw new IllegalStateException("Already have an action for " + c);
	}

	void setAxiom(final List<Module> axiom)
	{
		if (this.axiom.size() > 0)
			throw new IllegalStateException("Already have an axiom");
		if (axiom == null || axiom.size() == 0)
			throw new IllegalArgumentException("null axiom");
		this.axiom.addAll(axiom);
	}

	void setAngle(final double angle)
	{
		if (!Double.isNaN(this.angle))
			throw new IllegalStateException("Already have an angle");
		if (Double.isNaN(angle))
			throw new IllegalArgumentException("Bad angle");
		this.angle = angle;
		actions.put('+', new TurnLeftOp(angle));
		actions.put('-', new TurnRightOp(angle));
		actions.put('&', new PitchDownOp(angle));
		actions.put('^', new PitchUpOp(angle));
		actions.put('\\', new RollLeftOp(angle));
		actions.put('/', new RollRightOp(angle));
	}

	void checkChar(final char c)
	{
		if (!actions.containsKey(c))
			throw new IllegalStateException("The character " + c
					+ " is not specified in the vocabulary.");
	}

	void check()
	{
		if (axiom == null)
			throw new IllegalStateException("No initial state specified.");
		if (actions.size() == 0)
			throw new IllegalStateException("No vocabulary specified.");
		if (rules.size() == 0)
			throw new IllegalStateException("No rules specified.");
		if (Double.isNaN(angle))
			throw new IllegalStateException("No angle specified.");

		rules.check(actions.keySet());
	}

	private class Parameter implements DoubleValue
	{
		private final int position;

		private Parameter(final int position)
		{
			this.position = position;
		}

		public double getValue()
		{
			return actualParams.get(position);
		}
	}

	@Override
	public String toString()
	{
		final String TAB = "\n    ";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("LSystem ( ").append(super.toString()).append(TAB).append(
				"axiom=").append(this.axiom).append(TAB).append("angle=").append(
				this.angle).append(TAB).append("rules=").append(this.rules).append(TAB)
				.append("actions=").append(this.actions).append(TAB).append(" )");

		return retValue.toString();
	}
}
