package Google;

import org.json.JSONObject;

public class GoogleSearchResult {

    private String mUnescapedUrl;
    private String mUrl;
    private String mVisibleUrl;
    private String mCacheUrl;
    private String mTitle;
    private String mTitleNoFormatting;
    private String mContent;


    GoogleSearchResult(JSONObject jsonObject) {
        mUnescapedUrl = jsonObject.getString("unescapedUrl");
        mUrl = jsonObject.getString("url");
        mVisibleUrl = jsonObject.getString("visibleUrl");
        mCacheUrl = jsonObject.getString("cacheUrl");
        mTitle = jsonObject.getString("title");
        mTitleNoFormatting = jsonObject.getString("titleNoFormatting");
        mContent = jsonObject.getString("content");
    }

    public String getUnescapedUrl() {
        return mUnescapedUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getVisibleUrl() {
        return mVisibleUrl;
    }

    public String getCacheUrl() {
        return mCacheUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTitleNoFormatting() {
        return mTitleNoFormatting;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public String toString() {
        return "GoogleSearchResult{" +
                "mUnescapedUrl='" + mUnescapedUrl + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mVisibleUrl='" + mVisibleUrl + '\'' +
                ", mCacheUrl='" + mCacheUrl + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mTitleNoFormatting='" + mTitleNoFormatting + '\'' +
                ", mContent='" + mContent + '\'' +
                '}';
    }
}
