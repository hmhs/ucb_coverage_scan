import com.urbancode.air.AirPluginTool
import com.hmhs.underpinning.uc.coverageScan.CoverageScanner

final airTool = new AirPluginTool(args[0], args[1])

final def props = airTool.getStepProperties()

def projectId = props['projectId'].toString()
def processId = props['processId'].toString()
def buildLifeId = props['buildLifeId'].toString()
def scanLevel = props['scanLevel'].toString()
def percentageType = props['percentageType'].toString()
def complexity = props['complexity'].toString()
def percentage = props['percentage'].toString()
def webUrl = props['webUrl'].toString()
def userName = props['userName'].toString()
def password = props['password'].toString()

CoverageScanner coverageScanner = new CoverageScanner(webUrl, userName, password)

List<String> foundFailures = coverageScanner.getCoverageInfo(projectId, processId, buildLifeId,  scanLevel, percentage,
        percentageType, complexity)

if(foundFailures.size() > 0) {
    println "Code Coverage found which does not meet the criteria specified"
    foundFailures.each() { report ->
        println report
    }
}

System.exit(foundFailures.size())