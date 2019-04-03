/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
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
package org.sonar.java.model;

import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TreeVisitor;

import java.util.Iterator;

public class InternalSyntaxTrivia extends JavaTree implements SyntaxTrivia {

  private final String comment;
  private final int startLine;
  private final int column;

  public InternalSyntaxTrivia(String comment, int startLine, int column) {
    super(null);
    this.comment = comment;
    this.startLine = startLine;
    this.column = column;
  }

  @Override
  public String comment() {
    return comment;
  }

  @Override
  public int startLine() {
    return startLine;
  }

  @Override
  public Kind getKind() {
    return Tree.Kind.TRIVIA;
  }

  @Override
  public boolean isLeaf() {
    return true;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void accept(TreeVisitor visitor) {
    //FIXME do nothing
  }

  public static SyntaxTrivia create(String comment, int startLine, int column) {
    return new InternalSyntaxTrivia(comment, startLine, column);
  }

  @Override
  public int getLine() {
    return startLine;
  }

  @Override
  public int column() {
    return column;
  }
}
