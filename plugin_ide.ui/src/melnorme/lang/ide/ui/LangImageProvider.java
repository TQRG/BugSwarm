/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui;

import melnorme.lang.ide.ui.views.AbstractLangImageProvider;
import melnorme.lang.tooling.LANG_SPECIFIC;
import melnorme.util.swt.jface.IManagedImage;

import com.googlecode.goclipse.ui.GoPluginImages;

@LANG_SPECIFIC
public class LangImageProvider extends AbstractLangImageProvider {
	
	@Override
	public IManagedImage visitFunction() {
		return GoPluginImages.SOURCE_FUNCTION;
	}
	
	@Override
	public IManagedImage visitMethod() {
		return GoPluginImages.SOURCE_METHOD;
	}
	
	@Override
	public IManagedImage visitVariable() {
		return GoPluginImages.SOURCE_VARIABLE;
	}
	
	@Override
	public IManagedImage visitConst() {
		return GoPluginImages.SOURCE_VARIABLE;
	}
	
	@Override
	public IManagedImage visitTypeDecl() {
		return LangElementImages.T_TYPE;
	}
	
	@Override
	public IManagedImage visitImport() {
		return GoPluginImages.SOURCE_IMPORT;
	}
	
}