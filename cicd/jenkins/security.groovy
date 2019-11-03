#!groovy
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.security.*
import jenkins.model.*
import jenkins.security.s2m.AdminWhitelistRule

def jenkins_user = new File("/run/secrets/jenkins_user").text.trim()
def jenkins_pass = new File("/run/secrets/jenkins_pass").text.trim()

def hudson_realm = new HudsonPrivateSecurityRealm(false)
hudson_realm.createAccount(jenkins_user, jenkins_pass)
Jenkins.instance.setSecurityRealm(hudson_realm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
Jenkins.instance.setAuthorizationStrategy(strategy)
Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

def github_user = new File("/run/secrets/github_user").text.trim()
def github_pass = new File("/run/secrets/github_pass").text.trim()

def store = Jenkins.instance
        .getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]
        .getStore()
def domain = Domain.global()
def github_credentials = new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        'github_credentials',
        'Credentials for accessing GitHub',
        github_user,
        github_pass
)
store.addCredentials(domain, github_credentials)

Jenkins.getInstance.setNumExecutors(1)
Jenkins.getInstance.save()
