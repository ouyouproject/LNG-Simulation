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

[調査要件]
・燃費（円/km）
・係船時間の内訳（準備・積み出し）
・W,W0の最大最小値
・その他コストの式

[前提]
・スタートは陸上から→終了時船の燃料は利益計算に含める

[戦略方針]
・Vの最低限の使用
	前の船が出発する時間を予測、通信
	波到着予定時間時の波の予測
・係船してない時間をなくす
	係船途中でも17:00に出発するか否か
		・コストパフォーマンスの計算
			時間あたりの汲み上げ利益-予測輸送費
