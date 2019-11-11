# Enterprise Systems Development Project
_This project currently provides tooling for the ESD tech stack that is compatible with Docker, IntelliJ, and NetBeans._

Other Documents:

* [Planning and Specification Documents](https://docs.google.com/document/d/1-RYVGb9SWycJ3qjYdG6a2n_CVkIMA-091wgTMpM1pWw/edit?usp=sharing)
* **Sprint 1**
    * [Presentation Slides](https://docs.google.com/presentation/d/1YE8q5Ss_eXeBY88T_o1lAt124U194z2HVEm3Ye2s6Zc/edit#slide=id.g65affc0ea7_0_26)
    * [Gantt Chart](https://docs.google.com/spreadsheets/d/1CuMzPxT09XPu4rKxUHP4BeWPx02Qsc_23hW29At_4mM/edit?usp=sharing)
    * [Team Meeting Logs](https://docs.google.com/document/d/1Sqs76sz8YiU-DdGOzemonaKh34DnCnZv508iSUtr2qY/edit)
    * [Task Delivery Form](https://docs.google.com/spreadsheets/d/12OIcEzbHAyU1NgS5qefXVFrq9kBFAxa1R7tE03d2C8g/edit)

## Quickstart
Requirements:
* [jdk8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [gradle](https://gradle.org/install/)
* [git](https://git-scm.com/)
* [docker](https://docs.docker.com/get-started/)
* [docker-compose](https://docs.docker.com/compose/)

Optional development dependencies:
* [node (lts)](https://nodejs.org/en/)
* [gulp](https://gulpjs.com/)

Steps:
1. Clone the repo `git clone https://github.com/novucs/esd.git && cd esd`
2. Build the repo `./gradlew autodeploy` (for UNIX) or `gradlew.bat autodeploy` (for Windows)
3. Run the project `docker-compose up -d`
4. [Open the app in your web browser](http://localhost:8080/app/)

When docker-compose is running, execute the relevant command to automatically build and deploy the project:
* UNIX: `./gradlew autodeploy -t`
* Windows: `gradlew.bat autodeploy -t`

## Developer Tips
During development, to run all CI checks locally you may perform the following command:
* `./gradlew checkBuildReport`

All the reports for any check/test failures will be saved into the relevant directory under:
`./build/reports/`

#### Compile with SCSS

In order to compile your SCSS files to CSS, please make sure you've installed Node and NPM.

1. Install Node via `apt-get install node`
2. Run `npm install -g gulp` to install Gulp for your system.
3. Run `npm install` in the repository root to install the dependencies required for Gulp.
4. Run `gulp` to compile SCSS.

## IDE Setup
IntelliJ:
* `File -> Open -> <select repo location>`
* `File -> Settings -> Editor -> Code Style -> Scheme Cog -> Import Scheme -> IntelliJ IDEA code style XML -> config/codestyles/GoogleStyle.xml`

NetBeans:
* `File -> Open Project -> <select repo location>`

NetBeans for UWE machines:
* `Tools -> Plugins -> Available Plugins -> Gradle Support -> Install`
* `File -> Open Project -> <select repo location>`
* `Tools -> Options -> Miscallenous -> Gradle -> Scripts & Tasks -> Gradle arguments`
    * Add `netbeansdeploy`
* Make sure to start the GlassFish server via the 'Services' tab:
    * Right-click `Servers -> GlassFish Server 4.1.1` and click `Start`
* If `netbeansdeploy` build location is invalid, update task in the `build.gradle` file to match the GlassFish location.
    * For example: `C:\\Users\\r2-benson\\AppData\\Roaming\\Netbeans\\8.2\\config\\GF_4.1.1\\domain1\\autodeploy`
* To deploy, click the big fancy Green Play button!

## IDE Debug Setup

To debug requests coming into Servelets you must enable remote debugging on Glassfish then connect to the remote
instance in your IDE

1. Set up remote debug configuration in IntelliJ
    
    Go to Run > Edit Configurations and add a new Remote configuration.
    
    Set the port to 9009 and the classpath to esd and save.
    
2. Run the Debugger

    From within Intellij, run the remote debug configuration you have just setup. The debug window will
    popup and say.  
    
    `Connected to the target VM, address: 'localhost:9009', transport: 'socket'`
    
    Connecting to localhost:8080 and placing a breakpoint in the servlet code should break whenever a request
    hits the endpoint.

## Code checks
To perform code checks with: `./gradlew check`

Code quality reports will be generated after running checks, view them locally at:
* CheckStyle: `./build/reports/checkstyle/main.html`
* PMD: `./build/reports/pmd/main.html`
* SpotBugs: `./build/reports/spotbugs/main.html`
* Tests: `./build/reports/tests/test/index.html`

If any lint checks seem out of place, and are difficult to work with please let @novucs know.

## Contributing
Feel free to branch off this repo in the format `feature/{ticket-number}-{name-of-feature}` e.g. 
`feature/123-add-user-create-endpoint`. The CI/CD pipeline will not execute on forks of this
repository, so its advisable to actively maintain branches here. Though feel free to do what you
want. I'm not your mum.

Once you have completed a feature, open a pull request to the master branch. If CI is passing, and
you are happy with your code, ask another member of the team to do a code review. When all checks
are green, hit `Squash and merge`.

## Deployments
There are currently two types of deployments accessible from the CI/CD pipeline:
* [latest](http://esd.novucs.net:8080/latest/) - Updated on every change merged into master
* [stable](http://esd.novucs.net:8080/stable/) - Updated on every release

All repository releases are marked via tags, and are accessible [here](https://github.com/novucs/esd/releases).

