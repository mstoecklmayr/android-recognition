package com.uclan.mstocklmayr.contacts;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.uclan.mstocklmayr.CaptureActivity;
import com.uclan.mstocklmayr.R;

import java.util.Map;

public class AddContact extends Activity implements AdapterView.OnItemSelectedListener {

    private int lastItemId;
    private Map<String, String> values;

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
        ab.setCustomView(linearLayout);
        ab.show();



        //test, no mobile, hide relating text/input
        TextView mobile = (TextView) findViewById(R.id.tvMobilePhone);
        mobile.setVisibility(View.GONE);
        EditText etMobile = (EditText) findViewById(R.id.etMobilePhone);
        etMobile.setVisibility(View.GONE);


        registerForContextMenu(findViewById(R.id.tvName));
        registerForContextMenu(findViewById(R.id.tvMobilePhone));
        registerForContextMenu(findViewById(R.id.tvPrivateEmail));
        registerForContextMenu(findViewById(R.id.tvSpacer));

        //add new items and register them for the context menu
//        registerForContextMenu(View v);


//        // Creating a button click listener for the "Add Contact" button
//        OnClickListener addClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// Getting reference to Name EditText
//				EditText firstName = (EditText) findViewById(R.id.firstName);
//                EditText lastName = (EditText) findViewById(R.id.lastName);
//
//				// Getting reference to Mobile EditText
//				EditText mobilePhone = (EditText) findViewById(R.id.mobilePhone);
//
//				// Getting reference to HomePhone EditText
//				EditText privatePhone = (EditText) findViewById(R.id.privatePhone);
//
//				// Getting reference to HomeEmail EditText
//				EditText homeEmail = (EditText) findViewById(R.id.et_home_email);
//
//				// Getting reference to WorkEmail EditText
//				EditText workEmail = (EditText) findViewById(R.id.privateEmail);
//
//                EditText address = (EditText) findViewById(R.id.address);
//                EditText company = (EditText) findViewById(R.id.company);
//
//
//				ArrayList<ContentProviderOperation> ops =
//				          new ArrayList<ContentProviderOperation>();
//
//
//				int rawContactID = ops.size();
//
//				// Adding insert operation to operations list
//				// to insert a new raw contact in the table ContactsContract.RawContacts
//				ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//						.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//						.withValue(RawContacts.ACCOUNT_NAME, null)
//						.build());
//
//				// Adding insert operation to operations list
//				// to insert first and last name in the table ContactsContract.Data
//				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
//                        .withValue(StructuredName.DISPLAY_NAME, firstName.getText().toString()+" "+lastName.getText().toString())
//                        .build());
//
//				// Adding insert operation to operations list
//				// to insert Mobile Number in the table ContactsContract.Data
//				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                        .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
//                        .withValue(Phone.NUMBER, mobilePhone.getText().toString())
//                        .withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
//                        .build());
//
//				// Adding insert operation to operations list
//				// to  insert Home Phone Number in the table ContactsContract.Data
//				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                        .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
//                        .withValue(Phone.NUMBER, privatePhone.getText().toString())
//                        .withValue(Phone.TYPE, Phone.TYPE_HOME)
//                        .build());
//
//				// Adding insert operation to operations list
//				// to insert Home Email in the table ContactsContract.Data
//				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.Intents.Insert.COMPANY)
//                        .withValue(Email.ADDRESS, homeEmail.getText().toString())
//                        .withValue(Email.TYPE, Email.TYPE_HOME)
//                        .build());
//
//				// Adding insert operation to operations list
//				// to insert Work Email in the table ContactsContract.Data
//				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                        .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
//                        .withValue(Email.ADDRESS, workEmail.getText().toString())
//                        .withValue(Email.TYPE, Email.TYPE_WORK)
//                        .build());
//
//                // Adding insert operation to operations list
//                // to insert address in the table ContactsContract.Data
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                        .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
//                        .withValue(CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getText().toString())
//                        .build());
//
//                // Adding insert operation to operations list
//                // to insert company in the table ContactsContract.Data
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                        .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//                        .withValue(CommonDataKinds.Organization.COMPANY, company.getText().toString())
//                        .build());
//
//				try{
//					// Executing all the insert operations as a single database transaction
//					getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//					Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
//				}catch (RemoteException e) {
//					e.printStackTrace();
//				}catch (OperationApplicationException e) {
//					e.printStackTrace();
//				}
//			}
//		};


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



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.contactFields, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


//        Spinner firstNameSpinner = (Spinner) findViewById(R.id.firstNameSpinner);
//        firstNameSpinner.setAdapter(adapter);
//        firstNameSpinner.setOnItemSelectedListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {


            //TODO add reprocess feature
//            case R.id.action_reprocess:
//                reprocessImage();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            case R.id.tvMobilePhone:
            case R.id.tvPrivateEmail:
                menu.add(0,v.getId(),0,"Switch first name and last name");
                break;
            default:
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
        Toast.makeText(AddContact.this, item.getTitle(),Toast.LENGTH_SHORT).show();

        return true;
    }

    private View createInputPair(Context ctx, int belowId, int id, String text){
        LinearLayout linearLayout = new LinearLayout(ctx);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv = new TextView(ctx);
        tv.setBackgroundResource(R.drawable.textview_border);
        tv.setId(id);
        tv.setText(text);
        params1.addRule(RelativeLayout.BELOW, belowId);

        EditText et = new EditText(ctx);
        params2.addRule(RelativeLayout.BELOW, id);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);


        linearLayout.addView(tv, params1);
        linearLayout.addView(et, params2);

        return linearLayout;
    }

    private View addUnknownItem(Context ctx, String text, String id){
        TextView tv = new TextView(ctx);
        //id must not be unique - http://stackoverflow.com/questions/8460680/how-can-i-assign-an-id-to-a-view-programmatically
        tv.setId(Integer.getInteger(id));
        tv.setText(text);
        return tv;
    }
}