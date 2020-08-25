package com.agan.redis.service;


public interface NettyClientService {

        public String sendSyncMsg(String text);

        public void ackSyncMsg(String msg) ;
}
