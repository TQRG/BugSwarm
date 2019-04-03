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
package org.sonar.java.model.statement;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import org.sonar.java.model.InternalSyntaxToken;
import org.sonar.java.model.JavaTree;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TreeVisitor;

import java.util.Iterator;
import java.util.List;

public class BlockTreeImpl extends JavaTree implements BlockTree {

  private final Kind kind;
  private final InternalSyntaxToken openBraceToken;
  private final List<StatementTree> body;
  private final InternalSyntaxToken closeBraceToken;

  public BlockTreeImpl(InternalSyntaxToken openBraceToken, List<StatementTree> body, InternalSyntaxToken closeBraceToken) {
    this(Kind.BLOCK, openBraceToken, body, closeBraceToken);
  }

  public BlockTreeImpl(Kind kind, InternalSyntaxToken openBraceToken, List<StatementTree> body, InternalSyntaxToken closeBraceToken) {
    super(kind);

    this.kind = kind;
    this.openBraceToken = openBraceToken;
    this.body = Preconditions.checkNotNull(body);
    this.closeBraceToken = closeBraceToken;
  }

  @Override
  public Kind getKind() {
    return kind;
  }

  @Override
  public SyntaxToken openBraceToken() {
    return openBraceToken;
  }

  @Override
  public List<StatementTree> body() {
    return body;
  }

  @Override
  public SyntaxToken closeBraceToken() {
    return closeBraceToken;
  }

  @Override
  public void accept(TreeVisitor visitor) {
    visitor.visitBlock(this);
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return Iterators.concat(
      Iterators.singletonIterator(openBraceToken),
      body.iterator(),
      Iterators.singletonIterator(closeBraceToken));
  }

}
