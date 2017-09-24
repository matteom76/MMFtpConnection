package it.matteomoretto.matteomorettoftpconnection;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matteo Moretto on 14/09/2017.
 */
public class FileAdapter extends BaseAdapter {
    private ArrayList<FileElement> FilelistData;
    private Context context;
    private SparseBooleanArray ItemSelected;


    public FileAdapter (Context aContext, ArrayList<FileElement> listData) {
        this.FilelistData = listData;
        this.context = aContext;
        ItemSelected = new SparseBooleanArray();
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
        try {
            if (ItemSelected.get(position)) {
                txt.setBackgroundColor(context.getResources().getColor(R.color.blueFileSelected));
                txt.setTextColor(context.getResources().getColor(R.color.blueListDir));
            } else {
                txt.setBackgroundColor(context.getResources().getColor(R.color.blueListFile));
                txt.setTextColor(context.getResources().getColor(R.color.whiteText));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            txt.setBackgroundColor(context.getResources().getColor(R.color.blueListFile));
            txt.setTextColor(context.getResources().getColor(R.color.whiteText));
        }

        return convertView;
    }

    public boolean ToggleSetItemCheck(int position) {
        boolean value = ItemSelected.get(position);
        ItemSelected.put(position,!value);
        notifyDataSetChanged();
        return ItemSelected.get(position);
    }

    public void SelectAllFile() {
        for (int i=0; i<=(getCount()-1);i++) {
            ItemSelected.put(i,true);
        }
        notifyDataSetChanged();
    }

    public void DeselectAllFile() {
        for (int i=0; i<=(getCount()-1);i++) {
            ItemSelected.put(i,false);
        }
        notifyDataSetChanged();
    }

}
