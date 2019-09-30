def getGitCommitShortHash() {
    return checkout(scm).GIT_COMMIT.substring(0, 7)
}

pipeline {
    agent {
        node {
            label 'maven'
        }
    }
    stages {
        stage("Step One - test") {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            echo 'Hello world'
                            echo "${getGitCommitShortHash()}"

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