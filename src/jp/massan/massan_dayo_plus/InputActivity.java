package jp.massan.massan_dayo_plus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.format.Time;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class InputActivity extends Activity {
	//フィールド
	ImageButton back = null;			//戻るボタン
	ImageButton camera = null;		//カメラ起動ボタン
	ImageButton save =null;			//保存ボタン
	EditText syouhin =null;				//商品名入力用
	EditText basyo =null;					//場所入力用
	EditText kin = null;					//金額入力用
	DatePicker hi = null;					//日付選択
	ImageView massan = null;			//まっさん（キャラ）
	Time time = new Time("Asia/Tokyo");		//時間帯取得
	Globals global;							//グローバル変数
	private SQLiteDatabase sqdb;		//データベース

	//カメラプレビュー準備
	 private Uri mImageUri;
	 private static final int IMAGE_CAPTURE = 10001;
	 private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);
		//アクティビティに登録
		global = (Globals)getApplication();
		camera = (ImageButton)findViewById(R.id.InputButton_camera);
        camera.setImageResource(R.drawable.camera);
        save = (ImageButton)findViewById(R.id.InputButton_save);
        save.setImageResource(R.drawable.save);
        back = (ImageButton)findViewById(R.id.InputButton_back);
        back.setImageResource(R.drawable.back);
        massan = (ImageView)findViewById(R.id.InputMassan);
        massan.setImageResource(R.drawable.massan);
        syouhin = (EditText)findViewById(R.id.InputEdittext_syouhin);
        kin = (EditText)findViewById(R.id.InputEditText_kingaku);
        basyo = (EditText)findViewById(R.id.InputEditText_basyo);
        hi = (DatePicker)findViewById(R.id.InputDatePicker_day);
    	time.setToNow();//現在時刻を取得
    	hi.updateDate(time.year,time.month,time.monthDay);//現在時刻に設定
        ///カメラボタン
        camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast_show(2);
				//カメラ呼び出し処理
				mImageUri = getPhotoUri();
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
				startActivityForResult(intent, IMAGE_CAPTURE);
			}
		});
        ///保存ボタン
        save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(syouhin.getText().toString().equals("")||kin.getText().toString().equals("")||basyo.getText().toString().equals("")){///空白判定
					Toast_show(3);
				}else{
					put_input();
					Toast_show(1);
					Intent intent = new Intent(InputActivity.this,MainActivity.class);
	                startActivity(intent);
				}
			}
		});
        ///戻るボタン
        back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InputActivity.this, MainActivity.class);
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
        case 2:
            Toast.makeText(this, "Camaera_start", Toast.LENGTH_SHORT).show();
            break;
        case 3:
        	Toast.makeText(this, "空白の箇所があります、入力してください。", Toast.LENGTH_SHORT).show();
        	break;
        }
    }
	///InputActivityデータを取得、DBにput(InputActivity)
	private void put_input(){
		//それぞれの値を取得（エディットテキスト）、DBにputする処理
		StringBuilder builder = new StringBuilder();
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		sqdb = dbHelper.getWritableDatabase();
		String syohin_s =syouhin.getText().toString();
		String kingaku_s = kin.getText().toString();
		String basyo_s = basyo.getText().toString();
		String hi_s =  builder.append(hi.getYear() + "年 ")
										.append(hi.getMonth()+1 + "月 ")
										.append(hi.getDayOfMonth() + "日 ").toString();
		int zankin = global.zankin_g - Integer.parseInt(kin.getText().toString());
		ContentValues values = new ContentValues();				//買物テーブル用
		ContentValues values_y = new ContentValues();			//予算テーブル用
		values.put("shinamono", syohin_s);
		values.put("kingaku", kingaku_s);
		values.put("basyo", basyo_s);
		values.put("hi", hi_s);
		values_y.put("yosan", zankin);
		//Insert
		sqdb.insert("kaimono_table", null, values);
		sqdb.insert("yosan_table", null, values_y);
		syouhin.setText("");
		kin.setText("");
		basyo.setText("");
		hi.updateDate(time.year,time.month,time.monthDay);
		sqdb.close();
	}
	/**
     * カメラから戻ってきた時の処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                setImageView();
            }
        }
    }

    /**
     * 状態を保持する
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_IMAGE_URI, mImageUri);
    }

    /**
     * 保持した状態を元に戻す
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageUri = (Uri) savedInstanceState.get(KEY_IMAGE_URI);
        setImageView();
    }

    /**
     * ImageViewに画像をセットする
     */
    private void setImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.photo_image);
        imageView.setImageURI(mImageUri);
    }

    /**
     * 画像のディレクトリパスを取得する
     *
     * @return
     */
    private String getDirPath() {
        String dirPath = "";
        File photoDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            photoDir = new File(extStorageDir.getPath() + "/" + getPackageName());
        }
        if (photoDir != null) {
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }
            if (photoDir.canWrite()) {
                dirPath = photoDir.getPath();
            }
        }
        return dirPath;
    }

    /**
     * 画像のUriを取得する
     *
     * @return
     */
    private Uri getPhotoUri() {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "samplecameraintent_" + title + ".jpg";
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, fileName);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.DATA, path);
        values.put(Images.Media.DATE_TAKEN, currentTimeMillis);
        if (file.exists()) {
            values.put(Images.Media.SIZE, file.length());
        }
        Uri uri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }
}
