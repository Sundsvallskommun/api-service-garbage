package se.sundsvall.garbage.integration.filehandler;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.sftp")
public record SftpProperties(String username, String password, String remoteHost, String filename) {

}
