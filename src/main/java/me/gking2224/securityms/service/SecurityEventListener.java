package me.gking2224.securityms.service;

import me.gking2224.securityms.client.SecurityEvent;

public interface SecurityEventListener {

    void onEvent(SecurityEvent event);
}
