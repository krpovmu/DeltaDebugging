package dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

/**
 * A class that encapsulates various configurations and states required throughout the Alloy analysis process.
 * It serves as a central data hub for passing information such as Alloy models, commands, and translator options
 * between different components of the system.
 */
public class DataTransportObject {

	Module module; // Represents the Alloy model to be analyzed.
	Command command; // The command to execute on the Alloy model.
	A4Reporter reporter = new A4Reporter(); // Reporter for Alloy analysis events.
	A4Options options = new A4Options(); // Options for the Alloy-to-Kodkod translator.
	String filePath = new String(); // Path to the Alloy model file.
	String coreType = new String(); // Type of core to analyze, e.g., facts or predicates.
	boolean trace = false; // Enables trace mode for detailed debugging information.
	Map<Integer, Object> listWithIds = new HashMap<>(); // Maps elements to unique IDs for analysis.
	int step = 0;  // Tracks the current step in the analysis process.

    /**
     * Gets the current step in the analysis process.
     *
     * @return The current step.
     */
	public int getStep() {
		return step;
	}
	
    /**
     * Increments the step counter by one.
     */
	public void setStep() {
		this.step++;
	}

    /**
     * Checks if trace mode is enabled.
     *
     * @return True if trace mode is enabled, false otherwise.
     */
	public boolean isTrace() {
		return trace;
	}

    /**
     * Enables or disables trace mode.
     *
     * @param trace True to enable trace mode, false to disable.
     */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

    /**
     * Gets the Alloy reporter.
     *
     * @return The Alloy reporter.
     */
	public A4Reporter getReporter() {
		return reporter;
	}

	/**
     * Resets the Alloy reporter to its default state.
     */
	public void setReporter() {
		this.reporter = new A4Reporter();
	}

    /**
     * Gets the type of core being analyzed.
     *
     * @return The core type.
     */
	public String getCoreType() {
		return coreType;
	}

    /**
     * Sets the type of core to be analyzed.
     *
     * @param coreType The core type.
     */
	public void setCoreType(String coreType) {
		this.coreType = coreType;
	}

    /**
     * Gets the file path of the Alloy model.
     *
     * @return The file path.
     */
	public String getFilePath() {
		return filePath;
	}

    /**
     * Sets the file path of the Alloy model.
     *
     * @param filePath The file path.
     */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

    /**
     * Gets the Alloy model.
     *
     * @return The Alloy model.
     */
	public Module getModule() {
		return module;
	}

    /**
     * Parses an Alloy model from a file and sets it as the current model.
     *
     * @param reporter The Alloy reporter.
     * @param filePath The path to the Alloy model file.
     */
	public void setModule(A4Reporter reporter, String filePath) {
		this.module = CompUtil.parseEverything_fromFile(reporter, null, filePath);
	}
	
    /**
     * Parses an Alloy model from a string and sets it as the current model.
     *
     * @param reporter The Alloy reporter.
     * @param modelWithJustThePredicateToEvaluate The Alloy model as a string.
     */
	public void setModulePred(A4Reporter reporter, String modelWithJustThePredicateToEvaluate) {
		this.module = CompUtil.parseEverything_fromString(reporter, modelWithJustThePredicateToEvaluate);
	}

    /**
     * Gets the command to execute on the Alloy model.
     *
     * @return The command.
     */
	public Command getCommand() {
		return command;
	}

    /**
     * Sets the command to execute on the Alloy model based on the first command found in the model.
     *
     * @param module The Alloy model.
     */
	public void setCommand(Module module) {
		this.command = module.getAllCommands().get(0);
	}

    /**
     * Gets the options for the Alloy-to-Kodkod translator.
     *
     * @return The translator options.
     */
	public A4Options getOptions() {
		return options;
	}

    /**
     * Resets the translator options to their default state.
     */
	public void setOptions() {
		this.options = new A4Options();
	}

