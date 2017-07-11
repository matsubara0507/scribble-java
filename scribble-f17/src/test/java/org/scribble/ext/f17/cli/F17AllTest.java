package org.scribble.ext.f17.cli;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scribble.cli.CLArgParser;
import org.scribble.cli.CommandLine;
import org.scribble.cli.CommandLineException;
import org.scribble.cli.ScribAllTest;
import org.scribble.ext.f17.main.F17SyntaxException;
import org.scribble.main.ScribbleException;

/**
 * Runs all tests under good and bad root directories in Scribble.
 */
//@RunWith(value = Parameterized.class)
@RunWith(Parameterized.class)
public class F17AllTest extends ScribAllTest
{
	private static int NUM_SKIPPED = 0;  // HACK
	
	public F17AllTest(String example, boolean isBadTest)
	{
		super(example, isBadTest);
	}

	@Override
	@Test
	public void tests() throws IOException, InterruptedException, ExecutionException
	{
		try
		{
			String dir = ClassLoader.getSystemResource(getTestRootDir()).getFile();

			if (File.separator.equals("\\")) // HACK: Windows
			{
				dir = dir.substring(1).replace("/", "\\");
			}
			
			String[] SKIP =  // HACK
				{
					"modules/cli/target/test-classes/bad/wfchoice/enabling/twoparty/Test01b.scr",  // f17 doesn't check choice subjects
					"modules/cli/target/test-classes/bad/wfchoice/gchoice/Choice02.scr",
					//"modules/cli/target/test-classes/bad/wfchoice/enabling/fourparty/Test01.scr"  // The original choice subject problem is gone, but we get a role-progress error instead (without fairness)
				};
			String foo = this.example.replace("\\", "/");
			for (String skip : SKIP)
			{
				if (foo.endsWith(skip))
				{
					F17AllTest.NUM_SKIPPED++;
					System.out.println("[f17] Manually skipping: " + this.example + " (" + F17AllTest.NUM_SKIPPED + " skipped.)");
					return;
				}
			}
			
			//new CommandLine(this.example, CommandLineArgParser.JUNIT_FLAG, CommandLineArgParser.IMPORT_PATH_FLAG, dir).run();
			new CommandLine(this.example, CLArgParser.JUNIT_FLAG, CLArgParser.IMPORT_PATH_FLAG, dir, 
						CLArgParser.F17_FLAG, "[F17AllTest]")  // HACK (cf. F17Main)
					.run();
			Assert.assertFalse("Expecting exception", this.isBadTest);
		}
		catch (F17SyntaxException e)  // HACK
		{
			F17AllTest.NUM_SKIPPED++;
			System.out.println("[f17] Skipping: " + this.example + "  (" + F17AllTest.NUM_SKIPPED + " skipped)");
		}
		catch (ScribbleException e)
		{
			Assert.assertTrue("Unexpected exception '" + e.getMessage() + "'", this.isBadTest);
		}
		//catch (ScribParserException | CommandLineException e)
		catch (CommandLineException e)
		{
			throw new RuntimeException(e);
		}
	}
}
