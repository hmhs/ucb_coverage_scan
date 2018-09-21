package com.hmhs.underpinning.uc.coverageScan

import com.urbancode.air.AirPluginTool
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CoverageScannerTest extends Specification {

    def "get coverage report"(String projectId, String processId, String buildLifeId, String scanLevel, String percentage,
                              String percentageType, String complexity,  String complexityHighLow, Integer expectedCount) {
        given:
        CoverageScanner coverageScanner = new CoverageScanner('https://ucbuildtest.highmark.com',
                'admin', 'aDm1n1strat0Rs')

        when:
        println "Getting Coverage report for project ${projectId} process ${processId} build life ${buildLifeId}"
        println "Scan Level: ${scanLevel} Percentage: ${percentage} Percentage Type: ${percentageType} Complexity: ${complexity}"
        def result = coverageScanner.getCoverageInfo(projectId, processId, buildLifeId, scanLevel, percentage,
                percentageType, complexity, complexityHighLow)
        println result

        then:
        assert expectedCount == result.size()

        where:
        projectId | processId | buildLifeId | scanLevel | percentage | percentageType | complexity | complexityHighLow | expectedCount
        "351"     | "607"     | "3762"      | "report"  | "99"       | "line"         | null       | "lower"           | 1
        "351"     | "607"     | "3762"      | "report"  | "75"       | "line"         | null       | "higher"          | 0
        "351"     | "607"     | "3762"      | "report"  | "99"       | "method"       | null       | "lower"           | 1
        "351"     | "607"     | "3762"      | "report"  | "75"       | "method"       | null       | "higher"          | 0
        "351"     | "607"     | "3762"      | "report"  | "99"       | "branch"       | null       | "lower"           | 1
        "351"     | "607"     | "3762"      | "report"  | "60"       | "branch"       | null       | "higher"          | 0
        "351"     | "607"     | "3762"      | "group"   | "99"       | "line"         | null       | "lower"           | 20
        "351"     | "607"     | "3762"      | "group"   | "50"       | "line"         | "75"       | "lower"           | 6
        "351"     | "607"     | "3762"      | "group"   | "50"       | "line"         | "75"       | "higher"          | 0
        "351"     | "607"     | "3762"      | "group"   | "99"       | "method"       | null       | "lower"           | 21
        "351"     | "607"     | "3762"      | "group"   | "75"       | "method"       | "25"       | "lower"           | 2
        "351"     | "607"     | "3762"      | "group"   | "75"       | "method"       | "25"       | "higher"          | 7
        "351"     | "607"     | "3762"      | "group"   | "99"       | "branch"       | null       | "lower"           | 24
        "351"     | "607"     | "3762"      | "group"   | "75"       | "branch"       | "25"       | "lower"           | 2
        "351"     | "607"     | "3762"      | "group"   | "75"       | "branch"       | "25"       | "higher"          | 17
        "442"     | "732"     | "3736"      | "report"  | "100"      | "line"         | null       | "lower"           | 1
        "442"     | "732"     | "3736"      | "group"   | "100"      | "line"         | null       | "higher"          | 1
    }
}
