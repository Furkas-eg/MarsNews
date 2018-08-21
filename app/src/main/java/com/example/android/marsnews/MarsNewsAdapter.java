package com.example.android.marsnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MarsNewsAdapter extends ArrayAdapter<MarsNews> {


    /********************************************
     * CONSTRUCTOR
     * creates new {@Link MarsNewsAdapter}
     *
     * @param context is context
     * @param marsNews is the list of the app
     ********************************************/
    public MarsNewsAdapter(Context context, List<MarsNews> marsNews) {
        super(context, 0, marsNews);
    }

    // Returns a list view item that displays info about the
    // article related to mars. yay
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.mars_list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        MarsNews currentMarsNews = getItem(position);

        // Find the TextView in XML by ID. Give TextView a value of the section ID
        TextView sectionIdView = (TextView) listItemView.findViewById(R.id.section_id);
        sectionIdView.setText(currentMarsNews.getSectionId());

        // Find the TextView in XML by ID. Give TextView a value of the article Title
        TextView articleTitleView = (TextView) listItemView.findViewById(R.id.article_title);
        articleTitleView.setText(currentMarsNews.getArticleTitleId());

        // Find the TextView in XML by ID. Give TextView a value of the posted article date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        dateView.setText(currentMarsNews.getDateId());

        // Find the TextView in XML by ID. Give TextView a value of the author name
        TextView authorNameView = (TextView) listItemView.findViewById(R.id.author_name);
        authorNameView.setText(currentMarsNews.getAuthorName());

        return listItemView;
    }

}
