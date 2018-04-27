package com.hmhs.underpinning.uc.coverageScan

import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import com.urbancode.commons.httpcomponentsutil.HttpClientBuilder
import java.math.BigDecimal

import groovy.json.JsonSlurper

import com.urbancode.air.AirPluginTool
import com.urbancode.ud.client.SystemClient

/**
 *  Coverage Scanner Class: Main class to initialize and check code coverage in a build life of UrbanCode Build
 */

class CoverageScanner {
    /**
     *  Base URL - UC Build base url.  example: https://ucbuild.example.com
     */
    String baseUrl
    /**
     *  User Name - User to use when connecting to UC Build
     */
    String userName
    /**
     * Password - Password for user connecting to UC Build
     */
     String password
    /**
     * ucClient - HTTP Client used to perform REST calls to UC Build
     */
    HttpClient ucClient

    /**
     * Constructor Method
     *
     * @param baseUrl UrbanCode Build Base URL (ex: https://ucbuild.example.com)
     * @param userName UrbanCode Build User Name
     * @param password UrbanCode Build Password for user supplied
     */
    CoverageScanner(String baseUrl, String userName, String password) {
        this.baseUrl = baseUrl
        this.userName = userName
        this.password = password
        this.ucClient = this.initializeClient()
    }

    /**
     * HttpClient initialization method
     *
     * @return HttpClient configured with UC Build parameters
     */
    HttpClient initializeClient(){
        HttpClientBuilder builder = new HttpClientBuilder()
        builder.setPreemptiveAuthentication(true)
        builder.setUsername(this.userName)
        builder.setPassword(this.password)
        //Accept all certificates
        builder.setTrustAllCerts(true)
        return builder.buildClient()
    }

    /**
     * Method to perform standard REST Get Request
     *
     * @param client HttpClient configured for UC Build
     * @param requestURL URL of the full REST call
     * @return JSON Object of data returned from the REST call
     */
    Object performGetRequest(HttpClient client, String requestURL) {
        HttpRequest request = new HttpGet(requestURL)
        //Execute the REST GET call
        HttpResponse response = client.execute(request)
        //Check that the call was successful
        int statusCode = response.getStatusLine().getStatusCode()
        if ( statusCode > 299 ) {
            println "ERROR : HttpGet to: "+requestURL+ " returned: " +statusCode
            return null
            //System.exit(1)
        } else {
            //Convert the InputStream returned by response.getEntity().getContent() to a String
            BufferedReader reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"))
            StringBuilder builder=new StringBuilder()
            for(String line=null;(line=reader.readLine())!=null;){
                builder.append(line).append("\n")
            }
            //Parse the returned JSON
            //http://groovy-lang.org/json.html
            JsonSlurper slurper = new JsonSlurper()
            def objects=slurper.parseText(builder.toString())
            //Ensure to release the connection
            request.releaseConnection()
            return objects
        }
    }

    /**
     * Method to set up REST call and perform logic processing of data returned from the REST call
     *
     * @param projectId UC Build project id
     * @param processId UC Build process id
     * @param buildLife UC Build BuildLife id
     * @param scanLevel Should the scan be performed at the root or group level of the report
     * @param percentage What percentage of coverage should be checked
     * @param percentageType Which percentage should be checked - method, line, branch
     * @param complexity The level of complexity on group level
     * @param complexityHighLow Group levels with complexity higher or lower will be checked depending on this flag
     * @return A list of string objects containing records found to not match the criteria specified
     */
    List<String> getCoverageInfo(String projectId, String processId, String buildLife, String scanLevel, String percentage,
                            String percentageType, String complexity, String complexityHighLow) {

        // Set up URL for obtaining the code coverage report for a specific build life
        String addUrl = "/rest2/projects/" + projectId + "/buildProcesses/" + processId + "/buildLives/" + buildLife + "/codeCoverage"
        def returnValue = []

        // Perform the get request to obtain code coverage
        def coverageInfo = performGetRequest(this.ucClient,this.baseUrl + addUrl)

        // If coverage wsa not found, return empty string
        if(coverageInfo == null && coverageInfo.size() != 0) {
            returnValue.add("No Coverage Report Found")
            return returnValue
        }

        if(coverageInfo.totalReports == 0) {
            returnValue.add("No Coverage Report Found")
            return returnValue
        }

        // Convert the numbers passed in to a percentage value
        BigDecimal myPercentage = new BigDecimal(percentage)
        myPercentage = myPercentage / 100
\
        BigDecimal myComplexity = 0
        if(complexity != "" && complexity != null) {
            myComplexity = new BigDecimal(complexity)
            myComplexity = myComplexity / 100
        } else {
            myComplexity = 100
        }

        BigDecimal foundPercentage = 0
        BigDecimal foundComplexity = 0

        // Look at each coverage report returned, typically this is one but may be more than one in some cases
        coverageInfo.reports.each() { allReports ->
            // Look at the specific level in the report, currently root/report level or group/line level.
            if(scanLevel == "report") {
                switch(percentageType) {
                    // Determine the percentage in the report by the value passed in on the percentage type
                    case "line":
                        foundPercentage = new BigDecimal(allReports.linePercentage.toString())
                        break
                    case "method":
                        foundPercentage = new BigDecimal(allReports.methodPercentage.toString())
                        break
                    case "branch":
                        foundPercentage = new BigDecimal(allReports.branchPercentage.toString())
                        break
                }
                // if the report percentage is less than the required percentage, add the report name to the
                // return results
                if(foundPercentage < myPercentage) {
                    returnValue.add(allReports.name)
                }
            }
            if(scanLevel == "group") {
                allReports.groups.each() { group ->
                    switch(percentageType) {
                        // Determine the percentage in the report by the value passed in on the percentage type
                        case "line":
                            foundPercentage = new BigDecimal(group.linePercentage.toString())
                            break
                        case "method":
                            foundPercentage = new BigDecimal(group.methodPercentage.toString())
                            break
                        case "branch":
                            foundPercentage = new BigDecimal(group.branchPercentage.toString())
                            break
                    }

                    // Store the complexity listed on the report
                    foundComplexity = new BigDecimal(group.complexity.toString())

                    // if the report percentage is less than the required percentage and the complexity level is
                    // higher/lower than the specified value, add the report group name to the return results
                    if(foundPercentage < myPercentage && foundComplexity <= myComplexity && complexityHighLow == "lower") {
                        returnValue.add(group.name)
                    }

                    if(foundPercentage < myPercentage && foundComplexity >= myComplexity && complexityHighLow == "higher") {
                        returnValue.add(group.name)
                    }
                }
            }
        }

        // Return the list of reports found
        return returnValue
    }
}