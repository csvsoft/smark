package com.csvsoft.smark.ui.event;

import com.csvsoft.smark.ui.SmarkAppBuilderUI;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class SmarkBuilderUIEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);

    public static void post(final Object event) {
        SmarkAppBuilderUI.getBuilderUIEventbus().eventBus.post(event);
    }

    public static void register(final Object object) {
        SmarkAppBuilderUI.getBuilderUIEventbus().eventBus.register(object);
    }

    public static void unregister(final Object object) {
        SmarkAppBuilderUI.getBuilderUIEventbus().eventBus.unregister(object);
    }

    @Override
    public final void handleException(final Throwable exception,
                                      final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
