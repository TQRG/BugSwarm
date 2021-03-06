// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ../wire-runtime/src/test/proto/single_level.proto
package com.squareup.wire.protos.single_level;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Label.REPEATED;

public final class Foos extends Message {

  public static final List<Foo> DEFAULT_FOOS = Collections.emptyList();

  @ProtoField(tag = 1, label = REPEATED, messageType = Foo.class)
  public final List<Foo> foos;

  public Foos(List<Foo> foos) {
    this.foos = immutableCopyOf(foos);
  }

  private Foos(Builder builder) {
    this(builder.foos);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Foos)) return false;
    return equals(foos, ((Foos) other).foos);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = foos != null ? foos.hashCode() : 1);
  }

  public static final class Builder extends Message.Builder<Foos> {

    public List<Foo> foos;

    public Builder() {
    }

    public Builder(Foos message) {
      super(message);
      if (message == null) return;
      this.foos = copyOf(message.foos);
    }

    public Builder foos(List<Foo> foos) {
      this.foos = checkForNulls(foos);
      return this;
    }

    @Override
    public Foos build() {
      return new Foos(this);
    }
  }
}
