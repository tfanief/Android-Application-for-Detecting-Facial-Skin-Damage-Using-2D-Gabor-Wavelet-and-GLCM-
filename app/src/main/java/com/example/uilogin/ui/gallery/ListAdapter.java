package com.example.uilogin.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.uilogin.R;
import com.squareup.picasso.Picasso;


public class ListAdapter extends BaseAdapter {

    Context context;

    private final String [] hasil;
    private final String [] images;

    public ListAdapter(Context context, String [] hasil, String[] images){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context=context;
        this.hasil=hasil;
        this.images = images;
    }

    @Override
    public int getCount() {
        return hasil.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.txtName.setText(" "+hasil[position]);
        Picasso.get().load(images[position]).into(viewHolder.icon);

        return convertView;
    }

    private static class ViewHolder {
        TextView txtName;
        ImageView icon;

    }

}
