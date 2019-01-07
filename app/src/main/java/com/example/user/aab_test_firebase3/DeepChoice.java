package com.example.user.aab_test_firebase3;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.google.firebase.database.*;

import java.util.ArrayList;

import static com.example.user.aab_test_firebase3.MainActivity.mainSelectList;

public class DeepChoice extends AppCompatActivity {
    public static ArrayList<String> deepSelectList;
    private final ArrayList<String> deepTagList=new ArrayList<>();
    private String titleText="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_choice_condition);  // layout xml 과 자바파일을 연결

        final TextView loadingTextView = (TextView) findViewById(R.id.loadingView);
        loadingTextView.setVisibility(loadingTextView.GONE);//불러오는중입니다 없애기

        final ListView listView = (ListView) findViewById(R.id.listView);
        final Button beforeButton = (Button) findViewById(R.id.beforeBtn);
        final Button nextButton = (Button) findViewById(R.id.nextBtn);

        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        // Firebase 연동
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseRef = firebaseDatabase.getReference();

        deepSelectList=new ArrayList<>();

        for(int i=0;i<mainSelectList.size();i++){
            String tagId=mainSelectList.get(i);
            System.out.println("Deep Choice // tag id : "+tagId);
            titleText+=tagId+", ";
            // realtime database 조회
            databaseRef.child("tags/"+tagId+"/conditions").addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final String conditionId = dataSnapshot.getKey();
                    adapter.add(conditionId);
                    deepTagList.add(conditionId);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    final String conditionId = dataSnapshot.getKey();
                    int position=-1;
                    for(int i=0;i<deepTagList.size();i++){
                        if(deepTagList.get(i).equals(conditionId)){
                            position=i;
                            break;
                        }
                    }
                    deepTagList.remove(position);
                    adapter.remove(adapter.getItem(position));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }

        setTitle(titleText);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String conditionId = deepTagList.get(position);
                final int index=deepSelectList.indexOf(conditionId);
                if(deepSelectList.size()==0||index<0){
                    deepSelectList.add(conditionId);
                    view.setBackgroundColor(Color.GRAY);
                    System.out.println("세부조건 : "+conditionId +" - 선택");
                }
                else{
                    deepSelectList.remove(index);
                    view.setBackgroundColor(Color.WHITE);
                    System.out.println("세부조건 : "+conditionId +" - 삭제");
                }

                System.out.println("----------------------------------");
                System.out.println("position = "+position);
                System.out.println("conditionId = "+conditionId);
                System.out.println("----------------------------------");
            }
        });
        nextButton.setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        FoodChoice.class); // 다음 넘어갈 클래스 지정
                startActivity(intent);
            }
        });
        beforeButton.setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View view) {
                finish();
            }
        });
    } // end onCreate()
}
