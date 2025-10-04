{lpd} This is the pull request title

# What is this trying to solve?

{url}

Explain *what* you're trying to do, without talking about *how* you're doing it.
Here, brevity is a virtue, don't get into too much detail. An overview should be
more than enough.

# How am I fixing it?

Don't repeat what the code already says, try to give a bird's eye perspective so
that the code is easier to understand.

# How can you verify that it works?

If the PR includes automated tests, simply saying "Run the included automated
tests." is enough. Please, add extra information that might be needed to
understand the testing strategy.

If the PR doesn't include tests, and the ticket includes reproduction steps:
"Follow the steps described in {url}."

Otherwise, define a procedure to test the changes using a numbered list. E.g:

1. Go to "Foo".

1. Click on "Bar".

1. Go back to step #1.

# High level requirements rationale

## Why doesn't this include any test?

If the PR contains automated tests, omit this section.

Otherwise, you need to give a rationale of why.

## Has a11y been checked?

If the PR is not related at all with the UI, omit this section.

Otherwise, you need to give a rationale of how.

## Do you include custom CSS?

If the PR does not contain custom CSS, omit this section.

Otherwise, you need to give a rationale of why.

## Have you introduced breaking changes?

If the PR does not introduce breaking changes, omit this section.

Otherwise, you need to explain the rationale of why. You can
refer to the particular commit message where this is explained.

## Are you affecting other teams functionalities?

If the PR does not affect other team functionalities, omit this section.

Otherwise, you need to give a rationale of why, and a description of the
mitigation plan agreed with other teams.

## How does this affect performance?

If you are confident that this PR does not affect performance, omit this section.

If performance could be affected (postively or negatively), please explain them.
Make references to how the differences were measured.

## Have you followed well established secure coding patterns?

If you have escaped user input, avoid path traversal, controlled user access, ...
(see https://owasp.org/www-project-top-ten/), you can omit this section.

If there is any particular aspect that the reviewer should pay particular
attention to, please explain it so it can be properly considered.