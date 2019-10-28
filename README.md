# Enterprise Systems Development Project
*[Link to planning/spec document](https://docs.google.com/document/d/1-RYVGb9SWycJ3qjYdG6a2n_CVkIMA-091wgTMpM1pWw/edit?usp=sharing)*
*This project currently provides tooling for the ESD tech stack that is compatible with Docker, IntelliJ, and NetBeans.*

## Quickstart
Requirements:
* [jdk8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [gradle](https://gradle.org/install/)
* [git](https://git-scm.com/)
* [docker](https://docs.docker.com/get-started/)
* [docker-compose](https://docs.docker.com/compose/)

Steps:
1. Clone the repo `git clone https://github.com/novucs/esd.git && cd esd`
2. Run the project `docker-compose up -d`
3. [Open the app in your web browser](http://localhost:8080/app/)

When docker-compose is running, execute the relevant command to automatically build and deploy the project:
* UNIX: `./gradlew autodeploy -t`
* Windows: `gradlew.bat autodeploy -t`

## IDE Setup
IntelliJ:
* `File -> Open -> <select repo location>`
* `File -> Settings -> Editor -> Code Style -> Scheme Cog -> Import Scheme -> IntelliJ IDEA code style XML -> config/codestyles/GoogleStyle.xml`

NetBeans:
* `File -> Open Project -> <select repo location>`

NetBeans for UWE machines:
* `Tools -> Plugins -> Available Plugins -> Gradle Support -> Install`
* `Tools -> Options -> Miscallenous -> Gradle -> Scripts & Tasks -> Gradle arguments`
    * Add `netbeansdeploy`
* If `netbeansdeploy` build location is invalid, update task in the `build.gradle` file to match the GlassFish location.
    * For example: `C:\\Users\\r2-benson\\AppData\\Roaming\\Netbeans\\8.2\\config\\GF_4.1.1\\domain1\\autodeploy`

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
