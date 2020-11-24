package com.example

class Pipeline {
    def script
    def configurationFile

    Pipeline(script, configurationFile) {
        this.script = script
        this.configurationFile = configurationFile
    }

    def execute() {
//    ===================== Your Code Starts Here =====================
//    Note : use "script" to access objects from jenkins pipeline run (WorkflowScript passed from Jenkinsfile)
//           for example: script.node(), script.stage() etc

//    ===================== Parse configuration file ==================
	def yaml = new Yaml()

	def config = yaml.load(this.configurationFile)

	def tests=[:]

	def notifyFailed() {
  		emailext (
			to:"${config.notification.email.to}",
      			subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      			body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        		<p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""
    			)
	}

	tests["performance"] = {
  		stage ("performance"){    
    			node('label_example1') {  
        			try{
					sh "cd ${config.test[0].testFolder}"
					sh "${config.test[0].testCommand}"
				}
				catch(e){
					notifyFailed()
				}
    			}
  		}
	}
	tests["regression"] = {
  		stage ("regression"){    
    			node('label_example2') {  
        			try{
					sh "cd ${config.test[1].testFolder}"
					sh "${config.test[1].testCommand}"
					
				}
				catch(e){
					notifyFailed()
				}
				
    			}
  		}
	}
	tests["integration"] = {
  		stage ("integration"){    
    			node('label_example2') {  
        			try{
					sh "cd ${config.test[2].testFolder}"
					sh "${config.test[2].testCommand}"
				}
				catch(e){
					notifyFailed()
				}
    			}
  		}
	}
	
	stages{
		script.stage('build'){
				try{
					def projectFolder=config.build.projectFolder
					sh "cd ${projectFolder}"
					sh "${config.build.buildCommand}"
					sh "cd .."
				}
				catch(e){
					notifyFailed()
				}
			
		}
		script.stage('database'){
				try{
					def databaseFolder=config.database.databaseFolder
					sh "cd ${databaseFolder}"
					sh "${config.database.databaseCommand}"
					sh "cd .."
				}			
				catch(e){
					notifyFailed()
				}
			
		}
		script.stage('deploy'){
				try{
					//add deploy folder to config file as 'project'
					def deployFolder=config.build.buildFolder
					sh "cd ${deployFolder}"
					sh "${config.deploy.deployCommand}"
					sh "cd .."
				}
				catch(e){
					notifyFailed()
				}
			
		}
		parallel tests
	}

//    ===================== Run pipeline stages =======================

//    ===================== End pipeline ==============================
    }
}

