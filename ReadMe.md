### High-level Design
For a given class A with dependency (i.e. fields) on classes B, C, D:
```
Class A {
    B someB;
    C someC;
    D someD;
}
```
For us to create an instance of A, we'll need to know how could B, C and D be created.
Supposedly there are known functions that create them, we just need a way to map these fields to such functions:
```
Class A {
    @Need(name="B1")
    B someB;
    @Need(name="C1")
    C someC;
    @Need(name="D1")
    D someD;
    
    public A(
      @Need(name="B1") B b1,
      @Need(name="C1") C c1,
      @Need(name="D1") D d1) {
      ...
   }
}
```
where `B1, C1, D1` are keys used to identify these creator functions that known to the tool.
If there only exists a single creator function for class/interface B then `name` could be omitted:
```
Class A {
    ...
    public A(
      @Need B b1,
      @Need C c1,
      @Need D d1) {
      ...
   }
}
```
In all above cases, when we want to create an instance of A, which normally means A is the entrance of the program, we
will simply need to do:
```
Injector injector = new Injector();
injector.register(Register someRegister) // optional
injector.prepare(A.class); // entry class of the program 
A entrance = injector.createInstance(A.class);
```
Now 2 things needs to happen in the `injector.prepare()` call: 
1. find out what are the creator functions exists in the path and store them in a map
   - use `@Need` classes or `@Provide` methods as root, DFS walk
2. DFS walk the dependency graph of A, creates objects and store in another map

We haven't talked about how to allow creator function be discovered by the tool yet, which could be as simple as:
```
@Provide(name="someString")
String returnMyString() {
...
}
```
Functions tagged with `@Provide` are known as provider functions. As a design pattern, all the methods annotated with 
`@Provide` should only exist in `Register` classes or constructors; 
Naturally, all the classes annotated with `@Need` will have an unnamed creator function mapped in the data store.

A provider function could use further make sure of `@Need` on its parameter to leverage on existing creator functions:
```
@Provide(name="myAccount")
Account returnMyAccount(
       @Need(name="userName") String myName, 
       @Need(name="myCreditCard") CreditCard myCard,
       @Need(name="myEmail") EmailAddress myEmail) {
      ...
}

@Provide(name="testAccount")
Account returnTestAccount(
    @Need(name="testCard") CreditCard testCard) {
    ...
}
```
### What do we need to store?
Consider a dependency graph as below:
```
class A {
   B b;
   C c;
   public A(@Need B b, @Need(name="c1") C c) {
   ...
   }
}

class B {
   C c;
   D d;
   public B(@Need(name="c1") C c1, @Need D d) {
   ...
   }
}

Class C {
}

class D {
   C c;
   public D(@Need(name="c3") C c) {
   ...
   }
}

class Register {
   @Provide(name="c1")
   C getC1();
   
   @Provide(name="c2")
   C getC2();
   
   @Provide(name="c3")
   C getC3();
}
```
We need to keep 2 store:
1. object store
2. creator store

Both stores use a composite key of `{Class, Name}`, so we'd define a storage key class:
```
class StorageKey {
   Class clazz;
   String key;
}
```
Object store's mapping could simply be:
```
{Class, Name} -> Instance
```
while creator store needs to be:
```
{Class, Name} -> Creator

class Creator {
   Method method;
   Object[] arguments;
}
```
right now, we'll only consider the `singleton` scope, so all classes will only ever need to be instantiated once,
and thus creator store doesn't need to be implemented yet