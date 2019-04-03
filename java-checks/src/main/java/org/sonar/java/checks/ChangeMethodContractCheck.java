/*
 * SonarQube Java
 * Copyright (C) 2012-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.declaration.MethodTreeImpl;
import org.sonar.java.resolve.JavaSymbol;
import org.sonar.java.tag.Tag;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.List;

@Rule(
  key = "S2638",
  name = "Method overrides should not change contracts",
  priority = Priority.MAJOR,
  tags = {Tag.SUSPICIOUS})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY)
@SqaleConstantRemediation("15min")
@ActivatedByDefault
public class ChangeMethodContractCheck extends SubscriptionBaseVisitor {

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.METHOD);
  }

  @Override
  public void visitNode(Tree tree) {
    if(!hasSemantic()) {
      return;
    }
    MethodTreeImpl methodTree = (MethodTreeImpl) tree;
    Symbol.MethodSymbol methodSymbol = methodTree.symbol();
    JavaSymbol.MethodJavaSymbol overridee = ((JavaSymbol.MethodJavaSymbol) methodSymbol).overriddenSymbol();
    if (overridee != null && overridee.isMethodSymbol()) {
      checkContractChange(methodTree, overridee);
    }
  }

  private void checkContractChange(MethodTreeImpl methodTree, JavaSymbol.MethodJavaSymbol overridee) {
    if (methodTree.isEqualsMethod() && methodTree.parameters().get(0).symbol().metadata().isAnnotatedWith("javax.annotation.Nonnull")) {
      reportIssue(methodTree.parameters().get(0), "Equals method should accept null parameters and return false.");
      return;
    }
    for (int i = 0; i < methodTree.parameters().size(); i++) {
      Symbol paramSymbol = methodTree.parameters().get(i).symbol();
      Symbol overrideeParamSymbol = overridee.getParameters().scopeSymbols().get(i);
      if (nonNullVsNull(paramSymbol, overrideeParamSymbol) || nonNullVsNull(overrideeParamSymbol, paramSymbol)) {
        reportIssue(methodTree.parameters().get(i), "The \"" + paramSymbol.name()
            + "\" parameter nullability is different in the superclass method, and that should not be changed.");
      }
    }
    if (nonNullVsNull(methodTree.symbol(), overridee) || nonNullVsNull(overridee, methodTree.symbol())) {
      reportIssue(methodTree.returnType(), "The return value nullability of this method is different in the superclass, and that should not be changed.");
    }
  }

  private static boolean nonNullVsNull(Symbol sym1, Symbol sym2) {
    return sym1.metadata().isAnnotatedWith("javax.annotation.Nonnull") &&
      (sym2.metadata().isAnnotatedWith("javax.annotation.Nullable")
      || sym2.metadata().isAnnotatedWith("javax.annotation.CheckForNull"));

  }

}
