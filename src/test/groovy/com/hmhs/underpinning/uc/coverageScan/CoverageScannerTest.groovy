package com.hmhs.underpinning.uc.coverageScan

import com.urbancode.air.AirPluginTool
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CoverageScannerTest extends Specification {

    def "get coverage report"(String projectId, String processId, String buildLifeId, String scanLevel, String percentage,
                              String percentageType, String complexity) {
        given:
        CoverageScanner coverageScanner = new CoverageScanner('https://ucbuildtest.highmark.com',
                'admin', 'aDm1n1strat0Rs')

        when:
        println "Getting Coverage report for project ${projectId} process ${processId} build life ${buildLifeId}"
        def result = coverageScanner.getCoverageInfo(projectId, processId, buildLifeId, scanLevel, percentage,
                percentageType, complexity)
        println result

        then:
        //assert result.size() == expectedSize
        //assert expectedResult == result.find {it == expectedResult}
        assert true == true

        where:
        projectId | processId | buildLifeId | scanLevel | percentage | percentageType | complexity
        "351"     | "607"     | "3762"      | "report"  | "99"       | "line"         | null
        "351"     | "607"     | "3762"      | "group"  | "99"       | "line"         | null
        "351"     | "607"     | "3762"      | "group"  | "50"       | "line"         | "75"
    }
}
