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
package javalin.parsing;

import java.io.IOException;
import java.io.Reader;

import javalin.LSystem;
import javalin.LSystemLexer;
import javalin.LSystemParser;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

public class LSystemReader
{
	public static LSystem read(final Reader in) throws IOException
	{
		final ANTLRReaderStream input = new ANTLRReaderStream(Preprocessor.preprocess(in));
		final LSystemLexer lexer = new LSystemLexer(input);
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		final LSystemParser parser = new LSystemParser(tokens);
		try
		{
			return parser.lsystem();
		}
		catch (RecognitionException e)
		{
			throw new RuntimeException(e);
		}
	}

}
