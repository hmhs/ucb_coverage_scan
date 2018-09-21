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

/** TODO: Add Groovy Doc dumbass */

class CoverageScanner {
    String baseUrl
    String userName
    String password
    HttpClient ucClient

    CoverageScanner(String baseUrl, String userName, String password) {
        this.baseUrl = baseUrl
        this.userName = userName
        this.password = password
        this.ucClient = this.initializeClient()
    }

    HttpClient initializeClient(){
        HttpClientBuilder builder = new HttpClientBuilder()
        builder.setPreemptiveAuthentication(true)
        builder.setUsername(this.userName)
        builder.setPassword(this.password)
        //Accept all certificates
        builder.setTrustAllCerts(true)
        return builder.buildClient()
    }

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

    List<String> getCoverageInfo(String projectId, String processId, String buildLife, String scanLevel, String percentage,
                            String percentageType, String complexity, String complexityHighLow) {
        String addUrl = "/rest2/projects/" + projectId + "/buildProcesses/" + processId + "/buildLives/" + buildLife + "/codeCoverage"
        def returnValue = []

        def coverageInfo = performGetRequest(this.ucClient,this.baseUrl + addUrl)
        if(coverageInfo == null && coverageInfo.size() != 0) {
            returnValue.add("No Coverage Report Found")
            return returnValue
        }

        if(coverageInfo.totalReports == 0) {
            returnValue.add("No Coverage Report Found")
            return returnValue
        }

        BigDecimal myPercentage = new BigDecimal(percentage)
        myPercentage = myPercentage / 100

        BigDecimal myComplexity = 0
        if(complexity != "" && complexity != null) {
            myComplexity = new BigDecimal(complexity)
            myComplexity = myComplexity / 100
        } else {
            myComplexity = 100
        }

        BigDecimal foundPercentage = 0
        BigDecimal foundComplexity = 0

        coverageInfo.reports.each() { allReports ->
            if(scanLevel == "report") {
                switch(percentageType) {
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
                if(foundPercentage < myPercentage) {
                    returnValue.add(allReports.name)
                }
            }
            if(scanLevel == "group") {
                allReports.groups.each() { group ->
                    switch(percentageType) {
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
                    foundComplexity = new BigDecimal(group.complexity.toString())
                    if(foundPercentage < myPercentage && foundComplexity <= myComplexity && complexityHighLow == "lower") {
                        returnValue.add(group.name)
                    }

                    if(foundPercentage < myPercentage && foundComplexity >= myComplexity && complexityHighLow == "higher") {
                        returnValue.add(group.name)
                    }
                }
            }
        }

        return returnValue
    }
}