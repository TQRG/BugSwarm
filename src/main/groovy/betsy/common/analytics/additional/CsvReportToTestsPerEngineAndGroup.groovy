package betsy.common.analytics.additional

import betsy.common.aggregation.TrivalentResult
import betsy.common.analytics.CsvReportLoader
import betsy.common.analytics.model.CsvReport
import betsy.common.analytics.model.Result

import java.nio.file.Paths


class CsvReportToTestsPerEngineAndGroup {

    CsvReport report

    void toCsvReport(PrintStream writer) {
        report.getEngines().each { engine ->
            int totalSuccessful = 0
            int totalPartial = 0
            int totalFailed = 0
            report.getGroups().each { group ->
                Collection<Result> results = group.getResultsPerEngine(engine)
                int successful = results.count { it.support == TrivalentResult.PLUS}
                totalSuccessful += successful
                int partial = results.count { it.support == TrivalentResult.PLUS_MINUS}
                totalPartial += partial
                int failed = results.count { it.support == TrivalentResult.MINUS}
                totalFailed += failed

                writer.println "$successful\t${partial == 0 ? '' : partial}\t$failed"
            }

            writer.println "$totalSuccessful\t${totalPartial == 0 ? '' : totalPartial}\t$totalFailed"
        }

        writer.println "Engine in %"
        int total = report.tests.size()
        report.getEngines().each { engine ->
            int totalSuccessful = 0
            report.getGroups().each { group ->
                Collection<Result> results = group.getResultsPerEngine(engine)
                totalSuccessful += results.count { it.support == TrivalentResult.PLUS}
            }

            def successfulInPercent = (int) Math.round(((double) totalSuccessful / total * 100))
            writer.println "${engine.name}\t$totalSuccessful\t${successfulInPercent}%\t${100-successfulInPercent}%"
        }
    }

    public static void main(String[] args) {
        new CsvReportToTestsPerEngineAndGroup(report: new CsvReportLoader(Paths.get(args[0]), new CsvReport()).load()).toCsvReport(System.out)
    }
}
