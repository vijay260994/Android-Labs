package com.example.vijay.androidlabs;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class ChatWindow extends Activity
{
    protected static final String ACTIVITY_NAME = "ChatWindow";
    ArrayList<String> listArray = new ArrayList<>();

    ChatDatabaseHelper chatdb;
    SQLiteDatabase db;
    ContentValues cValues;
    Cursor cursor;
    ChatAdapter msgAdapter;
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


        cursor = db.query(false, chatdb.TABLE_NAME, new String[]{chatdb.KEY_ID, chatdb.KEY_MESSAGE},
                null, null, null, null, null, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            listArray.add(cursor.getString(cursor.getColumnIndex(chatdb.KEY_MESSAGE)));
            Log.i(ACTIVITY_NAME, "Your Message " + cursor.getString(cursor.getColumnIndex(chatdb.KEY_MESSAGE)));
            Log.i("Number of Column ",cursor.getColumnCount()+"");
            Log.i(ACTIVITY_NAME,"Column Name" + cursor.getColumnName(cursor.getColumnIndex(chatdb.KEY_MESSAGE)));
            cursor.moveToNext();
        }
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listArray.add(edit.getText().toString());
                cValues.put(chatdb.KEY_MESSAGE, edit.getText().toString());
                db.insert(chatdb.TABLE_NAME, null, cValues);
                edit.setText("");
            }
        });
        list.setAdapter(msgAdapter);
        msgAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    private class ChatAdapter extends ArrayAdapter<String>
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

        public long getId(int position)
        {
            return position;
        }
    }


}