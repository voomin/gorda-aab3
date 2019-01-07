package com.example.user.aab_test_firebase3;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.example.user.aab_test_firebase3.List.ListviewAdapter;
import com.example.user.aab_test_firebase3.List.Listviewitem;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static com.example.user.aab_test_firebase3.DeepChoice.deepSelectList;

public class FoodChoice extends AppCompatActivity {
    private final ArrayList<String> foodList=new ArrayList<>();
    private FirebaseFunctions mFunctions;
    private String titleText="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_choice_condition);  // layout xml 과 자바파일을 연결

        titleText=deepSelectList.toString();
        setTitle(titleText);

        final TextView loadingTextView = (TextView) findViewById(R.id.loadingView);
        final ListView listView = (ListView) findViewById(R.id.listView);
        final Button beforeButton = (Button) findViewById(R.id.beforeBtn);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this); // Context, this, etc.

        //final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        final ArrayList<Listviewitem> data=new ArrayList<>();


        // Firebase 연동
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseRef = firebaseDatabase.getReference();

        mFunctions = FirebaseFunctions.getInstance();

        FoodSelect(deepSelectList).addOnCompleteListener(new OnCompleteListener<HashMap<String,String>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String,String>> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                    System.out.println("에러발생 !! error : "+e);
                    return ;
                    // ...
                }
                HashMap<String,String> map =task.getResult();
                System.out.println("불러오기 성공 !! map : "+map);
                loadingTextView.setVisibility(loadingTextView.GONE);//불러오는중입니다 없애기

                if(map.size()==0) {
                    Listviewitem none = new Listviewitem(R.drawable.lion,"메뉴가 존재하지 않습니다.");
                    data.add(none);
                    return;
                }

                for(String key : map.keySet()){
                    System.out.println("key : "+key);
                    foodList.add(key);

                    Listviewitem item = new Listviewitem(R.drawable.lion,key);
                    data.add(item);
                }
            }
        });



        ListviewAdapter adapter=new ListviewAdapter(this,R.layout.item,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String foodName=foodList.get(position);
                dialogBuilder.setTitle(foodName+" 정보");

                dialogBuilder.setMessage("태그정보 가져오는 중..");
                databaseRef.child("foods/"+foodName+"/tags").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String,String> tagsMap = (HashMap<String,String>)dataSnapshot.getValue();
                        System.out.println(tagsMap);
                        //dialogBuilder.setMessage("가져왔다. 대기");
                        String str="";
                        for(String key : tagsMap.keySet())
                            str+="#"+key+" ";
                        System.out.println(str);
                        dialogBuilder.setMessage(str);
                        dialogBuilder.setCancelable(true);
                        dialogBuilder.setPositiveButton(
                                "닫기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = dialogBuilder.create();
                        alert.show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialogBuilder.setMessage("정보가 존재하지 않습니다.");
                        dialogBuilder.setCancelable(true);
                        dialogBuilder.setPositiveButton(
                                "닫기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = dialogBuilder.create();
                        alert.show();
                    }
                });
            }
        });
        beforeButton.setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View view) {
                finish();
            }
        });

    } // end onCreate()

    // start FoodSelect()
    private Task<HashMap<String,String>> FoodSelect(ArrayList arrayList) {
        // Create the arguments to the callable function.
        ArrayList<String> data=arrayList;

        return mFunctions
                .getHttpsCallable("select")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String,String>>() {
                    @Override
                    public HashMap<String,String> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        HashMap<String,String> result = (HashMap<String,String>) task.getResult().getData();
                        return result;
                    }
                });
    }
    // end FoodSelect()
}
