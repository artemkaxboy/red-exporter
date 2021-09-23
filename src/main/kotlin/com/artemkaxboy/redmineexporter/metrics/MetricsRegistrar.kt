package com.artemkaxboy.redmineexporter.metrics

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class MetricsRegistrar(

    private val metricsRegistry: MetricsRegistry,
) : ApplicationListener<MetricRegistrationEvent> {

    override fun onApplicationEvent(event: MetricRegistrationEvent) {
        metricsRegistry.updateMeterRegistration(event.exists, event.meterData)
    }
}

class MetricRegistrationEvent(
    val exists: Boolean,
    val meterData: List<MeterData>,
) : ApplicationEvent(Unit)
