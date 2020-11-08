package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by Andreas Kutschera.
 */
@Component
@Profile({ "producer", "all" })
public class ProducerBean {

    @Autowired
    private StreamBridge streamBridge;

    public void sendCity( String city ) {
        streamBridge.send( "produceTopic", new Address( city ) );
    }

}
