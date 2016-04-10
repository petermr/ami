package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import nu.xom.Attribute;

/**
 * @author pm286
 *
 */
public class Argument implements Comparable<Argument> {

	public static final String MINUS_MINUS = "--";
	public static final String MINUS = "-";
	public static final String P = "-p";
	public static final String PROJECT = "project";
	public static final String Q = "-q";
	public static final String CTREE = "ctree";
	
	private static Map<String, String> abbreviationMap = new HashMap<String, String>();
	static {
		abbreviationMap.put(P, PROJECT);
		abbreviationMap.put(Q, CTREE);
	}
	
	private String name;
	private List<String> values;

	public Argument(String arg) {
		this.name = arg;
		values = new ArrayList<String>();
	}

	public Argument(String name, String values) {
		this.name = name;
		this.values = Arrays.asList(values.split("\\s+"));
	}

	public String getName() {
		return name;
	}

	public List<String> getValues() {
		return values;
	}
	
	public String toString() {
		return name+" "+values;
	}

	public String getArgumentString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" "+name);
		for (String value : values) {
			sb.append(" "+value);
		}
		return sb.toString();
	}

	public void add(String token) {
		values.add(token);
	}

	/** returns first arguments with name.
	 * 
	 * @param name
	 * @param argumentList
	 * @return first argument or null if no match or name == null or argumentList == null
	 */
	public static Argument getArgumentByName(String name, List<Argument> argumentList) {
		if (name != null && argumentList != null) {
			for (Argument arg : argumentList) {
				if (name.equals(arg.getName())) {
					return arg;
				}
			}
		}
		return null;
	}

	public static String toString(List<Argument> argumentList) {
		StringBuilder sb = new StringBuilder();
		sb.append("{a: ");
		sb.append(argumentList);
		sb.append("}");
		return sb.toString();
	}

	public static Argument createArgument(Attribute attribute) {
		Argument argument = null;
		if (attribute != null) {
			String value = attribute.getValue() == null ? "" : attribute.getValue().trim();
			argument = new Argument(attribute.getLocalName(), value);
		}
		return argument;
	}
	
	public static Attribute createAttribute(Argument argument) {
		Attribute attribute = null;
		if (argument != null) {
			List<String> values = argument.getValues();
			String value = StringUtils.join(values, " ");
			attribute = new Attribute(argument.getTrimmedName(), value);
		}
		return attribute;
	}

	private String getTrimmedName() {
		String name = getName();
		if (name == null) {
			return null;
		} else if (name.startsWith(MINUS_MINUS)) {
			return name.substring(2);
		} else if (name.startsWith(MINUS)) {
			String expanded = abbreviationMap.get(name);
			if (expanded == null) {
				throw new RuntimeException("Unknown abbreviated argument: "+name);
			}
			return expanded;
		} else {
			throw new RuntimeException("name must start with: "+MINUS);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Argument other = (Argument) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	public int compareTo(Argument o) {
		if (o == null || !(o instanceof Argument)) return 0;
		return this.toString().compareTo(o.toString());
	}

	public List<String> getMinussedNameAndValues() {
		List<String> ss = new ArrayList<String>();
		ss.add(getMinussedName());
		ss.addAll(values);
		return ss;
	}

	public String getMinussedName() {
		return MINUS_MINUS + name;
	}
	

	
}
