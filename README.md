# BugSwarm

This repository contains the artifacts collected for the paper "Critical Review of BugSwarm for Fault Localization and Program Repair".

**BugSwarn** is a benchmark of 3091 pairs of failing/passing builds that have been mined from TravisCI service and encapslued in a Docker image. 
In our critical review of BugSwarn we show that **112** pairs of builds match the requirements of automatic program repair and fault localization.

The main content of this repository are:

* The buggy and fixed files for each bug
* The diff between the buggy and fixed files
* The TravisCI log of the buggy and passing executions.
* The Docker manifest for each docker image
* A log paser to indentify the reasons of the failures
* And a [website](https://tqrg.github.io/BugSwarm/) that allows to browse all those artifacts.

## Repository content

* [`docs`](docs) contains all the data required for the website
* [`BugSwarm`](BugSwarm) contains each BugSwarm pair of builds buggy files, human patch, Docker manifest and Travis logs
* [`script`](script) contains the script we use to collect the data and generate the tables and figures of the paper
* [`bugswarm.json`](bugswarm.json) is a json file that contains all the metadata for each pair of builds.

## Repository Branches

Each branch of the repository contains a BugSwarm pair of builds. You can easily clone one specific branch with the following command line.
`git clone --single-branch --branch ProjectKorra-ProjectKorra-165108371 git@github.com:TQRG/BugSwarm.git ProjectKorra-ProjectKorra-165108371`

I recommend to only clone the master branch to reduce the size of the repository on your system.
`git clone --single-branch --branch master git@github.com:TQRG/BugSwarm.git BugSwarm`