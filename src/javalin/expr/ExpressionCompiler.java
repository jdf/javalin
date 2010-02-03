package javalin.expr;

/***
 * 
 * This class is a modified copy of a file from the ASM project, which
 * bears the following license:
 * 
 * ASM examples: examples showing how ASM can be used
 * Copyright (c) 2000-2007 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The original code was written by Eric Bruneton.
 */
import java.util.HashMap;
import java.util.Map;

public class ExpressionCompiler extends ClassLoader
{
	@SuppressWarnings("unchecked")
	public static DoubleExpression compile(final Exp exp)
	{
		final ExpressionCompiler compiler = new ExpressionCompiler();
		final byte[] b = exp.compile("AnonymousExpression");
		final Class<DoubleExpression> expClass = (Class<DoubleExpression>) compiler
				.defineClass("AnonymousExpression", b, 0, b.length);
		try
		{
			return expClass.newInstance();
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void main(final String[] args) throws Exception
	{
		final Exp exp = new Exp.Add(new Exp.Neg(new Exp.Param("y")),
				new Exp.BinaryMathMethod("pow", new Exp.UnaryMathMethod("sin",
						new Exp.Param("x")), new Exp.Constant(2)));
		final DoubleExpression iexp = compile(exp);

		final Map<String, Double> bindings = new HashMap<String, Double>();
		for (int i = 0; i < 10; ++i)
		{
			bindings.put("x", (double) i);
			bindings.put("y", (double) 3);
			final double result = iexp.eval(bindings);
			System.out.println(exp + " == " + result);
		}
	}

}
