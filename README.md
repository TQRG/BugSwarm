# betsy (BPEL/BPMN Engine Test System)

[![Build Status](https://travis-ci.org/uniba-dsg/betsy.png?branch=master)](https://travis-ci.org/uniba-dsg/betsy)
[![Dependency Status](https://www.versioneye.com/user/projects/53b3c3b20d5bb8eb3c00001f/badge.svg?style=flat)](https://www.versioneye.com/user/projects/53b3c3b20d5bb8eb3c00001f)
[![Coverage Status](https://coveralls.io/repos/uniba-dsg/betsy/badge.svg?branch=master&service=github)](https://coveralls.io/github/uniba-dsg/betsy?branch=master)

Betsy is a tool to check the degree of conformance of a BPEL/BPMN engine against the BPEL/BPMN standard.

This software is licensed under the LGPL Version 3 Open Source License!

## Releases
- [Release v2.0.0 January 2015](https://github.com/uniba-dsg/betsy/releases/tag/2.0.0)
  - Release Notes: After one year since its first major release, we are proud to release the second major release with the following additional features:
    - Fully automated conformance testing of three BPMN engines
    - 70 conformance tests for a variety of BPMN features
    - Separating the code base into three parts, namely, BPEL, BPMN and process-language independent packages

	The changes are detailed in a conference proceedings paper accepted at the [9th International Symposium on Service-oriented System Engineering (SOSE 2015)](http://sose2015.com/).
- [Release v1.1.0 October 2014](https://github.com/uniba-dsg/betsy/releases/tag/v1.1.0)
- [Release v1.0.0 February 2014](https://github.com/uniba-dsg/betsy/releases/tag/v1.0.0)
- [Release v0.4.0 February 2014](https://github.com/uniba-dsg/betsy/releases/tag/v0.4.0-icst2014)
- [Release v0.3.0 December 2013](https://github.com/uniba-dsg/betsy/releases/tag/v0.3.0-icsoc2013)
- [Release v0.2.0 December 2012](https://github.com/uniba-dsg/betsy/releases/tag/v0.2.0-soca2012)
- [Release v0.1.0 July 2012](https://github.com/uniba-dsg/betsy/releases/tag/v0.1.0)

## Software Requirements
- Windows 7
- JDK 1.8.0_05 (64 Bit) or higher
  - `JAVA_HOME` should point to the jdk directory
  - `PATH` should include `JAVA_HOME/bin`
- JDK 1.7.X 
  - `JAVA7_HOME` should point to the jdk directory (needed to exectue older engines not supporting Java 8)

## Licensing
LGPL Version 3: http://www.gnu.org/licenses/lgpl-3.0.html

## Usage

Requirements (see above) have to be fulfilled to execute `betsy` on the command line with the configuration from `config.properties`.

### BPEL

```
usage: betsy [OPTIONS] <ENGINES> <PROCESSES>

Options:
 -b,--build-only                     Builds only the artifacts. Does
                                     nothing else.
 -c,--check-deployment               Verifies deployment instead of test
                                     success
 -e,--use-external-partner-service   Use external partner service instead
                                     of internal one
 -h,--help                           Print usage information.
 -o,--open-results-in-browser        Opens results in default browser
 -p,--partner-address <arg>          Partner IP and Port (defaults to
                                     localhost:2000)
 -t,--to-core-bpel <arg>             Transform to Core BPEL
 -f,--use-custom-test-folder <arg>   Use a custom test folder (folder name defaults to "test")

GROUPS for <ENGINES> and <PROCESSES> are in CAPITAL LETTERS.
<ENGINES>: [ALL, LOCALS, VMS, RECENT, ode, bpelg, openesb, petalsesb,
orchestra, active-bpel, openesb23, openesb231, petalsesb41, ode136,
ode-in-memory, ode136-in-memory, bpelg-in-memory, wso2_v3_1_0,
wso2_v3_0_0, wso2_v2_1_2, ode_v, bpelg_v, openesb_v, petalsesb_v,
orchestra_v, active_bpel_v]
<PROCESSES>: [ALL, BASIC_ACTIVITIES_WAIT, BASIC_ACTIVITIES_THROW,
BASIC_ACTIVITIES_RECEIVE, BASIC_ACTIVITIES_INVOKE,
BASIC_ACTIVITIES_ASSIGN, BASIC_ACTIVITIES, SCOPES_EVENT_HANDLERS,
SCOPES_FAULT_HANDLERS, SCOPES, STRUCTURED_ACTIVITIES_FLOW,
STRUCTURED_ACTIVITIES_IF, STRUCTURED_ACTIVITIES_FOR_EACH,
STRUCTURED_ACTIVITIES_PICK, STRUCTURED_ACTIVITIES, CONTROL_FLOW_PATTERNS,
STATIC_ANALYSIS, SA00019, SA00018, SA00017, SA00012, SA00056, SA00011,
SA00055, SA00010, SA00054, SA00053, SA00016, SA00015, SA00059, SA00014,
SA00058, SA00013, SA00057, SA00063, SA00062, SA00061, SA00060, SA00023,
SA00067, SA00022, SA00066, SA00021, SA00065, SA00020, SA00064, SA00025,
SA00069, SA00024, SA00068, SA00070, SA00072, SA00071, SA00034, SA00078,
SA00077, SA00032, SA00076, SA00037, SA00036, SA00035, SA00079, SA00081,
SA00080, SA00085, SA00084, SA00083, SA00082, SA00008, SA00007, SA00006,
SA00001, SA00045, SA00089, SA00044, SA00088, SA00087, SA00086, SA00005,
SA00048, SA00003, SA00047, SA00002, SA00046, SA00092, SA00091, SA00090,
SA00052, SA00051, SA00095, SA00050, SA00093, FAULTS, ERRORS, ...]

# Examples
$ betsy bpel # Running all tests for all engines
$ betsy bpel ode # Running all tests for Apache ODE
$ betsy bpel ode,bpelg # Running all tests for Apache ODE and bpel-g
$ betsy bpel ALL Sequence # Running Sequence test for all engines
$ betsy bpel ALL Sequence,While # Running Sequence and While test for all engines
$ betsy bpel ode Sequence # Running Sequence test for Apache ODE
$ betsy bpel ode Invoke-Catch # Running Invoke-Catch test for Apache ODE
$ betsy bpel -t sequence.xsl,pick.xsl ode_v # Running all tests for the virtualised Apache ODE with sequence.xsl and pick.xsl CoreBPEL transformations
$ betsy bpel -o # Opens the results in the default browser after a successful run
```

### BPMN

```
usage: betsy bpmn [OPTIONS] <ENGINES> <PROCESSES>

Options:
 -b,--build-only                Builds only the artifacts. Does nothing
                                else.
 -h,--help                      Print usage information.
 -o,--open-results-in-browser   Opens results in default browser

GROUPS for <ENGINES> and <PROCESSES> are in CAPITAL LETTERS.
<ENGINES>: [ALL, camunda, camunda710, camunda720, camunda730, activiti,
activiti5170, jbpm, jbpm610, jbpm620]


<PROCESSES>: [ALL, GATEWAYS, ACTIVITIES, ERRORS, EVENTS, BASICS, DATA,
MINIMAL, ExclusiveGateway, ExclusiveGateway_Default,...]
```

## Administrative Tasks

```
$ gradlew idea # Generating Intellij IDEA project files
$ gradlew eclipse # Generating Eclipse project files
$ gradlew groovydoc # Generating GroovyDoc
$ gradlew enginecontrol # Opens a Swing GUI that allows to install, start and stop supported engines
```

## Downloads

From public subversion directory https://lspi.wiai.uni-bamberg.de/svn/betsy/

## Project Structure

    downloads/ # downloads of the engines
    server/ # engine installation directory
    test/ # execution results and reports
    src/main/tests/configuration/[standard]/ # the test configuration
    src/main/tests/files/[standard]/ # the test files
    src/main/xslt/[standard]/[engine/] # common and engine specific xslt scripts
    src/main/resources/[standard]/[engine/] # common and engine specific xsds and other resources
    src/main/groovy # the main source code
    src/main/java # mock web service implementation

    # for BPMN
    maven/ # Maven
    jbpmdependencies/ # Dependencies for jBPM
    jbpmdeployer/ # jars to deploy a process to jBPM

    # for BPEL
    ant/ # Apache Ant
    soapui/ # soapUI

## Test Structure

	test/
	test/reports/
    test/reports/html/ # html junit reports
	test/$engine/
	test/$engine/$process/
	test/$engine/$process/process/
	test/$engine/$process/reports/
    test/$engine/$process/logs/

	BPEL structure
	test/$engine/$process/pgk/ # deployable zip files
	test/$engine/$process/soapui/ # soapUI test suite
	[test/$engine/$process/binding/ # binding package]
	[test/$engine/$process/composite/ # composite package]

	BPMN structure
	test/$engine/$process/testSrc/ # source java files for the test
    test/$engine/$process/testBin/ # class files for the test
    [test/$engine/$process/war/ # the files for the war file]

# Authors (in alphabetical order)

Adrian Bazyli, Annalena Bentele, Mathias Casar, [Matthias Geiger](http://www.uni-bamberg.de/pi/team/geiger/), [Simon Harrer](http://www.uni-bamberg.de/pi/team/harrer/), Christian Kremitzl, [Joerg Lenhard](http://www.uni-bamberg.de/pi/team/lenhard-joerg/), Lea-Louisa Maaß, Frederik Müller, Christian Preißinger, Cedric Röck , Severin Sobetzko and Andreas Vorndran

# Publications
The following scientific publications are either about betsy, have used betsy to present benchmarks or use and build upon data obtained through betsy:
 - Harrer, S., Lenhard, J.: [Betsy - A BPEL Engine Test System](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/12-a1-harrer-lenhard/), Bamberger Beiträge zur Wirtschaftsinformatik und Angewandten Informatik Nr. 90, Bamberg University, July 2012. ISSN 0937-3349, this is betsy's original architectural white paper
 - Harrer, S., Lenhard, J., Wirtz, G.: [BPEL Conformance in Open Source Engines](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/12-02-lenhard-wirtz-harrer/), Proceedings of the 5th IEEE International Conference on Service-Oriented Computing and Applications (SOCA'12), Taipei, Taiwan, December 17-19, 2012, see also the [presentation](https://lspi.wiai.uni-bamberg.de/svn/betsy/betsy-presentation-soca-2012.pdf) for which these [test results](https://svn.lspi.wiai.uni-bamberg.de/svn/betsy/test-results-soca-2012.zip) have been used.
 - Lenhard, J., Wirtz, G.: [Detecting Portability Issues in Model-Driven BPEL Mappings](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/13-03-lenhard-wirtz/), Proceedings of the 25th International Conference on Software Engineering and Knowledge Engineering (SEKE'2013), Boston, Massachusetts, USA, Knowledge Systems Institute, June 27 - 29, 2013
 - Lenhard, J., Wirtz, G.: [Measuring the Portability of Executable Service-Oriented Processes](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/13-05-lenhard-wirtz/), Proceedings of the 17th IEEE International EDOC Conference, Vancouver, Canada, September 9 - 13, 2013, Awarded Best Student Conference Paper in Service Science
 - Harrer, S., Lenhard, J., Wirtz, G.: [Open Source versus Proprietary Software in Service-Orientation: The Case of BPEL Engines](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/13-07-harrer-lenhard-wirtz/), Proceedings of the 11th International Conference on Service Oriented Computing (ICSOC '13), Berlin, Germany, December 2 - 5, 2013
 - Lenhard, J., Harrer, S., Wirtz, G.: [Measuring the Installability of Service Orchestrations Using the SQuaRE Method](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/13-08-harrer-lenhard-wirtz/), Proceedings of the 6th IEEE International Conference on Service-Oriented Computing and Applications (SOCA'13), Kauai, Hawaii, USA, December 16 - 18, 2013, Awarded Best Conference Paper
 - Harrer, S., Röck, C., Wirtz, G.: [Automated and Isolated Tests for Complex Middleware Products: The Case of BPEL Engines](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/14-04-harrer-wirtz/), Proceedings of the 7th IEEE  International Conference on Software Testing, Verification and Validation Workshops (ICSTW 2014), Testing Tools Track, Cleveland, OH, USA, March 30 – April 4, 2014
 - Geiger, M., Harrer, S. Lenhard, J. Casar, M., Vorndran, A., Wirtz, G.: [BPMN Conformance in Open Source Engines](http://www.uni-bamberg.de/pi/bereich/forschung/publikationen/15-01-geiger-harrer-lenhard-wirtz/), Proceedings of the 9 th IEEE International Symposium on Service-Oriented System Engineering (SOSE), San Francisco Bay, CA, USA, March 30 – April 3 2015

# Contribution Guide
- Fork
- Send Pull Request
