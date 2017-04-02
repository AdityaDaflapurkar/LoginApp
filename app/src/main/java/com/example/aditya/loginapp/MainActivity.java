package com.example.aditya.loginapp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
    private NfcAdapter mNfcAdapter;
    TextView sourceTextView, destinationTextView, messageTextView;
    Button btnMove;
    Spinner MySpinner1, MySpinner2;
    List<String> myList1, myList2;
    private ArrayAdapter<String> myAdapter1, myAdapter2;
    private HashMap<String, Integer> placeToVertex;
    private HashMap<Integer, String> VertexDescription;
    String s;
    boolean scannedNFC = false;
    
    String[] data = new String[1000];
    int dataIndex = 0;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FOR HASHMAP

        placeToVertex = new HashMap<String, Integer>();
        placeToVertex.put("Near Gate_L1", 0);
        placeToVertex.put("Near Gate_L2", 1);
        placeToVertex.put("Near Gate_U1", 2);
        placeToVertex.put("Near Gate_U2", 3);
        placeToVertex.put("Lower Vertex_1", 4);
        placeToVertex.put("Lower Vertex_2", 5);
        placeToVertex.put("Upper Vertex_1",6);
        placeToVertex.put("Upper Vertex_2",7);

        VertexDescription=new HashMap<Integer,String>();
        VertexDescription.put(0,"Lower Left gate of LT2");
        VertexDescription.put(1,"Lower Right gate of LT2");
        VertexDescription.put(2,"Upper Left gate of LT2");
        VertexDescription.put(3,"Upper Right gate of LT2");
        VertexDescription.put(4, "Something_1");
        VertexDescription.put(5, "Something_1");
        VertexDescription.put(6, "Something_1");
        VertexDescription.put(7, "Something_1");
       

        setContentView(R.layout.activity_main);
        btnMove = (Button)findViewById(R.id.btn);
        
        MySpinner2 = (Spinner)findViewById(R.id.myspinner2);
        sourceTextView = (TextView)findViewById(R.id.source);
        destinationTextView = (TextView)findViewById(R.id.destination);
        messageTextView = (TextView)findViewById(R.id.messageBox);
        initList();
       

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter == null)
            Log.d("NFC", "Adapter not suppported");

        resolveIntent(getIntent());
        
        myAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myList2);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MySpinner2.setAdapter(myAdapter2);

        btnMove.setOnClickListener(MoveOnClickListener);
    }

    Button.OnClickListener MoveOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            
            if(!scannedNFC){
                sourceTextView.setText("PLEASE SCAN THE NEAREST NFC TAG");
                return;
            }
            int pos2 = MySpinner2.getSelectedItemPosition();

            if(pos2 != AdapterView.INVALID_POSITION){

                Integer source = placeToVertex.get(s);
               String sd = myList2.get(pos2);
                destinationTextView.setText(sd);
                Integer destination = placeToVertex.get(sd);
                if(source.equals(destination)){
                    messageTextView.setText("CHOOSE A DIFFERENT DESTINATION");
                    return;
                }
                messageTextView.setText((String)VertexDescription.get(pos2));
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("startingVertex", source);
                intent.putExtra("destinationVertex", destination);
                startActivity(intent);
            }
        }};

    void initList(){
        myList1 = new ArrayList<String>();
        myList1.add("Near Gate_L1");
        myList1.add("Near Gate_L2");
        myList1.add("Near Gate_U1");
        myList1.add("Near Gate_U2");
        myList1.add("Lower Vertex_1");
        myList1.add("Lower Vertex_2");
        myList1.add("Upper Vertex_1");
        myList1.add("Upper Vertex_2");

        myList2 = new ArrayList<String>();
        myList2.add("Near Gate_L1");
        myList2.add("Near Gate_L2");
        myList2.add("Near Gate_U1");
        myList2.add("Near Gate_U2");
        myList2.add("Lower Vertex_1");
        myList2.add("Lower Vertex_2");
        myList2.add("Upper Vertex_1");
        myList2.add("Upper Vertex_2");
        
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    void buildTagViews(NdefMessage[] msgs)
    {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        String tagId = new String(msgs[0].getRecords()[0].getType());
        String body = new String(msgs[0].getRecords()[0].getPayload());
        data[dataIndex] = body;
        dataIndex++;
        sourceTextView.setText(body.substring(3));
        s=body.substring(3);
        Log.d("NFC", tagId + "; " + body);
        scannedNFC = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        PendingIntent pendingIntent     = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter[] intentFilters    = { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) };

        mNfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,new String[][]{new String[]{"android.nfc.tech.NfcA"}});
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null)
        {
            try {
                mNfcAdapter.disableForegroundDispatch(this);
            }
            catch (NullPointerException e) {
            }
        }
    }
}
