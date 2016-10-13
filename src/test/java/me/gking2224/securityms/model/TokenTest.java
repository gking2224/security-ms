package me.gking2224.securityms.model;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TokenTest {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        Encoder encoder = Base64.getUrlEncoder();
        Decoder decoder = Base64.getUrlDecoder();
        
        Long p1 = 1L;
        Long p2 = 2L;
        Set<Long> permissions = new HashSet<Long>();
        permissions.add(p1);
        permissions.add(p2);
        String username = "username";
        Instant timeout = Instant.now();
        
        DefaultToken t = new DefaultToken().username(username).timeout(timeout).permissions(permissions);
        
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        ObjectOutput oo = new ObjectOutputStream(bos);
        t.writeExternal(oo);
        oo.flush();
        
        String s = encoder.encodeToString(bos.toByteArray());
        
        ByteArrayInputStream bis = new ByteArrayInputStream(decoder.decode(s));
        ObjectInput oi = new ObjectInputStream(bis);
        
        DefaultToken t2 = new DefaultToken();
        t2.readExternal(oi);
        
        assertEquals(username, t2.getUsername());
        assertEquals(timeout, t2.getTimeout());
        assertEquals(permissions, t2.getPermissions());
    }

}
