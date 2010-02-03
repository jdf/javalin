package javalin.expr;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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

/**
 * An abstract expression.
 * 
 * @author Eric Bruneton
 */
abstract public class Exp implements Opcodes
{

	/*
	 * Returns the byte code of an Expression class corresponding to this
	 * expression.
	 */
	byte[] compile(final String name)
	{
		// class header
		final String[] itfs = { DoubleExpression.class.getName().replaceAll("\\.", "/") };
		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, name, null, "java/lang/Object", itfs);

		// default public constructor
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		// eval method
		mv = cw.visitMethod(ACC_PUBLIC, "eval", "(Ljava/util/Map;)D",
				"(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Double;>;)D", null);
		compile(mv);
		mv.visitInsn(DRETURN);
		// max stack and max locals automatically computed
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
		mv.visitLdcInsn(toString());
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		return cw.toByteArray();
	}

	/*
	 * Compile this expression. This method must append to the given code writer
	 * the byte code that evaluates and pushes on the stack the value of this
	 * expression.
	 */
	abstract void compile(MethodVisitor mv);

	public static class Constant extends Exp
	{
		private final double value;

		public Constant(final double value)
		{
			this.value = value;
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			mv.visitLdcInsn(new Double(value));
		}

		@Override
		public String toString()
		{
			return String.valueOf(value);
		}
	}

	public static class Param extends Exp
	{
		private final String key;

		public Param(final String key)
		{
			this.key = key;
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(key);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get",
					"(Ljava/lang/Object;)Ljava/lang/Object;");
			mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
		}

		@Override
		public String toString()
		{
			return key;
		}
	}

	abstract static class BinaryExp extends Exp
	{
		protected final Exp e1, e2;

		protected BinaryExp(final Exp e1, final Exp e2)
		{
			this.e1 = e1;
			this.e2 = e2;
		}
	}

	abstract static class BinaryOpExp extends BinaryExp
	{
		private final int op;

		protected BinaryOpExp(final Exp e1, final Exp e2, final int op)
		{
			super(e1, e2);
			this.op = op;
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			e1.compile(mv);
			e2.compile(mv);
			mv.visitInsn(op);
		}
	}

	public static class Add extends BinaryOpExp
	{
		public Add(final Exp e1, final Exp e2)
		{
			super(e1, e2, DADD);
		}

		@Override
		public String toString()
		{
			return "(+ " + e1 + " " + e2 + ")";
		}
	}

	public static class Sub extends BinaryOpExp
	{
		public Sub(final Exp e1, final Exp e2)
		{
			super(e1, e2, DSUB);
		}

		@Override
		public String toString()
		{
			return "(- " + e1 + " " + e2 + ")";
		}
	}

	public static class Mul extends BinaryOpExp
	{
		public Mul(final Exp e1, final Exp e2)
		{
			super(e1, e2, DMUL);
		}

		@Override
		public String toString()
		{
			return "(* " + e1 + " " + e2 + ")";
		}
	}

	public static class Div extends BinaryOpExp
	{
		public Div(final Exp e1, final Exp e2)
		{
			super(e1, e2, DDIV);
		}

		@Override
		public String toString()
		{
			return "(/ " + e1 + " " + e2 + ")";
		}

	}

	public static class Mod extends BinaryOpExp
	{
		public Mod(final Exp e1, final Exp e2)
		{
			super(e1, e2, DREM);
		}

		@Override
		public String toString()
		{
			return "(% " + e1 + " " + e2 + ")";
		}
	}

	abstract static class Cmp extends BinaryExp
	{
		private final int op;

		protected Cmp(final Exp e1, final Exp e2, final int op)
		{
			super(e1, e2);
			this.op = op;
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			e1.compile(mv);
			e2.compile(mv);
			final Label iftrue = new Label();
			final Label end = new Label();
			mv.visitInsn(DCMPL);
			mv.visitJumpInsn(op, iftrue);
			mv.visitLdcInsn(new Double(0));
			mv.visitJumpInsn(GOTO, end);
			mv.visitLabel(iftrue);
			mv.visitLdcInsn(new Double(1));
			mv.visitLabel(end);
		}
	}

	public static class GT extends Cmp
	{
		public GT(final Exp e1, final Exp e2)
		{
			super(e1, e2, IFGT);
		}

		@Override
		public String toString()
		{
			return "(> " + e1 + " " + e2 + ")";
		}

	}

	public static class LT extends Cmp
	{
		public LT(final Exp e1, final Exp e2)
		{
			super(e1, e2, IFLT);
		}

		@Override
		public String toString()
		{
			return "(< " + e1 + " " + e2 + ")";
		}

	}

	public static class GE extends Cmp
	{
		public GE(final Exp e1, final Exp e2)
		{
			super(e1, e2, IFGE);
		}

		@Override
		public String toString()
		{
			return "(>= " + e1 + " " + e2 + ")";
		}

	}

	public static class LE extends Cmp
	{
		public LE(final Exp e1, final Exp e2)
		{
			super(e1, e2, IFLE);
		}

		@Override
		public String toString()
		{
			return "(<= " + e1 + " " + e2 + ")";
		}

	}

	public static class EQ extends Cmp
	{
		public EQ(final Exp e1, final Exp e2)
		{
			super(e1, e2, IFEQ);
		}

		@Override
		public String toString()
		{
			return "(= " + e1 + " " + e2 + ")";
		}

	}

	public static class And extends BinaryExp
	{
		public And(final Exp e1, final Exp e2)
		{
			super(e1, e2);
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			e1.compile(mv);
			mv.visitLdcInsn(new Double(0));
			mv.visitInsn(DCMPL);

			final Label falsity = new Label();
			mv.visitJumpInsn(IFEQ, falsity);

			e2.compile(mv);
			mv.visitLdcInsn(new Double(0));
			mv.visitInsn(DCMPL);
			mv.visitJumpInsn(IFEQ, falsity);

			mv.visitLdcInsn(new Double(1));
			final Label end = new Label();
			mv.visitJumpInsn(GOTO, end);

			mv.visitLabel(falsity);
			mv.visitLdcInsn(new Double(0));

			mv.visitLabel(end);
		}

		@Override
		public String toString()
		{
			return "(&& " + e1 + " " + e2 + ")";
		}

	}

	public static class Or extends BinaryExp
	{
		public Or(final Exp e1, final Exp e2)
		{
			super(e1, e2);
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			e1.compile(mv);
			mv.visitLdcInsn(new Double(1));
			mv.visitInsn(DCMPL);

			final Label truth = new Label();
			mv.visitJumpInsn(IFEQ, truth);

			e2.compile(mv);
			mv.visitLdcInsn(new Double(1));
			mv.visitInsn(DCMPL);
			mv.visitJumpInsn(IFEQ, truth);

			mv.visitLdcInsn(new Double(0));
			final Label end = new Label();
			mv.visitJumpInsn(GOTO, end);

			mv.visitLabel(truth);
			mv.visitLdcInsn(new Double(1));

			mv.visitLabel(end);
		}

		@Override
		public String toString()
		{
			return "(|| " + e1 + " " + e2 + ")";
		}

	}

	protected abstract static class UnaryOp extends Exp
	{
		protected final Exp e;

		protected UnaryOp(final Exp e)
		{
			this.e = e;
		}
	}

	public static class Not extends UnaryOp
	{
		public Not(final Exp e)
		{
			super(e);
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			// computes !e1 by evaluating 1 - e1
			mv.visitLdcInsn(new Double(1));
			e.compile(mv);
			mv.visitInsn(DSUB);
		}

		@Override
		public String toString()
		{
			return "!(" + e + ")";
		}

	}

	public static class Neg extends UnaryOp
	{
		public Neg(final Exp e)
		{
			super(e);
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			e.compile(mv);
			mv.visitInsn(DNEG);
		}

		@Override
		public String toString()
		{
			return "-(" + e + ")";
		}

	}

	public static class UnaryMathMethod extends UnaryOp
	{
		final String method;

		public UnaryMathMethod(final String method, final Exp e)
		{
			super(e);
			this.method = method;
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			e.compile(mv);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", method, "(D)D");
		}

		@Override
		public String toString()
		{
			return method + "(" + e + ")";
		}
	}

	public static class BinaryMathMethod extends BinaryExp
	{
		final String method;

		public BinaryMathMethod(final String method, final Exp e1, final Exp e2)
		{
			super(e1, e2);
			this.method = method;
		}

		@Override
		void compile(final MethodVisitor mv)
		{
			e1.compile(mv);
			e2.compile(mv);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", method, "(DD)D");
		}

		@Override
		public String toString()
		{
			return method + "(" + e1 + "," + e2 + ")";
		}

	}

}