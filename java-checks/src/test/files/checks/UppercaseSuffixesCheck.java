class A {
  long long1 = 1l; // Noncompliant
  float float1 = 1.0f;  // Noncompliant
  double double1 = 1.0d;  // Noncompliant

  private void test () {

    long retVal = (bytes[0] & 0xFF);  // OK
    for (int i = 1; i < Math.min(bytes.length, 8); i++) {
      retVal |= (bytes[i] & 0xFFL) << (i * 8);  // OK
    }
  }
}
