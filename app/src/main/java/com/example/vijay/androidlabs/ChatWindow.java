package com.example.vijay.androidlabs;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import static com.example.vijay.androidlabs.ChatDatabaseHelper.KEY_ID;
import static com.example.vijay.androidlabs.ChatDatabaseHelper.KEY_MESSAGE;


public class ChatWindow extends Activity
{
    protected static final String ACTIVITY_NAME = "ChatWindow";
    ArrayList<String> listArray = new ArrayList<>();

    ChatDatabaseHelper chatdb;
    SQLiteDatabase db;
    ContentValues cValues;
    Cursor cursor;
    ChatAdapter msgAdapter;

    FragmentManager manager = getFragmentManager();
    FragmentTransaction fragmentTransaction;
    boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        final ListView list = (ListView) findViewById(R.id.listView);
        final EditText edit = (EditText) findViewById(R.id.editText);
        Button btn = (Button) findViewById(R.id.btnSend);

        chatdb = new ChatDatabaseHelper(this);
        db = chatdb.getWritableDatabase();
        cValues = new ContentValues();
        msgAdapter = new ChatAdapter(this);

        FrameLayout frameLayout = findViewById(R.id.frame1);


        if (frameLayout != null) {
            isTablet = true;
            Log.i(ACTIVITY_NAME, "Tablet mode on");
            frameLayout.setMinimumWidth(600);
        }
        else
        {
            isTablet= false;
            Log.i(ACTIVITY_NAME, "Phone mode on");
        }

        cursor = db.query(false, chatdb.TABLE_NAME, new String[]{chatdb.KEY_ID, chatdb.KEY_MESSAGE},
                null, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            listArray.add(cursor.getString(cursor.getColumnIndex(chatdb.KEY_MESSAGE)));
            Log.i(ACTIVITY_NAME, "Your Message " + cursor.getString(cursor.getColumnIndex(chatdb.KEY_MESSAGE)));
            Log.i("Number of Column ", cursor.getColumnCount() + "");
            Log.i(ACTIVITY_NAME, "Column Name" + cursor.getColumnName(cursor.getColumnIndex(chatdb.KEY_MESSAGE)));
            cursor.moveToNext();
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listArray.add(edit.getText().toString());
                cValues.put(chatdb.KEY_MESSAGE, edit.getText().toString());
                db.insert(chatdb.TABLE_NAME, null, cValues);
                edit.setText("");
            }
        });
        list.setAdapter(msgAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (isTablet)
                {
                    Log.e("ChatWindow", "In the Tablet Mode");

                    manager.popBackStackImmediate();
                    fragmentTransaction = manager.beginTransaction();
                    MessageFragment messageFragment = new MessageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isTablet", isTablet);
                    bundle.putLong("textID", id);
                    bundle.putString("textMessage", KEY_MESSAGE);
                    fragmentTransaction.replace(R.id.frame1, messageFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else
                {
                    Log.e("ChatWindow", "In the Phone Mode");

                    Intent intent = new Intent(ChatWindow.this,MessageDetails.class);
                    intent.putExtra(KEY_ID,id);
                    intent.putExtra("IsTablet",isTablet);
                    startActivityForResult(intent,5);
                }
            }
        });
        msgAdapter.notifyDataSetChanged();
    }

    public interface DeleteListener
    {
        public void onDeleted();
    }

    private DeleteListener onDeleteListener = new DeleteListener()
    {
        @Override
        public void onDeleted()
        {
            if(isTablet)
            {
                manager.popBackStackImmediate();
            }
            listArray.clear();
            final SQLiteDatabase db = chatdb.getWritableDatabase();
            final Cursor cursor = db.rawQuery("select * from " + chatdb.TABLE_NAME,null);
            if(cursor.getCount() > 0){
                //cursor.moveToFirst();
                if(cursor.moveToFirst()) {
                    while (!cursor.isAfterLast())
                    {
                        int index = cursor.getColumnIndex(KEY_MESSAGE);
                        String msg = cursor.getString(index);
                        Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + msg);
                        listArray.add(msg);
                        cursor.moveToNext();
                    }
                }
            }
            msgAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==5 && requestCode==10)
        {
            boolean isDelete = data.getBooleanExtra("IsDelete", false);
            if (isDelete)
            {
                onDeleteListener.onDeleted();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(ACTIVITY_NAME,"OnResume");
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        this.finish();
    }

public class ChatAdapter extends ArrayAdapter<String>
    {
        public ChatAdapter(Context ctx)
        {
            super(ctx, 0);
        }

        public int getCount()
        {
            return listArray.size();
        }
        public String getItem(int position)
        {
            return listArray.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;

            if (position % 2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView) result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }

//        public long getId(int position)
//        {
//            return position;
//        }

        public long getItemId(int position)
        {
            final SQLiteDatabase db = chatdb.getWritableDatabase();
            final Cursor cursor = db.rawQuery("select * from " + chatdb.TABLE_NAME, null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToPosition(position);
                int index = cursor.getColumnIndex(KEY_MESSAGE);
                String msg = cursor.getString(index);

                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                return id;
            }
            return 0;
        }
    }

    @Override
    protected void onDestroy()
    {
        chatdb.close();
        super.onDestroy();

    }
}