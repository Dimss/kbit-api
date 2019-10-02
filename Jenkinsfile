import groovy.json.JsonOutput

def getGitCommitShortHash() {
    return sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
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
                            def scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
                            echo "${scmUrl}"
                            def crTemplate = readFile('ocp/tmpl/app-image-build.yaml')
                            def models = openshift.process(crTemplate,
                                    "-p=IS_NAME=${env.NAME}-${getGitCommitShortHash()}",
                                    "-p=IMAGE_NAME=${env.IMAGE_NAME}",
                                    "-p=IMAGE_TAG=${getGitCommitShortHash()}",
                                    "-p=GIT_REPO=${scmUrl}")
                            echo "${JsonOutput.prettyPrint(JsonOutput.toJson(models))}"
                            openshift.create(models)
                        }
                    }
                }
            }
        }
//
//        stage("Executing integration tests") {
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