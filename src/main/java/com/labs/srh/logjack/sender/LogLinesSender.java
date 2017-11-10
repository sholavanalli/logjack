package com.labs.srh.logjack.sender;

import com.labs.srh.logjack.LogMessage;

import java.util.List;

public interface LogLinesSender {

    void send(List<LogMessage> logMessages);
}
