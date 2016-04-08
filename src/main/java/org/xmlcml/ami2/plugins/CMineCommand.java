package org.xmlcml.ami2.plugins;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Util;

/** holds command + arguments for each chained command.
 * 
 * @author pm286
 *
 */
public class CMineCommand {

	private static final Logger LOG = Logger.getLogger(CMineCommand.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private CommandOption option;
	private String name;
	private AMIPluginOption pluginOption;
	private CMine cmine;

	public CMineCommand(CMine cmine, String name) {
		CMineParser.checkAlphabeticName(name);
		setName(name);
		this.cmine = cmine;
	}


	/** PluginOption is historic and should be removed later.
	 * 
	 * @return
	 */
	public AMIPluginOption getOrCreatePluginOption() {
		String pluginClassName = this.getClass().getPackage().getName()+"."+name+"."+Util.capitalise(name)+"PluginOption";
		try {
			Class<? extends Object> pluginClass = Class.forName(pluginClassName);
			pluginOption = (AMIPluginOption) pluginClass.newInstance();
			pluginOption.setCMineCommand(this);
		} catch (Exception e) {
			throw new RuntimeException("cannot create plugin option class: "+name+"; "+pluginClassName, e);
		}
		return pluginOption;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "{"+name+": option: "+ option+"}";
	}

	public void add(CommandOption option) {
		this.option = option;
	}

	public void setOption(CommandOption option) {
		this.option = option;
	}

	public CommandOption getOption() {
		return option;
	}

	public String getName() {
		return name;
	}

	/** gets first option with name.
	 * 
	 * @param name of command
	 * @return null if name is null or 
	 */
	public CommandOption getOptionByName(String name) {
		return option == null || name == null || !name.equals(option.getName()) ? null : option;
	}


	public String getOptionName() {
		return option == null ? null : option.getName();
	}

	public File getProjectDir() {
		return cmine.getProjectDir();
	}

	public List<Argument> getArgumentList() {
		return option == null ? null : option.getArgumentList();
	}


}
