package org.testng.internal;

public class Version {
  public static final String VERSION = "${project.version}";

  public static void displayBanner() {
    System.out.println("...\n... TestNG " + VERSION + " by Cédric Beust (cedric@beust.com)\n...\n");
  }
}