	/**
	 * Prints the elements of a list in numerical order based on their assigned numeric IDs.
	 * This method is useful for displaying elements, such as unsatisfiable subsets or model components,
	 * in an ordered manner to facilitate analysis and debugging.
	 */
	public void printNumericalOrderedList() {

		System.out.println("=======================");
		System.out.println("ID : Element");
		System.out.println("=======================");

		for (Map.Entry<Integer, Object> entry : this.listWithIds.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		System.out.println("=======================");
	}

	/**
	 * Sorts a given list based on a specified ordering criteria. This method is intended
	 * to organize lists of model elements or diagnostic output in a consistent order
	 * for analysis, comparison, or display purposes.
	 *
	 * @param list The list to be sorted. The sorting criteria is the Id element.
	 */
	public void sortList(List<Object> list) {
		this.listWithIds = giveNumericIdsToList(list);
		TreeMap<Integer, Object> sortedList = new TreeMap<>();
		sortedList.putAll(this.listWithIds);
	}

	/**
	 * Assigns numeric IDs to each element in a given list. This method facilitates tracking
	 * and referencing elements, especially when dealing with large or complex Alloy model components.
	 * The numeric IDs can be used for sorting, identification, and display purposes.
	 *
	 * @param elements The list of elements to assign numeric IDs to.
	 * @return A map associating each element with its assigned numeric ID.
	 */
	public static Map<Integer, Object> giveNumericIdsToList(List<Object> elements) {
		Map<Integer, Object> elementIds = new HashMap<>();
		int nextId = 1;
		for (Object element : elements) {
			if (!elementIds.containsKey(element)) {
				elementIds.put(nextId, element);
				nextId++;
			}
		}
		return elementIds;
	}

	/**
	 * Prints the core elements of an unsatisfiable subset as numeric IDs. This method provides
	 * a concise way to represent and analyze the unsatisfiable core, especially when dealing with
	 * large models or complex unsatisfiability scenarios.
	 *
	 * @param input The list of core elements identified as part of the subset which is analyzed.
	 * @param checkResult The result of the analysis of that subset.
	 * @return String message with the information about actual iteration number analyzed.
	 */
	public String printCoreAsNumber(List<Object> input, int checkResult) {
		List<Integer> coreId = elementsToID(input, this.listWithIds);
		return "Iteration " + this.getStep() + " : " + coreId + " Result: " + ((checkResult == 1) ? "PASS" : (checkResult == 0) ? "UNRESOLVED" : "FAIL");
	}
	
	/**
	 * Converts a list of Alloy model elements into a list of unique identifiers, facilitating
	 * their tracking and management during the analysis process. This method is crucial for
	 * identifying and referencing specific elements within the model, particularly when
	 * performing operations such as delta debugging that require precise manipulation of
	 * individual components.
	 *
	 * @param input A list of model elements to be converted into identifiers. These elements
	 *              represent specific parts of the Alloy model under analysis.
	 * @param sortedList A map that associates unique integer identifiers with corresponding
	 *                   model elements. This map is used to ensure each element is assigned
	 *                   a distinct identifier and to facilitate the retrieval of elements
	 *                   based on their IDs.
	 * @return A list of integers representing the unique identifiers assigned to the input
	 *         model elements. These identifiers correspond to keys in the sortedList map,
	 *         allowing for efficient indexing and retrieval of elements during the analysis.
	 */
	public List<Integer> elementsToID(List<Object> input, Map<Integer, Object> sortedList) {
		List<Integer> output = new ArrayList<Integer>();
		for (Object compElem : input) {
			for (Map.Entry<Integer, Object> entry : sortedList.entrySet()) {
				if (compElem.equals(entry.getValue())) {
					output.add(entry.getKey());
					break;
				}
			}
		}
		return output;
	}

	/**
	 * Determines if the Alloy model is unsatisfiable. This method analyzes the model based on
	 * the provided configuration and data to determine if there exists an unsatisfiable subset,
	 * indicating that the model cannot be satisfied under the current conditions.
	 *
	 * @return true if the model is determined to be unsatisfiable, false otherwise.
	 */
	public boolean isModelUNSAT() {
		boolean isUNSAT = true;
		try {
			this.module = CompUtil.parseEverything_fromFile(this.reporter, null, this.filePath);
			A4Solution ans = TranslateAlloyToKodkod.execute_command(this.reporter, this.module.getAllReachableSigs(), this.command, this.options);
			if (ans.satisfiable()) {
				isUNSAT = false;
			}
		} catch (Exception e) {
			isUNSAT = true;
		}
		return isUNSAT;
	}
}
