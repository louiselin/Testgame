package com.example.louise.test;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;


public class ManualActivity extends AppCompatActivity {

    private CheckBox donotshowagain;
    public static final String PREFS_NAME = "manualgot";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_manual);
            getWindow().setBackgroundDrawableResource(R.drawable.bg);
//            switch (StoryActivity.party) {
//                case "Sinae": getWindow().setBackgroundDrawableResource(R.drawable.blue); break;
//                default: getWindow().setBackgroundDrawableResource(R.drawable.red); break;
//            }

            String re = refresh();
            String classjson = classjson(re);

            String bjson = "";
            String bjson2 = "";
            String bjsonreq = "";
            String bjson3 = "";
            String classification = "";
            String userbadge = userbadge();
            String newname = "";

            TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
            tabHost.setup();

            try {
                JSONArray jsonlists = new JSONArray(classjson);
                int size = jsonlists.length();
                for (int ii = 0; ii < size-1; ii++) {
                    classification = jsonlists.get(ii).toString();
                    switch (classification) {
                        case "專家":newname = "專家\n條件";break;
                        case "校園尋奇":newname = "成就\n解鎖";break;
                        case "探索":newname = "玩家\n級別";break;
                        default:newname = "樓主\n資格";
                    }

                    TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab1");

                    bjson = bjson(re, classification, userbadge);
                    final List<String> listname =new ArrayList<>();
                    for (String r: bjson.split("@")){
                        listname.add(r);
                    }

                    bjson2 = bjson2(re, classification, userbadge);
                    final List<String> listdes =new ArrayList<>();
                    for (String r: bjson2.split("@")){
                        listdes.add(r);
                    }

                    bjsonreq = bjsonreq(re, classification, userbadge);
                    final List<String> listreq =new ArrayList<>();
                    for (String r: bjsonreq.split("@")){
                        listreq.add(r);
                    }

                    bjson3 = bjson3(re, classification, userbadge);
                    final List<String> listk =new ArrayList<>();
                    for (String r: bjson3.split("@")){
                        listk.add(r);
                    }

                    final MyAdapter adapter;
                    adapter = new MyAdapter(this, listname, listdes, listreq, listk);

                    spec1.setContent(new TabHost.TabContentFactory() {
                        public View createTabContent(String tag) {
                            ListView listView01 = new ListView(ManualActivity.this);
                            listView01.setAdapter(adapter);

                            return listView01;
                        }
                    });
                    spec1.setIndicator(classification);
                    tabHost.addTab(spec1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            LayoutInflater adbInflater = LayoutInflater.from(this);
            View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
            donotshowagain = (CheckBox) eulaLayout.findViewById(R.id.skip);

            AlertDialog.Builder ad = new AlertDialog.Builder(ManualActivity.this);
            ad.setView(eulaLayout);
            ad.setTitle("看看您的徽章");
            ad.setMessage("請點選徽章分頁和您感興趣的徽章成就。\n\n快想想要如何獲得這些徽章條件吧！\n\n");
            ad.setNegativeButton("看看我的徽章", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    String checkBoxResult = "NOT checked";
                    if (donotshowagain.isChecked())
                        checkBoxResult = "checked";
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("skipMessage", checkBoxResult);
                    // Commit the edits!
                    editor.commit();

                    return;
                }
            });
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String skipMessage = settings.getString("skipMessage", "NOT checked");
            if (!skipMessage.equals("checked"))
                ad.show();
//            ad.show();
        }


    private String refresh() {
            String badgejson = "";
            try {
                badgejson = Httpconnect.httpget("http://140.119.163.40:8080/Spring08/app/badge");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            return badgejson;
        }

        private String userbadge() {
            String badgejson = "";
            try {
                badgejson = Httpconnect.httpget("http://140.119.163.40:8080/Spring08/app/badge/" + IndexActivity.userid);

            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            return badgejson;
        }


        private String classjson(String badgejson) {
            String keeper = "";
            try {
                JSONObject keeperlist = new JSONObject(badgejson);
                keeper = keeperlist.getString("classification");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return keeper;
        }

    private String bjson(String badgejson, String classification, String userbadge) {
        String keeper = "";
        String keeper2 = "";
        String k = "0";

        try {


            JSONObject keeperlist = new JSONObject(badgejson);
            keeper = keeperlist.getString("badge");

            JSONArray jsonlist = new JSONArray(keeper);
            JSONArray userbadgelist = new JSONArray(userbadge);

            for (int i = 0; i < jsonlist.length(); i++) {
                if (jsonlist.getJSONObject(i).getString("classification").equals(classification)) {
                    k = "0";
                    for (int j = 0; j < userbadgelist.length(); j++) {
                        int w = userbadgelist.getInt(j);
                        int x = jsonlist.getJSONObject(i).getInt("id");
                        if (w == x) {
                            k = w + "取得";
                            break;
                        }
                    }
                    keeper2 = keeper2 + jsonlist.getJSONObject(i).getString("name") + "@";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keeper2;
    }

    private String bjson2(String badgejson, String classification, String userbadge) {
        String keeper = "";
        String keeper2 = "";
        String k = "0";

        try {


            JSONObject keeperlist = new JSONObject(badgejson);
            keeper = keeperlist.getString("badge");

            JSONArray jsonlist = new JSONArray(keeper);
            JSONArray userbadgelist = new JSONArray(userbadge);

            for (int i = 0; i < jsonlist.length(); i++) {
                if (jsonlist.getJSONObject(i).getString("classification").equals(classification)) {
                    k = "0";

                    for (int j = 0; j < userbadgelist.length(); j++) {
                        int w = userbadgelist.getInt(j);
                        int x = jsonlist.getJSONObject(i).getInt("id");
                        if (w == x) {
                            k = w + "取得";
                            break;
                        }
                    }
                    keeper2 = keeper2 + jsonlist.getJSONObject(i).getString("description") + "@";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keeper2;
    }

    private String bjsonreq(String badgejson, String classification, String userbadge) {
        String keeper = "";
        String keeper2 = "";
        String k = "0";

        try {


            JSONObject keeperlist = new JSONObject(badgejson);
            keeper = keeperlist.getString("badge");

            JSONArray jsonlist = new JSONArray(keeper);
            JSONArray userbadgelist = new JSONArray(userbadge);

            for (int i = 0; i < jsonlist.length(); i++) {
                if (jsonlist.getJSONObject(i).getString("classification").equals(classification)) {
                    k = "0";

                    for (int j = 0; j < userbadgelist.length(); j++) {
                        int w = userbadgelist.getInt(j);
                        int x = jsonlist.getJSONObject(i).getInt("id");
                        if (w == x) {
                            k = w + "取得";
                            break;
                        }
                    }
                    keeper2 = keeper2 + jsonlist.getJSONObject(i).getString("requirement") + "@";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keeper2;
    }

    private String bjson3(String badgejson, String classification, String userbadge) {
        String keeper = "";
        String keeper2 = "";
        String k = "0";

        try {


            JSONObject keeperlist = new JSONObject(badgejson);
            keeper = keeperlist.getString("badge");

            JSONArray jsonlist = new JSONArray(keeper);
            JSONArray userbadgelist = new JSONArray(userbadge);

            for (int i = 0; i < jsonlist.length(); i++) {
                if (jsonlist.getJSONObject(i).getString("classification").equals(classification)) {
                    k = "0";

                    for (int j = 0; j < userbadgelist.length(); j++) {
                        int w = userbadgelist.getInt(j);
                        int x = jsonlist.getJSONObject(i).getInt("id");
                        if (w == x) {
                            k = w + "取得";
                            break;
                        }
                    }
                    keeper2 = keeper2 + k + "@";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keeper2;
    }
    private int p;
    public class MyAdapter extends BaseAdapter{
        private LayoutInflater myInflater;
        private List<String> na = new ArrayList<>();
        private List<String> des = new ArrayList<>();
        private List<String> req = new ArrayList<>();
        private List<String> k = new ArrayList<>();
        public MyAdapter(Context c, List<String> na, List<String> des, List<String> req, List<String> k) {
            this.na = na;
            this.des = des;
            this.req = req;
            this.k = k;
            myInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return na.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return na.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        private ImageView logo;
        private TextView name;
        private String list;
        private String reqes;
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            p = position;
            convertView = myInflater.inflate(R.layout.tab2xml, null);
            convertView.setOnClickListener(new OnClick(p));
            logo = (ImageView) convertView.findViewById(R.id.imglogo);
            name = (TextView) convertView.findViewById(R.id.name);
//            list = (TextView) convertView.findViewById(R.id.description);

            String ans = "";
            if(k.get(position) != "") {
                ans = k.get(position);
                switch (ans) {
                    case "0" : logo.setImageResource(R.drawable.a1); break;
                    default: logo.setImageResource(R.drawable.award);
                }
            }

            name.setText(na.get(position));
//            list.setText(des.get(position));
            list = des.get(position);
            reqes = req.get(position);

//            logo.setOnClickListener(new OnClick(p));
//            name.setOnClickListener(new OnClick(p));
//            list.setOnClickListener(new OnClick(p));

//            logo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder ad = new AlertDialog.Builder(ManualActivity.this);
//                    ad.setTitle("徽章成就-" + na.get(p));
//                    ad.setMessage("徽章描述：" + des.get(p) + "\n\n所需條件：" + req.get(p));
//                    ad.setNegativeButton("觀看結束", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int i) {
//                            return;
//                        }
//                    });
//                    ad.show();
//                }
//            });
            convertView.setOnClickListener(new OnClick(p));
            return convertView;
        }

        class OnClick implements View.OnClickListener{
            final int mPosition;
            public OnClick(int position){
                mPosition = position;
            }

            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ManualActivity.this);
                ad.setTitle("徽章成就-" + na.get(mPosition));
                ad.setMessage("\n徽章描述：" + des.get(mPosition)
//                        + "\n所需條件：" + req.get(mPosition)
                          + "\n\n"
                );
                ad.setNegativeButton("觀看結束", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        return;
                    }
                });
                ad.show();
            }
        }
        public String a(List<String>l) {
            return l.toString();
        }
    }

}











































