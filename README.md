[![stagemonitor-h75px](https://cloud.githubusercontent.com/assets/2163464/3024619/70ed9cd0-dffb-11e3-9251-083e62d97f0d.png)](http://www.stagemonitor.org)


[![Build Status](https://travis-ci.org/stagemonitor/stagemonitor.svg?branch=master)](https://travis-ci.org/stagemonitor/stagemonitor) [![Coverage Status](https://coveralls.io/repos/stagemonitor/stagemonitor/badge.svg?branch=master&service=github)](https://coveralls.io/github/stagemonitor/stagemonitor?branch=master)
=================

Stagemonitor is a Java monitoring agent that tightly integrates with time series databases like Elasticsearch, Graphite and InfluxDB to analyze graphed metrics and [Kibana](http://www.elasticsearch.org/overview/kibana/) to analyze requests and call stacks. It includes preconfigured Grafana and Kibana dashboards that can be customized.

## More Information
For more information about the project, please see http://www.stagemonitor.org

## Live Demo
http://stagemonitor-demo.isys-software.de

## Getting Started
Check the [Installation](https://github.com/stagemonitor/stagemonitor/wiki/Installation) site of the wiki

## Issues
If you encounter any issues or if you have a question, don't hesitate to create an issue.

## Mailing List
The mailing list can be found at https://github.com/stagemonitor/stagemonitor-mailinglist

## Build Locally
To build this project locally, clone the repo and execute `./gradlew compileJava install` (Linux) `gradlew.bat compileJava install` (Windows) to install stagemonitor to your local maven repo (`~/.m2/repository/org/stagemonitor/`).

## Contributing
We love contributions from the community! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before creating a pull request.
