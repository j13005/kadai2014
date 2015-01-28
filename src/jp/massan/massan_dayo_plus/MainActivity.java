package jp.massan.massan_dayo_plus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	//フィールド
	ImageButton input = null;																//入力ボタン
	ImageButton manage = null;															//管理ボタン
	ImageView massan = null;																//タイトル
	String massan_tweet = "--まっさんのお小遣い帳からツイート--";		//tweet付属
	String tweet = null;																		//tweet文字列
	ImageButton twitter =null; 															//twitterボタン
	TextView zankin = null;																	//残金表示用
	static SQLiteDatabase mydb;															//SQlite
	Globals global;																				//グローバル変数
	String UserName = null;																//ユーザー名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//アクティビティに登録
		global =  (Globals)getApplication();	//グローバル変数使用準備
		massan = (ImageView)findViewById(R.id.Massan);
		massan.setImageResource(R.drawable.title_logo);
		zankin = (TextView)findViewById(R.id.MaintextView_2_zankin);
		input = (ImageButton)findViewById(R.id.MainImageButton_I);
		input.setImageResource(R.drawable.input_activity_b);
		manage = (ImageButton)findViewById(R.id.MainImageButton_M);
		manage.setImageResource(R.drawable.manage_activity_b);
		twitter = (ImageButton)findViewById(R.id.MainImageButton_T);
		twitter.setImageResource(R.drawable.twitter_b);
		UserName = "まっさん";		//debug(login)

		//zankin表示メソッド
		get_main();

		//NFC起動処理
		// インテントの取得
        Intent intent = getIntent();
        // NDEF対応カードの検出かチェック
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
        	//login処理(実装予定)
    		}

		//入力画面ボタン
		input.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
			}
		});
		//管理画面ボタン
		manage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,ManageActivity.class);
                startActivity(intent);
			}
		});
		//twitterボタン
		twitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if(appInstalledOrNot("com.twitter.android"))
	                {
					 	tweet = UserName+"の残金は"+zankin.getText()+"円です！";
	                    String s = (new StringBuilder("twitter://post?message=")).append(tweet).append(massan_tweet).toString();
	                    Intent intent = new Intent("android.intent.action.VIEW");
	                    intent.setData(Uri.parse(s));
	                    startActivity(intent);
	                    return;
	                } else
	                {
	                    Toast_show(3);
	                    return;
	                }
			}
		});
	}
	///該当するアプリがインストールされているか調べる（変数s内に調べるアプリ名）
	private boolean appInstalledOrNot(String s)
    {
        PackageManager packagemanager = getPackageManager();
        try
        {
            packagemanager.getPackageInfo(s, 1);
        }
        catch(Exception exception)
        {
            return false;
        }
        return true;
    }
	///トースト表示用
    public void Toast_show(int i)
    {
        switch(i)
        {
        case 1: // '\001'
            Toast.makeText(this, "Input", Toast.LENGTH_SHORT).show();
            break;
        case 2: // '\002'
            Toast.makeText(this, "Manage", Toast.LENGTH_SHORT).show();
            break;
        case 3: // '\003'
            Toast.makeText(this, "Twitterアプリがインストールされていません", Toast.LENGTH_LONG).show();
            break;
        case 4: // '\004'
            Toast.makeText(this, "Camera start", Toast.LENGTH_SHORT).show();
            break;
        case 5: // '\005'
            Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
            break;
        }
    }

  ///残金取得（MainActivity）
  	private void get_main(){
  		//YosanテーブルのYosanの値‐ManageActivityのManageActivity_TextView_todaykingakuの値を
  		//DBから取得
  		//MainActivity_TextView_2_zankinに設定
  		DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
  		mydb = dbHelper.getWritableDatabase();
  		try{
  			Cursor csr = mydb.rawQuery("SELECT * FROM yosan_table ORDER BY id_yosan DESC",null);
  			csr.moveToFirst();
  			if(csr.getCount() > 0){
  					zankin.setText(csr.getString(1));
  					if(global.zankin_g < 0) {
  						global.zankin_g = 0;
  					}else 	global.zankin_g = Integer.parseInt(csr.getString(1));
  			}else if(!global.flg){ 	//データがなかったとき
  				global.flg = true;
  				add_yosan();			//とりあえずyosanDBデータを入れる
  				add_kaimono();	//とりあえずkaimonoDBデータを入れる
  				get_main();		//再帰的
  			}
  		}finally{
  			mydb.close();
  		}
  	}
  	///とりあえずyosanDBにデータ入れるメソッド
  	private void add_yosan(){
  		DatabaseHelper hlper = new DatabaseHelper(this);
  		SQLiteDatabase db = hlper.getWritableDatabase();
  		int yosan = 0;
  		String HI = "";
  		ContentValues values = new ContentValues();
  		values.put("yosan",yosan);
  		values.put("hi",HI);
  		db.insert("yosan_table", null, values);
  		db.close();
  	}
  	///とりあえずkaimonoDBにデータを入れるメソッド
  	private void add_kaimono(){
  		DatabaseHelper hlper = new DatabaseHelper(this);
  		SQLiteDatabase db = hlper.getWritableDatabase();
  		String shinamono = "";
  		String basyo = "";
  		int kingaku = 0;
  		String hi = "";
  		ContentValues values = new ContentValues();
  		values.put("shinamono", shinamono);
  		values.put("basyo", basyo);
  		values.put("kingaku",kingaku);
  		values.put("hi", hi);
  		db.insert("kaimono_table", null, values);
  		db.close();
  	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
