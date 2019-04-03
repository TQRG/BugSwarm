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
package com.googlecode.goclipse.ui.actions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.googlecode.goclipse.core.GoProjectEnvironment;
import com.googlecode.goclipse.core.GoToolPreferences;
import com.googlecode.goclipse.tooling.env.GoEnvironment;
import com.googlecode.goclipse.tooling.oracle.GoFindDefinitionOperation;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.ui.editor.EditorSourceBuffer;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.ide.ui.editor.actions.AbstractOpenElementOperation;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.common.ISourceBuffer;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.lang.tooling.toolchain.ops.FindDefinitionResult;
import melnorme.lang.tooling.toolchain.ops.IToolOperationService;
import melnorme.lang.tooling.toolchain.ops.SourceOpContext;
import melnorme.lang.tooling.toolchain.ops.ToolOpResult;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.status.StatusException;

public class GoOpenDefinitionOperation extends AbstractOpenElementOperation {
	
	public static final String OPEN_DEFINITION_OpName = "Open definition";
	
	public GoOpenDefinitionOperation(ITextEditor editor, SourceRange range, OpenNewEditorMode openEditorMode) {
		super(OPEN_DEFINITION_OpName, editor, range, openEditorMode);
	}
	
	@Override
	protected void prepareOperation() throws CommonException {
		super.prepareOperation();
		assertNotNull(getInputLocation());
		saveEditor(new NullProgressMonitor());
		
	}
	
	@Override
	protected FindDefinitionResult performLongRunningComputation_doAndGetResult(IOperationMonitor cm) 
			throws CommonException, OperationCancellation {
		
		ToolOpResult<FindDefinitionResult> opResult = getToolOperation().executeOp(cm);
		try {
			return opResult.get();
		} catch(CommonException e) {
			statusErrorMessage = e.getMessage();
			return null;
		}
	}
	
	public GoFindDefinitionOperation getToolOperation() {
		return getFindDefinitionOperation(new EditorSourceBuffer(editor), getOperationOffset());
	}
	
	public static GoFindDefinitionOperation getFindDefinitionOperation(ISourceBuffer sourceBuffer,
			int offset) {
		SourceOpContext opContext = sourceBuffer.getSourceOpContext(new SourceRange(offset, 0));
		
		IProject project = ResourceUtils.getProject(opContext.getOptionalFileLocation());
		GoEnvironment goEnv = GoProjectEnvironment.getGoEnvironment(project);
		
		IToolOperationService toolsOpService = LangCore.getToolManager().getEngineToolsOperationService();
		return GoOpenDefinitionOperation.getFindDefOperation(goEnv, opContext, toolsOpService);
	}
	
	public static GoFindDefinitionOperation getFindDefOperation(GoEnvironment goEnv, SourceOpContext sourceOpContext,
			IToolOperationService toolService) {
		return new GoFindDefinitionOperation(goEnv, sourceOpContext, toolService) {
			@Override
			protected String getGodefPath() throws StatusException {
				return GoToolPreferences.GODEF_Path.getDerivedValue().toString();
			};
			
			@Override
			protected String getGuruPath() throws StatusException {
				return GoToolPreferences.GO_GURU_Path.getDerivedValue().toString();
			};
		};
	}
	
}