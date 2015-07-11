abmutils
========

## Simple Headless Agent-Based Modeling Framework for java

This library provides a very simple set of tools useful to developing agent-based models (ABMs).  The most widely used ABM platform for Java is [Repast](http://repast.sourceforge.net/) which I have found cumbersome to use, especially when the models I implement are too complicated or large to have any need for an interactive graphical display of my agents during simulations.

I found my Repast models were only using the scheduling and random number capabilities provided by Repast, but I needed dozens of dependencies to just run a model in Headless mode.  So this library provides the basic capabilities I need and perhaps it will be useful to others.

Features:
* Scheduler (a discrete event schedule like what is provided in Repast)
* Experiment mananger (the ability to easily specify multi-factorial experiments with a simple YAML config file)
* IO (easily load data from CSV into a table)
* Random number generation (just a wrapper to [Uncommons Maths](http://maths.uncommons.org/))

## Disclaimer

This library was developed in association with a major agent-based model I've been working on and is relatively stable. Until I or someone else uses this for a second model, there are probably still some bugs / issues to be worked out. At some point I will add documentation soon on how to use.
