package dto;

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
}
