package com.webfarms.treatmyfish.parser;

import android.content.Context;

import com.webfarms.treatmyfish.bean.BeanIssueList;
import com.webfarms.treatmyfish.database.TableIssueList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;


/**
 * Created by Ashish Zade on 2/28/2017 & 12:06 PM.
 */

public class JSONparser {

    public static boolean parseIssueList(Context context, String result) {


        LinkedList<BeanIssueList> issueList = new LinkedList<BeanIssueList>();

        JSONObject jsonObj;
        try {


            jsonObj = new JSONObject(result);
            String status = "" + jsonObj.getInt("responseCode");

            if (status.equalsIgnoreCase("200")) {

                try {

                    JSONArray issueArray = jsonObj.getJSONArray("issueList");

                    for (int i = 0; i < issueArray.length(); i++) {
                        try {
                            BeanIssueList bean = new BeanIssueList();
                            JSONObject obj = issueArray.getJSONObject(i);
                            bean.setIssueId(obj.getInt("issueId"));
                            bean.setIssueName(obj.getString("issueName"));
                            bean.setActive(obj.getString("active"));
                            bean.setCreatedOn(obj.getString("createdOn"));

                            issueList.add(bean);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    TableIssueList issTable = new TableIssueList(context);
                    if (issueList.size() > 0) {
                   //     issTable.deleteAllCategory();
                        issTable.insertAll(issueList);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } else {

                try {
                    String message = jsonObj.getString("status");
                    //    GlobalData.showSnackBar(view, message, true);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return false;
            }
        }catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
