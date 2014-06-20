package cl.wafle.mediaplayer.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cl.wafle.mediaplayer.model.Media;

/**
 * Created by ezepeda on 19-06-14.
 */
public abstract class GetRadios extends AsyncTask<Void, Void, List<String[]>> {
    private final String TAG = GetRadios.class.getSimpleName();
    private final String URL = "http://www.radio-browser.info/webservice/json/stations/bycountry/chile";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String[]> doInBackground(Void... voids) {
        return postData();
    }

    @Override
    protected void onPostExecute(List<String[]> strings) {
        super.onPostExecute(strings);
    }

    private List<String[]> postData() {
        JSONArray jsonArray = null;
        List<String[]> stringList = new ArrayList<String[]>();
        try{
            // Create a new HttpClient and Post Header
            String response = HttpRequest.get(URL).body();

            Log.v(TAG, "response > "+response);
            jsonArray = new JSONArray(response);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Media media = new Media();
                media.setId(jsonObject.getInt("id"));
                media.setName(jsonObject.getString("name"));
                media.setUrl(jsonObject.getString("url"));
                stringList.add(new String[]{media.getName(), media.getUrl()});
            }

        }catch (JSONException e){
            e.printStackTrace();
            Log.e(TAG, "postData"+ e.getMessage());
        }

        return stringList;
    }
}
