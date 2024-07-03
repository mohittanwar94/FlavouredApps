package com.qureka.skool.event

interface EventLogger {

    fun logFirebaseEvent(eventName: String)

    fun logGAEvents(eventName: String)

    fun logApsalarEvent(eventName: String)

    fun subscribeToChannel(channelName: String)
}