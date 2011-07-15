/*
   Copyright 2011 Christoph Heinig

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package de.chdev.artools.loga.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.framework.Bundle;

import de.chdev.artools.loga.Activator;
import de.chdev.artools.loga.controller.ActlController;
import de.chdev.artools.loga.controller.ApiController;
import de.chdev.artools.loga.controller.EsclController;
import de.chdev.artools.loga.controller.FltrController;
import de.chdev.artools.loga.controller.SqlController;
import de.chdev.artools.loga.controller.WflgController;

public class KeywordLoader {

	private static KeywordLoader instance;
	private static List<String> supportedLanguages = new ArrayList<String>();
	//	private static CompositeConfiguration configuration;
	private static Map<String, Configuration> localKeywords = new HashMap<String, Configuration>();
	private static Map<String, Configuration> nameConfigMap = new HashMap<String, Configuration>();


	private KeywordLoader(){
		
	}
	
	public static Configuration getConfiguration(String prefix){
		return instance.localKeywords.get(prefix);
	}
	
	public static void initialize(Reader reader){
		instance = new KeywordLoader();
		instance.buildLocalKeywordMap(reader);
	}
	
//	private static Configuration load() {
//		if (configuration == null) {
//			try {
//				configuration = new CompositeConfiguration();
//
//				PropertiesConfiguration keywords_de = new PropertiesConfiguration(
//						"config/keywords_de.properties");
//				configuration.addConfiguration(keywords_de);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		return configuration;
//	}

	private List<Configuration> getAllConfigurations() {
		List<Configuration> localConfigurationList = new ArrayList<Configuration>();
		try {
//			File path = new File("./config");
			
//			File[] listFiles = path.listFiles();
			
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith("keywords")) {
						return true;
					} else {
						return false;
					}
				}

			};
			
			/* TESTCODE */
//			Bundle location = Activator.getDefault().getBundle();
//			Enumeration entryPaths = Activator.getDefault().getBundle().getEntryPaths("/");
			URL configEntry = Activator.getDefault().getBundle().getEntry("config");
			URL configPath = FileLocator.resolve (configEntry);
			File path = new File(configPath.getFile());
//			String[] list2 = resFile.list();
//			File file = new ConfigurationScope().getLocation().toFile();
//			String[] list = file.list();
//			IProject project = root.getProject();
//			IFolder files = project.getFolder("");
//			IResource[] members = files.members();
			/* TESTCODE END */

			String[] fileNames = path.list(filter);
			supportedLanguages.clear();
			
			for (String fileName : fileNames) {
				File fileObj = new File(path, fileName);
				CompositeConfiguration configuration = new CompositeConfiguration();

				PropertiesConfiguration keywords = new PropertiesConfiguration(fileObj);
				configuration.addConfiguration(keywords);
				configuration.addProperty("filename", fileName);
				localConfigurationList.add(configuration);
				supportedLanguages.add(configuration.getString("language.name"));
				nameConfigMap.put(configuration.getString("language.name"), configuration);
			}

//			for (String fileName : fileNames) {
//				CompositeConfiguration configuration = new CompositeConfiguration();
//
//				PropertiesConfiguration keywords = new PropertiesConfiguration(
//						"config/" + fileName);
//				configuration.addConfiguration(keywords);
//				configuration.addProperty("filename", fileName);
//				localConfigurationList.add(configuration);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localConfigurationList;
	}

	private void buildLocalKeywordMap(Reader reader) {
		
		String defaultLang = "EN";
		Configuration defaultConfig = null;

		Configuration clientConfig = null;
		Configuration serverConfig = null;
		
		// Open and input and output stream
		try {
			BufferedReader in = new BufferedReader(reader);

			// The pattern matches control characters
			Map<Pattern, Configuration> patternConfMap = new HashMap<Pattern, Configuration>();
			Map<Pattern, String> patternTypeMap = new HashMap<Pattern, String>();
			for (Configuration conf : getAllConfigurations()) {
				Pattern pClient = Pattern.compile(conf
						.getString("language.identifier.client"));
				Pattern pServer = Pattern.compile(conf
						.getString("language.identifier.server"));
				patternConfMap.put(pClient, conf);
				patternTypeMap.put(pClient, "client");
				patternConfMap.put(pServer, conf);
				patternTypeMap.put(pServer, "server");
				
				// Set default configuration
				if (conf.getString("language.locale").equalsIgnoreCase(defaultLang)){
					defaultConfig=conf;
				}
			}
			String aLine = null;
			while ((aLine = in.readLine()) != null && (clientConfig==null || serverConfig==null)) {
				for (Pattern pattern : patternConfMap.keySet()) {
					Matcher matcher = pattern.matcher(aLine);
					if (matcher.matches()){
						if (patternTypeMap.get(pattern).equals("client") && clientConfig==null){
							clientConfig = patternConfMap.get(pattern);
						} 
						else if (patternTypeMap.get(pattern).equals("server") && serverConfig==null){
							serverConfig = patternConfMap.get(pattern);
						}
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create local config mapping
		if (clientConfig==null && serverConfig==null){
			if (defaultConfig == null){
			throw new RuntimeException(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				
			};
			} else {
				clientConfig = defaultConfig;
				serverConfig = defaultConfig;
			}
		} else if (clientConfig == null){
			clientConfig = serverConfig;
		} else if (serverConfig == null){
			serverConfig = clientConfig;
		}
		
		localKeywords.put(ActlController.PREFIX, clientConfig);
		localKeywords.put(WflgController.PREFIX, clientConfig);
		localKeywords.put(ApiController.PREFIX, serverConfig);
		localKeywords.put(SqlController.PREFIX, serverConfig);
		localKeywords.put(FltrController.PREFIX, serverConfig);
		localKeywords.put(EsclController.PREFIX, serverConfig);
	}

	public static List<String> getSupportedLanguages() {
		return supportedLanguages;
	}
	
	public static void setClientLanguage(String langName){
		localKeywords.put(ActlController.PREFIX, nameConfigMap.get(langName));
		localKeywords.put(WflgController.PREFIX, nameConfigMap.get(langName));
	}
	
	public static void setServerLanguage(String langName){
		localKeywords.put(ApiController.PREFIX, nameConfigMap.get(langName));
		localKeywords.put(SqlController.PREFIX, nameConfigMap.get(langName));
		localKeywords.put(FltrController.PREFIX, nameConfigMap.get(langName));
		localKeywords.put(EsclController.PREFIX, nameConfigMap.get(langName));
	}
}
