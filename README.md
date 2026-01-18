# local-stock-viewer

ローカルで動く株価ビューア（Stooq + Spring Boot + H2）。

## 起動
```bash
mvn spring-boot:run
```
または IntelliJ で `LocalStockViewerApplication` をRun。

## 画面
- http://localhost:8080/stocks

## 使い方
1. 銘柄をクリック
2. 右上の「Stooqから更新（5年）」を押す（初回必須）
3. 期間ボタン（1D/5D/1M/3M/6M/1Y/5Y/MAX）で推移を見る
4. `/stocks` で銘柄を追加（証券コードだけでもOK）して、保有/ウォッチを分ける

## H2 Console
- http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:stocksdb`
- user: `sa` / password: 空
