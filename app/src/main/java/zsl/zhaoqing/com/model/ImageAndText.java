package zsl.zhaoqing.com.model;

/**
 * 一个列表项的数据模型
 */
public class ImageAndText {

    //一个列表项中图片的链接字符串
    private String imageUrl;
    //一个列表项中显示的标题
    private String label;
    //一个列表项中所对应的文章链接
    private String articleUrl;

    public ImageAndText(String imageUrl, String label,String articleUrl) {
        this.imageUrl = imageUrl;
        this.label = label;
        this.articleUrl = articleUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }
}
