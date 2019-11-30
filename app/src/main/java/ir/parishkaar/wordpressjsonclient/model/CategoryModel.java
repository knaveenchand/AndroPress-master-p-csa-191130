package ir.parishkaar.wordpressjsonclient.model;

import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

import ir.parishkaar.wordpressjsonclient.util.NetUtil;

public class CategoryModel extends SugarRecord {

    public String title, slug;
    public int id, postCount;

    public String getUrl(int page) {
        return "get_category_posts/?id=" + id + "&count=" + NetUtil.postCount + "&page=" + page;
//        return "get_posts/?product_cat=" + slug;
    }

    public void fromJson(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.slug = jsonObject.getString("slug");
            this.title = jsonObject.getString("title");
            this.postCount = jsonObject.getInt("post_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", this.id);
            jsonObject.put("slug", this.slug);
            jsonObject.put("title", this.title);
            jsonObject.put("post_count", this.postCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
