package com.invadermonky.thaumicapi.api.warpevent;

import com.invadermonky.thaumicapi.warpevents.WarpEventRegistry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A functional interface used to define {@link IWarpEvent} classes for automatic registration
 * with {@link WarpEventRegistry}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WarpEvent {

}
