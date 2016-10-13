package me.gking2224.securityms;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

public class EncryptorTest {

    SecurityConfiguration config = new SecurityConfiguration();
    @Before
    public void beforeTest() {
        config.setEncryptionSalt("def3aef30a");
        config.setEncryptionPassword("password1234");
    }
    @Test
    public void testStrong() {
        BytesEncryptor enc = config.encryptor();
        enc.encrypt("test".getBytes());
    }

}
