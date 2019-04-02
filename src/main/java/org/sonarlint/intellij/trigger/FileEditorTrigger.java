/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.trigger;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class FileEditorTrigger extends AbstractProjectComponent implements FileEditorManagerListener {
  private final SonarLintSubmitter submitter;
  private final MessageBusConnection busConnection;

  public FileEditorTrigger(Project project, SonarLintSubmitter submitter) {
    super(project);
    this.submitter = submitter;
    this.busConnection = project.getMessageBus().connect(project);
    busConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);
  }

  @Override
  /**
   * I've tried hard to group opening events on startup without success.
   * Tried: Project.isInitialized, Project.isOpen, schedule to EDT thread and to WriteAction.
   * So on startup, opened files will be submitted one by one.
   */
  public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
    submitter.submitFiles(Collections.singleton(file), TriggerType.EDITOR_OPEN, true);
  }

  @Override
  public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
    // nothing to do
  }

  @Override
  public void selectionChanged(@NotNull FileEditorManagerEvent event) {
    // nothing to do
  }

  @Override
  public void disposeComponent() {
    busConnection.disconnect();
  }
}
