/*
 * SonarQube Java
 * Copyright (C) 2012-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "S3366")
public class ThisExposedFromConstructorCheck extends IssuableSubscriptionVisitor {

  private static final String REPORT = "Make sure the use of \"this\" doesn't" +
    " expose partially-constructed instances of this class in multi-threaded environments.";

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.CONSTRUCTOR);
  }

  @Override
  public void visitNode(Tree tree) {
    if (!hasSemantic()) {
      return;
    }
    MethodTree methodTree = (MethodTree) tree;
    methodTree.block().accept(new ConstructorBodyVisitor(methodTree.symbol().owner()));
  }

  private class ConstructorBodyVisitor extends BaseTreeVisitor {
    private Symbol owner;

    public ConstructorBodyVisitor(Symbol owner) {
      this.owner = owner;
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree tree) {
      if (this.owner == tree.symbol().owner()) {
        return;
      }
      Arguments args = tree.arguments();
      if (args.isEmpty()) {
        return;
      }
      args.stream().filter(arg -> is(arg, "this")).forEach(expr -> reportIssue(expr, REPORT));
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpressionTree tree) {
      if (!is(tree.expression(), "this")) {
        return;
      }
      ExpressionTree variable = tree.variable();
      MemberSelectExpressionTree memberSelect;
      if (variable.is(Tree.Kind.MEMBER_SELECT)) {
        memberSelect = (MemberSelectExpressionTree) variable;
        if (memberSelect.expression().symbolType().symbol().equals(this.owner) && memberSelect.identifier().symbol().isStatic()) {
          return;
        }
      }
      if (variable.is(Tree.Kind.IDENTIFIER) && isStaticField((IdentifierTree) variable)) {
        return;
      }
      reportIssue(tree, REPORT);
    }

    private boolean is(ExpressionTree expression, String match) {
      if (expression.is(Tree.Kind.IDENTIFIER)) {
        String targetName = ((IdentifierTree) expression).name();
        return match.equals(targetName);
      }
      return false;
    }

    private boolean isStaticField(IdentifierTree identifier) {
      return identifier.symbol().isStatic();
    }
  }
}
