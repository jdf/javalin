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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Preprocessor
{
	private static final Pattern DEFINE_STATEMENT = Pattern
			.compile("^\\s*#define\\s+([a-zA-Z]+)\\s+(.*)$");

	//	private static final Pattern COMMENT = Pattern.compile("//.*$");

	/**
	 * Closes the given reader.
	 * 
	 * @param in
	 * @return the LSystem grammar with #defines processed and comments removed
	 * @throws IOException
	 */
	public static Reader preprocess(final Reader in) throws IOException
	{
		final Map<Pattern, String> defines = new HashMap<Pattern, String>();
		final StringWriter out = new StringWriter(1000);
		final PrintWriter printer = new PrintWriter(out);
		final BufferedReader b = new BufferedReader(in);
		int lineno = 0;
		try
		{
			String line;
			while ((line = b.readLine()) != null)
			{
				lineno++;
				final Matcher m = DEFINE_STATEMENT.matcher(line);
				if (m.matches())
				{
					final Pattern key = Pattern.compile("\\b" + m.group(1) + "\\b");
					final String value = sub(m.groupCount() > 1 ? m.group(2) : "",
							defines);
					if (defines.put(key, value) != null)
						System.err.println("Warning: overwriting previous definition of "
								+ key + " at line " + lineno);
					printer.println();
				}
				else
				{
					printer.println(sub(line, defines));
				}
			}
		}
		finally
		{
			b.close();
		}
		return new StringReader(out.toString());
	}

	private static String sub(final String text, final Map<Pattern, String> defines)
	{
		String subbed = text;
		for (final Map.Entry<Pattern, String> define : defines.entrySet())
			subbed = define.getKey().matcher(subbed).replaceAll(define.getValue());
		return subbed;//COMMENT.matcher(subbed).replaceAll("");
	}
}
