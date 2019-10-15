# Enterprise Systems Development Project
*We're not entirely sure what it is yet. Though we do know we need Glassfish,
Derby, and JDK8. This project currently provides tooling for the ESD tech stack
that is compatible with Docker, IntelliJ, and NetBeans.*

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

NetBeans:
* `File -> Open Project -> <select repo location>`

## Linting
To perform linting checks: `./gradlew check`

Code quality reports will be generated after running checks, view them locally at:
* [CheckStyle](build/reports/checkstyle/main.html)
* [PMD](build/reports/pmd/main.html)
* [SpotBugs](build/reports/spotbugs/main.html)

If any lint checks seem out of place, and are difficult to work with please let @novucs know.

## Testing
Currently only unit tests are supported. To run them, simply execute: `./gradlew test`

Code coverage and integration tests will be defined when the project specification has been confirmed.

## Contributing
Feel free to branch off this repo in the format `feature/{ticket-number}-{name-of-feature}` e.g. 
`feature/123-add-user-create-endpoint`. The CI/CD pipeline will not execute on forks of this
repository, so its advisable to actively maintain branches here. Though feel free to do what you
want. I'm not your mum.

Once you have completed a feature, open a pull request to the master branch. If CI is passing, and
you are happy with your code, ask another member of the team to do a code review. When all checks
are green, hit `Squash and merge`.
