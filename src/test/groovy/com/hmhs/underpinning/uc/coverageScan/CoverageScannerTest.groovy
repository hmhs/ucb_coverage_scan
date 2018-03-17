package com.hmhs.underpinning.uc.coverageScan

import com.urbancode.air.AirPluginTool
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CoverageScannerTest extends Specification {

    def "get coverage report"(String projectId, String processId, String buildLifeId, String scanLevel, String percentage,
                              String percentageType, String complexity, Integer expectedCount) {
        given:
        CoverageScanner coverageScanner = new CoverageScanner('https://ucbuildtest.highmark.com',
                'admin', 'aDm1n1strat0Rs')

        when:
        println "Getting Coverage report for project ${projectId} process ${processId} build life ${buildLifeId}"
        println "Scan Level: ${scanLevel} Percentage: ${percentage} Percentage Type: ${percentageType} Complexity: ${complexity}"
        def result = coverageScanner.getCoverageInfo(projectId, processId, buildLifeId, scanLevel, percentage,
                percentageType, complexity)
        println result

        then:
        assert expectedCount == result.size()

        where:
        projectId | processId | buildLifeId | scanLevel | percentage | percentageType | complexity | expectedCount
        "351"     | "607"     | "3762"      | "report"  | "99"       | "line"         | null       | 1
        "351"     | "607"     | "3762"      | "report"  | "75"       | "line"         | null       | 0
        "351"     | "607"     | "3762"      | "report"  | "99"       | "method"       | null       | 1
        "351"     | "607"     | "3762"      | "report"  | "75"       | "method"       | null       | 0
        "351"     | "607"     | "3762"      | "report"  | "99"       | "branch"       | null       | 1
        "351"     | "607"     | "3762"      | "report"  | "60"       | "branch"       | null       | 0
        "351"     | "607"     | "3762"      | "group"   | "99"       | "line"         | null       | 20
        "351"     | "607"     | "3762"      | "group"   | "50"       | "line"         | "75"       | 6
        "351"     | "607"     | "3762"      | "group"   | "99"       | "method"       | null       | 21
        "351"     | "607"     | "3762"      | "group"   | "75"       | "method"       | "25"       | 2
        "351"     | "607"     | "3762"      | "group"   | "99"       | "branch"       | null       | 24
        "351"     | "607"     | "3762"      | "group"   | "75"       | "branch"       | "25"       | 2
        "442"     | "732"     | "3736"      | "report"  | "100"      | "line"         | null       | 1
        "442"     | "732"     | "3736"      | "group"   | "100"      | "line"         | null       | 1
    }
}
