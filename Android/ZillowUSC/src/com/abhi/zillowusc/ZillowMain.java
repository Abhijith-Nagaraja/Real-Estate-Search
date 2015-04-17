package com.abhi.zillowusc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ZillowMain extends Activity
{
	private EditText street;
	private TextView streetL;
	private EditText city;
	private TextView cityL;
	private Spinner state;
	private TextView stateL;
	private boolean streetFl = false;
	private boolean cityFl = false;
	private boolean stateFl = false;
	private boolean stateIFl = false;
	public static Drawable image1 = null;
	public static Drawable image2 = null;
	public static Drawable c0 = null;
	public static Drawable c1 = null;
	public static Drawable c2 = null;
	AsyncTask< Void, Void, Void > mTask;
	private int position = 0;
	private TextView tv;
	String jsonString;
	String url = "http://abhi-webtech.elasticbeanstalk.com/index.php?";
	protected String spinnerValue;
	private Builder aboutAlert;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_zillow_main );
		addHandlers();
		findViewById( R.id.loadingPanel ).setVisibility( View.INVISIBLE );
		aboutAlert = new AlertDialog.Builder( ZillowMain.this );
		aboutAlert.setTitle( "About" );
		aboutAlert.setMessage( "This app is developed by Abhijith Nagaraja" );
		aboutAlert.setNegativeButton( "Dismiss", null );
	}

	private void addHandlers()
	{
		street = ( EditText ) findViewById( R.id.editStreet );
		streetL = ( TextView ) findViewById( R.id.e1 );
		street.addTextChangedListener( new TextWatcher()
		{
			@Override
			public void onTextChanged( CharSequence s, int start, int before, int count )
			{
				// do nothing
			}

			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after )
			{
				// do nothing
			}

			@Override
			public void afterTextChanged( Editable s )
			{
				validateStreet();
			}
		} );
		street.requestFocus();
		city = ( EditText ) findViewById( R.id.editCity );
		cityL = ( TextView ) findViewById( R.id.e2 );
		city.addTextChangedListener( new TextWatcher()
		{
			@Override
			public void onTextChanged( CharSequence s, int start, int before, int count )
			{
				// do nothing
			}

			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after )
			{
				// do nothing
			}

			@Override
			public void afterTextChanged( Editable s )
			{
				validateCity();
			}
		} );

		state = ( Spinner ) findViewById( R.id.spinner );
		stateL = ( TextView ) findViewById( R.id.e3 );
		state.setOnItemSelectedListener( new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected( AdapterView< ? > parent, View view, int position, long id )
			{
				spinnerValue = ( String ) parent.getItemAtPosition( position );
				ZillowMain.this.position = position;
				validateState( position );
			}

			@Override
			public void onNothingSelected( AdapterView< ? > parent )
			{
				// Do nothing
			}
		} );
		Button submit = ( Button ) findViewById( R.id.button1 );
		submit.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				validateStreet();
				validateCity();
				validateState( position );
				if ( stateFl && streetFl && cityFl )
				{
					mTask = new AsyncTask< Void, Void, Void >()
					{
						@Override
						protected void onPreExecute()
						{
							super.onPreExecute();
							findViewById( R.id.loadingPanel ).setVisibility( View.VISIBLE );
						}

						@Override
						protected Void doInBackground( Void... params )
						{
							try
							{
								// String url =
								// "http://abhi-webtech.elasticbeanstalk.com/index.php?streetAddress=d&city=f&state=AK";
								jsonString = getJsonFromServer( url + "streetAddress=" + street.getText().toString().replace( " ", "+" ) + "&city=" + city.getText().toString().replace( " ", "+" ) + "&state=" + spinnerValue.replace( " ", "+" ) );
								jsonString = jsonString.substring( 1, jsonString.length() - 2 );
								JSONParser parser = new JSONParser();
								JSONObject obj = ( JSONObject ) parser.parse( jsonString );
								String message = ( String ) obj.get( "message" );
								if ( !( message != null && message.length() > 0 ) )
								{
									setUpDownImages( obj );
									setCharts( obj );
								}
							}
							catch ( IOException e )
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch ( Exception e )
							{
								e.printStackTrace();
							}
							return null;
						}

						@Override
						protected void onPostExecute( Void result )
						{
							super.onPostExecute( result );
							parse();
							findViewById( R.id.loadingPanel ).setVisibility( View.INVISIBLE );
						}

					};
					mTask.execute();
				}
			}
		} );
		tv = ( TextView ) findViewById( R.id.er );

	}

	private void validateStreet()
	{
		if ( street.getText() == null || street.getText().toString().trim().length() <= 0 )
		{
			streetL.setText( "This Field is Required" );
			streetFl = false;
			return;
		}
		else
		{
			streetL.setText( "" );
			streetFl = true;
		}
	}

	private void validateCity()
	{
		if ( city.getText() == null || city.getText().toString().trim().length() <= 0 )
		{
			cityL.setText( "This Field is Required" );
			cityFl = false;
			return;
		}
		else
		{
			cityL.setText( "" );
			cityFl = true;
		}
	}

	private void validateState( int position )
	{
		if ( stateIFl )
		{
			if ( position == 0 )
			{
				stateL.setText( "This Field is Required" );
				stateFl = false;
				return;
			}
			else
			{
				stateL.setText( "" );
				stateFl = true;
			}
		}
		else
		{
			stateIFl = true;
		}
	}

	public static String getJsonFromServer( String link ) throws IOException
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		try
		{
			request.setURI( new URI( link ) );
		}
		catch ( URISyntaxException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpResponse response = client.execute( request );
		StringBuffer sb = readJsonInBuffer( response );
		return sb.toString();
	}

	private static StringBuffer readJsonInBuffer( HttpResponse response ) throws IOException
	{
		BufferedReader in = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

		StringBuffer sb = new StringBuffer( "" );
		String line = "";
		while ( ( line = in.readLine() ) != null )
		{
			sb.append( line );
			break;
		}
		in.close();
		return sb;
	}

	void parse()
	{
		JSONParser parser = new JSONParser();
		try
		{
			JSONObject obj = ( JSONObject ) parser.parse( jsonString );
			String message = ( String ) obj.get( "message" );
			if ( message != null && message.length() > 0 )
			{
				tv.setText( message );
			}
			else
			{
				tv.setText( "" );
				Intent intent = new Intent( getApplicationContext(), SubActivity.class );
				intent.putExtra( "jsonString", obj.toJSONString() );
				startActivity( intent );
			}
		}
		catch ( ParseException e )
		{
			e.printStackTrace();
		}
	}

	private Drawable loadImage( String source, boolean isChart )
	{
		Drawable drawable = null;
		URL sourceURL;
		try
		{
			InputStream inputStream = getInputStream( source );
			BufferedInputStream bufferedInputStream = new BufferedInputStream( inputStream );
			drawable = drawProperImages( isChart, bufferedInputStream );

		}
		catch ( MalformedURLException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return drawable;
	}

	private InputStream getInputStream( String source ) throws MalformedURLException, IOException
	{
		URL sourceURL;
		sourceURL = new URL( source );
		URLConnection urlConnection = sourceURL.openConnection();
		urlConnection.connect();
		InputStream inputStream = urlConnection.getInputStream();
		return inputStream;
	}

	private Drawable drawProperImages( boolean isChart, BufferedInputStream bufferedInputStream )
	{
		Drawable drawable;
		Bitmap bm = BitmapFactory.decodeStream( bufferedInputStream );

		// convert Bitmap to Drawable
		drawable = new BitmapDrawable( getResources(), bm );

		if ( isChart )
		{
			drawable.setBounds( 0, 0, 600, 550 );
		}
		else
		{
			drawable.setBounds( 0, 0, 30, 42 );
		}
		return drawable;
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.zillow_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if ( id == R.id.action_about )
		{
			aboutAlert.show();
			return true;
		}

		if ( id == R.id.exit )
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
			alertDialogBuilder.setTitle( "Exit Application?" );
			alertDialogBuilder.setMessage( "Click yes to exit!" ).setCancelable( false ).setPositiveButton( "Yes", new DialogInterface.OnClickListener()
			{
				public void onClick( DialogInterface dialog, int id )
				{
					moveTaskToBack( true );
					android.os.Process.killProcess( android.os.Process.myPid() );
					System.exit( 1 );
				}
			} )

			.setNegativeButton( "No", new DialogInterface.OnClickListener()
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

	private void setUpDownImages( JSONObject obj )
	{
		Html.fromHtml( ( String ) obj.get( "change1" ), new ImageGetter()
		{
			@Override
			public Drawable getDrawable( String source )
			{
				image1 = loadImage( source, false );
				return ZillowMain.image1;
			}
		}, null );
		Html.fromHtml( ( String ) obj.get( "change2" ), new ImageGetter()
		{
			@Override
			public Drawable getDrawable( String source )
			{
				image2 = loadImage( source, false );
				return ZillowMain.image2;
			}
		}, null );
	}

	private void setCharts( JSONObject obj )
	{
		c0 = new ImageGetter()
		{

			@Override
			public Drawable getDrawable( String source )
			{
				return loadImage( source, true );
			}
		}.getDrawable( ( String ) ( ( JSONObject ) obj.get( "c1" ) ).get( "0" ) );
		c1 = new ImageGetter()
		{

			@Override
			public Drawable getDrawable( String source )
			{
				return loadImage( source, true );
			}
		}.getDrawable( ( String ) ( ( JSONObject ) obj.get( "c2" ) ).get( "0" ) );
		c2 = new ImageGetter()
		{

			@Override
			public Drawable getDrawable( String source )
			{
				return loadImage( source, true );
			}
		}.getDrawable( ( String ) ( ( JSONObject ) obj.get( "c3" ) ).get( "0" ) );
	}

}
