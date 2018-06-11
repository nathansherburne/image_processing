package ch.makery.address.model;

import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class Parameter<T> extends TextField {
	private boolean optional;
	private String name;
	private Class<T> type;
	private T defaultValue;
	
	public Parameter(String name, Class<T> type) {
		this(name, type, false, null);
	}
	
	public Parameter(String name, Class<T> type, boolean optional, T defaultValue) {
		this.name = name;
		this.optional = optional;
		this.type = type;
		this.defaultValue = defaultValue;
		initializeTextFormatter();
	}
	
	public T getDefualtValue() {
		return defaultValue;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public boolean isOptional() {
		return optional;
	}
	
	public boolean hasValue() {
		return !(getText() == null || getText().trim().isEmpty());
	}
	
	public void initializeTextFormatter() {
		if(type == Integer.class ) {
			Pattern validIntegerText = Pattern.compile("\\d*");
	        TextFormatter<Integer> textFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), null, 
	            change -> {
	                String newText = change.getControlNewText() ;
	                if (validIntegerText.matcher(newText).matches()) {
	                    return change ;
	                } else return null ;
	            });
	        
	        setTextFormatter(textFormatter);
		}
		else if(type == Double.class) {
			Pattern validDoubleText = Pattern.compile("-?((\\d*)|(\\d+\\.\\d*))");
	        TextFormatter<Double> textFormatter = new TextFormatter<Double>(new DoubleStringConverter(), null, 
	            change -> {
	                String newText = change.getControlNewText() ;
	                if (validDoubleText.matcher(newText).matches()) {
	                    return change ;
	                } else return null ;
	            });
	        
	        setTextFormatter(textFormatter);
		}
	}
}
