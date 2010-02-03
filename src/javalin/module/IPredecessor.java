package javalin.module;

import javalin.Environment;

public interface IPredecessor
{

	public boolean match(final Module module, final Environment env);

}