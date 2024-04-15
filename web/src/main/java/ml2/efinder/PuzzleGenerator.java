package ml2.efinder;

import java.io.File;
import java.util.concurrent.Callable;

import org.testng.reporters.Files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ml2.logic.games.GameGenerator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "puzzle-generator", mixinStandardHelpOptions = true, description = "Generates a puzzle")
public class PuzzleGenerator implements Callable<Integer> {
	
    @Option(required = true, names = { "-d", "--description" }, description = "Properties files")
    private File gameDescription;

    @Option(required = false, names = { "-inv", "--invariants" }, description = "Additional invariants")
    private File additionalInvariants;

    @Option(required = true, names = { "-o", "--output" }, description = "Output file for the game")
    private File output;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PuzzleGenerator()).execute(args);
        System.exit(exitCode);
    }

	@Override
	public Integer call() throws Exception {
		String description = Files.readFile(gameDescription);
		String invariants  = null;
		if (additionalInvariants != null)
			invariants = Files.readFile(additionalInvariants);
		
		long start = System.currentTimeMillis();
		GameGenerator generator = new GameGenerator(description, invariants);
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.writer().writeValue(output, generator.getGamePlayEngine());
		
		long end = System.currentTimeMillis();
		
		System.out.println(String.format("Time %.2f", (end - start) / 1_000.0));

		return 0;
	}
}
