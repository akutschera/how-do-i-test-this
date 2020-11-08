# How to I test this?

There are a lot of examples for writing Unit tests that test just one class.
There are few examples for writing tests that test Spring(Boot) annotations.

When I go TDD, how to I write a test that is red without the spring boot annotation and green with it?

In this project I collect some of the examples I created while figuring out how those annotations work.

Each sub-project contains its own README where I try to explain what motivated me to write those tests.

These examples do in no way represent any so-called "best practices", in fact, some of them I would consider
bad practices. This is more a collection of something that seems to work, and before you have no test at all
for a given funtionality, I think it's better to have a bad one, even if you just use it as an indicator that
you need to refactor your code so the tests become more easily maintainable.