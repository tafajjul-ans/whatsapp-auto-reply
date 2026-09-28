package com.example.autoreply;

import android.app.Notification;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        // Check if message is from WhatsApp
        if ("com.whatsapp".equals(packageName)) {
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            String text = extras.getString(Notification.EXTRA_TEXT);

            if (text != null && text.toLowerCase().contains("hi")) {
                sendDirectReply(notification, "Hello! Yeh ek automated reply hai.");
            }
        }
    }

    private void sendDirectReply(Notification notification, String replyText) {
        for (Notification.Action action : notification.actions) {
            if (action.getRemoteInputs() != null) {
                for (RemoteInput remoteInput : action.getRemoteInputs()) {
                    Bundle data = new Bundle();
                    data.putCharSequence(remoteInput.getResultKey(), replyText);
                    Intent intent = new Intent();
                    RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, data);
                    try {
                        action.actionIntent.send(this, 0, intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
