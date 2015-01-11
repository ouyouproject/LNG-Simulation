//グローバル変数で記述、ただし①内でしか使ってはいけない。
day = [];
time = [];
enableLoad = [];
FSRU_amount = [];
FSRU_loading = [];
FLNG_amount = [];
FLNG_loading = [];
number_of_ships = 0;
ship_position = [];//以下二次元配列にし、一つ目のインデックスは他のと同じ（行番号てきな）2つ目のインデックスは船の番号
ship_amount = [];
ship_loadingTime = [];
timecounter = 0;

function createArray(csvData) {
    var tempArray = csvData.split("\n");
    var csvArray = [];
    for(var i = 0; i<tempArray.length;i++){
        csvArray[i] = tempArray[i].split(",");
    }
    console.log(csvArray[1][0]);//一つ目行番号、もうひとつ何番目か
    CsvToArray(csvArray);
    //console.log(number_of_ships);
    //console.log(csvArray[2].length);
}

function CsvToArray(csvArray){//csvを配列に代入
    number_of_ships = (csvArray[2].length - 8) / 3;
    for(var i = 2; i < csvArray.length - 1; i++){//二次元配列を生成
        ship_position[i-2] = new Array(number_of_ships);
        ship_amount[i-2] = new Array(number_of_ships);
        ship_loadingTime[i-2] = new Array(number_of_ships);
    }
    for(var i = 2; i < csvArray.length - 1; i++){
        day[i-2] = csvArray[i][0];
        time[i-2] = csvArray[i][1];
        enableLoad[i-2] = csvArray[i][2];
        FSRU_amount[i-2] = csvArray[i][3];
        FSRU_loading[i-2] = csvArray[i][4];
        FLNG_amount[i-2] = csvArray[i][5];
        FLNG_loading[i-2] = csvArray[i][6];
        for(var j = 0; j < number_of_ships; j++){
            ship_position[i-2][j] = csvArray[i][7 + 3*j]/4;//画面と合わせるために割る4
            ship_amount[i-2][j] = csvArray[i][8 + 3*j];
            ship_loadingTime[i-2][j] = csvArray[i][9 + 3*j];
        }
    }
    console.log("day初日" + day[0]);//行数
}

function pursue() {
    console.log("最初")
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
        //①この中の関数が最後に実行されるっぽい
        createArray(xhr.responseText);
        start();
        draw();
        setTimeout(draw, 1000/FPS);
    };
    
    xhr.open("get", "data.csv", true);
    xhr.send(null);
}

pursue();

//以下可視化の記述
var SCREEN_SIZE = 515.0;                    // キャンバスの幅
var SIDE_CELLS = 50.0;                     // 一辺のセルの数
var CELL_SIZE = SCREEN_SIZE / SIDE_CELLS; // セルの幅
var FPS = 2;                             // フレームレート
var canvas;                     //= document.getElementById('world');
var context;                    //= canvas.getContext('2d');

function start(){
    canvas = document.getElementById('world'); // canvas要素を取得
    canvas.width = canvas.height = SCREEN_SIZE; // キャンバスのサイズを設定
    var scaleRate = Math.min(window.innerHeight/SCREEN_SIZE, window.innerHeight/SCREEN_SIZE); // Canvas引き伸ばし率の取得
    canvas.style.width = canvas.style.height = SCREEN_SIZE*scaleRate+'px';  // キャンバスを引き伸ばし
    context = canvas.getContext('2d');                // コンテキスト
    context.fillStyle = 'rgb(211, 85, 149)';          // 色
    context.fillRect(0,0,5,515);
    context.fillRect(510,0,5,515);
}

function draw(){
    context.clearRect(5, 0, SCREEN_SIZE-10, SCREEN_SIZE);
    for(var j = 0; j < number_of_ships; j++){
        if(ship_amount[timecounter][j] == 0){
            context.fillStyle = 'yellow';
            context.fillRect(ship_position[timecounter][j],CELL_SIZE*j,CELL_SIZE,CELL_SIZE);
        }else{
            context.fillStyle = 'red';
            context.fillRect(ship_position[timecounter][j],CELL_SIZE*j,CELL_SIZE,CELL_SIZE);
        }
    }
    timecounter++;
    console.log(timecounter);
    setTimeout(draw, 1000/FPS);
}



/**
function createXMLHttpRequest() {
    var XMLhttpObject = null;
    XMLhttpObject = new XMLHttpRequest();
    return XMLhttpObject;
}
 **/


//var number_of_ships = (csvArray[0].length - 7)/2 ;

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
