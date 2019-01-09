const config = {
    apiKey: "AIzaSyB1KYErv6Zp2R-BI2x3hN41hfDoXNSRAb4",
    authDomain: "tebah-236b1.firebaseapp.com",
    //databaseURL: "https://tebah-236b1.firebaseio.com",
    projectId: "tebah-236b1",
    //storageBucket: "tebah-236b1.appspot.com",
    //messagingSenderId: "554080724621"
};
firebase.initializeApp(config);
  
// Initialize Cloud Firestore through Firebase
const storage = firebase.storage();
const db = firebase.firestore();
let obj={};

// [ 초기 데이터 불러오기 Start ]
db.collection('conditions').onSnapshot((snap)=>{
    snap.docChanges.forEach(function(change) {
        if (change.type === "added") {
            const data=change.doc.data();
            const name=data.name;
            $('.conditions .box').append(condition.div(name));
        }
        if (change.type === "modified") {
            console.log("Modified city: ", change.doc.data());
        }
        if (change.type === "removed") {
            console.log("Removed city: ", change.doc.data());
        }
    });
});
const dd=db.collection('conditions').where("main","==",true).get().then(function(snap){
    console.log(snap);
});
console.log(dd);
// [ 초기 데이터 불러오기 End ]



// [ condition object START ] 
const condition={
    div:function(id){
        //const c=obj.conditions[id];
        return $('<div/>',{
            id:id,
            class:'condition',
            text:id,
            on:{click:function(){
                //condition.del(id);
            }}
        });
    },
    add:function(){
        const conditionId=$('.conditions .insertName').val();
        const selectTagId="test_main"//$('.conditions .tag.select').attr('id');

        if(conditionId==""){
            alert("조건이름을 입력해 주세요.");
            return false;
        }
        if(selectTagId==null){
            alert("Tag를 선택해 주세요.");
            return false;
        }
        if(conditionId=="main"){
            alert("main은 추가할수 없는 조건이름 입니다.");
            return false;
        }
        /* if(obj&&obj.conditions[conditionId]){
            alert("중복된 조건값이 존재합니다. 삭제후에 진행해주세요.");
            return false;
        } */

        let conditionData={
            selectTagId:selectTagId,
            name:conditionId
        }

        /* db.ref('conditions/').child(conditionId).set(conditionData,function(err){
            if(err) return err;
            alert("성공적으로 업데이트 하였습니다.");
        }); */
        db.collection('conditions').doc(conditionId).set(
            conditionData
        ).then(function(){
            alert("성공적으로 '"+conditionId+"' 추가 하였습니다.");
        }).catch(function(err){
            console.log(err);
        })
        
        /* 
        //[ firestore map 에 push 하는 방법 ]
        db.collection('conditions').doc('main').update({
            "conditions.c":true
        }).then(function(){
            console.log("Document successfully updated!");
        }); 
        */
    },
    del:function(id){
        const bool=confirm("'"+id+"' 조건을 정말 삭제하시겠습니까?");
        if(!bool) return false;
        db.ref('conditions/').child(id).set(null,function(err){
            if(err) return err;
            alert("'"+id+"'를 정상적으로 삭제 완료하였습니다.");
        });
    }
}
// [ condition object END ] 

// [ food object START ] 
const food={
    div:function(id){
        const div=$('<div/>',{
            id:id,
            class:'food',
            text:id,
            on:{click:function(){
                food.del(id);
            }}
        });
        const img=$('<img/>');
        const storageRef=storage.ref('foods/'+id);
        storageRef.getDownloadURL().then(function(url){
            img.attr({src:url});
            div.append(img);
        }).catch(function(err){
            console.log(err);
            img.attr({src:""});
            div.append(img);
        });
        return div;
    },
    add:function(){
        const foodName = $('.foods .insertName').val();
        const storageRef = storage.ref();
        const file = $('.foods .insertFile').get(0).files[0];
        let tags = {};
        
        $('.foods .tag.select').each(function(){
            let tagId=$(this).attr('id');
            tags[tagId]=true;
        });
        
        if(Object.keys(tags).length==0)
            return false;
        if(file==null)
            return false;
        if(obj.foods&&obj.foods[foodName])
            return false;
        if(foodName=="")
            return false;
         
        db.ref('foods/').child(foodName).set({tags:tags}); 
        
        //[ File Storage upload START ]
        const fileName = foodName;
        const fileMetadata = { contentType: "image/jpg" };
        const task =storageRef.child('foods').child(fileName).put(file,fileMetadata);
        task
            .then(snapshot => snapshot.ref.getDownloadURL())
            .then((url) => {
                console.log(url);
                console.log("사진 등록 완료!");
                $(".foods .box [id='"+foodName+"'] img").attr({src:url});
            })
            .catch(console.error);
        //[ File Storage upload END ]

        alert("성공적으로 등록하였습니다."); 
    },
    del:function(id){
        const bool=confirm("'"+id+"' 메뉴를 정말 삭제하시겠습니까?");
        if(!bool) return false;
        const desertRef= storage.ref().child('foods/'+id);
        const tags=obj.foods[id].tags;
            
        db.ref('foods/').child(id).set(null);
        
        //[ File Storage desert START ]
        desertRef.delete().then(function(){
            alert("삭제완료!!");
        }).catch(function(err){
            console.log(err);
        });
        //[ File Storage desert END ]
    }
}
// [ food object END ] 

// [ tag object START ] 
const tag={
    div:function(id){
        return $('<div/>',{
                    id:id,
                    class:'tag',
                    text:"#"+id,
                    on:{
                        click:function(){
                            tag.selected(id,this);
                        }
                    }
                });
    },
    appendDiv:function(id){
        const div=tag.div(id);
        const condition=obj.conditions[id];
        if(condition.tags&&condition.tags.main)
            $('.conditions .tags').append(div);
        else
            $('.foods .tags').append(div);
    },
    selected:function(id,doc){
        if(obj.tags[id])
            if(obj.tags[id].conditions)
                $('.tag').removeClass("select");
        $(doc).toggleClass("select");
    },
    del:function(){

    }
}
// [ tag object START ] 

$(document).ready(function(){
    $('.add').click(function(){
        var parentClass=$(this).parent().attr('class');
        $("."+parentClass+' .ide').toggle();
    });
    $('.conditions .btn').click(condition.add);
    $('.foods .btn').click(food.add);
    
});