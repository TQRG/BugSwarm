import java.util.List;

class A{
  private static final List<A> list;
  private String location;
  private static A tempVal;
  public A Instance;
  private B Instance2;
  
  public A(String location) {
    list.add(this);  // Noncompliant [sc=16;ec=20] {{Make sure the use of "this" doesn't expose partially-constructed instances of this class in multi-threaded environments.}}
    foo(this);  // Compliant
    this.location = location; // Compliant
    this.tempVal = this;  // Compliant
    Instance = this;  // Noncompliant
    this.Instance = this; // Noncompliant
    tempVal = this;  // Compliant
  }

  public A() {
    this.location = ""; // Compliant
    foo2(this);  // Noncompliant
    foo2(); // Compliant
    foo3(); //  Compliant
    foo1(new A());  // Compliant
    B.foo2();  // Compliant
    B.foo1(this);  // Noncompliant
    B.field = this;  // Noncompliant
    this.Instance2.foo3(this); // Noncompliant
    this.foo3(this);  // Compliant
  }
  
  public void foo(A a) {}
  
  private void foo2() {
    list.add(this);
  }
  
  private void foo3(A a) {
  }
  
}

class B {
  public static final List<A> list1;
  public A field;
  public B field2;
  
  public B() {
    this.field2 = this;  // Noncompliant  
  }
  
  void foo(A a) {
  }
  
  void foo2() {}
  
  public void foo3(A a) {
  }
}
