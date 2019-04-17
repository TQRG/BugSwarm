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
package org.sonar.java.checks.helpers;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.sonar.plugins.java.api.semantic.Type;

public class TypePredicates {

  private TypePredicates() {
    // Useful predicates to be used with types
  }

  public static Predicate<String> typeIs(final Type type) {
    return new Predicate<String>() {
      @Override
      public boolean apply(String input) {
        return type.is(input);
      }
    };
  }

  public static Predicate<String> typeIsSubtypeOf(final Type type) {
    return new Predicate<String>() {
      @Override
      public boolean apply(String input) {
        return type.isSubtypeOf(input);
      }
    };
  }

  public static Predicate<Type> isType(final String type) {
    return new Predicate<Type>() {
      @Override
      public boolean apply(Type input) {
        return input.is(type);
      }
    };
  }

  public static Predicate<Type> isSubtypeOf(final String fullyQualifiedName) {
    return new Predicate<Type>() {
      @Override
      public boolean apply(Type input) {
        return input.isSubtypeOf(fullyQualifiedName);
      }
    };
  }

  public static Predicate<Type> anyType() {
    return Predicates.alwaysTrue();
  }

}
