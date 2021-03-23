package com.renato.springbootstrap.features

import org.togglz.core.annotation.EnabledByDefault
import org.togglz.core.context.FeatureContext

enum class MyFeatures {
    @EnabledByDefault
    MY_FEATURE_1,

    MY_FEATURE_2;

    fun isActive(): Boolean {
        return FeatureContext.getFeatureManager().isActive { name }
    }
}