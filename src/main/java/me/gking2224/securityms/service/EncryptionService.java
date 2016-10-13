package me.gking2224.securityms.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public interface EncryptionService {

    String toBase64(String value);

    byte[] fromBase64(String value);

    String fromBase64toString(String value, Charset charset);

    String fromBase64toString(String value);

    String toBase64(byte[] bytes);

    byte[] encrypt(byte[] bytes);

    String encrypt(String value, Charset charset) throws UnsupportedEncodingException;

    String encrypt(String value);

}
