package com.browser.codedady.EventBus;

public class BrowserSearchEvent
{
    private String mMessage;

    public BrowserSearchEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
