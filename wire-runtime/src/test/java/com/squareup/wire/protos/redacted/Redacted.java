// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ../wire-runtime/src/test/proto/redacted_test.proto
package com.squareup.wire.protos.redacted;

import com.google.protobuf.FieldOptions;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.STRING;

public final class Redacted extends Message {

  public static final FieldOptions FIELD_OPTIONS_A = new FieldOptions.Builder()
      .setExtension(Ext_redacted_test.redacted, true)
      .build();
  public static final FieldOptions FIELD_OPTIONS_B = new FieldOptions.Builder()
      .setExtension(Ext_redacted_test.redacted, false)
      .build();

  public static final String DEFAULT_A = "";
  public static final String DEFAULT_B = "";
  public static final String DEFAULT_C = "";

  @ProtoField(tag = 1, type = STRING, redacted = true)
  public final String a;

  @ProtoField(tag = 2, type = STRING)
  public final String b;

  @ProtoField(tag = 3, type = STRING)
  public final String c;

  public Redacted(String a, String b, String c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  private Redacted(Builder builder) {
    this(builder.a, builder.b, builder.c);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Redacted)) return false;
    Redacted o = (Redacted) other;
    return equals(a, o.a)
        && equals(b, o.b)
        && equals(c, o.c);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = a != null ? a.hashCode() : 0;
      result = result * 37 + (b != null ? b.hashCode() : 0);
      result = result * 37 + (c != null ? c.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Redacted> {

    public String a;
    public String b;
    public String c;

    public Builder() {
    }

    public Builder(Redacted message) {
      super(message);
      if (message == null) return;
      this.a = message.a;
      this.b = message.b;
      this.c = message.c;
    }

    public Builder a(String a) {
      this.a = a;
      return this;
    }

    public Builder b(String b) {
      this.b = b;
      return this;
    }

    public Builder c(String c) {
      this.c = c;
      return this;
    }

    @Override
    public Redacted build() {
      return new Redacted(this);
    }
  }
}
