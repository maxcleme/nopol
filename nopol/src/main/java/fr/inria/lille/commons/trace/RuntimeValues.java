package fr.inria.lille.commons.trace;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import xxl.java.container.classic.MetaMap;
import xxl.java.container.classic.MetaSet;
import xxl.java.support.GlobalToggle;
import fr.inria.lille.commons.trace.collector.ValueCollector;

public class RuntimeValues<T> extends GlobalToggle {

	public static <T> RuntimeValues<T> newInstance() {
		int instanceNumber = numberOfInstances();
		RuntimeValues<T> newInstance = new RuntimeValues<T>(instanceNumber);
		allInstances().put(instanceNumber, newInstance);
		return newInstance;
	}
	
	public static RuntimeValues<?> instance(int instanceID) {
		return allInstances().get(instanceID);
	}
	
	@Override
	protected void reset() {
		specifications().clear();
	}
	
	@Override
	protected String globallyAccessibleName() {
		return format("%s.instance(%d)", getClass().getName(), instanceID());
	}
	
	public String invocationOnCollectionOf(String variableName) {
		return invocationOnCollectionOf(variableName, variableName);
	}
	
	public String invocationOnCollectionOf(String codeSource, String executableCode) {
		String quoatationSafeName = codeSource.replace("\"", "\\\"");
		return globallyAccessibleName() + format(".collectInput(\"%s\", %s)", quoatationSafeName, executableCode);
	}
	
	public String invocationOnOutputCollection(String outputName) {
		return globallyAccessibleName() + format(".collectOutput(%s)", outputName, outputName);
	}
	
	public String invocationOnCollectionEnd() {
		return globallyAccessibleName() + ".collectionEnds()";
	}
	
	public void collectInput(String variableName, Object value) {
		ValueCollector.collectFrom(variableName, value, valueBuffer());
	}
	
	public void collectOutput(Object output) {
		outputBuffer = output;
	}
	
	@SuppressWarnings("unchecked")
	public void collectionEnds() {
		specifications().add(new Specification<T>(valueBuffer(), (T) outputBuffer()));
		flush();
	}
	
	public boolean isEmpty() {
		return specifications().isEmpty();
	}
	
	public Collection<Specification<T>> specifications() {
		return specifications;
	}

	protected RuntimeValues(int instanceID) {
		this.instanceID = instanceID;
		specifications = MetaSet.newHashSet();
		flush();
	}
	
	private Integer instanceID() {
		return instanceID;
	}
	
	private static int numberOfInstances() {
		return allInstances().size();
	}
	
	protected Map<String, Object> valueBuffer() {
		return valueBuffer;
	}
	
	protected Object outputBuffer() {
		return outputBuffer;
	}
	
	protected void flush() {
		outputBuffer = null;
		valueBuffer = MetaMap.newHashMap();
	}
	
	private static Map<Integer, RuntimeValues<?>> allInstances() {
		if (allInstances == null) {
			allInstances = MetaMap.newHashMap();
		}
		return allInstances;
	}
	
	private int instanceID;
	private Object outputBuffer;
	private Map<String, Object> valueBuffer;
	private Collection<Specification<T>> specifications;
	private static Map<Integer, RuntimeValues<?>> allInstances;
}