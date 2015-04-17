package com.abhi.zillowusc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ZestimateFragment extends Fragment
{
	public ZestimateFragment()
	{
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View rootView = inflater.inflate( R.layout.fragment_zest, container, false );
		return rootView;
	}
}
