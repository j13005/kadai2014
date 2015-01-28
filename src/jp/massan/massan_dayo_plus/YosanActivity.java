package jp.massan.massan_dayo_plus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class YosanActivity extends Activity {
	//フィールド
	ImageButton back = null;	//戻るボタン
	ImageButton save = null;	//保存ボタン
	ImageView masahiro = null;//まさひろ(キャラ)
	EditText yosan = null;		//予算入力ボタン

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yosan);

		//アクティビティ登録
		back = (ImageButton)findViewById(R.id.YosanImageButton_back);
		back.setImageResource(R.drawable.back);
		save =(ImageButton)findViewById(R.id.YosanImageButton_save);
		save.setImageResource(R.drawable.save);
		masahiro = (ImageView)findViewById(R.id.Masahiro);
		masahiro.setImageResource(R.drawable.masahiro);
		yosan = (EditText)findViewById(R.id.YosanEditText_yosan);
		//戻るボタン
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(YosanActivity.this,ManageActivity.class);
                startActivity(intent);
                System.out.println("aaaa");
			}
		});
		//保存ボタン
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//DBデータ代入
				put_yosan();
				Toast_show(1);
                Intent intent = new Intent(YosanActivity.this, ManageActivity.class);
                startActivity(intent);
			}
		});
	}
	///トースト表示用
	public void Toast_show(int i){
        switch(i)
        {
        case 1:
            Toast.makeText(this, "保存されました", Toast.LENGTH_SHORT).show();
            break;
        }
    }
	///YosanActivity画面のデータを取得、DBにput(YosanActivity)
	private void put_yosan(){
		//Yosanの値を取得（エディットテキスト）、DBにputする処理
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		SQLiteDatabase  sqdb = dbHelper.getWritableDatabase();
		String yosan_s = yosan.getText().toString();
		ContentValues values = new ContentValues();
		values.put("yosan", yosan_s);
		//Insert
		sqdb.insert("yosan_table", null, values);
		yosan.setText("");
		sqdb.close();
	}
}
