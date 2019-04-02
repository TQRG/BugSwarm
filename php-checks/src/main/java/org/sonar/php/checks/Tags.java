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

public class Tags {

  public static final String BRAIN_OVERLOAD = "brain-overload";
  public static final String BUG = "bug";
  public static final String CERT = "cert";
  public static final String CLUMSY = "clumsy";
  public static final String CONVENTION = "convention";
  public static final String CWE = "cwe";
  public static final String DESIGN = "design";
  public static final String ERROR_HANDLING = "error-handling";
  public static final String MISRA = "misra";
  public static final String OBSOLETE = "obsolete";
  public static final String PERFORMANCE = "performance";
  public static final String PITFALL = "pitfall";
  public static final String PSR1 = "psr1";
  public static final String PSR2 = "psr2";
  public static final String SANS_TOP25_POROUS = "sans-top25-porous";
  public static final String SECURITY = "security";
  public static final String UNUSED = "unused";
  public static final String USER_EXPERIENCE = "user-experience";
  public static final String OWASP_A2 = "owasp-a2";
  public static final String OWASP_A3 = "owasp-a3";

  private Tags() {
    // This class only defines constants
  }

}
