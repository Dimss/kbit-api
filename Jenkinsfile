import groovy.json.JsonOutput

def getGitCommitHash() {
    return sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%H'").trim()
}

def getGitCommitShortHash() {
    return sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
}

def deployPg() {
    def pgModels = openshift.process("openshift//postgresql-ephemeral",
            "-p", "DATABASE_SERVICE_NAME=${getAppName()}",
            "-p", "POSTGRESQL_USER=${getAppName()}",
            "-p", "POSTGRESQL_PASSWORD=${getAppName()}",
            "-p", "POSTGRESQL_DATABASE=${getAppName()}")
    openshift.create(pgModels)

}

def getAppName() {
    return "${env.NAME}-${getGitCommitShortHash()}"
}

pipeline {
    agent {
        node {
            label 'maven'
        }
    }
    stages {
        stage("Run unit tests") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            echo "Hello from ${openshift.cluster()}'s default project: ${openshift.project()}"

                        }
                    }
                }
            }
        }
        stage("Run static code analysis") {
            steps {
                script {
                    echo "Running static code analysis"
                    withSonarQubeEnv('SonarQube1') {
//                        sh 'mvn clean package sonar:sonar'
                    }
                }
            }

        }
//        stage("Deploy integration tests dependencies") {
//            steps {
//                script {
//                    openshift.withCluster() {
//                        openshift.withProject() {
//                            echo "Deploying integration tests dependencies"
//
//                        }
//                    }
//                }
//            }
//        }
        stage("Build & push docker image ") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            def bcTemplate = readFile('ocp/tmpl/s2i-bc.yaml')
                            def models = openshift.process(bcTemplate,
                                    "-p=IS_NAME=${getAppName()}",
                                    "-p=REGISTRY_NAME=${env.REGISTRY_NAME}",
                                    "-p=IMAGE_NAME=${env.IMAGE_NAME}",
                                    "-p=IMAGE_TAG=${getGitCommitShortHash()}",
                                    "-p=GIT_REPO=${scm.getUserRemoteConfigs()[0].getUrl()}",
                                    "-p=GIT_REF=${getGitCommitHash()}")
                            echo "${JsonOutput.prettyPrint(JsonOutput.toJson(models))}"
                            openshift.create(models)
                            def bc = openshift.selector("buildconfig/${getAppName()}")
                            def build = bc.startBuild()
                            build.logs("-f --pod-running-timeout=60")
                        }
                    }
                }
            }
        }

        stage("Deploying integration tests dependencies") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            def pgModels = openshift.process("openshift//postgresql-ephemeral",
                                    "-p", "DATABASE_SERVICE_NAME=${getAppName()}",
                                    "-p", "POSTGRESQL_USER=${getAppName()}",
                                    "-p", "POSTGRESQL_PASSWORD=${getAppName()}",
                                    "-p", "POSTGRESQL_DATABASE=${getAppName()}")
                            openshift.create(pgModels)

                        }
                    }
                }
            }
        }
    }

    post {
        failure {
            script {
                openshift.withCluster() {
                    openshift.withProject() {
                        sh "echo 'this is failure catch'"
                    }
                }
            }
        }
    }
}