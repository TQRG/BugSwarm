/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import melnorme.utilbox.core.CommonException;

public interface ILaunchConfigSerializer {

	ProjectLaunchSettings initFrom(IResource contextualResource);
	
	default void saveToConfig(ILaunchConfigurationWorkingCopy config) throws CommonException {
		saveToConfig(config, false);
	}
	
	void saveToConfig(ILaunchConfigurationWorkingCopy config, boolean rename) throws CommonException;
	
}