<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<goldensource-package version="8.7.1.93">
<package-comment/>
<businessobject displayString="GenericEvent" type="org.quartz.impl.JobDetailImpl">
<org.quartz.impl.JobDetailImpl id="0">
<group id="1">Events</group>
<jobClass>com.j2fe.scheduling.jobs.ExecuteEventJob</jobClass>
<jobDataMap id="2"/>
<name id="3">GenericEvent</name>
</org.quartz.impl.JobDetailImpl>
</businessobject>
<businessobject displayString="CallRMBStrateMessageChildTask" type="org.quartz.impl.triggers.CronTriggerImpl">
<org.quartz.impl.triggers.CronTriggerImpl id="0">
<cronExpression id="1">0 */3 0-23 ? * 1-7</cronExpression>
<group id="2">Events</group>
<jobDataMap id="3">
<entry>
<key id="4" type="java.lang.String">Event:DestinationName</key>
<value id="5" type="java.lang.String">T.SRD.PUBLISH.INSTR</value>
</entry>
<entry>
<key id="6" type="java.lang.String">Event:SubscriptionName</key>
<value id="7" type="java.lang.String">RMBStrateShortPaperPublish</value>
</entry>
<entry>
<key id="8" type="java.lang.String">EventName</key>
<value id="9" type="java.lang.String">RMBStrateMessageProcessorChildEvent</value>
</entry>
</jobDataMap>
<jobGroup id="10">Events</jobGroup>
<jobName id="11">GenericEvent</jobName>
<misfireInstruction>1</misfireInstruction>
<name id="12">CallRMBStrateMessageChildTask</name>
<nextFireTime id="13">2021-11-08T20:27:00.000+0530</nextFireTime>
<previousFireTime id="14">2021-11-08T20:24:00.000+0530</previousFireTime>
<priority>5</priority>
<startTime id="15">2021-11-08T20:13:09.000+0530</startTime>
<timeZone id="16">CAT</timeZone>
</org.quartz.impl.triggers.CronTriggerImpl>
</businessobject>
</goldensource-package>
