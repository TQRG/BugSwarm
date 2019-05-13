const fs = require('fs')
const stripAnsi = require('strip-ansi');
const Parser = require('./Parser').Parser;
const JavaParser = require('./javaParser').Parser;
const JsParser = require('./npmParser').Parser;
const PyParser = require('./pythonParser').Parser;
const ObjcParser = require('./objcParser').Parser;
const PhpParser = require('./phpParser').Parser;
const GoParser = require('./goParser').Parser;
const RubyParser = require('./rubyParser').Parser;

const fold_start = new RegExp("travis_fold:start:(.*)");
const fold_end = new RegExp("travis_fold:end:(.*)");

/**
 * @param job
 * @returns {Promise<String>}
 */
async function getLog(build) {
    return new Promise((resolve, reject) => {
        //console.log(__dirname + '/../../BugSwarm/'+build.bugId+'/failing.log')
        fs.readFile(__dirname + '/../../BugSwarm/'+build.bugId+'/failing.log', (err, data) => {
            if (err != null) {
                return reject("Log for " + build.bugId + " not found");
            }
            return resolve(data.toString());
        })
    });
}

async function parseLog(job) {
    return new Promise(async (resolve, reject) => {
        try {
            const log = await getLog(job);
            /**
             * @type {Parser[]}
             */
            const parsers = [new JavaParser(), new PyParser()];

            let exitCode = null;

            let tool = null;
            let tests = [];
            let errors = [];

            let lineStart = 0;
            for (let i = 0; i < log.length; i++) {
                if (i == log.length - 1 || (log[i] == '\r' && log[i + 1] == '\n') || log[i] == '\n') {
                    const line = stripAnsi(log.slice(lineStart, i));
                    if (log[i] == '\r') {
                        i++;
                    }
                    lineStart = i + 1;

                    if (line.length === 0) {
                        continue;
                    }


                    if ((!job.config || !job.config.language) && line.indexOf("Build language: ") == 0) {
                        if (!job.config) {
                            job.config = {};
                        }
                        job.config.language = line.replace("Build language: ", "");
                    }
                    if (line.indexOf("Done. Your build exited with ") != -1) {
                        exitCode = parseInt(line.substring("Done. Your build exited with ".length, line.length -1));
                    }

                    if (line.indexOf("fatal: Could not read from remote repository.") != -1)  {
                        errors.push({
                            type: 'Unable to clone'
                        })
                    }

                    for (let parser of parsers) {
                        if (job.config && !parser.isCompatibleLanguage(job.config.language)) {
                            continue;
                        }
                        try {
                            parser.parse(line);
                        } catch (e) {
                            console.error(e, parser, job.id);
                        }
                    }
                }
            }
            

            for (let parser of parsers) {
                tests = tests.concat(parser.tests);
                errors = errors.concat(parser.errors);

                if (parser.tool != null && tool == null) {
                    tool = parser.tool
                }
            }

            resolve({
                tests: tests,
                errors: errors,
                //log: log,
                tool: tool
            })
        } catch (e) {
            return reject(e);
        }
    });
}

module.exports.parser = parseLog;
module.exports.getLog = getLog;