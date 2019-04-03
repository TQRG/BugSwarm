/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.googlecode.goclipse.ui.properties;

import org.eclipse.core.resources.IProject;

import melnorme.lang.ide.ui.preferences.ProjectBuildConfigurationComponent;
import melnorme.lang.ide.ui.preferences.pages.BuildConfigurationPropertyPage;

public class GoProjectBuildConfigurationPropertyPage extends BuildConfigurationPropertyPage {
	
	@Override
	protected ProjectBuildConfigurationComponent createProjectConfigWidget(IProject project) {
		return new GoProjectOptionsBlock(project);
	}
	
	public class GoProjectOptionsBlock extends ProjectBuildConfigurationComponent {
		
		public GoProjectOptionsBlock(IProject project) {
			super(project);
		}
		
	}
	
}