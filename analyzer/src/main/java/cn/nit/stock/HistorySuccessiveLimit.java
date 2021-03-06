package cn.nit.stock;

import cn.nit.stock.model.StockName;
import cn.nit.stock.model.TradeDay;
import com.mongodb.MongoClient;
import org.apache.commons.io.FileUtils;
import org.mongodb.morphia.Datastore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gengke on 2014/10/23.
 */
public class HistorySuccessiveLimit {

    private static Datastore ds;

    private static MongoClient mongoClient;

    private static MongoOperations mongoOps;

    public static void main( String[] args ) throws Exception {
        ds = ConnUtils.getDatastore();
        mongoClient = ConnUtils.getMongo();
        mongoOps = new MongoTemplate(mongoClient, "stock");

       List<String> limit = new ArrayList<String>();

        for (StockName stockName : ds.find(StockName.class).asList()) {
            System.err.println(stockName);

            String stockcode = stockName.getCode();

            List<TradeDay> list = mongoOps.find(new Query(Criteria.where("closePrice").gt(0)).with(new Sort(Sort.Direction.ASC, "tradeDate")),TradeDay.class, stockName.getCode());

            for(int i = 0 ; i < list.size(); i++) {
                if ( i + 2 >= list.size()) continue;

                TradeDay day1 = list.get(i);
                TradeDay day2 = list.get(i+1);
                TradeDay day3 = list.get(i+2);

                if (day1.isOpenLimit() && day2.isOpenLimit() && day3.isOpenLimit()) {
                    String str = "0" + stockcode;

                    if (stockcode.startsWith("6"))
                        str = "1" + stockcode;

                    if (!limit.contains(str)) limit.add(str);
                }

            }
        }

        StringBuilder sb = new StringBuilder();
        for (String code : limit) {
            sb.append(code + "\r\n");
        }
        FileUtils.writeStringToFile(new File("C:\\new_gxzq_v6\\T0002\\blocknew\\NG.blk"), sb.toString());
        System.err.println(limit.toString());
    }
}
