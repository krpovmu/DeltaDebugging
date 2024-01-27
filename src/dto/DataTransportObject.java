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

public class DataTransportObject {

	Module module;
	Command command;
	A4Reporter reporter = new A4Reporter();
	A4Options options = new A4Options();
	String filePath = new String();
	String coreType = new String();
	boolean trace = false;
	Map<Integer, Object> listWithIds = new HashMap<>();
	int step = 0;

	public int getStep() {
		return step;
	}

	public void setStep() {
		this.step++;
	}

	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public A4Reporter getReporter() {
		return reporter;
	}

	public void setReporter() {
		this.reporter = new A4Reporter();
	}

	public String getCoreType() {
		return coreType;
	}

	public void setCoreType(String coreType) {
		this.coreType = coreType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(A4Reporter reporter, String filePath) {
		this.module = CompUtil.parseEverything_fromFile(reporter, null, filePath);
	}

	public void setModulePred(A4Reporter reporter, String modelWithJustThePredicateToEvaluate) {
		this.module = CompUtil.parseEverything_fromString(reporter, modelWithJustThePredicateToEvaluate);
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Module module) {
		this.command = module.getAllCommands().get(0);
	}

	public A4Options getOptions() {
		return options;
	}

	public void setOptions() {
		this.options = new A4Options();
	}

	public void printNumericalOrderedList() {

		// this.listWithIds = sortList(list);

		System.out.println("=======================");
		System.out.println("ID : Element");
		System.out.println("=======================");

		for (Map.Entry<Integer, Object> entry : this.listWithIds.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		System.out.println("=======================");
	}

	public void sortList(List<Object> list) {
		this.listWithIds = giveNumericIdsToList(list);
		TreeMap<Integer, Object> sortedList = new TreeMap<>();
		sortedList.putAll(this.listWithIds);
	}

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

	public String printCoreAsNumber(List<Object> input, int checkResult) {
		List<Integer> coreId = elementsToID(input, this.listWithIds);
		return "Iteration "+ this.getStep() +" : " + coreId + " Result: " + ((checkResult == 1) ? "PASS" : (checkResult == 0) ? "UNRESOLVED" : "FAIL");
	}

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
}
