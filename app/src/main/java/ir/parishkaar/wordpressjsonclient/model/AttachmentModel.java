package ir.parishkaar.wordpressjsonclient.model;

import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

public class AttachmentModel extends SugarRecord {

    public int id;
    public String name, date, content, imgurl;

    public AttachmentModel() {
        id = -1;
        name = date = content = imgurl = "";
    }

    public void fromJson(JSONObject jsonObject) {
        try {
            imgurl = jsonObject.getString("url");
//            name = jsonObject.getString("name");
//            date = jsonObject.getString("date");
//            content = jsonObject.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imgurl", imgurl);
//            jsonObject.put("name", name);
//            jsonObject.put("date", date);
//            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
