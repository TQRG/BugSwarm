package com.squareup.wire.logger;

/**
 * Created by zundel on 12/23/14.
 */
public class WireLoggerFactory {
  private static final WireLogger defaultLogger = new ConsoleWireLogger();
  private static WireLogger singleton = defaultLogger;

  public static WireLogger get() {
    return singleton;
  }

  public static void set(WireLogger logger) {
    singleton = logger;
  }
  public static void reset() {
    singleton = defaultLogger;
  }
}
