# Lox

My implementation of the Lox language as I following along with [Crafting Interpreters](https://craftinginterpreters.com/). 
The code here doesn't match 1:1 with what's in the book, but I follow the spirit of what's there.

# Major Differences

## Redeclaring Variables

According to the language spec, the following code is equivalent:

```
var x = 10;
var x = "hello world!";
```

and

```
var x = 10;
x = "hello world!";
```

Redeclaring a variable is equivalent to reassigning a new value to it. I decided to
make the first scenario a runtime error instead. There's no advantage this way over
the other way, but the Lox spec didn't do what I expected it to. So, in my implementation,
I changed it. 

## Use of Sealed Interfaces for Tokens, Statements, and Expressions

At work, we're migrating to Java 17 for existing and new applications. I wanted to 
acquaint myself with some of the new features, such as sealed interfaces, record classes,
etc. Some examples of this are the `Token`, `Statement`, and `Expression` interfaces. My hope
for using this approach was to increase type-safety by representing a valid construct in the
type system.

Maybe this will pay off, maybe it won't. Maybe I overused or abused the pattern too...

## The `assert` Keyword

I added support for an `assert` keyword similar to what C and friends have. If the value is
falsy, an `AssertionError` will be bubbled up to the user at runtime. Otherwise, nothing
happens. I did this because I would like to write tests for Lox _in Lox_ and I figured the
best way to do that would be to add support for assertions throughout the code.

## Visitor Pattern Usage

In jlox, I did not use the Visitor pattern for implementing the `Interpreter` class like
the book does. Instead, it's a big if-else. I did that mostly out of personal preference:
flat if-else statements are easier for me to parse than wrapping my head around code
using the Visitor pattern. Maybe it's just my unfamiliarity with it, and following how
the book implemented it would help me to that end.

Unfortunately, as I got to Chapter 11 with the variable resolution pass, I regret my
decision a little bit... Maybe I'll refactor to the Visitor pattern, or maybe I won't.
At least I understand why it's useful now, though!

# JLox Tests

There are several files within the test resources folder that contain different kinds of tests.
Additionally, a coverage report is created from this, which can be accessed under `jlox/target/site/jacoco/index.html`.
I understand that code coverage isn't a 100% great metric to use, but it provides a look into where tests can be improved.