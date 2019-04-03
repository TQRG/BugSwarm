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
package org.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.List;


@Rule(
    key = "S818",
    name = "Literal suffixes should be upper case",
    tags = {"cert", "convention", "misra", "pitfall"},
    priority = Priority.MINOR)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNDERSTANDABILITY)
@SqaleConstantRemediation("2min")
public class UppercaseSuffixesCheck extends SubscriptionBaseVisitor {

  @Override
  public List<Tree.Kind> nodesToVisit() {

    return ImmutableList.of(Tree.Kind.DOUBLE_LITERAL, Tree.Kind.FLOAT_LITERAL, Tree.Kind.LONG_LITERAL);
  }

  @Override
  public void visitNode(Tree tree) {

    LiteralTree node = (LiteralTree)tree;
    String suffix = node.value().substring(node.value().length()-1);
    if (!suffix.toUpperCase().equals(suffix)) {
      addIssue(tree, "Upper-case this literal \"" + suffix + "\" suffix.");
    }
  }
}
