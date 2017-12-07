package com.example.acm_yan.easynote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.acm_yan.easynote.NoteDB.Note;

public class NoteAdapter extends BaseAdapter {

    private Context mContext;

    protected class ViewHolder {
        TextView mNoteDate;
        TextView mNoteTitle;
    }

    public NoteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return NoteDB.getInstance().size();
    }//返回数据数量

    @Override
    public Object getItem(int position) {
        return NoteDB.getInstance().get(position);
    }//得到数据

    @Override
    public long getItemId(int position) {
        return ((Note)getItem(position)).key;
    }//得到数据的id

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (LinearLayout)inflater.inflate(R.layout.layout_note_item, null);
            ViewHolder holder = new ViewHolder();
            holder.mNoteDate  = (TextView)convertView.findViewById(R.id.NoteDateText);
            holder.mNoteTitle  = (TextView)convertView.findViewById(R.id.NoteTitleText);
            convertView.setTag(holder);
        }

        Note note = (Note)getItem(position);
        if(note != null) {
            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.mNoteDate.setText(getDateStr(note.date));//设置日期
            holder.mNoteTitle.setText(note.title);//设置标题
        }

        return convertView;
    }

    public static String getDateStr(long milliseconds) {//设置时间
        return new SimpleDateFormat("yyyy年MM月dd日 EEEE HH点mm分", Locale.CHINA).format(milliseconds);
    }
}
