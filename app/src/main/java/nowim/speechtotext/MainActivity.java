package nowim.speechtotext;
/*
Wojciech Szymczyk, Michał Czerwień
Virtual Personal Assistant, ver 0.5
Api level: 17

 */
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;                 //now only for recognition, later on let's use speech.RecognizerResultsIntent or speech* to obtain and display the results of a search
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.content.ComponentName;



public class MainActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;

    private ImageButton ButtonToRecord;
    private Button ButtonToMinimalize;
    private TextView RecognisedText;
    Methods method = new Methods();
    About about = new About();
    String phone = new String();
    String name = new String();
    String id = new String();
    String number = new String();
    String nameOfContact = new String();
    boolean minimalized = false;
    boolean isCalling;
    String choiceMSG = "wiadomość";
    String choiceBrowser = "Szukaj"; /* Szukaj always starts with big S, O_o */
    String choiceAlarm  = "budzik";
    String choiceMap = "Znajdź na mapie";
    String choiceCall = "Zadzwoń";
    String test = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);      // view declared in xml, You can change the layout there

        RecognisedText = (TextView) findViewById(R.id.Text);
        about.AboutText = (TextView) findViewById(R.id.Text);
        ButtonToRecord = (ImageButton) findViewById(R.id.ButtonToRecord);
        ButtonToMinimalize = (Button) findViewById(R.id.Minimalize);

        ButtonToRecord.setOnClickListener(new View.OnClickListener() {      // creation of layout, found by ID

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);          // after pressing button, perform the action of recognition
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pl-PL");
                try {                                                       // if speech recognition not supported, exception is being thrown
                    startActivityForResult(intent, RESULT_SPEECH);
                    RecognisedText.setText("");

                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Device doesn't support speech recognition",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        ButtonToMinimalize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (minimalized) {
                    stopService(new Intent(getApplication(), MinimalizedApplication.class));
                    minimalized = false;
                } else {
                    startService(new Intent(getApplication(), MinimalizedApplication.class));
                    minimalized = true;
                    startActivity(method.hideApp());

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                             // we can probably get rid of this, but for now we have a simple menu with settings, which does nothing. Here may be exit button implemented
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {     // result of the activity
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {                                                          // in our case speech recognition is chosen all the time since RESULT_SPEECH=1
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {                          // the speech was recognised and is not silence
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    RecognisedText.setText("");
                    RecognisedText.setText(text.get(0));

                    if (RecognisedText.getText().toString().startsWith(test)) {

                        Intent intent = new Intent("android.intent.action.MAIN");
                        intent.setComponent(new ComponentName("com.android.mms", "com.android.mms.ui.ConversationList"));
                        startActivity(intent);
                        /*
                        Intent test = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        startActivityForResult(test, RESULT_SPEECH2);
                        */
                    }

                    if (RecognisedText.getText().toString().startsWith(choiceCall)){
                        isCalling = true;
                        readContacts(text);
                        makeCall(number);

                    }

                    if (RecognisedText.getText().toString().startsWith(choiceMSG))
                    {
                        isCalling = false;
                        readContacts(text);
                        String msg = text.get(0);
                        msg = method.checkPolish(msg);
                        startActivity(method.sendSMS(msg, number));
                    }
                    if (RecognisedText.getText().toString().startsWith(choiceBrowser))
                    {
                        String page = text.get(0);
                        startActivity(method.browse(page));
                    }
                    if (RecognisedText.getText().toString().contains(choiceAlarm))
                    {
                        int hour = 7;
                        int minute = 15;
                        startActivity(method.alarm(hour, minute));
                    }
                    if (RecognisedText.getText().toString().startsWith(choiceMap))
                    {
                        String addr = text.get(0);
                        startActivity(method.maps(addr));
                    }
                /*        switch (decider) {  //skurwysyn nie działa po polsku. znaczy, czasem bangla, ale zazwyczaj się crashuje <-- by włączyc polski odkomentuj intent.putextra -->
                            case "zadzwoń":
                                readContacts(nameOfContact);
                                makeCall(number);
                             break;

                         case "napisz":
                             RecognisedText.setText("LOG : " + "\n" + "MADAFAKA");
                             //write sms
                             break;

                         default:
                             RecognisedText.setText("LOG : " + "\n" + "default");
                             break;
                        } */
                }
            }
                break;
        }

    }

    public String readContacts(ArrayList text) {
        Map<String, String> book = new HashMap<String, String>();
        nameOfContact = (String) text.get(0);
        text.clear();
        text.add(nameOfContact);
        nameOfContact = TextUtils.join(" ", text);
        if(isCalling) {
            nameOfContact = nameOfContact.substring(11);
        }
        else{
            nameOfContact = nameOfContact.substring(9);
        }
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            book.put(name, phone);
                        }
                        pCur.close();
                    }
                }
            }
            number = book.get(nameOfContact);

            //RecognisedText.setText(number + " view from readContacts field" + "\n"); //FOR LOGGING

        return number;
        }

    public void makeCall (String number) {
        if(number != null) {
                String numberToCall = "tel:" + number.trim();
                Uri Call = Uri.parse(numberToCall);
                Intent callIntent = new Intent(Intent.ACTION_CALL, Call);
                startActivity(callIntent);
            }
        else {
            Toast t = Toast.makeText(getApplicationContext(),
                    "No such contact in your phonebook", // later can add voice saying this and asking if you can add contact
                    Toast.LENGTH_SHORT);
                t.show();
            }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                               // what happens after clicking the menu's option (settings)
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_scenarios:

                about.AboutScenarios();

                return true;

            case R.id.action_settings:

                //later on

                return true;

            case R.id.action_about:

                about.AboutText();

                return true;

            case R.id.action_exit:

                startActivity(method.hideApp());

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}