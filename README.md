# IBM UrbanCode Buil Code Coverage Scanner
---

This plugin scans a UC Build Code Coverage Report.  Currently this was developed to read JaCoCo reports but may be
extended to look at other code coverage reports (ex: karama) in the future.

## Build Information
---

The plugin is built with Gradle.  All groovy scripts should be placed and called in src/main/groovy.  All files to be
placed into the plugin root should be placed into src/ucbuild/zip.  Dependent objects will automatically be placed into
the lib folder at the root of the plugin.

### To Build
---
- Update the unit test class with the UC Build user and password to use for testing and update the data in the test class
for the data stored in instance of UC Build to use for testing
- Run ./gradlew build distPlugin
- Build will compile the Groovy code and run the unit test cases
- The distPlugin task will zip up the required files and place the plugin zip file into build/distributions