import javax.mail.*
import javax.mail.internet.*

Address[] jenkinsAdmin = [
    'jenkins-admin@FreeBSD.org',
].collect { new InternetAddress(it) }

patterns = [
    'hudson.remoting.RequestAbortedException:',
    'Connection aborted: org.jenkinsci.remoting',
    'Test reports were found but none of them are new.'
]

try {
    logger.write('Checking with false-positive patterns...\n')
    def logFilePath = build.getLogFile().getPath();
    String logContent = new File(logFilePath).text;
    patterns.find {
        if (logContent.find(/$it/)) {
            logger.write('******** This is Jenkins internal issue! ********\n')
            msg.setRecipients(Message.RecipientType.TO, jenkinsAdmin)
            return true
        }
        return false
    }
} catch (all) {
        logger.write('******** Pre-send script got exception! ********\n')
        msg.setRecipients(Message.RecipientType.TO, jenkinsAdmin)
}
