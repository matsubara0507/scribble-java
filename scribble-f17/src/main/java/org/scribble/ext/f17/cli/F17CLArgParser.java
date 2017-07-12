/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.ext.f17.cli;

import java.util.HashMap;
import java.util.Map;

import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLineException;

public class F17CLArgParser extends CLArgParser
{
	// Unique flags
	public static final String F17_FLAG = "-f17";
	
	private static final Map<String, F17CLArgFlag> F17_UNIQUE_FLAGS = new HashMap<>();
	{
		F17CLArgParser.F17_UNIQUE_FLAGS.put(F17CLArgParser.F17_FLAG, F17CLArgFlag.F17);
	}

	private static final Map<String, F17CLArgFlag> F17_FLAGS = new HashMap<>();
	{
		F17CLArgParser.F17_FLAGS.putAll(F17CLArgParser.F17_UNIQUE_FLAGS);
		//F17CLArgParser.F17_FLAGS.putAll(F17CLArgParser.NON_UNIQUE_FLAGS);
	}

	private final Map<F17CLArgFlag, String[]> f17Parsed = new HashMap<>();
	
	public F17CLArgParser(String[] args) throws CommandLineException
	{
		super(args);  // Assigns this.args and calls parseArgs
	}		
	
	public Map<F17CLArgFlag, String[]> getF17Args()
	{
		return this.f17Parsed;
	}

	@Override
	protected boolean isFlag(String arg)
	{
		return F17CLArgParser.F17_FLAGS.containsKey(arg) || super.isFlag(arg);
	}
	
	// Pre: i is the index of the current flag to parse
	// Post: i is the index of the last argument parsed -- parseArgs does the index increment to the next current flag
	// Currently allows repeat flag decls: next overrides previous
	@Override
	protected int parseFlag(int i) throws CommandLineException
	{
		String flag = this.args[i];
		switch (flag)
		{
			// Unique flags
			case F17CLArgParser.F17_FLAG:
			{
				return parseF17(i);
			}
			default:
			{
				return super.parseFlag(i);
			}
		}
	}

	private int parseF17(int i) throws CommandLineException
	{
		if ((i + 1) >= this.args.length)
		{
			throw new CommandLineException("Missing simple global protocol name argument.");
		}
		String proto = this.args[++i];
		f17CheckAndAddNoArgUniqueFlag(F17CLArgParser.F17_FLAG, new String[] { proto });
		return i;
	}

	// FIXME: factor out with core arg parser -- issue is F17CLArgFlag is currently an unlreated type to CLArgFlag
	private void f17CheckAndAddNoArgUniqueFlag(String flag, String[] args) throws CommandLineException
	{
		F17CLArgFlag argFlag = F17CLArgParser.F17_UNIQUE_FLAGS.get(flag);
		if (this.f17Parsed.containsKey(argFlag))
		{
			throw new CommandLineException("Duplicate flag: " + flag);
		}
		this.f17Parsed.put(argFlag, args);
	}
}
