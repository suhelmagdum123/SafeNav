package de.tubs.ibr.dtn.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import java.util.Random;

import de.tubs.ibr.dtn.chat.service.ChatService;
import de.tubs.ibr.dtn.chat.service.Utils;

public class RosterFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    boolean mDualPane;
    Long mBuddyId = null;
	
    private final static int LOADER_ID = 1;
    
	@SuppressWarnings("unused")
	private final String TAG = "RosterFragment";
	
	private RosterAdapter mAdapter = null;

	private ChatService mService = null;
	private boolean mBound = false;
	
	private MenuItem mMenuShowOffline = null;
    // Create the Handler object (on the main thread by default)
    final Handler handler = new Handler();
    Random rand = new Random();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d(TAG, "Handlers :: Called on main thread inside the RosterFragment ");
            mService.startDebug(ChatService.Debug.SEND_PRESENCE);

            handler.postDelayed(runnableCode, 10000);
        }
    };
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((ChatService.LocalBinder)service).getService();
			
			switch (mService.getServiceError()) {
			case NO_ERROR:
				break;
				
			case SERVICE_NOT_FOUND:
				Utils.showInstallServiceDialog(getActivity());
				break;
				
			case PERMISSION_NOT_GRANTED:
				Utils.showReinstallDialog(getActivity());
				break;
			}
			
			// load roster
			getLoaderManager().initLoader(LOADER_ID, null, RosterFragment.this);
		}

		public void onServiceDisconnected(ComponentName name) {
			getLoaderManager().destroyLoader(LOADER_ID);
			mService = null;
		}
	};
	
    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.buddy_menu, menu);
        Log.i(TAG,"onCreateOptionsMenu");
	    
	    if (0 != (getActivity().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
	    	inflater.inflate(R.menu.debug_menu, menu);
	    }
	    
	    mMenuShowOffline = menu.findItem(R.id.itemHideOffline);
	    if(getActivity()!=null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			mMenuShowOffline.setChecked(prefs.getBoolean("hideOffline", false));

// Start the initial runnable task by posting through the handler
			handler.post(runnableCode);
		}
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Log.i(TAG,"onOptionsItemSelected");
		switch (item.getItemId()) {
	    case R.id.itemPreferences:
	    {
			// Launch Preference activity
			Intent i = new Intent(getActivity(), Preferences.class);
			startActivity(i);
	        return true;
	    }
	    
	    case R.id.itemHideOffline:
	    {
	    	if(getActivity()!=null) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				boolean previousState = prefs.getBoolean("hideOffline", false);
				mMenuShowOffline.setChecked(!previousState);
				prefs.edit().putBoolean("hideOffline", !previousState).commit();
				return true;
			}
	    }
	    
	    case R.id.itemDebugNotification:
	    	if (mService != null)
				mService.startDebug(ChatService.Debug.NOTIFICATION);
	    	return true;
	    	
	    case R.id.itemDebugBuddyAdd:
	    	if (mService != null)
				mService.startDebug(ChatService.Debug.BUDDY_ADD);
	    	return true;
	    	
	    case R.id.itemDebugSendPresence:
            if (mService != null)
                mService.startDebug(ChatService.Debug.SEND_PRESENCE);
	        return true;
	        
	    case R.id.itemDebugUnregister:
            if (mService != null)
                mService.startDebug(ChatService.Debug.UNREGISTER);
            getActivity().finish();
	        return true;
    
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG,"onActivityCreated");
		// enable options menu
		setHasOptionsMenu(true);
		
		// enable context menu
		registerForContextMenu(getListView());
		
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new RosterAdapter(getActivity(), null, new RosterAdapter.ColumnsMap());
        setListAdapter(mAdapter);
		
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View messagesFrame = getActivity().findViewById(R.id.messages);
        mDualPane = messagesFrame != null && messagesFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
        	mBuddyId = savedInstanceState.getLong(ChatService.EXTRA_BUDDY_ID, -1);
        	if (mBuddyId.equals(-1L)) mBuddyId = null;
        } else {
        	Intent i = getActivity().getIntent();
        	if (i != null)
        	{
        		Bundle extras = i.getExtras();
            	if (extras != null) {
            		mBuddyId = extras.getLong(ChatService.EXTRA_BUDDY_ID, -1);
            		if (mBuddyId.equals(-1L)) mBuddyId = null;
            	}
        	}
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        
        // Make sure our UI is in the correct state.
        if (mBuddyId != null) showMessages(mBuddyId);

        // Start out with a progress indicator.
        setListShown(false);
	}
	  
    public void showMessages(Long buddyId) {
    	mBuddyId = buddyId;
    	
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
    		if (mAdapter != null)
    			mAdapter.setSelected(buddyId);
    		
            // Check what fragment is currently shown, replace if needed.
            MessageFragment messages = (MessageFragment)
                    getFragmentManager().findFragmentById(R.id.messages);
            if (messages == null || messages.getBuddyId() != buddyId) {
                // Make new fragment to show this selection.
            	messages = MessageFragment.newInstance(buddyId);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.messages, messages);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), MessageActivity.class);
            intent.putExtra(ChatService.EXTRA_BUDDY_ID, buddyId);
            startActivity(intent);
        }
    }
    
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mBuddyId != null)
			outState.putLong(ChatService.EXTRA_BUDDY_ID, mBuddyId);
		else
			outState.remove(ChatService.EXTRA_BUDDY_ID);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// get buddy of position
		RosterItem ritem = (RosterItem)v;
		if (ritem != null)
			showMessages(ritem.getBuddyId());
	}
    
	@Override
	public void onResume() {
		super.onResume();
		
		// Establish a connection with the service.  We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		if (!mBound) {
			getActivity().bindService(new Intent(getActivity(), ChatService.class), mConnection, Context.BIND_AUTO_CREATE);
			mBound = true;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// unbind from service
		if (mBound) {
			getActivity().unbindService(mConnection);
			getLoaderManager().destroyLoader(LOADER_ID);
			mBound = false;
		}
		
		super.onDestroy();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.buddycontext_menu, menu);
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		RosterItem ritem = (RosterItem)info.targetView;
		
		if (ritem.isPinned()) {
			menu.removeItem(R.id.itemPin);
		} else {
			menu.removeItem(R.id.itemUnpin);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		if (info.targetView instanceof RosterItem) {
			RosterItem ritem = (RosterItem)info.targetView;

			switch (item.getItemId())
			{
			case R.id.itemDelete:
				if (mService != null) {
					mService.getRoster().removeBuddy(ritem.getBuddyId());
				}
				return true;
			case R.id.itemPin:
				if (mService != null) {
					mService.getRoster().setPinned(ritem.getBuddyId(), true);
				}
				return true;
			case R.id.itemUnpin:
				if (mService != null) {
					mService.getRoster().setPinned(ritem.getBuddyId(), false);
				}
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new RosterLoader(getActivity(), mService);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
        
        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
	}
}
