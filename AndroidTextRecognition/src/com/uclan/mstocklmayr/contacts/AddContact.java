package com.uclan.mstocklmayr.contacts;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.uclan.mstocklmayr.CaptureActivity;
import com.uclan.mstocklmayr.R;
import com.uclan.mstocklmayr.utils.ContactTypes;
import com.uclan.mstocklmayr.utils.RandomId;
import com.uclan.mstocklmayr.utils.TextSplitter;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContact extends Activity implements AdapterView.OnItemSelectedListener, OnClickListener {

    private int lastItemId;
    private int lastInputId;
    private Map<String, String> values;
    private Map<String, Integer> finalPairs;
    RandomId randomId = new RandomId();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        ActionBar ab = getActionBar();

        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayUseLogoEnabled(false);
        ab.setDisplayShowCustomEnabled(true);

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.finish_button,null);
        linearLayout.setOnClickListener(this);
        ab.setCustomView(linearLayout);
        ab.show();

        this.finalPairs = new HashMap<String, Integer>();

        //get result map from capture activity
        this.values = CaptureActivity.textResultMap;

        this.lastItemId = R.id.tvSpacer;
        this.lastInputId = R.id.etName;

        //no mobile, hide relating text/input
        EditText etMobile = (EditText) findViewById(R.id.etMobilePhone);
        if(!this.values.containsKey(ContactTypes.PHONE.toString())){
            TextView mobile = (TextView) findViewById(R.id.tvMobilePhone);
            mobile.setVisibility(View.GONE);
            etMobile.setVisibility(View.GONE);
        }else{
            etMobile.setText(this.values.get(ContactTypes.PHONE.toString()));
            registerForContextMenu(findViewById(R.id.tvMobilePhone));
            this.finalPairs.put(ContactTypes.PHONE.toString(),etMobile.getId());
            this.lastInputId = etMobile.getId();
        }

        EditText etEmail = (EditText) findViewById(R.id.etPrivateEmail);
        if(!this.values.containsKey(ContactTypes.EMAIL.toString())){
            TextView email = (TextView) findViewById(R.id.tvPrivateEmail);
            email.setVisibility(View.GONE);
            etEmail.setVisibility(View.GONE);
        }else{
            etEmail.setText(this.values.get(ContactTypes.EMAIL.toString()));
            registerForContextMenu(findViewById(R.id.tvPrivateEmail));
            this.finalPairs.put(ContactTypes.EMAIL.toString(),etEmail.getId());
            this.lastInputId = etEmail.getId();
        }

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.addContactLayout);

        for(Map.Entry<String, String> entry : this.values.entrySet()){
            if(entry.getKey().equalsIgnoreCase(ContactTypes.NAME.toString())){
                EditText name = (EditText)findViewById(R.id.etName);
                name.setText(entry.getValue());
                this.finalPairs.put(ContactTypes.NAME.toString(), name.getId());
                continue;
            }
            if(!entry.getKey().equalsIgnoreCase(ContactTypes.EMAIL.toString()) && !entry.getKey().equalsIgnoreCase(ContactTypes.PHONE.toString())){
                View viewToAdd = addUnknownItem(this, entry.getValue(), randomId.getIdFromKey(entry.getKey()), this.lastItemId);
                registerForContextMenu(viewToAdd);
                this.lastItemId = viewToAdd.getId();
                relativeLayout.addView(viewToAdd);
            }
        }

        registerForContextMenu(findViewById(R.id.tvName));

        // Creating a button click listener for the "Add Contact" button
        OnClickListener contactsClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Creating an intent to open Android's Contacts List
                Intent contacts = new Intent(Intent.ACTION_VIEW,ContactsContract.Contacts.CONTENT_URI);

                // Starting the activity
                startActivity(contacts);
            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(view.getContext(),"item clicked in: "+view.getId(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("Context Menu");
        switch (v.getId()){
            case R.id.tvName:
                menu.add(0,v.getId(),0,"Switch first name and last name");
                break;
            case R.id.tvMobilePhone:
            case R.id.tvPrivateEmail:
            default:
                menu.add(0,v.getId(),0, R.string.mobilePhone);
                menu.add(0,v.getId(),0, R.string.privateEmail);
                menu.add(0,v.getId(),0, R.string.privateEmail);
                menu.add(0,v.getId(),0, R.string.privateNumber);
                menu.add(0,v.getId(),0, R.string.company);
                menu.add(0,v.getId(),0, R.string.address);
                menu.add(0,v.getId(),0, R.string.city);
                menu.add(0,v.getId(),0, R.string.country);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        Toast.makeText(AddContact.this, "title: "+item.getTitle()+" id: "+item.getItemId(),Toast.LENGTH_SHORT).show();
        String text = null;
        View toRemove = findViewById(item.getItemId());
        if(toRemove instanceof TextView) {
            text = ((TextView) toRemove).getText().toString();
        }
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.addContactLayout);
        relativeLayout.removeView(toRemove);
        int spacerBelowId = createInputPair(AddContact.this,this.lastInputId,item.getItemId(),item.getTitle().toString(),text);
        this.finalPairs.put(ContactTypes.valueOf(item.getTitle().toString()).toString(),spacerBelowId);
        int spacerId = realignSpacer(spacerBelowId);
        realignOthers(spacerId,text);
        return true;
    }

    //reset the below id of the first following element
    private void realignOthers(int spacerId, String value) {
        //remove the "OTHER" entry from the map and rebuild the GUI
        String key = null;
        for(Map.Entry<String, String> entry : this.values.entrySet()){
            if(entry.getValue().equalsIgnoreCase(value)){
                key = entry.getKey();
            }
        }
        this.values.remove(key);

        List<View> viewsToRemove = new ArrayList<View>();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.addContactLayout);
        for(int i=0;i<relativeLayout.getChildCount();i++){
            View view = relativeLayout.getChildAt(i);
            if(view instanceof TextView){
                for(Map.Entry<String, String> entry : this.values.entrySet()){
                    if(!entry.getKey().equalsIgnoreCase(ContactTypes.EMAIL.toString())
                            && !entry.getKey().equalsIgnoreCase(ContactTypes.NAME.toString())
                            && !entry.getKey().equalsIgnoreCase(ContactTypes.PHONE.toString())
                            && entry.getValue().equalsIgnoreCase(((TextView) view).getText().toString())){
                        viewsToRemove.add(view);
                    }
                }
            }
        }
        for(View v : viewsToRemove){
            relativeLayout.removeView(v);
        }

        for(Map.Entry<String,Integer> entry : randomId.getEntries()){
           View v = findViewById(entry.getValue());
           if(v != null){
            relativeLayout.removeView(v);
           }else{
               break;
           }
        }

        this.lastItemId = spacerId;

        for(Map.Entry<String, String> entry : this.values.entrySet()){
            if(entry.getKey().equalsIgnoreCase(ContactTypes.NAME.toString())){
                continue;
            }
            if(!entry.getKey().equalsIgnoreCase(ContactTypes.EMAIL.toString()) && !entry.getKey().equalsIgnoreCase(ContactTypes.PHONE.toString())){
                View viewToAdd = addUnknownItem(this, entry.getValue(), randomId.getIdFromKey(entry.getKey()), this.lastItemId);
                registerForContextMenu(viewToAdd);
                this.lastItemId = viewToAdd.getId();
                relativeLayout.addView(viewToAdd);
            }
        }

    }



    private int createInputPair(Context ctx, int belowId, int id, String header, String text){
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.addContactLayout);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv = new TextView(ctx);
        tv.setBackgroundResource(R.drawable.textview_border);
        tv.setId(id);
        tv.setText(header);

        DisplayMetrics metrics = AddContact.this.getResources().getDisplayMetrics();
        float dp = 7f;
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);

        tv.setPadding(pixels, 0, 0, 0);

        fpixels = metrics.density * 22f;
        pixels = (int) (fpixels + 0.5f);

        tv.setTextSize(pixels);
        params1.addRule(RelativeLayout.BELOW, belowId);

        EditText et = new EditText(ctx);
        et.setId(id+1);
        et.setText(text);
        params2.addRule(RelativeLayout.BELOW, id);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        relativeLayout.addView(tv,params1);
        relativeLayout.addView(et,params2);

        this.lastInputId = et.getId();

        return et.getId();
    }

    private View addUnknownItem(Context ctx, String text, int id, int belowId){
        TextView tv = new TextView(ctx);
        //id must not be unique - http://stackoverflow.com/questions/8460680/how-can-i-assign-an-id-to-a-view-programmatically
        tv.setId(id);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tv.setTextSize(22);
        tv.setPadding(7,0,0,0);
        params.addRule(RelativeLayout.BELOW, belowId);
        tv.setText(text);
        tv.setLayoutParams(params);
        return tv;
    }

    private int realignSpacer(int belowId){
        TextView spacer = (TextView) findViewById(R.id.tvSpacer);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, belowId);
        spacer.setLayoutParams(params);
        return spacer.getId();
    }

    @Override
    public void onClick(View v) {
        // Getting reference to Name EditText
        EditText name = (EditText) findViewById(finalPairs.get(ContactTypes.NAME.toString()));

        //TODO: check if the change first/lastName button has been clicked
        String[] nameParts = TextSplitter.splitName(true, name.getText().toString());
        String firstName = nameParts[0];
        String lastName = null;
        if(nameParts.length > 1)
            lastName = nameParts[1];


        // Getting reference to Mobile EditText
        EditText mobilePhone = (EditText) findViewById(finalPairs.get(ContactTypes.PHONE.toString()));

        // Getting reference to HomePhone EditText
        EditText privatePhone = (EditText) findViewById(finalPairs.get(ContactTypes.PRIVATE_PHONE.toString()));

        // Getting reference to HomeEmail EditText
        EditText homeEmail = (EditText) findViewById(finalPairs.get(ContactTypes.EMAIL.toString()));

        // Getting reference to WorkEmail EditText
        EditText workEmail = (EditText) findViewById(finalPairs.get(ContactTypes.WORK_EMAIL.toString()));

        EditText address = (EditText) findViewById(finalPairs.get(ContactTypes.ADDRESS.toString()));
        EditText company = (EditText) findViewById(finalPairs.get(ContactTypes.COMPANY.toString()));


        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();


        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert first and last name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, firstName+" "+lastName == null ? "":lastName)
                .build());

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobilePhone.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // Adding insert operation to operations list
        // to  insert Home Phone Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, privatePhone.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        // Adding insert operation to operations list
        // to insert Home Email in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.Intents.Insert.COMPANY)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, homeEmail.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                .build());

        // Adding insert operation to operations list
        // to insert Work Email in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, workEmail.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        // Adding insert operation to operations list
        // to insert address in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getText().toString())
                .build());

        // Adding insert operation to operations list
        // to insert company in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company.getText().toString())
                .build());

        try{
            // Executing all the insert operations as a single database transaction
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
        }catch (RemoteException e) {
            e.printStackTrace();
        }catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}