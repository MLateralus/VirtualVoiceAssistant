package nowim.speechtotext;

import android.net.Uri;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;


public class Methods extends AppCompatActivity {

    public Intent hideApp () {
        Intent hideIntent = new Intent(Intent.ACTION_MAIN);
        hideIntent.addCategory(Intent.CATEGORY_HOME);
        hideIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return hideIntent;
    }

    public Intent sendSMS(String msg, String caller) {
        //readContacts() -> caller
        msg = msg.substring(9);  /*chocieSMS.length()*/
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", msg);
        sendIntent.putExtra("address", caller);
        sendIntent.setType("vnd.android-dir/mms-sms");
        //    startActivity(sendIntent);
        return sendIntent;
    }

    public Intent browse(String page) {
        page = page.substring(6); /*choiceBrowser.length()*/
        String url = "https://www.google.pl/search?q=" + page;
        Intent browseNET = new Intent(Intent.ACTION_VIEW);
        browseNET.setData(Uri.parse(url));
        return browseNET;
    }

    public Intent alarm(int hour, int minute) {
        Intent Alarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        Alarm.putExtra(AlarmClock.EXTRA_HOUR, hour);
        Alarm.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        //Alarm.putExtra(AlarmClock.EXTRA_DAYS,"MONDAY");
        return Alarm;
    }

    public Intent maps(String address) {
        address = address.substring(16); /* mapa.length() */
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        return mapIntent;
    }

    public String checkPolish(String msg) {
        msg = msg.replace("ć", "c");
        msg = msg.replace("ś", "s");
        msg = msg.replace("ą", "a");
        msg = msg.replace("ę", "e");
        msg = msg.replace("ó", "o");
        msg = msg.replace("ż", "z");
        msg = msg.replace("ź", "z");
        msg = msg.replace("ł", "l");
        msg = msg.replace("ń", "n");
        return msg;
    }

}