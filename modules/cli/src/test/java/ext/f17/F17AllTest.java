package ext.f17;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scribble.cli.CommandLine;
import org.scribble.cli.CommandLineArgParser;
import org.scribble.cli.CommandLineException;
import org.scribble.ext.f17.main.F17SyntaxException;
import org.scribble.main.ScribbleException;
import org.scribble.util.ScribParserException;

import scribtest.AllTest;

/**
 * Runs all tests under good and bad root directories in Scribble.
 */
//@RunWith(value = Parameterized.class)
@RunWith(Parameterized.class)
public class F17AllTest extends AllTest
{
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
			String dir = ClassLoader.getSystemResource(F17AllTest.ALL_ROOT).getFile();

			if (File.separator.equals("\\")) // HACK: Windows
			{
				dir = dir.substring(1).replace("/", "\\");
			}
			
			//new CommandLine(this.example, CommandLineArgParser.JUNIT_FLAG, CommandLineArgParser.IMPORT_PATH_FLAG, dir).run();
			new CommandLine(this.example, CommandLineArgParser.JUNIT_FLAG, CommandLineArgParser.IMPORT_PATH_FLAG, dir, 
						CommandLineArgParser.F17_FLAG, "[F17AllTest]")  // HACK (cf. F17Main)
					.run();
			Assert.assertFalse("Expecting exception", this.isBadTest);
		}
		catch (F17SyntaxException e)  // HACK
		{
			System.out.println("[f17] Skipping: " + this.example);
		}
		catch (ScribbleException e)
		{
			Assert.assertTrue("Unexpected exception '" + e.getMessage() + "'", this.isBadTest);
		}
		catch (ScribParserException | CommandLineException e)
		{
			throw new RuntimeException(e);
		}
	}
}
