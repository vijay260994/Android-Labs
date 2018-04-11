package com.example.vijay.androidlabs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MessageFragment extends Fragment
{
    private TextView textMessage,textId;
    private ChatDatabaseHelper chatdb;
    protected static final String ACTIVITY_NAME = "MessageFragment";
    private Button btnDelete;
    private boolean isTablet;
    private ChatWindow.DeleteListener onDeleteListener = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_message_fragment,container,false);

        return view;
    }

}
