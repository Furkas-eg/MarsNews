package com.example.android.marsnews;

public class MarsNews {

    // Section ID, Title of article, date published of article, article url, and article author below.
    private String mSectionId;
    private String mArticleTitle;
    private String mDate;
    private String mUrl;
    private String mAuthorName;

    /************************************************************
     *  CONSTRUCTOR
     *  Creates a new {@Link MarsNews} object.
     *
     *  @param sectionId is the section where article falls in
     *  @param articleTitle is title of actual article
     *  @param date date published of article
     *  @param url is the article's URL
     *  @param authorName is the name of author who wrote the article
     *
     ************************************************************/
    public MarsNews(String sectionId, String articleTitle, String date, String url, String authorName) {
        // Give value
        mSectionId = sectionId;
        mArticleTitle = articleTitle;
        mDate = date;
        mUrl = url;
        mAuthorName = authorName;
    }

    /*****************
     * METHODS below pull info about the article
     * returns section of the article
     *
     * @methodName getSectionId()
     * @methodName getArticleTitle()
     * @methodName getDateId()
     * @methodName getUrlId()
     * @methodName getAuthorName()
     *****************/
    public String getSectionId() {
        return mSectionId;
    }
    public String getArticleTitleId() {
        return mArticleTitle;
    }
    public String getDateId() {
        return mDate;
    }
    public String getUrlId() {
        return mUrl;
    }
    public String getAuthorName() {
        return mAuthorName;
    }


}

