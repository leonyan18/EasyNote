package com.example.acm_yan.easynote;

import com.example.acm_yan.easynote.NoteDB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity implements OnItemClickListener {

    private static final int REQUEST_CODE_ADD  = 0;
    private static final int REQUEST_CODE_EDIT = 1;

    private ListView mNoteListView;
    private NoteAdapter mNoteAdapter;
    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NoteDB.getInstance().open(this);
        mNoteListView = (ListView)findViewById(R.id.NoteListView);//得到对象
        mNoteAdapter = new NoteAdapter(this);
        mNoteListView.setAdapter(mNoteAdapter);
        registerForContextMenu(mNoteListView);321
        OnItemLongClickListener longListener = new OnItemLongClickListener() {//设置长按的时间触发
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                mSelectedPosition = position;//获取我点击的位置
                mNoteListView.showContextMenu();//弹出菜单
                return true;
            }
        };
        mNoteListView.setOnItemLongClickListener(longListener);
        mNoteListView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        NoteDB.getInstance().close();//关闭数据库
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//添加菜单
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.data_menu, menu);//
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//点击添加的事件触发
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this,NoteActivity.class);//跳转到活动NoteActivity
            startActivityForResult(intent,REQUEST_CODE_ADD);//确定返回的数据是从哪个Activity中返回，用来标识目标activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {//菜单点击事件
        switch(item.getItemId()) {
            case R.id.DataDelete:
                if(mSelectedPosition != -1) {
                    NoteDB.getInstance().delete(mSelectedPosition);//删除该位置的信息
                    mNoteAdapter.notifyDataSetChanged();//更新位置
                }
                return true;
            case R.id.DataClear:
                NoteDB.getInstance().clear();//清除所有的数据
                mNoteAdapter.notifyDataSetChanged();//更新
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {//弹出菜单
        Intent intent = new Intent(this,NoteActivity.class);
        intent.putExtra("NoteId",id);
        startActivityForResult(intent,REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//活动返回值
        if(requestCode==REQUEST_CODE_ADD||requestCode==REQUEST_CODE_EDIT) {
            mNoteAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}