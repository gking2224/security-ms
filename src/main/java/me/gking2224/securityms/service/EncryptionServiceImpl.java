package me.gking2224.securityms.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncryptionServiceImpl implements EncryptionService {

    @Autowired
    private BytesEncryptor encryptor;

    private Encoder base64Encoder = Base64.getUrlEncoder();
    
    private Decoder base64Decoder = Base64.getUrlDecoder();

    @Autowired
    private Charset charset;

    @Override
    public String encrypt(final String value) {
        try {
            return encrypt(value, this.charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public String encrypt(final String value, final Charset charset) throws UnsupportedEncodingException {
        return new String(encryptor.encrypt(getBytes(value, charset)), charset);
    }

    @Override
    public byte[] encrypt(final byte[] bytes) {
        return encryptor.encrypt(bytes);
    }

    private byte[] getBytes(final String value) {
        try {
            return getBytes(value, this.charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    private byte[] getBytes(final String value, final Charset charset) throws UnsupportedEncodingException {
        return value.getBytes(charset);
    }

    @Override
    public String toBase64(final String value) {
        return toBase64(getBytes(value));
    }

    @Override
    public String toBase64(final byte[] bytes) {
        return base64Encoder.encodeToString(bytes);
    }
    
    @Override
    public byte[] fromBase64(final String value) {
        return base64Decoder.decode(value);
    }
    
    @Override
    public String fromBase64toString(final String value) {
        return fromBase64toString(value, charset);
    }
    
    @Override
    public String fromBase64toString(final String value, final Charset charset) {
        return new String(fromBase64(value), charset);
    }

}
