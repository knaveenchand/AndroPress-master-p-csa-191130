package ir.parishkaar.wordpressjsonclient.model;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 10/5/2016.
 */

public class PostModel extends SugarRecord {
    public int id, comment_count;
    public String url, title, content, date, author, comment_status, status, img, symptoms, management, problem_category, scientific_name, customcomments, customattachments;
    public List<String> categories, attachmentImagesList;
    public List<CommentModel> comments;
    public List<AttachmentModel> attachments;
    public JSONArray categoryjsonlist, commentjsonlist, attachmentImagesArrayList;
    int attachmentImagesListcount, customattachmentscount;
    public String imageone, imagetwo, imagethree;

    public PostModel() {
        id = -1;
        url = title = content = date = author = comment_status = status = symptoms = "";
        categories = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public void fromJson(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            url = jsonObject.getString("url");
            title = jsonObject.getString("title");
            status = jsonObject.getString("status");
            content = jsonObject.getString("content");
            date = jsonObject.getString("date");
            for (int i = 0; i < jsonObject.getJSONArray("categories").length(); i++) {
                categories.add(jsonObject.getJSONArray("categories").getJSONObject(i).getString("title"));
            }
            author = jsonObject.getJSONObject("author").getString("nickname");
            symptoms = jsonObject.getJSONObject("custom_fields").getString("symptoms");
            management = jsonObject.getJSONObject("custom_fields").getString("management");
            problem_category = jsonObject.getJSONObject("custom_fields").getString("problem_category");
            scientific_name = jsonObject.getJSONObject("custom_fields").getString("scientific_name");
            customcomments = jsonObject.getJSONObject("custom_fields").getString("comments");

            for (int i = 0; i < jsonObject.getJSONArray("comments").length(); i++) {
                CommentModel commentModel = new CommentModel();
                commentModel.fromJson(jsonObject.getJSONArray("comments").getJSONObject(i));
                comments.add(commentModel);
            }
            comment_count = jsonObject.getInt("comment_count");
            comment_status = jsonObject.getString("comment_status");
            img = jsonObject.getString("thumbnail");

//            for (int i=0; i < jsonObject.getJSONArray("attachments").length(); i++) {
//                AttachmentModel attachmentModel = new AttachmentModel();
//                attachmentModel.fromJson(jsonObject.getJSONArray("attachments").getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail"));
//                attachments.add(attachmentModel);
//            }





        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("url", url);
            jsonObject.put("title", title);
            jsonObject.put("status", status);
            jsonObject.put("content", content);
            jsonObject.put("date", date);

            categoryjsonlist = new JSONArray();
            for (int i = 0; i < categories.size(); i++) {
                categoryjsonlist.put(new JSONObject().put("title", categories.get(i)));
            }
            jsonObject.put("categories", categoryjsonlist);


            JSONObject authorobj = new JSONObject();
            authorobj.put("nickname", author);
            jsonObject.put("author", authorobj);

            JSONObject custom_fieldsobj = new JSONObject();
            custom_fieldsobj.put("symptoms", symptoms);
            custom_fieldsobj.put("management", management);
            custom_fieldsobj.put("problem_category", problem_category);
            custom_fieldsobj.put("scientific_name", scientific_name);
            custom_fieldsobj.put("comments", customcomments);
            jsonObject.put("custom_fields", custom_fieldsobj);

            commentjsonlist = new JSONArray();
            for (int i = 0; i < comments.size(); i++) {
                commentjsonlist.put(comments.get(i).toJson());
            }
            jsonObject.put("comments", commentjsonlist);

            jsonObject.put("comment_count", comment_count);
            jsonObject.put("comment_status", comment_status);
            jsonObject.put("thumbnail", img);

//            attachmentImagesArrayList = new JSONArray();
//            for (int al = 0; al < attachments.size(); al++) {
//                attachmentImagesArrayList.put(attachments.get(al).toJson());
//            }
//            jsonObject.put("attachments",attachmentImagesArrayList);

//            jsonObject.put("attachmentImagesListcount", customattachmentscount);
//
//            attachmentImagesArrayList = new JSONArray();
//            for (int aIAl = 0; aIAl < attachmentImagesList.size(); aIAl++) {
//                attachmentImagesArrayList.put(new JSONObject().put("url", attachmentImagesList.get(aIAl)));
//            }
//            jsonObject.put("imageLi", attachmentImagesArrayList);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
