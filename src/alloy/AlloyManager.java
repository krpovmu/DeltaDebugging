package alloy;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ddmin.AbstractDDPlus;
import dto.DataTransportObject;
import edu.mit.csail.sdg.ast.Browsable;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.Func;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * The AlloyManager class orchestrates the delta debugging process for Alloy models.
 * It parses command-line arguments to configure the debugging session, including
 * selecting between analyzing facts or predicates and enabling trace mode for detailed output.
 * It manages the execution of the debugging process, including file validation,
 * checking model satisfiability, and extracting and analyzing the unsatisfiable core.
 */
public class AlloyManager<E> {
	
	/**
     * Main method to start the delta debugging process.
     * It initializes the debugging session based on command-line arguments, prepares
     * the DataTransportObject with the necessary configurations, and triggers the
     * debugging process to identify minimal unsatisfiable subsets.
     *
     * @param args Command-line arguments for configuring the debugging session.
     * @throws ArgumentParserException if there is an error parsing the command-line arguments.
     */
	public static void main(String[] args) throws ArgumentParserException {

		ArgumentParser parser = ArgumentParsers.newFor("DeltaDebugging").build().defaultHelp(true).description("Process a file passed as argument.");
		parser.addArgument("-i", "--input").required(true).help("The file to process");
		parser.addArgument("-f", "--facts").action(Arguments.storeTrue()).help("Analize facts errors in the alloy model");
		parser.addArgument("-p", "--predicates").action(Arguments.storeTrue()).help("Analize predicates errors in the alloy model");
		parser.addArgument("-t", "--trace").action(Arguments.storeTrue()).help("Print detailed stack of the process");

		try {
			Namespace res = parser.parseArgs(args);
			DataTransportObject dto = new DataTransportObject();
			dto.setFilePath(res.getString("input"));
			dto.setCoreType((res.getBoolean("facts")) ? "facts" : (res.getBoolean("predicates")) ? "predicates" : "choose something perejil");
			dto.setReporter();
			dto.setOptions();
			dto.setModule(dto.getReporter(), dto.getFilePath());
			dto.setCommand(dto.getModule());
			dto.setTrace(res.getBoolean("trace"));

			LinkedHashMap<String, Object> factsAndpredicates = new LinkedHashMap<String, Object>();
			AbstractDDPlus ddplus = new AbstractDDPlus();

			// System.out.println(dto.getFilePath());
			if (validateFile(dto.getFilePath())) {

				// validate if the model is UNSAT
				if (dto.isModelUNSAT()) {

					factsAndpredicates = getFactsOrPredicates(dto);
					// System.out.println(factsAndpredicates);

					if (!factsAndpredicates.isEmpty()) {
						for (Map.Entry<String, Object> element : factsAndpredicates.entrySet()) {
							List<Object> cores = new ArrayList<Object>();
							List<Object> valuesList = (List<Object>) element.getValue();

							if (!valuesList.isEmpty()) {
								if (dto.isTrace()) {
									dto.sortList(valuesList);
									dto.printNumericalOrderedList();
								}
								cores = AbstractDDPlus.dd(valuesList, dto);
							} else {
								System.out.println("Empty List Of " + element.getKey());
							}

							System.out.println("=======================");
							System.out.println("\n" + printCore(cores));
							System.out.println("=======================");

						}
					} else {
						System.out.println("There are not elements to analyze, have a good day");
					}
				} else {
					System.out.println("Invalid model, model is SAT");
				}
			} else {
				System.out.println("Invalid file not found");
			}
		} catch (ArgumentParserException e) {
			parser.handleError(e);
		}
	}
	
	 /**
     * Extracts facts or predicates from the Alloy model based on the configured core type.
     * This method segregates model elements into facts or predicates to be analyzed
     * during the debugging process.
     *
     * @param dto The DataTransportObject containing configuration and model information.
     * @return A LinkedHashMap with elements categorized as either facts or predicates.
     */
	public static LinkedHashMap<String, Object> getFactsOrPredicates(DataTransportObject dto) {

		LinkedHashMap<String, Object> listElementsAlloyModel = new LinkedHashMap<String, Object>();

		if (dto.getCoreType().equals("facts")) {
			// Facts extraction
			List<Object> facts = new ArrayList<Object>();
			for (int i = 0; i < dto.getModule().getAllFacts().size(); i++) {
				List<? extends Browsable> listFacts = (dto.getModule().getAllFacts().get(i).b).getSubnodes();
				for (Browsable fact : listFacts) {
					facts.add(fact);
				}
			}
			// Add facts to the list
			listElementsAlloyModel.put("Facts", facts);
		} else if (dto.getCoreType().equals("predicates")) {
			// Predicates and Functions
			List<Object> functions = new ArrayList<Object>();
			List<Object> predicates = new ArrayList<Object>();
			for (int i = 0; i < dto.getModule().getAllFunc().size(); i++) {
				Func element = dto.getModule().getAllFunc().get(i);
				// The idea is ignore run commands, run commands are always null in the explain
				if (!(element.explain() == null)) {
					// This validation tell me if the element is a predicate
					if (element.isPred) {
						predicates.add(element);
					}
					// otherwise the element is a function
					else {
						functions.add(element);
					}
				}
			}
			listElementsAlloyModel.put("Predicates", predicates);
			// listElementsAlloyModel.put("Function", functions);
		}
		return listElementsAlloyModel;
	}

    /**
     * Generates a string representation of the unsatisfiable core elements for printing.
     * This method formats the core elements extracted during the debugging process
     * for user-friendly output.
     *
     * @param core The list of core elements identified as part of the unsatisfiable subset.
     * @return A formatted string representation of the unsatisfiable core.
     */
	public static String printCore(List<Object> core) {
		String result = "";
		for (Object e : core) {
			if (e.getClass().getSimpleName().equals("Func")) {
				Func elem = (Func) e;
				result += "Pred: " + elem.pos.toShortString() + ": ";
				result += elem.toString() + "\n";
			} else {
				Expr elem = (Expr) e;
				result += "Fact: " + elem.pos.toShortString() + ": ";
				result += elem.toString() + "\n";
			}
		}
		return result;
	}

    /**
     * Validates the given file path to ensure the file exists and is not a directory.
     *
     * @param filePath The path of the file to validate.
     * @return true if the file exists and is valid, false otherwise.
     */
	public static boolean validateFile(String filePath) {
		File file = new File(filePath);

		// Check if the file exists and is not a directory
		if (!file.exists() || file.isDirectory()) {
			System.out.println("File does not exist or is a directory.");
			return false;
		}
		// Check if the file is empty
		if (file.length() == 0) {
			System.out.println("File is empty.");
			return false;
		}
		return true;
	}
}
