package xyz.ujjwalarora.contactsprovider;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.ContactsContract;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.URI;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


public class ContactsList extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    // Global vars
    private final static int READ_CONTACTS_PERMISSION_CODE = 0;

    ListView contactsList;

    SimpleCursorAdapter cursorAdapter;

    private final static String[] FROM_COLS = {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY };

    private final static int[] TO_IDS = {
            R.id.list_item_text
    };

    private final static String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;


    // Empty public constructor, required by the system
    public ContactsList() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the list view from view list of parent activity
        contactsList = getActivity().findViewById(R.id.list_view_contacts);
        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.contacts_list_item, null, FROM_COLS, TO_IDS, 0);
        contactsList.setAdapter(cursorAdapter);
        contactsList.setOnItemClickListener(this);

        // request permission if we don't have it
        if(checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_CODE);
        } else {
            // Init Loader
            initLoader();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_CONTACTS_PERMISSION_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Init Loader
                    initLoader();
                } else {
                    System.exit(0);
                }
            }
        }
    }

    public void initLoader() {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Cursor cursor = ((CursorAdapter) adapterView.getAdapter()).getCursor();
        cursor.moveToPosition(i);

        long contactID = cursor.getLong(CONTACT_ID_INDEX);
        String lookupKey = cursor.getString(LOOKUP_KEY_INDEX);
        Uri contactURI = ContactsContract.Contacts.getLookupUri(contactID, lookupKey);

        Toast.makeText(getActivity(), "URI" + contactURI.toString(), Toast.LENGTH_SHORT).show();
    }
}
