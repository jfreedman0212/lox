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


