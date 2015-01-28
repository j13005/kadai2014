package jp.massan.massan_dayo_plus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ManageActivity extends Activity {
	//フィールド
	ImageButton back =null;		//戻るボタン
	ImageButton yosan = null;		//予算アクティビティ移行
	ImageView masako = null;		//まさこ（キャラ）
	TextView buybasyo = null;		//買った場所
	TextView buyday = null;			//買った日
	TextView buykingaku = null;	//買った金額
	TextView todaybuy = null;		//今日買った合計金額
	TextView todaymoney = null;//今日つかったお金
	TextView todayshina = null;	//今日買ったもの
	ListView todays = null;			//リストビュー
	static SQLiteDatabase mydb;	//SQlite
	int summoney = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);

		//アクティビティ登録
		back = (ImageButton)findViewById(R.id.ManageImageButton_back);
		back.setImageResource(R.drawable.back);
		yosan = (ImageButton)findViewById(R.id.ManageImageButton_yosan);
		yosan.setImageResource(R.drawable.yosan_activity);
		masako = (ImageView)findViewById(R.id.Masako);
		masako.setImageResource(R.drawable.masako);
		todaymoney = (TextView)findViewById(R.id.ManageTextView_todaymoney);
		todays = (ListView)findViewById(R.id.manage_listview);

		//データベースから取得する処理
		get_manage();

		//戻るボタン
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(ManageActivity.this, MainActivity.class);
	             startActivity(intent);
			}
		});
		//予算ボタン
		yosan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageActivity.this, YosanActivity.class);
                startActivity(intent);
			}
		});
	}

	///管理画面にDBの値を計算してエディットテキストに設定(ManageActivity)
	private void get_manage(){
		//ID_kaimonoで各情報をリストビューに設定する
		DatabaseHelper hlpr = new DatabaseHelper(getApplicationContext());
		mydb = hlpr.getWritableDatabase();
		try{
			Cursor cr = mydb.rawQuery("SELECT * FROM kaimono_table ORDER BY id_kaimono desc", null);
			cr.moveToFirst();
			if(cr.getCount()>0){
				ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
				for(int cnt = 1;cnt<cr.getCount();cnt++){
					adapter.add("買ったもの "+cr.getString(1)+"\n"+cr.getString(4)+cr.getString(2)+" "+cr.getString(3)+"円");
					summoney+= Integer.parseInt(cr.getString(3));
					cr.moveToNext();
					todays.setAdapter(adapter);
				}
				todaymoney.setText(String.valueOf(summoney));
			}else todays.setAdapter(null);
		}finally{
			mydb.close();
		}

	}
}
