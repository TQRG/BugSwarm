/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010 SonarSource and Akram Ben Aissi
 * sonarqube@googlegroups.com
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
package org.sonar.php.metrics;

import org.sonar.plugins.php.api.tree.CompilationUnitTree;

import java.util.Collections;
import java.util.Set;

public class CommentLineVisitor {
  public CommentLineVisitor(CompilationUnitTree tree) {
  }

  public int getCommentLineNumber() {
    return 0;
  }

  public Set<Integer> getNoSonarLines() {
    return Collections.emptySet();
  }

  public Set<Integer> getCommentLines() {
    return Collections.emptySet();
  }
}
