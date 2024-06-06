pipeline {
    agent {
        label 'lab-server'
    }
     environment {
        appUser = "bookinghotel"
        appName = "booking-hotel"
        appVersion = "0.0.1-SNAPSHOT"
        appType = "jar"
        processName = "${appName}-${appVersion}.${appType}"
        folderDeploy = "/datas/${appUser}"
        buildScript = "mvn clean install -DskipTests=true"
        copyScript = "cp target/${processName} ${folderDeploy}"
        permsScript = "chown -R jenkins. ${folderDeploy}"
        // killScript = "kill -9 \$(ps -ef| grep ${processName}| grep -v grep| awk '{print \$2}')"
        runScript = 'su jenkins -c "cd ${folderDeploy}; java -jar ${processName} > nohup.out 2>&1 &"'
    }

    stages {
        stage('info') {
          steps {
              sh(script: """ whoami;pwd;ls -la; """, label: "first time so give me your info")
          }
        }
        stage('build') {
            steps {
                sh(script: """ ${buildScript} """, label: "maven is building")
            }
        }
        stage('deploy') {
            steps {
                sh(script: """ whoami;pwd; """, label: "second time so give me your info")
                sh(script: """ ${copyScript} """, label: "copy the .jar file into deploy folder")
                sh(script: """ ${permsScript} """, label: "set permission folder")
                sh(script: """ ${killScript} """, label: "terminate the running process")
                sh(script: """ ${runScript} """, label: "run the project")
            }
        }

    }
}

  
    
