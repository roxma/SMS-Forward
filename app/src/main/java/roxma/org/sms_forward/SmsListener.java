package roxma.org.sms_forward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsListener extends BroadcastReceiver {

    public SmsListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");
        Log.i("sms", "on receive," + intent.getAction());
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                String messageBody = smsMessage.getMessageBody();
                String fromSms = smsMessage.getDisplayOriginatingAddress();
                String emailFrom = smsMessage.getEmailFrom();
                String address = smsMessage.getOriginatingAddress();
                Log.i("sms", "body: " + messageBody);
                Log.i("sms", "address: " + address);

                String message = "[" + address + "] " + messageBody;

                String number = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("number", "");
                boolean save = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("saveNext", false);
                if (save) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                    editor.putString("from", address);
                    editor.commit();
                    editor.putBoolean("saveNext",false);
                    editor.commit();
                }
                String from = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("from", "");
                Log.i("sms", String.valueOf(from.equals(address)));
                if ((from != "" && from.equals(address)) || from == "") {
                    if (number == "") {
                        Log.i("sms", "phone number not set. ignore this one.");
                        return;
                    }
                    Log.i("sms", "sending to " + number);

                    Log.i("sms", "message send:" + message);
                    SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
                }
            }
        }
    }
}
