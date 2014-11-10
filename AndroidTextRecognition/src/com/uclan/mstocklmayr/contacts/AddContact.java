package com.uclan.mstocklmayr.contacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.uclan.mstocklmayr.CaptureActivity;
import com.uclan.mstocklmayr.R;

import java.util.ArrayList;

public class AddContact extends Activity implements AdapterView.OnItemSelectedListener {
    private String path;
    private String text;


    @Override
    public void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.add_contact);

        Bundle extras = this.getIntent().getExtras();
        this.path = extras.getString(CaptureActivity.FILE_PATH);
        this.text = extras.getString(CaptureActivity.TEXT_RESULT);


        // Creating a button click listener for the "Add Contact" button
        OnClickListener addClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Getting reference to Name EditText
				EditText firstName = (EditText) findViewById(R.id.firstName);
                EditText lastName = (EditText) findViewById(R.id.lastName);

				// Getting reference to Mobile EditText
				EditText mobilePhone = (EditText) findViewById(R.id.mobilePhone);

				// Getting reference to HomePhone EditText
				EditText privatePhone = (EditText) findViewById(R.id.privatePhone);

				// Getting reference to HomeEmail EditText
				EditText homeEmail = (EditText) findViewById(R.id.et_home_email);

				// Getting reference to WorkEmail EditText
				EditText workEmail = (EditText) findViewById(R.id.privateEmail);

                EditText address = (EditText) findViewById(R.id.address);
                EditText company = (EditText) findViewById(R.id.company);


				ArrayList<ContentProviderOperation> ops =
				          new ArrayList<ContentProviderOperation>();


				int rawContactID = ops.size();

				// Adding insert operation to operations list
				// to insert a new raw contact in the table ContactsContract.RawContacts
				ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
						.withValue(RawContacts.ACCOUNT_TYPE, null)
						.withValue(RawContacts.ACCOUNT_NAME, null)
						.build());

				// Adding insert operation to operations list
				// to insert first and last name in the table ContactsContract.Data
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.DISPLAY_NAME, firstName.getText().toString()+" "+lastName.getText().toString())
                        .build());

				// Adding insert operation to operations list
				// to insert Mobile Number in the table ContactsContract.Data
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.NUMBER, mobilePhone.getText().toString())
                        .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                        .build());

				// Adding insert operation to operations list
				// to  insert Home Phone Number in the table ContactsContract.Data
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.NUMBER, privatePhone.getText().toString())
                        .withValue(Phone.TYPE, Phone.TYPE_HOME)
                        .build());

				// Adding insert operation to operations list
				// to insert Home Email in the table ContactsContract.Data
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.Intents.Insert.COMPANY)
                        .withValue(Email.ADDRESS, homeEmail.getText().toString())
                        .withValue(Email.TYPE, Email.TYPE_HOME)
                        .build());

				// Adding insert operation to operations list
				// to insert Work Email in the table ContactsContract.Data
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                        .withValue(Email.ADDRESS, workEmail.getText().toString())
                        .withValue(Email.TYPE, Email.TYPE_WORK)
                        .build());

                // Adding insert operation to operations list
                // to insert address in the table ContactsContract.Data
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getText().toString())
                        .build());

                // Adding insert operation to operations list
                // to insert company in the table ContactsContract.Data
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(CommonDataKinds.Organization.COMPANY, company.getText().toString())
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

                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
			}
		};
		
		
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
		
        
        // Getting reference to "Add Contact" button
        Button btnAdd = (Button) findViewById(R.id.btn_add);


        // Setting click listener for the "Add Contact" button
        btnAdd.setOnClickListener(addClickListener);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.contactFields, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        Spinner firstNameSpinner = (Spinner) findViewById(R.id.firstNameSpinner);
        firstNameSpinner.setAdapter(adapter);
        firstNameSpinner.setOnItemSelectedListener(this);

        Spinner lastNameSpinner = (Spinner) findViewById(R.id.lastNameSpinner);
        lastNameSpinner.setAdapter(adapter);
        lastNameSpinner.setSelection(1);
        lastNameSpinner.setOnItemSelectedListener(this);

        Spinner mobilePhoneSpinner = (Spinner) findViewById(R.id.mobilePhoneSpinner);
        mobilePhoneSpinner.setAdapter(adapter);
        mobilePhoneSpinner.setSelection(2);
        mobilePhoneSpinner.setOnItemSelectedListener(this);

        Spinner privatePhoneSpinner = (Spinner) findViewById(R.id.privatePhoneSpinner);
        privatePhoneSpinner.setAdapter(adapter);
        privatePhoneSpinner.setSelection(3);
        privatePhoneSpinner.setOnItemSelectedListener(this);

        Spinner privateEmailSpinner = (Spinner) findViewById(R.id.privateEmailSpinner);
        privateEmailSpinner.setAdapter(adapter);
        privateEmailSpinner.setSelection(5);
        privateEmailSpinner.setOnItemSelectedListener(this);

        Spinner workEmailSpinner = (Spinner) findViewById(R.id.workEmailSpinner);
        workEmailSpinner.setAdapter(adapter);
        workEmailSpinner.setSelection(4);
        workEmailSpinner.setOnItemSelectedListener(this);

        Spinner address = (Spinner) findViewById(R.id.addressSpinner);
        address.setAdapter(adapter);
        address.setSelection(7);
        address.setOnItemSelectedListener(this);

        Spinner company = (Spinner) findViewById(R.id.companySpinner);
        company.setAdapter(adapter);
        company.setSelection(6);
        company.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(view.getContext(),"item clicked in: "+view.getId(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}