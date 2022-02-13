package com.sbr.attendme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

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
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
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
    public  void createWebPagePrint(WebView webView) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5);
        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());

        if(printJob.isCompleted()){
            Toast.makeText(getApplicationContext(), "PDF saved", Toast.LENGTH_LONG).show();
        }
        else if(printJob.isFailed()){
            Toast.makeText(getApplicationContext(), "Can not save PDF", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.print_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        createWebPagePrint(webView);
        return super.onOptionsItemSelected(item);
    }
}