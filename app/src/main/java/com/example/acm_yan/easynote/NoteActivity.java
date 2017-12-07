package com.example.acm_yan.easynote;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidParameterException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import com.example.acm_yan.easynote.NoteDB.Note;
import com.example.acm_yan.easynote.Writer;
public class NoteActivity extends Activity {

    private Note mNote = new Note();
    private Writer mWriter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mWriter = new Writer((EditText)findViewById(R.id.NoteEditText));
        mNote.key = getIntent().getLongExtra("NoteId",-1);//获取获取数据
        if(mNote.key!=-1) {
            Note note = NoteDB.getInstance().get(mNote.key);
            if(note!=null) {//如果没有找到
                mWriter.setContent(note.content);
                mNote = note;
            }
            else {
                mNote.key=-1;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        onSaveNote();//保存
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);//菜单添加
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.share) {

            checkStorageDir(Environment.getExternalStorageDirectory() + File.separator + "EasyNote");//检查内存 并创建文件夹
            getScreenHot(this, "/sdcard/EasyNote/"+mWriter.getTitle()+".png");//截图保存
            showPopMenu(findViewById(R.id.share));//弹出窗口
            return true;
        }
        else if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void onSaveNote() {
        mNote.title = mWriter.getTitle();//得到主题
        mNote.content = mWriter.getContent();//得到文字
        if(mNote.key==-1) {
            if(!"".equals(mNote.content)) {//没有这个数据
                mNote.date = Calendar.getInstance().getTimeInMillis();//改时间
                NoteDB.getInstance().insert(mNote);//插入数据
            }
        }
        else {
            NoteDB.getInstance().update(mNote);//更新数据
        }
    }

    private void getScreenHot(Activity activity, String filePath)
    {
        try
        {
            View view = activity.getWindow().getDecorView();//得到这个视图
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap b1 = view.getDrawingCache();

            // 获取状态栏高度
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            // 获取屏幕长和高
            int width = activity.getWindowManager().getDefaultDisplay().getWidth();
            int height = activity.getWindowManager().getDefaultDisplay()
                    .getHeight();
            // 去掉标题栏
            Bitmap bitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                    - statusBarHeight);
            view.destroyDrawingCache();

            try
            {
                FileOutputStream fos = new FileOutputStream(filePath);//打开文件输出流
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);//输出
            }
            catch (FileNotFoundException e)
            {
                throw new InvalidParameterException();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//检查有没内存卡
    }

    public void checkStorageDir(String filepath) {
        if(isSDCardMounted()) {
            File directory = new File(filepath);
            if( !directory.exists()) {
                directory.mkdirs();//如果没有这个文件夹 就创建
            }
        }
    }
    public void showPopMenu(View view){
        PopupMenu menu = new PopupMenu(this,view);
        menu.getMenuInflater().inflate(R.menu.share_menu,menu.getMenu());//设置弹出窗口
        final EditText editText=(EditText) findViewById(R.id.NoteEditText);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.txt:
                        Intent textIntent = new Intent(Intent.ACTION_SEND);//创建活动
                        textIntent.putExtra(Intent.EXTRA_TEXT,editText.getText().toString());//文字输入
                        textIntent.setType("text/plain");
                        startActivity(Intent.createChooser(textIntent,"分享给"));
                        break;

                    case R.id.png:

                        Intent imageIntent = new Intent(Intent.ACTION_SEND);
                        Uri imageUri = Uri.fromFile(new File("/sdcard/EasyNote/"+mWriter.getTitle()+".png"));
                        imageIntent.putExtra(Intent.EXTRA_STREAM, imageUri);//图片输入
                        imageIntent.setType("image/*");
                        startActivity(Intent.createChooser(imageIntent,"分享给"));
                        break;

                }
                return true;
            }
        });
        menu.show();
    }

}
