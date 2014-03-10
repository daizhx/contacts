package daizhx.example.contactslist;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ContactsFragment extends Fragment implements
	LoaderCallbacks<Cursor>, OnItemClickListener{

	ListView mContactsList;
	private final static String[] FROM_COLUMNS={
		Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				Contacts.DISPLAY_NAME_PRIMARY :
				Contacts.DISPLAY_NAME
	};
	private final static int[] TO_IDS={
		android.R.id.text1
	};
	long mContactId;
	String mContactKey;
	Uri mContactUri;
	private SimpleCursorAdapter mCursorAdapter;
	
	private static final String[] PROJECTION={
		Contacts._ID,
		Contacts.LOOKUP_KEY,
		Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				Contacts.DISPLAY_NAME_PRIMARY :
				Contacts.DISPLAY_NAME
	};
	private static final int CONTACT_ID_INDEX = 0;
	private static final int LOOKUP_KEY_INDEX = 1;
	private static final String SELECTION=
		(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME) + "<>''";
				//+ " AND " + Contacts.IN_VISIBLE_GROUP + "=1";
	private String mSearchString;
	private String[] mSelectionArgs={mSearchString};
	private static final int CONTACTS_QUERY_ID = 1;
	private final static String SORT_ORDER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
			Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME;
	
	@Override
	public void onItemClick(AdapterView<?> parent, View item, int position, long rowId) {
		// TODO Auto-generated method stub
		Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
		cursor.moveToPosition(position);
		mContactId = cursor.getLong(CONTACT_ID_INDEX);
		mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
		mContactUri = Contacts.getLookupUri(mContactId, mContactKey);
		int count = cursor.getColumnCount();
		int index = cursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY);
		String primaryName = cursor.getString(index);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		if(id == CONTACT_ID_INDEX){
			Uri contentUri;
			if(mSearchString == null){
				contentUri = Contacts.CONTENT_URI;
			}else{
				contentUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(mSearchString));
			}
			return new CursorLoader(getActivity(),
					contentUri, PROJECTION, SELECTION, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		// TODO Auto-generated method stub
		if(data.moveToFirst()){
			int id = data.getInt(CONTACT_ID_INDEX);
			String key = data.getString(LOOKUP_KEY_INDEX);
		}
		mCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mCursorAdapter.swapCursor(null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		mContactsList = (ListView)getActivity().findViewById(getId());
		mCursorAdapter = new SimpleCursorAdapter(
				getActivity(), 
				R.layout.contacts_list_item, null, 
				FROM_COLUMNS, TO_IDS, 0);
		mContactsList.setAdapter(mCursorAdapter);

		//initializes the loader
		getLoaderManager().initLoader(0, null, this);
		mContactsList.setOnItemClickListener(this);
		super.onActivityCreated(savedInstanceState);
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.contacts_list_view, container, false);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.activity_main, menu);
		SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchMenu = menu.findItem(R.id.menu_search);
		SearchView searchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		//searchView.setIconified(true);
		searchView.setOnQueryTextListener(new OnQueryTextListener(){

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				String newFilter = newText.isEmpty() ? null : newText;
				if(newFilter == null){
					return true;
				}
				mSearchString = newFilter;
				
				getLoaderManager().restartLoader(CONTACT_ID_INDEX, null, ContactsFragment.this);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return true;
			}
			
		});
		searchMenu.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				// TODO Auto-generated method stub
				mSearchString = null;
				getLoaderManager().restartLoader(CONTACT_ID_INDEX, null, ContactsFragment.this);
				return true;
			}
		});
		
	}
	
	
	
}
