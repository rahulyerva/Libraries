package com.example
import groovy.yaml.YamlSlurper

class Pipeline {
    def script
    def configurationFile

    Pipeline(script, configurationFile) {
        this.script = script
        this.configurationFile = configurationFile
    }
    def notifyFailed(emailId) {
  	emailext (
		to:$emailId,
      		subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      		body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        	<p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""
    		 )
	}

    def execute() {
//    ===================== Your Code Starts Here =====================
//    Note : use "script" to access objects from jenkins pipeline run (WorkflowScript passed from Jenkinsfile)
//           for example: script.node(), script.stage() etc

//    ===================== Parse configuration file ==================
	def yaml = new YamlSlurper()
	def file = new File(configurationFile)
	def config = yaml.parseText(file.text)
	def tests=[:]
	tests["performance"] = {
  		stage ("performance"){    
    			node('label_example1') {  
        			try{
					sh "cd ${env.WORKSPACE}/${config.test[0].testFolder}"
					sh "${config.test[0].testCommand}"
				}
				catch(e){
					notifyFailed(${config.notification.email.to})
				}
    			}
  		}
	}
	tests["regression"] = {
  		stage ("regression"){    
    			node('label_example2') {  
        			try{
					sh "cd ${env.WORKSPACE}/${config.test[1].testFolder}"
					sh "${config.test[1].testCommand}"
					
				}
				catch(e){
					notifyFailed(${config.notification.email.to})
				}
				
    			}
  		}
	}
	tests["integration"] = {
  		stage ("integration"){    
    			node('label_example2') {  
        			try{
					sh "cd ${env.WORKSPACE}/${config.test[2].testFolder}"
					sh "${config.test[2].testCommand}"
				}
				catch(e){
					notifyFailed(${config.notification.email.to})
				}
    			}
  		}
	}
//    ===================== Run pipeline stages =======================	
	stages{
		script.stage('build'){
				try{
					def projectFolder=config.build.projectFolder
					sh "cd ${env.WORKSPACE}/${projectFolder}"
					sh "${config.build.buildCommand}"
				}
				catch(e){
					notifyFailed(${config.notification.email.to})
				}
			
		}
		script.stage('database'){
				try{
					def databaseFolder=config.database.databaseFolder
					sh "cd ${env.WORKSPACE}/${databaseFolder}"
					sh "${config.database.databaseCommand}"
				}			
				catch(e){
					notifyFailed(${config.notification.email.to})
				}
			
		}
		script.stage('deploy'){
				try{
					def deployFolder=config.build.buildFolder
					sh "cd ${env.WORKSPACE}/${deployFolder}"
					sh "${config.deploy.deployCommand}"
				}
				catch(e){
					notifyFailed(${config.notification.email.to})
				}
			
		}
		parallel tests
	}



//    ===================== End pipeline ==============================
    }
}
