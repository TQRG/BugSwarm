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
package org.sonar.php.checks;

import com.sonar.sslr.api.AstAndTokenVisitor;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.regex.Pattern;

@Rule(
  key = "S139",
  name = "Comments should not be located at the end of lines of code",
  priority = Priority.INFO,
  tags = {Tags.CONVENTION})
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.INFO)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class TrailingCommentCheck extends SquidCheck<LexerlessGrammar> implements AstAndTokenVisitor {

  public static final String DEFAULT_LEGAL_COMMENT_PATTERN = "^(//|#)\\s*+[^\\s]++$";

  @RuleProperty(
    key = "legalTrailingCommentPattern",
    defaultValue = DEFAULT_LEGAL_COMMENT_PATTERN)
  String legalCommentPattern = DEFAULT_LEGAL_COMMENT_PATTERN;

  private Pattern pattern;
  private int previousTokenLine;

  @Override
  public void visitFile(AstNode astNode) {
    previousTokenLine = -1;
    pattern = Pattern.compile(legalCommentPattern);
  }

  @Override
  public void visitToken(Token token) {
    for (Trivia trivia : token.getTrivia()) {
      if (trivia.isComment() && trivia.getToken().getLine() == previousTokenLine) {
        String comment = trivia.getToken().getValue();
        if ((comment.startsWith("//") || comment.startsWith("#")) && !pattern.matcher(comment).matches()) {
          getContext().createLineViolation(this, "Move this trailing comment on the previous empty line.", previousTokenLine);
        }
      }
    }
    previousTokenLine = token.getLine();
  }
}
