//グローバル変数で記述、ただし①内でしか使ってはいけない。
length = 0;
day = [];
time = [];
W = 0;//船のキャパ
W0 = 0;//FLNGのキャパ
enableLoad = [];
FSRU_amount = [];
FSRU_loading = [];
FSRU_prevShip = [];
FLNG_amount = [];
FLNG_idealAmount = [];
FLNG_condition = [];
FLNG_loading = [];
FLNG_prevShip = [];
ships_waitingTime = [];
number_of_ships = 0;
ship_position = [];//以下二次元配列にし、一つ目のインデックスは他のと同じ（行番号てきな）2つ目のインデックスは船の番号
ship_amount = [];
ship_loadingTime = [];
ship_status = [];
ship_v = [];
ship_nextGoalTime = [];
ship_finishTime = [];
timecounter = 0;

function createArray(csvData) {
    var tempArray = csvData.split("\n");
    var csvArray = [];
    for(var i = 0; i<tempArray.length;i++){
        csvArray[i] = tempArray[i].split(",");
    }
    //console.log(csvArray[1][0]);//一つ目行番号、もうひとつ何番目か
    CsvToArray(csvArray);
    //console.log(number_of_ships);
    //console.log(csvArray[2].length);
}

function CsvToArray(csvArray){//csvを配列に代入
    length = csvArray[0][7]
    number_of_ships = csvArray[0][5];
    W = csvArray[0][3];
    W0 = csvArray[0][1];
    for(var i = 3; i < csvArray.length - 1; i++){//二次元配列を生成
        ship_position[i-3] = new Array(number_of_ships);
        ship_amount[i-3] = new Array(number_of_ships);
        ship_loadingTime[i-3] = new Array(number_of_ships);
        ship_status[i-3] = new Array(number_of_ships);
        ship_v[i-3] = new Array(number_of_ships);
        ship_nextGoalTime[i-3] = new Array(number_of_ships);
        ship_finishTime[i-3] = new Array(number_of_ships);
    }
    for(var i = 3; i < csvArray.length - 1; i++){
        day[i-3] = csvArray[i][0];
        time[i-3] = csvArray[i][1];
        enableLoad[i-3] = csvArray[i][2];
        FSRU_amount[i-3] = csvArray[i][3];
        FSRU_loading[i-3] = csvArray[i][4];
        FSRU_prevShip[i-3] = csvArray[i][5];
        FLNG_amount[i-3] = csvArray[i][6];
        FLNG_idealAmount[i-3] = csvArray[i][7];
        FLNG_condition[i-3] = csvArray[i][8]
        FLNG_loading[i-3] = csvArray[i][9];
        FLNG_prevShip[i-3] = csvArray[i][10];
        ships_waitingTime[i-3] = csvArray[i][11];
        for(var j = 0; j < number_of_ships; j++){
            ship_position[i-3][j] = csvArray[i][12 + 8*j]/4*2.0/(length/1000);//画面と合わせるために割る4
            ship_amount[i-3][j] = csvArray[i][13 + 8*j];
            ship_loadingTime[i-3][j] = csvArray[i][14 + 8*j];
            ship_status[i-3][j] = csvArray[i][15 + 8*j];
            ship_v[i-3][j] = csvArray[i][16 + 8*j];
            ship_nextGoalTime[i-3][j] = csvArray[i][17 + 8*j];
            ship_finishTime[i-3][j] = csvArray[i][18 + 8*j]
        }
    }
    //console.log("day初日" + day[0]);//行数
}

function pursue() {
    //console.log("最初")
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
        //①この中の関数が最後に実行されるっぽい
        createArray(xhr.responseText);
        start();
        draw();
        //setTimeout(draw, 1000/FPS);
    };
    
    xhr.open("get", "data3.csv", true);
    xhr.send(null);
}

pursue();

