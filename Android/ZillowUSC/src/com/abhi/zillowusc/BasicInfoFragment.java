package com.abhi.zillowusc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BasicInfoFragment extends Fragment
{
	public BasicInfoFragment()
	{
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View rootView = inflater.inflate( R.layout.fragment_info, container, false );
		return rootView;
	}
}
