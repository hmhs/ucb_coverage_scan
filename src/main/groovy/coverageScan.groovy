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
def complexityHighLow = props['complexityHighLow'].toString()

/**
 * Groovy script to be called by UrbanCode Build to look at the code coverage report for a build life and
 * determine if any records in the report do not match the criteria specified
 */

// Set up main scanner
CoverageScanner coverageScanner = new CoverageScanner(webUrl, userName, password)

// Call the method to obtain records not meeting the criteria specified
List<String> foundFailures = coverageScanner.getCoverageInfo(projectId, processId, buildLifeId,  scanLevel, percentage,
        percentageType, complexity, complexityHighLow)

// If items were found, print them out
if(foundFailures.size() > 0) {
    println "Code Coverage found which does not meet the criteria specified"
    foundFailures.each() { report ->
        println report
    }
} else {
    println "Code Coverage matches or exceeds set criteria."
}

// Return value is the number of records found
System.exit(foundFailures.size())