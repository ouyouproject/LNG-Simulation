# LNG-Simulation

FLNG : 海に浮いてるやつ、こいつで資源を採掘  
FSRU : 港に停泊

【課題】  
洋上のFLNGから港のFSRUまでLNGを輸送するのに必要なLNG船の大きさ、隻数、速力およびFLNGの貯蔵能力の最適な計画を行う  

【横山】  
javaでアルゴリズムと最適化手法の作成、csvファイルで出力  
【伊藤】  
LNG船の可視化  

【csvファイルの形式】  
一行目(それぞれのパラメータとFLNGとFSRUの座標(コンマで区切ってほしい))  
W0,W,N,V,FLNGの座標,FSRUの座標  
二行目以降  
時刻,船1の座標,積載中かどうか,船2の座標,積載中かどうか,船3の座標,......,船Nの座標,積載中かどうか


