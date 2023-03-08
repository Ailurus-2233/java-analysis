package pers.ailurus.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.db.nosql.mongo.MongoDS;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import pers.ailurus.models.feature.FeaturePackage;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Database {
    private static MongoDS mongoDS;
    private static MongoCollection<Document> collection;

    public static void init(String url, String port, String user, String password, String db, String coll) {
        Setting setting = new Setting();
        setting.set("host", String.format("%s:%s", url, port));
        setting.set("username", user);
        setting.set("password", password);
        mongoDS = new MongoDS(setting);
        collection = mongoDS.getCollection(db, coll);
    }

    public static void init(String url, String port, String db, String coll) {
        Setting setting = new Setting();
        setting.set("host", String.format("%s:%s", url, port));
        mongoDS = new MongoDS(setting);
        collection = mongoDS.getCollection(db, coll);
    }

    public static void insert(Dict dict) {
        collection.insertOne(Document.parse(JSONUtil.toJsonStr(dict)));
    }

    public static void insert(Dict[] dictArray) {
        List<Document> documents = new ArrayList<>();
        for (Dict dict : dictArray) {
            documents.add(Document.parse(JSONUtil.toJsonStr(dict)));
        }
        collection.insertMany(documents);
    }

    public static FeaturePackage findOne(String key, String value) {
        Object obj = collection.find(new Document(key, value)).first();
        if (obj == null) {
            return null;
        }
        return new FeaturePackage(JSONUtil.parseObj(obj));
    }

    public static List<FeaturePackage> findAll(String key, String value) {
        List<FeaturePackage> fps = new ArrayList<>();
        for (Document document : collection.find(new Document(key, value))) {
            fps.add(new FeaturePackage(JSONUtil.parseObj(document)));
        }
        return fps;
    }

    public static List<String> findAllCDG3() {
        List<String> ans = new ArrayList<>();
        for (Document document: collection.aggregate(
                List.of(
                        Aggregates.group("$cdg_level3", Accumulators.sum("count", 1))
                )
        )) {
            if (document.getInteger("count") > 1) {
                ans.add(document.getString("_id"));
            }
        }
        return ans;
    }
}
