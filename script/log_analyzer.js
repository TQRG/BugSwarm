(async () => {
    const fs = require('fs')
    const parser = require('./log_analyzer/parser-init').parser
    
    const reasons = {
        'Python': {
            test: 0,
            doc: 0,
            unknown: 0 
        },
        'Java': {
            test: 0,
            doc: 0,
            unknown: 0
        }
    }
    const failure_types = JSON.parse(fs.readFileSync(__dirname + '/../failure_types.json'))

    const categories = JSON.parse(fs.readFileSync(__dirname + '/../category.json'))
    const data = JSON.parse(fs.readFileSync(__dirname + '/../bugswarm.json'))
    const toFix = []
    try {
        const later = ['kairosdb-kairosdb-165948003','kairosdb-kairosdb-165948004', 'ansible-ansible-84327405', 'ansible-ansible-80717654']
        const ignore = ['tornadoweb-tornado-87216613', 'huanghongxun-HMCL-125590421']
        for (let build of data) {
            if (build.unique == false) {
                //continue
            }
            if (ignore.indexOf(build.bugId) != -1 || later.indexOf(build.bugId) != -1) {
                continue
            }
            // if  (build.bugId != 'scikit-learn-scikit-learn-311360398') {
            //     continue
            // }
            if (build.bugId.indexOf('terasolunaorg-guideline') != -1) {
                reasons[build.lang].doc ++
                if (!categories[build.bugId]) {
                    categories[build.bugId] = {}
                }
                categories[build.bugId].failure_category = "Documentation"
                continue
            }
            const result =  await parser(build)
            failure_types[build.bugId] = result
            // console.log(result)
            if (result.errors.length == 0 && result.tests.length == 0) {
                toFix.push(build.bugId)
                // console.log(toFix.length, "/Users/tdurieux/git/bugswarm/script/log_analyzer/../../BugSwarm/"+ build.bugId+ "/failing.log")
                //break
                reasons[build.lang].unknown ++

                if (!categories[build.bugId]) {
                    categories[build.bugId] = {}
                }
                categories[build.bugId].failure_category = "Unknown"
            } else {
                if (result.errors.length == 0) {
                    reasons[build.lang].test ++
                    if (!categories[build.bugId]) {
                        categories[build.bugId] = {}
                    }
                    categories[build.bugId].failure_category = "Test"
                    continue
                }
                for (var error of result.errors) {
                    if (reasons[build.lang][error.type] == null) {
                        reasons[build.lang][error.type] = 0
                    }
                    if (error.type == 'Checkstyle') {
                        console.log(build.repo, build.failed_job.job_id, build.failed_job.trigger_sha)
                    }
                    reasons[build.lang][error.type]++
                    if (!categories[build.bugId]) {
                        categories[build.bugId] = {}
                    }
                    categories[build.bugId].failure_category = error.type
                    break
                }
            }
            
        }
        console.log(toFix.length)
        console.log(reasons)
        fs.writeFileSync(__dirname + '/../category.json', JSON.stringify(categories))
        fs.writeFileSync(__dirname + '/../failure_types.json', JSON.stringify(failure_types))
    } catch (e) {
        console.log(e)
        // Deal with the fact the chain failed
    }
})();