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
package org.sonar.php.tree.impl.expression;

import java.util.Iterator;
import java.util.List;

import org.sonar.php.tree.impl.PHPTree;
import org.sonar.php.tree.impl.lexical.InternalSyntaxToken;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.ReferenceVariableTree;
import org.sonar.plugins.php.api.tree.expression.VariableVariableTree;
import org.sonar.plugins.php.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.php.api.visitors.TreeVisitor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

public class ReferenceVariableTreeImpl extends PHPTree implements ReferenceVariableTree {

  private static final Kind KIND = Kind.REFERENCE_VARIABLE;
  private final InternalSyntaxToken ampersandToken;
  private final ExpressionTree variableExpression;

  public ReferenceVariableTreeImpl(InternalSyntaxToken ampersandToken, ExpressionTree variableExpression) {
    this.ampersandToken = ampersandToken;
    this.variableExpression = variableExpression;
  }

  @Override
  public SyntaxToken ampersandToken() {
    return ampersandToken;
  }

  @Override
  public ExpressionTree variableExpression() {
    return variableExpression;
  }

  @Override
  public Kind getKind() {
    return KIND;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return Iterators.forArray(ampersandToken, variableExpression);
  }

  @Override
  public void accept(TreeVisitor visitor) {
    visitor.visitReferenceVariable(this);
  }

}
