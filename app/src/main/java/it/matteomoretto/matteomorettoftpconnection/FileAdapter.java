package it.matteomoretto.matteomorettoftpconnection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matteo Moretto on 14/09/2017.
 */
public class FileAdapter extends BaseAdapter {
    private ArrayList<FileElement> FilelistData;
    private Context context;
    private ViewGroup ListObj;

    public FileAdapter (Context aContext, ArrayList<FileElement> listData) {
        this.FilelistData = listData;
        this.context = aContext;
    }

    @Override
    public int getCount() {
        return FilelistData.size();
    }

    @Override
        public Object getItem(int position) {
        return FilelistData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView=LayoutInflater.from(context).inflate(R.layout.listviewfile, null);
        }
        FileElement finfo=(FileElement) getItem(position);
        TextView txt=(TextView) convertView.findViewById(R.id.textfile);
        txt.setText(finfo.getFileName());
        ListObj = parent;
        return convertView;
    }
}