//以下可視化の記述
var SCREEN_SIZE = 515.0;                    // キャンバスの幅
var SIDE_CELLS = 50.0;                     // 一辺のセルの数
var CELL_SIZE = SCREEN_SIZE / SIDE_CELLS; // セルの幅
var FPS = 8;                             // フレームレート
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
            context.fillRect(ship_position[timecounter][j],(CELL_SIZE + 10)*j + 10,CELL_SIZE,CELL_SIZE);
            context.fillStyle = "black";
            context.fillText(ship_amount[timecounter][j]/100.0, ship_position[timecounter][j],(CELL_SIZE + 10)*j + 10);
        }else{
            context.fillStyle = 'red';
            context.fillRect(ship_position[timecounter][j],(CELL_SIZE + 10)*j + 10,CELL_SIZE,CELL_SIZE);
            context.fillStyle = "black";
            context.fillText(Math.round(ship_amount[timecounter][j]/700.0), ship_position[timecounter][j],(CELL_SIZE + 10)*j + 10);
        }
    }
    write_day_time();
    write_wave_height();
    //write_amount_of_ships();
    write_amount_of_FSRU();
    write_amount_of_FLNG();
    write_condition_of_FLNG();
    changebackground();
    timecounter++;
    //console.log(timecounter);
    action = setTimeout(draw, 1000/FPS);
}

function write_wave_height(){
    if (enableLoad[timecounter] == 1){
        var text=document.createTextNode("波的に出航可能ですよ");
        var element = document.getElementById("wave_height");
        element.style.color = 'black';
        //document.body.style.backgroundColor = "white";
    }else{
        var text=document.createTextNode("やめたまえ");
        var element = document.getElementById("wave_height");
        element.style.color = 'red';
        //document.body.style.backgroundColor = "#FA8072";
    }
    if(wave_height.childNodes.length != 0){
        wave_height.removeChild(wave_height.childNodes[0]);
    }
    wave_height.appendChild(text);
}


function write_amount_of_ships(){
    console.log(amount_of_ships.childNodes.length);
    var before_text = "";
    for(var j = 0; j < number_of_ships; j++){
        /**
        if(ship_amount[timecounter][j] == 0){
            context.fillStyle = 'yellow';
            context.fillRect(ship_position[timecounter][j],CELL_SIZE*j,CELL_SIZE,CELL_SIZE);
        }else{
            context.fillStyle = 'red';
            context.fillRect(ship_position[timecounter][j],CELL_SIZE*j,CELL_SIZE,CELL_SIZE);
        }
         **/
        before_text = before_text + ("船"+ (j + 1) + "の積載率は" + ship_amount[timecounter][j]/W + "%です。");
        var text=document.createTextNode(before_text);
    }
    if(amount_of_ships.childNodes.length != 0){
        amount_of_ships.removeChild(amount_of_ships.childNodes[0]);
    }
    amount_of_ships.appendChild(text);
}

function write_amount_of_FSRU(){
    var text=document.createTextNode("FSRUの積載量は" + FSRU_amount[timecounter]);
    if(amount_of_FSRU.childNodes.length != 0){
        amount_of_FSRU.removeChild(amount_of_FSRU.childNodes[0]);
    }
    amount_of_FSRU.appendChild(text);
}

function write_amount_of_FLNG(){
    var text=document.createTextNode("FLNGの積載量は" + FLNG_amount[timecounter]);
    if(amount_of_FLNG.childNodes.length != 0){
        amount_of_FLNG.removeChild(amount_of_FLNG.childNodes[0]);
    }
    amount_of_FLNG.appendChild(text);
}

function write_day_time(){
    var text=document.createTextNode("Day:" + day[timecounter] + " Time:" + time[timecounter]);
    if(day_time.childNodes.length != 0){
        day_time.removeChild(day_time.childNodes[0]);
    }
    day_time.appendChild(text);
}

function write_condition_of_FLNG(){
    var text=document.createTextNode("FLNG:"+FLNG_condition[timecounter]);
    if(FLNG_condition[timecounter]=="shortage"){
        var element = document.getElementById("condition_of_FLNG");
        element.style.color = 'blue';
    }
    else if(FLNG_condition[timecounter]=="full"){
        var element = document.getElementById("condition_of_FLNG");
        element.style.color = 'red';
    }
    else{
        var element = document.getElementById("condition_of_FLNG");
        element.style.color = 'black';
    }
    if(condition_of_FLNG.childNodes.length!=0){
        condition_of_FLNG.removeChild(condition_of_FLNG.childNodes[0]);
    }
    condition_of_FLNG.appendChild(text);
}

function onButtonClick_stop(){
    clearTimeout(action);
}

function onButtonClick_start(){
    draw();
}

function changebackground(){
    if (enableLoad[timecounter] == 1 && 8 <= time[timecounter] && time[timecounter] <= 18){
        document.body.style.backgroundColor = "white";
    }else{
        document.body.style.backgroundColor = "#FA8072";
    }
}


