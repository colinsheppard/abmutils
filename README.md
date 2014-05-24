abmutils
========

## Simple Agent-Based Modeling Framework for java

This library provides a very simple set of tools useful to developing agent-based models (ABMs).  The most widely used ABM platform for Java is [Repast](http://repast.sourceforge.net/) which I have found cumbersome to use, especially when the models I implement are too complicated or large to have any need for an interactive graphical display of my agents during simulations.

I found my Repast models were only using the scheduling and random number capabilities provided by Repast, but I needed dozens of dependencies to just run a model in Headless mode.  So this library provides the basic capabilities I need and perhaps it will be useful to others.

Features:
* Scheduler (a discrete event schedule like what is provided in Repast)
* Random number generation (just a wrapper to [Uncommons Maths](http://maths.uncommons.org/))

## Disclaimer

This library is under development and not even close to a release stage.  But, I'm actively developing an agent-based model (thoroughout 2014 and into 2015) using this library.  I therefore will be adding features and fixing bugs and would welcome others to test it out, submit issues, or contribute.
