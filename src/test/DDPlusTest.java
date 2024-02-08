package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alloy.IDDPlusTest;
import dto.DataTransportObject;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprList;
import edu.mit.csail.sdg.ast.Func;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

/**
 * Implementation of the IDDPlusTest interface for executing satisfiability checks
 * on subsets of an Alloy model. This class utilizes the Alloy API for model manipulation
 * and evaluation, tailored to support delta debugging algorithms by testing for
 * satisfiability within specific parts of the model.
 *
 * @param <E> The element type that represents components of the Alloy model.
 */
public class DDPlusTest<E> implements IDDPlusTest {

	public static final int PASS = 1;
	public static final int FAIL = -1;
	public static final int UNRESOLVED = 0;

    /**
     * Performs a check on the specified subsets of an Alloy model to assess their satisfiability.
     * Depending on the analysis type (facts or predicates), it manipulates the model accordingly
     * and evaluates its satisfiability.
     *
     * @param chunk A subset of the model elements to be checked.
     * @param input Another subset of model elements, used in predicate removal scenarios.
     * @param dto   The DataTransportObject containing the current context and configurations for the analysis.
     * @return The result of the check: {@code PASS}, {@code FAIL}, or {@code UNRESOLVED}.
     */
    @Override
	public int check(List<Object> chunk, List<Object> input, DataTransportObject dto) {
		A4Solution ans = null;
		int result = FAIL;
		if (dto.getCoreType().equals("facts")) {
			try {
				Command cm = dto.getCommand().change(assemble((List<E>) chunk));
				ans = TranslateAlloyToKodkod.execute_command(dto.getReporter(), dto.getModule().getAllReachableSigs(), cm, dto.getOptions());
			} catch (Exception e) {
				result = UNRESOLVED;
			}
		} else if (dto.getCoreType().equals("predicates")) {
			try {
				String modelWithJustThePredicateToEvaluate = removePredicates((List<E>) chunk, (List<E>) input, dto.getFilePath());
				dto.setModulePred(dto.getReporter(), modelWithJustThePredicateToEvaluate);
				ans = TranslateAlloyToKodkod.execute_command(dto.getReporter(), dto.getModule().getAllReachableSigs(), dto.getModule().getAllCommands().get(0), dto.getOptions());
			} catch (Exception e) {
				result = UNRESOLVED;
			}
		} else {
			System.out.println("Invalid kind of core.");
		}
		if (ans == null) {
			result = UNRESOLVED;
		} else if (ans.satisfiable()) {
			result = PASS;
		}
		if (dto.isTrace()) {
			dto.setStep();
			System.out.println(dto.printCoreAsNumber(chunk, result));
		}
		return result;
	}

    /**
     * Calculates the difference between two lists, effectively performing a set subtraction (A - B).
     *
     * @param a The first list.
     * @param b The second list, elements of which are to be removed from the first.
     * @return A list containing elements of 'a' not present in 'b'.
     */
	private List<E> difference(List<E> a, List<E> b) {
		List<E> result = new LinkedList<E>();
		result.addAll(a);
		result.removeAll(b);
		return result;
	}

    /**
     * Assembles a logical expression from a list of elements, typically used to construct
     * a portion of the Alloy model for analysis.
     *
     * @param complement The list of elements to assemble into an expression.
     * @return An Expr representing the assembled elements.
     * @throws IOException If an error occurs during expression assembly.
     */
	private Expr assemble(List<E> complement) throws IOException {
		List<E> cand = new ArrayList<E>(complement);
		ExprList el = ExprList.make(null, null, ExprList.Op.AND, (List<? extends Expr>) cand);
		return el;
	}

    /**
     * Removes specified predicates from the Alloy model, aiding in the analysis of predicate impact
     * on satisfiability.
     *
     * @param partListPredicates     The list of predicates to retain in the model.
     * @param originalListPredicates The original list of all predicates in the model.
     * @param pathModel              The file path to the Alloy model.
     * @return A string representation of the modified Alloy model with selected predicates removed.
     * @throws IOException If an error occurs while reading or modifying the model file.
     */
	public String removePredicates(List<E> partListPredicates, List<E> originalListPredicates, String pathModel) throws IOException {
		String originalModel = new String(Files.readAllBytes(Paths.get(pathModel)));
		StringBuilder modifiedModel = new StringBuilder();
		List<E> listPredicatesToDelete = difference(originalListPredicates, partListPredicates);
		String[] lines = originalModel.split("\n");
		for (String line : lines) {
			boolean lineIsPredicateToDelete = lineIsPredicateToDelete(listPredicatesToDelete, line);
			if (!lineIsPredicateToDelete) {
				modifiedModel.append(line).append("\n");
			}
		}
		String runOptions = getRunOptions(originalModel);
		String modifiedRun = createRunPredicateCommand(partListPredicates, runOptions);
		return (modifiedModel.toString()).replaceAll("(?m)^run\\s*\\{.*\\R?", modifiedRun);
	}

    /**
     * Determines if a line from the Alloy model is associated with a predicate that should be deleted.
     *
     * @param predicates The list of predicates to check against.
     * @param line       The line from the model to examine.
     * @return True if the line corresponds to a predicate to delete, false otherwise.
     */
	public boolean lineIsPredicateToDelete(List<E> predicates, String line) {
		boolean isPredicateToDelete = false;
		for (int i = 0; i < predicates.size(); i++) {
			Func funcPredicate = (Func) predicates.get(i);
			String[] elementsNamePredicate = funcPredicate.label.toString().split("/");
			String namePredicate = elementsNamePredicate[elementsNamePredicate.length - 1];
			String regex = "\\bpred\\s+" + namePredicate + "\\s*(\\([^)]*\\))?\\s*\\{.*?\\}";
			Matcher matcherPred = findByRegex(regex, line);
			if (matcherPred.find()) {
				isPredicateToDelete = true;
				break;
			}
		}
		return isPredicateToDelete;
	}

    /**
     * Constructs a run command for the Alloy model, including only the specified predicates.
     *
     * @param partPredicateList The list of predicates to include in the run command.
     * @param runOptions        Additional options for the run command.
     * @return A string representing the run command for the modified model.
     */
	public String createRunPredicateCommand(List<E> partPredicateList, String runOptions) {
		String listOfPredicates = "";
		for (E elem : partPredicateList) {
			Func predicate = (Func) elem;
			String[] nameofPredicate = predicate.label.toString().split("/");
			String namePredicate = nameofPredicate[nameofPredicate.length - 1];
			listOfPredicates += namePredicate.concat(" ");
		}
		return "run {".concat(listOfPredicates).concat("}" + runOptions);
	}

    /**
     * Extracts the options for a run command from the original Alloy model.
     *
     * @param originalModel The original Alloy model as a string.
     * @return The run command options extracted from the model.
     */
	public String getRunOptions(String originalModel) {
		String runCommand = "";
		try (BufferedReader reader = new BufferedReader(new StringReader(originalModel))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("run")) {
					runCommand = line;
					break; // Remove this line if you want to find all occurrences
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] listRunCommand = runCommand.split("}");
		String runOptions = listRunCommand[listRunCommand.length - 1];
		return runOptions;
	}

    /**
     * Utility method to find matches in a string based on a regex pattern.
     *
     * @param regex         The regex pattern to match against.
     * @param originalModel The string to search within.
     * @return A Matcher object containing the results of the search.
     */
	public Matcher findByRegex(String regex, String originalModel) {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(originalModel);
		return matcher;
	}
}
