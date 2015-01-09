
function getCSVFile() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
        createArray(xhr.responseText);
    };
    
    xhr.open("get", "data.csv", true);
    xhr.send(null);
}
getCSVFile();

function createXMLHttpRequest() {
    var XMLhttpObject = null;
    XMLhttpObject = new XMLHttpRequest();
    return XMLhttpObject;
}

function createArray(csvData) {
    var tempArray = csvData.split("\n");
    var csvArray = new Array();
    for(var i = 0; i<tempArray.length;i++){
        csvArray[i] = tempArray[i].split(",");
    }
    console.log(csvArray);
}
/**
var SCREEN_SIZE = 500.0;                    // キャンバスの幅
var SIDE_CELLS = 50.0;                     // 一辺のセルの数
var CELL_SIZE = SCREEN_SIZE / SIDE_CELLS; // セルの幅
var FPS = 0.5;                             // フレームレート
var canvas;                     //= document.getElementById('world');
var context;                    //= canvas.getContext('2d');
var number_of_server = 3.0;           //サーバー数
var arriving_rate = 46.0;              //到着率
var management = 12.0;                 //処理能力
var field;                      //その時々のいる人数の状態を格納

function onButtonClick(){
    if(document.form.number_of_server.value != ""){
        number_of_server = parseFloat(document.form.number_of_server.value);
    }
    if(document.form.arriving_rate.value != ""){
        arriving_rate = parseFloat(document.form.arriving_rate.value);
    }
    if(document.form.management.value != ""){
        management = parseFloat(document.form.management.value);
    }
    clearTimeout(default_action);
    update(parseFloat($.cookie("field")));
}


window.onload = function(){
    field = 0;
    $.cookie("field",field);
    canvas = document.getElementById('world'); // canvas要素を取得
    canvas.width = canvas.height = SCREEN_SIZE; // キャンバスのサイズを設定
    var scaleRate = Math.min(window.innerHeight/SCREEN_SIZE, window.innerHeight/SCREEN_SIZE); // Canvas引き伸ばし率の取得
    canvas.style.width = canvas.style.height = SCREEN_SIZE*scaleRate+'px';  // キャンバスを引き伸ばし
    context = canvas.getContext('2d');                // コンテキスト
    context.fillStyle = 'rgb(211, 85, 149)';          // 色
    update(parseFloat($.cookie("field")));   // ゲームループ開始
}


function update(x){
    var new_coming = -(arriving_rate)*Math.log(Math.random());　//単位時間に新たに来る人
    var per_manage_number = -(management)*Math.log(Math.random()); //単位時間に一つのサーバで処理できる人数
    x = x + new_coming - (number_of_server * per_manage_number);
    $.removeCookie("field");
    if(x<=0){
        $.cookie("field",0);
    }else{
        $.cookie("field",x); //$.cookie("field")の更新
    }
    console.log("サーバーの数は" + number_of_server);
    console.log("到着率は" + arriving_rate);
    console.log("処理能力は" + management);
    console.log("待ち人数は" + Math.round($.cookie("field")));
    draw(parseFloat($.cookie("field")));
    default_action = setTimeout(update, 1000/FPS,parseFloat($.cookie("field")));
}

function draw(x){
    context.clearRect(0, 0, SCREEN_SIZE, SCREEN_SIZE); // 画面をクリア
    if(x==0){
        
    }else if(x<=number_of_server){
        for (var i=1;i<=x;i++){
            context.fillRect(0,(SCREEN_SIZE/(number_of_server+1.0))*i,CELL_SIZE,CELL_SIZE);
            context.strokeRect(0,(SCREEN_SIZE/(number_of_server+1.0))*i,CELL_SIZE,CELL_SIZE);
        }
        
    }else{
        for (var i=1;i<=number_of_server;i++){
            context.fillRect(0,(SCREEN_SIZE/(number_of_server+1.0))*i,CELL_SIZE,CELL_SIZE);
            context.strokeRect(0,(SCREEN_SIZE/(number_of_server+1.0))*i,CELL_SIZE,CELL_SIZE);
        }//処理中の人を描画
        for(var j=0;j<Math.ceil((x - number_of_server)/SIDE_CELLS);j++){
            for(var i=0;i<Math.ceil((x - number_of_server)-j*SIDE_CELLS);i++){
                context.fillRect(SCREEN_SIZE - CELL_SIZE - j*CELL_SIZE,i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
                context.strokeRect(SCREEN_SIZE - CELL_SIZE - j*CELL_SIZE,i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
            }
        }
    }
    console.log(Math.ceil((x - number_of_server)));
    var field_column = document.getElementById("field_column");
    var text=document.createTextNode("現在の待ち人数は"　+ Math.round($.cookie("field")) + "人です");
    if(field_column.childNodes.length != 0){
        field_column.removeChild(field_column.childNodes[0]);
    }
    field_column.appendChild(text);
}
**/
