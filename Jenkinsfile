#!groovy

def workerNode = "devel11"

pipeline {
	agent {label workerNode}
	options {
		buildDiscarder(logRotator(artifactDaysToKeepStr: "",
			artifactNumToKeepStr: "", daysToKeepStr: "30", numToKeepStr: "30"))
		timestamps()
	}
	tools {
		jdk 'jdk11'
		maven "Maven 3"
	}
	triggers {
		pollSCM("H/03 * * * *")
		upstream(upstreamProjects: "Docker-payara6-bump-trigger",
				threshold: hudson.model.Result.SUCCESS)
	}
	stages {
		stage("clear workspace") {
			steps {
				deleteDir()
				checkout scm
			}
		}
		stage("Maven build") {
			steps {
				sh "mvn verify pmd:pmd pmd:cpd spotbugs:spotbugs"

				junit testResults: '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml'

				script {
					def java = scanForIssues tool: [$class: 'Java']
					publishIssues issues: [java], unstableTotalAll:1

					def pmd = scanForIssues tool: [$class: 'Pmd']
					publishIssues issues: [pmd], unstableTotalAll:1

					// spotbugs still has some outstanding issues with regard
					// to analyzing Java 11 bytecode.
					// def spotbugs = scanForIssues tool: [$class: 'SpotBugs']
					// publishIssues issues:[spotbugs], unstableTotalAll:1
				}
			}
		}
		stage("deploy") {
			when {
				branch "master"
			}
			steps {
				sh "mvn jar:jar deploy:deploy"
			}
		}
	}
}
