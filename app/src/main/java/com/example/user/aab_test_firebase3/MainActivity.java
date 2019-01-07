package com.example.user.aab_test_firebase3;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.google.firebase.database.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<String> mainSelectList=new ArrayList<>();
    private final ArrayList<String> mainTagList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_choice_condition);


        final TextView loadingTextView = (TextView) findViewById(R.id.loadingView);
            loadingTextView.setVisibility(loadingTextView.GONE);//불러오는중입니다 없애기

        final ListView listView = (ListView) findViewById(R.id.listView);
        final Button nextButton = (Button) findViewById(R.id.nextBtn);

        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        // Firebase 연동
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseRef = firebaseDatabase.getReference();

        // realtime database 조회
        databaseRef.child("tags/main/conditions").addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String conditionId = dataSnapshot.getKey();
                adapter.add(conditionId);
                mainTagList.add(conditionId);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final String conditionId = dataSnapshot.getKey();
                int position=-1;
                for(int i=0;i<mainTagList.size();i++){
                    if(mainTagList.get(i).equals(conditionId)){
                        position=i;
                        break;
                    }
                }
                mainTagList.remove(position);
                adapter.remove(adapter.getItem(position));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=mainTagList.size()) {
                    System.out.println("선택한 tag가 실제 arrayList 배열보다 큰 index에 존재한다.");
                    return;
                }
                final String conditionId = mainTagList.get(position);
                final int index=mainSelectList.indexOf(conditionId);
                if(mainSelectList.size()==0||index<0){
                    mainSelectList.add(conditionId);
                    view.setBackgroundColor(Color.GRAY);
                }
                else{
                    mainSelectList.remove(index);
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        });
        nextButton.setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        DeepChoice.class); // 다음 넘어갈 클래스 지정
                startActivity(intent);
            }
        });
    }
}
