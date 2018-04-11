package com.example.vijay.androidlabs;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class MessageDetails extends Activity
{
    private FragmentTransaction fragmentTransaction;
    private MessageFragment messageFragment;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        Bundle bundle = getIntent().getExtras();

        //Adding the fragment
        manager = getFragmentManager();
        messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);
        fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout,messageFragment);
        fragmentTransaction.commit();
    }


}

