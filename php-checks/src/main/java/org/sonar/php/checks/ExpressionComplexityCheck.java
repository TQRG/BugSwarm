/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010 SonarSource and Akram Ben Aissi
 * dev@sonar.codehaus.org
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
package org.sonar.php.checks;

import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.php.api.PHPPunctuator;
import org.sonar.php.parser.PHPGrammar;
import org.sonar.squidbridge.annotations.SqaleLinearWithOffsetRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.parser.LexerlessGrammar;

import javax.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

@Rule(
  key = "S1067",
  name = "Expressions should not be too complex",
  priority = Priority.MAJOR,
  tags = {Tags.BRAIN_OVERLOAD})
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.UNIT_TESTABILITY)
@SqaleLinearWithOffsetRemediation(coeff = "1min", offset = "5min", effortToFixDescription = "per complexity point above the threshold")
public class ExpressionComplexityCheck extends SquidCheck<LexerlessGrammar> {

  public static final int DEFAULT = 3;
  private Deque<ExpressionComplexity> scope = new ArrayDeque<ExpressionComplexity>();
  private static final GrammarRuleKey[] REQUIRES_NEW_SCOPE = {
    PHPGrammar.FUNCTION_EXPRESSION,
    PHPGrammar.ARRAY_PAIR_LIST,
    PHPGrammar.RETURN_STATEMENT,
    PHPGrammar.FOR_EXRR,
    PHPGrammar.PARAMETER_LIST_FOR_CALL};

  private static final GrammarRuleKey[] LOGICAL_AND_CONDITIONAL_EXPRS = {
    PHPGrammar.CONDITIONAL_EXPR,
    PHPGrammar.LOGICAL_AND_EXPR,
    PHPGrammar.LOGICAL_OR_EXPR};

  @RuleProperty(defaultValue = "" + DEFAULT)
  public int max = DEFAULT;

  public static class ExpressionComplexity {
    private int nestedLevel = 0;
    private int counterOperator = 0;

    public void increaseOperatorCounter(int nbOperator) {
      counterOperator += nbOperator;
    }

    public void incrementNestedExprLevel() {
      nestedLevel++;
    }

    public void decrementNestedExprLevel() {
      nestedLevel--;
    }

    public boolean isOnFirstExprLevel() {
      return nestedLevel == 0;
    }

    public int getExprNumberOfOperator() {
      return counterOperator;
    }

    public void resetExprOperatorCounter() {
      counterOperator = 0;
    }
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    scope.clear();
    scope.push(new ExpressionComplexity());
  }

  @Override
  public void init() {
    subscribeTo(LOGICAL_AND_CONDITIONAL_EXPRS);
    subscribeTo(PHPGrammar.EXPRESSION);
    subscribeTo(REQUIRES_NEW_SCOPE);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (isExpression(astNode)) {
      scope.peek().incrementNestedExprLevel();
    }
    if (astNode.is(LOGICAL_AND_CONDITIONAL_EXPRS)) {
      scope.peek().increaseOperatorCounter(
        astNode.getChildren(PHPGrammar.LOGICAL_OR_OPERATOR, PHPGrammar.LOGICAL_AND_OPERATOR, PHPPunctuator.QUERY).size());
    }
    if (astNode.is(REQUIRES_NEW_SCOPE)) {
      scope.push(new ExpressionComplexity());
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (isExpression(astNode)) {
      ExpressionComplexity currentExpression = scope.peek();
      currentExpression.decrementNestedExprLevel();

      if (currentExpression.isOnFirstExprLevel()) {
        if (currentExpression.getExprNumberOfOperator() > max) {
          getContext().createLineViolation(this,
            "Reduce the number of conditional operators (" + currentExpression.getExprNumberOfOperator() + ") used in the expression (maximum allowed " + max + ").",
            astNode);
        }
        currentExpression.resetExprOperatorCounter();
      }
    } else if (astNode.is(REQUIRES_NEW_SCOPE)) {
      scope.pop();
    }
  }

  public static boolean isExpression(AstNode node) {
    return node.is(PHPGrammar.EXPRESSION) || node.is(LOGICAL_AND_CONDITIONAL_EXPRS);
  }
}
