package com.renato.springbootstrap.config

import com.renato.springbootstrap.features.MyFeatures
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.togglz.core.context.StaticFeatureManagerProvider
import org.togglz.core.manager.EnumBasedFeatureProvider
import org.togglz.core.manager.FeatureManager
import org.togglz.core.manager.FeatureManagerBuilder
import org.togglz.core.repository.StateRepository
import org.togglz.core.spi.FeatureManagerProvider
import org.togglz.core.spi.FeatureProvider
import org.togglz.core.user.UserProvider
import org.togglz.kotlin.EnumClassFeatureProvider


@Configuration
class TogglzConfig {
    @Bean
    fun featureProvider() = EnumClassFeatureProvider(MyFeatures::class.java)

    @Bean
    @Primary
    fun myFeatureManager(featureProvider: FeatureProvider): FeatureManager {

        val featureManager = FeatureManagerBuilder()
            .featureProvider(featureProvider)
            .build()

        StaticFeatureManagerProvider.setFeatureManager(featureManager)
        return featureManager
    }
}