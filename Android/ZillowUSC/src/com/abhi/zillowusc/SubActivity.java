package com.abhi.zillowusc;

import java.util.Locale;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

@SuppressWarnings( "deprecation" )
public class SubActivity extends Activity implements ActionBar.TabListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private UiLifecycleHelper uiHelper;

	private int index = 0;

	private Facebook facebook;

	private JSONObject obj;

	private JSONParser parser;

	private Builder aboutAlert;

	private ImageView myView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_sub );
		
		setFacebook( savedInstanceState );
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter( getFragmentManager() );

		// Set up the ViewPager with the sections adapter.
		mViewPager = ( ViewPager ) findViewById( R.id.pager );
		mViewPager.setAdapter( mSectionsPagerAdapter );

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected( int position )
			{
				actionBar.setSelectedNavigationItem( position );
			}
		} );

		// For each of the sections in the app, add a tab to the action bar.
		for ( int i = 0; i < mSectionsPagerAdapter.getCount(); i++ )
		{
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab( actionBar.newTab().setText( mSectionsPagerAdapter.getPageTitle( i ) ).setTabListener( this ) );
		}

		aboutAlert = new AlertDialog.Builder( SubActivity.this );
		aboutAlert.setTitle( "About" );
		aboutAlert.setMessage( "This app is developed by Abhijith Nagaraja" );
		aboutAlert.setNegativeButton( "Dismiss", null );

	}

	private void setFacebook( Bundle savedInstanceState )
	{
		uiHelper = new UiLifecycleHelper( this, null );
		uiHelper.onCreate( savedInstanceState );
		facebook = new Facebook( getString( R.string.facebook_app_id ) );
	}

	@Override
	public void onTabSelected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction )
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem( tab.getPosition() );
	}

	@Override
	public void onTabUnselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction )
	{
	}

	@Override
	public void onTabReselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction )
	{
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter( FragmentManager fm )
		{
			super( fm );
		}

		@Override
		public Fragment getItem( int position )
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch ( position )
			{
				case 0:
					return new BasicInfoFragment()
					{
						@Override
						public void onStart()
						{
							// TODO Auto-generated method stub
							super.onStart();
							setContents();
						}
					};
				case 1:
					return new ZestimateFragment()
					{
						@Override
						public void onStart()
						{
							// TODO Auto-generated method stub
							super.onStart();
							myView = ( ImageView ) findViewById( R.id.imageSwitcher1 );
							myView.setScaleType( ImageView.ScaleType.FIT_XY );
							( ( TextView ) findViewById( R.id.zest_header ) ).setText( getHeader( index ) );
							myView.setImageDrawable( getImage( 0 ) );

							TextView sectionLabel = ( TextView ) findViewById( R.id.section_label_zest );
							sectionLabel.setMovementMethod( LinkMovementMethod.getInstance() );
							sectionLabel.setText( Html.fromHtml( "&copy; Zillow, Inc., 2006-2014.<br />Use is subject to <a href = 'http://www.zillow.com/corp/Terms.htm'>Terms of Use</a><br /><a href = 'http://www.zillow.com/zestimate/'>What is a Zestimate?</a>" ) );

							try
							{
								( ( TextView ) findViewById( R.id.zest_addr ) ).setText( ( String ) obj.get( "c4" ) );
							}
							catch ( Exception e )
							{
								e.printStackTrace();
							}
						}
					};
				default:
					return null;
			}

		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle( int position )
		{
			Locale l = Locale.getDefault();
			switch ( position )
			{
				case 0:
					return getString( R.string.title_section1 ).toUpperCase( l );
				case 1:
					return getString( R.string.title_section2 ).toUpperCase( l );
			}
			return null;
		}
	}

	private void setContents()
	{
		String jsonString = getIntent().getStringExtra( "jsonString" );
		parser = new JSONParser();
		try
		{
			obj = ( JSONObject ) parser.parse( jsonString );
			TextView propUrl = ( TextView ) findViewById( R.id.propUrl );
			propUrl.setMovementMethod( LinkMovementMethod.getInstance() );
			propUrl.setText( Html.fromHtml( ( String ) obj.get( "headerLink" ) ) );
			TextView sectionLabel = ( TextView ) findViewById( R.id.section_label );
			sectionLabel.setMovementMethod( LinkMovementMethod.getInstance() );
			sectionLabel.setText( Html.fromHtml( "&copy; Zillow, Inc., 2006-2014.<br />Use is subject to <a href = 'http://www.zillow.com/corp/Terms.htm'>Terms of Use</a><br /><a href = 'http://www.zillow.com/zestimate/'>What is a Zestimate?</a>" ) );
			setData();
			setImages();
			Button shareButton = ( Button ) findViewById( R.id.share_button );
			shareButton.setOnClickListener( new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					onShareButtonClick();
				}

			} );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

	}

	private void setData()
	{
		( ( TextView ) findViewById( R.id.propTypeV ) ).setText( validate( "", ( String ) ( ( JSONObject ) obj.get( "propertyType" ) ).get( "0" ), "" ) );
		( ( TextView ) findViewById( R.id.yearBuiltV ) ).setText( validate( "", ( String ) ( ( JSONObject ) obj.get( "yearBuilt" ) ).get( "0" ), "" ) );
		( ( TextView ) findViewById( R.id.lotSizeV ) ).setText( validate( "", ( String ) ( ( JSONObject ) obj.get( "lotSize" ) ).get( "0" ), " sq. ft." ) );
		( ( TextView ) findViewById( R.id.finAreaV ) ).setText( validate( "", ( String ) ( ( JSONObject ) obj.get( "finishedArea" ) ).get( "0" ), " sq. ft." ) );
		( ( TextView ) findViewById( R.id.bathroomV ) ).setText( validate( "", ( String ) ( ( JSONObject ) obj.get( "bathrooms" ) ).get( "0" ), "" ) );
		( ( TextView ) findViewById( R.id.bedroomV ) ).setText( validate( "", ( String ) ( ( JSONObject ) obj.get( "bedrooms" ) ).get( "0" ), "" ) );
		( ( TextView ) findViewById( R.id.taxAYearV ) ).setText( validate( "", ( String ) ( ( JSONObject ) obj.get( "taxAssesmentYear" ) ).get( "0" ), "" ) );
		( ( TextView ) findViewById( R.id.taxAV ) ).setText( validate( "$", ( String ) obj.get( "taxAssessment" ), "" ) );
		( ( TextView ) findViewById( R.id.lspV ) ).setText( validate( "$", ( String ) obj.get( "lastSoldPrice" ), "" ) );
		( ( TextView ) findViewById( R.id.lsdV ) ).setText( validate( "", ( String ) obj.get( "lastSoldDate" ), "" ) );
		( ( TextView ) findViewById( R.id.propEstV ) ).setText( validate( "$", ( String ) obj.get( "propEst" ), "" ) );
		( ( TextView ) findViewById( R.id.atprV ) ).setText( validate( "", ( String ) obj.get( "range1" ), "" ) );
		( ( TextView ) findViewById( R.id.rentEstV ) ).setText( validate( "$", ( String ) obj.get( "valueAmt" ), "" ) );
		( ( TextView ) findViewById( R.id.propEst ) ).setText( Html.fromHtml( ( String ) obj.get( "valuationString" ) ) );
		( ( TextView ) findViewById( R.id.rentEst ) ).setText( Html.fromHtml( ( String ) obj.get( "valuationString1" ) ) );
		( ( TextView ) findViewById( R.id.atrrV ) ).setText( validate( "", ( String ) obj.get( "range2" ), "" ) );
	}

	private void setImages()
	{
		if ( validate( "", ( String ) obj.get( "change1" ), "" ).equals( "N/A" ) )
		{
			( ( TextView ) findViewById( R.id.ovlChangeV ) ).setText( "N/A" );
		}
		else
		{
			( ( TextView ) findViewById( R.id.ovlChangeV ) ).setText( Html.fromHtml( ( String ) obj.get( "change1" ), new ImageGetter()
			{
				@Override
				public Drawable getDrawable( String source )
				{
					return ZillowMain.image1;
				}
			}, null ) );
		}

		if ( validate( "", ( String ) obj.get( "change2" ), "" ).equals( "N/A" ) )
		{
			( ( TextView ) findViewById( R.id.rentChV ) ).setText( "N/A" );
		}
		else
		{
			( ( TextView ) findViewById( R.id.rentChV ) ).setText( Html.fromHtml( ( String ) obj.get( "change2" ), new ImageGetter()
			{
				@Override
				public Drawable getDrawable( String source )
				{
					return ZillowMain.image2;
				}
			}, null ) );
		}
	}

	private Drawable getImage( int index )
	{
		switch ( index )
		{
			case 0:
				return ZillowMain.c0;

			case 1:
				return ZillowMain.c1;

			case 2:
				return ZillowMain.c2;
		}
		return null;
	}

	private String getHeader( int index )
	{
		switch ( index )
		{
			case 0:
				return "Historical Zestimate for the past 1 year";

			case 1:
				return "Historical Zestimate for the past 5 years";

			case 2:
				return "Historical Zestimate for the past 10 years";
		}
		return null;
	}

	public void next( View view )
	{
		if ( index == 2 )
		{
			index = 0;
		}
		else
		{
			index++;
		}
		myView.setImageDrawable( getImage( index ) );
		( ( TextView ) findViewById( R.id.zest_header ) ).setText( getHeader( index ) );
	}

	public void previous( View view )
	{
		if ( index == 0 )
		{
			index = 2;
		}
		else
		{
			index--;
		}
		myView.setImageDrawable( getImage( index ) );
		( ( TextView ) findViewById( R.id.zest_header ) ).setText( getHeader( index ) );
	}

	private void onShareButtonClick()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( SubActivity.this );

		builder.setTitle( "Post to Facebook" );
		builder.setPositiveButton( "Post Property Details", new DialogInterface.OnClickListener()
		{
			public void onClick( DialogInterface dialog, int which )
			{
				share();
			}
		} );
		builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick( DialogInterface dialog, int which )
			{
				Toast.makeText( SubActivity.this, "Post Cancelled", Toast.LENGTH_SHORT ).show();
			}
		} );
		builder.show();
	}

	private void share()
	{

		Bundle params = getFbBuildParams();

		facebook.dialog( SubActivity.this, "feed", params, new DialogListener()
		{

			@Override
			public void onFacebookError( FacebookError e )
			{
				Toast.makeText( SubActivity.this, "Facebook Error", Toast.LENGTH_SHORT ).show();
			}

			@Override
			public void onError( DialogError e )
			{
				Toast.makeText( SubActivity.this, "Facebook Error", Toast.LENGTH_SHORT ).show();
			}

			@Override
			public void onComplete( Bundle values )
			{
				String postId = values.getString( "post_id" );
				if ( postId == null )
				{
					Toast.makeText( SubActivity.this, "Post Cancelled", Toast.LENGTH_SHORT ).show();
				}
				else
				{
					Toast.makeText( SubActivity.this, "Posted Story, ID " + postId, Toast.LENGTH_SHORT ).show();
				}
			}

			@Override
			public void onCancel()
			{
				Toast.makeText( SubActivity.this, "Post Cancelled", Toast.LENGTH_SHORT ).show();
			}
		} );
	}

	private Bundle getFbBuildParams()
	{
		Bundle params = new Bundle();
		params.putString( "caption", "Property Information from Zillow.com" );
		params.putString( "description", "Last Sold Price: " + validate( "$", ( String ) obj.get( "lastSoldPrice" ), "" ) + ", 30 Days Overall Change: " + validate( ( String ) obj.get( "c5" ) + "$", ( String ) obj.get( "c6" ), "" ) );
		params.putString( "link", ( String ) ( ( JSONObject ) obj.get( "fblink" ) ).get( "0" ) );
		params.putString( "name", ( String ) obj.get( "c4" ) );
		params.putString( "picture", ( String ) ( ( JSONObject ) obj.get( "c1" ) ).get( "0" ) );
		return params;
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		facebook.authorizeCallback( requestCode, resultCode, data );
	}

	private String validate( String preAppend, String validateString, String postAppend )
	{
		if ( validateString == null )
		{
			return "N/A";
		}
		if ( validateString.trim().isEmpty() )
		{
			return "N/A";
		}
		if ( validateString.equals( "0.00" ) )
		{
			return "N/A";
		}
		if ( validateString.equals( "01-Jan-1970" ) )
		{
			return "N/A";
		}
		if ( validateString.startsWith( "<img src" ) )
		{
			if ( validateString.contains( "$0.00" ) )
			{
				return "N/A";
			}
		}
		return preAppend + validateString + postAppend;
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.sub, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if ( id == R.id.action_about_sub )
		{
			aboutAlert.show();
			return true;
		}
		if ( id == R.id.fb_logout )
		{
			Session session = Session.getActiveSession();
			if ( session != null )
			{
				session.close();
				session.closeAndClearTokenInformation();
			}
			Toast.makeText( SubActivity.this, "Facebook Logout Successfull", Toast.LENGTH_SHORT ).show();
			return true;
		}

		if ( id == R.id.exit1 )
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
			alertDialogBuilder.setTitle( "Exit Application?" );
			alertDialogBuilder.setMessage( "Click yes to exit!" );
			alertDialogBuilder.setCancelable( false );
			alertDialogBuilder.setPositiveButton( "Yes", new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int id )
				{
					moveTaskToBack( true );
					android.os.Process.killProcess( android.os.Process.myPid() );
					System.exit( 1 );
				}
			} );

			alertDialogBuilder.setNegativeButton( "No", new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int id )
				{
					dialog.cancel();
				}
			} );
			alertDialogBuilder.show();
			return true;
		}
		return super.onOptionsItemSelected( item );
	}
}
