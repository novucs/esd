pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('build') {
            steps {
                sh './gradlew autodeploy'
            }
        }
        stage('deploy') {
            steps {
                sh "ssh billy@esd.novucs.net bash -c 'cd ~/esd/cicd ; docker cp \$(docker-compose ps -q jenkins):${pwd()}/build/libs/*.war /tmp/app.war && docker cp /tmp/app.war \$(docker-compose ps -q app):/app/glassfish/domains/domain1/autodeploy/app.war' "
            }
        }
    }
}
