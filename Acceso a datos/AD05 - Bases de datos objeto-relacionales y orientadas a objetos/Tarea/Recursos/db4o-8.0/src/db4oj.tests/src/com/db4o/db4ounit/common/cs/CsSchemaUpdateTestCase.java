/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.ignore
 */
@decaf.Remove
public class CsSchemaUpdateTestCase extends AbstractDb4oTestCase implements OptOutMultiSession, OptOutNoInheritedClassPath, OptOutWorkspaceIssue {
	
	public static void main(String[] arguments) {
		new CsSchemaUpdateTestCase().runSolo();
	}
	
	public void test() throws IOException, InterruptedException{
		runForLabel("store");
		runForLabel("update");
		String res = runForLabel("assert");
		Assert.isGreater(-1, res.indexOf("IsNamedOK"));
	}

	private String runForLabel(String label) throws IOException, InterruptedException {
		prepareSource(label);
		JavaServices.javac(targetFileName());
		return JavaServices.java(packageName() + "." + className());
	}
	
	private void prepareSource(String label) throws IOException{
		FileReader reader = new FileReader(sourceFileName());
		BufferedReader bufferedReader = new BufferedReader(reader);
		String tempBuffer;
		StringBuffer stringBuffer = new StringBuffer();
		while ((tempBuffer = bufferedReader.readLine()) != null) {
			stringBuffer.append(tempBuffer);
			stringBuffer.append("\r\n");
		}
		reader.close();
		processLabel(label, stringBuffer);
		File4.mkdirs(targetFilePath());
		FileWriter writer = new FileWriter(targetFileName());
		writer.write(stringBuffer.toString());
		writer.flush();
		writer.close();
	}

	private void processLabel(String label, StringBuffer stringBuffer) {
		int pos = 0;
		while(pos >= 0){
			int labelPos = stringBuffer.indexOf("//" + label, pos);
			if(! (labelPos >= 0)){
				return;
			}
			replaceStringWithBlank(stringBuffer, labelPos, "/*");
			replaceStringWithBlank(stringBuffer, labelPos, "*/");
			pos = labelPos + 1;
		}
		
	}

	private void replaceStringWithBlank(StringBuffer stringBuffer,
			int labelPos, String commentString) {
		int startCommentPos = stringBuffer.indexOf(commentString, labelPos);
		stringBuffer.replace(startCommentPos, startCommentPos + 2, "  ");
	}
	
	private String tempPath(){
		return JavaServices.javaTempPath();
	}
	
	private String fileName(){
		return className() + ".java";
	}
	
	private String className(){
		return "CsSchemaMigrationSourceCode";
	}
	
	private String sourceFileName(){
		return testSourcePath() + packagePath() + fileName();
	}
	
	private String targetFilePath(){
		return tempPath() + packagePath();
	}
	
	private String targetFileName(){
		return targetFilePath() + fileName();
	}

	private String testSourcePath() {
		return WorkspaceServices.workspaceRoot() + "/" + "db4oj.tests/src/";
	}
	
	private String packageName(){
		String className = this.getClass().getName();
		return className.substring(0, className.lastIndexOf("."));
	}
	
	private String packagePath(){
		String dotsToSlashes = packageName().replaceAll("\\.", "/");
		return "/" + dotsToSlashes + "/";
	}
	


}
