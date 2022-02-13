package com.sbr.attendme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShowData extends AppCompatActivity {
    private Classs classs;
    private WebView webView;
    private ArrayList<String> heads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        classs= (Classs) getIntent().getSerializableExtra("classs");
        webView=(WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        //webview.loadData(data, "text/html; charset=utf-8", "UTF-8");
        heads=new ArrayList<>();
        ArrayList<Student> students=MainActivity.db.getStudents();
        ArrayList<DateData> dateDatas=MainActivity.db.getDates(classs.getDateTable());
        Collections.sort(students, (a, b) -> a.getRoll()- b.getRoll());
        String body="<html><body><table border=\"1\">";
        body+="<tr><th>Name</th><th>Roll</th>";
        for(DateData d:dateDatas) {
            if(!heads.contains(d.getDate())) {
                heads.add(d.getDate());
                body+="<th>"+d.getDate()+"</th>";
            }
        }
        body+="</tr>";
        for(Student s:students) {
            boolean isFind=false;
            String temp="<tr><td>"+s.getName()+"</td><td>"+s.getRoll()+"</td>";
            int i=0;
            for(String h:heads) {
                boolean findboy=false;
                for(DateData d:MainActivity.db.getDates(classs.getDateTable(),h)) {
                    if(d.getId().equals(s.getId())) {
                        findboy=true;
                    }
                }
                if(findboy) {
                    isFind=true;
                    temp+="<td>"+(++i)+"</td>";
                }
                else
                    temp+="<td>X</td>";
            }
            if(isFind)
                body+=(temp+"</tr>");
        }
        body+="</table></body></html>";
        webView.loadData(body, "text/html; charset=utf-8", "UTF-8");
    }
}