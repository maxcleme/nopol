package fr.inria.lille.commons.spoon;

import java.io.File;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;
import fr.inria.lille.commons.io.FileHandler;

public class LocationFilter extends AbstractFilter<CtElement> {

	public LocationFilter(File classSourceFile, String codeSnippet) {
		super(CtElement.class);
		this.codeSnippet = codeSnippet;
		this.classSourceFile = classSourceFile;
	}
	
	@Override
	public boolean matches(CtElement element) {
		SourcePosition position = element.getPosition();
		if (position != null) {
			return  FileHandler.isSameFile(classSourceFile(), position.getFile()) && codeSnippet().equals(element.toString());
		}
		return false;
	}

	private File classSourceFile() {
		return classSourceFile;
	}
	
	private String codeSnippet() {
		return codeSnippet;
	}
	
	private File classSourceFile;
	private String codeSnippet;
}
